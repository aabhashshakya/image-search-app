package com.avas.imagesearchapp.VIEW.FRAGMENTS

import android.Manifest
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Bitmap

import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.avas.imagesearchapp.R
import com.avas.imagesearchapp.databinding.DetailsFragmentBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.details_fragment.*
import java.io.File
import java.io.FileOutputStream

@AndroidEntryPoint
class DetailsFragment() : Fragment(R.layout.details_fragment) {

    //this is how we get the safe arguments we passed from the GalleryFragment
    private val args by navArgs<DetailsFragmentArgs>()

    private var _binding: DetailsFragmentBinding? = null
    private val binding get() = _binding
    private var downloadId: Long? = null

    companion object {
        private const val STORAGE_PERMISSION = 1
    }

    //we register for Broadcasts of download completed. so we can know the downloads completed when user downloads a photo
    override fun onAttach(context: Context) {
        super.onAttach(context)
        //check out the our BroadcastReceiver for more info
        context.registerReceiver(
            broadcastReceiver,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        )
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = DetailsFragmentBinding.bind(view)

        setUpTheViews()
    }


    private fun setUpTheViews() {
        binding.apply {
            //getting the photos object from the argument
            val photos = args.photo

            //when we use Glide in a fragment, we pass the context of the fragments
            Glide.with(this@DetailsFragment).load(photos.urls.regular)
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(android.R.drawable.stat_sys_warning)
                //A class for monitoring the status of a request while images load.
                //the generic is drawable as we loaded a drawable into the imageview
                .listener(object : RequestListener<Drawable> {
                    //when the loading of image fails
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        //the progressbar that we set visible initially in the layout file, is invisible
                        progress_bar.isVisible = false
                        return false
                        //ALWAYS return false otherwise glide will not load the image
                    }

                    //when images are loaded
                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        //the progressbar that we set visible initially in the layout file, is invisible
                        progress_bar.isVisible = false
                        //the textviews that we set invisible initially in the layout file, are visible
                        //doing this make the image and textviews both load at the same time
                        desc.isVisible = photos.description != null
                        source.isVisible = true
                        share_button.isVisible = true
                        save_button.isVisible = true
                        return false
                        //ALWAYS return false otherwise glide will not load the image
                    }

                })
                //the reason we added the above listener is becoz we set the progressbar to visible in the layout, so when the
                //image is loaded or load fails the progressbar is gone
                //also we set the textviews to invisible initially, so above when the image is loaded, the textviews are set to
                //visible so they load together for aesthetics

                .into(image_view)

            desc.text = photos.description

            //we create a URL as we want the user to click this and be sent to the original URL of the photo
            //remember we created this attributionUrl in the MODEL?
            val url = Uri.parse(photos.user.attributonUrl)
            //Setting up intent like this will automatically choose the right way(browser) to open this url
            val intent = Intent(Intent.ACTION_VIEW, url)
            source.apply {
                text = "Photo by: " + photos.user.name
                //upon clicking this text, android opens browser with that link
                setOnClickListener {
                    startActivity(intent)
                }
                //underlining the textview to make it look like a link
                paint.isUnderlineText = true


            }

            //WHEN CLICKING SHARE BUTTON WE CAN SHARE THE IMAGE TO OTHER APPS
            share_button.setOnClickListener {
                sharePhoto()
            }

            save_button.setOnClickListener {
                downloadPhoto()
            }

        }


    }

    private fun sharePhoto() {
        //first check if we have the external storage write permission
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            askForPermission()
        } else {

            //we need to save the image first before we can share it
            //getting the bitmap from image view//convert the Drawable to BitmapDrawable and get the Bitmap
            val drawable = image_view.drawable
            val bitmap =
                drawable.toBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight)

            //we store the file in our externalCacheDir as cacheDir is internal storage and we can share files in internal storage
            //WE CLEAR THE CACHE IN ONCREATE() of the activity
            val file = File(context?.externalCacheDir, "temp_image.png")
            val outputStream = FileOutputStream(file)
            //this is how to store a bitmap to a file
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)

            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                //we use FileProvider to add data to our intent, it is the only way after Android N
                //check out the Manifest file and the res/xml/file_paths file for setting up File provider
                putExtra(
                    Intent.EXTRA_STREAM, FileProvider.getUriForFile(
                        requireContext().applicationContext,
                        requireContext().packageName + ".provider", file
                    )
                )
                //this permission is for the receiving app so it can read the data
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                type = "image/png"
            }
            startActivity(Intent.createChooser(shareIntent, "Share to:"))

        }
    }

    private fun downloadPhoto() {
        //first check if we have the external storage write permission
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            askForPermission()
        } else {
            //first we get the download manager
            val dm = activity?.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            //we parse the Url which we want to download
            val uri = Uri.parse(args.photo.urls.full)
            //we create a download manager request and set up the parameters
            val request = DownloadManager.Request(uri)
            request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                "image ${System.currentTimeMillis()}.png"
            )
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setTitle(null)
            //we enqueue the request//it returns teh download id
            downloadId = dm.enqueue(request)
            Toast.makeText(context, "Starting download...", Toast.LENGTH_SHORT).show()
        }
    }

    //we create a broadcast receiver to listener for download complete intents from the DownloadManager
    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent!!.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            //if the download id from the DownloadManager intent matches our download id, we display a toast
            if (downloadId == id) {
                Toast.makeText(context, "Download completed", Toast.LENGTH_LONG).show()
            }

        }
    }


    private fun askForPermission() {

        if (shouldShowRequestPermissionRationale(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            AlertDialog.Builder(requireContext())
                .setTitle("Permission needed")
                .setMessage("Storage permission is needed to share the pictures.")
                .setPositiveButton("GRANT PERMISSION") { _, _ ->
                    //if clicked ok, it requests the permission
                    requestPermissions(
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        STORAGE_PERMISSION
                    )
                } //if clicked no, it dismisses the dialog
                .setNegativeButton(
                    "DECLINE"
                ) { dialog, _ ->
                    dialog.dismiss()
                    Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
                }.create().show()
        } else
            requestPermissions(
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                STORAGE_PERMISSION
            )
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        if (requestCode == STORAGE_PERMISSION) {

            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "Storage permissions granted.", Toast.LENGTH_SHORT)
                    .show()
            } else
                Toast.makeText(context, "Storage permission not granted.", Toast.LENGTH_SHORT)
                    .show()

        }


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }


}




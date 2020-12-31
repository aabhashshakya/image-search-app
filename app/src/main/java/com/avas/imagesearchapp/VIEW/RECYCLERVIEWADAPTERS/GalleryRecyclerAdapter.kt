package com.avas.imagesearchapp.VIEW.RECYCLERVIEWADAPTERS

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.avas.imagesearchapp.MODEL.UnsplashGETResponse
import com.avas.imagesearchapp.databinding.RecyclerViewLayoutBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

private const val TAG = "MYAPP"
//WE use Glide library to load images into our image view//so check out the build.gradle module level file for its dependency
//it need the INTERNET and the ACCESS_NETWORK_STATE permissions too in the manifest file
//Glide is good as it provides us with transitions, and it also automatically loads the unloaded images if there is no internet, after
//internet comes//it needs ACCESS_NETWORK_STATE permission for this

//extend the PagingDataAdapter for handling PagingData types// its easy don't worry//same as list adapter
class GalleryRecyclerAdapter constructor(private val listener: MyItemClickListener) :
    PagingDataAdapter<UnsplashGETResponse.UnsplashPhoto, GalleryRecyclerAdapter.MyViewHolder>(
        DIFF_CALLBACK
    ) {

    private lateinit var viewHolder: MyViewHolder


    //we need a DIFFUTIL callback to put in the constructor of this PagingDataAdapter
    companion object {
        val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<UnsplashGETResponse.UnsplashPhoto>() {
                override fun areItemsTheSame(
                    oldItem: UnsplashGETResponse.UnsplashPhoto,
                    newItem: UnsplashGETResponse.UnsplashPhoto
                ): Boolean {
                    return oldItem.id == newItem.id
                }

                //This method is called only if areItemsTheSame(int, int) returns true for these items.
                //if items are the same, it checks if their contents are the same or if they have changed
                override fun areContentsTheSame(
                    oldItem: UnsplashGETResponse.UnsplashPhoto,
                    newItem: UnsplashGETResponse.UnsplashPhoto
                ): Boolean {
                    return oldItem == newItem
                }


            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        //getting teh binding object
        val binding =
            RecyclerViewLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        //returning the view holder passing the binding object
        viewHolder = MyViewHolder(binding)
        return viewHolder

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        //getting the item
        val currentItem = getItem(position)
        if (currentItem != null) {
            holder.binding.apply {
                Log.d(TAG, "onBindViewHolder: Loading the views...")
                textView.text = currentItem.user.username

                //we use the Glide library to load images as it is easier
                Glide.with(relativeLayout)//with() is a reference for glide, we pass the layout of the recycler view//which is the
                    //whole layout for our recycler view
                    .load(currentItem.urls.regular)//where to load the image from//in this case the url(requires internet)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())//just some transition animation
                    .error(
                        android.R.drawable.stat_sys_warning//what to display when the image cannot be loaded
                    ).into(imageView)//view in which we want to load the image

            }

        }


    }


    //since we are using view binding, we pass in a binding object to the ViewHolder, not hte inflated view
    inner class MyViewHolder constructor(val binding: RecyclerViewLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        //since we used view binding, no need to do findViewId()

        //we also add a onItemClickListener for each item in the recycler view
        init {
            //binding.root is the whole card for an item in the recycler view
            binding.root.setOnClickListener {
                //getting the adapter position
                val position = bindingAdapterPosition
                if (bindingAdapterPosition != RecyclerView.NO_POSITION) { //basically if position is not -1
                    val photo = getItem(position)
                    //we can access the getItem() as we have set this ViewHolder class as an inner class
                    if (photo != null) {
                        listener.onItemClick(photo)
                    }


                }

            }

        }
    }


}

//fun interface instead of just interface allows lambda
fun interface MyItemClickListener {
    fun onItemClick(photos: UnsplashGETResponse.UnsplashPhoto)
}


package com.avas.imagesearchapp.VIEW.FRAGMENTS

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.avas.imagesearchapp.MODEL.UnsplashGETResponse
import com.avas.imagesearchapp.R
import com.avas.imagesearchapp.VIEW.RECYCLERVIEWADAPTERS.GalleryRecyclerAdapter
import com.avas.imagesearchapp.VIEW.RECYCLERVIEWADAPTERS.MyItemClickListener
import com.avas.imagesearchapp.VIEW.RECYCLERVIEWADAPTERS.PagingLoadStateAdapter
import com.avas.imagesearchapp.VIEWMODEL.GalleryViewModel
import com.avas.imagesearchapp.databinding.FragmentLayoutBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_layout.*

private const val TAG = "MYAPP"

//We need to annotate android components such as activity, fragments, services, etc with this annotation
@AndroidEntryPoint
class GalleryFragment() : Fragment(R.layout.fragment_layout),
    MyItemClickListener {


    //this is how we inject a viewmodel using HILT, it is a little different, we need other dependencies, so check out build.gradle
    //file as well
    private val galleryViewModel: GalleryViewModel by viewModels()

    //we get our recycler adapter

    private var recylerAdapter = GalleryRecyclerAdapter(this)

    //getting the _binding object, we know why we do this like this, check out the ViewBinding project file if you forgot
    private var _binding: FragmentLayoutBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //this is now we get the _binding object in onViewCreated when we passes the layout in the constructor of the Fragment class
        _binding = FragmentLayoutBinding.bind(view)

        //SETTING UP THE RECYCLER VIEW
        binding.apply {

            recycler_view.setHasFixedSize(true)
            //we set the adapter and also attach the load state adapter
            //in this case we set the header and footer to the same load state adapter
            recycler_view.adapter = recylerAdapter.withLoadStateHeaderAndFooter(
                header = PagingLoadStateAdapter {
                    //this retry function automatically loads data when possible //we pass it as lambda as we need to do this when
                    //the user clicks the RETRY button in our HEADER/FOOTER
                    recylerAdapter.retry()
                },
                footer = PagingLoadStateAdapter {
                    recylerAdapter.retry()
                })
        }

        //load state listener is for when we try to search when there is no internet or when our search query has no results
        //just some things to make our experience better
        //DON'T GET CONFUSED WITH LOAD STATE ADAPTER WE ADDED ABOVE IN THE FRAGMENT, THAT ONLY APPLIES FOR WHEN WE ARE SCROLLING
        //THE RECYCLER VIEW . IT IS ONLY USED TO ADD HEADED AND FOOTER to the recycler view when scrolling
        recylerAdapter.addLoadStateListener {
            binding.apply {
                //recyclerview is visible when results are done loading
                recycler_view.isVisible = it.source.refresh is LoadState.NotLoading
                //a progress bar instead is visible when results are still loading
                progressBar.isVisible = it.source.refresh is LoadState.Loading
                //if we searach something when there is no internet, this button and text is displayed
                button_retry.isVisible = it.source.refresh is LoadState.Error
                error_text_view.isVisible = it.source.refresh is LoadState.Error

                //when retry button clicked, we just do this which tries to fetch data again
                button_retry.setOnClickListener {
                    recylerAdapter.retry()
                }

                //if our search query has no results, this text is displayed and recycler view is not visible
                if (it.source.refresh is LoadState.NotLoading && it.append.endOfPaginationReached && recylerAdapter.itemCount < 1) {
                    query_not_found_text_view.isVisible = true
                    recycler_view.isVisible = false
                } else {
                    queryNotFoundTextView.isVisible = false
                }
            }

        }

        //we pass the lifecycle owner as viewLifeCycleOwner NOT this(fragment), as we don't want to keep observing it if the view
        //is destroyed
        //we know fragments have two lifecycle, and fragment can still be in memory even if its view is already destroyed, eg. in
        //back stack
        //so to prevent memory leak, we only observe it as long as the view is alive, no the whole fragment
        galleryViewModel.photos.observe(viewLifecycleOwner) {
            //we pass the data to the recycler adapter
            Log.d(TAG, "onViewCreated: Updating the recycler view")
            recylerAdapter.submitData(viewLifecycleOwner.lifecycle, it)
        }


        //you need to do this in fragment for it to have an option menu
        setHasOptionsMenu(true)
    }

    //we created this method in interface to listen for item clicks in the recycler view
    override fun onItemClick(photos: UnsplashGETResponse.UnsplashPhoto) {
        //WE have use SAFEARGS plugins for NAVIGATION which allows us to navigate to another fragment easily like this
        //The below class and its method is generated by that plugin
        //Also why these were generated is that we defined the flow of navigation and the argument the detailsFragment takes in the
        //nav_graph, so check that out
        val action = GalleryFragmentDirections.actionGalleryFragmentToDetailsFragment(photos)
        findNavController().navigate(action)
        //Navigating to another fragment with safe arguments, when each item in the recycler view is clicked


    }

    var currentQuery: String? = null
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_layout, menu)
        val searchMenuItem = menu.findItem(R.id.app_bar_search)
        val searchView: SearchView = searchMenuItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    //search for that query
                    currentQuery = query
                    galleryViewModel.searchPhotos(query)
                    //scroll to the top
                    recycler_view.scrollToPosition(0)
                    //clears text focus on the search view and collapses keyboard
                    searchView.clearFocus()

                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                //no need to do anything here
                return true
            }

        }


        )


    }

    override fun onDestroyView() {
        super.onDestroyView()
        //when view is destroyed, we set the _binding to null
        _binding = null
    }


}
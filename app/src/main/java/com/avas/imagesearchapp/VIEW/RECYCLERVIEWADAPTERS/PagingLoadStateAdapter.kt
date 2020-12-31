package com.avas.imagesearchapp.VIEW.RECYCLERVIEWADAPTERS

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.avas.imagesearchapp.databinding.LoadStateHeaderFooterBinding


//USING THIS LOAD STATE ADAPTER, WE CAN SET THE HEADER AND FOOTER TO OUR RECYCLER VIEW
//THIS ADAPTER IS A PART OF PAGING3 LIBRARY
//This load state adapter detects if the pages are being loaded or if the internet connected is gone
//THE USE CASE:
//1. If you have slow internet or if you are scrolling through the recycler view fast, you can display a progress bar at the
//bottom(FOOTER) or top(TOP)
//2. If the internet is disconnected you can display a RETRY button at the bottom or top

//DON'T GET CONFUSED WITH LOAD STATE LISTENER WE ADDED TO THE ADAPTER IN THE FRAGMENT, THIS ONLY APPLIES FOR WHEN WE ARE SCROLLING
//THE RECYCLER VIEW

//FOR THIS WE NEED TO CREATE A NEW LAYOUT FILE FOR HEADER/FOOTER SO CHECK THAT OUT

//we take a lambda in constructor , as we need to perform action when user clicks the RETRY BUTTON
class PagingLoadStateAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<PagingLoadStateAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): MyViewHolder {
        val binding =
            LoadStateHeaderFooterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, loadState: LoadState) {
        //is is good to do stuff in the MyViewHolder instead of here
        holder.bind(loadState)
    }

    inner class MyViewHolder(val binding: LoadStateHeaderFooterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        //we need an onclicklistener for RETRY button. we just take a lambda as we provide it from the fragment
        init {
            binding.buttonRetry.setOnClickListener {
                //invoking the lambda function when user click retry button
                retry.invoke()
            }
        }

        fun bind(loadState: LoadState) {
            binding.apply {
                //so progressbar is visible is the pages are loading
                progressBar.isVisible = loadState is LoadState.Loading
                //retry button and text is visible if the pages are not loading (due to connection problem)
                binding.buttonRetry.isVisible = loadState !is LoadState.Loading
                binding.textView.isVisible = loadState !is LoadState.Loading
                //DONT WORRY ABOUT THE VISIBILITY NEEDED TO BE SET WHILE LOADING AGAIN or WHEN IN NORMAL CONDITION, THIS ADAPTER DOES
                //THAT FOR YOU// HENCE IT IS ONLY VISIBLE AT HEADER/FOOTER OR BOTH
            }

        }

    }


}
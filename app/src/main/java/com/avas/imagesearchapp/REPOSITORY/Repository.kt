package com.avas.imagesearchapp.REPOSITORY

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.avas.imagesearchapp.MODEL.UnsplashAPI
import com.avas.imagesearchapp.MODEL.UnsplashGETResponse
import com.avas.imagesearchapp.REPOSITORY.PAGING3.UnsplashPagingSource
import javax.inject.Inject
import javax.inject.Singleton

const val TAG = "MYAPP"

@Singleton
class Repository @Inject constructor(private val unsplashAPI: UnsplashAPI) {
    //we get the UnsplashAPI instance from HILT automatically
    //We put @Inject to tell HILT to inject this Repository class as well so that we can provide Repository instance for ViewModel

    fun searchPhotos(query: String): LiveData<PagingData<UnsplashGETResponse.UnsplashPhoto>> {
        //instead of calling the UnsplashAPI's function, we create a Pager object that calls the suspend load() function of the
        //UnsplashPagingSource automatically
        return Pager(config = PagingConfig(
            pageSize = 20,//this is the value passes as params.loadSize in the UnsplashPagingSource class's function call
            //Defines the number of items loaded at once from the PagingSource.
            maxSize = 100,//Defines the maximum number of items that may be loaded into PagingData(in the recycler view)
            // before pages should be dropped.
            enablePlaceholders = false//placeholders for objects that haven't been loaded yet
        ), pagingSourceFactory = {
            //lambda function, we return out UnsplashPagingSource object
            UnsplashPagingSource(unsplashAPI, query)
        }
        ).liveData

    }


    //Pager.liveData returns a LiveData<PagingData> object that we use in our recyclerview adapter to load data


}
package com.avas.imagesearchapp.REPOSITORY.PAGING3

import android.util.Log
import androidx.paging.PagingSource
import com.avas.imagesearchapp.MODEL.UnsplashAPI
import com.avas.imagesearchapp.MODEL.UnsplashGETResponse
import retrofit2.HttpException
import java.io.IOException

//Most apps displays a large list of data to the users, but at a particular time the user sees only a small chunk of data in your
//app, so fetching all the data from the network is not an efficient solution. The solution is to fetch small chunks of data at
//a time, and as soon as the user reach at the end of the list, then the app will load more data. This is called Paging.

//We don't inject this class using HILT as it takes a query parameter that we don't know in compile-time
//The user may search for any query, so we have to instantiate this class by ourselves

private const val TAG = "MYAPP"
private const val INITIAL_PAGE_NUMBER = 1

class UnsplashPagingSource(
    private val unsplashAPI: UnsplashAPI,
    private val query: String
) : PagingSource<Int, UnsplashGETResponse.UnsplashPhoto>() {
    //we have to implement a PagingSource<Key, Value> to define a data source. The PagingSource takes two parameters a Key and a
    // Value. The Key parameter is the identifier of the data to be loaded such as page number and the Value is the type of the
    // data itself, which is an UnsplashPhoto object

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UnsplashGETResponse.UnsplashPhoto> {
        //first we need to define the current page, if params.key is null, it means it is the first request, so the currentPage is
        //set to 1
        val currentPage = params.key ?: INITIAL_PAGE_NUMBER

        //we return a LoadResult.Page or a LoadResult.Error object depending on if the request was successful
        //LoadResult is a sealed class, so our option is to return a Page or an Error
        return try {

            //searchPhotos() is a suspend function as we defined in the API, so it is handled by Kotlin Coroutines//no need to call
            //it in a separate thread

            //we define params.loadSize when we create a Pager in the Repository//is is basically how many objects in a single page
            //i.e Defines the number of items loaded at once from the PagingSource.
            val response = unsplashAPI.searchPhotos(query, currentPage, params.loadSize)

            //the list of UnsplashPhotos is what we want to load
            val photos = response.results
            Log.d(TAG, "load: contacting the api for response")

            LoadResult.Page(
                data = photos,//data we want to load
                prevKey = if (currentPage == INITIAL_PAGE_NUMBER) null else currentPage - 1,//previous page if exists
                nextKey = if (photos.isEmpty()) null else currentPage + 1//next page if exists

            )

        } catch (e: IOException) {
            //error occurs when the internet is not connected
            Log.d(TAG, "load: network error occured: ")
            e.printStackTrace()
            LoadResult.Error(e)
        } catch (e: HttpException) {
            //error occurs when the server gives us error response
            Log.d(TAG, "load: api error occured: ${e.printStackTrace()}")
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }


}
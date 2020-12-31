package com.avas.imagesearchapp.VIEWMODEL

import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.avas.imagesearchapp.REPOSITORY.Repository

private const val TAG = "MYAPP"

//We use a special annotation for injecting ViewModels
//@ViewModelInject requires more dependencies so check out the module-level build.gradle file
class GalleryViewModel @ViewModelInject constructor(
    private val repository: Repository,
    @Assisted stateHandle: SavedStateHandle
) :
    ViewModel() {
    //PROCESS DEATH
    //The SavedStateHandle above injected by @Assisted helps us to survive process death
    //Process death occurs when ur app is in the background and the system kills its process to free resources
    //A view model cannot survive that as it is not just a configuration change

    //In our case, example, when we open apps(query "cats" is loaded by default) and we search dogs and scroll down
    //and send our app to background, wait a while till the process gets killed and when we return to our app,
    //it loads cats again AS THE VIEW MODEL HAS TO BE LOADED AGAIN//we lose our query//the scroll position of the
    //RecyclerView stays the same as Paging3 library somehow manages to save the state of scroll

    //SO WE HAVE TO SAVE THE QUERY IN THIS CASE so that the query can be loaded again even after process death


    companion object {
        //this is a KEY for out savedStateHandle
        const val CURRENT_QUERY_KEY = "current_key"
    }

    //here we get currentQuery from SavedStateHandle so it survives Process Death
    //initially we load it up with "cats"//so when app starts cats are loaded
    //if user enters any search query, the searchPhotos(...) is called and the CURRENT_VALUE key is mapped to that query and that is saved and
    //is loaded if process death occurs//any newer query by the user takes this CURRENT_QUERY_KEY and is loaded upon process death
    private val currentQuery = stateHandle.getLiveData(CURRENT_QUERY_KEY, "cats")

    //switchMap when applied to a String liveData, it provides us with the new value string as a parameter in the lambda
    //so we can use that new string to do what we want
    //This is different than observe() as we don't have to pass an lifecycle owner
    val photos = currentQuery.switchMap {
        //whenever the currentQuery changes, we call the search function//to make new request to the API
        Log.d(TAG, "Query: Contacting the api for the new query that user entered")
        repository.searchPhotos(it)
    }.cachedIn(viewModelScope)
    //we have to do cachedIn() as if we don't or app will crash upon orientation change as we cant load from the same PagingData
    //twice


    //when called from fragment//changes the currentQuery//which triggers the switchMap function of the livedata
    fun searchPhotos(query: String) {
        Log.d(TAG, "searchPhotos: User entered new query")
        currentQuery.value = query

    }


}
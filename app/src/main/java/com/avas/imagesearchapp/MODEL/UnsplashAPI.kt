package com.avas.imagesearchapp.MODEL

import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

//WE GET THE INSTANCE OF THIS INTERFACE FROM RETROFIT, BOTH OF WHICH ARE IN THE HILT MODULE AS WE ARE DOING DEPENDENCY INJECTION

interface UnsplashAPI {

    companion object {
        const val BASE_URL = "https://api.unsplash.com/"
        const val ACCESS_KEY = "YOUR_KEY_HERE"
    }
    //Suspending functions are at the center of everything coroutines. A suspending function is simply a function that can be paused
    //and resumed at a later time. They can execute a long running operation without blocking the thread.
    //So basically it tells the thread don't wait for me, do other things nigga. So the thread is not blocked.
    //Suspending functions can only be invoked by another suspending function or within a coroutine.

    @Headers("Accept-Version: v1", "Authorization: Client-ID $ACCESS_KEY")
    @GET("search/photos")
    suspend fun searchPhotos(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): UnsplashGETResponse

}

//As per the documentation, we had to add those things to the HEADER while sending request because:
//1. All requests receive the v1 version of the API. We encourage you to specifically request this via the Accept-Version header://
//Accept-Version: v1
//2. Most actions can be performed without requiring authentication from a specific user. For example, searching, fetching, or
// downloading a photo does not require a user to log in.
//To authenticate requests in this way, pass your application’s access key via the HTTP Authorization header:
//Authorization: Client-ID YOUR_ACCESS_KEY

//Also to GET request for search a photo can takes these QUERY, as per the documentation:
//query	-> Search terms.
//page	-> Page number to retrieve. (Optional; default: 1)
//per_page	-> Number of items per page. (Optional; default: 10)
//order_by	-> How to sort the photos. (Optional; default: relevant). Valid values are latest and relevant.
//collections	-> Collection ID(‘s) to narrow search. Optional. If multiple, comma-separated.
//content_filter	->  Limit results by content safety. (Optional; default: low). Valid values are low and high.
//color	-> Filter results by color. Optional. Valid values are: black_and_white, black, white, yellow, orange, red, purple,
//magenta, green, teal, and blue.
//orientation	-> Filter by photo orientation. Optional. (Valid values: landscape, portrait, squarish)
package com.avas.imagesearchapp.DaggerHILT.ApplicationWrapper

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

//THIS IS REQUIRED EVERY TIME YOU USE HILT//CHECK THE MANIFEST FILE AS WELL AS WE NEED TO SET THE android:name FOR THE <application>

//YOU MUST ADD THIS ANNOTATION. This is like basically ENABLING Hilt for your app
//This annotation enables member injection (i.e) field and method injection in your Application class.
//So basically you can @Inject dependencies in this class//not needed for most cases tho

@HiltAndroidApp
class MyApplication : Application() {
}
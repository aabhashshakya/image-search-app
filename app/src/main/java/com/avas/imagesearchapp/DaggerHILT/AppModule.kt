package com.avas.imagesearchapp.DaggerHILT

import com.avas.imagesearchapp.MODEL.UnsplashAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

//WE NEED TO CREATE A MODULE TO PROVIDE INSTANCES OF RETROFIT AS WE DON'T OWN THE CLASS
@Module
//@InstallIn means its lifecycle. ApplicationComponent means its lifecycle is the same as of the app
//Other options are ActivityComponent, FragmentComponent, etc. which means its lifecycle is tied to these components
@InstallIn(ApplicationComponent::class)
//we prefer object when creating modules FOR CLASSES WE DON'T OWN
object AppModule {

    //Provides annotation tells HILT that this method provides an instance//we never call these functions// they are for HILT
    @Provides
    @Singleton //we do singleton as we only ever need 1 instance of these
    fun providesRetrofit(): Retrofit {
        return Retrofit.Builder().baseUrl(UnsplashAPI.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).build()
    }

    //UnsplashAPI is an interface so we ideally should have used @Binds but for that we need an abstract function
    //and an abstract class so we do it with @Provides, no biggie
    @Provides
    @Singleton
    //See since HILT already know how to provide retrofit instance, we can put retrofit as a parameter and HILT will provide
    //automatically
    fun providesUnsplashApi(retrofit: Retrofit): UnsplashAPI {
        return retrofit.create(UnsplashAPI::class.java)

    }


}
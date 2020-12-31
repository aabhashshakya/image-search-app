package com.avas.imagesearchapp.VIEW

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.avas.imagesearchapp.R
import dagger.hilt.android.AndroidEntryPoint


//PLEASE LOOK AT ALL THE LAYOUT FILES IN CODE VIEW TO LEARN MORE ABOUT NAVIGATION COMPONENT AND OTHER STUFFS
//ALSO look at the build.gradle files too


//We need to annotate android components such as activity, fragments, services, etc with this annotation
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {


    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //we delete the external cache// the cache was used in DetailsFragment to store the photo before sharing it to other apps
        //check out below
        //WE CLEAR THE CACHE IN ONCREATE() so it is cleard evertime the app starts
        //we dont clear it in onDestroy() as it may not be called sometimes for eg. when we clear our app from the recent apps list
        externalCacheDir?.deleteRecursively()
        //we also delete cacheDir as it taking lot of space lololol
        cacheDir.deleteRecursively()


        //We know that the FragmentContainerView is the navigation host as it derives from the NavHostFragment class
        // i.e android:name="androidx.navigation.fragment.NavHostFragment" CHECK LAYOUT
        //so we get that
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container_nav_host) as NavHostFragment

        //you cannot do this get the Nav Controller as it will give an error
        //navController = navHostFragment.findNavController()
        //navController = findNavController(R.id.fragment_container_nav_host)

        //we use the NavHostFragment above to find the Nav Controller
        navController = navHostFragment.findNavController()


        //THESE ARE FOR PUTTING AN 'UP' BUTTON TO THE ACTION BAR WHEN WE NAVIGATE AROUND THE APP

        //we create a new App Bar with the nav graph//so it automatically knows the back stack
        //or like we did here use a builder and select the top level destinations
        val appBarConfiguration = AppBarConfiguration.Builder(
            R.id.galleryFragment
        ).build()
        //we set up the action bar to the app bar we just created using navigation graph
        setupActionBarWithNavController(navController, appBarConfiguration)
        //YOU CAN CHANGE WHAT NAME OF THE FRAGMENT APPEARS N THE ACTION BAR BY CHANGING THE 'LABEL' OF THE FRAGMENTS IN THE NAV_GRAPH


    }

    //we need to override this method to modify what the UP button does
    override fun onSupportNavigateUp(): Boolean {
        //returning this will automaticaaly navigate up the nav graph and to the previous fragment if available
        return navController.navigateUp() || super.onSupportNavigateUp()
    }


}
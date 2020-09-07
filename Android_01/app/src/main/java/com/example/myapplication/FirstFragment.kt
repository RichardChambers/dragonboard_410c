package com.example.myapplication

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    // create a variable in which we keep the last location reported
    // and an inner class for our LocationListener that we will be
    // using to update the variable.
    //  https://kotlinlang.org/docs/reference/nested-classes.html
    //
    private lateinit var lastLocationReported: Location

    inner class MyLocation  : LocationListener {
        override fun onLocationChanged(p0: Location) {
            lastLocationReported = p0
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.button_first).setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        // find the button whose id is button_Location and then set an listener for
        // any clicks on that button. In the following listener we are going to have
        // the "Location" button, defined in the file fragment_first.xml, generate a
        // list of the GPS service providers by creatinga LocationManager object to
        // generate a list.
        view.findViewById<Button>(R.id.button_location).setOnClickListener {
            // See https://stackoverflow.com/questions/13306254/how-to-get-a-reference-to-locationmanager-inside-a-fragment
            // See as well manifest change for permissions.
            //    https://stackoverflow.com/questions/6899988/android-location-manager-permissions-to-be-used
            val mgr = activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager

            val gpslisten = MyLocation()

            val provider_list = mgr.allProviders
            val txtScroll = view.findViewById(R.id.providers) as TextView

            if (mgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                txtScroll.append("GPS  Provider is enabled.\n")

                // requestLocationUpdates() may throu SecurityException so we need to
                // use a try/catch. The value of a try expression is the result of
                // the last expression.
                // https://kotlinlang.org/docs/reference/exceptions.html
                try {
                    mgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0.0F, gpslisten)
                    1     // indicate success with a value of 1
                }
                catch (e : SecurityException) {
                    txtScroll.append("SecurityException from mgr.requestLocationUpdates() " + e.message + "\n")
                    0     // indicate failure with a value of 0
                }
            } else {
                txtScroll.append("GPS  Provider is NOT enabled.\n")
            }

            txtScroll.append("List of Location Providers\n")
            for (items in provider_list) {
                txtScroll.append (items)
                txtScroll.append("\n")
            }
        }
    }
}
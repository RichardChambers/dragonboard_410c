package com.example.myapplication

import android.content.Context
import android.location.LocationManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.findNavController

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.button_second).setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }

        val txtScroll = view.findViewById(R.id.LedStatus) as TextView

        // find the button whose id is button_Location and then set an listener for
        // any clicks on that button. In the following listener we are going to have
        // the "Location" button, defined in the file fragment_first.xml, generate a
        // list of the GPS service providers by creatinga LocationManager object to
        // generate a list.
            val gpioProcessor_x =  GpioProcessor()
            // Get reference of GPIO23.
            val gpioPin23_x = gpioProcessor_x.pin23
            gpioPin23_x.exportPin()

        view.findViewById<Button>(R.id.button_led_off).setOnClickListener {
            val gpioProcessor =  GpioProcessor()
            // Get reference of GPIO23.
            val gpioPin23 = gpioProcessor.pin23

            // Set GPIO23 as output.
            gpioPin23.pinOut()
            gpioPin23.pinLow()    // drive pin low to turn off LED.
            txtScroll.append("LED Off\n")
        }

        view.findViewById<Button>(R.id.button_led_on).setOnClickListener {
            val gpioProcessor =  GpioProcessor()
            // Get reference of GPIO23.
            val gpioPin23 = gpioProcessor.pin23

            // Set GPIO23 as output.
            gpioPin23.pinOut()
            gpioPin23.pinHigh()    // drive pin high to turn on LED
            txtScroll.append("LED On\n")
        }

        view.findViewById<Button>(R.id.button_led_blink).setOnClickListener {
            val myBlinkThread = led_thread()
            myBlinkThread.start()
            txtScroll.append("LED begin blink\n")
        }

    }
}
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
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private var pinUser1State = 0
    private var pinUser2State = 0

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

        view.findViewById<Button>(R.id.button_edge_poll).setOnClickListener {
            txtScroll.append("Edge Poll pin set up for rising. Polling now.\n")

                    val gpioProcessor = GpioProcessor()
                    // Get reference of GPIO23.
                    val gpioPin26 = gpioProcessor.pin26

                    // Set GPIO26 as input.
                    gpioPin26.pinIn()
                    gpioPin26.pinEdgeRising()
                    txtScroll.append("    Edge setting - ${gpioPin26.edge} active - ${gpioPin26.active_low} direction - ${gpioPin26.direction}\n")

                    gpioPin26.onPollStatusChanged = { _, _ ->

                        val xStatus = gpioPin26.lastPinPollStatus
                        val xvalue = gpioPin26.value
                        val xRevents: Int = gpioPin26.pinPollRevents()

                        if (xStatus == 0) {
                            txtScroll.append("    Edge Poll pin value " + xvalue.toString() + "\n")
                            txtScroll.append("    Edge poll Revents - " + xRevents.toString(16))
                        } else {
                            txtScroll.append("    Edge Poll pin failed - " + Integer.toString(xStatus) + "\n")
                            txtScroll.append("    Edge poll Revents - " + xRevents.toString(16))
                            txtScroll.append("    Edge Poll pin value " + xvalue.toString() + "\n")
                        }
                    }
                    MainScope().launch {
                        gpioPin26.pinPoll(10000)
                    }
                txtScroll.append(" *Polling launched.\n")
        }


        view.findViewById<Button>(R.id.button_led_blink).setOnClickListener {
            val myBlinkThread = led_thread()
            myBlinkThread.start()
            txtScroll.append("LED begin blink\n")
        }

        view.findViewById<Button>(R.id.button_led_blink_multi).setOnClickListener {
            val myBlinkThread = led_thread_multi()
            myBlinkThread.start()
            txtScroll.append("LED Multi begin blink\n")
        }

        view.findViewById<Button>(R.id.button_led_user1).setOnClickListener {
            val gpioProcessorLed =  GpioProcessorLed()
            val gpiopinLed = gpioProcessorLed.pinLed1

            if (pinUser1State == 0) {

                val myValue : Int = gpiopinLed.brightness
                gpiopinLed.pinHigh()    // drive pin high to turn on LED
                txtScroll.append("LED Uer 1 On " + gpiopinLed.pin + " was " + myValue.toString() + "\n")
                pinUser1State = 1
            } else {
                val myValue : Int = gpiopinLed.brightness
                gpiopinLed.pinLow()    // drive pin low to turn off LED
                txtScroll.append("LED Uer 1 Off " + gpiopinLed.pin + " was " + myValue.toString() + "\n")
                pinUser1State = 0
            }
        }

        view.findViewById<Button>(R.id.button_led_user2).setOnClickListener {
            val gpioProcessorLed =  GpioProcessorLed()
            val gpiopinLed = gpioProcessorLed.pinLed2

            if (pinUser2State == 0) {
                val myValue : Int = gpiopinLed.brightness
                gpiopinLed.pinHigh()    // drive pin high to turn on LED
                txtScroll.append("LED User 2 On " + gpiopinLed.pin + " was " + myValue.toString() + "\n")
                pinUser2State = 1
            } else {
                val myValue : Int = gpiopinLed.brightness
                gpiopinLed.pinLow()    // drive pin low to turn off LED
                txtScroll.append("LED User 2 Off " + gpiopinLed.pin + " was " + myValue.toString() + "\n")
                pinUser2State = 0
            }
        }

    }
}
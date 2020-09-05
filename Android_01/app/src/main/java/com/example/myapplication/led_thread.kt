package com.example.myapplication

class led_thread : Thread () {
    override fun run() {
        super.run()

        val gpioProcessor =  GpioProcessor()

        // Get reference of GPIO23.
        val gpioPin23 = gpioProcessor.pin23

        // Set GPIO23 as output and initialize its state to off.
        gpioPin23.pinOut()
        gpioPin23.pinLow()    // drive pin low to initially turn off LED.

        Thread.sleep(1000)     // pause for a moment before we begin.

        // now begin our sequence of LED on/off
        for (i in 0 until 10) {
            gpioPin23.pinHigh()    // drive pin high to turn on LED
            Thread.sleep(300)
            gpioPin23.pinLow()    // drive pin low to turn off LED.
            Thread.sleep(300)
        }
    }
}
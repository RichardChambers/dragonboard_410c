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

class led_thread_multi : Thread () {
    public val gpioLedPins : IntArray = intArrayOf(0, 0, 0)

    override fun run() {
        super.run()

        val gpioProcessor =  GpioProcessor()

        // Get reference of GPIO23.
        val gpioPin_multi = setOf<Gpio>(gpioProcessor.pin23, gpioProcessor.pin25, gpioProcessor.pin27)

        // Set GPIO pins as output and initialize their state to off.
        for (i in gpioPin_multi) {
            i.pinOut()
            i.pinLow()    // drive pin low to initially turn off LED.
        }

        Thread.sleep(1000)     // pause for a moment before we begin.

        // now begin our sequence of LED on/off
        for (i in 0 until 10) {
            for (j in gpioPin_multi) {
                j.pinHigh()    // drive pin high to turn on LED
            }
            Thread.sleep(300)
            for (j in gpioPin_multi) {
                j.pinLow()    // drive pin low to turn off LED.
            }
            Thread.sleep(300)
        }
    }
}
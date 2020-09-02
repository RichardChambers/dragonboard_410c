package com.example.myapplication

import java.io.*

/**
 * Created by Ara on 7/21/15.
 * From https://www.instructables.com/id/DragonBoard-How-to-Access-GPIOs-Using-Java/
 *   Java source from the article was converted to Kotlin using Android Studio.
 *
 * See as well https://github.com/IOT-410c/DragonBoard410c_GpioLibrary
 *
 */
class Gpio(pin: Int) {
    private val pin: Int

    /*
     *  The GPIO pins are represented by folders in the Linux file system
     *  within the folder /sys/class/gpio. Each pin is represented by a folder
     *  whose name is the prefix "gpio" followed by the pin number.
     *  Within the folder representing the pin are two files, "value" used to
     *  set or get the value of the pin and "direction" used to set or get
     *  the direction of the pin.
     *
     *  This function creates the path to the Linux file which represents a particular
     *  GPIO pin function, "value" or "direction".
     */
    private fun MakeFileName(pin: Int, op: String): String {
        return "/sys/class/gpio/gpio$pin$op"
    }

    /*
     * Get or set the current direction of a pin.
     * A pin may be either an Input pin or an Output pin.
     */
    var direction: String
        get() {
            println("Getting Direction")
            var line = ""
            try {
                val br = BufferedReader(FileReader(MakeFileName(pin, "/direction")))
                line = br.readLine()
                br.close()
            } catch (e: Exception) {
                println("Error: " + e.message)
            }
            return line
        }
        private set(direction) {
            println("Setting Direction")
            try {
                val out = BufferedWriter(FileWriter(MakeFileName(pin, "/direction"), false))
                out.write(direction)
                out.close()
            } catch (e: IOException) {
                println("Error: " + e.message)
            }
        }

    /**
     * Get or Set pin value.
     * @param value Value of pin.
     * 0 -> Low Level.
     * 1 -> High Level
     */
    var value: Int
        get() {
            println("Getting Value")
            var line = ""
            try {
                val br = BufferedReader(FileReader(MakeFileName(pin, "/value")))
                line = br.readLine()
                br.close()
            } catch (e: Exception) {
                println("Error: " + e.message)
            }
            return line.toInt()
        }
        private set(value) {
            println("Setting Value")
            try {
                val out = BufferedWriter(FileWriter(MakeFileName(pin, "/value"), false))
                out.write(Integer.toString(value))
                out.close()
            } catch (e: IOException) {
                println("Error: " + e.message)
            }
        }

    /**
     * Set pin as high.
     */
    fun pinHigh() {
        value = HIGH
    }

    /**
     * Set pin as low.
     */
    fun pinLow() {
        value = LOW
    }

    /**
     * Set pin as output.
     */
    fun pinOut() {
        direction = "out"
    }

    /**
     * Set pin as input.
     * @param pin - Desirable pin.
     */
    fun pinIn() {
        direction = "in"
    }

    fun exportPin() {
        println("Exporting Ping")
        try {
            val out = BufferedWriter(FileWriter("$PATH/export", false))
            out.write(pin.toString())
            out.close()
        } catch (e: IOException) {
            println("Error: " + e.message)
        }
    }

    /**
     * Disable access to GPIO.
     * @param pin GPIO pin to disable access.
     */
    fun unexportPin() {
        println("unExporting Ping")
        try {
            val out = BufferedWriter(FileWriter("$PATH/unexport", false))
            out.write(pin.toString())
            out.close()
        } catch (e: IOException) {
            println("Error: " + e.message)
        }
    }

    companion object {
        const val HIGH = 1
        const val LOW = 0
        private const val PATH = "/sys/class/gpio"
    }

    /**
     * Set desirable pin for the GPIO class.
     */
    init {
        println("Initializing pin $pin")
        this.pin = pin
    }
}
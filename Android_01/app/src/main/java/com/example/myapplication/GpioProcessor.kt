package com.example.myapplication

import java.io.BufferedWriter
import java.io.FileWriter
import java.io.IOException
import java.util.*

/**
 * Created by Ara on 7/21/15.
 * From https://www.instructables.com/id/DragonBoard-How-to-Access-GPIOs-Using-Java/
 *   Java source from the article was converted to Kotlin using Android Studio.
 *
 * See as well https://github.com/IOT-410c/DragonBoard410c_GpioLibrary
 *
 * Simple example main()
 *
 * public class Main {
 *
 * public static void main(String[] args) {
 * int count = 0;
 * int buttonValue = 0;
 *
 * GpioProcessor gpioProcessor = new GpioProcessor();
 *
 * // Get reference of GPIO27 and GPIO29.
 *
 * Gpio gpioPin27 = gpioProcessor.getPin27();
 * Gpio gpioPin29 = gpioProcessor.getPin29();
 *
 * // Set GPIO27 as output.Set GPIO29 as input.
 * gpioPin27.pinOut();
 * gpioPin29.pinIn();
 *
 * while(count<20){
 * count++;
 * // Read value of GPIO29.
 * buttonValue=gpioPin29.getValue();
 *
 * if(buttonValue == 0){
 * // Set GPIO27 as low level.
 * gpioPin27.pinLow();
 * } else{
 * // Set GPIO27 as high level.
 * gpioPin27.pinHigh();
 * }
 *
 * try {
 * Thread.sleep(1000);
 * } catch(InterruptedException e){
 * // TODO Auto-generated catch block
 * e.printStackTrace();
 * }
 * }
 *
 * // Disable access GPIO27 and GPIO29.
 * gpioProcessor.closePins();
 * }
 * }
 */ /*
 This class abstracts the use of the gpio pins. This class can be utilized on any linux operating
 system that has gpio pins defined in the /sys/class/gpio directory. It is required that the gpio
 pins themselves are available for access by the user of this application, and may require a
 change of permissions.
 */
class GpioProcessor {
    private val PATH = "/sys/class/gpio"
    private val pins: MutableList<Int> = ArrayList()

    // mapping of physical pin number to GPIO file number.
    // the mapping varies depending on the operating system
    private val  androidPin23 = 938
    private val  androidPin24 = 914
    private val  androidPin25 = 915
    private val  androidPin26 = 971
    private val  androidPin27 = 1017
    private val  androidPin28 = 0      // Reserved
    private val  androidPin29 = 926   // (input only)
    private val  androidPin30 = 927
    private val  androidPin31 = 937
    private val  androidPin32 = 936
    private val  androidPin33 = 930
    private val  androidPin34 = 935

    private val  linuxPin23 = 36
    private val  linuxPin24 = 12
    private val  linuxPin25 = 13
    private val  linuxPin26 = 69
    private val  linuxPin27 = 115
    private val  linuxPin28 = 0     // Reserved
    private val  linuxPin29 = 24    // (input only)
    private val  linuxPin30 = 25
    private val  linuxPin31 = 35
    private val  linuxPin32 = 34
    private val  linuxPin33 = 28
    private val  linuxPin34 = 33

    private val  physicalPin23 = androidPin23
    private val  physicalPin24 = androidPin24
    private val  physicalPin25 = androidPin25
    private val  physicalPin26 = androidPin26
    private val  physicalPin27 = androidPin27
    private val  physicalPin28 = androidPin28      // Reserved
    private val  physicalPin29 = androidPin29    // (input only)
    private val  physicalPin30 = androidPin30
    private val  physicalPin31 = androidPin31
    private val  physicalPin32 = androidPin32
    private val  physicalPin33 = androidPin33
    private val  physicalPin34 = androidPin34

    /**
     * Get function of specific pin.
     * @param pin Desirable pin.
     */
    fun getPin(pin: Int): Gpio {
        exportPin(pin)
        pins.add(pin)
        return Gpio(pin)
    }

    /**
     * Get pin 23;
     * @returns {Gpio}
     */
    val pin23: Gpio
        get() = getPin(physicalPin23)

    /**
     * Get pin 24.
     * @returns {Gpio}
     */
    val pin24: Gpio
        get() = getPin(physicalPin24)

    /**
     * Get pin 25.
     * @returns {Gpio}
     */
    val pin25: Gpio
        get() = getPin(physicalPin25)

    /**
     * Get pin 26.
     * @returns {Gpio}
     */
    val pin26: Gpio
        get() = getPin(physicalPin26)

    /**
     * Get pin 27.
     * @returns {Gpio}
     */
    val pin27: Gpio
        get() = getPin(physicalPin27)

    /**
     * Get pin 28.
     * @returns {Gpio}
     */
    val pin28: Gpio
        get() = getPin(physicalPin28)

    /**
     * Get pin 29.
     * @returns {Gpio}
     */
    val pin29: Gpio
        get() = getPin(physicalPin29)

    /**
     * Get pin 30.
     * @returns {Gpio}
     */
    val pin30: Gpio
        get() = getPin(physicalPin30)

    /**
     * Get pin 31.
     * @returns {Gpio}
     */
    val pin31: Gpio
        get() = getPin(physicalPin31)

    /**
     * Get pin 32.
     * @returns {Gpio}
     */
    val pin32: Gpio
        get() = getPin(physicalPin32)

    /**
     * Get pin 33.
     * @returns {Gpio}
     */
    val pin33: Gpio
        get() = getPin(physicalPin33)

    /**
     * Get pin 34.
     * @returns {Gpio}
     */
    val pin34: Gpio
        get() = getPin(physicalPin34)

    /**
     * Get all GPIO's pins.
     * @return List of pins.
     */
    val allPins: Array<Gpio?>
        get() {
            val allPins = arrayOfNulls<Gpio>(12)   // android       linux
            allPins[0] = pin23                          // GPIO 938     GPIO 36
            allPins[1] = pin24                          // GPIO 914     GPIO 12
            allPins[2] = pin25                          // GPIO 915     GPIO 13
            allPins[3] = pin26                          // GPIO 971     GPIO 69
            allPins[4] = pin27                          // GPIO 1017    GPIO 115
            allPins[5] = pin28                          // Reserved
            allPins[6] = pin29                          // GPIO 926     GPIO 24 (input only)
            allPins[7] = pin30                          // GPIO 927     GPIO 25
            allPins[8] = pin31                          // GPIO 937     GPIO 35
            allPins[9] = pin32                          // GPIO 936     GPIO 34
            allPins[10] = pin33                         // GPIO 930     GPIO 28
            allPins[11] = pin34                         // GPIO 935     GPIO 33
            return allPins
        }

    /**
     * Enable access to GPIO.
     * @param pin GPIO pin to access.
     */
    private fun exportPin(pin: Int) {
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
    private fun unexportPin(pin: Int) {
        println("unExporting Ping")
        try {
            val out = BufferedWriter(FileWriter("$PATH/unexport", false))
            out.write(pin.toString())
            out.close()
        } catch (e: IOException) {
            println("Error: " + e.message)
        }
    }

    fun closePins() {
        for (pin in pins) {
            unexportPin(pin)
        }
        pins.clear()
    }

    companion object {
        const val TAG = "GpioProcessor"
    }
}
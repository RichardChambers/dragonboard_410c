package com.example.myapplication

import java.io.*
import kotlin.properties.Delegates

/**
 * Originall Created by Ara on 7/21/15.
 * From https://www.instructables.com/id/DragonBoard-How-to-Access-GPIOs-Using-Java/
 *   Java source from the article was converted to Kotlin using Android Studio.
 *
 * The source has since been heavily modified by Richard Chambers, 9/17/2020
 *   - added GpioLed class
 *   - added GpioPwm class
 *
 * See as well https://github.com/IOT-410c/DragonBoard410c_GpioLibrary for a similar
 * library.
 *
 * See also for GPIO:
 *   https://stackoverflow.com/questions/63769403/what-is-the-sys-class-gpio-export-and-sys-class-gpio-unexport-mechanism-and-w
 *   https://www.kernel.org/doc/Documentation/gpio/sysfs.txt
 *   https://www.kernel.org/doc/html/v5.4/admin-guide/gpio/sysfs.html
 *
 * See also for PWM (Pulse Width Modulation)
 *   https://www.kernel.org/doc/html/latest/driver-api/pwm.html
 *   https://www.technexion.com/support/knowledgebase/using-pwm-from-a-linux-shell/
 *   https://wiki.phytec.com/productinfo/how-to-articles/how-to-use-pwm-in-linux
 *   https://openwrt.org/docs/guide-user/hardware/pwm
 *   https://raspberrypi.stackexchange.com/questions/66890/accessing-pwm-module-without-root-permissions
 *   https://www.96boards.org/documentation/consumer/dragonboard/dragonboard410c/guides/pmic-pwm.md.html
 *   https://makingaquadrotor.wordpress.com/2012/09/06/explanation-of-the-pwm-modules-on-the-beaglebone/
 *
 *   https://www.kernel.org/doc/Documentation/hwmon/sysfs-interface
 *   https://www.kernel.org/doc/html/latest/hwmon/sysfs-interface.html
 *
 *   https://docs.google.com/a/beagleboard.org/document/d/17P54kZkZO_-JtTjrFuVz-Cp_RMMg7GB_8W9JK9sLKfA/pub
 *   https://community.intel.com/t5/Intel-Makers/Programming-GPIO-Ports-From-Linux/td-p/502963?profile.language=en
 */

class GpioFile {

    /*
     * WARNING: The file access permissions for the corresponding user LED sysfs
     *          pseudo file must allow for the pseudo file to be opened and updated
     *          in order for the set() function to work.
     */
    fun setPseudoFile (pinPathFull : String, pinValue : String) : Int {
        println("    setPseudoFile - String")
        return try {
            val out = BufferedWriter(FileWriter(pinPathFull, false))
            out.write(pinValue)
            out.close()
            1
        } catch (e: IOException) {
            println("Error: " + e.message)
            0
        }
    }

    /*
     * WARNING: The file access permissions for the corresponding user LED sysfs
     *          pseudo file must allow for the pseudo file to be opened and updated
     *          in order for the set() function to work.
     */
    fun setPseudoFile(pinPathFull : String, pinValue : Int) : Int {
        println("   setPseudoFile - Int")
        return setPseudoFile(pinPathFull, Integer.toString(pinValue))
    }

    fun getPseudoFile(pinPathFull : String)  : String {
        println("    getPseudoFile - String")
        var line = ""
        try {
            val br = BufferedReader(FileReader(pinPathFull))
            line = br.readLine()
            br.close()
        } catch (e: IOException) {
            println("Error: " + e.message)
        }
        return line
    }

    fun getPseudoFileInt(pinPathFull : String)  : Int {
        println("    getPseudoFile - Int")
        // should there be a problem with the reading of the pseudo file
        // what will be returned is an empty string. when we try to convert
        // the empty string to an integer we will get an exception so we
        // will just return a value of zero (0).
        return try {
            getPseudoFile(pinPathFull).toInt()
        } catch (e: Exception) {
            println ("Error: " + e.message)
            0
        }
     }

    suspend fun pollPseudoFile (pinPathFull : String, timeOutMs : Int) : Int {
        println("    pollPseudoFile - String")
        return MainActivity().pollFileWithTimeOut (pinPathFull, timeOutMs)
    }

    fun pollPseudoFileRevents () : Int {
        println("    pollPseudoFileRevents - ")
        return MainActivity().pollGetLastRevents ()
    }
}

class Gpio(pin: Int)  {
    private val pin : Int
    private val pinGpio : GpioFile = GpioFile()

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
     *
     * "direction" ... reads as either "in" or "out". This value may
     * normally be written. Writing as "out" defaults to
     * initializing the value as low. To ensure glitch free
     * operation, values "low" and "high" may be written to
     * configure the GPIO as an output with that initial value.
     *
     * Note that this attribute *will not exist* if the kernel
     * doesn't support changing the direction of a GPIO, or
     * it was exported by kernel code that didn't explicitly
     * allow userspace to reconfigure this GPIO's direction.
    */
    var direction: String
        get() {
            println("Getting Direction")
            return pinGpio.getPseudoFile(MakeFileName(pin,  "/direction"))
        }
        private set(direction) {
            println("Setting Direction")
            pinGpio.setPseudoFile(MakeFileName(pin,  "/direction"), direction)
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

    /**
     * Get or Set pin value.
     * @param value Value of pin.
     * 0 -> Low Level.
     * 1 -> High Level
     *
     * "value" ... reads as either 0 (low) or 1 (high). If the GPIO
     * is configured as an output, this value may be written;
     * any nonzero value is treated as high.
     *
     * If the pin can be configured as interrupt-generating interrupt
     * and if it has been configured to generate interrupts (see the
     * description of "edge"), you can poll(2) on that file and
     * poll(2) will return whenever the interrupt was triggered. If
     * you use poll(2), set the events POLLPRI and POLLERR. If you
     * use select(2), set the file descriptor in exceptfds. After
     * poll(2) returns, either lseek(2) to the beginning of the sysfs
     * file and read the new value or close the file and re-open it
     * to read the value.
     */
    var value: Int
        get() {
            println("Getting Value")
            return pinGpio.getPseudoFileInt(MakeFileName(pin,  "/value"))
        }
        private set(value) {
            println("Setting Value")
            pinGpio.setPseudoFile (MakeFileName(pin,  "/value"), value)
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

// Check the specified file using the poll(2) service and
// return a status as follows:
//  -    0  -> poll(2) success indicating something is available
//  -    1  -> poll(2) failed with time out before anything available
//  -   -1  -> poll(2) error - EFAULT
//  -   -2  -> poll(2) error - EINTR
//  -   -3  -> poll(2) error - EINVAL
//  -   -4  -> poll(2) error - ENOMEM
//  - -100 -> poll(2) error - Unknown error
//
    var pollStatusShadow : Int by Delegates.observable(0) { _, oldValue, newValue ->
        // this variable is used to allow the user of this pin who is using
        // polling to set a listener so that when a pinPoll() is done, they
        // will get an event indicating the poll has finished.
        // this variable is toggled between the values of 0 and 1 by setting
        // its value using a statement of "pollStatusShadow = 1 - pollStatusShadow"
        onPollStatusChanged?.invoke(oldValue, newValue)
    }
    var onPollStatusChanged: ((Int, Int) -> Unit)? = null

    var lastPinPollStatus : Int = 0

    suspend fun pinPoll (timeMs: Int) : Int {
        lastPinPollStatus = pinGpio.pollPseudoFile (MakeFileName(pin,  "/value"), timeMs)
        pollStatusShadow = 1 - pollStatusShadow   // toggle the poll status to let observables know its done.
        return lastPinPollStatus
    }

    fun pinPollRevents () : Int {
        return pinGpio.pollPseudoFileRevents()
    }

    /**
     * Get or Set pin edge.
     * @param value Value of pin.
     *
     * "edge" ... reads as either "none", "rising", "falling", or
     * "both". Write these strings to select the signal edge(s)
     * that will make poll(2) on the "value" file return.
     *
     * This file exists only if the pin can be configured as an
    * interrupt generating input pin.
    */
    var edge: String
        get() {
            println("Getting edge")
            return pinGpio.getPseudoFile(MakeFileName(pin,  "/edge"))
        }
        private set(edge) {
            println("Setting edge")
            pinGpio.setPseudoFile (MakeFileName(pin,  "/edge"), edge)
        }

    fun pinEdgeNone() {
        edge = "none"
    }

    fun pinEdgeRising() {
        edge = "rising"
    }

    fun pinEdgeFalling() {
        edge = "falling"
    }

    fun pinEdgeBoth() {
        edge = "both"
    }

    /**
     * Get or Set pin active_low.
     * @param active_low Value of pin.
     * 0 -> Low Level.
     * 1 -> High Level
     *
     * 	"active_low" ... reads as either 0 (false) or 1 (true). Write
     * 	any nonzero value to invert the value attribute both
     * 	for reading and writing. Existing and subsequent
     * 	poll(2) support configuration via the edge attribute
     * 	for "rising" and "falling" edges will follow this
     * 	setting.
     */
    var active_low: Int
        get() {
            println("Getting active_low")
            return pinGpio.getPseudoFileInt(MakeFileName(pin, "/active_low"))
        }
        private set(active_low) {
            println("Setting active_low")
            pinGpio.setPseudoFile (MakeFileName(pin, "/active_low"), active_low)
        }


    fun pinActiveLow() {
        active_low = 1
    }

    fun pinActiveHigh() {
        active_low = 0
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

class GpioLed(pin : String) {
    val pin : String = pin

    private val gpioLed : GpioFile = GpioFile()

    /**
     * Get the current pin brightness setting or
     * Set the pin brightness. 0 is unlit and 1 is lit.
     */
    var brightness: Int
        get() {
            println("Getting Brightness")
            return gpioLed.getPseudoFileInt(pin + "/brightness")
        }
        private set(brightness) {
            println("Setting Brightness")
            gpioLed.setPseudoFile (pin + "/brightness", brightness)
        }

    fun pinHigh() {
        brightness = 1
    }

    /**
     * Set pin as low.
     */
    fun pinLow() {
        brightness = 0
    }

}

class GpioPwm(pin: Int) {
    val pin : Int = pin

    private val gpioPwm : GpioFile = GpioFile()

    /*   See https://www.kernel.org/doc/html/latest/driver-api/pwm.html
     *
     *  The PWM pins are represented by folders in the Linux file system
     *  within the folder /sys/class/pwm. Each pin is represented by a folder
     *  whose name is the prefix "pwmchip" followed by the pin number.
     *  Within the folder representing the pin are several files
     *
     * When a PWM channel is exported a pwmX directory will be created in the pwmchipN directory
     * it is associated with, where X is the number of the channel that was exported. The following
     * properties will then be available:
     *
     * period The total period of the PWM signal (read/write). Value is in nanoseconds and is the sum of the active
     *     and inactive time of the PWM.
     *
     * duty_cycle The active time of the PWM signal (read/write). Value is in nanoseconds and must be less than the period.
     *
     * polarity Changes the polarity of the PWM signal (read/write). Writes to this property only work if the PWM chip
     *     supports changing the polarity. The polarity can only be changed if the PWM is not enabled. Value is the
     *     string “normal” or “inversed”.
     *
     * enable Enable/disable the PWM signal (read/write).
     *     0 - disabled
     *     1 - enabled


     *  This function creates the path to the Linux file which represents a particular
     *  GPIO pin function, "value" or "direction".
     */
    private fun MakeFileName(pin: Int, op: String): String {
        return "/sys/class/pwm/pwmchip$pin$op"
    }

    /*
     * Get or set the current direction of a pin.
     * A pin may be either an Input pin or an Output pin.
     */
    var period: Int
        get() {
            println("Getting period")
            return gpioPwm.getPseudoFileInt(MakeFileName(pin,  "/period"))
        }
        private set(period) {
            println("Setting period")
            gpioPwm.setPseudoFile (MakeFileName(pin, "/period"), period)
        }

    /**
     * Get or Set pin value.
     * @param value Value of pin.
     * 0 -> Low Level.
     * 1 -> High Level
     */
    var duty_cycle: Int
        get() {
            println("Getting duty_cycle")
            return gpioPwm.getPseudoFileInt(MakeFileName(pin, "/duty_cycle"))
        }
        private set(duty_cycle) {
            println("Setting duty_cycle")
            gpioPwm.setPseudoFile (MakeFileName(pin, "/duty_cycle"), duty_cycle)
        }

    /**
     * Get or Set pin value.
     * @param value Value of pin.
     * Changes the polarity of the PWM signal (read/write). Writes to this property only
     * work if the PWM chip supports changing the polarity. The polarity can only
     * be changed if the PWM is not enabled. Value is the string “normal” or “inversed”.
     */
    var polarity: String
        get() {
            println("Getting polarity")
            return gpioPwm.getPseudoFile(MakeFileName(pin,  "/polarity"))
        }
        private set(polarity) {
            println("Setting polarity")
            gpioPwm.setPseudoFile (MakeFileName(pin, "/polarity"), polarity)
        }

    /**
     * Enable/disable the PWM signal (read/write)..
     * @param enable Enable state of pin.
     * 0 -> Disabled.
     * 1 -> Enabled.
     */
    var enable: Int
        get() {
            println("Getting enable")
            return gpioPwm.getPseudoFileInt(MakeFileName(pin, "/enable"))
        }
        private set(enable) {
            println("Setting enable")
            gpioPwm.setPseudoFile (MakeFileName(pin, "/enable"), enable)
        }

    /**
     * Set pin as Enabled.
     */
    fun pinEnabled() {
        enable = 1
    }

    /**
     * Set pin as Disabled.
     */
    fun pinDisabled() {
        enable = 0
    }

    /**
     * Set pin polarity.
     */
    fun pinPolarityNormal() {
        polarity = "normal"
    }

    /**
     * Set pin as input.
     * @param pin - Desirable pin.
     */
    fun pinPolarityInverted() {
        polarity = "inversed"
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
        private const val PATH = "/sys/class/pwm"
    }
}
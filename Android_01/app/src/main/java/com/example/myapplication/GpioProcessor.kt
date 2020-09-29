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
 * See as well https://www.coursera.org/learn/internet-of-things-sensing-actuation/lecture/cKLMW/modify-boot-script
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
 */

/*
 This class abstracts the use of the gpio pins. This class can be utilized on any linux operating
 system that has gpio pins defined in the /sys/class/gpio directory. It is required that the gpio
 pins themselves are available for access by the user of this application, and may require a
 change of permissions.
 */
class GpioProcessor {
    private val PATH = "/sys/class/gpio"
    private val pins: MutableList<Int> = ArrayList()

    // mapping of physical pin number of 40 pin low power connector
    // to pin functionality.
    //
    // orient board looking down with 40 pin low power connector on right and the
    // USB connectors to the left. The pins in the Pin Right column are nearest
    // to the right hand side of the board.
    //     Pin Left      |     Pin Right
    //  GND          1   |  GND           2
    //  UART0 CTS    3   |  Reserved      4
    //  UART0 TX     5   |  Reserved      6
    //  UART0 RX     7   |  SPI0 CLK      8
    //  UART0 RTS    9   |  SPI0 MISO    10
    //  UART1 TX    11   |  SPI0 CS N    12
    //  UART1 RX    13   |  SPI0 MOSI    14
    //  I2C0 SCL    15   |  Reserved     16
    //  I2C0 SDA    17   |  Reserved     18
    //  I2C1 SCL    19   |  Reserved     20
    //  I2C1 SDA    21   |  Reserved     22
    //  GPIO        23   |  GPIO         24
    //  GPIO        25   |  GPIO         26
    //  GPIO        27   |  Reserved*    28     * Reserved pin 28 to MPP-4
    //  GPIO *      29   |  GPIO         30     * pin 29 is Input only
    //  GPIO        31   |  GPIO         32
    //  GPIO        33   |  GPIO         34
    //  1.8V PWR    35   |  SYS DC IN    36
    //  5V PWR      37   |  SYS DC IN    38
    //  GND         39   |  GND          40
    //
    //  GPIO 21 (User LED 1) and GPIO 120 (User LED 2) control onboard LEDs.
    //  PM_GPIO_1 (User LED 3) and PM_GPIO_2 (User LED 4) control onboard LEDs.


    //----------------------------------------------------------
    // mapping of physical pin number to GPIO number for Android
    private val  androidPin23 = 938
    private val  androidPin24 = 914
    private val  androidPin25 = 915
    private val  androidPin26 = 971
    private val  androidPin27 = 1017
    private val  androidPin28 = 901   // GPIO pin borrowed from MPP-4. supports PWM. support analog I/O.
    private val  androidPin29 = 926   // (input only)
    private val  androidPin30 = 927   // Android reserves pin 30 for DSI display.
    private val  androidPin31 = 937
    private val  androidPin32 = 936
    private val  androidPin33 = 930
    private val  androidPin34 = 935

    //----------------------------------------------------------
    // mapping of physical pin number to GPIO number for Linux
    private val  linuxPin23 = 36
    private val  linuxPin24 = 12
    private val  linuxPin25 = 13
    private val  linuxPin26 = 69
    private val  linuxPin27 = 115
    private val  linuxPin28 = 4     // GPIO pin borrowed from MPP. supports PWM. support analog I/O.
    private val  linuxPin29 = 24    // (input only)
    private val  linuxPin30 = 25
    private val  linuxPin31 = 35
    private val  linuxPin32 = 34
    private val  linuxPin33 = 28
    private val  linuxPin34 = 33

    //----------------------------------------------------------
    // mapping of physical pin number to specified operating system GPIO number
    private val  physicalPin23 = androidPin23
    private val  physicalPin24 = androidPin24
    private val  physicalPin25 = androidPin25
    private val  physicalPin26 = androidPin26
    private val  physicalPin27 = androidPin27
    private val  physicalPin28 = androidPin28    // GPIO pin borrowed from MPP. supports PWM. support analog I/O.
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
        println("Exporting Pin Int")
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

class GpioProcessorLed {

    // Built in LEDs on board.  Turning them off using following python code.
    // This is for Android.
    // os.system("sudo echo none | sudo tee /sys/class/leds/apq8016-sbc:blue:bt/trigger")
    // os.system("sudo echo none | sudo tee /sys/class/leds/apq8016-sbc:green:user1/trigger")
    // os.system("sudo echo none | sudo tee /sys/class/leds/apq8016-sbc:green:user2/trigger")
    // os.system("sudo echo none | sudo tee /sys/class/leds/apq8016-sbc:green:user3/trigger")
    // os.system("sudo echo none | sudo tee /sys/class/leds/apq8016-sbc:green:user4/trigger")
    // os.system("sudo echo none | sudo tee /sys/class/leds/apq8016-sbc:yellow:wlan/trigger")

    // From https://www.96boards.org/documentation/consumer/dragonboard/dragonboard410c/guides/led-connectors.md.html
    //    Linux built in board LED assignments
    // +----------------------+----------------------+--------------------------+
    // | LED Board Identifier | Description          | Behavior                 |
    // +----------------------+----------------------+--------------------------+
    // | User LED 1           | Heartbeat            | Green: This LED is       |
    // |                      |                      | should always be         |
    // |                      |                      | blinking about once a    |
    // |                      |                      | second. If solid off or  |
    // |                      |                      | solid on, the board is   |
    // |                      |                      | not executing correctly  |
    // +----------------------+----------------------+--------------------------+
    // | User LED 2           | eMMC                 | Green: This LED blinks   |
    // |                      |                      | during accesses to eMMC  |
    // +----------------------+----------------------+--------------------------+
    // | User LED 3           | SD                   | Green: This LED blinks   |
    // |                      |                      | during accesses to SD    |
    // |                      |                      | Card                     |
    // +----------------------+----------------------+--------------------------+
    // | User LED 4           | currently unassigned | N/A                      |
    // +----------------------+----------------------+--------------------------+
    // | Wifi                 | Wifi                 | Yellow: This LED blinks  |
    // |                      |                      | during network accesses  |
    // |                      |                      | over Wifi                |
    // +----------------------+----------------------+--------------------------+
    // | BT                   | Bluetooth            | Yellow: This LED blinks  |
    // |                      |                      | when Bluetooth is being  |
    // |                      |                      | used                     |
    // +----------------------+----------------------+--------------------------+
    //
    //  Android built in board LED assignments
    // +----------------------+----------------------+--------------------------+
    // | LED Board Identifier | Description          | Behavior                 |
    // +----------------------+----------------------+--------------------------+
    // | User LED 1           | currently unassigned | Green:                   |
    // +----------------------+----------------------+--------------------------+
    // | User LED 2           | currently unassigned | Green:                   |
    // |                      |                      |                          |
    // +----------------------+----------------------+--------------------------+
    // | User LED 3           | currently unassigned | Green:                   |
    // |                      |                      |                          |
    // |                      |                      |                          |
    // +----------------------+----------------------+--------------------------+
    // | User LED 4           | Boot                 | This LED illuminates at  |
    // |                      |                      | at the start of boot     |
    // |                      |                      | and turns of after       |
    // |                      |                      | completion of boot.      |
    // +----------------------+----------------------+--------------------------+
    // | Wifi                 | Wifi                 | Yellow: TDB              |
    // +----------------------+----------------------+--------------------------+
    // | BT                   | Bluetooth            | Yellow: TBD              |
    // +----------------------+----------------------+--------------------------+

    private val  androidUserLed1 = "/sys/class/leds/led1"
    private val  androidUserLed2 = "/sys/class/leds/led2"
    private val  androidUserLed3 = "/sys/class/leds/led3"
    private val  androidUserLed4 = ""     // not available. used by Android for boot indicator

    private val  linuxUserLed1 = ""   // not available. used by Linux for heartbeat indicator
    private val  linuxUserLed2 = ""   // not available. used by Linux for eMMC indicator
    private val  linuxUserLed3 = ""   // not available. used by Linux for SD card indicator
    private val  linuxUserLed4 = "/sys/class/leds/apq8016-sbc:green:user4/brightness"   // not available. used by Android for boot indicator

    private val  userLED1 = androidUserLed1    // on board LED between the USB connectors
    private val  userLED2 = androidUserLed2    // on board LED between the USB connectors
    private val  userLED3 = androidUserLed3    // on board LED between the USB connectors
    private val  userLED4 = androidUserLed4    // on board LED between the USB connectors

    val pinLed1: GpioLed
        get() = GpioLed(userLED1)

    val pinLed2: GpioLed
        get() = GpioLed(userLED2)

    val pinLed3: GpioLed
        get() = GpioLed(userLED3)

    val pinLed4: GpioLed
        get() = GpioLed(userLED4)
}

class GpioProcessorPwm {

    //----------------------------------------------------------
    // mapping of physical pin number to specified operating system GPIO number
    private val  physicalPin23 = 1

    /**
     * Get function of specific pin.
     * @param pin Desirable pin.
     */
    fun getPin(pin: Int): GpioPwm {
        return GpioPwm(pin)
    }

    /**
     * Get pin 23;
     * @returns {Gpio}
     */
    val pin23: GpioPwm
        get() = getPin(physicalPin23)
}
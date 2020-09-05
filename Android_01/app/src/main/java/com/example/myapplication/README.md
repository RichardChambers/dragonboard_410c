Source code for the sample test application that runs under Android on a DragonBoard 410C.

See as well this document from Qualcomm developer network, [Peripherals Programming Guide, Linux Android](https://developer.qualcomm.com/download/db410c/peripherals-programming-guide-linux-android.pdf)


The GPIO library for accessing and manipulating the GPIO pins on the 40 pin low power connector is contained in two files: Gpio.kt and GpioProcessor.kt.
 - Gpio.kt contains low level primitives which directly work with the special files representing the GPIO pins
 - GpioProcessor contains higher level primitives mapping the physical pin numbers to GPIO pins and special files
 
 An example of using the library:
 
        val gpioProcessor =  GpioProcessor()

        // Get reference of GPIO23.
        val gpioPin23 = gpioProcessor.pin23
        
        gpioPin23.exportPin()   // export the pin so that we can use it.

        gpioPin23.pinOut()     // Set GPIO23 as output and initialize its state to off.
        gpioPin23.pinLow()     // drive pin low to initially turn off LED.

        for (i in 0 until 10) {
            gpioPin23.pinHigh()    // drive pin high to turn on LED
            Thread.sleep(300)
            gpioPin23.pinLow()    // drive pin low to turn off LED.
            Thread.sleep(300)
        }

        gpioPin23.unexportPin()   // unexport the pin as we are done with it.
        
This application is made up of a single Activity made up of two Fragments which have different functionality:
 - FirstFragment contains work to use the DragonBoard 410C onboard GPS with the Android platform
 - SecondFragment contains work to use the DragonBoard 410C GPIO pins of the 40 pin low power connector

Each of the Fragments is made up of a layout file such as FirstFragment.xml which describes the window components used in the
displayed window of the Fragment and a Kotlin source code file such as FirstFrament.kt which describes the behavior of the
window components.

Most of the Fragment Kotlin source files are the button listeners which wait for a button press and then perform some action.


**A note about the GPS**

As part of researching about the GPS of the DragonBoard 410C, I came across a developer forum posting by someone who was
having a problem with getting the GPS to provide proper coordinates. One answer was by a person who said that the onboard
antenna of the GPS is poor and reception suffers especially when indoors. However there is a document describing how to
add an active GPS antenna to the board.

See [Adding U.FL Antenna Connectors to DragonBoard™ 410c and Validating GPS on Android and Linux](https://developer.qualcomm.com/qfile/29467/lm80-p0436-42_c_add_ufl_ant_validate_gps_on_android_linux.pdf).

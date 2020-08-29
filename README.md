# dragonboard_410c
Experiments with a DragonBoard 410C from Arrow Electronics with the Qualcomm SnapDragon SoC and a form factor similar to Raspberry Pi 3. Supports Android 5.1, Linux, Windows 10 IoT Core.

**Resources**

There is an introductory online course in Coursera, [Internet of Things: Setting Up Your DragonBoard Development Platform](www.coursera.org/learn/internet-of-things-dragonboard/) which I am finding to be helpful.

Arrow Electronics has documentation and links to resources available on the [DragonBoard product description and purchase web page](www.arrow.com/en/products/dragonboard410c/arrow-development-tools).

96Boards has reference information as well as several operating system alternatives (Android and Linux) available on the [96Boards DragonBoard 410C product description page](www.96boards.org/product/dragonboard410c/).

Qualcomm has information and resources through the [DragonBoard 410C Development Board section of their developer portal](developer.qualcomm.com/hardware/dragonboard-410c).

Google Android Studio is avalable through their developer portal which also has a [link to resources for learning Kotlin](developer.android.com/kotlin).

**Background and initial unboxing**

The DragonBoard 410C is a small credit card sized computer similar in size and form to the Raspberry Pi 3. It has WiFi and Bluetooth built in and a low speed 40 pin connector similar to that of the Raspberry Pi 3 (the pin voltage is 1.8v). It has a micro-USB connector for connection to a host computer and a barrel power supply connector in addition to the full size HDMI connector and two USB 2.0 connectors for peripherals such as keyboard and mouse (the Pi 3 has four USB connectors). There is a GPS receiver built into the board.

The device has 1GB of RAM and a 8 GB eMMC persistent storage and a microSD 3.0 slot as well. Out of the box, the DragonBoard 410C had a generic Android 5.1 installed into the eMMC. Other operating systems can be installed and I plan to go with Windows 10 IoT Core later.

The Android 5.1 build does not have the Google ecosystem which means no Google applications such as the Play Store. However there are a [number of alternative Android app stores](theappsolutions.com/blog/marketing/alternative-android-app-stores/). For application development and testing, I will be sideloading apps anyway.

My initial DragonBoard setup was using an HDMI monitor along with a USB keyboard and USB mouse to see what the device looked like. The user interface is similar to an Android phone with the mouse rather than a finger being used to select with a click and to swipe doing a click/drag. I used the Settings app to connect the device to my cable router allowing access to the internet. The FireFox browser was already installed allowing me to read the Washington Post. There was a bit of delay at times when the browser displayed new content.

Next I replaced the initial setup with a development testing setup using an ASUS multi-touch touchscreen HDMI monitor that I had purchased on sale about a year ago to replicate a touchscreen phone interface. The touchscreen monitor HDMI cable went into the HDMI connector on the DragonBoard 410C and the USB cable went into one of the two USB ports on the board. The keyboard I removed, relying on the Android screen keyboard instead.

Using the touchscreen monitor, I was able to use swipes and touches to navigate and to start up applications. The onscreen keyboard was available as well with the keyboard removed. This was the kind of setup I wanted for using the DragonBoard, something similar to a phone interface.

**Setting up Windows 10 development Environment**

I am using a laptop with Windows 10 Pro installed for my development environment. The laptop has a copy of Android Studio installed. My development environment is as follows:

 - Dell laptop with Windows 10 Pro
 - Android Studio installed (Android Studio requires Java JDK)
 - Git installed
 - ASUS touchscreen monitor with HDMI and the touch USB connections to the DragonBoard 410C
 - DragonBoard 410C with a power supply (I purchased both from Arrow Electronics)
 - phone charging cable with USB A on one end and micro USB connector to connect DragonBoard to PC
 
I have seen a number of references to the Android Debug Bridge (ADB) software. It appears to be something that is installed separately from Android Studio. I have not yet installed it as I'm still learning Android Studio and am unsure as to what ADB provides. See the ADB section below where I provide links to some articles about ADB.

As a test of the development environment, I created a simple test application with Android Studio.  The test application was generated from what looked to be a simple template offered by Android Studio for a new project in the Create New Project dialog, the Basic Activity template. The generated project was a combination of Java and Kotlin source code along with a number of .xml files containing the code for the windows and user interface. I was able to switch between Design presentation and Code presentation of the user interface layout in Android Studio.

I connected the PC to the DragonBoard using the phone charging cable. Windows 10 saw the new Android device and set up the necessary drivers for me so that the device was available to Android Studio. I then downloaded the test application to the DragonBoard where it appeared in the apps folder.

I was able to start the app up and manpulate the simple controls using the touchscreen monitor with a feel  much like an Android smartphone.

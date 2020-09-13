# dragonboard_410c
Experiments with a DragonBoard 410C from Arrow Electronics with the Qualcomm SnapDragon SoC and a form factor similar to Raspberry Pi 3. Supports Android 5.1, Linux, Windows 10 IoT Core.

**Resources**

There is an introductory online course in Coursera, [Internet of Things: Setting Up Your DragonBoard Development Platform](https://www.coursera.org/learn/internet-of-things-dragonboard/) which I am finding to be helpful.

Arrow Electronics has documentation and links to resources available on the [DragonBoard product description and purchase web page](https://www.arrow.com/en/products/dragonboard410c/arrow-development-tools).

96Boards has reference information as well as several operating system alternatives (Android and Linux) available on the [96Boards DragonBoard 410C product description page](https://www.96boards.org/product/dragonboard410c/).

Qualcomm has information and resources through the [DragonBoard 410C Development Board section of their developer portal](https://developer.qualcomm.com/hardware/dragonboard-410c).

Google Android Studio is avalable through their developer portal which also has a [link to resources for learning Kotlin](https://developer.android.com/kotlin).

An article that provides an explanation of the Linux pseudo file system of which sysfs and the /sys/class/gpio legacy implementation of the GPIO device interface are a part. [Applying C - The Pseudo File System](https://www.i-programmer.info/programming/cc/12949-applying-c-the-pseudo-file-system.html).

.
**Background and initial unboxing**

The DragonBoard 410C is a small credit card sized computer similar in size and form to the Raspberry Pi 3. It has WiFi and Bluetooth built in and a low speed 40 pin connector similar to that of the Raspberry Pi 3 (the pin voltage is 1.8v). It has a micro-USB connector for connection to a host computer and a barrel power supply connector in addition to the full size HDMI connector and two USB 2.0 connectors for peripherals such as keyboard and mouse (the Pi 3 has four USB connectors). There is a GPS receiver built into the board.

The device has 1GB of RAM and a 8 GB eMMC persistent storage and a microSD 3.0 slot as well. Out of the box, the DragonBoard 410C had a generic Android 5.1 installed into the eMMC. Other operating systems can be installed and I plan to go with Windows 10 IoT Core later.

The Android 5.1 build does not have the Google ecosystem which means no Google applications such as the Play Store. However there are a [number of alternative Android app stores](https://theappsolutions.com/blog/marketing/alternative-android-app-stores/). For application development and testing, I will be sideloading apps anyway.

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
 
 **SDK Platform Tools and adb**
 
Documentation talks about installing software in addition to the Android Studio. The most important component is the adb application. This is part of the platform tools package which is a separate install from Android Studio.

There are a number of articles and guides on the web. Most of what I found was out of date. Here is my experience as of August 2020.

See this article from the developer.android.com about ADB. Android Debug Bridge (adb). In the article there is a link to the platform tools.

> adb is included in the Android SDK Platform-Tools package. You can download this package with the SDK Manager, which installs it at android_sdk/platform-tools/. 
Or if you want the standalone Android SDK Platform-Tools package, you can download it here.

From the SDK Platform Tools release notes article there are operating specific links to download the SDK Platform tools. Since I am using Windows 10 for development, I chose that link which resulted in a .zip file being downloaded. Inside the zip file was a directory platform-tools. I copied that directory and the directory tree from the .zip file to the folder C:\Program Files\Android which is the folder where Android Studio was already installed.

Also see this article, [What is ADB? How to Install ADB, Common Uses, and Advanced Tutorials](https://www.xda-developers.com/what-is-adb/).

The adb application requires a USB cable connecting the development computer to the micro USB B connector on the DragonBoard. I used the Windows 10 command shell to navigate to the folder where adb was located and then used the command line.

When setting up my DragonBoard GPIO pins I used adb to pull a copy of /etc/init.qcom.post_boot.sh to my PC, modified it using the Notepad application then pushed the modified version back. The shell script modifications are to create the sysfs GPIO pin special device files with the proper access permissions.

I've also used the command adb shell to open up a remote shell into the DragonBoard where the standard Linux commands of ls and cd can be used to look at the file system. I've used the adb reboot command to reboot the DragonBoard.

***Changes to Boot Initialization Script for sysfs GPIO***

The boot initialization script /etc/init.qcom.post_boot.sh needs to be modified as follows in order to provide access to the GPIO pins on the 40 pin low power connector.
 - use the adb pull command to pull a copy of the file to my PC
 - modify it using the Notepad application by adding the following lines
 - use the adb push command to push the modified version back to the device

The following script segment should be added to the bottom of the file:

    set -A pins 938 915 1017 926 937 930 914 971 901 936 935
    for i in 0 1 2 3 4 5 6 7 8 9 10
    do
        echo ${pins[i]} > /sys/class/gpio/export;
        chmod 777 /sys/class/gpio/gpio${pins[i]};
        chmod 777 /sys/class/gpio/gpio${pins[i]}/value;
        chmod 777 /sys/class/gpio/gpio${pins[i]}/direction;
    done



**Test of the Development Environment**

As a test of the development environment, I created a simple test application with Android Studio.  The test application was generated from what looked to be a simple template offered by Android Studio for a new project in the Create New Project dialog, the Basic Activity template. The generated project was a combination of Java and Kotlin source code along with a number of .xml files containing the code for the windows and user interface. I was able to switch between Design presentation and Code presentation of the user interface layout in Android Studio.

I connected the PC to the DragonBoard using the phone charging cable. Windows 10 saw the new Android device and set up the necessary drivers for me so that the device was available to Android Studio. I then downloaded the test application to the DragonBoard where it appeared in the apps folder.

I was able to start the app up and manpulate the simple controls using the touchscreen monitor with a feel  much like an Android smartphone.

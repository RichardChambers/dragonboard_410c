DragonBoard 410C Android 5.1 app with Android Studio.

This is a simple example first app to try a few things with the DragonBoard 410C and Android 5.1.

Setting up the GPIO pins

I am using the sysfs legacy GPIO manipulation procedures. These perform GPIO pin manipulation through a set of special device files in the
file system in the folder /sys/class/gpio.

In order to be able to access these special files I had to use the adb application in the SDK Platform Tools package to do the following:
 - pull a copy of the startup shell script /etc/init.qcom.post_boot.sh
 - modify the file by adding additional startup actions at the end of the file
 - push a copy of the modified shell script back to the DragonBoard
 - reboot the board
 
 The shell script change to file /etc/init.qcom.post_boot.sh was to add the following lines of code:
 
     set -A pins 938 915 1017 926 937 930 914 971 901 936 935
     for i in 0 1 2 3 4 5 6 7 8 9 10
     do
         echo ${pins[i]} > /sys/class/gpio/export;
         chmod 777 /sys/class/gpio/gpio${pins[i]};
         chmod 777 /sys/class/gpio/gpio${pins[i]}/value;
         chmod 777 /sys/class/gpio/gpio${pins[i]}/direction;
     done
 
 The commands used were:
  - adb pull /etc/init.qcom.post_boot.sh C:\users\rchamber\AndroidStudioProjects
  - adb push C:\users\rchamber\AndroidStudioProjects\init.qcom.post_boot.sh /etc/init.qcom.post_boot.sh
  - adb reboot
  
After rebooting the DragonBoard 410C, I then used adb shell to remote into the board and to navigate to the
/sys/class/gpio directory to check that the required special device files had been created.

I then tested my application on the DragonBoard with a single LED and a resistor on a bread board connected to GPIO pin 23 and
GND pin 1 on the 40 pin low power connector and saw the expected behavior of the LED turning on and off as I clicked the buttons
on the Second_Fragment window.

As part of my research into how to do this I submitted the following stackoverflow post which provides details as well
https://stackoverflow.com/questions/63653864/accessing-gpio-low-power-connector-on-dragonboard-410c-running-android/

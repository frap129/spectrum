# <img src="https://raw.githubusercontent.com/frap129/spectrum/master/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png" width="70" height="70" /> Spectrum
A simple, profile based kernel manager.

## How to add Spectrum support to your kernel
You will need to add the following two files to your device's ramdisk:
- init.spectrum.rc
- init.spectrum.sh

The files are included in the _ramdisk_ folder of this repo. To use these ramdisk files, add
>     import /init.spectrum.rc

to the top of your device's main ramdisk file.

Next, add your kernel name to the app. Open init.spectrum.rc and change "Electron" in
>        setprop persist.spectrum.kernel Electron

to your kernel's name.

All that is left is to customize the 4 profiles in init.spectrum.rc to your liking! Profile 0 (Balanced) is the default, however, this can be changed in init.spectrum.sh.

package org.frap129.spectrum;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import eu.chainfire.libsuperuser.Shell;

class Utils {

    private static String kpmSupport = "/proc/kpm_supported";

    public static String kpmPath = "/sys/module/profiles_manager/parameters/kpm_profile";

    private static String kpmDisabledProfilesPath = "/proc/kpm_disabled_profiles";

    private static String kpmDisabledProfiles = null;

    private static String kpmNotTuned = "/proc/kpm_not_tuned";

    public static String  kpmFinal = "/proc/kpm_final";

    public static String profileProp = "persist.spectrum.profile";

    public static String kernelProp = "persist.spectrum.kernel";

    public static String kpmPropPath = "/proc/kpm_name";

    public static Boolean KPM;

    public static String notTunedGov = listToString(Shell.SU.run(String.format("cat %s", kpmNotTuned)));

    public static String finalGov;

    public static String cpuScalingGovernorPath = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_governor";

    // Method to check if kernel supports
    public static boolean checkSupport(final Context context) {
        List<String> shResult;
        String supportProp = "spectrum.support";
        shResult = Shell.SH.run(String.format("getprop %s", supportProp));
        if(listToString(shResult).isEmpty()){
            shResult = Shell.SU.run(String.format("cat %s", kpmSupport));
            KPM = true;

            List<String> anyDisabledProfile;
            anyDisabledProfile = Shell.SU.run(String.format("cat %s", kpmDisabledProfilesPath));
            if(!listToString(anyDisabledProfile).isEmpty()){
                kpmDisabledProfiles = listToString(anyDisabledProfile);
            }

        } else {
            KPM = false;
        }
        String support = listToString(shResult);

        return !support.isEmpty();
    }

    // Method to check if the device is rooted
    public static boolean checkSU() {
        return Shell.SU.available();
    }

    // Method that converts List<String> to String
    public static String listToString(List<String> list) {
        StringBuilder Builder = new StringBuilder();
        for(String out : list){
            Builder.append(out);
        }
        return Builder.toString();
    }

    // Method that interprets a profile and sets it
    public static void setProfile(int profile) {
        int numProfiles = 3;
        if (profile > numProfiles || profile < 0) {
            setProp(0);
        } else {
            setProp(profile);
        }
    }

    // Method that sets system property
    private static void setProp(final int profile) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(KPM) {
                    Shell.SU.run(String.format("echo %s > %s", profile, kpmPath));
                } else {
                    Shell.SU.run(String.format("setprop %s %s", profileProp, profile));
                }
            }
        }).start();
    }

    public static String disabledProfiles(){
        String disabledProfilesProp = "spectrum.disabledprofiles";
        if(KPM && kpmDisabledProfiles != null){
            return kpmDisabledProfiles;
        }
        return listToString(Shell.SH.run(String.format("getprop %s", disabledProfilesProp)));
    }

    private static String readString(File file, String profileName) {
        String returnValue = null;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file), 512);
            returnValue = reader.readLine();
            while ( returnValue != null && !returnValue.contains(profileName)){
                returnValue = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) reader.close();
            } catch (IOException e) {
                // Ignored, not much we can do anyway
            }
        }
        return returnValue;
    }

    public static String getCustomDesc(String profileName) {
        File customDescFile = new File(Environment.getExternalStorageDirectory() + File.separator +".spectrum_descriptions");
        String retVal = readString(customDescFile, profileName);
        if (retVal != null) {
            return retVal.split(":")[1];
        } else {
            return "fail";
        }
    }

    public static boolean supportsCustomDesc(){
        return new File(Environment.getExternalStorageDirectory() + File.separator +".spectrum_descriptions").exists();
    }
}

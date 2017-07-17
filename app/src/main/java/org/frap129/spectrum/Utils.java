package org.frap129.spectrum;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import eu.chainfire.libsuperuser.Shell;

class Utils {

    public static String profileProp = "persist.spectrum.profile";

    public static String kernelProp = "persist.spectrum.kernel";

    private static String supportProp = "spectrum.support";

    private static String disabledProfilesProp = "spectrum.disabledprofiles";

    // Method to check if kernel supports
    public static boolean checkSupport(Context context) {
        List<String> shResult;
        shResult = Shell.SH.run(String.format("getprop %s", supportProp));
        String support = listToString(shResult);

        if (!support.isEmpty())
            return true;
        else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(context, android.R.style.Theme_Material);
            dialog.setTitle("Spectrum not supported!");
            dialog.setMessage("Please contact your kernel dev and ask them to add Spectrum support.");
            dialog.setCancelable(false);
            AlertDialog supportDialog = dialog.create();
            supportDialog.show();
            shResult = Shell.SH.run(String.format("getprop %s", profileProp));
            String defProfile = listToString(shResult);
            if (!defProfile.isEmpty() && !defProfile.contains("0"))
                setProfile(0);
            return false;
        }
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
                Shell.SU.run(String.format("setprop %s %s", profileProp, profile));
            }
        }).start();
    }

    public static String disabledProfiles(){
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
        File customDescFile = new File(Environment.getExternalStorageDirectory() + "/.spectrum_descriptions");
        String retVal = readString(customDescFile, profileName);
        if (retVal != null) {
            return retVal.split(":")[1];
        } else {
            return "fail";
        }
    }
}

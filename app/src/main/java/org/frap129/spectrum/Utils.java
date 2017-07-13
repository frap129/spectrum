package org.frap129.spectrum;

import android.app.AlertDialog;
import android.content.Context;

import java.util.List;

import eu.chainfire.libsuperuser.Shell;

import static org.frap129.spectrum.Props.disabledProfilesProp;
import static org.frap129.spectrum.Props.profileProp;
import static org.frap129.spectrum.Props.supportProp;

class Utils {

    // Method to check if kernel supports
    static boolean checkSupport(Context context) {
        List<String> suResult;
        suResult = Shell.SU.run(String.format("getprop %s", supportProp));
        String support = listToString(suResult);

        if (!support.isEmpty())
            return true;
        else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(context, android.R.style.Theme_Material);
            dialog.setTitle("Spectrum not supported!");
            dialog.setMessage("Please contact your kernel dev and ask them to add Spectrum support.");
            dialog.setCancelable(false);
            AlertDialog supportDialog = dialog.create();
            supportDialog.show();
            suResult = Shell.SU.run(String.format("getprop %s", profileProp));
            String defProfile = listToString(suResult);
            if (!defProfile.isEmpty() && !defProfile.contains("0"))
                setProfile(0);
            return false;
        }
    }

    // Method that converts List<String> to String
    static String listToString(List<String> list) {
        StringBuilder Builder = new StringBuilder();
        for(String out : list){
            Builder.append(out);
        }
        return Builder.toString();
    }

    // Method that interprets a profile and sets it
    static void setProfile(int profile) {
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

    static String disabledProfiles(){
        return listToString(Shell.SH.run(String.format("getprop %s", disabledProfilesProp)));
    }
}

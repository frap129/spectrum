package org.frap129.spectrum;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import eu.chainfire.libsuperuser.Shell;

public class BootReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            SharedPreferences boot = context.getSharedPreferences("loadOnBoot", Context.MODE_PRIVATE);
            if (boot.getBoolean("loadOnBoot", true)) {
                SharedPreferences path = context.getSharedPreferences("profilePath", Context.MODE_PRIVATE);
                String profile = path.getString("profilePath", null);
                String curProfile = MainActivity.listToString(Shell.SU.run("getprop persist.spectrum.profile"));
                if ((profile != null) && !(curProfile.contains("custom")))
                    ProfileLoaderActivity.setEXKMProfile(profile);
            }
        }
    }



}

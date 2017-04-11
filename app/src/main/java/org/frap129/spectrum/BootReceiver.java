package org.frap129.spectrum;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class BootReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            SharedPreferences boot = context.getSharedPreferences("loadOnBoot", Context.MODE_PRIVATE);
            if (boot.getBoolean("loadOnBoot", true)) {
                SharedPreferences path = context.getSharedPreferences("profilePath", Context.MODE_PRIVATE);
                SharedPreferences profile = context.getSharedPreferences("profile", Context.MODE_PRIVATE);
                String profilePath = path.getString("profilePath", null);
                String curProfile = profile.getString("profile", "0");
                if ((profilePath != null) && !(curProfile.contains("custom")))
                    ProfileLoaderActivity.setEXKMProfile(profilePath);
            }
        }
    }



}

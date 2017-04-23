package org.frap129.spectrum;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.service.quicksettings.TileService;

@TargetApi(Build.VERSION_CODES.N)
public class LaunchTile extends TileService {

    @Override
    public void onClick() {
        Intent collapseIntent = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        Intent intent = new Intent(this, MainActivity.class);
        sendBroadcast(collapseIntent);
        startActivity(intent);
    }
}

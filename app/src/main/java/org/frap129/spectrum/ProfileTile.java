package org.frap129.spectrum;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

@TargetApi(Build.VERSION_CODES.N)
public class ProfileTile extends TileService {

    private static final String SERVICE_STATUS_FLAG = "serviceStatus";
    private static final String PREFERENCES_KEY = "org.frap129.spectrum";
    private boolean click = false;

    @Override
    public void onStartListening() {
        resetTileStatus();
    }

    @Override
    public void onClick() {
        updateTile();
    }

    private void updateTile() {
        SharedPreferences profile = this.getSharedPreferences("profile", MODE_PRIVATE);
        SharedPreferences.Editor editor = profile.edit();
        Tile tile = this.getQsTile();
        boolean isActive = getServiceStatus();
        Icon newIcon;
        String newLabel;
        int newState;

        // Update tile and set profile
        if (isActive && click) {
            newLabel = "Gaming";
            newIcon = Icon.createWithResource(getApplicationContext(), R.drawable.game);
            newState = Tile.STATE_ACTIVE;
            click = false;
            MainActivity.setProfile(3);
            editor.putString("profile", "3");
            editor.apply();
        } else if (!isActive && click) {
            newLabel = "Battery";
            newIcon = Icon.createWithResource(getApplicationContext(), R.drawable.battery);
            newState = Tile.STATE_ACTIVE;
            click = true;
            MainActivity.setProfile(2);
            editor.putString("profile", "2");
            editor.apply();
        } else if (isActive && !click){
            newLabel = "Performance";
            newIcon = Icon.createWithResource(getApplicationContext(), R.drawable.rocket);
            newState = Tile.STATE_ACTIVE;
            click = true;
            MainActivity.setProfile(1);
            editor.putString("profile", "1");
            editor.apply();
        } else {
            newLabel = "Balance";
            newIcon = Icon.createWithResource(getApplicationContext(), R.drawable.atom);
            newState = Tile.STATE_ACTIVE;
            click = false;
            MainActivity.setProfile(0);
            editor.putString("profile", "0");
            editor.apply();
        }

        // Change the UI of the tile.
        tile.setLabel(newLabel);
        tile.setIcon(newIcon);
        tile.setState(newState);
        tile.updateTile();
    }

    private boolean getServiceStatus() {

        SharedPreferences prefs = getApplicationContext().getSharedPreferences(PREFERENCES_KEY, MODE_PRIVATE);
        boolean isActive = prefs.getBoolean(SERVICE_STATUS_FLAG, false);
        isActive = !isActive;

        prefs.edit().putBoolean(SERVICE_STATUS_FLAG, isActive).apply();

        return isActive;
    }

    private void resetTileStatus() {
        SharedPreferences profile = this.getSharedPreferences("profile", MODE_PRIVATE);
        Tile tile = this.getQsTile();
        Icon newIcon;
        String newLabel;
        int newState;

        // Update tile
        if (profile.getString("profile", "").contains("3")) {
            newLabel = "Gaming";
            newIcon = Icon.createWithResource(getApplicationContext(), R.drawable.game);
            newState = Tile.STATE_ACTIVE;
            click = false;
        } else if (profile.getString("profile", "").contains("2")) {
            newLabel = "Battery";
            newIcon = Icon.createWithResource(getApplicationContext(), R.drawable.battery);
            newState = Tile.STATE_ACTIVE;
            click = true;
        } else if (profile.getString("profile", "").contains("1")){
            newLabel = "Performance";
            newIcon = Icon.createWithResource(getApplicationContext(), R.drawable.rocket);
            newState = Tile.STATE_ACTIVE;
            click = true;
        } else if (profile.getString("profile", "").contains("0")) {
            newLabel = "Balance";
            newIcon = Icon.createWithResource(getApplicationContext(), R.drawable.atom);
            newState = Tile.STATE_ACTIVE;
            click = false;
        } else {
            newLabel = "Custom";
            newIcon = Icon.createWithResource(getApplicationContext(), R.drawable.ic_mono);
            newState = Tile.STATE_ACTIVE;
            click = false;
        }

        // Change the UI of the tile.
        tile.setLabel(newLabel);
        tile.setIcon(newIcon);
        tile.setState(newState);
        tile.updateTile();
    }
}
package org.frap129.spectrum;

import android.app.AlertDialog;
import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import eu.chainfire.libsuperuser.Shell;

public class MainActivity extends AppCompatActivity {

    private CardView oldCard;
    private List<String> suResult = null;
    private boolean specSupport = false;
    private boolean rooted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Define existing CardViews
        final CardView card0 = (CardView) findViewById(R.id.card0);
        final CardView card1 = (CardView) findViewById(R.id.card1);
        final CardView card2 = (CardView) findViewById(R.id.card2);
        final CardView card3 = (CardView) findViewById(R.id.card3);
        final ColorStateList ogColor = card0.getCardBackgroundColor();
        final int balColor = getColor(R.color.colorBalance);
        final int perColor = getColor(R.color.colorPerformance);
        final int batColor = getColor(R.color.colorBattery);
        final int gamColor = getColor(R.color.colorGaming);

        // Check for Spectrum Support
        checkSupport();

        if (!specSupport)
            return;

        // Ensure root access
        checkSU();

        if (!rooted)
            return;

        // Get profile descriptions
        //setDesc(); not working yet

        // Highlight current profile
        initSelected();

        // Set system property on click
        card0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (oldCard != card0) {
                    card0.setCardBackgroundColor(balColor);
                    if (oldCard != null)
                        oldCard.setCardBackgroundColor(ogColor);
                    setProfile(0);
                    oldCard = card0;
                }
            }
        });

        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (oldCard != card1) {
                    card1.setCardBackgroundColor(perColor);
                    if (oldCard != null)
                        oldCard.setCardBackgroundColor(ogColor);
                    setProfile(1);
                    oldCard = card1;
                }
            }
        });

        card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (oldCard != card2) {
                    card2.setCardBackgroundColor(batColor);
                    if (oldCard != null)
                        oldCard.setCardBackgroundColor(ogColor);
                    setProfile(2);
                    oldCard = card2;
                }
            }
        });

        card3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (oldCard != card3) {
                    card3.setCardBackgroundColor(gamColor);
                    if (oldCard != null)
                        oldCard.setCardBackgroundColor(ogColor);
                    setProfile(3);
                    oldCard = card3;
                }
            }
        });

    }

    // Method that interprets a profile and sets it
    private void setProfile(int profile) {
        int numProfiles = 3;
        if (profile > numProfiles || profile < 0) {
            setProp(0);
        } else {
            setProp(profile);
        }

    }

    // Method that sets system property
    private void setProp(final int profile) {
        new AsyncTask<Object, Object, Void>() {
            @Override
            protected Void doInBackground(Object... params) {
                suResult = Shell.SU.run(new String[] {
                        "setprop persist.spectrum.profile " + profile
                });
                return null;
            }
        }.execute();
    }

    // Method that detects the selected profile on launch
    private void initSelected() {
        CardView card0 = (CardView) findViewById(R.id.card0);
        CardView card1 = (CardView) findViewById(R.id.card1);
        CardView card2 = (CardView) findViewById(R.id.card2);
        CardView card3 = (CardView) findViewById(R.id.card3);
        int balColor = getColor(R.color.colorBalance);
        int perColor = getColor(R.color.colorPerformance);
        int batColor = getColor(R.color.colorBattery);
        int gamColor = getColor(R.color.colorGaming);


        suResult = Shell.SU.run(new String[] {
                "getprop persist.spectrum.profile"
        });

        if (suResult != null) {
            StringBuilder resultBuilder = new StringBuilder();

            for(String out : suResult){
                resultBuilder.append(out);
            }

            String result = resultBuilder.toString();

            if (result.contains("0")) {
                card0.setCardBackgroundColor(balColor);
                oldCard = card0;
            } else if (result.contains("1")) {
                card1.setCardBackgroundColor(perColor);
                oldCard = card1;
            } else if (result.contains("2")) {
                card2.setCardBackgroundColor(batColor);
                oldCard = card2;
            } else if (result.contains("3")) {
                card3.setCardBackgroundColor(gamColor);
                oldCard = card3;
            }
        }
    }

    // Create a dialog to stop unrooted users
    protected void checkSU() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Spectrum");
        dialog.setMessage("Checking for root...");
        dialog.setCancelable(false);
        AlertDialog root = dialog.create();
        root.show();

        rooted = Shell.SU.available();

        if (rooted)
            root.dismiss();
        else
            root.setTitle("Root access not available");
            root.setMessage("Please root your device and/or grant root access to Spectrum.");
    }

    // Method that reads and sets profile descriptions
    private void getDesc() {
        TextView desc0 = (TextView) findViewById(R.id.desc0);
        TextView desc1 = (TextView) findViewById(R.id.desc1);
        TextView desc2 = (TextView) findViewById(R.id.desc2);
        TextView desc3 = (TextView) findViewById(R.id.desc3);
        String balDesc;
        String perDesc;
        String batDesc;
        String gamDesc;

        suResult = Shell.SU.run(new String[] {
                "getprop persist.spectrum.desc0"
        });
        StringBuilder balBuilder = new StringBuilder();
        for(String out : suResult){
             balBuilder.append(out);
        }
        balDesc = balBuilder.toString();
        desc0.setText(balDesc);

        suResult = Shell.SU.run(new String[] {
                "getprop persist.spectrum.desc1"
        });
        StringBuilder perBuilder = new StringBuilder();
        for(String out : suResult){
            perBuilder.append(out);
        }
        perDesc = perBuilder.toString();
        desc1.setText(perDesc);

        suResult = Shell.SU.run(new String[] {
                "getprop persist.spectrum.desc2"
        });
        StringBuilder batBuilder = new StringBuilder();
        for(String out : suResult){
            batBuilder.append(out);
        }
        batDesc = batBuilder.toString();
        desc2.setText(batDesc);

        suResult = Shell.SU.run(new String[] {
                "getprop persist.spectrum.desc3"
        });
        StringBuilder gamBuilder = new StringBuilder();
        for(String out : suResult){
            gamBuilder.append(out);
        }
        gamDesc = gamBuilder.toString();
        desc3.setText(gamDesc);
    }

    private void setDesc() {
        new AsyncTask<Object, Object, Void>() {
            @Override
            protected Void doInBackground(Object... params) {
                getDesc();
                return null;
            }
        }.execute();
    }

    // Method to check if kernel supports
    private void checkSupport() {
        suResult = Shell.SU.run(new String[] {
                "getprop persist.spectrum.profile"
        });
        StringBuilder specBuilder = new StringBuilder();
        for(String out : suResult){
            specBuilder.append(out);
        }
        String supportString = specBuilder.toString();

        if (!supportString.isEmpty())
            specSupport = true;
        else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("Spectrum not supported!");
            dialog.setMessage("Please contact your kernel dev and ask them to add Spectrum support.");
            dialog.setCancelable(false);
            AlertDialog support = dialog.create();
            support.show();
        }
    }
}

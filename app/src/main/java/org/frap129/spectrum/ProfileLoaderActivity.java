package org.frap129.spectrum;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import eu.chainfire.libsuperuser.Shell;

import static java.lang.System.in;

public class ProfileLoaderActivity extends AppCompatActivity{
    private static final int SELECT_FILE = 1;
    static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_loader);

        CardView fileSelect = (CardView) findViewById(R.id.profCard);
        final Switch applyOnBoot = (Switch) findViewById(R.id.boot);
        SharedPreferences first = this.getSharedPreferences("firstFind", Context.MODE_PRIVATE);
        SharedPreferences.Editor feditor = first.edit();
        SharedPreferences boot = getApplication().getSharedPreferences("loadOnBoot", Context.MODE_PRIVATE);

        if (first.getBoolean("firstFind", true)) {
            aboutDialog();
            feditor.putBoolean("firstFind", false);
            feditor.apply();
        }

        applyOnBoot.setChecked(boot.getBoolean("loadOnBoot", false));

        try {
            if (ContextCompat.checkSelfPermission(ProfileLoaderActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(ProfileLoaderActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        fileSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                startActivityForResult(intent, SELECT_FILE);
            }
        });

        applyOnBoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getApplication().getSharedPreferences("loadOnBoot", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();

                if (applyOnBoot.isChecked()) {
                    editor.putBoolean("loadOnBoot", true);
                    editor.apply();
                } else {
                    editor.putBoolean("loadOnBoot", false);
                    editor.apply();
                }
            }
        });
    }

    // Method that parses profile file
    public static void setEXKMProfile(final String path) {
        new AsyncTask<Object, Object, Void>() {
            @Override
            protected Void doInBackground(Object... params) {
                try {
                    FileInputStream fstream = new FileInputStream(path);
                    BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

                    String strLine;
                    String exec = "write() { echo -n $2 > $1; };";

                    while ((strLine = br.readLine()) != null) {
                        exec = exec + " write " + strLine + ";";
                    }

                    Shell.SU.run(exec);

                    br.close();
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    // Method that creates intro dialog
    private void aboutDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.aboutLoader);
        dialog.setMessage(R.string.loaderDesc);
        dialog.setCancelable(true);
        AlertDialog supportDialog = dialog.create();
        supportDialog.show();
    }

    // Method that sets property as a string
    private void setProp(final String profile) {
        new AsyncTask<Object, Object, Void>() {
            @Override
            protected Void doInBackground(Object... params) {
                Shell.SU.run("setprop persist.spectrum.profile " + profile);
                return null;
            }
        }.execute();
    }

    // Method that prompts the user for confirmation
    protected void profileDialog(final String path){
        final Dialog pDialog = new Dialog(ProfileLoaderActivity.this);
        pDialog.setTitle("Profile Loader");
        pDialog.setContentView(R.layout.profile_dialog);
        Button pDialogCancel = (Button) pDialog.findViewById(R.id.pDialogCancel);
        pDialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog.dismiss();
            }
        });
        Button pDialogConfirm = (Button) pDialog.findViewById(R.id.pDialogConfirm);
        pDialogConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEXKMProfile(path);
                setProp("custom");
                SharedPreferences prefs = getApplication().getSharedPreferences("profilePath", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                SharedPreferences prof = getApplication().getSharedPreferences("profile", Context.MODE_PRIVATE);
                SharedPreferences.Editor peditor = prof.edit();
                editor.putString("profilePath", path);
                editor.apply();
                peditor.putString("profile", "custom");
                peditor.apply();
                pDialog.dismiss();
            }
        });
        pDialog.show();
    }

    // File path methods taken from aFileChooser, thanks to iPaulPro: https://github.com/iPaulPro/aFileChooser
    public static String getPath(final Context context, final Uri uri) {
        // DocumentProvider
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type))
                    return Environment.getExternalStorageDirectory() + "/" + split[1];

            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type))
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                else if ("video".equals(type))
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                else if ("audio".equals(type))
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme()))
            return getDataColumn(context, uri, null, null);
            // File
        else if ("file".equalsIgnoreCase(uri.getScheme()))
            return uri.getPath();

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length <= 0
                        && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(ProfileLoaderActivity.this, "Read permissions are required to run this app.", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(ProfileLoaderActivity.this, "Read permissions granted!", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    //Thanks: http://codetheory.in/android-pick-select-image-from-gallery-with-intents/
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                Uri selectedFileUri = data.getData();
                String selectedFilePath = getPath(this, selectedFileUri);
                if (selectedFilePath != null) {
                    profileDialog(selectedFilePath);
                } else {
                    Toast.makeText(ProfileLoaderActivity.this, "Invalid File Path.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

}

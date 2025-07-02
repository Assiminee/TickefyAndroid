package com.example.pjtfacerec;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

/**
 * Utility methods for permission handling, settings navigation, and view visibility.
 */
public class Utils {
    public static final int ORIENT_PORTRAIT = 0;
    public static final int ORIENT_LANDSCAPE_LEFT = 1;
    public static final int ORIENT_LANDSCAPE_RIGHT = 2;

    /**
     * Show a toast and open app settings to enable permissions.
     */
    public static void appSettingOpen(Context context) {
        Toast.makeText(
            context,
            "Go to Setting and Enable All Permission",
            Toast.LENGTH_LONG
        ).show();

        Intent settingIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        settingIntent.setData(Uri.parse("package:" + context.getPackageName()));
        context.startActivity(settingIntent);
    }

    /**
     * Show a non-cancelable dialog warning that all permissions are required.
     */
    public static void warningPermissionDialog(Context context, DialogInterface.OnClickListener listener) {
        new MaterialAlertDialogBuilder(context)
            .setMessage("All Permission are Required for this app")
            .setCancelable(false)
            .setPositiveButton("Ok", listener)
            .create()
            .show();
    }

    /**
     * Set a view's visibility to VISIBLE.
     */
    public static void visible(View view) {
        view.setVisibility(View.VISIBLE);
    }

    /**
     * Set a view's visibility to GONE.
     */
    public static void gone(View view) {
        view.setVisibility(View.GONE);
    }
}

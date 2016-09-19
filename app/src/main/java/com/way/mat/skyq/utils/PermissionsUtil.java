package com.way.mat.skyq.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.way.mat.skyq.config.Globals;

/**
 * Created by mpodolsky on 23.05.2016.
 */
public class PermissionsUtil {

    public static boolean needReadWritePermissions(Context context) {
        int permissionCheck1 = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionCheck2 = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return (permissionCheck1 != PackageManager.PERMISSION_GRANTED || permissionCheck2 != PackageManager.PERMISSION_GRANTED);
    }

    public static boolean needPermissions(Context context) {
        int permissionCheck1 = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionCheck2 = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO);
        int permissionCheck3 = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA);
        return (permissionCheck1 != PackageManager.PERMISSION_GRANTED || permissionCheck2 != PackageManager.PERMISSION_GRANTED || permissionCheck3 != PackageManager.PERMISSION_GRANTED);
    }

    public static void requestPermissions(Activity activity) {
        ActivityCompat.requestPermissions(activity,
                new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.CAMERA
                },
                Globals.REQUEST_PERMISSIONS);
    }

}

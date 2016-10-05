package com.way.mat.opentokchat.utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utils for managing local files and storage
 */
public class LocalFilesUtil {

    private static final String CAMERA_DIR = "/dcim";

    public static final String JPEG_FILE_PREFIX = "IMG_";
    public static final String JPEG_FILE_SUFFIX = ".jpg";

    public static final String MP4_FILE_PREFIX = "VID_";
    public static final String MP4_FILE_SUFFIX = ".mp4";

    public static final String FILE_PREFIX = "file:///";

    private static final String ALBUM_NAME = "/SkyTalk";
    private static final String FRAMES = "FRAMES";

    /**
     * Returns local dir file
     *
     * @return {@link File} - album file
     */
    private static File getAlbumStorageDir() {
        return new File(Environment.getExternalStorageDirectory()
                + CAMERA_DIR
                + ALBUM_NAME
        );
    }

    /**
     * Returns local dir file for downloading
     *
     * @return {@link File} - album file
     */
    private static File getDownloadStorageDir() {
        return new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                + ALBUM_NAME);
    }

    /**
     * Returns local dir file
     *
     * @return {@link File} - album file
     */
    public static File getAlbumDir() {
        File storageDir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            storageDir = getAlbumStorageDir();
            if (storageDir != null) {
                if (!storageDir.mkdirs()) {
                    if (!storageDir.exists()) {
                        Log.d("CameraHelper", "failed to create directory");
                        return null;
                    }
                }
            }
        } else {
            Log.v("CameraHelper", "External storage is not mounted READ/WRITE.");
        }
        return storageDir;
    }

    /**
     * Returns local dir file for downloading
     *
     * @return {@link File} - album file
     */
    public static File getDownloadDir() {
        File storageDir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            storageDir = getDownloadStorageDir();
            if (storageDir != null) {
                if (!storageDir.mkdirs()) {
                    if (!storageDir.exists()) {
                        Log.d("CameraHelper", "failed to create directory");
                        return null;
                    }
                }
            }
        } else {
            Log.v("CameraHelper", "External storage is not mounted READ/WRITE.");
        }
        return storageDir;
    }

    /**
     * Creates image file with timestamp in wochit album
     *
     * @return {@link File}
     * @throws IOException
     */
    public static File createAlbumImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File albumF = getAlbumDir();
        File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
        return imageF;
    }

    /**
     * Creates video file with timestamp in wochit album
     *
     * @return {@link File}
     * @throws IOException
     */
    public static File createAlbumVideoFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = MP4_FILE_PREFIX + timeStamp + "_";
        File albumF = getAlbumDir();
        File imageF = File.createTempFile(imageFileName, MP4_FILE_SUFFIX, albumF);
        return imageF;
    }

    /**
     * Creates downloading video file in storage
     *
     * @return {@link File}
     * @throws IOException
     */
    public static File createDownloadVideoFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = MP4_FILE_PREFIX + timeStamp + "_";
        File albumF = getDownloadDir();
        File imageF = File.createTempFile(imageFileName, MP4_FILE_SUFFIX, albumF);
        return imageF;
    }

    /**
     * Creates file for frames
     *
     * @return {@link File}
     * @throws IOException
     */
    public static File createFramesFile() throws IOException {
        // Create an image file name
        String imageFileName = JPEG_FILE_PREFIX + FRAMES + JPEG_FILE_SUFFIX;
        File albumF = getAlbumDir();
        File imageF = new File(albumF, imageFileName);
        if (imageF.exists()) {
            return imageF;
        } else {
            if (imageF.createNewFile()) {
                return imageF;
            } else {
                return null;
            }
        }
//        File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
//        return imageF;
    }

}

package com.way.mat.opentokchat.utils;

import com.way.mat.opentokchat.rest.client.RestClient;

/**
 * Created by matviy on 05.10.16.
 */
public class ImageUrlUtils {

    public static String getRealUrl(String url) {
        if (url.startsWith("http")) {
            return url;
        } else {
            return RestClient.SERVER_URL + url;
        }
    }

}

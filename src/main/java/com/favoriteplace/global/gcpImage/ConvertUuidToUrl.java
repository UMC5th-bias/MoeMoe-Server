package com.favoriteplace.global.gcpImage;

public class ConvertUuidToUrl {
    public static String convertUuidToUrl(String uuid){
        return "https://storage.googleapis.com/favorite_place/" + uuid;
    }
}

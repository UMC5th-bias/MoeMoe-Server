package com.favoriteplace.global.gcpImage;

public class ConvertUuidToUrl {
    public static String convertUuidToUrl(String uuid){
        if(uuid == null){return "";}
        return "https://storage.googleapis.com/favorite_place_storage/" + uuid;
    }
}

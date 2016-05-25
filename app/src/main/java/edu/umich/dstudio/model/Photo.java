package edu.umich.dstudio.model;

import java.util.Date;

/**
 * Created by neera_000 on 3/26/2016.
 */
public class Photo {
    public float latitude;
    public float longitude;
    public String base64Data;
    public String annotation;
    String createdTime;
    public photoType type;

    public enum photoType {
        GLUCOSE_READING, INSULIN_SHOT, FOOD, OTHER, SCREENSHOT
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public String getBase64Data() {
        return base64Data;
    }

    public String getAnnotation() {
        return annotation;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public Photo(float latitude, float longitude, String base64Data, String annotation, photoType type) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.base64Data = base64Data;
        this.annotation = annotation;
        this.createdTime = new Date().toString();
        this.type = type;
    }
}

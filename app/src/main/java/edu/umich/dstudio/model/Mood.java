package edu.umich.dstudio.model;

import java.util.Date;

/**
 * Created by neera_000 on 3/26/2016.
 */
public class Mood {
    /** X axis to draw */
    public float X = -50f;
    /** Y axis to draw */
    public float Y = -50f;
    /** Checking flag while drag the mood */
    public boolean isSelected = false;
    /** Checking flag while Inserting and Posting the mood */
    public boolean isCreated = false;
    /** X Possition on 10/10 graph */
    public float moodLevel = -50;
    /** Y Possition on 10/10 graph */
    public float energyLevel = -50;

    String createdTime;
    public float latitude;
    public float longitude;

    String moodAnnotation;

    public float getX() {
        return X;
    }

    public float getY() {
        return Y;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public boolean isCreated() {
        return isCreated;
    }

    public float getMoodLevel() {
        return moodLevel;
    }

    public float getEnergyLevel() {
        return energyLevel;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public String getMoodAnnotation() {
        return moodAnnotation;
    }

    public Mood() {

    }

    public Mood(float x, float y, boolean isSelected, boolean isCreated, float moodLevel, float energyLevel, float latitude, float longitude, String moodAnnotation) {

        X = x;
        Y = y;
        this.isSelected = isSelected;
        this.isCreated = isCreated;
        this.moodLevel = moodLevel;
        this.energyLevel = energyLevel;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdTime = new Date().toString();
        this.moodAnnotation = moodAnnotation;
    }

    /**todo: a) add a simple annotation after selecting mood, OR
    b)compare with last saved mood and ask why did your mood change this way?, OR
    c)if it is a negative mood that is sellected, ask what happened?**/
}

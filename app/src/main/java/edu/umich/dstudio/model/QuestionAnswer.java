package edu.umich.dstudio.model;

import android.widget.TextView;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by shriti on 4/1/16.
 */
public class QuestionAnswer {
    public String question;
    public String answer;
    public float latitude;
    public float longitude;
    String createdTime;

    public QuestionAnswer() {

    }

    public QuestionAnswer(String question, String answer, float latitude, float longitude) {
        this.question = question;
        this.answer = answer;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdTime = new Date().toString();
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() { return answer; }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() { return longitude; }

    public String getCreatedTime() {
        return createdTime;
    }

}

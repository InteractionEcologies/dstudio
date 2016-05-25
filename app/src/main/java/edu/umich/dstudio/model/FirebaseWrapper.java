package edu.umich.dstudio.model;

import android.location.Location;

import com.firebase.client.Firebase;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import edu.umich.dstudio.utils.Constants;
import edu.umich.dstudio.utils.GSharedPreferences;
import edu.umich.dstudio.utils.Utils;

/**
 * Created by neera_000 on 3/27/2016.
 */
public class FirebaseWrapper {

    private GoogleApiClient mGoogleApiClient;
    private static FirebaseWrapper mInstance;

    private FirebaseWrapper(GoogleApiClient aGoogleApiClient) {
        mGoogleApiClient = aGoogleApiClient;
    }

    private FirebaseWrapper() {
        mGoogleApiClient = null;
    }

    public static FirebaseWrapper getInstance(GoogleApiClient aGoogleApiClient) {
        if(mInstance == null) {
            mInstance = new FirebaseWrapper(aGoogleApiClient);
        }
        return mInstance;
    }

    public static FirebaseWrapper getInstance() {
        if(mInstance == null) {
            mInstance = new FirebaseWrapper();
        }
        return mInstance;
    }

    public static void uploadMood(Mood m) {
        String userEmail = Utils.encodeEmail(GSharedPreferences.getInstance().getPreference(Constants.ID_SHAREDPREF_EMAIL));
        Firebase moodListRef = new Firebase(Constants.FIREBASE_URL_MOODS).child(userEmail).child(new SimpleDateFormat("MMddyyyy").format(new Date()).toString());
        /* Create a new node and get a random id */
        Firebase newMoodRef = moodListRef.push();
        newMoodRef.setValue(m);
    }

    public static void uploadNote(Note n) {
        String userEmail = Utils.encodeEmail(GSharedPreferences.getInstance().getPreference(Constants.ID_SHAREDPREF_EMAIL));
        Firebase notesListRef = new Firebase(Constants.FIREBASE_URL_NOTES).child(userEmail).child(new SimpleDateFormat("MMddyyyy").format(new Date()).toString());
        /* Create a new node and get a random id */
        Firebase newNoteRef = notesListRef.push();
        newNoteRef.setValue(n);
    }

    public static void uploadPhoto(Photo p) {
        String userEmail = Utils.encodeEmail(GSharedPreferences.getInstance().getPreference(Constants.ID_SHAREDPREF_EMAIL));
        Firebase photosListRef = new Firebase(Constants.FIREBASE_URL_PHOTOS).child(userEmail).child(new SimpleDateFormat("MMddyyyy").format(new Date()).toString());
        /* Create a new node and get a random id */
        Firebase newPhotoRef = photosListRef.push();
        newPhotoRef.setValue(p);
    }

    public static void uploadLastLocation(LastLocation l) {
        String userEmail = Utils.encodeEmail(GSharedPreferences.getInstance().getPreference(Constants.ID_SHAREDPREF_EMAIL));
        Firebase locationListRef = new Firebase(Constants.FIREBASE_URL_LAST_LOCATION).child(userEmail).child(new SimpleDateFormat("MMddyyyy").format(new Date()).toString());
        /* Create a new node and get a random id */
        Firebase newLastLocationRef = locationListRef.push();
        newLastLocationRef.setValue(l);

    }
    public static void uploadQuestionAnswer(List<QuestionAnswer> QnAList) {
        String userEmail = Utils.encodeEmail(GSharedPreferences.getInstance().getPreference(Constants.ID_SHAREDPREF_EMAIL));
        Firebase questionAnswerListRef = new Firebase(Constants.FIREBASE_URL_QUESTION_ANSWER).child(userEmail).child(new SimpleDateFormat("MMddyyyy").format(new Date()).toString());
        /* Create a new node and get a random id */
        Firebase newQuestionAnswerListRef = questionAnswerListRef.push();
        newQuestionAnswerListRef.setValue(QnAList);

    }
}

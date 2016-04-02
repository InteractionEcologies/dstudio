package edu.umich.dstudio.ui.addDataActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.maps.model.LatLng;

import java.util.Set;

import edu.umich.dstudio.R;
import edu.umich.dstudio.model.Note;
import edu.umich.dstudio.ui.BaseActivity;

/**
 * Created by neera_000 on 3/27/2016.
 */
public class NoteEntryActivty extends BaseActivity {

    private Note mNote;
    private EditText mNoteData;
    private ImageView acceptButton;
    private ImageView rejectButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_entry);
        mNote = new Note();
        mNoteData = (EditText) findViewById(R.id.note_data);

        // Add click listeners for buttons
        acceptButton = (ImageView) findViewById(R.id.acceptButton);
        rejectButton = (ImageView) findViewById(R.id.rejectButton);

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptResults();
            }
        });
        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rejectResults();
            }
        });

        Bundle extras = getIntent().getExtras();
        if(extras !=null && extras.getBoolean("FROM_NOTIFICATION")) {
            for(String key:extras.keySet()) {
                Log.d("NoteEntryActivity", key);
            }
            showToast("This screen was started from a notification with type:" + getIntent().getExtras().getString("PROMPT_TYPE", ""));
        }
    }


    /**
     * This is called when the user pressed "Tick" button on the screen.
     */
    public void acceptResults() {
        LatLng l = getCurrentLocation();
        mNote.latitude = (float) l.latitude;
        mNote.longitude = (float) l.longitude;

        // Make sure that there is some data entered by the user in the note field.
        String noteData = mNoteData.getText().toString();
        if (noteData == null || noteData.equals("") || noteData.trim().isEmpty()) {
            showToast("Cannot add note without any content.");
            return;
        } else {
            mNote.note = noteData;
            mFirebaseWrapper.uploadNote(mNote);
            showToast("Your note has been recorded");
            finish();
        }
    }

    /**
     * This is called when the user presses the "X" button the screen.
     */
    public void rejectResults() {
        showToast("Going back to home screen");
        finish();
    }
}

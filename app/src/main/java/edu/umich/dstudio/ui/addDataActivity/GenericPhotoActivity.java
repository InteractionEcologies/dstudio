package edu.umich.dstudio.ui.addDataActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.desmond.squarecamera.CameraActivity;
import com.desmond.squarecamera.ImageUtility;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayOutputStream;

import edu.umich.dstudio.R;
import edu.umich.dstudio.model.FirebaseWrapper;
import edu.umich.dstudio.model.Photo;
import edu.umich.dstudio.ui.BaseActivity;

/**
 * Created by neera_000 on 3/27/2016.
 * This is the parent class for AddCamerPhotoActivity and AddGalleryPhotoActivity.
 * Both of these activities get images from their respective data sources and eventually end up
 * in the @link{onActivityResult} method of this class.
 */
public class GenericPhotoActivity extends BaseActivity {

    protected static final int REQUEST_CAMERA = 101;
    protected static final int REQUEST_GALLERY = 102;

    protected ImageView preview;
    protected EditText annotation;

    protected String base64;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo);
        preview = (ImageView) findViewById(R.id.preview);
        annotation = (EditText) findViewById(R.id.annotation);

        // Add click listeners for buttons
        ImageView acceptButton = (ImageView) findViewById(R.id.acceptButton);
        ImageView rejectButton = (ImageView) findViewById(R.id.rejectButton);

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
    }

    private void rejectResults() {
        showToast("Back to home screen");
        finish();
    }

    private void acceptResults() {
        // Make sure that there is some data entered by the user in the note field.
        String noteData = annotation.getText().toString();
        if (noteData == null || noteData.equals("") || noteData.trim().isEmpty() || base64 == null) {
            showToast("Cannot add photo without any annotation.");
            return;
        } else {
            LatLng l = getCurrentLocation();
            Photo photo = new Photo(
                    (float) l.latitude,
                    (float) l.longitude,
                    base64,
                    noteData);
            mFirebaseWrapper.uploadPhoto(photo);
            showToast("Uploading annotated photo.");
            finish();
        }
    }



    private String getBase64FromBitmap(Bitmap b) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.JPEG, 20, stream);
        byte[] byteArray = stream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            finish();
        }

        // There are two types of media that are requested, either from Camera, or from Gallery.
        //Each of them have their own ways of rendering the selected/taken image.
        if (requestCode == REQUEST_CAMERA
                && resultCode == RESULT_OK
                && null != data) {
            Uri photoUri = data.getData();
            Bitmap bitmap = ImageUtility.decodeSampledBitmapFromPath(photoUri.getPath(), 300, 300);
            preview.setImageBitmap(bitmap);
            base64 = getBase64FromBitmap(bitmap);
        } else if(requestCode == REQUEST_GALLERY
                && resultCode == RESULT_OK
                && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String imgDecodableString = cursor.getString(columnIndex);
            cursor.close();
            Bitmap bitmap = BitmapFactory.decodeFile(imgDecodableString);
            preview.setImageBitmap(bitmap);
            base64 = getBase64FromBitmap(bitmap);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

package edu.umich.dstudio.ui;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import edu.umich.dstudio.R;
import edu.umich.dstudio.prompt.PromptConfig;
import edu.umich.dstudio.prompt.PromptService;
import edu.umich.dstudio.ui.addDataActivity.AddCameraPhotoActivity;
import edu.umich.dstudio.ui.addDataActivity.AddGalleryPhotoActivity;
import edu.umich.dstudio.ui.addDataActivity.MoodEntryActivity;
import edu.umich.dstudio.ui.addDataActivity.SettingsActivity;
import edu.umich.dstudio.ui.addDataActivity.NoteEntryActivty;
import edu.umich.dstudio.ui.listadapters.ActionObject;
import edu.umich.dstudio.ui.listadapters.StableArrayAdapter;
import edu.umich.dstudio.utils.NotificationUtils;

public class MainActivity extends BaseActivity {

    private static final int CAMERA_GLUCOSE_READING = 0;
    private static final int CAMERA_INSULIN_SHOT = 1;
    private static final int CAMERA_FOOD = 2;
    private static final int CAMERA_GENERAL = 3;
    private static final int UPLOAD_FROM_GALLERY = 4;
    private static final int MOOD = 5;
    private static final int NOTE = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeActionList();

        // Start the prompt service after the user logs in.
        // This service periodically uploads the user's location as well as
        // checks the settings params to see if the user has set any reminders
        // to prompt them accordingly.
        startService(new Intent(this, PromptService.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            showSettingsScreen();
            return true;
        }
        if (id == R.id.action_show_camera_activity) {
            PromptConfig pendingNotificationConfig = new PromptConfig(PromptConfig.Type.NOTE, 0);
            Notification n = NotificationUtils.createNotification(this,
                    getPackageManager(), pendingNotificationConfig);
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(0, n);
        }
        if (id == R.id.action_show_questionnaire_activity_mgmt) {
            PromptConfig pendingNotificationConfig = new PromptConfig(PromptConfig.Type.MANAGEMENT_PLAN, 0);
            Notification n = NotificationUtils.createNotification(this,
                    getPackageManager(), pendingNotificationConfig);
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(0, n);
        }
        if (id == R.id.action_show_questionnaire_activity_mood) {
            PromptConfig pendingNotificationConfig = new PromptConfig(PromptConfig.Type.MOOD, 0);
            Notification n = NotificationUtils.createNotification(this,
                    getPackageManager(), pendingNotificationConfig);
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(0, n);
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSettingsScreen() {
        //showToast("Clicked settings");
        Intent preferencesIntent = new Intent(this, SettingsActivity.class);
        startActivity(preferencesIntent);
    }

    private void initializeActionList() {
        final ListView listview = (ListView) findViewById(R.id.list_view_actions_list);

        // the action object is the model behind the list that is shown on the main screen.
        final ActionObject[] array = {
                new ActionObject("Take Glucose Reading Pictures", "A", R.drawable.glucose_reading),
                new ActionObject("Take Insulin Shot Pictures", "A", R.drawable.insulin_shot),
                new ActionObject("Take Food Pictures", "A", R.drawable.food),
                new ActionObject("Take Other Pictures", "A", R.drawable.camera),
                new ActionObject("Upload Screenshot", "A", R.drawable.upload_from_gallery),
                new ActionObject("Report Mood", "A", R.drawable.mood),
                new ActionObject("Take Note", "A", R.drawable.note)
        };

        // The adapter takes the action object array and converts it into a view that can be
        // rendered as a list, one item at a time.
        final StableArrayAdapter adapter = new StableArrayAdapter(
                this.getApplicationContext(), array);
        listview.setAdapter(adapter);


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // The position here corresponds to position of objects in the array passed above.
                Bundle extras = new Bundle();
                switch (position) {
                    case CAMERA_GLUCOSE_READING:
                        Intent addCameraGlucoseReadingPhotoIntent = new Intent(MainActivity.this, AddCameraPhotoActivity.class);
                        extras.putString("photoType", "GLUCOSE_READING");
                        addCameraGlucoseReadingPhotoIntent.putExtras(extras);
                        startActivity(addCameraGlucoseReadingPhotoIntent);
                        break;
                    case CAMERA_INSULIN_SHOT:
                        Intent addCameraInsulinShotPhotoIntent = new Intent(MainActivity.this, AddCameraPhotoActivity.class);
                        extras.putString("photoType", "INSULIN_SHOT");
                        addCameraInsulinShotPhotoIntent.putExtras(extras);
                        startActivity(addCameraInsulinShotPhotoIntent);
                        break;
                    case CAMERA_FOOD:
                        Intent addCameraFoodPhotoIntent = new Intent(MainActivity.this, AddCameraPhotoActivity.class);
                        extras.putString("photoType", "FOOD");
                        addCameraFoodPhotoIntent.putExtras(extras);
                        startActivity(addCameraFoodPhotoIntent);
                        break;
                    case CAMERA_GENERAL:
                        Intent addCameraPhotoIntent = new Intent(MainActivity.this, AddCameraPhotoActivity.class);
                        extras.putString("photoType", "OTHER");
                        addCameraPhotoIntent.putExtras(extras);
                        startActivity(addCameraPhotoIntent);
                        break;
                    case UPLOAD_FROM_GALLERY:
                        Intent addGalleryPhotoIntent = new Intent(MainActivity.this, AddGalleryPhotoActivity.class);
                        extras.putString("photoType", "SCREENSHOT");
                        addGalleryPhotoIntent.putExtras(extras);
                        startActivity(addGalleryPhotoIntent);
                        break;
                    case MOOD:
                        Intent moodIntent = new Intent(MainActivity.this, MoodEntryActivity.class);
                        startActivity(moodIntent);
                        break;
                    case NOTE:
                        Intent noteIntent = new Intent(MainActivity.this, NoteEntryActivty.class);
                        startActivity(noteIntent);
                        break;
                    default:
                        showToast("Clicked unknown");
                        break;
                }
            }
        });

    }

}

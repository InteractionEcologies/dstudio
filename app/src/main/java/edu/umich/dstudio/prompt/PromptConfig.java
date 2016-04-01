package edu.umich.dstudio.prompt;

import edu.umich.dstudio.ui.MainActivity;
import edu.umich.dstudio.ui.addDataActivity.AddGalleryPhotoActivity;
import edu.umich.dstudio.ui.addDataActivity.GenericPhotoActivity;
import edu.umich.dstudio.ui.addDataActivity.MoodEntryActivity;
import edu.umich.dstudio.ui.addDataActivity.NoteEntryActivty;

/**
 * Created by neera_000 on 4/1/2016.
 * This stores information about what kind of prompt is to be shown.
 */
public class PromptConfig {

    private Type mPromptType;
    private int mNotificationTimeInSecondsFromMidnight;

    public enum Type {
        MOOD, PICTURE, NOTE, GENERAL
    }

    /**
     * Give the type of prompt e.g. MOOD, PICTURE, NOTE, GENERAL and the time at which this prompt
     * should be shown. The time is the number of seconds from midnight today at which the prompt
     * should be shown.
     * @param promptType
     * @param aNotificationTimeInSecondsFromMidnight
     */
    public PromptConfig(Type promptType, int aNotificationTimeInSecondsFromMidnight) {
        this.mPromptType = promptType;
        this.mNotificationTimeInSecondsFromMidnight = aNotificationTimeInSecondsFromMidnight;
    }

    public String getMessage() {
        switch (mPromptType) {
            case GENERAL:
                return "Everyone forgets, we get that. That's why God made notifications.";
            case MOOD:
                return "We see that you haven't logged your mood today. Want to tell us how your day went?";
            case PICTURE:
                return "Let's log your GM info. It's as simple as point and click -  we promise!";
            case NOTE:
                return "Want to tell us something about your day?";
            default:
                return "Everyone forgets, we get that. That's why God made notifications.";
        }
    }

    public Class getActivityType() {
        switch (mPromptType) {
            case MOOD:
                return MoodEntryActivity.class;
            case PICTURE:
                return AddGalleryPhotoActivity.class;
            case NOTE:
                return NoteEntryActivty.class;
            case GENERAL:
                default:
                    return MainActivity.class;
        }
    }

    public int getmNotificationTimeInSecondsFromMidnight() {
        return this.mNotificationTimeInSecondsFromMidnight;
    }

    @Override
    public String toString() {
        return "Prompt: Type " + this.mPromptType.toString() +
                " Time:" + this.mNotificationTimeInSecondsFromMidnight;
    }
}

package edu.umich.dstudio.utils;


import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import edu.umich.dstudio.prompt.PromptConfig;

/**
 * Created by neera_000 on 3/26/2016.
 */
public class Utils {

    public static String LOG_TAG = Utils.class.getSimpleName();
    //public static HashMap<Integer, HashMap<String, Integer>> intervals = new HashMap<Integer, HashMap<String, Integer>>();

    /**
     * Given the startTime and endTime in seconds from midnight, and number of prompts to show to
     * the user every day, generates an interval hashmap with the start and end time of each
     * interval.
     * @param numberOfPromptsNeeded
     * @param startTime
     * @param endTime
     * @return
     */
    public static Map<Integer, HashMap<String, Integer>> getIntervalsForMoodRandomization(
            int numberOfPromptsNeeded,
            int startTime,
            int endTime){
        Map<Integer, HashMap<String, Integer>> intervals = new HashMap<>();
        int intervalLenghtInSeconds = (endTime - startTime)/numberOfPromptsNeeded;

        int start;
        int end = startTime;

        for(int i = 0; i < numberOfPromptsNeeded; i++){
            HashMap<String, Integer> map = new HashMap<>();
            start = end;
            map.put("start", start);
            end = end + intervalLenghtInSeconds;
            map.put("end", end);
            intervals.put(i, map);
        }
        return intervals;
    }

    /**
     * Given an interval hashmap, picks random times between the interval start time and end time
     * and returns and array of integers at which the prompts should be shown.
     * @param intervals
     * @return
     */
    public static List<Integer> getTimesForRandomMoodPrompts(Map<Integer, HashMap<String, Integer>> intervals) {
        LinkedList<Integer> notificationTimes = new LinkedList<>();
        Random rand = new Random();
        Iterator<Map.Entry<Integer, HashMap<String, Integer>>> iterator = intervals.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<Integer, HashMap<String, Integer>> entry = iterator.next();
            HashMap<String, Integer> value = entry.getValue();
            int max = value.get("end");
            int min = value.get("start");
            int diff =  max-min;
            int randomNum = rand.nextInt(diff + 1) + min;
            notificationTimes.add(randomNum);

        }
        return notificationTimes;
    }

    /**
     * Given an email Id as string, encodes it so that it can go into the firebase object without
     * any issues.
     * @param unencodedEmail
     * @return
     */
    public static final String encodeEmail(String unencodedEmail) {
        if (unencodedEmail == null) return null;
        return unencodedEmail.replace(".", ",");
    }

    /**
     * (TODO shriti): Implement this to get times as per the settings.
     * Returns a list of integers, where each integer represent the # of seconds since midnight
     * at which the notification needs to be shown.
     * E.g. if the notification should be shown at noon, 4 PM and 8 PM, this method would return
     * 43200 (i.e. number of seconds between midnight and noon),
     * 57600 (i.e. number of seconds between midnight and 4 PM),
     * 72000 (i.e. you get what I'm saying)
     * @return
     */
    public static List<PromptConfig> getTimesAndConfigForNotification() {
        GSharedPreferences gSharedPreferences = GSharedPreferences.getInstance();
        String wakeUpTime = gSharedPreferences.getPreference("startTime");
        String bedTime = gSharedPreferences.getPreference("endTime");
        Set<String> insulinTimes = gSharedPreferences.getHashSetPreference("insulinTimes");
        Set<String> gmTimes = gSharedPreferences.getHashSetPreference("gmTimes");

        // todo : handling default null values. Should be changed to be set in settings directly
        if(wakeUpTime == null || wakeUpTime==""){
            wakeUpTime = "8:0";
        }
        if(bedTime == null || bedTime==""){
            bedTime = "20:0";
        }

        Log.d(LOG_TAG, "Settings information: Wake - " + wakeUpTime +
                " Sleep - " + bedTime +
                " Insulin - " + insulinTimes.toString() +
                " GM Time - " + gmTimes.toString());
        List<Integer> notificationTimes = getTimesForRandomMoodPrompts(
                getIntervalsForMoodRandomization(3,
                        convertStringTimeToSeconds(wakeUpTime),
                        convertStringTimeToSeconds(bedTime)));
        Log.d(LOG_TAG, "Notification Times:" + notificationTimes.toString());

        List<PromptConfig> promptConfigList = new ArrayList<>();

        for(Integer notificationTime:notificationTimes) {
            promptConfigList.add(new PromptConfig(PromptConfig.Type.GENERAL, notificationTime));
        }

        Log.d(LOG_TAG, "Prompt configs: " + promptConfigList.toString());

        return promptConfigList;
    }

    /**
     * Gets all the times at which a notification needs to be shown, and check if the current time
     * is within a window of 15 minutes from that time. Returns true if that is the case, false
     * otherwise.
     * @return A @link{PromptConfig} if a notification were supposed to be scheduled in the last 15
     * minutes. Returns null if there are no notifications pending.
     */
    public static PromptConfig getPromptConfigForPendingNotification() {
        Calendar c = Calendar.getInstance();
        long now = c.getTimeInMillis();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        int midnightSeconds = (int) c.getTimeInMillis()/1000;
        int secondsPassedAfterMidnightToday = (int) now/1000 - midnightSeconds;

        int FIFTEEN_MINUTES_IN_SECONDS = 15 * 60;
        int HALF_OF_FIFTEEN_MINUTES = FIFTEEN_MINUTES_IN_SECONDS/2;

        for(PromptConfig promptConfig:getTimesAndConfigForNotification()) {
            int notificationTimeAsPerSettings = promptConfig.getmNotificationTimeInSecondsFromMidnight();
            int timeDifferenceBetweenNowAndNotificationTime = notificationTimeAsPerSettings - secondsPassedAfterMidnightToday;
            Log.d(LOG_TAG, "Now: " + secondsPassedAfterMidnightToday +
                    " Notification Time: " + notificationTimeAsPerSettings +
                    " Time difference in notification" + timeDifferenceBetweenNowAndNotificationTime);
            if(timeDifferenceBetweenNowAndNotificationTime  > -HALF_OF_FIFTEEN_MINUTES &&
                    timeDifferenceBetweenNowAndNotificationTime < HALF_OF_FIFTEEN_MINUTES) {
                Log.d(LOG_TAG, "Returning notification details from utils pending notification." + promptConfig.toString());
                return promptConfig;
            }
        }
        Log.d(LOG_TAG, "Returning null from utils pending notification.");
        return null;
    }

    public static int convertStringTimeToSeconds(String time){
        int secondsPassed = 0;
        String [] timeString = time.split(":");
        int hour = Integer.parseInt(timeString[0]);
        int minutes = Integer.parseInt(timeString[1]);
        secondsPassed = (hour*60 + minutes)*60;
        return secondsPassed;
    }

    public static List<String> getQuestionList(PromptConfig.Type promptType){
        switch(promptType){
            case MOOD:
                return Constants.MOOD_QUESTIONS;
            case NO_ACTIVITY:
                return Constants.NO_ACTIVITY_QUESTIONS;
            case MANAGEMENT_PLAN:
                return Constants.MANAGEMENT_PLAN_QUESTIONS;
            default:
                return Constants.DEFAULT_QUESTIONS;
        }
    }

}
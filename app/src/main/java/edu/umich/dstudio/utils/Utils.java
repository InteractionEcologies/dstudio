package edu.umich.dstudio.utils;


import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by neera_000 on 3/26/2016.
 */
public class Utils {

    public static HashMap<Integer, HashMap<String, Integer>> intervals = new HashMap<Integer, HashMap<String, Integer>>();

    public static void setIntervalsForMoodRandomization(int n){
        int numberOfPromptsNeeded = n;
        GSharedPreferences gSharedPreferences = GSharedPreferences.getInstance();
        String wakeUpTime = gSharedPreferences.getPreference("startTime");
        String bedTime = gSharedPreferences.getPreference("endTime");

        int startTime = convertStringTimetoSeconds(wakeUpTime);
        int endTime = convertStringTimetoSeconds(bedTime);
        int intervalLenghtInSeconds = (endTime - startTime)/numberOfPromptsNeeded;

        int start;
        int end = startTime;

        for(int i=0; i<n; i++){
            HashMap<String, Integer> map = new HashMap<>();
            start = end;
            map.put("start", start);
            end = end + intervalLenghtInSeconds;
            map.put("end", end);
            intervals.put(i, map);
        }

    }

    public static int[] getTimesForRandomMoodPrompts() {
        //get intervals
        //generate random numbers between those intervals
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
        return toIntArray(notificationTimes);
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
    public static int[] getTimesForNotification() {
        return new int[] {
            43200, 57600, 72000, 79200, 81000, 83400
        };
    }

    /**
     * Gets all the times at which a notification needs to be shown, and check if the current time
     * is within a window of 15 minutes from that time. Returns true if that is the case, false
     * otherwise.
     * @return true if current time is within a 15 minutes window of a time at which a notification
     * needs to be shown. False otherwise.
     */
    public static boolean shouldShowNotification() {
        Calendar c = Calendar.getInstance();
        long now = c.getTimeInMillis();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        int midnightSeconds = (int) c.getTimeInMillis()/1000;
        int secondsPassedAfterMidnight = (int) now/1000 - midnightSeconds;

        GSharedPreferences gSharedPreferences = GSharedPreferences.getInstance();
        String wakeUpTime = gSharedPreferences.getPreference("startTime");
        String bedTime = gSharedPreferences.getPreference("endTime");

        int secondsPassedAfterMidnightAtStartTime = convertStringTimetoSeconds(wakeUpTime) - midnightSeconds;
        int secondsPassedAfterMidnightAtEndTime = convertStringTimetoSeconds(bedTime) - midnightSeconds;

        for(int i:getTimesForNotification()) {
            if(secondsPassedAfterMidnight> secondsPassedAfterMidnightAtStartTime && secondsPassedAfterMidnight < secondsPassedAfterMidnightAtEndTime) {
                return true;
            }
        }
        return false;
    }

    public static int convertStringTimetoSeconds(String time){
        int secondsPassed = 0;
        String [] timeString = time.split(":");
        int hour = Integer.parseInt(timeString[0]);
        int minutes = Integer.parseInt(timeString[1]);
        secondsPassed = (hour*60 + minutes)*60;
        return secondsPassed;
    }

    public static int[] toIntArray(List<Integer> integerList) {
        int[] intArray = new int[integerList.size()];
        for (int i = 0; i < integerList.size(); i++) {
            intArray[i] = integerList.get(i);
        }
        return intArray;
    }


}


package com.servantsoftware.intervaltimer;

import java.util.HashMap;

/**
 * Created by Nick Gable on 4/29/2016.
 */
public class TimerState {
    public enum State {NOTSTARTED, WARMUP, SLOW, FAST, COOLDOWN, PAUSED, FINISHED}

    //Settings
    public static int intervalMax = 5;
    public static HashMap<State, String> labels = new HashMap<>();
    public static HashMap<State, Integer> maxCounts = new HashMap<>();

    //State
    public static Object stateLock = new Object();
    public static int currentInterval = 1;
    public static int currentCount = 0;
    public static State currentState = State.NOTSTARTED;
    public static State prePauseState = State.NOTSTARTED;

    public static String getTimeString(int seconds){
        String pad = "";
        if (seconds % 60 < 10) pad = "0";
        return Integer.toString(seconds / 60) + ":" + pad + Integer.toString(seconds % 60);
    }

    //Initialization
    static {
        labels.put(State.NOTSTARTED, "Ready?");
        labels.put(State.WARMUP, "Warm it up");
        labels.put(State.SLOW, "Slow and Steady");
        labels.put(State.FAST, "Give it all you've got!");
        labels.put(State.COOLDOWN, "Cool down, almost there");
        labels.put(State.PAUSED, "Paused");
        labels.put(State.FINISHED, "All done! You rock!");

        maxCounts.put(State.NOTSTARTED, 0);
        maxCounts.put(State.WARMUP, 12);
        maxCounts.put(State.SLOW, 10);
        maxCounts.put(State.FAST, 5);
        maxCounts.put(State.COOLDOWN, 14);
        maxCounts.put(State.PAUSED, 0);
        maxCounts.put(State.FINISHED, 0);
    }
}

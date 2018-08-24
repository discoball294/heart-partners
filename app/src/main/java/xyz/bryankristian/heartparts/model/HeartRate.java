package xyz.bryankristian.heartparts.model;

/**
 * Created by bryanasakristian on 8/16/18.
 */

public class HeartRate {
    long date;
    int heartRate;
    String userUID;

    public HeartRate(){

    }

    public HeartRate(long date, int heartRate, String userUID) {
        this.date = date;
        this.heartRate = heartRate;
        this.userUID = userUID;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }

    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }
}

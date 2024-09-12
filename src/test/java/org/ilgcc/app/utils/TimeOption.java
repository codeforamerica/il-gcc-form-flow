package org.ilgcc.app.utils;

public enum TimeOption {

    NOTIME("", "", ""),
    TIME8AM("8", "0", "AM"),
    TIME9AM("9", "0", "AM"),
    TIME10AM("10", "0", "AM"),
    TIME1PM("1", "0", "PM"),
    TIME113PM("1", "13", "PM"),
    TIME12PM("12", "0", "PM"),
    TIME3PM("3", "0", "PM"),
    TIME310PM("3", "10", "PM"),
    TIME345PM("3", "45", "PM"),
    TIME5PM("5", "0", "PM"),
    TIME7PM("7", "0", "PM");

    private final String hour;
    private final String minute;
    private final String amOrPm;

    TimeOption(String hour, String minute, String amOrPm) {

        this.hour = hour;
        this.minute = minute;
        this.amOrPm = amOrPm;
    }

    public String getHour() {
        return hour;
    }

    public String getMinute() {
        return minute;
    }

    public String getAmOrPm() {
        return amOrPm;
    }

}

package com.leqienglish.util.string;

public class StringUtil {


    public static String replace(String str, String src, String target) {
        String lowStr = src.toLowerCase();

        String[] wordArr = str.split(" ");
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < wordArr.length; i++) {
            String word = wordArr[i];
            if (word.toLowerCase().equals(lowStr)) {
                wordArr[i] = target;
            }
            stringBuilder.append(word).append(" ");
        }

        return stringBuilder.toString();
    }

    public static String toTime(int hour, int mins, int seconds) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(addPre(hour));
        stringBuilder.append(":");

        stringBuilder.append(addPre(mins));
        stringBuilder.append(":");
        stringBuilder.append(addPre(seconds));
        return stringBuilder.toString();
    }

    public static String toMinsAndSeconds(long millis) {
        int seconds = (int) (millis / 1000);

        return toMinsAndSecondsBySeconds(seconds);
    }

    public static String toMinsAndSeconds(Integer millis) {
        int seconds = (int) (millis / 1000);

        return toMinsAndSecondsBySeconds(seconds);
    }

    public static String toMinsAndSecondsBySeconds(int seconds) {
        int mins = seconds / 60;

        seconds %= 60;

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(addPre(mins));
        stringBuilder.append(":");
        stringBuilder.append(addPre(seconds));
        return stringBuilder.toString();
    }


    public static String toTime(int mins, int seconds) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(addPre(mins));
        stringBuilder.append(":");
        stringBuilder.append(addPre(seconds));
        return stringBuilder.toString();
    }

    private static String addPre(int num) {
        StringBuilder stringBuilder = new StringBuilder();
        if (num < 10) {
            stringBuilder.append("0");
        }

        stringBuilder.append(num);

        return stringBuilder.toString();
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }
}

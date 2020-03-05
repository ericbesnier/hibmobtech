package com.hibernatus.hibmobtech.search;

/**
 * Created by tgo on 02/12/15.
 */
public class SearchHelper {
    private static String activityFilter;

    public static String getActivityFilter() {
        return activityFilter;
    }

    public static void setActivityFilter(String activityFilter) {
        SearchHelper.activityFilter = activityFilter;
    }
}

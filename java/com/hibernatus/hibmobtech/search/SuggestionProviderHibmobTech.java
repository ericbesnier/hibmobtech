package com.hibernatus.hibmobtech.search;

import android.content.SearchRecentSuggestionsProvider;
import android.util.Log;

public class SuggestionProviderHibmobTech extends SearchRecentSuggestionsProvider {
    public static final String TAG = SuggestionProviderHibmobTech.class.getSimpleName();
    public final static String AUTHORITY = SuggestionProviderHibmobTech.class.getName();
    public final static int MODE = DATABASE_MODE_QUERIES;
    public SuggestionProviderHibmobTech() {
        Log.d(TAG, "SuggestionProviderHibmobTech");
        setupSuggestions(AUTHORITY, MODE);
    }
}

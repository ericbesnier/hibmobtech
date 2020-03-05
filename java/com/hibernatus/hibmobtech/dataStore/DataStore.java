package com.hibernatus.hibmobtech.dataStore;

/**
 * Created by tgo on 04/11/15.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.Log;

import com.hibernatus.hibmobtech.authenticator.User;

/**
 * Created by tgo on 04/11/15.
 */
public class DataStore {
    public static final String USER_NAME_KEY = "UserName";
    public static final String PASS_KEY = "Password";
    public static final String USER_FULLNAME_KEY = "UserFullname";
    public static final String USER_ID_KEY = "UserId";
    public static final String AUTH_TOKEN_KEY = "AuthToken";
    final static String TAG = DataStore.class.getSimpleName();
    private static final String DATA_FILE = "hibbizDataFile";
    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";
    public static final long UNDEFINED_USER_ID = -1;
    private Context context;
    private SharedPreferences data;
    private SharedPreferences.Editor editor;

    public DataStore(Context context) {
        this.context = context;
        this.data = context.getSharedPreferences(DATA_FILE, Context.MODE_PRIVATE);
        editor = data.edit();
    }

    public void setLogin(boolean isLoggedIn) {

        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);

        // commit changes
        editor.commit();

        Log.d(TAG, "User login session modified!");
    }

    public String getDeviceId() {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public boolean isLoggedIn() {
        Log.d(TAG, "isLoggedIn: " + data.getBoolean(KEY_IS_LOGGEDIN, false));
        return data.getBoolean(KEY_IS_LOGGEDIN, false);
    }

    public void saveUserAndPassword(User user, String pass) {
        SharedPreferences.Editor editor = data.edit();
        editor.putString(USER_NAME_KEY, user.getName());
        editor.putString(USER_FULLNAME_KEY, user.getFullname());
        editor.putLong(USER_ID_KEY, user.getId());
        editor.putString(PASS_KEY, pass);
        setLogin(true);
        editor.commit();
    }

    public String getUserFullname() {
        return data.getString(USER_FULLNAME_KEY, "");
    }

    public Long getUserId() {
        return data.getLong(USER_ID_KEY, UNDEFINED_USER_ID);
    }

    public String getUserName() {
        return data.getString(USER_NAME_KEY, "");
    }

    public String getUserPass() {
        return data.getString(PASS_KEY, "");
    }

    public String getPass() {
        return data.getString(PASS_KEY, "");
    }

    public SharedPreferences getData() {
        return data;
    }

    public void clear() {
        SharedPreferences.Editor editor = data.edit();
        editor.clear();
        editor.commit();
    }

    public void saveAuthToken(String token) {
        SharedPreferences.Editor editor = data.edit();
        editor.putString(AUTH_TOKEN_KEY, token);
        editor.commit();
    }

    public String getAuthToken() {
        return data.getString(AUTH_TOKEN_KEY, null);
    }

    public void clearAuthToken() {
        SharedPreferences.Editor editor = data.edit();
        editor.remove(AUTH_TOKEN_KEY);
        setLogin(false);
        editor.commit();
    }

    public boolean hasData() {
        return !data.getAll().isEmpty();
    }
}
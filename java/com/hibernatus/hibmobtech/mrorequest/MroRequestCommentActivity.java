package com.hibernatus.hibmobtech.mrorequest;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.hibernatus.hibmobtech.HibmobtechOptionsMenuActivity;
import com.hibernatus.hibmobtech.MainActivity;
import com.hibernatus.hibmobtech.R;


/**
 * Created by Eric on 01/04/2016.
 */
public class MroRequestCommentActivity extends HibmobtechOptionsMenuActivity {
    public static final String TAG = MroRequestCommentActivity.class.getSimpleName();
    protected String comment;
    protected MenuItem mainMenuItemCheck;
    protected EditText mroRequestCreateCommentActivityCommentsEditText;
    public static final String ACTIVITY_FILTER = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        setContentView(R.layout.mro_request_create_comment_activity);
        initToolBar();
        //initDrawer();
        mroRequestCreateCommentActivityCommentsEditText
                = (EditText) findViewById(R.id.mroRequestCreateCommentActivityCommentsEditText);
        String currentComment = MroRequestCurrent.getInstance().getMroRequestCurrent().getOperation().getNote();
        if(currentComment != null) {
            mroRequestCreateCommentActivityCommentsEditText.setText(currentComment + "\n");
        }
        mroRequestCreateCommentActivityCommentsEditText.setSelection(mroRequestCreateCommentActivityCommentsEditText.getText().length());
        mroRequestCreateCommentActivityCommentsEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return true;
            }
        });
        setEditTextListerner();
    }

    @Override
    public void onResume() {
        super.onResume();
        trackScreen(TAG);
    }

    public void setEditTextListerner() {
        mroRequestCreateCommentActivityCommentsEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                comment = mroRequestCreateCommentActivityCommentsEditText.getText().toString();
                if (mainMenuItemCheck != null) {
                    if (!mainMenuItemCheck.isVisible()) {
                        mainMenuItemCheck.setVisible(true);
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        Log.i(TAG, "onCreateOptionsMenu");
        super.onCreateOptionsMenu(menu);
        mainMenuItemCheck = menu.findItem(R.id.mainMenuCheckItem);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.mainMenuCheckItem:
                Log.i(TAG, "onOptionsItemSelected: mainMenuCheckItem");
                saveCommentCurrent(comment);
                break;
            default:
                return true;
        }
        return true;
    }

    void saveCommentCurrent(String comment) {
        Log.i(TAG, "savePictureAndCommentCurrent");
        MroRequestCurrent.getInstance().getMroRequestCurrent().getOperation().setNote(comment);
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        finish();
    }
}

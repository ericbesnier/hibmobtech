package com.hibernatus.hibmobtech.authenticator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hibernatus.hibmobtech.R;


public class LoginSuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_success_activity);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.login_success_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String principal = intent.getStringExtra(LoginActivity.PRINCIPAL);

        TextView textView = new TextView(this);
        textView.setTextSize(40);
        textView.setText("You are logged as " + principal);

        RelativeLayout layout = (RelativeLayout)findViewById(R.id.login_success_layout);
        layout.addView(textView);
    }
}

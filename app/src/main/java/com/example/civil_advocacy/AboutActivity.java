package com.example.civil_advocacy;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    private static final String URL_GOOGLE_API = "https://developers.google.com/civic-information/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setTitle(R.string.about_title);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void redirectGoogleApi(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL_GOOGLE_API));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            new AlertDialog.Builder(this)
                    .setMessage(R.string.about_error_no_browser)
                    .setPositiveButton(R.string.ok, null)
                    .show();
        }
    }
}


package com.example.civil_advocacy;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
public class PhotoActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "PhotoActivity";
    private TextView officeTitleTextView;
    private TextView officialNameTextView;
    private TextView locationTextView;
    private OfficialModel official;
    private String locationStr;
    private ImageView democraticLogoImageView;
    private ImageView republicanLogoImageView;
    private ConstraintLayout photoLayout;
    private ImageView photoImageView;
    private Picasso picasso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        official = (OfficialModel) getIntent().getSerializableExtra("info");
        locationStr = getIntent().getStringExtra("location");

        locationTextView = findViewById(R.id.location_text_view);
        locationTextView.setText(locationStr);

        photoLayout = findViewById(R.id.photo_layout);
        photoImageView = findViewById(R.id.photo_image_view);
        picasso = Picasso.get();

        officeTitleTextView = findViewById(R.id.office_title_text_view);
        officialNameTextView = findViewById(R.id.official_name_text_view);
        democraticLogoImageView = findViewById(R.id.democratic_logo_image_view);
        republicanLogoImageView = findViewById(R.id.republican_logo_image_view);

        loadRemoteImage(official);
        loadInfo(official);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void loadInfo(OfficialModel official) {
        officeTitleTextView.setText(official.getPosition());
        officialNameTextView.setText(official.getName());

        if (official.getPartyAffiliation().contains("Democratic")) {
            democraticLogoImageView.setVisibility(View.VISIBLE);
            republicanLogoImageView.setVisibility(View.INVISIBLE);
            photoLayout.setBackgroundColor(Color.BLUE);
            democraticLogoImageView.setOnClickListener(this);
        } else if (official.getPartyAffiliation().contains("Republican")) {
            democraticLogoImageView.setVisibility(View.INVISIBLE);
            republicanLogoImageView.setVisibility(View.VISIBLE);
            photoLayout.setBackgroundColor(Color.RED);
            republicanLogoImageView.setOnClickListener(this);
        } else {
            democraticLogoImageView.setVisibility(View.INVISIBLE);
            republicanLogoImageView.setVisibility(View.INVISIBLE);
            photoLayout.setBackgroundColor(Color.BLACK);
        }
    }

    @Override
    public void onClick(View view) {
        String partyUrl = "";

        if (view == democraticLogoImageView) {
            partyUrl = "https://democrats.org/";
        } else if (view == republicanLogoImageView) {
            partyUrl = "https://www.gop.com/";
        }

        if (!partyUrl.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(partyUrl));
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(String.format("Cannot open %s's website", partyUrl));
                builder.setTitle("No App Found");
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
    }

    private void loadRemoteImage(OfficialModel official) {
        if (!official.getPhotoURL().equals("")) {
            String url = official.getPhotoURL();

            picasso.load(url)
                    .centerInside()
                    .fit()
                    .centerInside()
                    .error(R.drawable.brokenimage)
                    .fit()
                    .centerInside()
                    .placeholder(R.drawable.missing)
                    .into(photoImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "load Image Success");
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.d(TAG, "load Image Error");
                        }
                    });
        }
        else{
            picasso.load(R.drawable.brokenimage);
        }
    }
}


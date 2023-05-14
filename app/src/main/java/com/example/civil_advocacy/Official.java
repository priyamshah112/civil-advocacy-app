package com.example.civil_advocacy;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Callback;
public class Official extends AppCompatActivity {
    private final String TAG = "Official";
    private boolean address_check = false, phone_check = false, email_check = false;
    private TextView address_label;
    private TextView phone_label;
    private TextView email_label;
    private TextView website_label;
    private TextView location;
    private ImageView official_image;
    private Picasso picasso;
    private TextView office;
    private TextView official_name;
    private TextView party_name;
    private TextView official_address;
    private TextView official_phone;
    private TextView official_email;
    private TextView official_website;
    private ImageView dem_party_logo;
    private ImageView rep_party_logo;
    private ScrollView scrollView;
    private ConstraintLayout layout;
    private String youtubeID;
    private String facebookID;
    private String twitterID;
    private ImageView Facebook;
    private ImageView Youtube;
    private ImageView Twitter;
    private OfficialModel official;
    private String locationStr;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.official_activity);
        official = (OfficialModel) getIntent().getSerializableExtra("info");
        locationStr = (String) getIntent().getSerializableExtra("location");
        location = findViewById(R.id.location1);
        location.setText(locationStr);
        scrollView = findViewById(R.id.scrollView2);
        layout = findViewById(R.id.layout);
        address_label = findViewById(R.id.address_label);
        phone_label = findViewById(R.id.phone_label);
        email_label = findViewById(R.id.email_label);
        website_label = findViewById(R.id.website_label);
        official_image = findViewById(R.id.official_image);
        picasso = Picasso.get();
        picasso.setLoggingEnabled(true);
        office = findViewById(R.id.office);
        official_name = findViewById(R.id.official_name);
        party_name = findViewById(R.id.party_name);
        official_address = findViewById(R.id.official_address);
        official_phone = findViewById(R.id.official_phone);
        official_email = findViewById(R.id.official_email);
        official_website = findViewById(R.id.official_website);
        dem_party_logo = findViewById(R.id.dem_party_logo);
        rep_party_logo = findViewById(R.id.rep_party_logo);
        facebookID = official.getFacebookURL();
        youtubeID = official.getYoutubeURL();
        twitterID = official.getTwitterURL();
        Facebook = findViewById(R.id.facebook);
        Youtube = findViewById(R.id.youtube);
        Twitter = findViewById(R.id.twitter);
        if(!facebookID.equals("")) {
            Facebook.setVisibility(View.VISIBLE);
        }
        else {
            Facebook.setVisibility(View.INVISIBLE);
            Facebook.setOnClickListener(null);
        }

        if(!youtubeID.equals("")) {
            Youtube.setVisibility(View.VISIBLE);
        }
        else {
            Youtube.setVisibility(View.INVISIBLE);
            Youtube.setOnClickListener(null);
        }

        if(!twitterID.equals("")) {
            Twitter.setVisibility(View.VISIBLE);
        }
        else {
            Twitter.setVisibility(View.INVISIBLE);
            Twitter.setOnClickListener(null);
        }
        loadRemoteImage(official);
        loadInfo(official);
    }

    @Override
    public void onBackPressed() {
        finish();
    }


    private void loadInfo(OfficialModel official){
        office.setText(official.getPosition());
        official_name.setText(official.getName());
        String party = "(" + official.getPartyAffiliation() +")";
        party_name.setText(party);

        if(official.getPartyAffiliation().contains("Democrat")) {
            dem_party_logo.setVisibility(View.VISIBLE);
            rep_party_logo.setVisibility(View.INVISIBLE);
            scrollView.setBackgroundColor(Color.BLUE);
            layout.setBackgroundColor(Color.BLUE);
            dem_party_logo.setOnClickListener(this::DemClick);

        }
        else if(official.getPartyAffiliation().contains("Republican")){
            dem_party_logo.setVisibility(View.INVISIBLE);
            rep_party_logo.setVisibility(View.VISIBLE);
            scrollView.setBackgroundColor(Color.RED);
            layout.setBackgroundColor(Color.RED);
            rep_party_logo.setOnClickListener(this::RepClick);
        }
        else{
            dem_party_logo.setVisibility(View.INVISIBLE);
            rep_party_logo.setVisibility(View.INVISIBLE);
            scrollView.setBackgroundColor(Color.BLACK);
            layout.setBackgroundColor(Color.BLACK);
            party_name.setVisibility(View.INVISIBLE);
        }

        if(!official.getAddress().equals("")){
            address_label.setText(R.string.official_address);
            String str = "<u>"+official.getAddress() + "<u>";
            address_check =true;
            official_address.setText(Html.fromHtml(str));
            official_address.setOnClickListener(this::clickMap);
            official_address.setTextColor(Color.WHITE);
        }

        if(!official.getPhoneNumber().equals("")) {
            if(address_check) {
                phone_label.setText(R.string.official_phone);
                phone_check =true;
                official_phone.setText(official.getPhoneNumber());
                Linkify.addLinks(official_phone,Linkify.PHONE_NUMBERS);
                official_phone.setLinkTextColor(Color.WHITE);
            }
            else {
                address_label.setText(R.string.official_phone);
                address_check =true;
                official_address.setText(official.getPhoneNumber());
                official_address.setLinkTextColor(Color.WHITE);
                Linkify.addLinks(official_address,Linkify.PHONE_NUMBERS);
            }
        }

        if(!official.getEmailAddress().equals("")) {
            if(address_check && phone_check) {
                email_label.setText(R.string.official_email);
                email_check =true;
                official_email.setText(official.getEmailAddress());
                Linkify.addLinks(official_email,Linkify.EMAIL_ADDRESSES);
                official_email.setLinkTextColor(Color.WHITE);
            }
            else if(address_check){
                phone_label.setText(R.string.official_email);
                phone_check =true;
                official_phone.setText(official.getEmailAddress());
                Linkify.addLinks(official_phone,Linkify.EMAIL_ADDRESSES);
                official_phone.setLinkTextColor(Color.WHITE);
            }
            else{
                address_label.setText(R.string.official_email);
                address_check =true;
                official_address.setText(official.getEmailAddress());
                Linkify.addLinks(official_address,Linkify.EMAIL_ADDRESSES);
                official_address.setLinkTextColor(Color.WHITE);
            }
        }

        if(!official.getWebsiteURL().equals("")) {
            if(address_check && phone_check && email_check) {
                website_label.setText(R.string.official_website);
                official_website.setText(official.getWebsiteURL());
                Linkify.addLinks(official_website,Linkify.WEB_URLS);
                official_website.setLinkTextColor(Color.WHITE);
            }
            else {
                official_website.setVisibility(View.INVISIBLE);
                website_label.setVisibility(View.INVISIBLE);
                if(address_check && phone_check){
                    email_label.setText(R.string.official_website);
                    email_check =true;
                    official_email.setText(official.getWebsiteURL());
                    Linkify.addLinks(official_email,Linkify.WEB_URLS);
                    official_email.setLinkTextColor(Color.WHITE);
                }
                else if (address_check){
                    phone_label.setText(R.string.official_website);
                    phone_check =true;
                    official_phone.setText(official.getWebsiteURL());
                    Linkify.addLinks(official_phone,Linkify.WEB_URLS);
                    email_label.setVisibility(View.INVISIBLE);
                    official_email.setVisibility(View.INVISIBLE);
                    official_phone.setLinkTextColor(Color.WHITE);
                }
                else{
                    address_label.setText(R.string.official_website);
                    address_check =true;
                    official_address.setText(official.getWebsiteURL());
                    Linkify.addLinks(official_address,Linkify.WEB_URLS);
                    phone_label.setVisibility(View.INVISIBLE);
                    official_phone.setVisibility(View.INVISIBLE);
                    email_label.setVisibility(View.INVISIBLE);
                    official_email.setVisibility(View.INVISIBLE);
                    official_address.setLinkTextColor(Color.WHITE);
                }
            }
        }
        if(!address_check &&!phone_check && !email_check){
            address_label.setVisibility(View.INVISIBLE);
            official_address.setVisibility(View.INVISIBLE);
            phone_label.setVisibility(View.INVISIBLE);
            official_phone.setVisibility(View.INVISIBLE);
            email_label.setVisibility(View.INVISIBLE);
            official_email.setVisibility(View.INVISIBLE);
            website_label.setVisibility(View.INVISIBLE);
            official_website.setVisibility(View.INVISIBLE);
        }
    }

    public void clickMap(View v) {
        String address = official.getAddress();

        Uri mapUri = Uri.parse("geo:0,0?q=" + Uri.encode(address));

        Intent intent = new Intent(Intent.ACTION_VIEW, mapUri);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage("Cannot open address");
            builder.setTitle("No App Found");

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
    private void loadRemoteImage(OfficialModel official) {
        if(!official.getPhotoURL().equals("")) {
            String url = official.getPhotoURL();
            official_image.setOnClickListener(this::imageClick);
            picasso.load(url).centerInside().fit().centerInside()
                    .error(R.drawable.brokenimage).fit().centerInside()
                    .placeholder(R.drawable.missing)
                    .into(official_image, new Callback() {
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
    }

    public void DemClick(View v){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://democrats.org/"));

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage("Cannot open Democrat's website");
            builder.setTitle("No App Found");

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    public void RepClick(View v){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://www.gop.com/"));

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage("Cannot open Republican's website");
            builder.setTitle("No App Found");

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    public void imageClick(View v){
        Intent intent = new Intent(this, PhotoActivity.class);
        intent.putExtra("info", official);
        intent.putExtra("location", locationStr);
        startActivity(intent);
    }

    public void youTubeClicked(View v) {
        Intent intent;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.youtube");
            intent.setData(Uri.parse("https://www.youtube.com/" + youtubeID));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com/" + youtubeID)));

        }
    }

    public boolean checkInstalled(String packageName) {
        try {
            return getPackageManager().getApplicationInfo(packageName, 0).enabled;
        }
        catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public void facebookClicked(View v) {
        String FACEBOOK_URL = "https://www.facebook.com/"+ facebookID;

        Intent intent;

        if (checkInstalled("com.facebook.katana")) {
            String urlToUse = "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlToUse));
        } else {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(FACEBOOK_URL));
        }

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage("No Application found that handles ACTION_VIEW (FB) intents");
            builder.setTitle("No App Found");

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    public void twitterClicked(View v) {
        String twitterAppUrl = "twitter://user?screen_name=" + twitterID;
        String twitterWebUrl = "https://twitter.com/" +  twitterID;

        Intent intent;
        // Check if Twitter is installed, if not we'll use the browser
        if (checkInstalled("com.twitter.android")) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(twitterAppUrl));
        } else {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(twitterWebUrl));
        }

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage("No Application found that handles ACTION_VIEW (twitter) intents");
            builder.setTitle("No App Found");

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
}

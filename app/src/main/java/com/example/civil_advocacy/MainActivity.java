package com.example.civil_advocacy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private final String TAG = "Main Activity";
    private RecyclerView recyclerView;
    private OfficialAdapter adapter;
    private final ArrayList<OfficialModel> officialList = new ArrayList<>();
    private SwipeRefreshLayout swiper;
    private FusedLocationProviderClient mFusedLocationClient;
    private static final int LOCATION_REQUEST = 111;
    private static String locationString = "Undefined Location";
    private TextView location;
    private String value;
    private TextView no_internet;
    private SharedPreferences sp_forLocationPreserve;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        no_internet = findViewById(R.id.no_internet_message);
        officialList.clear();
        location = findViewById(R.id.location);
        location.setText(locationString);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        checkInternet();
        determineLocation();
        recyclerView = findViewById(R.id.recyclerView);
        adapter = new OfficialAdapter(officialList, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        swiper = findViewById(R.id.swiper);
        swiper.setOnRefreshListener(this::doRefresh);
        sp_forLocationPreserve = getSharedPreferences("civil_advocacy_app", MODE_PRIVATE);
    }

    public void zip_finder(){
        if(checkInternet()) {
            LayoutInflater inflater = LayoutInflater.from(this);
            @SuppressLint("InflateParams") final View view = inflater.inflate(R.layout.find_zip, null);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Enter Address:");
            builder.setView(view);
            no_internet.setVisibility(View.INVISIBLE);
            builder.setPositiveButton("OK", (dialog, id) -> {
                EditText enteredValue = view.findViewById(R.id.find_location);
                if (enteredValue.getText().length() != 0) {
                    officialList.clear();
                    adapter.notifyDataSetChanged();
                    value = enteredValue.getText().toString();
                    sp_forLocationPreserve.edit().putString("preservedLocation1", value).apply();
                    sp_forLocationPreserve.edit().putString("preservedLocation2", value).apply();
                    OfficialController officialController = new OfficialController(value,this);
                    new Thread(officialController).start();
                }
            });
            builder.setNegativeButton("CANCEL", (dialog, id) -> Log.d(TAG, "No zipcode was entered"));

            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Cannot find zip!");
            builder.setMessage("Please connect to the internet!");
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.side_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.about) {
            Intent intent = new Intent(this,AboutActivity.class);
            startActivity(intent);
            return true;
        }
        else if(item.getItemId() == R.id.search){
            zip_finder();
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        int pos = recyclerView.getChildLayoutPosition(v);
        OfficialModel temp = officialList.get(pos);
        Intent intent = new Intent(this, Official.class);
        intent.putExtra("info", temp);
        intent.putExtra("location", locationString);
        startActivity(intent);
    }

    public void updateList(OfficialModel s){
        officialList.add(s);
        adapter.notifyItemInserted(officialList.indexOf(s));
    }

    public void setTextLocation(String s){
        locationString = s;
        location.setText(locationString);
    }

    private void determineLocation() {
        if (checkPermission() ) {
            if(checkInternet())
                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, location1 -> {
                            if (location1 != null) {
                                no_internet.setVisibility(View.INVISIBLE);
                                getPlace(location1);
                            }
                        })
                        .addOnFailureListener(this, e -> Toast.makeText(MainActivity.this,
                                e.getMessage(), Toast.LENGTH_LONG).show());
        }
    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{ android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
            return false;
        }
        return true;
    }


    private void getPlace(Location loc) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
            String a ="";
            for (Address ad:addresses) {
                a = String.format("%s %s %s %s %s %s",
                        (ad.getSubThoroughfare() == null ? "" : ad.getSubThoroughfare()),
                        (ad.getThoroughfare() == null ? "" : ad.getThoroughfare()),
                        (ad.getLocality() == null ? "" : ad.getLocality()),
                        (ad.getAdminArea() == null ? "" : ad.getAdminArea()),
                        (ad.getPostalCode() == null ? "" : ad.getPostalCode()),
                        (ad.getCountryName() == null ? "" : ad.getCountryName()));
            }
            if (sp_forLocationPreserve.getString("preservedLocation1", "").equals("")) {
                OfficialController officialController = new OfficialController(a,this);
                new Thread(officialController).start();
            }
            else {
                OfficialController officialController = new OfficialController(sp_forLocationPreserve.getString("preservedLocation1", ""),this);
                new Thread(officialController).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void NoData(String s){
        location.setText(R.string.no_location_found);
        no_internet.setVisibility(View.VISIBLE);
        officialList.clear();
        if(adapter!=null)
            adapter.notifyDataSetChanged();
        if(s.equals("no internet"))
            no_internet.setText(R.string.no_internet);
        else
            no_internet.setText(R.string.no_location_found);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_REQUEST) {
            if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    determineLocation();
                } else {
                    Toast.makeText(this,"location request denied", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void doRefresh() {
        if(checkInternet()) {
            officialList.clear();
            adapter.notifyDataSetChanged();
            OfficialController officialController = new OfficialController(locationString, this);
            new Thread(officialController).start();
        }
        swiper.setRefreshing(false);
    }

    public boolean checkInternet() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if (!isConnected) {
            NoData("no internet");
        } else {
            no_internet.setVisibility(View.INVISIBLE);
            return true;
        }
        return false;
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration config) {
        super.onConfigurationChanged(config);
        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            sp_forLocationPreserve.edit().putString("preservedLocation", sp_forLocationPreserve.getString("preservedLocation2","")).apply();
        } else if (config.orientation == Configuration.ORIENTATION_PORTRAIT){
            sp_forLocationPreserve.edit().putString("preservedLocation", sp_forLocationPreserve.getString("preservedLocation2","")).apply();
        }
    }
}
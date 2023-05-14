package com.example.civil_advocacy;

import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

public class OfficialController implements Runnable{
    private final String value;
    private final MainActivity mainActivity;
    private final String API_KEY="AIzaSyCEXmtBV4dujXjAp1CVCOoCSaYR6kPeEnM";
    private final String url = "https://www.googleapis.com/civicinfo/v2/representatives?key="+API_KEY+"&address=";

    OfficialController(String value, MainActivity mainActivity) {
        this.value = value;
        this.mainActivity = mainActivity;
    }

    @Override
    public void run() {
        Uri dataUri = Uri.parse(url+ value);
        String urlToUse = dataUri.toString();

        RequestQueue queue = Volley.newRequestQueue(mainActivity);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                urlToUse,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject Data) {
                        try {
                            String normalizedInput = Data.getString("normalizedInput");
                            JSONObject locationInfo = new JSONObject(normalizedInput);
                            String city = locationInfo.getString("city");
                            String state = locationInfo.getString("state");
                            String zip = locationInfo.getString("zip");
                            String location_display = "";
                            if(city.equals(""))
                                location_display = city + state + zip;
                            else
                                location_display = city + ", " +state + " " + zip;
                            mainActivity.setTextLocation(location_display);
                            JSONArray offices = (JSONArray) Data.get("offices");
                            List<String> officeInfo = new ArrayList<>();
                            for (int i = 0 ; i < offices.length(); i++) {
                                JSONObject info = offices.getJSONObject(i);
                                JSONArray indices = (JSONArray) info.get("officialIndices");
                                for(int j = 0; j < indices.length();j++){
                                    officeInfo.add(info.getString("name"));
                                }
                            }
                            JSONArray officials = (JSONArray) Data.get("officials");
                            for (int i = 0 ; i < officials.length(); i++) {
                                JSONObject info = officials.getJSONObject(i);
                                String official_name = info.getString("name");
                                official_name = official_name.trim();
                                String official_position = officeInfo.get(i);
                                JSONArray address=null;
                                if(info.has("address"))
                                    address = (JSONArray) info.get("address");
                                String addressLine = "";
                                if(address!=null)
                                    for (int j = 0; j <address.length(); j++) {
                                        JSONObject addressDetail = address.getJSONObject(0);
                                        addressLine = addressDetail.getString("line1");
                                        if(addressDetail.has("line2"))
                                            addressLine+= ",+" + addressDetail.getString("line2");
                                        if (addressDetail.has("line3"))
                                            addressLine+=", " + addressDetail.getString("line3");
                                        addressLine+= ", " + addressDetail.getString("city");
                                        addressLine += " " + addressDetail.getString("state");
                                        addressLine += " " + addressDetail.getString("zip");
                                    }

                                String party = "", phone="", website="", email="";
                                if(info.has("party"))
                                    party = info.getString("party");

                                if(info.has("phones")) {
                                    JSONArray phoneArray = info.getJSONArray("phones");
                                    String [] phoneList = phoneArray.toString().split(",");
                                    phone = phoneList[0].replaceAll("[\\[\\]\"]", "");
                                }
                                if(info.has("urls")) {
                                    JSONArray websites = info.getJSONArray("urls");
                                    String [] websiteList = websites.toString().split(",");
                                    website = websiteList[0].replaceAll("[\\[\\]\"\\\\]", "");
                                }

                                if(info.has("emails")) {
                                    JSONArray emails = info.getJSONArray("emails");
                                    String [] emailList = emails.toString().split(",");
                                    email = emailList[0].replaceAll("[\\[\\]\"]", "");
                                }

                                String photoURL="";
                                if(info.has("photoUrl")) {
                                    photoURL = info.getString("photoUrl").replaceAll("[\\[\\]\"]", "");
                                    if(!photoURL.contains("https"))
                                        photoURL = photoURL.replaceAll("http","https").trim();
                                }

                                JSONArray channels;
                                String facebook ="", twitter="", youtube="";
                                if(info.has("channels")) {
                                    channels = (JSONArray) info.get("channels");
                                    for (int j = 0; j < channels.length(); j++) {
                                        JSONObject info2 = channels.getJSONObject(j);
                                        if(info2.getString("type").equals("Facebook"))
                                            facebook = info2.getString("id");
                                        if(info2.getString("type").equals("Twitter"))
                                            twitter = info2.getString("id");
                                        if(info2.getString("type").equals("YouTube"))
                                            youtube = info2.getString("id");
                                    }
                                }
                                final OfficialModel official = new OfficialModel(official_name,official_position,addressLine,party,phone,website,email,photoURL,facebook, twitter, youtube);
                                mainActivity.runOnUiThread(() ->  mainActivity.updateList(official));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(mainActivity, "Failed to get data..", Toast.LENGTH_SHORT).show();
                    }
                });
        queue.add(jsonObjectRequest);
    }
}

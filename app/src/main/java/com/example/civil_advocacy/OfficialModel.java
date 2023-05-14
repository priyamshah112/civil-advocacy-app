package com.example.civil_advocacy;

import java.io.Serializable;

public class OfficialModel implements Serializable {
    private final String name;
    private final String position;
    private final String address;
    private final String partyAffiliation;
    private final String phoneNumber;
    private final String websiteURL;
    private final String emailAddress;
    private final String photoURL;
    private final String facebookURL;
    private final String twitterURL;
    private final String youtubeURL;

    public OfficialModel(String name, String position, String address, String partyAffiliation,
                              String phoneNumber, String websiteURL, String emailAddress, String photoURL,
                              String facebookURL, String twitterURL, String youtubeURL) {
        this.name = name;
        this.position = position;
        this.address = address;
        this.partyAffiliation = partyAffiliation;
        this.phoneNumber = phoneNumber;
        this.websiteURL = websiteURL;
        this.emailAddress = emailAddress;
        this.photoURL = photoURL;
        this.facebookURL = facebookURL;
        this.twitterURL = twitterURL;
        this.youtubeURL = youtubeURL;
    }

    public String getName(){ return name;}
    public String getPartyAffiliation(){return partyAffiliation;}
    public String getPhoneNumber(){return phoneNumber;}
    public String getPhotoURL(){return photoURL;}
    public String getPosition(){return position;}
    public String getAddress(){return address;}
    public String getFacebookURL(){return facebookURL;}
    public String getTwitterURL(){return twitterURL;}
    public String getYoutubeURL(){return youtubeURL;}
    public String getWebsiteURL(){return websiteURL;}
    public String getEmailAddress(){return emailAddress;}
}

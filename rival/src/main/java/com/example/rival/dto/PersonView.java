package com.example.rival.dto;

import com.example.rival.model.AppUser;

// One row of the People page, as seen by the person looking at it
public class PersonView {

    // What the "Rival" column should show for this person
    public static final String RIVALS = "RIVALS";                   // already my rival
    public static final String SENT = "SENT";                       // I asked them, waiting
    public static final String RECEIVED = "RECEIVED";               // they asked me, waiting
    public static final String I_HAVE_RIVAL = "I_HAVE_RIVAL";       // I'm in a rivalry with someone else
    public static final String THEY_HAVE_RIVAL = "THEY_HAVE_RIVAL"; // they are
    public static final String FOLLOW_FIRST = "FOLLOW_FIRST";       // I don't follow them yet
    public static final String CAN_REQUEST = "CAN_REQUEST";         // show the request button

    private final AppUser user;
    private final boolean following;
    private final boolean followsMe;
    private final String rivalState;

    public PersonView(AppUser user, boolean following, boolean followsMe, String rivalState) {
        this.user = user;
        this.following = following;
        this.followsMe = followsMe;
        this.rivalState = rivalState;
    }

    public AppUser getUser() { return user; }
    public boolean isFollowing() { return following; }
    public boolean isFollowsMe() { return followsMe; }
    public String getRivalState() { return rivalState; }
}

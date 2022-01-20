package com.example.tidbits;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;

public class ChildUserProfile implements Serializable {
    private String firstName;
    private Bitmap profilePicture;
    private int childId;
    private Calendar birthday;
    private int mathLevel;
    private int scienceLevel;
    private int languagesLevel;

    // Default Constructor Placeholder Values

    /**
     *
     */
    public ChildUserProfile() {
        this.firstName = "<Name>";
        this.profilePicture = null;
        this.birthday = Calendar.getInstance();
        this.childId = -1;
        this.mathLevel = 1;
        this.scienceLevel = 1;
        this.languagesLevel = 1;

    }

    // testing

    /**
     *
     * @param firstName
     * @param profilePicture
     */
    public ChildUserProfile(String firstName, Bitmap profilePicture) {
        super();
        this.firstName = firstName;
        this.profilePicture = profilePicture;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2000);
        calendar.set(Calendar.MONTH, 01);
        calendar.set(Calendar.DAY_OF_MONTH, 01);
        this.birthday = calendar;
    }

    // childId should be managed by application/ database

    /**
     *
     * @param firstName
     * @param profilePicture
     * @param birthday
     */
    public ChildUserProfile(String firstName, Bitmap profilePicture, Calendar birthday) {
        super();
        this.firstName = firstName;
        this.profilePicture = profilePicture;
        this.birthday = birthday;
    }

    // Assume user has valid info, for testing purposes

    /**
     *
     * @param firstName
     * @param profilePicture
     * @param childId
     * @param birthday
     */
    public ChildUserProfile(String firstName, Bitmap profilePicture, int childId, Calendar birthday) {
        super();
        this.firstName = firstName;
        this.profilePicture = profilePicture;
        this.childId = childId;
        this.birthday = birthday;
    }

    /**
     *
     * @return Firstname from Firebase child obj.
     */
    public String getName() {
        return firstName;
    }

    /**
     *
     * @return
     */
    public Bitmap getProfilePicture() {
        return profilePicture;
    }

    /**
     *
     * @return
     */
    public int getChildId() {
        return childId;
    }

    /**
     *
     * @return
     */
    public int getChildAge() { return (int) ChronoUnit.YEARS.between(LocalDate.of(birthday.get(Calendar.YEAR), birthday.get(Calendar.MONTH), birthday.get(Calendar.DAY_OF_MONTH)), LocalDate.now()); }

    /**
     *
     * @return
     */
    public Calendar getBirthday() { return birthday; }

    /**
     *
     * @return
     */
    public int getMathLevel() { return mathLevel; }

    /**
     *
     * @return
     */
    public int getScienceLevel() { return scienceLevel; }

    /**
     *
     * @return
     */
    public int getLanguagesLevel() { return languagesLevel; }
}

package com.tidbits.firebase;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

public class ChildUser implements Serializable {

    private String id;
    Calendar birthday;
    String avatarFile;
    String name;
    String levels;
    String progress;
    LocalDateTime lastPlaytime;

    public ChildUser() {};


    public ChildUser(String id, Calendar birthday, String avatarFile, String name, String levels,
                     String progress, LocalDateTime lastPlaytime) {
        this.id = id;
        this.birthday = birthday;
        this.avatarFile = avatarFile;
        this.name = name;
        this.levels = levels;
        this.progress = progress;
        this.lastPlaytime = lastPlaytime;
    }

    /*
    Getter Methods
     */

    public String getName() {
        return name;
    }

    public String getLevels() {
        return levels;
    }

    public String getProgress() {
        return progress;
    }

    public LocalDateTime getlastPlaytime() {
        return lastPlaytime;
    }

    public String getId() {return id;}

    public int getChildAge() {
        return (int) ChronoUnit.YEARS.between(
                LocalDate.of(birthday.get(Calendar.YEAR),
                        birthday.get(Calendar.MONTH),
                        birthday.get(Calendar.DAY_OF_MONTH)),
                LocalDate.now());
    }
    public Calendar getBirthday() {
        return this.birthday;
    }

    public String getChildId(){
        return this.id;
    }

    /*
    Setter Methods
     */

    public void setName(String name) {
        this.name = name;
        this.updateChild("name");
    }

    public void setLevels(String levels) {
        this.levels = levels;
        this.updateChild("levels");
    }

    public void setProgress(String progress) {
        this.progress = progress;
        this.updateChild("progress");
    }

    public void updatePlaytime() {
        this.lastPlaytime = LocalDateTime.now();
        this.updateChild("lastPlaytime");
    }

    public void updateAvatar(String avatarFile) {
        this.avatarFile = avatarFile;
        this.updateChild("avatarFile");
    }

    /*
    Database Involved Methods
     */

    /**
     * Creates a new ChildUser object and assigns all attributes to a node in RealtimeDatabase
     * under "childUsers" parent w/ key of id assigned from database and populated w/ key:value
     * pairs of name == parameter name and all default attributes
     * @param name - Name of the child being added
     * @return - returns the ChildUser object created
     * @throws JSONException - when JSON tries to do some weird stuff
     */
    public static ChildUser createChild(String name, String AvatarFile, Calendar birthday) throws JSONException {
        DatabaseReference pushedChild =
                FirebaseDatabase
                        .getInstance()
                        .getReference()
                        .child("childUsers").push();
        String childId = pushedChild.getKey();

        // Create ChildUser Object
        ChildUser child = new ChildUser();
        child.id = childId;
        child.name = name;
        child.levels = createLevelsJSON();
        child.progress = createProgressJSON();
        child.lastPlaytime = LocalDateTime.now();
        child.birthday = birthday;
        child.avatarFile = AvatarFile;

        LocalDate localDate = LocalDateTime.ofInstant(
                birthday.toInstant(),
                birthday.getTimeZone().toZoneId()).toLocalDate();

        // Create HashMap of ChildUser object
        HashMap<String, String> childHash = new HashMap<>();
        childHash.put("id", childId);
        childHash.put("name", child.getName());
        childHash.put("levels", child.getLevels());
        childHash.put("lastPlaytime", child.getlastPlaytime().toString());
        childHash.put("progress", child.getProgress());
        childHash.put("birthday", localDate.toString());
        childHash.put("avatarFile", child.avatarFile);

        pushedChild.setValue(childHash).addOnCompleteListener(
                task -> {
                    if (task.isSuccessful())
                        Log.d("Testing ChildUser:        ", child.toString());
                    else {
                        Log.d("Testing Child user:    ", "post not successful :(");
                    }
                }
        );
        return child;
    }

    /**
     * Takes ChildUser object referenced and creates database node w/ corresponding information
     */
    public void createChild() {
        // pushes a new childUser key to the database generating a new unique data path (ID)
        DatabaseReference childUserReference =
                FirebaseDatabase
                        .getInstance()
                        .getReference()
                        .child("childUsers").push();
        // this is the id pushed
        String childId = childUserReference.getKey();
        // assigns ID to child
        this.id = childId;

        LocalDate localDate = LocalDateTime.ofInstant(
                this.birthday.toInstant(),
                this.birthday.getTimeZone().toZoneId()).toLocalDate();

        // creating a HashMap from the childUser information
        HashMap<String, String> childHash = new HashMap<>();
        childHash.put("id", childId);
        childHash.put("name", this.name);
        childHash.put("levels", this.levels.toString());
        childHash.put("lastPlaytime", this.lastPlaytime.toString());
        childHash.put("progress", this.progress.toString());
        childHash.put("birthday", localDate.toString());
        childHash.put("avatarFile", this.avatarFile);

        // Sets value of child in RT database
        assert childId != null;
        childUserReference.setValue(childHash);
    }

    /**
     *
     * @param childId - this is the selector for the ChildUser in the database
     * @return - returns a MutableLiveData for a ChildUser object, populated by the database
     */
    public static MutableLiveData<ChildUser> getChild(String childId) {
        DatabaseReference childReference = FirebaseDatabase
                .getInstance()
                .getReference()
                .child("childUsers")
                .child(childId);

        MutableLiveData<ChildUser> child = new MutableLiveData<>();

        childReference.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot data) {
                        if (data.exists()) {
                            //TLDR - get id, name, progress, levels and lastPlaytime
                            String id = data.child("id").getValue(String.class);
                            String name = data.child("name").getValue(String.class);
                            JSONObject progress = null;
                            JSONObject levels = null;
                            try {
                                progress = new JSONObject(
                                        data.child("progress").getValue(String.class)
                                );
                                levels = new JSONObject(
                                        data.child("levels").getValue(String.class)
                                );
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            LocalDateTime lastPlaytime =
                                    LocalDateTime.parse(
                                            data.child("lastPlaytime").getValue(String.class)
                                    );

                            LocalDate birthday_LD =
                                    LocalDate.parse(
                                            data.child("birthday").getValue(String.class)
                                    );
                            Calendar birthday = DateTimetoDate(birthday_LD);
                            String avatarFile = data.child("avatarFile").getValue(String.class);

                            ChildUser childUser =
                                    new ChildUser(childId, birthday, avatarFile, name, progress.toString(), levels.toString(), lastPlaytime);

                            child.setValue(childUser);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }
        );

        return child;
    }

    /**
     * Updates full child instance inside of database
     */
    public void updateChild() {
        DatabaseReference childUserReference =
                FirebaseDatabase
                        .getInstance()
                        .getReference()
                        .child("childUsers").child(id);

        LocalDate localDate = LocalDateTime.ofInstant(
                this.birthday.toInstant(),
                this.birthday.getTimeZone().toZoneId()).toLocalDate();

        // creating a HashMap from the childUser information
        HashMap<String, String> childHash = new HashMap<>();
        childHash.put("id", this.id);
        childHash.put("name", this.name);
        childHash.put("levels", this.levels.toString());
        childHash.put("lastPlaytime", this.lastPlaytime.toString());
        childHash.put("progress", this.progress.toString());
        childHash.put("birthday", localDate.toString());
        childHash.put("avatarFile", this.avatarFile);


        childUserReference.setValue(childHash);
    }

    public void updateChild(String parameter) {
        DatabaseReference childUserParameterReference =
                FirebaseDatabase
                        .getInstance()
                        .getReference()
                        .child("childUsers").child(id).child(parameter);

        String updateVar = null;

        switch (parameter) {
            case "name":
                updateVar = this.name;
                break;
            case "levels":
                updateVar = this.levels.toString();
                break;
            case "lastPlaytime":
                updateVar = this.lastPlaytime.toString();
                break;
            case "progress":
                updateVar = this.progress.toString();
                break;
            case "avatarFile":
                updateVar = this.avatarFile;
                break;
        }

        if (updateVar != null)
            childUserParameterReference.setValue(updateVar);
    }


    /*
    Helper Methods
     */
    private static String createLevelsJSON() throws JSONException {
        JSONObject levels = new JSONObject();

        levels.put("mathematics", 1);
        levels.put("spelling", 1);
        levels.put("geography", 1);

        return levels.toString();
    };

    private static String createProgressJSON() throws JSONException {
        JSONObject levels = new JSONObject();

        levels.put("mathematics", 0);
        levels.put("spelling", 0);
        levels.put("geography", 0);

        return levels.toString();
    };

    private static Calendar DateTimetoDate(LocalDate localDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();

        calendar.set(localDate.getYear(), localDate.getMonthValue()-1, localDate.getDayOfMonth());
        return calendar;
    }
}
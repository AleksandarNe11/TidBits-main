package com.tidbits.firebase;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class User {
    ChildUser[] childUsers;
    String userId;

    User() {

    }

    /**
     * Creates new child instance for the database -> doesn't need to be asynchronous however
     * because the creation runs in background but child is instantiated in foreground
     *
     * @param name - the name of the child being created
     * @return - returns the newly created child for representation
     * @throws JSONException - here because I decided to use JSON objects... lame
     */
    public static ChildUser createChild(String userId, String name, Calendar birthday, String avatarFile, LifecycleOwner owner) throws JSONException {
        // Create the child object
        ChildUser child = ChildUser.createChild(name, avatarFile, birthday);

        updateUserNewChild(userId, child.getId(), owner);

        return child;
    }

    private static void updateUserNewChild(String userId, String childId, LifecycleOwner owner) {
        MutableLiveData<String[]> childIdsMLD = User.getChildIds(userId, owner);

        DatabaseReference userReference =
                FirebaseDatabase.getInstance().getReference().child("users").child(userId);

        childIdsMLD.observe(owner, new Observer<String[]>() {
            @Override
            public void onChanged(String[] childIds) {
                ArrayList<String> childIdsAl = new ArrayList<String>(Arrays.asList(childIds));
                childIdsAl.add(childId);

                HashMap<String, Object> userHash = new HashMap<>();
                userHash.put("childNum", childIdsAl.size() + "");
                HashMap<String, String> childIdsHash = new HashMap<>();
                for (int i = 0; i < childIdsAl.size(); i++) {
                    childIdsHash.put(i + "", childIdsAl.get(i));
                }
                userHash.put("childIds", childIdsHash);

                userReference.setValue(userHash);
            }
        });
    }

    private static MutableLiveData<String[]> getChildIds(String userId, LifecycleOwner owner) {
        DatabaseReference userReference = FirebaseDatabase
                .getInstance()
                .getReference("users")
                .child(userId);

        MutableLiveData<String[]> idsMLD = new MutableLiveData<>();

        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // typecast return to ArrayList<Object>
                    @SuppressWarnings("unchecked") ArrayList<Object> idsObject = (ArrayList<Object>) snapshot.child("childIds").getValue();
                    // extract from the array list a String Array and pass to callback
                    String[] childIds = new String[0];
                    try {
                        assert idsObject != null;
                        childIds = idsObject.toArray(new String[0]);
                        idsMLD.setValue(childIds);
                    } catch (AssertionError e) {
                        idsMLD.setValue(new String[0]);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return idsMLD;
    }

    public static MutableLiveData<User> getUser(String userId, LifecycleOwner owner) {
        DatabaseReference userReference = FirebaseDatabase
                .getInstance()
                .getReference("users")
                .child(userId);

        MutableLiveData<User> userMLD = new MutableLiveData<>();

        userReference.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // typecast return to ArrayList<Object>
                            @SuppressWarnings("unchecked") ArrayList<Object> idsObject = (ArrayList<Object>) snapshot.child("childIds").getValue();
                            // extract from the array list a String Array and pass to callback
                            String[] childIds = new String[0];
                            User user = new User();
                            user.userId = userId;
                            try {
                                assert idsObject != null;
                                childIds = idsObject.toArray(new String[0]);
                            } catch (AssertionError e) {
                                user.childUsers = new ChildUser[0];
                                userMLD.setValue(user);
                            }

                            //Create synchronized list object to pass to addChild for use in
                            // .observer on MutableLiveObject class
                            ArrayList<ChildUser> childrenAL = new ArrayList<>();
                            List<ChildUser> childrenSYNC =
                                    Collections.synchronizedList(childrenAL);

                            // add children to the ArrayList
                            for (int i = 0; i < childIds.length; i++){
                                // on this iteration, we send the user data upwards into
                                // the userMLD object, this ensures that data is not pushed
                                // before it has been initialized -> IE all children added
                                if (i == childIds.length - 1) {
                                    addChildToList(
                                            childIds[i], owner,
                                            childrenSYNC, userMLD,
                                            user,true);
                                } else {
                                    addChildToList(
                                            childIds[i], owner,
                                            childrenSYNC, userMLD,
                                            user, false);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }
        );

        return userMLD;
    }

    public ChildUser[] getChildUsers() {
        return childUsers;
    }

    // Helper method to add child to ListArray by querying database and observing the
    // MutableLiveData object that was created -> add to list upon update
    private static void addChildToList(
            String childID,
            LifecycleOwner owner,
            List<ChildUser> children,
            MutableLiveData<User> userMLD,
            User user,
            Boolean finalIteration) {

        MutableLiveData<ChildUser> childMLD = ChildUser.getChild(childID);

        childMLD.observe(owner, new Observer<ChildUser>() {
            @Override
            public void onChanged(ChildUser childUser) {
                children.add(childUser);

                if (finalIteration) {
                    user.childUsers = children.toArray(new ChildUser[0]);
                    userMLD.setValue(user);
                }
            }
        });
    }

}

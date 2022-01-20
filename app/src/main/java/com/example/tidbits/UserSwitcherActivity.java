package com.example.tidbits;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tidbits.firebase.*;

import java.util.ArrayList;

/*
TODO:
- link with login and firebase to query child users
- create shared preference to determine which child user is currently selected
 */

public class UserSwitcherActivity extends AppCompatActivity {
    private ArrayList<ChildUser> ChildUsers;
    private RecyclerView recyclerView;
    private Button btnManageKids;
    private String uid;
    private User user;

    // Recycler View Adapter. Manages the RecyclerView
    private class KidsProfileListAdapter extends RecyclerView.Adapter<KidsProfileListAdapter.ViewHolder> {

        /**
         *
         * @param parent
         * @param viewType
         * @return
         */
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.user_profile_tile, parent, false);

            return new ViewHolder(view);
        }

        /**
         *
         * @param holder
         * @param position
         */
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.getFirstName().setText(ChildUsers.get(position).getName());
            // placeholder
            holder.getIVprofilePicture().setImageResource(android.R.drawable.picture_frame);
            // Firebase stuff for profile picture, or do we let them just from predefined presets

        }

        /**
         *
         * @return
         */
        @Override
        public int getItemCount() {
            return ChildUsers.size();
        }

        /**
         *
         */
        // holds the different user profiles
        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView tvFirstName;
            private ImageView IVprofilePicture;

            /**
             *
             * @param itemView
             */
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                // can declareOnClicks Here
                tvFirstName = itemView.findViewById(R.id.textview_name);
                IVprofilePicture = itemView.findViewById(R.id.profilePicture);

                // used SharedPreferences to store current child?
                View.OnClickListener onClickListener = new View.OnClickListener() {
                    /**
                     *
                     * @param view
                     */
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(UserSwitcherActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                };
                tvFirstName.setOnClickListener(onClickListener);
                IVprofilePicture.setOnClickListener(onClickListener);
            }

            /**
             *
             * @return
             */
            public TextView getFirstName() {
                return tvFirstName;
            }

            /**
             *
             * @return
             */
            public ImageView getIVprofilePicture() {
                return IVprofilePicture;
            }
        }

        /**
         *
         * @param profiles
         */
        public KidsProfileListAdapter(ArrayList<ChildUser> profiles) {
            ChildUsers = profiles;
        }
    }

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_switcher);

        Intent intent = getIntent();
        uid = intent.getExtras().getString("uid");

        recyclerView = findViewById(R.id.userSwitcherRecyclerView);
        // hard code until db is complete


        // add new user option if user did not reach limit


    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        MutableLiveData<User> userMLD = User.getUser(uid, this);

        userMLD.observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                ChildUsers = new ArrayList<ChildUser>();
                for (ChildUser childUser: user.getChildUsers()) {
                    ChildUsers.add(childUser);
                }

                KidsProfileListAdapter kidsProfileListAdapter = new KidsProfileListAdapter(ChildUsers);
                recyclerView.setAdapter(kidsProfileListAdapter);

                btnManageKids = findViewById(R.id.btnManageUser);
                btnManageKids.setOnClickListener(new View.OnClickListener() {
                    /**
                     *
                     * @param view
                     */
                    @Override
                    public void onClick(View view) {
                        Bundle bundle = new Bundle();
                        bundle.putInt("numChildren", ChildUsers.size());
                        for (int i = 0; i < ChildUsers.size(); i++) {
                            bundle.putSerializable(i + "", ChildUsers.get(i));
                        }
                        bundle.putString("callingClass",  UserSwitcherActivity.class.getName());
                        bundle.putString("uid", uid);

                        Intent intent = new Intent(UserSwitcherActivity.this, ToolbarNoNavigation.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
            }
        });
    }
}
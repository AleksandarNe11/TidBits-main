package com.example.tidbits;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.tidbits.firebase.*;

import org.json.JSONException;

import java.util.ArrayList;
/*
 * TODO: Hide Add new user option if user is at limit
 */
public class YourKidsFragment extends Fragment{

    private ArrayList<ChildUser> ChildUsers;
    private RecyclerView kidsRecyclerView;
    private LinearLayout addNewChild;

    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_your_kids, container, false);
    }

    /**
     *
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();

        kidsRecyclerView = view.findViewById(R.id.kidsRecyclerView);

        MutableLiveData<User> userMLD = User.getUser(bundle.getString("uid"), getViewLifecycleOwner());

        userMLD.observe(getActivity(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                ChildUsers = new ArrayList<ChildUser>();
                for (ChildUser c : user.getChildUsers()) {
                    ChildUsers.add(c);
                }
                KidsProfileListAdapter kidsProfileListAdapter = new KidsProfileListAdapter(ChildUsers);
                kidsRecyclerView.setAdapter(kidsProfileListAdapter);
            }
        });

        // placeholder
//        ChildUsers = new ArrayList<ChildUser>();
//        int numChildren = bundle.getInt("numChildren");
//        for (int i = 0; i < numChildren; i++) {
//            ChildUsers.add((ChildUser) bundle.getSerializable(i + ""));
//        }
//
//
//        KidsProfileListAdapter kidsProfileListAdapter = new KidsProfileListAdapter(ChildUsers);
//        kidsRecyclerView.setAdapter(kidsProfileListAdapter);

        addNewChild = view.findViewById(R.id.addNewChildOption);
        addNewChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddChildFragment acf = new AddChildFragment();
                acf.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, acf, AddChildFragment.class.getName())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    /**
     *
     */
    // Recycler View Adapter.
    private class KidsProfileListAdapter extends RecyclerView.Adapter<KidsProfileListAdapter.ViewHolder> {

        /**
         *
         * @param profiles
         */
        public KidsProfileListAdapter(ArrayList<ChildUser> profiles) {
            ChildUsers = profiles;
        }

        /**
         *
         * @param parent
         * @param viewType
         * @return
         */
        @NonNull
        @Override
        public KidsProfileListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_kids, parent, false);

            return new ViewHolder(view);
        }

        /**
         *
         * @param holder
         * @param position
         */
        @Override
        public void onBindViewHolder(@NonNull KidsProfileListAdapter.ViewHolder holder, int position) {
            holder.getFirstName().setText(ChildUsers.get(position).getName());
            holder.getAge().setText(String.valueOf(ChildUsers.get(position).getChildAge()));
            // placeholder image
            holder.getKidPicture().setImageResource(R.mipmap.ic_launcher);

            holder.getMathematicsLevel().setText("1");
            holder.getScienceLevel().setText("1");
            holder.getLanguagesLevel().setText("1");

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

        // Manage row_kids.xml Recycler Item View
        private class ViewHolder extends RecyclerView.ViewHolder {
            private TextView tvFirstName;
            private TextView tvKidAge;
            private ImageView ivKidPicture;
            private TextView tvMathematicsLevel;
            private TextView tvScienceLevel;
            private TextView tvLanguagesLevel;
            private ProgressBar pbMathematics;
            private ProgressBar pbScience;
            private ProgressBar pbLanguages;
            private Button btnSelectKid;

            /**
             *
             * @param itemView
             */
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                // initialize views
                tvFirstName = itemView.findViewById(R.id.kidName);
                tvKidAge = itemView.findViewById(R.id.kidAge);
                ivKidPicture = itemView.findViewById(R.id.kidPicture);
                tvMathematicsLevel = itemView.findViewById(R.id.mathematics_level);
                tvScienceLevel = itemView.findViewById(R.id.science_level);
                tvLanguagesLevel = itemView.findViewById(R.id.languages_level);
                pbMathematics = itemView.findViewById(R.id.mathProgressBar);
                pbScience = itemView.findViewById(R.id.geographyProgressBar);
                pbLanguages = itemView.findViewById(R.id.spellingProgressBar);
                btnSelectKid = itemView.findViewById(R.id.btnSelectKid);

                itemView.setOnClickListener(new View.OnClickListener() {
                    /**
                     *
                     * @param view
                     */
                    @Override
                    public void onClick(View view) {
                        ChildUser selectedChild = ChildUsers.get(getAdapterPosition());
                        EditChildFragment editChildFragment = new EditChildFragment();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(ChildUser.class.getName(), selectedChild);
                        editChildFragment.setArguments(bundle);

                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.nav_host_fragment, editChildFragment, EditChildFragment.class.getName())
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .addToBackStack(null)
                                .commit();
                    }
                });
            }

            /**
             *
             * @return
             */
            public TextView getFirstName() { return tvFirstName; }

            /**
             *
             * @return
             */
            public TextView getAge() { return tvKidAge; }

            /**
             *
             * @return
             */
            public ImageView getKidPicture() { return ivKidPicture; }

            /**
             *
             * @return
             */
            public TextView getMathematicsLevel() { return tvMathematicsLevel; }

            /**
             *
             * @return
             */
            public TextView getScienceLevel() { return tvScienceLevel; }

            /**
             *
             * @return
             */
            public TextView getLanguagesLevel() { return tvLanguagesLevel; }

            /**
             *
             * @return
             */
            public ProgressBar getMathematicsProgressBar() { return pbMathematics; }

            /**
             *
             * @return
             */
            public ProgressBar getScienceProgressBar() { return pbScience; }

            /**
             *
             * @return
             */
            public ProgressBar getLanguagesProgressBar() { return pbLanguages; }

            /**
             *
             * @return
             */
            public Button getSelectKidButton() { return btnSelectKid; }
        }
    }

}

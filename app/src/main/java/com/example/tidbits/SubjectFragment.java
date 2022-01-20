package com.example.tidbits;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.tidbits.firebase.Quiz;

import java.util.ArrayList;
import java.util.Locale;

public class SubjectFragment extends Fragment {

    private ArrayList<String> levels;
    private TextView subjectTextView;
    private RecyclerView levelsRecyclerView;
    private String subject;
    private int level;

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
        return inflater.inflate(R.layout.fragment_level_selection, container, false);
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
        subjectTextView = getView().findViewById(R.id.textViewSubject);
        subjectTextView.setText(bundle.getString("subject"));
        subject = bundle.getString("subject");

        levelsRecyclerView = view.findViewById(R.id.levelsRecyclerView);
        levels = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            levels.add("Level " + String.valueOf(i + 1));
        }

        LevelsListAdapter levelsListAdapter = new LevelsListAdapter(levels);
        levelsRecyclerView.setAdapter(levelsListAdapter);
    }

    /**
     *
     */
    // Recycler View Adapter
    private class LevelsListAdapter extends RecyclerView.Adapter<LevelsListAdapter.ViewHolder> {

        /**
         *
         * @param levelsList
         */
        public LevelsListAdapter(ArrayList<String> levelsList) {
            levels = levelsList;
        }

        /**
         *
         * @param parent
         * @param viewType
         * @return
         */
        @NonNull
        @Override
        public LevelsListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_level, parent, false);
            return new ViewHolder(view);
        }

        /**
         *
         * @param holder
         * @param position
         */
        @Override
        public void onBindViewHolder(@NonNull LevelsListAdapter.ViewHolder holder, int position) {
            level = (position + 1);
            // set the level text
            holder.getTextViewLevel().setText(levels.get(position));

            // currently, only 1 level is available
            if (position == 0) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        QuestionFragment questionFragment = new QuestionFragment();

                        MutableLiveData<Quiz> quizMutableLiveData = Quiz.getQuiz(getViewLifecycleOwner(), subject.toLowerCase(Locale.ROOT), 1);


                        quizMutableLiveData.observe(getViewLifecycleOwner(), new Observer<Quiz>() {
                            @Override
                            public void onChanged(Quiz quiz) {
                                Bundle bundle = new Bundle();
                                bundle.putInt("level", 1);
                                bundle.putInt("progress", 0);
                                bundle.putSerializable("quizObject", quiz);
                                bundle.putInt("currentQuestion", 0);


                                questionFragment.setArguments(bundle);

                                getActivity().getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.nav_host_fragment, questionFragment, QuestionFragment.class.getName())
                                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                        .commit();
                            }
                        });

//                        Bundle bundle = new Bundle();
//                        bundle.putInt("level", 1);
//                        bundle.putInt("progress", 0);
////                        bundle.putStringArray("questions", questionIDs);
//                        bundle.putInt("currentQuestion", 0);
//
//
//                        questionFragment.setArguments(bundle);
//
//                        getActivity().getSupportFragmentManager().beginTransaction()
//                                .replace(R.id.nav_host_fragment, questionFragment, QuestionFragment.class.getName())
//                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//                                .commit();
                    }
                });
            }
            // show snackbar
            else {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Snackbar.make(getView(), R.string.not_available, Snackbar.LENGTH_SHORT).show();
                    }
                });
            }
        }

        /**
         *
         * @return
         */
        @Override
        public int getItemCount() {
            return levels.size();
        }

        private class ViewHolder extends RecyclerView.ViewHolder {
            private TextView textViewLevel;

            /**
             *
             * @param itemView
             */
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                // initialize views
                textViewLevel = itemView.findViewById(R.id.textViewLevel);
            }


            public TextView getTextViewLevel() { return textViewLevel; }
        }
    }
}

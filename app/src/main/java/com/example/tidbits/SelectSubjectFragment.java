package com.example.tidbits;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SelectSubjectFragment extends Fragment {
    private ImageButton mathButton;
    private ImageButton geographyButton;
    private ImageButton spellingButton;

    /**
     * onCreateView that inflates each subject.
     * @param inflater Contains the subjects to inflate.
     * @param container Container holding the subjects.
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_select_subject, container, false);
    }

    /**
     *
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mathButton = getView().findViewById(R.id.mathButton);
        mathButton.setOnClickListener(new View.OnClickListener() {
            /**
             * onClick to select math as a subject.
             * @param view
             */
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ToolbarNoNavigation.class);
                intent.putExtra("callingClass", SelectSubjectFragment.class.getName());
                intent.putExtra("subject", getString(R.string.mathematics));
                startActivity(intent);
            }
        });
            /**
             * onClick to select Geography-- as a subject.
             * @param view
             */
        geographyButton = getView().findViewById(R.id.geographyButton);
        geographyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ToolbarNoNavigation.class);
                intent.putExtra("callingClass", SelectSubjectFragment.class.getName());
                intent.putExtra("subject", getString(R.string.geography));
                startActivity(intent);
            }
        });
            /**
             * onClick to select Spelling-- as a subject.
             * @param view
             */
        spellingButton = getView().findViewById(R.id.spellingButton);
        spellingButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ToolbarNoNavigation.class);
                intent.putExtra("callingClass", SelectSubjectFragment.class.getName());
                intent.putExtra("subject", getString(R.string.spelling));
                startActivity(intent);
            }
        });
    }
}

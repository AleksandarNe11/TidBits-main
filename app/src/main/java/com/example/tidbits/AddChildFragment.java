package com.example.tidbits;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;

import com.google.android.material.snackbar.Snackbar;
import com.tidbits.firebase.ChildUser;
import com.tidbits.firebase.User;

import org.json.JSONException;

import java.io.IOException;
import java.util.Calendar;

public class AddChildFragment extends Fragment {

    private Button createButton;
    private EditText editTextFirstName;
    private EditText editTextBirthday;
    private ImageView imageViewProfilePicture;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private ActivityResultLauncher<Intent> intentActivityResultLauncher;

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
        return inflater.inflate(R.layout.fragment_add_new_child, container, false);
    }

    /**
     *
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        createButton = getView().findViewById(R.id.createButton);
        editTextFirstName = getView().findViewById(R.id.first_name);
        editTextBirthday = getView().findViewById(R.id.birthday_picker);
        imageViewProfilePicture = getView().findViewById(R.id.imageView_profile);

        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your
                // app.
            } else {
                // Explain to the user that the feature is unavailable because the
                // features requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
            }
        });

        intentActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Uri photoUri = result.getData().getData();
                            try {
                                Bitmap image = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), photoUri);
                                imageViewProfilePicture.setImageBitmap(image);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

        imageViewProfilePicture.setOnClickListener(new ImageViewOnClickListener());

        Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                calendar.set(Calendar.YEAR, i);
                calendar.set(Calendar.MONTH, i1);
                calendar.set(Calendar.DAY_OF_MONTH, i2);

                editTextBirthday.setText(String.format("%s-%02d-%02d", i, i1, i2));
            }
        };

        editTextBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getActivity(), date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = getArguments();
                String userId = bundle.getString("uid");
                String name = editTextFirstName.getText().toString();
                Calendar birthday = Calendar.getInstance();
                String avatarFile = "";
                try {
                    User.createChild(userId, name, birthday, avatarFile, getViewLifecycleOwner());
                    // go back to YourKids Fragment - doesn't work since createChild takes too long to load
//                    getActivity().getSupportFragmentManager().popBackStack();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     *
     */
    private class ImageViewOnClickListener implements View.OnClickListener {
        /**
         *
         *
         * @param view
         */
        @Override
        public void onClick(View view) {
            // check permission
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intentActivityResultLauncher.launch(intent);
            }
            else if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Snackbar.make(getView(), R.string.photo_permission, Snackbar.LENGTH_LONG)
                        .setAction(R.string.change, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            }
                        }).show();
            }
            else {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
    }
}

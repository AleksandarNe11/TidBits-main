package com.example.tidbits;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.Snackbar;
import com.tidbits.firebase.Question;
import com.tidbits.firebase.Quiz;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

public class QuestionFragment extends Fragment {
    private static String TAG = "QuestionFragment";
    private TextView levelText;
    private TextView textViewQuestion;
    private TextView textViewQuestionPrompt;
    private TextView textViewProgressPercent;
    private ProgressBar progressBar;
    private ImageView imageViewQuestion;
    private ImageView textToSpeechButton;
    private TextView textViewAnswerOne;
    private TextView textViewAnswerTwo;
    private TextView textViewAnswerThree;
    private TextView textViewAnswerFour;
    private TextToSpeech tty;
//    private FirebaseHelper firebaseHelper;
    /**
     * Question fragment onCreate, inflates the questions into the view.
     * @param inflater the question fragment that needs to be inflated.
     * @param container the container to hold the question fragment.
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_question, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        levelText = view.findViewById(R.id.levelNumberText);
        progressBar = view.findViewById(R.id.progressBar);
        textViewProgressPercent = view.findViewById(R.id.textViewProgressPercent);
        textViewQuestion = view.findViewById(R.id.textViewQuestion);
        textViewQuestionPrompt = view.findViewById(R.id.textViewQuestionPrompt);
        imageViewQuestion = view.findViewById(R.id.imageViewQuestion);
        textViewAnswerOne = view.findViewById(R.id.textViewAnswerOne);
        textViewAnswerTwo = view.findViewById(R.id.textViewAnswerTwo);
        textViewAnswerThree = view.findViewById(R.id.textViewAnswerThree);
        textViewAnswerFour = view.findViewById(R.id.textViewAnswerFour);

        textToSpeechButton = view.findViewById(R.id.textToSpeechButton);
        textToSpeechButton.setOnClickListener(new View.OnClickListener() {
            /**
             * TextToSpeech onClick
             * @param view
             */
            @Override
            public void onClick(View view) {
                // create an object textToSpeech and adding features into it
                tty = new TextToSpeech(getActivity().getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int i) {
                        // if No error is found then only it will run
                        if(i!=TextToSpeech.ERROR){
                            // To Choose language of speech
                            tty.setLanguage(Locale.US);
                            tty.setSpeechRate((float) 0.75);
                            if(tty.isSpeaking() == false){
                                tty.speak(textViewQuestion.getText().toString().split( "\n" )[0],TextToSpeech.QUEUE_FLUSH,null);
                            }

                        }
                    }
                });
            }
        });
        // get data from class that inflated this fragment
        Bundle bundle = getArguments();

        levelText.setText(String.valueOf(bundle.getInt("level")));
        progressBar.setProgress(bundle.getInt("progress"));
        textViewProgressPercent.setText(bundle.getInt("progress") + "%");

        // Question
        Quiz quiz = (Quiz)bundle.getSerializable("quizObject");
        Question question = quiz.getQuestionsList()[bundle.getInt("currentQuestion")];

        textViewQuestion.setText(question.getQuestionText());

        // async task for pictures
        class downloadImage extends AsyncTask<String, Void, Bitmap> {
            ImageView image;

            public downloadImage(ImageView iv) {
                image = iv;
            }

            @Override
            protected Bitmap doInBackground(String... strings) {
                try {
                    URL url = new URL(strings[0]);
                    return getImage(url);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                return null;
            }
            public Bitmap getImage(URL url) {
                HttpsURLConnection connection = null;
                try {
                    connection = (HttpsURLConnection)
                            url.openConnection();
                    connection.connect();
                    int responseCode = connection.getResponseCode();
                    if (responseCode == 200) {
                        return BitmapFactory.decodeStream(connection.getInputStream());
                    } else
                        return null;
                } catch (Exception e) {
                    return null;
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
            protected void onPostExecute(Bitmap result) {
                image.setImageBitmap(result);
            }
        }

        // check if image based
        if (question.isImageBased()) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new downloadImage(imageViewQuestion).execute(question.getimageURL());
                }
            });

        }
        else {
            textViewQuestionPrompt.setText(question.getQuestionPrompt());
        }
        // answers text
        textViewAnswerOne.setText(question.getOptions()[0]);
        textViewAnswerTwo.setText(question.getOptions()[1]);
        textViewAnswerThree.setText(question.getOptions()[2]);
        textViewAnswerFour.setText(question.getOptions()[3]);

        View.OnClickListener correctAnswerListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(getView(), R.string.correct_answer, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.next_question, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // open next question if not on q4
                                if (bundle.getInt("currentQuestion") == 4) {
                                    new AlertDialog.Builder(getContext()).setTitle(R.string.congratulations)
                                            .setMessage(getString(R.string.congratulations_message) + " " + bundle.getInt("level"))
                                            .setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    getActivity().getSupportFragmentManager().popBackStack();
                                                    getActivity().finish();
                                                }
                                            }).show();
                                }
                                else {
                                    QuestionFragment questionFragment = new QuestionFragment();
                                    bundle.putInt("currentQuestion", bundle.getInt("currentQuestion") + 1);
                                    bundle.putInt("progress", bundle.getInt("currentQuestion") * 20);
                                    questionFragment.setArguments(bundle);
                                    getActivity().getSupportFragmentManager().popBackStack();
                                    getActivity().getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.nav_host_fragment, questionFragment, QuestionFragment.class.getName())
                                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                            .commit();
                                }
                            }
                        })
                        .show();
            }
        };

        View.OnClickListener incorrectAnswerListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar snackbar = Snackbar.make(getView(), question.getIncorrectResponse(), Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction(R.string.close, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        snackbar.dismiss();
                    }
                });
                snackbar.show();
            }
        };

        switch (question.getCorrectResponse()) {
            case 0:
                textViewAnswerOne.setOnClickListener(correctAnswerListener);
                textViewAnswerTwo.setOnClickListener(incorrectAnswerListener);
                textViewAnswerThree.setOnClickListener(incorrectAnswerListener);
                textViewAnswerFour.setOnClickListener(incorrectAnswerListener);
                break;
            case 1:
                textViewAnswerOne.setOnClickListener(incorrectAnswerListener);
                textViewAnswerTwo.setOnClickListener(correctAnswerListener);
                textViewAnswerThree.setOnClickListener(incorrectAnswerListener);
                textViewAnswerFour.setOnClickListener(incorrectAnswerListener);
                break;
            case 2:
                textViewAnswerOne.setOnClickListener(incorrectAnswerListener);
                textViewAnswerTwo.setOnClickListener(incorrectAnswerListener);
                textViewAnswerThree.setOnClickListener(correctAnswerListener);
                textViewAnswerFour.setOnClickListener(incorrectAnswerListener);
                break;
            case 3:
                textViewAnswerOne.setOnClickListener(incorrectAnswerListener);
                textViewAnswerTwo.setOnClickListener(incorrectAnswerListener);
                textViewAnswerThree.setOnClickListener(incorrectAnswerListener);
                textViewAnswerFour.setOnClickListener(correctAnswerListener);
                break;
        }
    }
}

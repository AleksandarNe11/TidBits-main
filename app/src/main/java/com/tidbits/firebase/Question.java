package com.tidbits.firebase;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;

public class Question implements Serializable {
    boolean imageBased;
    String questionPrompt;
    String imageURL;
    String questionText;
    int correctResponse;
    String incorrectResponse;
    String[] options;

    public Question() {};

    public void setImageBased(boolean imageBased) {
        this.imageBased = imageBased;
    }

    public boolean isImageBased() {
        return imageBased;
    }

    public String getQuestionPrompt() {
        return questionPrompt;
    }

    public void setQuestionPrompt(String questionPrompt) {
        this.questionPrompt = questionPrompt;
    }

    public String getimageURL() {
        return imageURL;
    }

    public void setimageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public void setCorrectResponse(int correctResponse) {
        this.correctResponse = correctResponse;
    }

    public int getCorrectResponse() {
        return correctResponse;
    }

    public String getIncorrectResponse() {
        return incorrectResponse;
    }

    public void setIncorrectResponse(String incorrectResponse) {
        this.incorrectResponse = incorrectResponse;
    }

    public String[] getOptions() {
        return options;
    }

    public void setOptions(String[] options) {
        this.options = options;
    }

    public static MutableLiveData<Question> getQuestion(String QuestionId) {

        DatabaseReference questionReference =
                FirebaseDatabase.getInstance().getReference().child("questions").child(QuestionId);

        MutableLiveData<Question> questionMLD = new MutableLiveData<>();

        questionReference.addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Question question = new Question();
                            question.imageBased =
                                    snapshot.child("imageBased").getValue(Boolean.class);
                            question.questionPrompt =
                                    snapshot.child("questionPrompt").getValue(String.class);
                            question.imageURL =
                                    snapshot.child("imageURL").getValue(String.class);
                            question.questionText =
                                    snapshot.child("questionText").getValue(String.class);
                            question.correctResponse =
                                    snapshot.child("correctResponse").getValue(int.class);
                            question.incorrectResponse =
                                    snapshot.child("incorrectResponse").getValue(String.class);
                            // typecast return to ArrayList<Object>
                            @SuppressWarnings("unchecked") ArrayList<Object> optionsObject
                                    = (ArrayList<Object>) snapshot.child("Options").getValue();
                            // extract from the array list a String Array and pass to callback
                            assert optionsObject != null;
                            question.options = optionsObject.toArray(new String[0]);

                            questionMLD.setValue(question);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }
        );

        return questionMLD;
    }
}
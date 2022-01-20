package com.tidbits.firebase;

import android.provider.ContactsContract;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Quiz implements Serializable {

    String[] questionIds;
    Question[] questionsList;

    public Quiz() {

    }

    public Question[] getQuestionsList() {
        return questionsList;
    }

    public String[] getQuestionIds() {
        return questionIds;
    }

    public void setQuestionIds(String[] questionIds) {
        this.questionIds = questionIds;
    }

    public void setQuestionsList(Question[] questionsList) {
        this.questionsList = questionsList;
    }

    public static MutableLiveData<Quiz> getQuiz(LifecycleOwner owner, String subject, int level) {
        DatabaseReference quizzesReference =
                FirebaseDatabase.getInstance().getReference().child("quizzes");

        MutableLiveData<Quiz> quizMLD = new MutableLiveData<>();

        quizzesReference.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // typecast return to ArrayList<Object>
                            @SuppressWarnings("unchecked") ArrayList<Object> idsObject =
                                    (ArrayList<Object>) snapshot.child(subject)
                                            .child(level + "")
                                            .getValue();
                            // extract from the array list a String Array and pass to callback
                            assert idsObject != null;
                            String[] _questionIds = idsObject.toArray(new String[0]);

                            ArrayList<Question> questionsAL = new ArrayList<>();
                            List<Question> questionsSYNC =
                                    Collections.synchronizedList(questionsAL);

                            Quiz quiz = new Quiz();

                            for (int i=0; i< _questionIds.length; i++) {
                                if (i == _questionIds.length - 1) {
                                    addQuestionToList(
                                            owner, _questionIds[i],
                                            questionsSYNC, quizMLD,
                                            quiz, true);

                                } else {
                                    addQuestionToList(
                                            owner, _questionIds[i],
                                            questionsSYNC, quizMLD,
                                            quiz, false);
                                }
                            }

                            quiz.questionIds = _questionIds;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }
        );

        return quizMLD;
    }

    private static void addQuestionToList(
            LifecycleOwner owner,String questionId,
            List<Question>questionsSYNC, MutableLiveData<Quiz> quizMLD,
            Quiz quiz, Boolean finalIteration) {

        MutableLiveData<Question> questionMLD = Question.getQuestion(questionId);

        questionMLD.observe(owner, new Observer<Question>() {
            @Override
            public void onChanged(Question question) {
                questionsSYNC.add(question);

                if (finalIteration) {
                    quiz.questionsList = questionsSYNC.toArray(new Question[0]);
                    quizMLD.setValue(quiz);
                }
            }
        });
    }
}

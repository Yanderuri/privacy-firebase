package com.example.privacy_firebase;

import com.google.firebase.database.Exclude;

import java.util.Arrays;
import java.util.List;

public class Survey {
    @Exclude
    private String topic;
    private List<Question> questionList;

    public Survey(String topic, Question ...questionList) {
        this.topic = topic;
        this.questionList = Arrays.asList(questionList);
    }

    public String getTopic() {
        return topic;
    }

    public List<Question> getQuestionList() {
        return questionList;
    }

    public void addQuestion(Question lol){
        questionList.add(lol);
    }


}

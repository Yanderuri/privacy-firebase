package com.example.privacy_firebase;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Survey {
    @Exclude
    private final String topic;
    private List<Question> questionList = new ArrayList<>();

    public Survey(String topic, Question ...questionList) {
        this.topic = topic;
        this.questionList = Arrays.asList(questionList);
    }
    public Survey(String topic){
        this.topic = topic;
    }

    @Override
    public String toString(){
        return String.format("Topic: %s",this.topic);
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
    public void addQuestion(String lol){
        questionList.add(new Question(lol));
    }


}

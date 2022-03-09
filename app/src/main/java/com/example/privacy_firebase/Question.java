package com.example.privacy_firebase;

import java.util.Arrays;
import java.util.List;

public class Question {
    private String question;
    private List<String> answers_list = null;

    public Question(String question, String ...answers_choices) {
        this.question = question;
        this.answers_list = Arrays.asList(answers_choices);
    }
    public Question(String question){
        this.question = question;
    }

    public Question(String question, List<String> answers_list) {
        this.question = question;
        this.answers_list = answers_list;
    }

    public String getQuestion() {
        return question;
    }

    public List<String> getAnswers_list() {
        return answers_list;
    }
}

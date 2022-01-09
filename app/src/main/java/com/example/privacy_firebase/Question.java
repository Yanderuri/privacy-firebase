package com.example.privacy_firebase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Question {
    private final String question;
    private final List<String> answers_list;

    public Question(String question, String ...answers_choices) {
        this.question = question;
        this.answers_list = Arrays.asList(answers_choices);
    }

    public String getQuestion() {
        return question;
    }

    public List<String> getAnswers_list() {
        return answers_list;
    }
}

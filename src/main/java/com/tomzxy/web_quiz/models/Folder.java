package com.tomzxy.web_quiz.models;

import com.tomzxy.web_quiz.models.Host.QuestionBank;
import com.tomzxy.web_quiz.models.User.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Folder extends BaseEntity{
    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_id", nullable = false)
    private QuestionBank bank;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Folder parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Folder> children = new ArrayList<>();

    @OneToMany(mappedBy = "folder")
    private List<Question> questions = new ArrayList<>();

    // Helper methods để đồng bộ cả hai chiều
    public void addSubfolder(Folder  sub) {
        children.add(sub);
        sub.setParent(this);
    }

    public void removeSubfolder(Folder sub) {
        children.remove(sub);
        sub.setParent(null);
    }

    public void addQuestion(Question question) {
        questions.add(question);
        question.setFolder(this);
    }

    public void removeQuestion(Question question) {
        questions.remove(question);
        question.setFolder(null);
    }
}

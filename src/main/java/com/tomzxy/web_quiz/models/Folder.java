package com.tomzxy.web_quiz.models;

import com.tomzxy.web_quiz.models.Host.QuestionBank;
import com.tomzxy.web_quiz.models.User.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
public class Folder extends BaseEntity{
    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id", nullable = false)
    private User host;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Folder parentFolder; // hỗ trợ folder lồng nhau

    @OneToMany(mappedBy = "parentFolder", cascade = CascadeType.ALL)
    private List<Folder> subFolders = new ArrayList<>();


    @OneToMany(mappedBy = "folder", cascade = CascadeType.ALL)
    private List<QuestionBank> hostQuestions = new ArrayList<>();
}

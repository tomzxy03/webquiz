package com.tomzxy.web_quiz.models;

import java.sql.Date;
import java.util.List;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Quiz extends BaseEntity {
    private String title;
    private String description;
    private int totalQuestion;

    @ManyToOne
    @JoinColumn(name = "host_id")
    private User host;

    @ManyToOne
    private Lobby group;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL)
    private List<QuizQuestion> questions;


    
}

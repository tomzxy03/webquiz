package com.tomzxy.web_quiz.models;


import com.tomzxy.web_quiz.enums.Level;
import com.tomzxy.web_quiz.enums.QuestionAndAnswerType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Table(name = "Question")
public class Question extends BaseEntity{

    @Column(name = "question_name", unique = true)
    String questionName;

    @Enumerated(EnumType.STRING)
    @Column(name = "question_type")
    QuestionAndAnswerType questionType;

    @Column(name = "level")
    @Enumerated(EnumType.STRING)
    Level level;

    @ManyToOne(fetch = FetchType.LAZY)
    Subject subject;

    @OneToMany(mappedBy = "question",cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    Set<Answer> answers =  new HashSet<>();
}

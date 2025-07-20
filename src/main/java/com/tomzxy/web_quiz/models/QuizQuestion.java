package com.tomzxy.web_quiz.models;

import lombok.*;
import jakarta.persistence.*;
import lombok.experimental.FieldDefaults;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Table(name = "QuizQuestion")
public class QuizQuestion extends BaseEntity {

    String question_text;
    Boolean is_custom;

    
    
}

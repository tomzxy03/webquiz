package com.tomzxy.web_quiz.models;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "subject")
@Builder
public class Subject extends BaseEntity{

    @Column(name = "subject_name")
    String subjectName;

    @Column(name = "description")
    String description;
}

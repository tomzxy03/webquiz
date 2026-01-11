package com.tomzxy.web_quiz.models.Host;


import com.tomzxy.web_quiz.models.BaseEntity;
import com.tomzxy.web_quiz.models.Question;
import com.tomzxy.web_quiz.models.User.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.List;


import java.util.ArrayList;
import java.util.Set;


@Entity
@Table(name = "question_banks",
        indexes = {
                @Index(name = "idx_bank_owner", columnList = "owner_id")
        })
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionBank extends BaseEntity{

    // Thuộc về Host nào
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false, unique = true)
    private User owner;

    // Các folders trong bank này
    @OneToMany(mappedBy = "bank", cascade = {CascadeType.MERGE,CascadeType.PERSIST}, orphanRemoval = true)
    @Builder.Default
    private List<QuestionFolder> folders = new ArrayList<>();

    // Các questions trong bank này (cả có folder và không folder)
    @OneToMany(mappedBy = "bank", cascade = {CascadeType.MERGE,CascadeType.PERSIST}, orphanRemoval = true)
    @Builder.Default
    private Set<Question> questions = new HashSet<>();


}

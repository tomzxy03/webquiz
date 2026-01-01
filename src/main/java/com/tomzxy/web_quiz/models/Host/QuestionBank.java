package com.tomzxy.web_quiz.models.Host;


import com.tomzxy.web_quiz.models.BaseEntity;
import com.tomzxy.web_quiz.models.Question;
import com.tomzxy.web_quiz.models.User.User;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;


import java.util.ArrayList;


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

    @Column(nullable = false)
    private String name;  // "My Question Bank"

    @Column(columnDefinition = "TEXT")
    private String description;

    // Các folders trong bank này
    @OneToMany(mappedBy = "bank", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<QuestionFolder> folders = new ArrayList<>();

    // Các questions trong bank này (cả có folder và không folder)
    @OneToMany(mappedBy = "bank", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Question> questions = new ArrayList<>();


}

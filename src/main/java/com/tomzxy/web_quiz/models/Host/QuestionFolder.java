package com.tomzxy.web_quiz.models.Host;


import com.tomzxy.web_quiz.models.BaseEntity;
import com.tomzxy.web_quiz.models.Question;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "question_folders",
        indexes = {
                @Index(name = "idx_folder_bank", columnList = "bank_id"),
                @Index(name = "idx_folder_parent", columnList = "parent_id"),
                @Index(name = "idx_folder_bank_parent", columnList = "bank_id, parent_id")
        }
)

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionFolder extends BaseEntity {

    // Thuộc về Question Bank nào
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_id", nullable = false)
    private QuestionBank bank;

    // Support nested folders (folder trong folder)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private QuestionFolder parent;  // null = root folder

    @Column(nullable = false)
    private String name;  // "Java Basics", "OOP", etc.

    @Column(columnDefinition = "TEXT")
    private String description;

    // Subfolder của folder này
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<QuestionFolder> subfolders = new ArrayList<>();

    // Questions trong folder này
    @OneToMany(mappedBy = "folder", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Question> questions = new ArrayList<>();


}

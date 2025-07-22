package com.tomzxy.web_quiz.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
@Getter
@Setter 
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "quiz_result")
public class QuizResult extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    @JsonIgnore
    private Quiz quiz;

    @Column(name = "score")
    private int score;

    

    @Column(name = "total_corrected")   
    private int totalCorrected;

    @Column(name = "total_failed")
    private int totalFailed;

    @Column(name = "total_skipped")
    private int totalSkipped;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @OneToMany(mappedBy = "quizResult", cascade = CascadeType.ALL)
    private List<QuizAttempt> attempts;
}

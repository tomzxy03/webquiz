package com.tomzxy.web_quiz.models;

import java.util.List;
import java.util.Set;

import com.tomzxy.web_quiz.models.Quiz.Quiz;

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
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "lobbies")
public class Lobby extends BaseEntity{
    @Column(name = "lobby_name")
    private String lobbyName;

    @Column(name = "host_name")
    private String hostName;

    @ManyToMany
    @JoinTable(name = "lobby_user",
        joinColumns = @JoinColumn(name = "lobby_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> members;

    @OneToMany(mappedBy = "lobby")
    private List<Quiz> quizzes;
}

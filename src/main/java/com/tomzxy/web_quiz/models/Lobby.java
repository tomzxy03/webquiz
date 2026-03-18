package com.tomzxy.web_quiz.models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.tomzxy.web_quiz.models.Host.LobbyMember;
import com.tomzxy.web_quiz.models.NotificationUser.Notification;
import com.tomzxy.web_quiz.models.Quiz.Quiz;

import com.tomzxy.web_quiz.models.User.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;



@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "lobbies")
public class Lobby extends BaseEntity{
    @Column(name = "lobby_name")
    private String lobbyName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id")
    private User host;

    @Column(name = "code_invite")
    private String codeInvite;

    @OneToMany(mappedBy = "lobby", cascade = CascadeType.ALL)
    private Set<LobbyMember> members = new HashSet<>();

    @OneToMany(mappedBy = "lobby")
    private List<Quiz> quizzes = new ArrayList<>();

    @OneToMany(mappedBy = "lobby")
    private List<Notification> notifications = new ArrayList<>();


}

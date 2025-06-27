package com.tomzxy.web_quiz.models;


import com.tomzxy.web_quiz.enums.Gender;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Builder
@Getter
@Setter
@Table(name = "user")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User extends BaseEntity {
    @Column(name = "user_name")
    String user_name;

    @Column(name = "phone")
    String phone;

    @Column(name = "email")
    String email;

    @Column(name = "gender")
    Gender gender;

    @Column(name = "dob")
    Date dob;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"),
                inverseJoinColumns = @JoinColumn(name = "role_id"))
    Set<Role> roles = new HashSet<>();
}

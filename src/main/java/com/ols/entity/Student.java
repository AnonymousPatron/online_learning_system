package com.ols.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@Entity
@Getter
@Setter
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "users_id", referencedColumnName = "id")
    private Users users;

    @ManyToMany(mappedBy = "students")
    private List<Course> enrolledCourses = new ArrayList<>();

    @Builder
    public Student(Users users) {
        this.users = users;
    }

}

package com.example.taskmanager.entity;

import com.example.taskmanager.enums.Gender;
import com.example.taskmanager.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;


@Entity
@Audited
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Audited
    private Long id;

    @Column(name = "username")
    @Audited
    private String username;

    @NotAudited
    @Column(name = "email")
    private String email;

    @NotAudited
    @Column(name = "full_name")
    private String fullName;

    @NotAudited
    @Column(name = "dob")
    private LocalDate dob;

    @NotAudited
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @NotAudited
    @Column(name = "phone")
    private String phone;

    @NotAudited
    @Column(name = "password")
    private String password;

    @NotAudited
    @Column(name = "address")
    private String address;

    @NotAudited
    @Column(name = "image_url")
    private String imageUrl;

    @NotAudited
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    @CollectionTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "role"})
    )
    private Set<Role> roles;

    @NotAudited
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProjectMember> projectMemberships;

    @NotAudited
    @OneToMany(mappedBy = "assignee", fetch = FetchType.LAZY)
    private List<Task> myTasks;

    @Audited
    @Column(name = "enabled")
    private boolean enabled = true; // Defaulting to true for existing users, will set to false for new ones

    @Audited
    @Column(name = "verified")
    private boolean verified = false;
}
package com.example.taskmanager.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Audited
@Table(name = "project")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Audited
    private Long id;

    @Column(name = "name")
    @Audited
    private String name;

    @Column(name = "description")
    @Audited
    private String description;

    @CreationTimestamp
    @Column(name = "created_at")
    @NotAudited
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private User createdBy;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @NotAudited
    private List<Task> tasks;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @NotAudited
    private List<ProjectMember> members;
}
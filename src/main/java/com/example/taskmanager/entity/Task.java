package com.example.taskmanager.entity;

import com.example.taskmanager.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;

import java.time.LocalDateTime;

@Entity
@Audited
@Table(name = "task")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Audited
    private Long id;

    @Column(name = "title")
    @Audited
    private String title;

    @Column(name = "description")
    @Audited
    private String description;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    @Audited
    private TaskStatus status;

    @Column(name = "deadline")
    @Audited
    private LocalDateTime deadline;

    @CreationTimestamp
    @Column(name = "created_at")
    @NotAudited
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id")
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private User assignee;

    @Version
    @NotAudited
    private Long version;
}


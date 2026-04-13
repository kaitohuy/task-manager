package com.example.taskmanager.entity;

import com.example.taskmanager.enums.ProjectRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.envers.*;

import java.time.LocalDateTime;

@Entity
@Audited
@Table(name = "project_member")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Audited
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private Project project;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    @Audited
    private ProjectRole role;

    @CreationTimestamp
    @Column(name = "joined_at")
    @NotAudited
    private LocalDateTime joinedAt;
}
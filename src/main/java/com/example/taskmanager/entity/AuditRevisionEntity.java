package com.example.taskmanager.entity;

import com.example.taskmanager.config.audit.AuditRevisionListener;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

@Entity
@Table(name = "revinfo")
@RevisionEntity(AuditRevisionListener.class)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuditRevisionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "revinfo_seq_gen")
    @SequenceGenerator(
            name        = "revinfo_seq_gen",
            sequenceName = "revinfo_seq",
            allocationSize = 1
    )
    @RevisionNumber
    private long id;

    @RevisionTimestamp
    @Column(name = "timestamp", nullable = false)
    private long timestamp;

    @Column(name = "username")
    private String username;

    @Column(name = "ip_address")
    private String ipAddress;
}
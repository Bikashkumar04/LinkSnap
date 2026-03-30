package com.bikash.LinkSnap.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "domains",
        indexes = {
                @Index(name = "idx_domains_workspace_id", columnList = "workspace_id")
        }
)
public class Domain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "workspace_id", nullable = false)
    private Long workspaceId;

    @Column(nullable = false, unique = true, length = 255)
    private String host;

    @Column(name = "is_primary", nullable = false)
    private boolean isPrimary;

    @Column(name = "verification_status", nullable = false, length = 40)
    private String verificationStatus = "PENDING";

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}

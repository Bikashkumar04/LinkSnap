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
        name = "tags",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_tags_workspace_name", columnNames = {"workspace_id", "name"})
        },
        indexes = {
                @Index(name = "idx_tags_workspace_id", columnList = "workspace_id")
        }
)
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "workspace_id", nullable = false)
    private Long workspaceId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 30)
    private String color;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}

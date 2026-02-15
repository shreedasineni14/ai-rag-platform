package com.rag.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    private Integer version;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}

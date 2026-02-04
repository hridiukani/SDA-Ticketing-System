package com.sda.ticketing.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "sla_policies")
public class SlaPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private int targetResolutionHours;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Priority applicablePriority;

    @Column(nullable = false)
    private boolean active = true;

    public SlaPolicy() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTargetResolutionHours() {
        return targetResolutionHours;
    }

    public void setTargetResolutionHours(int targetResolutionHours) {
        this.targetResolutionHours = targetResolutionHours;
    }

    public Priority getApplicablePriority() {
        return applicablePriority;
    }

    public void setApplicablePriority(Priority applicablePriority) {
        this.applicablePriority = applicablePriority;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}

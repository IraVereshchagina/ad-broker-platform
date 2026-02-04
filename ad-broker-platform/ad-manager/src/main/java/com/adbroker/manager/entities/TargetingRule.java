package com.adbroker.manager.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "targeting_rules")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TargetingRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id", nullable = false)
    @ToString.Exclude
    private Campaign campaign;

    @Column(nullable = false)
    private String attribute;

    @Column(nullable = false)
    private String operator;

    @Column(name = "rule_value", nullable = false)
    private String ruleValue;
}
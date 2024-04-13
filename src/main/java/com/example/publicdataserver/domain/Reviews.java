package com.example.publicdataserver.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Reviews {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String authorName;
    private String profilePhotoUrl;
    private Integer rating;
    private String relativeTimeDescription;

    @Column(columnDefinition = "TEXT")
    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;
}

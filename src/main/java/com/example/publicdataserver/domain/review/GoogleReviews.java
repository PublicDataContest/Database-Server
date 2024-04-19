package com.example.publicdataserver.domain.review;

import com.example.publicdataserver.domain.restaurant.Restaurant;
import com.example.publicdataserver.domain.users.Users;
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
public class GoogleReviews {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String authorName; // 작성자 이름
    private Integer rating; // 별점
    private String relativeTimeDescription; // 작성 시간

    @Column(columnDefinition = "TEXT")
    private String text; // 리뷰 내용

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

}

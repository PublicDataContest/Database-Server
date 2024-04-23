package com.example.publicdataserver.repository;

import com.example.publicdataserver.domain.review.KakaoReviews;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KakaoReviewsRepository extends JpaRepository<KakaoReviews, Long> {
}

package com.example.publicdataserver.repository;

import com.example.publicdataserver.domain.review.GoogleReviews;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewsRepository extends JpaRepository<GoogleReviews, Long> {

}

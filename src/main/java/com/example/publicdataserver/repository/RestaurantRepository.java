package com.example.publicdataserver.repository;

import com.example.publicdataserver.domain.restaurant.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    @Query("SELECT r FROM Restaurant r WHERE r.execLoc = :execLoc")
    List<Restaurant> findRestaurantsByExecLoc(@Param("execLoc") String execLoc);

    @Modifying
    @Query("UPDATE Restaurant r SET r.priceModel = true WHERE r.phone IN :telNos")
    void updateRestaurantPriceModelInBatch(@Param("telNos") List<String> telNos);
}

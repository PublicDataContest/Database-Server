package com.example.publicdataserver.repository;

import com.example.publicdataserver.domain.restaurant.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
}

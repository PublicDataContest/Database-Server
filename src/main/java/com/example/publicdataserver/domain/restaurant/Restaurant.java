package com.example.publicdataserver.domain.restaurant;

import com.example.publicdataserver.domain.review.GoogleReviews;
import com.example.publicdataserver.domain.users.WishListRestaurant;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String execLoc;
    private String addressName;
    private String phone;
    private String placeName;
    private String placeUrl;

    private String x;
    private String y;

    private Integer rating;

    @Column(columnDefinition = "TEXT")
    private String currentOpeningHours;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GoogleReviews> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Category> categories = new ArrayList<>();

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Menu> menus = new ArrayList<>();

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WishListRestaurant> wishListRestaurants = new ArrayList<>();

}

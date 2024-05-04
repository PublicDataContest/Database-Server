package com.example.publicdataserver.domain.restaurant;

import com.example.publicdataserver.domain.review.KakaoReviews;
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
    private String longText;

    @Column(columnDefinition = "TEXT")
    private String photoUrl;

    private String x;
    private String y;

    private Double rating;
    private String storeId;

    private Long totalExecAmounts;
    private Long numberOfVisit;

    @Column(columnDefinition = "TEXT")
    private String currentOpeningHours;

    @Column(name = "priceModel", columnDefinition = "boolean default false")
    private boolean priceModel; // 착한가게

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<KakaoReviews> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Category> categories = new ArrayList<>();

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Menu> menus = new ArrayList<>();

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WishListRestaurant> wishListRestaurants = new ArrayList<>();

}

package com.favoriteplace.app.item.domain;

import com.favoriteplace.app.domain.Image;
import com.favoriteplace.app.domain.common.BaseTimeEntity;
import com.favoriteplace.app.domain.enums.ItemCategory;
import com.favoriteplace.app.domain.enums.ItemType;
import com.favoriteplace.app.domain.enums.SaleStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class Item extends BaseTimeEntity{

    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "item_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id", nullable = false)
    private Image defaultImage;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "center_image_id")
    private Image centerImage;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SaleStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemCategory category;

    private LocalDateTime saleDeadline;

    @Column(nullable = false)
    private long point;

    @Column(nullable = false)
    private String description;

}

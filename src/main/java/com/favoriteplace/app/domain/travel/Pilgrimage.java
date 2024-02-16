package com.favoriteplace.app.domain.travel;

import com.favoriteplace.app.domain.Image;
import com.favoriteplace.app.domain.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class Pilgrimage extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pilgrimage_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rally_id", nullable = false)
    private Rally rally;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "virtual_image_id", nullable = false)
    private Image virtualImage;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "real_image_id", nullable = false)
    private Image realImage;

    @Column(name = "rally_name", nullable = false)
    private String rallyName;

    @Column(name = "detail_address", nullable = false)
    private String detailAddress;

    @Column(name = "detail_address_en", nullable = false)
    private String detailAddressEn;

    @Column(name = "detail_address_jp", nullable = false)
    private String detailAddressJp;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

}

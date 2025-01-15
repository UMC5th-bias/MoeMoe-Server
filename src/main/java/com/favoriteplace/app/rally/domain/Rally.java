package com.favoriteplace.app.rally.domain;

import com.favoriteplace.app.common.domain.Image;
import com.favoriteplace.app.item.domain.Item;
import com.favoriteplace.app.common.domain.BaseTimeEntity;

import com.favoriteplace.app.pilgrimage.domain.Pilgrimage;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

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
public class Rally extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rally_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id", nullable = false)
    private Image image;

    @Column(nullable = false)
    private String name;  //애니메이션 이름

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private long achieveNumber;  //달성한 사람 수

    @Column(nullable = false)
    private long pilgrimageNumber; //해당 랠리의 성지 순례 갯수

    @OneToMany(mappedBy = "rally")
    private List<Pilgrimage> pilgrimages;

    public void addPilgrimage(){
        this.pilgrimageNumber += 1;
    }
    public void addAchieveNumber() { this.achieveNumber += 1; }
}

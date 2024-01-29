package com.favoriteplace.app.domain;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import com.favoriteplace.app.domain.common.BaseTimeEntity;
import com.favoriteplace.app.domain.enums.LoginType;
import com.favoriteplace.app.domain.enums.MemberStatus;
import com.favoriteplace.app.domain.item.Item;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
@Entity
public class Member extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "profile_title_id")
    private Item profileTitle;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "profile_icon_id")
    private Item profileIcon;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String birthday;

    @Column(nullable = false)
    private String nickname;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String profileImageUrl;

    @Enumerated(STRING)
    @Column(nullable = false)
    private MemberStatus status;

    @Column(nullable = false)
    private Boolean alarmAllowance;  //true: 허용, false: 허용x

    @Column(nullable = false)
    private Long point;

    @Enumerated(STRING)
    @Column(nullable = false)
    private LoginType loginType;

    private String refreshToken;

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void updatePoint(Long point) {
        this.point += point;
    }
}

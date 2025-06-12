package com.favoriteplace.app.member.domain;

import com.favoriteplace.app.common.domain.BaseTimeEntity;
import com.favoriteplace.app.member.domain.enums.LoginType;
import com.favoriteplace.app.member.domain.enums.MemberStatus;
import com.favoriteplace.app.item.domain.Item;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
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

    @Column(nullable = false, unique = true)
    private String email;

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
    private boolean alarmAllowance;  //true: 허용, false: 허용x

    @Column(nullable = false)
    private long point;

    @Enumerated(STRING)
    @Column(nullable = false)
    private LoginType loginType;

    private String refreshToken;

    private String fcmToken;

    public static Member create(
            String nickname, String email,
            boolean snsAllow, String introduction,
            String profileImage, Item titleItem, LoginType loginType)
    {
        return Member.builder()
                .nickname(nickname)
                .email(email)
                .alarmAllowance(snsAllow)
                .description(introduction)
                .profileImageUrl(profileImage)
                .point(0L)
                .loginType(loginType)
                .profileTitle(titleItem)
                .status(MemberStatus.Y)
                .build();
    }

    public void updatePassword(String password) { this.password = password; }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void deleteRefreshToken(String refreshToken) {
        this.refreshToken = null;
    }

    public void updatePoint(Long point) {
        this.point += point;
    }

    public void updatePointWhenBuyItem(Long point) {
        this.point = point;
    }

    public void updateIcon(Item icon) {
        this.profileIcon = icon;
    }

    public void updateTitle(Item title) {
        this.profileTitle = title;
    }
    public void refreshFcmToken(String fcmToken){this.fcmToken = fcmToken;}
}

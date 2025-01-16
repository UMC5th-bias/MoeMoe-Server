package com.favoriteplace.app.notification.domain;

import com.favoriteplace.app.member.domain.Member;
import com.favoriteplace.app.common.domain.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;import lombok.AllArgsConstructor;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class Notification extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String title;

    private String content;

    private Long postId;

    private Long guestBookId;

    private Long rallyId;

    private Boolean isRead;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    private Notification(String type, String title, String content, Long postId, Long guestBookId, Long rallyId, Member member){
        this.type = type;
        this.title = title;
        this.content = content;
        this.postId = postId;
        this.guestBookId = guestBookId;
        this.rallyId = rallyId;
        this.isRead = false;
        this.member = member;
    }

    public void readNotification(){this.isRead = true;}
}

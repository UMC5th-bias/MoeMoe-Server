package com.favoriteplace.app.domain.community;

import com.favoriteplace.app.domain.Image;
import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.domain.common.BaseTimeEntity;
import com.favoriteplace.app.domain.travel.Pilgrimage;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
@Entity
public class GuestBook extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "guestbook_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "pilgrimage_id", nullable = false)
    private Pilgrimage pilgrimage;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private Long likeCount;

    @Column(nullable = false)
    private Long view;

    private Double latitude;  //위도
    private Double longitude;  //경도

    @OneToMany(mappedBy = "guestBook", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    List<HashTag> hashTags = new ArrayList<>();

    @OneToMany(mappedBy = "guestBook", cascade = CascadeType.ALL)
    @Builder.Default
    List<Image> images = new ArrayList<>();

    @OneToMany(mappedBy = "guestBook", cascade = CascadeType.ALL)
    @Builder.Default
    List<Comment> comments = new ArrayList<>();

    public void setTitle(String title){this.title = title;}
    public void setContent(String content){this.content = content;}
    public void increaseView(){this.view++;}

    public void setHashTag(HashTag hashTag){
        hashTag.setGuestBook(this);
        this.hashTags.add(hashTag);
    }

    public void setImage(Image image){
        image.setGuestBook(this);
        this.images.add(image);
    }

    public void addComment(Comment comment){
        comment.setGuestBook(this);
        this.comments.add(comment);
    }
}

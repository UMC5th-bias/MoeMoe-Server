package com.favoriteplace.app.domain.community;

import com.favoriteplace.app.domain.Image;
import com.favoriteplace.app.member.domain.Member;
import com.favoriteplace.app.domain.common.BaseTimeEntity;
import com.favoriteplace.app.domain.travel.Pilgrimage;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
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
    private long likeCount;

    @Column(nullable = false)
    private long view;

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
    public void decreaseView(){this.view--;}

    public void setHashTag(HashTag hashTag){
        hashTag.setGuestBook(this);
        this.hashTags.add(hashTag);
    }

    public void setImage(Image image){
        image.setGuestBook(this);
        this.images.add(image);
    }

    public void setImages(List<Image> images){
        images.stream().forEach(image->{
            image.setGuestBook(this);
        });
        this.images.addAll(images);
    }

    public void addComment(Comment comment){
        comment.setGuestBook(this);
        this.comments.add(comment);
    }

    public void addImages(List<String> imageUrls){
        for(String imageUrl:imageUrls){
            Image newImage = Image.builder()
                    .guestBook(this)
                    .url(imageUrl)
                    .build();
            this.images.add(newImage);
        }
    }

}

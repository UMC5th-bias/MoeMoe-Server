package com.favoriteplace.app.domain.community;

import com.favoriteplace.app.domain.Image;
import com.favoriteplace.app.member.domain.Member;
import com.favoriteplace.app.domain.common.BaseTimeEntity;

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
public class Post extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private long likeCount;

    @Column(nullable = false)
    private long view;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Image> images = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    public void setTitle(String title) {this.title = title;}

    public void setContent(String content) {this.content = content;}

    public void increaseView(){this.view++;}

    public void increaseLikeCount(){this.likeCount++;}

    public void decreaseLikeCount(){this.likeCount--;}

    public void addComment(Comment comment){
        comment.setPost(this);
        this.comments.add(comment);
    }

    public void disconnectImages(){
        for(Image image:this.images){
            image.setPost(null);
        }
        this.images = new ArrayList<>();
    }

    public void addImages(List<String> imageUrls){
        for(String imageUrl:imageUrls){
            Image newImage = Image.builder()
                    .post(this)
                    .url(imageUrl)
                    .build();
            this.images.add(newImage);
        }
    }

}

package com.favoriteplace.app.domain;

import com.favoriteplace.app.domain.community.GuestBook;
import com.favoriteplace.app.domain.community.Post;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

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
public class Image {

    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "image_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "guestbook_id")
    private GuestBook guestBook;

    @Column(nullable = false)
    private String url;

    public void setPost(Post post) {
        this.post = post;
    }
    public void setGuestBook(GuestBook guestBook){this.guestBook = guestBook;}
}

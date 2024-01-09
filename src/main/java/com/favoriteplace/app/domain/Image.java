package com.favoriteplace.app.domain;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import com.favoriteplace.app.domain.community.GuestBook;
import com.favoriteplace.app.domain.community.Post;
import com.favoriteplace.app.domain.travel.Pilgrimage;
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

@Getter
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
@Entity
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

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "pilgrimage_id")
    private Pilgrimage pilgrimage;

    @Column(nullable = false)
    private String url;

}

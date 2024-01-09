package com.favoriteplace.app.domain.community;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import com.favoriteplace.app.domain.community.GuestBook;
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
public class HashTag {

    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "hashtag_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "guest_book_id", nullable = false)
    private GuestBook guestBook;

    @Column(nullable = false)
    private String tagName;
}

package com.favoriteplace.app.domain.community;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.domain.common.BaseTimeEntity;
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

@Getter
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
@Entity
public class Comment extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "guest_book_id")
    private GuestBook guestBook;

    @Column(nullable = false)
    private String content;

    public void setGuestBook(GuestBook guestBook) {
        this.guestBook = guestBook;
    }

    public void setPost(Post post){
        this.post = post;
    }

    public void setContent(String content){this.content = content;}
}

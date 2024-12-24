package com.favoriteplace.app.domain.community;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.domain.common.BaseTimeEntity;
import com.favoriteplace.app.domain.enums.CommentType;

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

@Entity
@Getter
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
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

    @Enumerated(EnumType.STRING)
    private CommentType commentType;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "parent_comment_id")
    private Comment parentComment;  // 최상위 부모 댓글

    @OneToMany(mappedBy = "parentComment")
    private List<Comment> childComments = new ArrayList<>();

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "reference_comment_id")
    private Comment referenceComment; // 대댓글 내에서 어떤 댓글을 참조한 것인지 (최상위 댓글을 참조하는 경우는 포함 X)

    @Column(nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    public void setGuestBook(GuestBook guestBook) {this.guestBook = guestBook;}
    public void setPost(Post post){this.post = post;}

    public void addParentComment(Comment parentComment){
        this.parentComment = parentComment;
        parentComment.getChildComments().add(this);
    }

    public void setReferenceComment(Comment referenceComment){this.referenceComment = referenceComment;}
    public void setCommentType(CommentType commentType){this.commentType = commentType;}
    public void modifyContent(String content){this.content = content;}
    public void softDeleteComment(){
        this.isDeleted = true;
        this.content = "[삭제된 댓글입니다.]";
    }
}

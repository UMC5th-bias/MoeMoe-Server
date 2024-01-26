package com.favoriteplace.app.service;

import com.favoriteplace.app.converter.CommentConverter;
import com.favoriteplace.app.converter.PostConverter;
import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.domain.community.Comment;
import com.favoriteplace.app.domain.community.Post;
import com.favoriteplace.app.dto.community.CommentResponseDto;
import com.favoriteplace.app.dto.community.GuestBookResponseDto;
import com.favoriteplace.app.dto.community.PostResponseDto;
import com.favoriteplace.app.repository.CommentRepository;
import com.favoriteplace.app.repository.PostRepository;
import com.favoriteplace.global.exception.ErrorCode;
import com.favoriteplace.global.exception.RestApiException;
import com.favoriteplace.global.util.DateTimeFormatUtils;
import com.favoriteplace.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CountComments countComments;
    private final MemberService memberService;
    private final SecurityUtil securityUtil;

    //TODO 여기 수정 필요 (isWrite 수정 -> 내가 이 댓글을 작성자인지 확인하는 기능)
    public List<CommentResponseDto.PostComment> getPostComments
            (int page, int size, Long postId) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Comment> commentPage = commentRepository.findAllByPostIdOrderByCreatedAtAsc(postId, pageable);
        if(commentPage.isEmpty()){
            return Collections.emptyList();
        }
        List<CommentResponseDto.PostComment> comments = new ArrayList<>();
        for(Comment comment:commentPage.getContent()){
            comments.add(CommentResponseDto.PostComment.builder()
                            .userInfo(memberService.getUserInfoByPostId(postId))
                            .id(comment.getId())
                            .content(comment.getContent())
                            .passedTime(DateTimeFormatUtils.getPassDateTime(comment.getCreatedAt()))
                            .isWrite(isPostCommentWriter(postId, comment)).build()
            );
        }
        return comments;
    }

    /**
     * 커뷰니티 : 성지순례 인증글의 댓글 전부 보여주기 (페이징 적용)
     * @param page
     * @param size
     * @param member
     * @param guestbookId
     * @return 페이징 된 댓글 리스트
     */
    public Page<CommentResponseDto.PostComment> getGuestBookComments(int page, int size, Member member, Long guestbookId) {
        Pageable pageable = PageRequest.of(page-1, size);
        Page<Comment> commentPage = commentRepository.findAllByGuestBookIdOrderByCreatedAtAsc(guestbookId, pageable);
        if(commentPage.isEmpty()){return Page.empty();}
        return commentPage.map(comment -> CommentConverter.toPostComment(comment, member, isCommentWriter(member, comment)));
    }

    public List<PostResponseDto.MyComment> getMyPostComments(int page, int size) {
        Member member = securityUtil.getUser();
        Pageable pageable = PageRequest.of(page, size);
        Page<Comment> pageComment = commentRepository.findAllByMemberIdAndPostIsNotNullAndGuestBookIsNullOrderByCreatedAtDesc(member.getId(), pageable);
        if(pageComment.isEmpty()){
            return Collections.emptyList();
        }
        List<PostResponseDto.MyComment> myComments = new ArrayList<>();
        for(Comment comment: pageComment.getContent()){
            Post post = comment.getPost();
            Long comments = countComments.countPostComments(post.getId());
            myComments.add(
                    PostResponseDto.MyComment.builder()
                            .id(comment.getId())
                            .content(comment.getContent())
                            .passedTime(DateTimeFormatUtils.getPassDateTime(comment.getCreatedAt()))
                            .post(PostConverter.toMyPost(post, member, comments))
                            .build()
            );
        }
        return myComments;
    }

    public Page<GuestBookResponseDto.MyGuestBookComment> getMyGuestBookComments(int page, int size) {
        Member member = securityUtil.getUser();
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Comment> pageComment = commentRepository.findAllByMemberIdAndPostIsNullAndGuestBookIsNotNullOrderByCreatedAtDesc(member.getId(), pageable);
        if(pageComment.isEmpty()){return Page.empty();}
        return pageComment.map(comment -> CommentConverter.toMyGuestBookComment(comment, countComments.countGuestBookComments(comment.getGuestBook().getId())));
    }

    //TODO : 이거 삭제 필요
    private Boolean isPostCommentWriter(Long postId, Comment comment){
        Optional<Post> optionalPost = postRepository.findById(postId);
        if(optionalPost.isEmpty()){
            throw new RestApiException(ErrorCode.POST_NOT_FOUND);
        }
        return optionalPost.get().getMember().getId().equals(comment.getMember().getId());
    }

    /**
     * 사용자(앱을 사용하는 유저)가 댓글 작성자가 맞는지 확인하는 함수
     * @param member
     * @param comment
     * @return 댓글 작성자가 맞으면 true, 아니면 false
     */
    private Boolean isCommentWriter(Member member, Comment comment){
        if(member == null){return false;}
        return member.getId().equals(comment.getMember().getId());
    }

    public void createComment(long postId, String content) {
        Member member = securityUtil.getUser();
        Comment comment = Comment.builder()
                .member(member)
                .post(postRepository.findById(postId).orElseThrow(() -> new RestApiException(ErrorCode.POST_NOT_FOUND)))
                .content(content)
                .build();
        commentRepository.save(comment);
    }
}

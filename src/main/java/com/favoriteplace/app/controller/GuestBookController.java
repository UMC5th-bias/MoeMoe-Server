package com.favoriteplace.app.controller;

import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.dto.community.GuestBookRequestDto;
import com.favoriteplace.app.dto.community.GuestBookResponseDto;
import com.favoriteplace.app.dto.community.PostResponseDto;
import com.favoriteplace.app.service.community.GuestBookCommandService;
import com.favoriteplace.app.service.community.GuestBookQueryService;
import com.favoriteplace.app.service.community.LikedPostService;
import com.favoriteplace.global.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/posts/guestbooks")
@RequiredArgsConstructor
public class GuestBookController {
    private final GuestBookQueryService guestBookQueryService;
    private final GuestBookCommandService guestBookCommandService;
    private final LikedPostService likedPostService;
    private final SecurityUtil securityUtil;

    @GetMapping()
    public GuestBookResponseDto.TotalGuestBookDto getTotalGuestBooks(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "latest") String sort
    ){
        List<GuestBookResponseDto.TotalGuestBookInfo> guestBookInfos = guestBookQueryService.getTotalGuestBooksBySort(page, size, sort);
        return GuestBookResponseDto.TotalGuestBookDto.builder()
                .page((long)page)
                .size((long)size)
                .guestBook(guestBookInfos)
                .build();
    }

    @GetMapping("/search")
    public GuestBookResponseDto.TotalGuestBookDto getTotalGuestBookByKeyword(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam() String searchType,
            @RequestParam() String keyword
    ){
        List<GuestBookResponseDto.TotalGuestBookInfo> guestBooks = guestBookQueryService.getTotalPostByKeyword(page, size, searchType, keyword);
        return GuestBookResponseDto.TotalGuestBookDto.builder()
                .page((long) page)
                .size((long) size)
                .guestBook(guestBooks)
                .build();
    }


    @GetMapping("/my-posts")
    public GuestBookResponseDto.MyGuestBookDto getMyGuestBooks(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ){
        List<GuestBookResponseDto.MyGuestBookInfo> myGuestBooks = guestBookQueryService.getMyGuestBooks(page, size);
        return GuestBookResponseDto.MyGuestBookDto.builder()
                .page((long)page)
                .size((long)size)
                .myGuestBookInfo(myGuestBooks)
                .build();
    }

    @GetMapping("/{guestbook_id}")
    public GuestBookResponseDto.DetailGuestBookDto getDetailGuestBook(
            @PathVariable("guestbook_id") Long guestBookId,
            HttpServletRequest request
    ){
        Member member = securityUtil.getUserFromHeader(request);
        guestBookCommandService.increaseGuestBookView(guestBookId);
        return guestBookQueryService.getDetailGuestBookInfo(guestBookId, member);
    }

    @PatchMapping("/{guestbook_id}")
    public ResponseEntity<PostResponseDto.SuccessResponseDto> modifyGuestBook(
            @PathVariable("guestbook_id") Long guestbookId,
            @RequestPart GuestBookRequestDto.ModifyGuestBookDto data,
            @RequestPart(required = false) List<MultipartFile> images
    ) throws IOException {
        Member member = securityUtil.getUser();
        guestBookCommandService.modifyGuestBook(member, guestbookId, data, images);
        return new ResponseEntity<>(
                PostResponseDto.SuccessResponseDto.builder().message("성지순례 인증글을 성공적으로 수정했습니다.").build(),
                HttpStatus.OK
        );
    }

    @DeleteMapping("/{guestbook_id}")
    public ResponseEntity<PostResponseDto.SuccessResponseDto> deleteGuestBook(
            @PathVariable("guestbook_id") Long guestbookId
    ){
        Member member = securityUtil.getUser();
        guestBookCommandService.deleteGuestBook(member, guestbookId);
        return new ResponseEntity<>(
                PostResponseDto.SuccessResponseDto.builder().message("성지순례 인증글을 성공적으로 삭제했습니다.").build(),
                HttpStatus.OK
        );
    }

    @PostMapping("/{guestbook_id}/like")
    public ResponseEntity<PostResponseDto.SuccessResponseDto> modifyGuestBookLike(
            @PathVariable("guestbook_id") Long guestbookId
    ){
        Member member = securityUtil.getUser();
        String message = likedPostService.modifyGuestBookLike(member, guestbookId);
        return new ResponseEntity<>(
                PostResponseDto.SuccessResponseDto.builder().message(message).build(),
                HttpStatus.OK
        );
    }

    // 성지순례 장소 방문 인증글 작성하기
    @PostMapping("/{pilgrimage_id}")
    public PostResponseDto.SuccessResponseDto postToPilgrimage(
            @PathVariable("pilgrimage_id") Long pilgrimageId,
            @RequestPart GuestBookRequestDto.ModifyGuestBookDto data,
            @RequestPart(required = false) List<MultipartFile> images
    ) throws IOException {
        Member member = securityUtil.getUser();
        return guestBookCommandService.postGuestBook(member, pilgrimageId, data, images);
    }
}

package com.favoriteplace;

import com.favoriteplace.app.domain.Image;
import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.domain.community.GuestBook;
import com.favoriteplace.app.domain.community.Post;
import com.favoriteplace.app.domain.enums.ItemType;
import com.favoriteplace.app.domain.enums.LoginType;
import com.favoriteplace.app.domain.enums.MemberStatus;
import com.favoriteplace.app.domain.enums.SaleStatus;
import com.favoriteplace.app.domain.item.Item;
import com.favoriteplace.app.domain.travel.Address;
import com.favoriteplace.app.domain.travel.Pilgrimage;
import com.favoriteplace.app.domain.travel.Rally;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class InitDB {
    private final InitService initService;

    @PostConstruct
    public void init(){
        initService.initDB();
    }

    @Component
    @RequiredArgsConstructor
    @Transactional
    static class InitService {
        private final EntityManager em;

        public void initDB(){
            Image image1 = Image.builder()
                    .post(null).guestBook(null)
                    .url("imgIcon").build();
            Image image2 = Image.builder()
                    .post(null).guestBook(null)
                    .url("imgTitle").build();
            em.persist(image1); em.persist(image2);

            Item item1 = Item.builder()
                    .image(image1).name("icon1")
                    .status(SaleStatus.NOT_FOR_SALE).type(ItemType.ICON)
                    .saleDeadline(null).point(null)
                    .description(null).build();
            Item item2 = Item.builder()
                    .image(image2).name("title1")
                    .status(SaleStatus.NOT_FOR_SALE).type(ItemType.TITLE)
                    .saleDeadline(null).point(null)
                    .description(null).build();
            em.persist(item1); em.persist(item2);

            Member member = Member.builder()
                    .id(0L).profileIcon(item1)
                    .profileTitle(item2).email("aaa@email.com")
                    .password("1234").birthday(null)
                    .nickname("user1").description("hi")
                    .profileImageUrl("").status(MemberStatus.Y)
                    .alarmAllowance(false).point(0L)
                    .loginType(LoginType.SELF).refreshToken("")
                    .build();
            em.merge(member);

            Post post1 = Post.builder()
                    .id(0L).member(member)
                    .title("게시글1").content("abcdefg")
                    .likeCount(10L).view(10L).build();
            Post post2 = Post.builder()
                    .id(1L).member(member)
                    .title("게시글2").content("abcdefg")
                    .likeCount(15L).view(15L).build();
            Post post3 = Post.builder()
                    .id(2L).member(member)
                    .title("게시글3").content("abcdefg")
                    .likeCount(20L).view(20L).build();
            Post post4 = Post.builder()
                    .id(3L).member(member)
                    .title("게시글4").content("abcdefg")
                    .likeCount(20L).view(20L).build();
            Post post5 = Post.builder()
                    .id(4L).member(member)
                    .title("게시글5").content("abcdefg")
                    .likeCount(25L).view(25L).build();
            Post post6 = Post.builder()
                    .id(5L).member(member)
                    .title("게시글6").content("abcdefg")
                    .likeCount(30L).view(30L).build();
            Post post7 = Post.builder()
                    .id(6L).member(member)
                    .title("게시글7").content("abcdefg")
                    .likeCount(35L).view(35L).build();
            em.merge(post1); em.merge(post2); em.merge(post3); em.merge(post4); em.merge(post5); em.merge(post6); em.merge(post7);

            Address address = Address.builder().id(0L)
                    .state("도교").district("시부야구").build();
            em.merge(address);

            Rally rally1 = Rally.builder().id(0L)
                    .item(item1).image(image1).name("최애의 아이")
                    .description("환생한 내가 아이돌의 자녀??!!").achieveNumber(10L)
                    .pilgrimage_number(4L).build();
            Rally rally2 = Rally.builder().id(1L)
                    .item(item2).image(image2).name("날씨의 아이")
                    .description("비야 멈춰라!").achieveNumber(20L)
                    .pilgrimage_number(2L).build();
            em.merge(rally1); em.merge(rally2);

            Pilgrimage pilgrimage1 = Pilgrimage.builder()
                    .address(address).rally(rally1).virtualImage(image1).realImage(image1)
                    .rallyName("최애의 아이").detailAddress("스크램블 교차로1").latitude(1.1).longitude(1.1).build();
            Pilgrimage pilgrimage2 = Pilgrimage.builder()
                    .address(address).rally(rally2).virtualImage(image2).realImage(image2)
                    .rallyName("최애의 아이").detailAddress("스크램블 교차로2").latitude(1.1).longitude(1.1).build();
            em.merge(pilgrimage1); em.merge(pilgrimage2);

            GuestBook guestBook1 = GuestBook.builder().id(0L)
                    .member(member).pilgrimage(pilgrimage1)
                    .title("인증글1").content("인증글내용1").likeCount(5L).view(5L).latitude(1.1).longitude(1.1)
                    .build();
            GuestBook guestBook2 = GuestBook.builder().id(1L)
                    .member(member).pilgrimage(pilgrimage1)
                    .title("인증글2").content("인증글내용2").likeCount(10L).view(10L).latitude(1.1).longitude(1.1)
                    .build();
            GuestBook guestBook3 = GuestBook.builder()
                    .member(member).pilgrimage(pilgrimage1)
                    .title("인증글3").content("인증글내용3").likeCount(15L).view(15L).latitude(1.1).longitude(1.1)
                    .build();
            GuestBook guestBook4 = GuestBook.builder()
                    .member(member).pilgrimage(pilgrimage2)
                    .title("인증글4").content("인증글내용4").likeCount(20L).view(20L).latitude(1.1).longitude(1.1)
                    .build();
            GuestBook guestBook5 = GuestBook.builder()
                    .member(member).pilgrimage(pilgrimage2)
                    .title("인증글5").content("인증글내용5").likeCount(20L).view(20L).latitude(1.1).longitude(1.1)
                    .build();
            GuestBook guestBook6 = GuestBook.builder()
                    .member(member).pilgrimage(pilgrimage2)
                    .title("인증글6").content("인증글내용6").likeCount(25L).view(25L).latitude(1.1).longitude(1.1)
                    .build();
            GuestBook guestBook7 = GuestBook.builder()
                    .member(member).pilgrimage(pilgrimage2)
                    .title("인증글7").content("인증글내용7").likeCount(30L).view(30L).latitude(1.1).longitude(1.1)
                    .build();
            em.merge(guestBook1);em.merge(guestBook2); em.merge(guestBook3); em.merge(guestBook4); em.merge(guestBook5); em.merge(guestBook6); em.merge(guestBook7);
        }
    }
}

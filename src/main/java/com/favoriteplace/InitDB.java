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
import com.favoriteplace.app.repository.AddressRepository;
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
        initService.createMember("1");
        initService.initRallyAndPilgrimage();
        initService.initDB();
    }

    @Component
    @RequiredArgsConstructor
    @Transactional
    static class InitService {
        private final EntityManager em;
        private final AddressRepository addressRepository;

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
                    .profileTitle(item2).email("email@email.com")
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

            Address address = addressRepository.findByStateAndDistrict("도쿄도", "시부야구");

            Rally rally1 = Rally.builder().id(0L)
                    .item(item1).image(image1).name("최애의 아이")
                    .description("환생한 내가 아이돌의 자녀??!!").achieveNumber(10L)
                    .pilgrimageNumber(4L).build();
            Rally rally2 = Rally.builder().id(1L)
                    .item(item2).image(image2).name("날씨의 아이")
                    .description("비야 멈춰라!").achieveNumber(20L)
                    .pilgrimageNumber(2L).build();
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
        public void initMember(){
            createMember("1");
        }

        public void initRallyAndPilgrimage(){
            Address addressSibuya = createAddress("도쿄도", "시부야구");
            Address addressShinjuku = createAddress("도쿄도", "신주쿠구");
            Address addressMinato = createAddress("도쿄도", "미나토구");
            Rally rally = createRally("날씨의 아이", "천비가 멈추지 않던 여름날, 고향을 떠나 도쿄로 온 가출 소년 호다카는 우연히 비를 멈추게 하는 능력을 가진 신비한 소녀 히나를 만난다. 히나의 능력을 알게 된 호다카는 그 능력을 사용해 돈을 벌 계획을 세운다. 매일 내리는 비로 우울해하는 도쿄 사람들은 히나의 능력으로 인해 행복감을 되찾게 되지만, 호다카와 히나는 뜻밖의 비밀을 마주하게 된다.");
            Pilgrimage pilgrimage1 = createPilgrimage(addressSibuya, rally, "시부야 스크램블교차로");
            Pilgrimage pilgrimage2 = createPilgrimage(addressMinato, rally, "롯폰기 힐즈 스카이덱 전망대");
            Pilgrimage pilgrimage3 = createPilgrimage(addressMinato, rally, "오다이바 해변공원");
            Pilgrimage pilgrimage4 = createPilgrimage(addressShinjuku, rally, "맥도날드 신주쿠 역전점");
        }

        public Member createMember(String number){
            Image image1 = createImage("imgIcon"+number);
            Image image2 = createImage("imgTitle"+number);

            Item item1 = createItem(image1, "icon"+number, ItemType.ICON);
            Item item2 = createItem(image2, "title"+number, ItemType.TITLE);

            Member member = Member.builder()
                    .id(0L)
                    .profileIcon(item1)
                    .profileTitle(item2)
                    .email("user@naver.com")
                    .password("1234")
                    .birthday(null)
                    .nickname("user"+number)
                    .description("hi")
                    .profileImageUrl("")
                    .status(MemberStatus.Y)
                    .alarmAllowance(false)
                    .point(0L)
                    .loginType(LoginType.SELF)
                    .refreshToken("")
                    .build();
            em.merge(member);
            return member;
        }

        public Rally createRally(String name, String description){
            Image image1 = createImage("rallyItemImg");
            Image animeImg = createImage("animeImg");
            Item item1 = createItem(image1, "rallyTitle", ItemType.TITLE);
            Rally rally = Rally.builder()
                    .item(item1)
                    .image(animeImg)
                    .name(name)
                    .description(description)
                    .achieveNumber(0L)
                    .pilgrimageNumber(0L)
                    .build();
            em.persist(rally);
            return rally;
        }

        public Pilgrimage createPilgrimage(Address address, Rally rally, String detailAddress){
            Image realImg = createImage(rally.getName()+"PilRealImg");
            Image animeImg = createImage(rally.getName()+"PilAnimeImg");
            Pilgrimage pilgrimage = Pilgrimage.builder()
                    .address(address)
                    .rally(rally)
                    .virtualImage(animeImg)
                    .realImage(realImg)
                    .rallyName(rally.getName())
                    .detailAddress(detailAddress)
                    .latitude(0.0)
                    .longitude(0.0)
                    .build();
            rally.addPilgrimage();
            em.merge(rally);
            em.persist(pilgrimage);
            return pilgrimage;
        }

        public Address createAddress(String state, String district){
            Address address = Address.builder()
                    .state(state)
                    .district(district)
                    .build();
            em.persist(address);
            return address;
        }
        private Item createItem(Image image1, String name, ItemType type) {
            Item item = Item.builder()
                    .image(image1)
                    .name(name)
                    .status(SaleStatus.NOT_FOR_SALE)
                    .type(type)
                    .saleDeadline(null)
                    .point(null)
                    .description(null)
                    .build();
            em.persist(item);
            return item;
        }
        private Image createImage(String url) {
            Image image1 = Image.builder()
                    .post(null)
                    .guestBook(null)
                    .url(url)
                    .build();
            em.persist(image1);
            return image1;
        }

    }
}

package com.favoriteplace;

import com.favoriteplace.app.domain.Image;
import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.domain.community.Comment;
import com.favoriteplace.app.domain.community.GuestBook;
import com.favoriteplace.app.domain.community.Post;
import com.favoriteplace.app.domain.enums.*;
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
//        initService.createMember("1");
//        initService.initRallyAndPilgrimage();
//        initService.initDB();
//        initService.initAddress();
//        initService.initWeatheringWithYou();
//        initService.initOshiNoKo();
//        initService.initJujutsuKaisen();
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
                    .defaultImage(image1).name("icon1")
                    .centerImage(image2)
                    .status(SaleStatus.NOT_FOR_SALE).type(ItemType.ICON)
                    .saleDeadline(null).point(10L).category(ItemCategory.NEW).description("아이템1번")
                    .saleDeadline(null).point(5L)
                    .build();
            Item item2 = Item.builder()
                    .defaultImage(image2)
                    .centerImage(image1)
                    .name("새싹회원")
                    .status(SaleStatus.NOT_FOR_SALE).type(ItemType.TITLE)
                    .saleDeadline(null).point(10L).category(ItemCategory.NEW).description("아이템2번")
                    .build();
            em.merge(item1); em.merge(item2);

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

//            Post post1 = Post.builder()
//                    .id(0L).member(member)
//                    .title("게시글1").content("abcdefg")
//                    .likeCount(10L).view(10L).build();
//            Post post2 = Post.builder()
//                    .id(1L).member(member)
//                    .title("게시글2").content("abcdefg")
//                    .likeCount(15L).view(15L).build();
//            Post post3 = Post.builder()
//                    .id(2L).member(member)
//                    .title("게시글3").content("abcdefg")
//                    .likeCount(20L).view(20L).build();
//            Post post4 = Post.builder()
//                    .id(3L).member(member)
//                    .title("게시글4").content("abcdefg")
//                    .likeCount(20L).view(20L).build();
//            Post post5 = Post.builder()
//                    .id(4L).member(member)
//                    .title("게시글5").content("abcdefg")
//                    .likeCount(25L).view(25L).build();
//            Post post6 = Post.builder()
//                    .id(5L).member(member)
//                    .title("게시글6").content("abcdefg")
//                    .likeCount(30L).view(30L).build();
//            Post post7 = Post.builder()
//                    .id(6L).member(member)
//                    .title("게시글7").content("abcdefg")
//                    .likeCount(35L).view(35L).build();
//            em.merge(post1); em.merge(post2); em.merge(post3); em.merge(post4); em.merge(post5); em.merge(post6); em.merge(post7);
//
//
//            Address address = addressRepository.findByStateAndDistrict("도쿄도", "시부야구");
//            Address address2 = Address.builder().state("도쿄도").district("시부야구").build();
//            em.persist(address2);
//
//            Rally rally1 = Rally.builder().id(0L)
//                    .item(item1).image(image1).name("최애의 아이")
//                    .description("환생한 내가 아이돌의 자녀??!!").achieveNumber(10L)
//                    .pilgrimageNumber(4L).build();
//            Rally rally2 = Rally.builder().id(1L)
//                    .item(item2).image(image2).name("날씨의 아이")
//                    .description("비야 멈춰라!").achieveNumber(20L)
//                    .pilgrimageNumber(2L).build();
//            em.merge(rally1); em.merge(rally2);
//
//            Pilgrimage pilgrimage1 = Pilgrimage.builder()
//                    .address(address2).rally(rally1).virtualImage(image1).realImage(image1)
//                    .rallyName("최애의 아이").detailAddress("스크램블 교차로1")
//                    .detailAddressEn("스크램블 교차로1")
//                    .detailAddressJp("스크램블 교차로1")
//                    .latitude(1.1).longitude(1.1).build();
//            Pilgrimage pilgrimage2 = Pilgrimage.builder()
//                    .address(address2).rally(rally2).virtualImage(image2).realImage(image2)
//                    .rallyName("최애의 아이").detailAddress("스크램블 교차로2").detailAddressEn("스크램블 교차로1")
//                    .detailAddressJp("스크램블 교차로1").latitude(1.1).longitude(1.1).build();
//            em.merge(pilgrimage1); em.merge(pilgrimage2);
//
//            GuestBook guestBook1 = GuestBook.builder().id(0L)
//                    .member(member).pilgrimage(pilgrimage1)
//                    .title("인증글1").content("인증글내용1").likeCount(5L).view(5L)
//                    .build();
//            GuestBook guestBook2 = GuestBook.builder().id(1L)
//                    .member(member).pilgrimage(pilgrimage1)
//                    .title("인증글2").content("인증글내용2").likeCount(10L).view(10L)
//                    .build();
//            GuestBook guestBook3 = GuestBook.builder()
//                    .member(member).pilgrimage(pilgrimage1)
//                    .title("인증글3").content("인증글내용3").likeCount(15L).view(15L)
//                    .build();
//            GuestBook guestBook4 = GuestBook.builder()
//                    .member(member).pilgrimage(pilgrimage2)
//                    .title("인증글4").content("인증글내용4").likeCount(20L).view(20L)
//                    .build();
//            GuestBook guestBook5 = GuestBook.builder()
//                    .member(member).pilgrimage(pilgrimage2)
//                    .title("인증글5").content("인증글내용5").likeCount(20L).view(20L)
//                    .build();
//            GuestBook guestBook6 = GuestBook.builder()
//                    .member(member).pilgrimage(pilgrimage2)
//                    .title("인증글6").content("인증글내용6").likeCount(25L).view(25L)
//                    .build();
//            GuestBook guestBook7 = GuestBook.builder()
//                    .member(member).pilgrimage(pilgrimage2)
//                    .title("인증글7").content("인증글내용7").likeCount(30L).view(30L)
//                    .build();
//            em.merge(guestBook1);em.merge(guestBook2); em.merge(guestBook3); em.merge(guestBook4); em.merge(guestBook5); em.merge(guestBook6); em.merge(guestBook7);
//
//            Comment comment1 = Comment.builder()
//                    .member(member).post(null).guestBook(guestBook1).content("G댓글1").build();
//            Comment comment2 = Comment.builder()
//                    .member(member).post(null).guestBook(guestBook1).content("G댓글2").build();
//            Comment comment3 = Comment.builder()
//                    .member(member).post(null).guestBook(guestBook1).content("G댓글3").build();
//            Comment comment4 = Comment.builder()
//                    .member(member).post(post1).guestBook(null).content("P댓글4").build();
//            Comment comment5 = Comment.builder()
//                    .member(member).post(post1).guestBook(null).content("P댓글5").build();
//            Comment comment6 = Comment.builder()
//                    .member(member).post(post1).guestBook(null).content("P댓글6").build();
//            em.merge(comment1); em.merge(comment2);em.merge(comment3);em.merge(comment4);em.merge(comment5);em.merge(comment6);
        }

        public void initMember(){
            createMember("1");
        }

        public void initAddress(){
            Address addressSibuya = createAddress("도쿄도", "시부야구");
            Address addressShinjuku = createAddress("도쿄도", "신주쿠구");
            Address addressMinato = createAddress("도쿄도", "미나토구");
        }

        public void initWeatheringWithYou(){
            Address addressSibuya = addressRepository.findById(1L).orElse(null);
            Address addressShinjuku = addressRepository.findById(2L).orElse(null);
            Address addressMinato = addressRepository.findById(3L).orElse(null);

            Rally rally = createRally("날씨의 아이", "천비가 멈추지 않던 여름날, 고향을 떠나 도쿄로 온 가출 소년 호다카는 우연히 비를 멈추게 하는 능력을 가진 신비한 소녀 히나를 만난다. 히나의 능력을 알게 된 호다카는 그 능력을 사용해 돈을 벌 계획을 세운다. 매일 내리는 비로 우울해하는 도쿄 사람들은 히나의 능력으로 인해 행복감을 되찾게 되지만, 호다카와 히나는 뜻밖의 비밀을 마주하게 된다.");
            Pilgrimage pilgrimage1 = createPilgrimage(addressShinjuku, rally, "맥도날드 세이부 신주쿠역 앞점");
            Pilgrimage pilgrimage2 = createPilgrimage(addressShinjuku, rally, "아타미 빌딩");
            Pilgrimage pilgrimage3 = createPilgrimage(addressSibuya, rally, "시부야 스크램블 교차로");
            Pilgrimage pilgrimage4 = createPilgrimage(addressMinato, rally, "시바 공원");
        }

        public void initOshiNoKo(){
            Address addressSibuya = addressRepository.findById(1L).orElse(null);
            Address addressShinjuku = addressRepository.findById(2L).orElse(null);
            Rally rally = createRally("최애의 아이", "‘최애의 아이’는 주인공인 호시노 루비, 호시노 아쿠아를 비롯한 연예계 친구들이 다니고 있는 학교나 사무실의 위치, 자주 등장하는 배경 등을 미루어보았을 때 도쿄도, 특히 메구로구 중심으로 배경지가 설정되어 있음을 알 수 있다. 특히 ‘오늘은 달콤하게’ 촬영 장면의 배경이 된 오다이바와 도쿄 돔, 무도관 등을 제외하면 메구로-신주쿠-시부야구의 도쿄 서쪽이 대부분이므로 한 번에 여러 장소를 둘러보기 좋다.");
            Pilgrimage pilgrimage1 = createPilgrimage(addressSibuya, rally, "에비스 가든 플레이스");
            Pilgrimage pilgrimage2 = createPilgrimage(addressSibuya, rally, "패밀리 마트 시부야 공원 도오리점");
            Pilgrimage pilgrimage3 = createPilgrimage(addressSibuya, rally, "에비스 미나미 2공원");
            Pilgrimage pilgrimage4 = createPilgrimage(addressSibuya, rally, "스타벅스 시부야 츠타야점");
            Pilgrimage pilgrimage5 = createPilgrimage(addressShinjuku, rally, "쿠시카츠 타나카 신주쿠산초메점");
        }

        public void initJujutsuKaisen(){
            Address addressSibuya = addressRepository.findById(1L).orElse(null);
            Address addressShinjuku = addressRepository.findById(2L).orElse(null);
            Address addressMinato = addressRepository.findById(3L).orElse(null);
            Rally rally = createRally("주술회전", "‘주술고전’이 도쿄와 교토에 하나씩 있는 만큼, 1기는 도쿄도와 교토부를 배경으로 하는 장면이 많다. 1기 오프닝의 배경 역시 도쿄 스카이트리, 신주쿠의 눈 등 다양한 도쿄의 장소를 담아내기도 하였다. 2기는 부제목이 ‘시부야 사변’인 만큼 도쿄도 시부야구를 배경으로 스토리가 진행되었기에 시부야의 실제 장소가 여럿 등장한다.");
            Pilgrimage pilgrimage1 = createPilgrimage(addressShinjuku, rally, "KFC 니시신주쿠점 건너편");
            Pilgrimage pilgrimage2 = createPilgrimage(addressSibuya, rally, "시부야 히카리에 입구");
            Pilgrimage pilgrimage3 = createPilgrimage(addressMinato, rally, "롯폰기 힐즈 아레나");
            Pilgrimage pilgrimage4 = createPilgrimage(addressMinato, rally, "도쿄 스카이트리");
            Pilgrimage pilgrimage5 = createPilgrimage(addressMinato, rally, "신주쿠의 눈");
        }

        public Member createMember(String number){
            Image image1 = createImage("imgIcon"+number);
            Image image2 = createImage("imgTitle"+number);

            Item item1 = createItem(image1, "icon"+number, ItemType.ICON, ItemCategory.NEW);
            Item item2 = createItem(image2, "title"+number, ItemType.TITLE, ItemCategory.NEW);

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
            Item item1 = createItem(image1, "rallyTitle", ItemType.TITLE, ItemCategory.NEW);
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
                    .detailAddressEn(detailAddress)
                    .detailAddressJp(detailAddress)
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
        private Item createItem(Image image1, String name, ItemType type, ItemCategory itemCategory) {
            Item item = Item.builder()
                    .defaultImage(image1)
                    .centerImage(image1)
                    .name(name)
                    .status(SaleStatus.NOT_FOR_SALE)
                    .type(type)
                    .saleDeadline(null)
                    .point(0L)
                    .description("item 설명")
                    .category(itemCategory)
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
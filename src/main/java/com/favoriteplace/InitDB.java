package com.favoriteplace;

import com.favoriteplace.app.domain.Image;
import com.favoriteplace.app.domain.Member;
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
        initService.createMember("1");
        initService.initRallyAndPilgrimage();
    }

    @Component
    @RequiredArgsConstructor
    @Transactional
    static class InitService {
        private final EntityManager em;

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
                    .email("email@email.com")
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

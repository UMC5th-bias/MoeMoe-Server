package com.favoriteplace;

import com.favoriteplace.app.domain.Image;
import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.domain.enums.ItemType;
import com.favoriteplace.app.domain.enums.LoginType;
import com.favoriteplace.app.domain.enums.MemberStatus;
import com.favoriteplace.app.domain.enums.SaleStatus;
import com.favoriteplace.app.domain.item.Item;
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
                    .post(null)
                    .guestBook(null)
                    .url("imgIcon")
                    .build();
            Image image2 = Image.builder()
                    .post(null)
                    .guestBook(null)
                    .url("imgTitle")
                    .build();
            em.persist(image1);
            em.persist(image2);

            Item item1 = Item.builder()
                    .image(image1)
                    .name("icon1")
                    .status(SaleStatus.NOT_FOR_SALE)
                    .type(ItemType.ICON)
                    .saleDeadline(null)
                    .point(null)
                    .description(null)
                    .build();
            Item item2 = Item.builder()
                    .image(image2)
                    .name("title1")
                    .status(SaleStatus.NOT_FOR_SALE)
                    .type(ItemType.TITLE)
                    .saleDeadline(null)
                    .point(null)
                    .description(null)
                    .build();
            em.persist(item1);
            em.persist(item2);

            Member member = Member.builder()
                    .id(0L)
                    .profileIcon(item1)
                    .profileTitle(item2)
                    .email("email@email.com")
                    .password("1234")
                    .birthday(null)
                    .nickname("user1")
                    .description("hi")
                    .profileImageUrl("")
                    .status(MemberStatus.Y)
                    .alarmAllowance(false)
                    .point(0L)
                    .loginType(LoginType.SELF)
                    .refreshToken("")
                    .build();
            em.merge(member);
        }
    }
}

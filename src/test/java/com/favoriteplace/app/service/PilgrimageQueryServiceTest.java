package com.favoriteplace.app.service;

import static org.mockito.Mockito.when;

import com.favoriteplace.app.domain.Image;
import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.domain.travel.Rally;
import com.favoriteplace.app.dto.travel.RallyResponseDto.PilgrimageCategoryAnimeDto;
import com.favoriteplace.app.repository.AddressRepository;
import com.favoriteplace.app.repository.GuestBookRepository;
import com.favoriteplace.app.repository.HashtagRepository;
import com.favoriteplace.app.repository.ImageRepository;
import com.favoriteplace.app.repository.LikedRallyRepository;
import com.favoriteplace.app.repository.PilgrimageRepository;
import com.favoriteplace.app.repository.RallyRepository;
import com.favoriteplace.app.repository.VisitedPilgrimageRepository;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.Nested;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PilgrimageQueryServiceTest {
    @Mock
    PilgrimageRepository pilgrimageRepository;
    @Mock
    RallyRepository rallyRepository;
    @Mock
    LikedRallyRepository likedRallyRepository;
    @Mock
    VisitedPilgrimageRepository visitedPilgrimageRepository;
    @Mock
    AddressRepository addressRepository;
    @Mock
    GuestBookRepository guestBookRepository;
    @Mock
    HashtagRepository hashtagRepository;
    @Mock
    ImageRepository imageRepository;
    private PilgrimageQueryService pilgrimageService;

    @BeforeEach
    void setup() {
        this.pilgrimageService = new PilgrimageQueryService(pilgrimageRepository,
                rallyRepository,
                likedRallyRepository, visitedPilgrimageRepository, addressRepository, guestBookRepository,
                hashtagRepository, imageRepository);
    }

    @Test
    @DisplayName("성지순례 애니 별 카테고리 조회 성공")
    void 성지순례_애니별_카테고리_조회() {
        //given
        Member member = Mockito.mock(Member.class);
        Rally rallyOne = Mockito.mock(Rally.class);
        Image image = Mockito.mock(Image.class);
        Mockito.when(image.getUrl()).thenReturn("이미지");
        Mockito.when(rallyOne.getId()).thenReturn(0L);
        Mockito.when(rallyOne.getName()).thenReturn("최애의아이");
        Mockito.when(rallyOne.getImage()).thenReturn(image);
        Rally rallyTwo = Mockito.mock(Rally.class);
        Mockito.when(rallyTwo.getId()).thenReturn(1L);
        Mockito.when(rallyTwo.getName()).thenReturn("날씨의아이");
        Mockito.when(rallyTwo.getImage()).thenReturn(image);

        //when
        Mockito.when(rallyRepository.findAllOrderByCreatedAt()).thenReturn(List.of(rallyOne, rallyTwo));
        List<PilgrimageCategoryAnimeDto> categoryAnime = pilgrimageService.getCategoryAnime(null);

        //then
        Assertions.assertThat(categoryAnime.get(0).getId()).isEqualTo(0L);
        Assertions.assertThat(categoryAnime.get(1).getId()).isEqualTo(1L);
    }
}

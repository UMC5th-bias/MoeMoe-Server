package com.favoriteplace.app.service;

import com.favoriteplace.app.community.repository.GuestBookRepository;
import com.favoriteplace.app.community.repository.HashtagRepository;
import com.favoriteplace.app.image.domain.Image;
import com.favoriteplace.app.image.repository.ImageRepository;
import com.favoriteplace.app.member.domain.Member;
import com.favoriteplace.app.pilgrimage.repository.PilgrimageRepository;
import com.favoriteplace.app.pilgrimage.repository.VisitedPilgrimageRepository;
import com.favoriteplace.app.pilgrimage.service.PilgrimageQueryService;
import com.favoriteplace.app.rally.controller.dto.RallyResponseDto.PilgrimageCategoryAnimeDto;
import com.favoriteplace.app.rally.domain.Rally;
import com.favoriteplace.app.rally.repository.AddressRepository;
import com.favoriteplace.app.rally.repository.LikedRallyRepository;
import com.favoriteplace.app.rally.repository.RallyRepository;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

    @Nested
    @DisplayName("성지순례 애니 별 카테고리 조회")
    class AnemeCategories {
        @Test
        @DisplayName("비로그인 성지순례 애니 별 카테고리 조회 성공")
        void 비로그인_성지순례_애니별_카테고리_조회() {
            //given
            Image image = Mockito.mock(Image.class);
            Mockito.when(image.getUrl()).thenReturn("이미지");
            Rally rallyOne = getRally(0L, "최애의아이", image);
            Rally rallyTwo = getRally(1L, "날씨의아이", image);

            //when
            Mockito.when(rallyRepository.findAllOrderByCreatedAt()).thenReturn(List.of(rallyOne, rallyTwo));
            List<PilgrimageCategoryAnimeDto> categoryAnime = pilgrimageService.getCategoryAnime(null);

            //then
            Assertions.assertThat(categoryAnime.get(0).getId()).isEqualTo(0L);
            Assertions.assertThat(categoryAnime.get(1).getId()).isEqualTo(1L);
            Assertions.assertThat(categoryAnime.size()).isEqualTo(2);
        }

        @Test
        @DisplayName("로그인 성지순례 애니 별 카테고리 조회 성공")
        void 로그인_성지순례_애니별_카테고리_조회() {
            //given
            Member member = Mockito.mock(Member.class);
            Mockito.when(member.getId()).thenReturn(0L);
            Image image = Mockito.mock(Image.class);
            Mockito.when(image.getUrl()).thenReturn("이미지");
            Rally rally = getRally(0L, "최애의아이", image);
            Mockito.when(rallyRepository.findAllOrderByCreatedAt()).thenReturn(List.of(rally));
            Mockito.when(visitedPilgrimageRepository.findByDistinctCount(member.getId(), rally.getId())).thenReturn(1L);

            //when
            List<PilgrimageCategoryAnimeDto> categoryAnime = pilgrimageService.getCategoryAnime(member);

            //then
            Assertions.assertThat(categoryAnime.get(0).getId()).isEqualTo(0L);
            Assertions.assertThat(categoryAnime.get(0).getName()).isEqualTo("최애의아이");
        }

        @NotNull
        private Rally getRally(Long id, String name, Image image) {
            Rally rally = Mockito.mock(Rally.class);
            Mockito.when(rally.getId()).thenReturn(id);
            Mockito.when(rally.getName()).thenReturn(name);
            Mockito.when(rally.getImage()).thenReturn(image);
            return rally;
        }
    }

    @Nested
    @DisplayName("성지순례 지역 별 카테고리 조회")
    class RegionCategories {
        @Test
        @DisplayName("비로그인 성지순례 지역 별 카테고리 조회 성공")
        void 비로그인_성지순례_지역별_카테고리_조회_성공() {

        }

        @Test
        @DisplayName("로그인 성지순례 지역 별 카테고리 조회 성공")
        void 로그인_성지순례_지역별_카테고리_조회_성공() {

        }
    }
}

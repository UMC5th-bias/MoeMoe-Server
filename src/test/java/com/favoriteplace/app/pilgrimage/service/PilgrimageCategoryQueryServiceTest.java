package com.favoriteplace.app.pilgrimage.service;

import com.favoriteplace.app.image.domain.Image;
import com.favoriteplace.app.member.domain.Member;
import com.favoriteplace.app.member.repository.MemberRepository;
import com.favoriteplace.app.pilgrimage.controller.dto.PilgrimageResponseDto;
import com.favoriteplace.app.pilgrimage.controller.dto.PilgrimageResponseDto.PilgrimageCategoryRegionDetailDto;
import com.favoriteplace.app.pilgrimage.controller.dto.PilgrimageResponseDto.PilgrimageCategoryRegionDto;
import com.favoriteplace.app.pilgrimage.domain.Pilgrimage;
import com.favoriteplace.app.pilgrimage.repository.PilgrimageRepository;
import com.favoriteplace.app.pilgrimage.repository.VisitedPilgrimageRepository;
import com.favoriteplace.app.rally.controller.dto.RallyResponseDto.PilgrimageCategoryAnimeDto;
import com.favoriteplace.app.rally.domain.Address;
import com.favoriteplace.app.rally.domain.Rally;
import com.favoriteplace.app.rally.repository.AddressRepository;
import com.favoriteplace.app.rally.repository.RallyRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PilgrimageCategoryQueryServiceTest {
    @Mock
    MemberRepository memberRepository;
    @Mock
    PilgrimageRepository pilgrimageRepository;
    @Mock
    RallyRepository rallyRepository;
    @Mock
    VisitedPilgrimageRepository visitedPilgrimageRepository;
    @Mock
    AddressRepository addressRepository;
    private PilgrimageCategoryQueryService pilgrimageService;

    @BeforeEach
    void setup() {
        this.pilgrimageService = new PilgrimageCategoryQueryService(memberRepository, rallyRepository,
                visitedPilgrimageRepository, addressRepository, pilgrimageRepository);
    }

    @Nested
    @DisplayName("성지순례 애니 별 카테고리 조회")
    class AnimeCategories {
        @Test
        @DisplayName("비로그인 성지순례 애니 별 카테고리 조회 성공")
        void 비로그인_성지순례_애니별_카테고리_조회() {
            //given
            Rally rallyOne = getRally(0L, "최애의아이");
            Rally rallyTwo = getRally(1L, "날씨의아이");
            Mockito.when(rallyRepository.findAllOrderByCreatedAt()).thenReturn(List.of(rallyOne, rallyTwo));

            //when
            List<PilgrimageCategoryAnimeDto> categoryAnime = pilgrimageService.getCategoryAnime(null);

            //then
            Assertions.assertThat(categoryAnime).hasSize(2);
            Assertions.assertThat(categoryAnime.get(0).getId()).isEqualTo(0L);
            Assertions.assertThat(categoryAnime.get(1).getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("로그인 성지순례 애니 별 카테고리 조회 성공")
        void 로그인_성지순례_애니별_카테고리_조회() {
            //given
            Member member = getMember(0L, "user@user.com");
            Rally rally = getRally(0L, "최애의아이");

            Mockito.when(memberRepository.findByEmail(member.getEmail())).thenReturn(Optional.of(member));
            Mockito.when(rallyRepository.findAllOrderByCreatedAt()).thenReturn(List.of(rally));
            Mockito.when(visitedPilgrimageRepository.findByDistinctCount(member.getId(), rally.getId())).thenReturn(1L);

            //when
            List<PilgrimageCategoryAnimeDto> categoryAnime = pilgrimageService.getCategoryAnime(member.getEmail());

            //then
            Assertions.assertThat(categoryAnime).hasSize(1);
            Assertions.assertThat(categoryAnime.get(0).getId()).isEqualTo(0L);
            Assertions.assertThat(categoryAnime.get(0).getName()).isEqualTo("최애의아이");
        }

        private Member getMember(Long id, String email) {
            Member member = Mockito.mock(Member.class);
            Mockito.when(member.getId()).thenReturn(id);
            Mockito.when(member.getEmail()).thenReturn(email);
            return member;
        }

        private Rally getRally(Long id, String name) {
            Image image = Mockito.mock(Image.class);
            Mockito.when(image.getUrl()).thenReturn("이미지");
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
        @DisplayName("성지순례 지역 별 카테고리 조회 성공")
        void 성지순례_지역별_카테고리_조회_성공() {
            // given
            List<Address> addresses = new ArrayList<>();
            addresses.addAll(createAddress("도쿄", "시부야구", "신주쿠구"));
            addresses.addAll(createAddress("오사카", "어쩌구"));

            Mockito.when(addressRepository.findAll()).thenReturn(addresses);

            // when
            List<PilgrimageCategoryRegionDto> categoryRegion = pilgrimageService.getCategoryRegion();

            // then
            Assertions.assertThat(categoryRegion).hasSize(2);
            assertCategoryRegion(categoryRegion, "오사카", "어쩌구");
            assertCategoryRegion(categoryRegion, "도쿄", "시부야구", "신주쿠구");
        }

        private void assertCategoryRegion(List<PilgrimageCategoryRegionDto> categoryRegion, String state,
                                          String... districts) {
            PilgrimageCategoryRegionDto region = findCategoryRegionByState(categoryRegion, state);

            Assertions.assertThat(region).isNotNull();
            Assertions.assertThat(region.getState()).isEqualTo(state);

            List<String> districtList = region.getDetail().stream()
                    .map(PilgrimageResponseDto.PilgrimageAddressDetailDto::getDistrict)
                    .toList();

            Assertions.assertThat(districtList).containsOnly(districts);
        }

        private PilgrimageCategoryRegionDto findCategoryRegionByState(List<PilgrimageCategoryRegionDto> categoryRegion,
                                                                      String state) {
            return categoryRegion.stream()
                    .filter(region -> region.getState().equals(state))
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("State를 찾을 수 없습니다: " + state));
        }
    }

    @Nested
    @DisplayName("성지순례 지역 상세 카테고리 조회")
    class RegionDetails {
        @Test
        @DisplayName("성지순례 지역 상세 목록 조회 성공")
        void 성지순례_지역_상세_목록_조회_성공() {
            // given
            Address address = createAddress("도쿄", "시부야구", "신주쿠구").get(0);
            Pilgrimage pilgrimage1 = getPilgrimage(0L, "시부야 스크램교차로");
            Pilgrimage pilgrimage2 = getPilgrimage(1L, "시부야 어쩌구");
            Rally rally1 = getRally("최애의장소");
            Rally rally2 = getRally("날씨의아이");

            Mockito.when(addressRepository.findById(address.getId())).thenReturn(Optional.of(address));
            Mockito.when(pilgrimageRepository.findByAddress(address)).thenReturn(List.of(pilgrimage1, pilgrimage2));
            Mockito.when(rallyRepository.findByPilgrimage(pilgrimage1)).thenReturn(rally1);
            Mockito.when(rallyRepository.findByPilgrimage(pilgrimage2)).thenReturn(rally2);

            // when
            List<PilgrimageCategoryRegionDetailDto> categoryRegionDetail = pilgrimageService.getCategoryRegionDetail(
                    address.getId());

            // then
            Assertions.assertThat(categoryRegionDetail).hasSize(2);
            Assertions.assertThat(categoryRegionDetail.get(0).getDetailAddress())
                    .isEqualTo("시부야 스크램교차로");
            Assertions.assertThat(categoryRegionDetail.get(1).getDetailAddress())
                    .isEqualTo("시부야 어쩌구");
            Assertions.assertThat(categoryRegionDetail.get(0).getTitle()).isEqualTo("최애의장소");
            Assertions.assertThat(categoryRegionDetail.get(1).getTitle()).isEqualTo("날씨의아이");
        }

        private Rally getRally(String title) {
            Rally rally = Mockito.mock(Rally.class);
            Mockito.when(rally.getName()).thenReturn(title);
            return rally;
        }

        private Pilgrimage getPilgrimage(Long id, String detailAddress) {
            Image image = Mockito.mock(Image.class);
            Mockito.when(image.getUrl()).thenReturn("이미지");
            Pilgrimage pilgrimage = Mockito.mock(Pilgrimage.class);
            Mockito.when(pilgrimage.getId()).thenReturn(id);
            Mockito.when(pilgrimage.getDetailAddress()).thenReturn(detailAddress);
            Mockito.when(pilgrimage.getVirtualImage()).thenReturn(image);
            Mockito.when(pilgrimage.getLatitude()).thenReturn(11.111);
            Mockito.when(pilgrimage.getLongitude()).thenReturn(127.111);
            return pilgrimage;
        }
    }

    protected List<Address> createAddress(String state, String... districts) {
        return Arrays.stream(districts).map(district -> Address.builder()
                        .state(state)
                        .district(district)
                        .build())
                .toList();
    }
}

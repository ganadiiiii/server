package ganadii.hanjum.service;

import ganadii.hanjum.domain.CardFlowers;
import ganadii.hanjum.domain.FlowerCards;
import ganadii.hanjum.domain.Flowers;
import ganadii.hanjum.domain.User;
import ganadii.hanjum.domain.enums.CardImageSource;
import ganadii.hanjum.domain.enums.FlowerType;
import ganadii.hanjum.dto.FlowerCardDtos;
import ganadii.hanjum.repository.CardFlowersRepository;
import ganadii.hanjum.repository.FlowerCardsRepository;
import ganadii.hanjum.repository.FlowersRepository;
import ganadii.hanjum.repository.UserRepository;
import ganadii.hanjum.repository.CardDesignAssetRepository;
import ganadii.hanjum.service.asset.CardAssetGenerator;
import ganadii.hanjum.service.asset.CardAssetPresetLocator;
import ganadii.hanjum.service.asset.model.CardAssetDescriptor;
import ganadii.hanjum.service.asset.model.CardDesignRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class FlowerCardServiceTests {

    @Autowired
    private FlowerCardService flowerCardService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FlowersRepository flowersRepository;

    @Autowired
    private FlowerCardsRepository flowerCardsRepository;

    @Autowired
    private CardFlowersRepository cardFlowersRepository;

    @Autowired
    private CardDesignAssetRepository cardDesignAssetRepository;

    @MockBean
    private CardAssetGenerator cardAssetGenerator;

    @MockBean
    private CardAssetPresetLocator cardAssetPresetLocator;

    @Test
    void createCardPersistsCardAndMainFlowerWhenEnumNamesUsed() {
        User creator = userRepository.save(User.builder()
                .email("test@example.com")
                .password("password")
                .nickname("tester")
                .build());
        Flowers mainFlower = flowersRepository.save(Flowers.builder()
                .koreanName("장미")
                .englishName("Rose")
                .imageUrl("https://example.com/rose.png")
                .build());

        when(cardAssetPresetLocator.findPreset(any(CardDesignRequest.class))).thenReturn(Optional.empty());
        when(cardAssetGenerator.generate(any(CardDesignRequest.class))).thenReturn(
                new CardAssetDescriptor(
                        "https://assets.example.com/generated/rose-friend.png",
                        "generated/rose-friend.png",
                        CardImageSource.GENERATED,
                        "checksum-rose"
                )
        );

        FlowerCardDtos.CreateCardRequest request = new FlowerCardDtos.CreateCardRequest(
                mainFlower.getFlowerId(),
                "고마워 카드",
                "감사합니다",
                "FRIEND",
                "BIRTHDAY",
                "CELEBRATION",
                "M",
                12000,
                List.of("#FFFFFF", "#FF00FF", "#FFFFFF")
        );

        FlowerCardDtos.CardResponse response = flowerCardService.createCard(creator.getUserId(), request);

        assertThat(response.cardId()).isNotNull();
        assertThat(response.imageUrl()).isEqualTo("https://assets.example.com/generated/rose-friend.png");
        assertThat(response.imageSource()).isEqualTo("GENERATED");
        assertThat(response.designAssetId()).isNotNull();
        assertThat(response.whoType()).isEqualTo("FRIEND");
        assertThat(response.whoLabel()).isEqualTo("친구");
        assertThat(response.backgroundColors()).containsExactly("#FFFFFF", "#FF00FF");
        assertThat(response.mainFlower().flowerId()).isEqualTo(mainFlower.getFlowerId());

        FlowerCards persisted = flowerCardsRepository.findById(response.cardId()).orElseThrow();
        assertThat(persisted.getCreator().getUserId()).isEqualTo(creator.getUserId());
        List<CardFlowers> links = cardFlowersRepository.findByFlowerCards_CardId(response.cardId());
        assertThat(links).hasSize(1);
        assertThat(links.get(0).getFlowers().getFlowerId()).isEqualTo(mainFlower.getFlowerId());
        assertThat(links.get(0).getFlowerType()).isEqualTo(FlowerType.MAIN);

        assertThat(cardDesignAssetRepository.count()).isEqualTo(1);
        verify(cardAssetGenerator, times(1)).generate(any(CardDesignRequest.class));
    }

    @Test
    void createCardAcceptsKoreanLabelsForEnums() {
        User creator = userRepository.save(User.builder()
                .email("label@example.com")
                .password("password")
                .nickname("labeler")
                .build());
        Flowers mainFlower = flowersRepository.save(Flowers.builder()
                .koreanName("튤립")
                .englishName("Tulip")
                .imageUrl("https://example.com/tulip.png")
                .build());

        reset(cardAssetGenerator, cardAssetPresetLocator);
        when(cardAssetPresetLocator.findPreset(any(CardDesignRequest.class))).thenReturn(Optional.of(
                new CardAssetDescriptor(
                        "https://assets.example.com/preset/tulip-happy.png",
                        "preset/tulip-happy.png",
                        CardImageSource.PRESET,
                        "checksum-preset"
                )));


        FlowerCardDtos.CreateCardRequest request = new FlowerCardDtos.CreateCardRequest(
                mainFlower.getFlowerId(),
                null,
                null,
                null,
                "친구",
                "생일",
                "축하",
                null,
                null,
                null
        );

        FlowerCardDtos.CardResponse response = flowerCardService.createCard(creator.getUserId(), request);

        assertThat(response.cardId()).isNotNull();
        assertThat(response.whoType()).isEqualTo("FRIEND");
        assertThat(response.whenType()).isEqualTo("BIRTHDAY");
        assertThat(response.emotionType()).isEqualTo("CELEBRATION");
        assertThat(response.imageSource()).isEqualTo("PRESET");
        assertThat(response.imageUrl()).isEqualTo("https://assets.example.com/preset/tulip-happy.png");

        verify(cardAssetGenerator, never()).generate(any(CardDesignRequest.class));
    }

    @Test
    void createCardReusesCachedAssetOnRepeatedRequests() {
        User creator = userRepository.save(User.builder()
                .email("cache@example.com")
                .password("password")
                .nickname("cache")
                .build());
        Flowers mainFlower = flowersRepository.save(Flowers.builder()
                .koreanName("라넌큘러스")
                .englishName("Ranunculus")
                .imageUrl("https://example.com/ranunculus.png")
                .build());

        reset(cardAssetGenerator, cardAssetPresetLocator);
        when(cardAssetPresetLocator.findPreset(any(CardDesignRequest.class))).thenReturn(Optional.empty());
        when(cardAssetGenerator.generate(any(CardDesignRequest.class))).thenReturn(
                new CardAssetDescriptor(
                        "https://assets.example.com/generated/cache.png",
                        "generated/cache.png",
                        CardImageSource.GENERATED,
                        "checksum-cache"
                )
        );

        FlowerCardDtos.CreateCardRequest request = new FlowerCardDtos.CreateCardRequest(
                mainFlower.getFlowerId(),
                "캐시 테스트",
                "라넌큘러스 카드",
                "FRIEND",
                "BIRTHDAY",
                "CELEBRATION",
                "L",
                15000,
                List.of("#123456")
        );

        flowerCardService.createCard(creator.getUserId(), request);
        flowerCardService.createCard(creator.getUserId(), request);

        verify(cardAssetGenerator, times(1)).generate(any(CardDesignRequest.class));
        assertThat(cardDesignAssetRepository.count()).isEqualTo(1);
    }
}

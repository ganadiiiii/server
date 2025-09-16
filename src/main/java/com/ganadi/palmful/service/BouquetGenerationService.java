package com.ganadi.palmful.service;

import com.ganadi.palmful.dto.BouquetResponse;
import com.ganadi.palmful.dto.GenerationRequest;
import com.ganadi.palmful.dto.GenerationResponse;
import com.ganadi.palmful.entity.Bouquet;
import com.ganadi.palmful.entity.BouquetGeneration;
import com.ganadi.palmful.repository.BouquetGenerationRepository;
import com.ganadi.palmful.repository.BouquetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BouquetGenerationService {

    private final BouquetGenerationRepository generationRepository;
    private final BouquetRepository bouquetRepository;
    private final BouquetService bouquetService;

    @Autowired
    public BouquetGenerationService(BouquetGenerationRepository generationRepository,
                                    BouquetRepository bouquetRepository,
                                    BouquetService bouquetService) {
        this.generationRepository = generationRepository;
        this.bouquetRepository = bouquetRepository;
        this.bouquetService = bouquetService;
    }

    @Transactional
    public GenerationResponse generate(Long userId, Long bouquetId, GenerationRequest request) {
        Bouquet bouquet = bouquetRepository.findByIdAndOwnerId(bouquetId, userId)
                .orElseThrow(() -> new IllegalArgumentException("꽃다발을 찾을 수 없거나 권한이 없습니다: " + bouquetId));

        // 다음 버전 계산 (현재 최대 버전 + 1)
        int nextVersion = generationRepository.findByBouquetId(bouquetId)
                .stream()
                .map(BouquetGeneration::getVersion)
                .max(Comparator.naturalOrder())
                .map(v -> v + 1)
                .orElse(1);

        String previewUrl = "https://cdn.mock/" + bouquetId + "/" + nextVersion + ".png";

        BouquetGeneration generation = new BouquetGeneration();
        generation.setBouquet(bouquet);
        generation.setVersion(nextVersion);
        generation.setModel("stable-diffusion");
        generation.setPrompt(request != null ? request.getPrompt() : null);
        generation.setSeed(request != null ? request.getSeed() : null);
        generation.setParamsJson(request != null ? request.getParamsJson() : null);
        generation.setPreviewUrl(previewUrl);
        generation.setStatus("generated");

        BouquetGeneration saved = generationRepository.save(generation);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<GenerationResponse> getGenerations(Long userId, Long bouquetId) {
        bouquetRepository.findByIdAndOwnerId(bouquetId, userId)
                .orElseThrow(() -> new IllegalArgumentException("꽃다발을 찾을 수 없거나 권한이 없습니다: " + bouquetId));

        return generationRepository.findByBouquetId(bouquetId)
                .stream()
                .sorted(Comparator.comparing(BouquetGeneration::getVersion))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public BouquetResponse publish(Long userId, Long bouquetId, Long generationId) {
        Bouquet bouquet = bouquetRepository.findByIdAndOwnerId(bouquetId, userId)
                .orElseThrow(() -> new IllegalArgumentException("꽃다발을 찾을 수 없거나 권한이 없습니다: " + bouquetId));

        BouquetGeneration gen = generationRepository.findById(generationId)
                .orElseThrow(() -> new IllegalArgumentException("생성 이력을 찾을 수 없습니다: " + generationId));

        if (!gen.getBouquet().getId().equals(bouquetId)) {
            throw new IllegalArgumentException("해당 부케의 생성 이력이 아닙니다.");
        }

        bouquet.setPreviewUrl(gen.getPreviewUrl());
        bouquetRepository.save(bouquet);

        return bouquetService.getBouquet(bouquetId);
    }

    private GenerationResponse toResponse(BouquetGeneration g) {
        return new GenerationResponse(
                g.getId(),
                g.getVersion(),
                g.getModel(),
                g.getPrompt(),
                g.getSeed(),
                g.getPreviewUrl(),
                g.getStatus(),
                g.getCreatedAt()
        );
    }
}



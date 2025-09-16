package com.ganadi.palmful.service;

import com.ganadi.palmful.dto.FlowerResponse;
import com.ganadi.palmful.entity.Flower;
import com.ganadi.palmful.repository.FlowerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class FlowerService {

    private final FlowerRepository flowerRepository;

    @Autowired
    public FlowerService(FlowerRepository flowerRepository) {
        this.flowerRepository = flowerRepository;
    }

    public List<FlowerResponse> getAllFlowers() {
        return flowerRepository.findAll()
                .stream()
                .map(this::toFlowerResponse)
                .collect(Collectors.toList());
    }

    public FlowerResponse getFlower(Long id) {
        Flower flower = flowerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("꽃을 찾을 수 없습니다: " + id));
        return toFlowerResponse(flower);
    }

    private FlowerResponse toFlowerResponse(Flower flower) {
        return new FlowerResponse(
                flower.getId(),
                flower.getName(),
                flower.getColorHex(),
                flower.getMeaning(),
                flower.getAssetUrl()
        );
    }
}



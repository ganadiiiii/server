package com.ganadi.palmful.config;

import com.ganadi.palmful.entity.Flower;
import com.ganadi.palmful.repository.FlowerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class FlowerDataLoader implements CommandLineRunner {

    private final FlowerRepository flowerRepository;

    @Autowired
    public FlowerDataLoader(FlowerRepository flowerRepository) {
        this.flowerRepository = flowerRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // 꽃 데이터가 이미 있는지 확인
        if (flowerRepository.count() > 0) {
            return; // 이미 데이터가 있으면 스킵
        }

        // 기본 꽃 데이터 추가 (png, 지정 9종)
        addFlower("개나리", "#FFD800", "봄, 희망", "/images/flowers/forsythia.png");
        addFlower("거베라", "#FF6F61", "순수한 사랑", "/images/flowers/gerbera.png");
        addFlower("은방울꽃", "#E6E6FA", "다시 찾은 행복", "/images/flowers/lilyvalley.png");
        addFlower("튤립", "#FF6347", "완벽한 사랑", "/images/flowers/tulip.png");
        addFlower("물망초", "#6CA6CD", "나를 잊지 말아요", "/images/flowers/forgetmenot.png");
        addFlower("장미", "#FF69B4", "사랑과 열정", "/images/flowers/rose.png");
        addFlower("백합", "#FFFFFF", "순수와 깨끗함", "/images/flowers/lily.png");
        addFlower("카네이션", "#FF1493", "존경과 사랑", "/images/flowers/carnation.png");
        addFlower("해바라기", "#FFD700", "행복과 긍정", "/images/flowers/sunflower.png");
    }

    private void addFlower(String name, String colorHex, String meaning, String assetUrl) {
        Flower flower = new Flower(name, colorHex, meaning, assetUrl);
        flowerRepository.save(flower);
    }
}

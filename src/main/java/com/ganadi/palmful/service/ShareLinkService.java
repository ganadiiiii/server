package com.ganadi.palmful.service;

import com.ganadi.palmful.dto.BouquetResponse;
import com.ganadi.palmful.dto.UserResponse;
import com.ganadi.palmful.entity.Bouquet;
import com.ganadi.palmful.entity.ShareLink;
import com.ganadi.palmful.entity.User;
import com.ganadi.palmful.repository.BouquetRepository;
import com.ganadi.palmful.repository.ShareLinkRepository;
import com.ganadi.palmful.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShareLinkService {

    private final ShareLinkRepository shareLinkRepository;
    private final BouquetRepository bouquetRepository;
    private final UserRepository userRepository;
    private final BouquetService bouquetService;

    @Autowired
    public ShareLinkService(ShareLinkRepository shareLinkRepository,
                            BouquetRepository bouquetRepository,
                            UserRepository userRepository,
                            BouquetService bouquetService) {
        this.shareLinkRepository = shareLinkRepository;
        this.bouquetRepository = bouquetRepository;
        this.userRepository = userRepository;
        this.bouquetService = bouquetService;
    }

    @Transactional
    public String createLink(Long userId, Long bouquetId, String channel) {
        User creator = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));
        Bouquet bouquet = bouquetRepository.findById(bouquetId)
                .orElseThrow(() -> new IllegalArgumentException("부케를 찾을 수 없습니다: " + bouquetId));

        ShareLink link = new ShareLink(bouquet, channel, creator);
        ShareLink saved = shareLinkRepository.save(link);
        return saved.getToken();
    }

    @Transactional(readOnly = true)
    public BouquetResponse getByToken(String token) {
        ShareLink link = shareLinkRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("공유 링크를 찾을 수 없습니다: " + token));
        return bouquetService.getBouquet(link.getBouquet().getId());
    }
}




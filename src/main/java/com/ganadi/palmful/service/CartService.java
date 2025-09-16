package com.ganadi.palmful.service;

import com.ganadi.palmful.dto.BouquetResponse;
import com.ganadi.palmful.dto.CartItemResponse;
import com.ganadi.palmful.entity.Bouquet;
import com.ganadi.palmful.entity.CartItem;
import com.ganadi.palmful.entity.User;
import com.ganadi.palmful.repository.BouquetRepository;
import com.ganadi.palmful.repository.CartItemRepository;
import com.ganadi.palmful.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final BouquetRepository bouquetRepository;
    private final BouquetService bouquetService;

    @Autowired
    public CartService(CartItemRepository cartItemRepository,
                       UserRepository userRepository,
                       BouquetRepository bouquetRepository,
                       BouquetService bouquetService) {
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.bouquetRepository = bouquetRepository;
        this.bouquetService = bouquetService;
    }

    @Transactional
    public CartItemResponse addItem(Long userId, Long bouquetId, Integer quantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));
        Bouquet bouquet = bouquetRepository.findById(bouquetId)
                .orElseThrow(() -> new IllegalArgumentException("부케를 찾을 수 없습니다: " + bouquetId));

        CartItem item = new CartItem(user, bouquet, quantity);
        CartItem saved = cartItemRepository.save(item);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<CartItemResponse> getCart(Long userId) {
        return cartItemRepository.findByUser_IdOrderByCreatedAtDesc(userId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public void removeItem(Long id) {
        if (!cartItemRepository.existsById(id)) {
            throw new IllegalArgumentException("장바구니 아이템이 없습니다: " + id);
        }
        cartItemRepository.deleteById(id);
    }
    
    @Transactional
    public CartItemResponse updateItem(Long id, Integer quantity) {
        CartItem item = cartItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("장바구니 아이템이 없습니다: " + id));
        
        if (quantity <= 0) {
            throw new IllegalArgumentException("수량은 1 이상이어야 합니다.");
        }
        
        item.setQuantity(quantity);
        CartItem saved = cartItemRepository.save(item);
        return toResponse(saved);
    }

    private CartItemResponse toResponse(CartItem item) {
        BouquetResponse bouquet = bouquetService.getBouquet(item.getBouquet().getId());
        return new CartItemResponse(
                item.getId(), bouquet, item.getQuantity(), item.getCreatedAt()
        );
    }
}





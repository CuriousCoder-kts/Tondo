package com.tondo.module.card.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tondo.common.response.Result;
import com.tondo.module.card.entity.Card;
import com.tondo.module.card.entity.dto.CreateCardDTO;
import com.tondo.module.card.entity.dto.ResolveCardDTO;
import com.tondo.module.card.service.CardService;
import com.tondo.module.card.service.ThanksService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;
    private final ThanksService thanksService;

    @PostMapping
    public Result<Card> createCard(@RequestAttribute("userId") Long userId,
                                   @Valid @RequestBody CreateCardDTO dto) {
        Card card = cardService.createCard(userId, dto);
        return Result.success(card);
    }

    @GetMapping("/{id}")
    public Result<Card> getCard(@PathVariable Long id) {
        Card card = cardService.getCardById(id);
        return Result.success(card);
    }

    @GetMapping
    public Result<Page<Card>> getCards(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String confusionTag,
            @RequestParam(defaultValue = "new") String sort) {
        Page<Card> cards = cardService.getCards(page, size, confusionTag, sort);
        return Result.success(cards);
    }

    @PutMapping("/{id}/resolve")
    public Result<?> resolveCard(@RequestAttribute("userId") Long userId,
                                 @PathVariable Long id,
                                 @Valid @RequestBody ResolveCardDTO dto) {
        cardService.resolveCard(userId, id, dto.getResolutionContent());
        return Result.success();
    }

    @PostMapping("/{id}/thanks")
    public Result<?> thankCard(@RequestAttribute("userId") Long userId,
                               @PathVariable Long id) {
        thanksService.thankCard(userId, id);
        return Result.success();
    }
}
package com.tondo.module.card.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tondo.common.response.Result;
import com.tondo.module.card.entity.Card;
import com.tondo.module.card.entity.dto.CreateCardDTO;
import com.tondo.module.card.entity.dto.ResolveCardDTO;
import com.tondo.module.card.entity.vo.CardVO;
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
    public Result<CardVO> getCard(@PathVariable Long id) {
        return Result.success(cardService.getCardVO(id));
    }

    @GetMapping
    public Result<Page<CardVO>> getCards(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String confusionTag,
            @RequestParam(defaultValue = "new") String sort) {
        return Result.success(cardService.getCardVOs(page, size, confusionTag, sort));
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

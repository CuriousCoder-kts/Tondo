package com.tondo.module.companion.entity.vo;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CheckinResultVO {
    private LocalDate checkinDate;
    private Integer myStreak;
    private Integer myTotalCheckins;
    private Integer relationTotalCheckins;
    private Boolean partnerCheckedInToday;
    private Integer daysRemaining;
}

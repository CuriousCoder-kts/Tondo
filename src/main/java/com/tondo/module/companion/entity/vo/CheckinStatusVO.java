package com.tondo.module.companion.entity.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CheckinStatusVO {
    private Long relationId;
    private Integer durationDays;
    private Integer daysRemaining;
    private Integer daysElapsed;
    private LocalDateTime startedAt;
    private LocalDateTime expectedEndAt;
    private String checkinFrequency;
    private Boolean checkedInToday;
    private Boolean partnerCheckedInToday;
    private Integer myStreak;
    private Integer myTotalCheckins;
    private Integer partnerTotalCheckins;
    private List<LocalDate> myCheckinDates;
    private List<LocalDate> partnerCheckinDates;
}

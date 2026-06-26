package com.tondo.module.companion.entity.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CheckinDTO {
    @Size(max = 500, message = "打卡记录最多500字")
    private String note;
}

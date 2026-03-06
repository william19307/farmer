package com.farmer.modules.map.finance.vo;

import lombok.Data;

@Data
public class IncomeThresholdVO {
    private Long threshold;           // 阈值（单位：元）
    private Long aboveCount;          // 高于阈值村数
    private Long belowCount;          // 低于阈值村数
    private Long totalCount;          // 村集体总数
    private Double abovePercent;      // 高于阈值占比
    private Double belowPercent;      // 低于阈值占比
}

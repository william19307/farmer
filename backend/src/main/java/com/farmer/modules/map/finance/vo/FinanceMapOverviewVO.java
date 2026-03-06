package com.farmer.modules.map.finance.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FinanceMapOverviewVO {
    private LocalDateTime currentTime;
    private String weather;
    private String temperature;
    private Integer countyCount;
    private Integer townshipCount;
    private Integer villageTotal;
    private Integer unclosedCount;
    private Integer noAccountCount;
}

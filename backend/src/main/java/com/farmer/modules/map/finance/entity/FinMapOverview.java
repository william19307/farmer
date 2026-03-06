package com.farmer.modules.map.finance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.farmer.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("fin_map_overview")
public class FinMapOverview extends BaseEntity {

    private Long regionId;
    private String statMonth;       // 统计年月 yyyyMM
    private Integer countyCount;    // 区县数
    private Integer townshipCount;  // 乡镇数
    private Integer villageTotal;   // 村集体总数
    private Integer unclosedCount;  // 未结账村数
    private Integer noAccountCount; // 未创建账套村数
}

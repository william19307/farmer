package com.farmer.modules.finance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.farmer.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("fin_close_period")
public class FinClosePeriod extends BaseEntity {

    private Long accountSetId;
    private String accountPeriod;   // 会计期间 yyyyMM
    private Integer status;         // 0-未结 1-已结
    private LocalDateTime closeTime;// 结账时间
    private Long closeBy;           // 结账人
    private String remark;
}

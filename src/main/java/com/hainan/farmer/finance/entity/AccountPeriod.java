package com.hainan.farmer.finance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hainan.farmer.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 账期实体
 * 对应表：t_account_period
 *
 * <p>以自然月为账期单位，结账后不允许新增/修改凭证。
 * 支持反结账（OPEN→CLOSED 双向流转），最终关闭后不可逆。</p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_account_period")
public class AccountPeriod extends BaseEntity {

    /**
     * 村集体组织 ID（多租户隔离）
     */
    private Long orgId;

    /**
     * 会计年度（如 2024）
     */
    private Integer periodYear;

    /**
     * 会计月份（1-12）
     */
    private Integer periodMonth;

    /**
     * 账期开始日期
     */
    private LocalDate startDate;

    /**
     * 账期结束日期
     */
    private LocalDate endDate;

    /**
     * 账期状态
     * 0-未结账 1-已结账 2-已关闭
     *
     * @see com.hainan.farmer.common.enums.PeriodStatusEnum
     */
    private Integer status;

    /**
     * 月结时间
     */
    private LocalDateTime closeTime;

    /**
     * 月结操作人 ID
     */
    private Long closeBy;
}

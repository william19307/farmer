package com.hainan.farmer.finance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hainan.farmer.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 记账凭证主表实体
 * 对应表：t_voucher
 *
 * <p>凭证状态流转：草稿 → 待审核 → 已审核 → 已记账 → （已作废）
 * 记账后科目余额 t_subject_balance 同步更新。
 * 借贷合计必须平衡（total_debit == total_credit）。</p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_voucher")
public class Voucher extends BaseEntity {

    /**
     * 村集体组织 ID（多租户隔离）
     */
    private Long orgId;

    /**
     * 凭证编号（年月+类型+流水，如 "2024010001"）
     */
    private String voucherNo;

    /**
     * 凭证日期
     */
    private LocalDate voucherDate;

    /**
     * 所属账期 ID，关联 t_account_period.id
     */
    private Long periodId;

    /**
     * 凭证类型
     * 1-收款凭证 2-付款凭证 3-转账凭证 4-记账凭证
     *
     * @see com.hainan.farmer.common.enums.VoucherTypeEnum
     */
    private Integer voucherType;

    /**
     * 凭证摘要（整体说明）
     */
    private String summary;

    /**
     * 借方合计金额（所有分录借方之和）
     */
    private BigDecimal totalDebit;

    /**
     * 贷方合计金额（所有分录贷方之和）
     */
    private BigDecimal totalCredit;

    /**
     * 附件张数
     */
    private Integer attchCount;

    /**
     * 凭证状态
     * 0-草稿 1-待审核 2-已审核 3-已记账 4-已作废
     *
     * @see com.hainan.farmer.common.enums.VoucherStatusEnum
     */
    private Integer status;

    /**
     * 制单人 ID
     */
    private Long makerId;

    /**
     * 制单时间
     */
    private LocalDateTime makeTime;

    /**
     * 审核人 ID
     */
    private Long auditorId;

    /**
     * 审核时间
     */
    private LocalDateTime auditTime;

    /**
     * 记账人 ID
     */
    private Long bookkeeperId;

    /**
     * 记账时间
     */
    private LocalDateTime bookTime;

    /**
     * 作废操作人 ID
     */
    private Long voidBy;

    /**
     * 作废时间
     */
    private LocalDateTime voidTime;

    /**
     * 作废原因
     */
    private String voidReason;
}

package com.hainan.farmer.finance.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 凭证分录实体
 * 对应表：t_voucher_entry
 *
 * <p>每张凭证至少包含两条分录，且借贷方向各至少一条。
 * 同一分录的 debit_amount 与 credit_amount 只允许有一个大于 0。
 * subject_code / subject_name 冗余存储，用于历史报表追溯。</p>
 */
@Data
@TableName("t_voucher_entry")
public class VoucherEntry {

    /** 主键 ID（雪花算法） */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 村集体组织 ID（多租户隔离）
     */
    private Long orgId;

    /**
     * 所属凭证 ID，关联 t_voucher.id
     */
    private Long voucherId;

    /**
     * 分录行号（从 1 开始，决定打印顺序）
     */
    private Integer rowNo;

    /**
     * 会计科目 ID，关联 t_account_subject.id
     */
    private Long subjectId;

    /**
     * 科目编码（冗余，用于报表历史追溯）
     */
    private String subjectCode;

    /**
     * 科目名称（冗余，用于报表历史追溯）
     */
    private String subjectName;

    /**
     * 分录摘要
     */
    private String summary;

    /**
     * 借方金额（借贷只能有一方 > 0）
     */
    private BigDecimal debitAmount;

    /**
     * 贷方金额（借贷只能有一方 > 0）
     */
    private BigDecimal creditAmount;

    /**
     * 币种（默认 CNY）
     */
    private String currency;

    /**
     * 汇率（本位币为 1.000000）
     */
    private BigDecimal exchangeRate;

    /**
     * 关联银行账户 ID（收付款凭证必填），关联 t_bank_account.id
     */
    private Long bankAccountId;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 最后更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}

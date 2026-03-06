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
 * 科目余额实体
 * 对应表：t_subject_balance
 *
 * <p>按 org_id + period_id + subject_id 唯一确定一条余额记录。
 * 每次凭证记账时实时更新本期发生额，月结时快照写入期末余额。
 * 期初余额 = 上月期末余额，由结账程序自动结转。</p>
 *
 * <p>余额规则（以借方余额方向科目为例）：
 * <pre>
 * 期末借方余额 = 期初借方余额 + 本期借方发生额 - 本期贷方发生额
 * </pre>
 * </p>
 */
@Data
@TableName("t_subject_balance")
public class SubjectBalance {

    /** 主键 ID（雪花算法） */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 村集体组织 ID（多租户隔离）
     */
    private Long orgId;

    /**
     * 所属账期 ID，关联 t_account_period.id
     */
    private Long periodId;

    /**
     * 会计年度（冗余，便于按年查询）
     */
    private Integer periodYear;

    /**
     * 会计月份（冗余，便于按月查询）
     */
    private Integer periodMonth;

    /**
     * 科目 ID，关联 t_account_subject.id
     */
    private Long subjectId;

    /**
     * 科目编码（冗余，用于报表历史追溯）
     */
    private String subjectCode;

    /**
     * 期初借方余额
     */
    private BigDecimal openingDebit;

    /**
     * 期初贷方余额
     */
    private BigDecimal openingCredit;

    /**
     * 本期借方发生额
     */
    private BigDecimal currentDebit;

    /**
     * 本期贷方发生额
     */
    private BigDecimal currentCredit;

    /**
     * 期末借方余额
     */
    private BigDecimal closingDebit;

    /**
     * 期末贷方余额
     */
    private BigDecimal closingCredit;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 最后更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}

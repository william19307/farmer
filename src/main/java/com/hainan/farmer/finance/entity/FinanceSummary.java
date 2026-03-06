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
 * 财务报表汇总实体
 * 对应表：t_finance_summary
 *
 * <p>月结时由系统自动生成，覆盖式写入（同 org_id+period_id+report_type+subject_id 唯一）。
 * 支持两类报表：
 * <ul>
 *   <li>资产负债表（BALANCE_SHEET）：反映期末资产、负债和权益状况</li>
 *   <li>损益表（INCOME_STATEMENT）：反映本期收入与支出情况</li>
 * </ul>
 * </p>
 */
@Data
@TableName("t_finance_summary")
public class FinanceSummary {

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
     * 报表类型
     * 1-资产负债表 2-损益表
     *
     * @see com.hainan.farmer.common.enums.ReportTypeEnum
     */
    private Integer reportType;

    /**
     * 科目 ID，关联 t_account_subject.id
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
     * 报表行次（对应国标财务报表格式中的"行次"编号）
     */
    private String lineNo;

    /**
     * 本期金额（损益表：本月发生额；资产负债表：期末余额）
     */
    private BigDecimal currentAmount;

    /**
     * 本年累计金额（损益表专用：年初至本期累计）
     */
    private BigDecimal yearAmount;

    /**
     * 上年同期金额（对比分析用）
     */
    private BigDecimal lastYearAmount;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 最后更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}

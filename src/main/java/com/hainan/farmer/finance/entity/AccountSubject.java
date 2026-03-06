package com.hainan.farmer.finance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hainan.farmer.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 会计科目实体
 * 对应表：t_account_subject
 *
 * <p>支持树形结构（无限层级），通过 parent_id 关联父节点。
 * 科目类型决定余额方向与报表归属，叶节点科目方可挂接凭证分录。</p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_account_subject")
public class AccountSubject extends BaseEntity {

    /**
     * 村集体组织 ID（多租户隔离）
     */
    private Long orgId;

    /**
     * 科目编码（唯一，如 "1001"、"1001.01"）
     */
    private String subjectCode;

    /**
     * 科目名称
     */
    private String subjectName;

    /**
     * 父科目 ID，顶级科目为 0
     */
    private Long parentId;

    /**
     * 科目层级（从 1 开始）
     */
    private Integer level;

    /**
     * 科目类型
     * 1-资产 2-负债 3-权益 4-收入 5-支出
     *
     * @see com.hainan.farmer.common.enums.SubjectTypeEnum
     */
    private Integer subjectType;

    /**
     * 余额方向
     * 1-借方 2-贷方
     *
     * @see com.hainan.farmer.common.enums.BalanceDirectionEnum
     */
    private Integer balanceDirection;

    /**
     * 是否叶节点（仅叶节点可使用）
     * 0-否 1-是
     */
    private Integer isLeaf;

    /**
     * 是否现金科目（用于现金流量表）
     * 0-否 1-是
     */
    private Integer isCash;

    /**
     * 是否银行存款科目
     * 0-否 1-是
     */
    private Integer isBank;

    /**
     * 科目状态
     * 0-停用 1-正常
     */
    private Integer status;

    /**
     * 同级排序号
     */
    private Integer sortNo;

    /**
     * 是否系统内置（内置科目不允许删除）
     * 0-否 1-是
     */
    private Integer isSystem;

    /**
     * 备注
     */
    private String remark;
}

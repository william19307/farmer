package com.farmer.modules.finance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.farmer.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("fin_asset")
public class FinAsset extends BaseEntity {

    private Long accountSetId;
    private String assetNo;             // 资产编号
    private String assetName;           // 资产名称
    private Integer assetType;          // 资产类型（1-房屋建筑 2-机器设备 3-运输设备 4-其他）
    private LocalDate purchaseDate;     // 入账日期
    private BigDecimal originalValue;   // 原值
    private BigDecimal residualRate;    // 残值率
    private Integer usefulLifeMonths;   // 预计使用月份
    private Integer deprecMethod;       // 折旧方法（1-平均年限法 2-工作量法）
    private BigDecimal accumulatedDepreciation; // 累计折旧
    private BigDecimal netValue;        // 净值（原值-累计折旧）
    private Integer status;             // 0-停用 1-在用 2-已处置
    private LocalDate lastDeprecDate;   // 最后折旧日期
    private String remark;
}

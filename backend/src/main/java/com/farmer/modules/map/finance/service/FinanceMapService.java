package com.farmer.modules.map.finance.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.farmer.modules.map.finance.entity.FinIndicators;
import com.farmer.modules.map.finance.entity.FinVillageIncome;
import com.farmer.modules.map.finance.vo.FinanceMapOverviewVO;
import com.farmer.modules.map.finance.vo.IncomeThresholdVO;

import java.math.BigDecimal;
import java.util.List;

public interface FinanceMapService {

    FinanceMapOverviewVO getOverview(Long regionId, String statMonth);

    List<IncomeThresholdVO> getIncomeStats(Long regionId, String statMonth);

    IPage<FinVillageIncome> getIncomeDetailAbove(Page<FinVillageIncome> page, Long regionId,
                                                  String statMonth, BigDecimal threshold);

    IPage<FinVillageIncome> getIncomeDetailBelow(Page<FinVillageIncome> page, Long regionId,
                                                  String statMonth, BigDecimal threshold);

    List<FinIndicators> getIndicators(Long regionId, String statMonth);
}

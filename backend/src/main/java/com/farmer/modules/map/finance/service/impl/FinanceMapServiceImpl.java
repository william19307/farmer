package com.farmer.modules.map.finance.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.farmer.modules.map.finance.entity.FinIndicators;
import com.farmer.modules.map.finance.entity.FinMapOverview;
import com.farmer.modules.map.finance.entity.FinVillageIncome;
import com.farmer.modules.map.finance.mapper.FinIndicatorsMapper;
import com.farmer.modules.map.finance.mapper.FinMapOverviewMapper;
import com.farmer.modules.map.finance.mapper.FinVillageIncomeMapper;
import com.farmer.modules.map.finance.service.FinanceMapService;
import com.farmer.modules.map.finance.vo.FinanceMapOverviewVO;
import com.farmer.modules.map.finance.vo.IncomeThresholdVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FinanceMapServiceImpl implements FinanceMapService {

    private final FinMapOverviewMapper overviewMapper;
    private final FinVillageIncomeMapper incomeMapper;
    private final FinIndicatorsMapper indicatorsMapper;

    private static final List<BigDecimal> THRESHOLDS = List.of(
            new BigDecimal("50000"),    // 5万
            new BigDecimal("100000"),   // 10万
            new BigDecimal("200000")    // 20万
    );

    @Override
    public FinanceMapOverviewVO getOverview(Long regionId, String statMonth) {
        if (statMonth == null) {
            statMonth = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        }

        FinMapOverview overview = overviewMapper.selectByRegionAndMonth(regionId, statMonth);

        FinanceMapOverviewVO vo = new FinanceMapOverviewVO();
        vo.setCurrentTime(LocalDateTime.now());
        // Weather mock - in production connect to real weather API
        vo.setWeather("晴");
        vo.setTemperature("18°C ~ 25°C");

        if (overview != null) {
            vo.setCountyCount(overview.getCountyCount());
            vo.setTownshipCount(overview.getTownshipCount());
            vo.setVillageTotal(overview.getVillageTotal());
            vo.setUnclosedCount(overview.getUnclosedCount());
            vo.setNoAccountCount(overview.getNoAccountCount());
        } else {
            vo.setCountyCount(0);
            vo.setTownshipCount(0);
            vo.setVillageTotal(0);
            vo.setUnclosedCount(0);
            vo.setNoAccountCount(0);
        }
        return vo;
    }

    @Override
    public List<IncomeThresholdVO> getIncomeStats(Long regionId, String statMonth) {
        if (statMonth == null) {
            statMonth = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        }

        Long totalCount = incomeMapper.countByRegionAndMonth(regionId, statMonth);
        List<IncomeThresholdVO> result = new ArrayList<>();

        for (BigDecimal threshold : THRESHOLDS) {
            IncomeThresholdVO vo = new IncomeThresholdVO();
            vo.setThreshold(threshold.longValue());
            vo.setTotalCount(totalCount);

            Long aboveCount = incomeMapper.countAboveThreshold(regionId, statMonth, threshold);
            Long belowCount = incomeMapper.countBelowThreshold(regionId, statMonth, threshold);
            vo.setAboveCount(aboveCount);
            vo.setBelowCount(belowCount);

            if (totalCount > 0) {
                vo.setAbovePercent(BigDecimal.valueOf(aboveCount)
                        .divide(BigDecimal.valueOf(totalCount), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100)).doubleValue());
                vo.setBelowPercent(BigDecimal.valueOf(belowCount)
                        .divide(BigDecimal.valueOf(totalCount), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100)).doubleValue());
            }
            result.add(vo);
        }
        return result;
    }

    @Override
    public IPage<FinVillageIncome> getIncomeDetailAbove(Page<FinVillageIncome> page, Long regionId,
                                                         String statMonth, BigDecimal threshold) {
        return incomeMapper.selectAboveThreshold(page, regionId, statMonth, threshold);
    }

    @Override
    public IPage<FinVillageIncome> getIncomeDetailBelow(Page<FinVillageIncome> page, Long regionId,
                                                         String statMonth, BigDecimal threshold) {
        return incomeMapper.selectBelowThreshold(page, regionId, statMonth, threshold);
    }

    @Override
    public List<FinIndicators> getIndicators(Long regionId, String statMonth) {
        if (statMonth == null) {
            statMonth = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        }
        return indicatorsMapper.selectByRegionAndMonth(regionId, statMonth);
    }
}

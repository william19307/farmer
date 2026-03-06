package com.farmer.modules.finance.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.farmer.common.exception.BizException;
import com.farmer.common.exception.ErrorCode;
import com.farmer.common.result.PageResult;
import com.farmer.modules.finance.dto.AssetDTO;
import com.farmer.modules.finance.entity.FinAsset;
import com.farmer.modules.finance.mapper.FinAssetMapper;
import com.farmer.modules.finance.service.FinAssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FinAssetServiceImpl extends ServiceImpl<FinAssetMapper, FinAsset> implements FinAssetService {

    @Override
    public PageResult<FinAsset> pageAssets(Long accountSetId, Integer assetType, Integer status, String keyword, Integer pageNum, Integer pageSize) {
        Page<FinAsset> page = new Page<>(pageNum, pageSize);
        baseMapper.selectPage(page, accountSetId, assetType, status, keyword);
        return PageResult.of(page);
    }

    @Override
    @Transactional
    public Long createAsset(AssetDTO dto) {
        FinAsset asset = new FinAsset();
        BeanUtils.copyProperties(dto, asset);
        asset.setAccumulatedDepreciation(BigDecimal.ZERO);
        asset.setNetValue(dto.getOriginalValue());
        asset.setStatus(1);
        save(asset);
        return asset.getId();
    }

    @Override
    @Transactional
    public void updateAsset(AssetDTO dto) {
        FinAsset asset = getById(dto.getId());
        if (asset == null) throw new BizException(ErrorCode.ASSET_NOT_FOUND);
        BeanUtils.copyProperties(dto, asset, "id", "accountSetId", "accumulatedDepreciation", "netValue", "status");
        updateById(asset);
    }

    @Override
    @Transactional
    public void deleteAsset(Long id) {
        FinAsset asset = getById(id);
        if (asset == null) throw new BizException(ErrorCode.ASSET_NOT_FOUND);
        if (asset.getStatus() == 1) throw new BizException(ErrorCode.SYSTEM_ERROR, "在用资产不可删除，请先停用");
        removeById(id);
    }

    @Override
    @Transactional
    public void depreciate(Long accountSetId, String period) {
        List<FinAsset> assets = baseMapper.selectActiveAssets(accountSetId);
        for (FinAsset asset : assets) {
            BigDecimal monthlyDepreciation = calculateMonthlyDepreciation(asset);
            if (monthlyDepreciation.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal newAccumulated = asset.getAccumulatedDepreciation().add(monthlyDepreciation);
                BigDecimal maxDepreciation = asset.getOriginalValue()
                        .multiply(BigDecimal.ONE.subtract(asset.getResidualRate()));
                if (newAccumulated.compareTo(maxDepreciation) > 0) {
                    newAccumulated = maxDepreciation;
                }
                asset.setAccumulatedDepreciation(newAccumulated);
                asset.setNetValue(asset.getOriginalValue().subtract(newAccumulated));
                asset.setLastDeprecDate(LocalDate.now());
                updateById(asset);
            }
        }
    }

    @Override
    public Long generateDepreciationVoucher(Long accountSetId, String period) {
        // TODO: 实现生成折旧凭证逻辑，需根据科目配置生成借：管理费用-折旧费，贷：累计折旧
        return null;
    }

    private BigDecimal calculateMonthlyDepreciation(FinAsset asset) {
        if (asset.getOriginalValue() == null || asset.getResidualRate() == null
                || asset.getUsefulLifeMonths() == null || asset.getUsefulLifeMonths() == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal depreciableAmount = asset.getOriginalValue()
                .multiply(BigDecimal.ONE.subtract(asset.getResidualRate()));
        return depreciableAmount.divide(new BigDecimal(asset.getUsefulLifeMonths()), 2, RoundingMode.HALF_UP);
    }
}

package com.farmer.modules.finance.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.farmer.common.result.PageResult;
import com.farmer.modules.finance.dto.AssetDTO;
import com.farmer.modules.finance.entity.FinAsset;

public interface FinAssetService extends IService<FinAsset> {

    PageResult<FinAsset> pageAssets(Long accountSetId, Integer assetType, Integer status, String keyword, Integer pageNum, Integer pageSize);

    Long createAsset(AssetDTO dto);

    void updateAsset(AssetDTO dto);

    void deleteAsset(Long id);

    void depreciate(Long accountSetId, String period);

    Long generateDepreciationVoucher(Long accountSetId, String period);
}

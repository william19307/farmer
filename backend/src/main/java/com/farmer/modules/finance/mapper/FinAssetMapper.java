package com.farmer.modules.finance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.farmer.modules.finance.entity.FinAsset;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FinAssetMapper extends BaseMapper<FinAsset> {

    @Select("<script>" +
            "SELECT * FROM fin_asset WHERE deleted = 0" +
            "<if test='accountSetId != null'> AND account_set_id = #{accountSetId}</if>" +
            "<if test='assetType != null'> AND asset_type = #{assetType}</if>" +
            "<if test='status != null'> AND status = #{status}</if>" +
            "<if test='keyword != null and keyword != \"\"'> AND (asset_no LIKE CONCAT('%',#{keyword},'%') OR asset_name LIKE CONCAT('%',#{keyword},'%'))</if>" +
            " ORDER BY purchase_date DESC" +
            "</script>")
    IPage<FinAsset> selectPage(Page<FinAsset> page,
                                @Param("accountSetId") Long accountSetId,
                                @Param("assetType") Integer assetType,
                                @Param("status") Integer status,
                                @Param("keyword") String keyword);

    @Select("SELECT * FROM fin_asset WHERE account_set_id = #{accountSetId} AND status = 1 AND deleted = 0")
    List<FinAsset> selectActiveAssets(@Param("accountSetId") Long accountSetId);
}

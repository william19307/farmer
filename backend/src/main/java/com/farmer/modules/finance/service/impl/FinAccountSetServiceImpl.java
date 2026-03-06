package com.farmer.modules.finance.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.farmer.common.exception.BizException;
import com.farmer.common.exception.ErrorCode;
import com.farmer.modules.finance.dto.AccountSetDTO;
import com.farmer.modules.finance.entity.FinAccountSet;
import com.farmer.modules.finance.mapper.FinAccountSetMapper;
import com.farmer.modules.finance.service.FinAccountSetService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FinAccountSetServiceImpl extends ServiceImpl<FinAccountSetMapper, FinAccountSet> implements FinAccountSetService {

    @Override
    public Long createAccountSet(AccountSetDTO dto) {
        FinAccountSet accountSet = new FinAccountSet();
        BeanUtils.copyProperties(dto, accountSet);
        accountSet.setStatus(1);
        save(accountSet);
        return accountSet.getId();
    }

    @Override
    public void updateAccountSet(AccountSetDTO dto) {
        FinAccountSet accountSet = getById(dto.getId());
        if (accountSet == null) throw new BizException(ErrorCode.ACCOUNT_SET_NOT_FOUND);
        BeanUtils.copyProperties(dto, accountSet, "id");
        updateById(accountSet);
    }
}

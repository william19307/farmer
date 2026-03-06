package com.farmer.modules.finance.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.farmer.modules.finance.dto.AccountSetDTO;
import com.farmer.modules.finance.entity.FinAccountSet;

public interface FinAccountSetService extends IService<FinAccountSet> {

    Long createAccountSet(AccountSetDTO dto);

    void updateAccountSet(AccountSetDTO dto);
}

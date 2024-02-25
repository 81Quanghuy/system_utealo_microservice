package com.trvankiet.app.service.impl;

import com.trvankiet.app.constant.ProviderType;
import com.trvankiet.app.constant.RoleType;
import com.trvankiet.app.entity.Provider;
import com.trvankiet.app.repository.ProviderRepository;
import com.trvankiet.app.service.ProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProviderServiceImpl implements ProviderService {

    private final ProviderRepository providerRepository;

}

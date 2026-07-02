package com.tekclover.wms.core.batch.config.singleton;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class AppConfig {

    @Bean("accountService")
    public AccountService getAccountService() {
        return new AccountService();
    }
}
package com.tekclover.wms.api.masters.config.dynamicConfig;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.tekclover.wms.api.masters",entityManagerFactoryRef = "entityManager", transactionManagerRef = "mastersManager")
public class DataSourceConfig {

    @Bean
    public DataSource dataSource() {

        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put("WK", wkDataSource());
        targetDataSources.put("MDU", mduDataSource());
        targetDataSources.put("CMP", cmpDataSource());
        targetDataSources.put("CHN", chnDataSource());
        targetDataSources.put("VGA", vgaDataSource());

        DynamicDataSource ds = new DynamicDataSource();
        ds.setTargetDataSources(targetDataSources);
        ds.setDefaultTargetDataSource(wkDataSource());

        return ds;
    }

    private DataSource wkDataSource() {
        return buildDataSource(
                "WK",
                "jdbc:sqlserver://10.10.6.30;databaseName=WMS_WK",
                "sa",
                "SuHcHQR72nxvyJx6EPpoOsK4V"
        );
    }
    private DataSource mduDataSource() {
        return buildDataSource(
                "MDU",
                "jdbc:sqlserver://10.10.6.30;databaseName=WMS_WK_PRD",
                "sa",
                "SuHcHQR72nxvyJx6EPpoOsK4V"
        );
    }

    private DataSource cmpDataSource() {
        return buildDataSource(
                "CMP",
                "jdbc:sqlserver://10.10.10.61;databaseName=WMS_CBE",
                "sa",
                "4V7lOXaxgAi3i6mgJL7qBUSPM"
        );
    }

    private DataSource chnDataSource() {
        return buildDataSource(
                "CHN",
                "jdbc:sqlserver://10.10.10.61;databaseName=WMS_CHN",
                "sa",
                "4V7lOXaxgAi3i6mgJL7qBUSPM"
        );
    }

    private DataSource vgaDataSource() {
        return buildDataSource(
                "VGA",
                "jdbc:sqlserver://10.10.6.30;databaseName=WMS_VGA",
                "sa",
                "SuHcHQR72nxvyJx6EPpoOsK4V"
        );
    }

    private DataSource buildDataSource(String poolName, String jdbcUrl, String username, String password) {

        HikariConfig config = new HikariConfig();
        config.setPoolName("HIKARI-" + poolName);
        config.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        // SAFE POOL SIZE (PER DB)
//        config.setMaximumPoolSize(6);
//        config.setMinimumIdle(3);
//        config.setConnectionTimeout(30000);
//        config.setIdleTimeout(300000);
//        config.setMaxLifetime(1800000);
//        config.setAutoCommit(false);

        // SQL Server recommended
        config.addDataSourceProperty("encrypt", "false");
        config.addDataSourceProperty("trustServerCertificate", "true");

        return new HikariDataSource(config);
    }

    // ================= ENTITY MANAGER =================
    @Bean(name = "entityManager")
    public LocalContainerEntityManagerFactoryBean entityManager() {

        LocalContainerEntityManagerFactoryBean emf =
                new LocalContainerEntityManagerFactoryBean();

        emf.setDataSource(dataSource());
        emf.setPackagesToScan("com.tekclover.wms.api.masters.model");
        emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        emf.setJpaProperties(hibernateProperties());

        return emf;
    }

    @Bean(name = "mastersManager")
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager tm = new JpaTransactionManager();
        tm.setEntityManagerFactory(entityManager().getObject());
        return tm;
    }

    // ================= HIBERNATE PROPS =================
    private Properties hibernateProperties() {
        Properties props = new Properties();
        props.put("hibernate.dialect", "org.hibernate.dialect.SQLServer2012Dialect");
        props.put("hibernate.show_sql", "false");
        props.put("hibernate.format_sql", "false");
        props.put("hibernate.jdbc.batch_size", "20");
        props.put("hibernate.order_inserts", "true");
        props.put("hibernate.order_updates", "true");
        return props;
    }






}

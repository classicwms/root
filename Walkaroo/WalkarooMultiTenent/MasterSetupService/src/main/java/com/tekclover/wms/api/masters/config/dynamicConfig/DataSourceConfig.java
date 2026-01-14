package com.tekclover.wms.api.masters.config.dynamicConfig;

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
@EnableJpaRepositories(basePackages = "com.tekclover.wms.api.masters",entityManagerFactoryRef = "entityManager", transactionManagerRef = "multiTransactionManager")
public class DataSourceConfig {

    @Bean
    public DynamicDataSource dataSource() {
        // Setup the target data sources (you can dynamically load these from a DB or config)
        Map<Object, Object> targetDataSources = new HashMap<>();

        // Example data source configurations (replace with actual configuration)
        DataSource db = createDataSource("WK");
        DataSource db1 = createDataSource("MDU");
        DataSource db2 = createDataSource("CMP");

        targetDataSources.put("WK",db);
        targetDataSources.put("MDU",db1);
        targetDataSources.put("CMP",db2);

        DynamicDataSource dataSource = new DynamicDataSource();
        dataSource.setTargetDataSources(targetDataSources);
        dataSource.setDefaultTargetDataSource(db); // Default database (e.g., db1)
        return dataSource;
    }

    private DataSource createDataSource(String dbName) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        // Configure database connection here
        switch (dbName) {
            case "WK":
                dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                dataSource.setUrl("jdbc:sqlserver://10.10.14.24;databaseName=WMS_WK");
                dataSource.setUsername("sa");
                dataSource.setPassword("Sd2se5y3mPD9BLr3QzZMyNU1V");
                break;
            case "MDU":
                dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                dataSource.setUrl("jdbc:sqlserver://10.10.14.24;databaseName=WMS_WK_PRD");
                dataSource.setUsername("sa");
                dataSource.setPassword("Sd2se5y3mPD9BLr3QzZMyNU1V");
                break;
            case "CMP":
                dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                dataSource.setUrl("jdbc:sqlserver://10.10.14.24;databaseName=WMS_CMP");
                dataSource.setUsername("sa");
                dataSource.setPassword("Sd2se5y3mPD9BLr3QzZMyNU1V");
                break;
            default:
                throw new IllegalArgumentException("Unknown database: " + dbName);
        }
        return dataSource;
    }

    @Bean(name = "entityManager")
    public LocalContainerEntityManagerFactoryBean entityManager() throws PropertyVetoException {

        LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactory.setDataSource(dataSource());
        entityManagerFactory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        entityManagerFactory.setPackagesToScan("com.tekclover.wms.api.masters.model");
        entityManagerFactory.setJpaProperties(hibernateProperties());
        return entityManagerFactory;
    }

    @Bean(name = "multiTransactionManager")
    public PlatformTransactionManager multiTransactionManager() throws PropertyVetoException {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManager().getObject());
        return transactionManager;
    }



//    private Properties hibernateProperties() {
//        Properties properties = new Properties();
//        properties.put("hibernate.show_sql", true);
//        properties.put("hibernate.format_sql", true);
//        return properties;
//    }



    private Properties hibernateProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.dialect", "org.hibernate.dialect.SQLServer2012Dialect");  // Adjust for your DB
        properties.put("hibernate.hbm2ddl.auto", "update");  // Or "validate" for production
        properties.put("hibernate.show_sql", "false");  // Logs the generated SQL
        properties.put("hibernate.format_sql", "false");  // Pretty formats the SQL
        properties.put("hibernate.use_sql_comments", "false");  // Adds comments to SQL for better debugging
        properties.put("spring.jpa.hibernate.ddl-auto", "update");
//        properties.put("spring.datasource.hikari.maximumPoolSize", "20");
//        properties.put("spring.datasource.hikari.minimum-idle", "5");
//        properties.put("spring.datasource.hikari.idleTimeout", "180000");
//        properties.put("spring.datasource.hikari.maxLifetime", "1800000");
//        properties.put("spring.datasource.hikari.connection-timeout", "30000");
        return properties;
    }






}

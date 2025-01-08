package com.tekclover.wms.api.idmaster.config.dynamicConfig;

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
@EnableJpaRepositories(basePackages = "com.tekclover.wms.api.idmaster",entityManagerFactoryRef = "entityManager", transactionManagerRef = "multiTransactionManager")
public class DataSourceConfig {

//    @Primary
//    @Bean(name = "primaryDataSource")
//    @ConfigurationProperties(prefix = "spring.datasource.primary")
//    public DataSource primaryDataSource() {
//        return DataSourceBuilder.create().build();
//    }
//
//    @Bean(name = "impexDataSource")
//    @ConfigurationProperties(prefix = "spring.datasource.impex")
//    public DataSource impexDataSource() {
//        return DataSourceBuilder.create().build();
//    }
//
//    @Bean(name = "walkarooDataSource")
//    @ConfigurationProperties(prefix = "spring.datasource.walkaroo")
//    public DataSource walkarooDataSource() {
//        return DataSourceBuilder.create().build();
//    }
//
//    @Bean(name = "dataSource")
//    public DataSource dataSource() {
//
//        Map<Object, Object> dataSourceMap = new HashMap<>();
//
//        dataSourceMap.put("primary", primaryDataSource());
//        dataSourceMap.put("impex", impexDataSource());
//        dataSourceMap.put("walkaroo", walkarooDataSource());
//
//
//        DynamicDataSource routingDataSource = new DynamicDataSource();
//        routingDataSource.setDefaultTargetDataSource(primaryDataSource());
//        routingDataSource.setTargetDataSources(dataSourceMap);
//        return routingDataSource;
//    }
//
//    @Bean(name = "entityManager")
//    public LocalContainerEntityManagerFactoryBean entityManager() throws PropertyVetoException {
//
//        LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
//        entityManagerFactory.setDataSource(dataSource());
//        entityManagerFactory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
//        entityManagerFactory.setPackagesToScan("com.tekclover.wms.api.transaction.model");
//        entityManagerFactory.setJpaProperties(hibernateProperties());
//        return entityManagerFactory;
//    }
//
//    @Bean(name = "multiTransactionManager")
//    public PlatformTransactionManager multiTransactionManager() throws PropertyVetoException {
//        JpaTransactionManager transactionManager = new JpaTransactionManager();
//        transactionManager.setEntityManagerFactory(entityManager().getObject());
//        return transactionManager;
//    }
//
//    private Properties hibernateProperties() {
//        Properties properties = new Properties();
//        properties.put("hibernate.show_sql", true);
//        properties.put("hibernate.format_sql", true);
//        return properties;
//    }

//    --------------------------------

//    @Bean
//    @Primary
//    public DataSource dataSource() {
//        DynamicDataSource dynamicDataSource = new DynamicDataSource();
//
//        // Set the primary (default) data source
//        Map<Object, Object> targetDataSources = new HashMap<>();
//
//        // Primary data source (default)
//        DataSource primaryDataSource = DataSourceBuilder.create()
//                .url("jdbc:sqlserver://3.111.134.239;databaseName=wms_imf")
//                .username("sa")
//                .password("Do1cavIFK4^$pQ^zZYsX")
//                .driverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver")
//                .build();
//
//        targetDataSources.put("primary", primaryDataSource);
//
//        // Secondary data source
//        DataSource secondaryDataSource = DataSourceBuilder.create()
//                .url("jdbc:sqlserver://3.111.134.239;databaseName=WMS_IMPEX")
//                .username("sa")
//                .password("Do1cavIFK4^$pQ^zZYsX")
//                .driverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver")
//                .build();
//
//        targetDataSources.put("secondary", secondaryDataSource);
//
//        dynamicDataSource.setTargetDataSources(targetDataSources);
//        dynamicDataSource.setDefaultTargetDataSource(primaryDataSource); // Set the default database
//
//        return dynamicDataSource;
//    }

    @Bean
    public DynamicDataSource dataSource() {
        // Setup the target data sources (you can dynamically load these from a DB or config)
        Map<Object, Object> targetDataSources = new HashMap<>();

        // Example data source configurations (replace with actual configuration)
        DataSource db = createDataSource("IMF");
        DataSource db1 = createDataSource("IMPEX");
        DataSource db2 = createDataSource("WK");

        targetDataSources.put("IMF",db);
        targetDataSources.put("IMPEX", db1);
        targetDataSources.put("WK", db2);

        DynamicDataSource dataSource = new DynamicDataSource();
        dataSource.setTargetDataSources(targetDataSources);
        dataSource.setDefaultTargetDataSource(db); // Default database (e.g., db1)
        return dataSource;
    }

    private DataSource createDataSource(String dbName) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        // Configure database connection here
        switch (dbName) {
            case "IMF":
                dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                dataSource.setUrl("jdbc:sqlserver://3.111.134.239;databaseName=wms_imf");
                dataSource.setUsername("sa");
                dataSource.setPassword("Do1cavIFK4^$pQ^zZYsX");
                break;
            case "IMPEX":
                dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                dataSource.setUrl("jdbc:sqlserver://3.111.134.239;databaseName=WMS_IMPEX");
                dataSource.setUsername("sa");
                dataSource.setPassword("Do1cavIFK4^$pQ^zZYsX");
                break;
            case "WK":
                dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                dataSource.setUrl("jdbc:sqlserver://3.111.134.239;databaseName=WMS_WK");
                dataSource.setUsername("sa");
                dataSource.setPassword("Do1cavIFK4^$pQ^zZYsX");
                break;
            default:
                throw new IllegalArgumentException("Unknown database: " + dbName);
        }
        return dataSource;
    }

//    @Bean
//    public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder, DataSource dataSource) {
//        return builder
//                .dataSource(dataSource)
//                .packages("com.tekclover.wms.api.transaction.model")
//                .persistenceUnit("primary")
//                .properties(hibernateProperties())
//                .build();
//    }
//
//    @Bean
//    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
//        return new JpaTransactionManager(entityManagerFactory);
//    }
//
//    private Map<String, Object> hibernateProperties() {
//        Map<String, Object> properties = new HashMap<>();
//        properties.put("hibernate.dialect", "org.hibernate.dialect.SQLServer2012Dialect");
//        properties.put("hibernate.show_sql", "true");
//        properties.put("hibernate.format_sql", "true");
//        return properties;
//    }

    @Bean(name = "entityManager")
    public LocalContainerEntityManagerFactoryBean entityManager() throws PropertyVetoException {

        LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactory.setDataSource(dataSource());
        entityManagerFactory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        entityManagerFactory.setPackagesToScan("com.tekclover.wms.api.idmaster.model");
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
        properties.put("hibernate.show_sql", "true");  // Logs the generated SQL
        properties.put("hibernate.format_sql", "true");  // Pretty formats the SQL
        properties.put("hibernate.use_sql_comments", "true");  // Adds comments to SQL for better debugging
        return properties;
    }






}

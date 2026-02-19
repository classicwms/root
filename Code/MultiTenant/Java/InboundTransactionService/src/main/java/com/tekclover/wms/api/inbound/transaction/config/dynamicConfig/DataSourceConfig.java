package com.tekclover.wms.api.inbound.transaction.config.dynamicConfig;

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
@EnableJpaRepositories(basePackages = "com.tekclover.wms.api.inbound.transaction",entityManagerFactoryRef = "entityManager", transactionManagerRef = "multiTransactionManager")
public class DataSourceConfig {

    @Bean
    public DynamicDataSource dataSource() {
        // Setup the target data sources (you can dynamically load these from a DB or config)
        Map<Object, Object> targetDataSources = new HashMap<>();

        // Example data source configurations (replace with actual configuration)
        DataSource db = createDataSource("MT");
        DataSource db1 = createDataSource("IMPEX");
        DataSource db2 = createDataSource("WK");
        DataSource db3 = createDataSource("ALM");
        DataSource db4 = createDataSource("NAMRATHA");
        DataSource db5 = createDataSource("REEFERON");
        DataSource db6 = createDataSource("KNOWELL");
        DataSource db7 = createDataSource("FAHAHEEL");
        DataSource db8 = createDataSource("AUTO_LAP");


        targetDataSources.put("MT",db);
        targetDataSources.put("IMPEX", db1);
        targetDataSources.put("WK", db2);
        targetDataSources.put("ALM",db3);
        targetDataSources.put("NAMRATHA",db4);
        targetDataSources.put("REEFERON", db5);
        targetDataSources.put("KNOWELL", db6);
        targetDataSources.put("FAHAHEEL", db7);
        targetDataSources.put("AUTO_LAP", db8);

        DynamicDataSource dataSource = new DynamicDataSource();
        dataSource.setTargetDataSources(targetDataSources);
        dataSource.setDefaultTargetDataSource(db); // Default database (e.g., db1)
        return dataSource;
    }

    private DataSource createDataSource(String dbName) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        // Configure database connection here
        switch (dbName) {
            case "MT":
                dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                dataSource.setUrl("jdbc:sqlserver://10.20.0.19;databaseName=WMS_MT");
                dataSource.setUsername("sa");
                dataSource.setPassword("TvHcGBR84nxvyJx6EPpoOsL5V");
                break;
            case "ALM":
                dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                dataSource.setUrl("jdbc:sqlserver://10.20.0.19;databaseName=WMS_ALMDEV_SPLIT");
                dataSource.setUsername("sa");
                dataSource.setPassword("TvHcGBR84nxvyJx6EPpoOsL5V");
                break;
            case "NAMRATHA":
                dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                dataSource.setUrl("jdbc:sqlserver://10.20.0.19;databaseName=WMS_NAMRATHA");
                dataSource.setUsername("sa");
                dataSource.setPassword("TvHcGBR84nxvyJx6EPpoOsL5V");
                break;
            case "IMPEX":
                dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                dataSource.setUrl("jdbc:sqlserver://10.20.0.19;databaseName=WMS_IMPEX");
                dataSource.setUsername("sa");
                dataSource.setPassword("TvHcGBR84nxvyJx6EPpoOsL5V");
                break;
            case "WK":
                dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                dataSource.setUrl("jdbc:sqlserver://10.20.0.19;databaseName=WMS_WK");
                dataSource.setUsername("sa");
                dataSource.setPassword("TvHcGBR84nxvyJx6EPpoOsL5V");
                break;
            case "REEFERON":
                dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                dataSource.setUrl("jdbc:sqlserver://10.20.0.19;databaseName=REEFERON_DEV");
                dataSource.setUsername("sa");
                dataSource.setPassword("TvHcGBR84nxvyJx6EPpoOsL5V");
                break;
            case "KNOWELL":
                dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                dataSource.setUrl("jdbc:sqlserver://10.20.0.19;databaseName=WMS_KNOWELL");
                dataSource.setUsername("sa");
                dataSource.setPassword("TvHcGBR84nxvyJx6EPpoOsL5V");
                break;
            case "FAHAHEEL":
                dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                dataSource.setUrl("jdbc:sqlserver://10.20.0.19;databaseName=WMS_FAHAHEEL");
                dataSource.setUsername("sa");
                dataSource.setPassword("TvHcGBR84nxvyJx6EPpoOsL5V");
                break;
            case "AUTO_LAP":
                dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                dataSource.setUrl("jdbc:sqlserver://10.20.0.19;databaseName=WMS_AUTO_LAP");
                dataSource.setUsername("sa");
                dataSource.setPassword("TvHcGBR84nxvyJx6EPpoOsL5V");
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
        entityManagerFactory.setPackagesToScan("com.tekclover.wms.api.inbound.transaction.model");
        entityManagerFactory.setJpaProperties(hibernateProperties());
        return entityManagerFactory;
    }

    @Bean(name = "multiTransactionManager")
    public PlatformTransactionManager multiTransactionManager() throws PropertyVetoException {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManager().getObject());
        return transactionManager;
    }

    private Properties hibernateProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.dialect", "org.hibernate.dialect.SQLServer2012Dialect");  // Adjust for your DB
        properties.put("hibernate.hbm2ddl.auto", "update");  // Or "validate" for production
        properties.put("hibernate.show_sql", "false");  // Logs the generated SQL
        properties.put("spring.jpa.hibernate.ddl-auto", "update");  // Logs the generated SQL
        properties.put("hibernate.format_sql", "false");  // Pretty formats the SQL
        properties.put("hibernate.use_sql_comments", "false");  // Adds comments to SQL for better debugging
        return properties;
    }

}

package com.tekclover.wms.api.inbound.orders.config.dynamicConfig;

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
@EnableJpaRepositories(basePackages = "com.tekclover.wms.api.inbound.orders",entityManagerFactoryRef = "entityManager", transactionManagerRef = "multiTransactionManager")
public class DataSourceConfig {

    @Bean
    public DynamicDataSource dataSource() {
        // Setup the target data sources (you can dynamically load these from a DB or config)
        Map<Object, Object> targetDataSources = new HashMap<>();

        // Example data source configurations (replace with actual configuration)
//        DataSource db = createDataSource("MT");
//        DataSource db1 = createDataSource("ALM");
        DataSource db2 = createDataSource("FAHAHEEL");
//        DataSource db3 = createDataSource("JAHRA");

//
//        targetDataSources.put("MT",db);
//        targetDataSources.put("ALM",db1);
        targetDataSources.put("FAHAHEEL", db2);
//        targetDataSources.put("JAHRA", db3);

        DynamicDataSource dataSource = new DynamicDataSource();
        dataSource.setTargetDataSources(targetDataSources);
        dataSource.setDefaultTargetDataSource(db2); // Default database (e.g., db1)
        return dataSource;
    }

    private DataSource createDataSource(String dbName) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        // Configure database connection here
        switch (dbName) {
//            case "MT":
//                dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//                dataSource.setUrl("jdbc:sqlserver://10.10.22.24;databaseName=wms_imf");
//                dataSource.setUsername("sa");
//                dataSource.setPassword("9SOwjgFjm0sM7qMOFz16mICJUx");
//                break;
//            case "ALM":
//                dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//                dataSource.setUrl("jdbc:sqlserver://10.10.22.24;databaseName=WMS_ALMDEV_SPLIT");
//                dataSource.setUsername("sa");
//                dataSource.setPassword("9SOwjgFjm0sM7qMOFz16mICJUx");
//                break;
            case "FAHAHEEL":
                dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                dataSource.setUrl("jdbc:sqlserver://10.10.22.24;databaseName=WMS_FAHAHEEL");
                dataSource.setUsername("sa");
                dataSource.setPassword("9SOwjgFjm0sM7qMOFz16mICJUx");
                break;
//            case "JAHRA":
//                dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//                dataSource.setUrl("jdbc:sqlserver://10.10.22.24;databaseName=WMS_FAHAHEEL");
//                dataSource.setUsername("sa");
//                dataSource.setPassword("9SOwjgFjm0sM7qMOFz16mICJUx");
//                break;
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
        entityManagerFactory.setPackagesToScan("com.tekclover.wms.api.inbound.orders.model");
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
        properties.put("hibernate.format_sql", "false");  // Pretty formats the SQL
        properties.put("hibernate.use_sql_comments", "false");  // Adds comments to SQL for better debugging
        return properties;
    }

}

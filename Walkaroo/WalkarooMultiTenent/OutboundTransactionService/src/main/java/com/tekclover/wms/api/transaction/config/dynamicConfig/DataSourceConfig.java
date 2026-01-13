package com.tekclover.wms.api.transaction.config.dynamicConfig;

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
@EnableJpaRepositories(basePackages = "com.tekclover.wms.api.transaction",entityManagerFactoryRef = "entityManager", transactionManagerRef = "multiTransactionManager")
public class DataSourceConfig {

//    @Bean
//    public DynamicDataSource dataSource() {
//        // Setup the target data sources (you can dynamically load these from a DB or config)
//        Map<Object, Object> targetDataSources = new HashMap<>();
//
//        // Example data source configurations (replace with actual configuration)
//        DataSource db = createDataSource("WK");
//        DataSource db1 = createDataSource("MDU");
//        DataSource db2 = createDataSource("CMP");
//
//
//        targetDataSources.put("WK",db);
//        targetDataSources.put("MDU",db1);
//        targetDataSources.put("CMP",db2);
//
//        DynamicDataSource dataSource = new DynamicDataSource();
//        dataSource.setTargetDataSources(targetDataSources);
//        dataSource.setDefaultTargetDataSource(db); // Default database (e.g., db1)
//        return dataSource;
//    }

    // ================= DYNAMIC DATASOURCE =================
    @Bean
    public DataSource dataSource() {

        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put("WK", wkDataSource());
        targetDataSources.put("MDU", mduDataSource());
        targetDataSources.put("CMP", cmpDataSource());

        DynamicDataSource ds = new DynamicDataSource();
        ds.setTargetDataSources(targetDataSources);
        ds.setDefaultTargetDataSource(wkDataSource());

        return ds;
    }

    // ================= HIKARI PER DB =================
    private DataSource wkDataSource() {
        return buildDataSource(
                "WK",
                "jdbc:sqlserver://10.10.14.24;databaseName=WMS_WK"
        );
    }
    private DataSource mduDataSource() {
        return buildDataSource(
                "MDU",
                "jdbc:sqlserver://10.10.14.24;databaseName=WMS_WK_PRD"
        );
    }

    private DataSource cmpDataSource() {
        return buildDataSource(
                "CMP",
                "jdbc:sqlserver://10.10.14.24;databaseName=WMS_CMP"
        );
    }

    private DataSource buildDataSource(String poolName, String jdbcUrl) {

        HikariConfig config = new HikariConfig();
        config.setPoolName("HIKARI-" + poolName);
        config.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        config.setJdbcUrl(jdbcUrl);
        config.setUsername("sa");
        config.setPassword("Sd2se5y3mPD9BLr3QzZMyNU1V");

        // 🔥 SAFE POOL SIZE (PER DB)
        config.setMaximumPoolSize(5);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(300000);
        config.setMaxLifetime(1800000);
        config.setAutoCommit(false);

        // SQL Server recommended
        config.addDataSourceProperty("encrypt", "false");
        config.addDataSourceProperty("trustServerCertificate", "true");

        return new HikariDataSource(config);
    }


//    private DataSource createDataSource(String dbName) {
//        DriverManagerDataSource dataSource = new DriverManagerDataSource();
//        // Configure database connection here
//        switch (dbName) {
//            case "WK":
//                dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//                dataSource.setUrl("jdbc:sqlserver://10.10.14.24;databaseName=WMS_WK");
//                dataSource.setUsername("sa");
//                dataSource.setPassword("Sd2se5y3mPD9BLr3QzZMyNU1V");
//                break;
//            case "MDU":
//                dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//                dataSource.setUrl("jdbc:sqlserver://10.10.14.24;databaseName=WMS_WK_PRD");
//                dataSource.setUsername("sa");
//                dataSource.setPassword("Sd2se5y3mPD9BLr3QzZMyNU1V");
//                break;
//            case "CMP":
//                dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//                dataSource.setUrl("jdbc:sqlserver://10.10.14.24;databaseName=WMS_CMP");
//                dataSource.setUsername("sa");
//                dataSource.setPassword("Sd2se5y3mPD9BLr3QzZMyNU1V");
//                break;
//            default:
//                throw new IllegalArgumentException("Unknown database: " + dbName);
//        }
//        return dataSource;
//    }

    // ================= ENTITY MANAGER =================
    @Bean(name = "entityManager")
    public LocalContainerEntityManagerFactoryBean entityManager() {

        LocalContainerEntityManagerFactoryBean emf =
                new LocalContainerEntityManagerFactoryBean();

        emf.setDataSource(dataSource());
        emf.setPackagesToScan("com.tekclover.wms.api.transaction.model");
        emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        emf.setJpaProperties(hibernateProperties());

        return emf;
    }

    @Bean
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
        props.put("hibernate.jdbc.batch_size", "30");
        props.put("hibernate.order_inserts", "true");
        props.put("hibernate.order_updates", "true");
        return props;
    }

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


    @Bean(name = "multiTransactionManager")
    public PlatformTransactionManager multiTransactionManager() throws PropertyVetoException {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManager().getObject());
        return transactionManager;
    }


//    // ================= HIBERNATE PROPS =================
//    private Properties hibernateProperties() {
//        Properties props = new Properties();
//        props.put("hibernate.dialect", "org.hibernate.dialect.SQLServer2012Dialect");
//        props.put("hibernate.show_sql", "false");
//        props.put("hibernate.format_sql", "false");
//        props.put("hibernate.jdbc.batch_size", "30");
//        props.put("hibernate.order_inserts", "true");
//        props.put("hibernate.order_updates", "true");
//        return props;
//    }

}

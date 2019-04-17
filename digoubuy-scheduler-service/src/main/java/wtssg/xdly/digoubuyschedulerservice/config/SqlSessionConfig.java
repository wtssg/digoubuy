package wtssg.xdly.digoubuyschedulerservice.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = {"wtssg.xdly.stock.dao"}, sqlSessionFactoryRef = "stockSqlSessionFactory",
        sqlSessionTemplateRef = "stockSqlSessionTemplate")
public class SqlSessionConfig {

    @Autowired
    @Qualifier("stockDataSource")
    private DataSource stockDataSource;

    @Bean(name = "stockSqlSessionFactory")
    public SqlSessionFactory stockSqlSessionFactory() throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(stockDataSource);
        return factoryBean.getObject();
    }

    @Bean(name = "stockSqlSessionTemplate")
    public SqlSessionTemplate stockSqlSessionTemplate() throws Exception {
        return new SqlSessionTemplate(stockSqlSessionFactory());
    }
}


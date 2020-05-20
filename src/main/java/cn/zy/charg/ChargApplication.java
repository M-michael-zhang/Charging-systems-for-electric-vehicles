package cn.zy.charg;

import cn.zy.charg.dao.PileMapper;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

import java.util.TimeZone;

import static org.springframework.boot.SpringApplication.*;

@SpringBootApplication
//@PropertySource("classpath:application.yml")
@MapperScan(basePackages="cn.zy.charg.dao")

public class ChargApplication {
	private static final Logger LOG = LoggerFactory.getLogger(ChargApplication.class);
	public static void main(String[] args) {
//		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
		run(ChargApplication.class, args);
	}

}

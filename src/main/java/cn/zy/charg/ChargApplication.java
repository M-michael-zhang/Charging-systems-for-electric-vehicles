package cn.zy.charg;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

import static org.springframework.boot.SpringApplication.*;

@SpringBootApplication
//@PropertySource("classpath:application.yml")
@MapperScan(basePackages="cn.zy.charg.dao")
public class ChargApplication {

	public static void main(String[] args) {
		run(ChargApplication.class, args);
	}

}

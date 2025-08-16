package julyjolly.librarysystem;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 项目启动类
 */
@MapperScan("julyjolly.librarysystem.mapper")
@SpringBootApplication
public class BookManageSysApplication {
    public static void main(String[] args) {
        SpringApplication.run(BookManageSysApplication.class, args);
    }
}

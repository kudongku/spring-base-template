package kr.soulware.teen_mydata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class TeenMydataApplication {

    public static void main(String[] args) {
        SpringApplication.run(TeenMydataApplication.class, args);
    }

}

package de.szut.lf8_starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class Lf8StarterApplication {

    public static void main(String[] args) {
        SpringApplication.run(Lf8StarterApplication.class, args);
    }

}

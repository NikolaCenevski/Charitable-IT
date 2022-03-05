package it.finki.charitable;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CharitableApplication {

    public static void main(String[] args) {
        SpringApplication.run(CharitableApplication.class, args);
    }

}


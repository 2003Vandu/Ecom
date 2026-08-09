package com.eComm.eComm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableWebSecurity
public class ECommApplication {

	public static void main(String[] args) {
		SpringApplication.run(ECommApplication.class, args);
        System.out.println("pos billing System  Backend.. ");
	}

}

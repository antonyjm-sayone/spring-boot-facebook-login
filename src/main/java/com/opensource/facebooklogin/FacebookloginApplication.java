package com.opensource.facebooklogin;

import com.opensource.facebooklogin.token.TokenProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.opensource.facebooklogin")
@EnableConfigurationProperties(TokenProperties.class)
public class FacebookloginApplication {

	public static void main(String[] args) {
		SpringApplication.run(FacebookloginApplication.class, args);
	}

}

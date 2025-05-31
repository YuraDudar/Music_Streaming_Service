package ru.SberPo666.security_service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.UUID;

@SpringBootApplication
public class SecurityServiceApplication {

	public static void main(String[] args) throws JsonProcessingException {
		SpringApplication.run(SecurityServiceApplication.class, args);
	}

}

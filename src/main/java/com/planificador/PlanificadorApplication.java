package com.planificador;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class PlanificadorApplication {
	
	public static void main(String[] args){
		SpringApplication.run(PlanificadorApplication.class, args);
	}
}

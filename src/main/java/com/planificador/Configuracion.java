package com.planificador;

import org.springframework.context.annotation.Configuration;

import com.utilities.MessagesQueue;
import com.utilities.PasswordEncoder;

import org.springframework.context.annotation.Bean;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.modelmapper.ModelMapper;

@Configuration
public class Configuracion {

	@Bean
	public ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();
		return modelMapper;
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new PasswordEncoder();
	}
	
	@Bean
	public MessagesQueue messagesQueue(){
		ExecutorService hilo = Executors.newFixedThreadPool(1);
		MessagesQueue messagesQueue = new MessagesQueue();
		hilo.execute(messagesQueue);
		return messagesQueue;
	}
}

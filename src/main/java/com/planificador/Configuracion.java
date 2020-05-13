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

	private ExecutorService threadPool = Executors.newFixedThreadPool(1);
	
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
		MessagesQueue messagesQueue = new MessagesQueue();
		threadPool.execute(messagesQueue);
		return messagesQueue;
	}
}

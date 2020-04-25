package com.utilities;

import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.springframework.mail.SimpleMailMessage;

public class MessagesQueue implements Runnable{
	private LinkedList<SimpleMailMessage> messages;
	private ReentrantReadWriteLock lock;

	public void add(String to, String subject, String text) {
		lock.writeLock().lock();
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(to);
		message.setSubject(subject);
		message.setText(text);
		messages.add(message);
		lock.writeLock().unlock();
	}
	
	public void send() {
		lock.writeLock().lock();
		SimpleMailMessage message = messages.getFirst();
		lock.writeLock().unlock();
		if(message != null) {
			// enviar
		}
	}
	
	@Override
	public void run() {
		while(true)
		send();
	}
}

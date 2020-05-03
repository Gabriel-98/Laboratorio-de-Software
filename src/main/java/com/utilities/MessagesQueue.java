package com.utilities;

import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.springframework.mail.SimpleMailMessage;

public class MessagesQueue implements Runnable{
	private LinkedList<SimpleMailMessage> messages;
	private ReentrantReadWriteLock lock;

	public MessagesQueue() {
		messages = new LinkedList<SimpleMailMessage>();
		lock = new ReentrantReadWriteLock();
	}
	
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
		SimpleMailMessage message = messages.pollFirst();
		lock.writeLock().unlock();
		if(message != null) {
			// enviar
			System.out.println(message.getTo()[0]);
			System.out.println(message.getSubject());
			System.out.println(message.getText());
		}
	}
	
	@Override
	public void run() {
		while(true)
		send();
	}
}

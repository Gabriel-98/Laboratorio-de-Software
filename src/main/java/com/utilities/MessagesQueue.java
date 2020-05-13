package com.utilities;

import java.util.LinkedList;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

public class MessagesQueue implements Runnable{
	private LinkedList<SimpleMailMessage> messages;
	private ReentrantReadWriteLock lock;
	private JavaMailSenderImpl mailSender;

	public MessagesQueue() {
		messages = new LinkedList<SimpleMailMessage>();
		mailSender = new JavaMailSenderImpl();
		mailSender.setHost("smtp.gmail.com");
	    mailSender.setPort(587);

	    mailSender.setUsername("lab.software.2020.01@gmail.com");
	    mailSender.setPassword("Password705486594478");
		     
		Properties props = mailSender.getJavaMailProperties();
	    props.put("mail.transport.protocol", "smtp");
	    props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		
		lock = new ReentrantReadWriteLock();
	}
	
	private boolean hasNext(){
		lock.readLock().lock();
		boolean ans = !messages.isEmpty();
		lock.readLock().unlock();
		return ans;
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
			mailSender.send(message);
			System.out.println(message.getTo()[0]);
			System.out.println(message.getSubject());
			System.out.println(message.getText());
		}
	}
	
	@Override
	public void run() {
		while(true) {
			try{ Thread.sleep(100); }
			catch(Exception e){}
			while(hasNext())
			send();
		}
	}
}

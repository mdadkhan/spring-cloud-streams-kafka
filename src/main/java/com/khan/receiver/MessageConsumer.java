package com.khan.receiver;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import com.khan.pojo.Employee;

@Service
public class MessageConsumer {
	
	Logger log = LoggerFactory.getLogger(MessageConsumer.class);
	
	
    @Bean
//    public Consumer<Emp> msgReceiver() {
    public Consumer<Message<Employee>> msgReceiver() {
        return message -> {
            log.error(Thread.currentThread().getName() + " - Received: " + message.getPayload());
            // Simulate processing delay
            try { 
            	Employee emp = (Employee)message.getPayload();
            	message.getHeaders().forEach((key, value) -> System.err.println(key + ": " + value));
            	log.info("First Name : " + emp.getFirstName());
//            	throw new RuntimeException("Simulated processing error for " + emp.getFirstName());
            } 
            catch (Exception e) { 
            	//Thread.currentThread().interrupt(); 
            	e.printStackTrace();
            }
        };
        
        // Another easy way to consume the message with the top function definition.
        
//        return employee -> {
//            System.out.println("Consumed: " + employee.getFirstName());
//        };
        
    }

}

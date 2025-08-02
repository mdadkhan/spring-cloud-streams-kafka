package com.khan.controller;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.khan.pojo.Employee;
import com.khan.service.SimpleEmpService;


@RestController
public class SimpleRestController {
	
	@Autowired
	SimpleEmpService simpleEmpService;
	
	@Autowired
	private StreamBridge streamBridge;
	
		
	Logger log = LoggerFactory.getLogger(SimpleRestController.class);
	
	@RequestMapping(method = RequestMethod.GET, path = "/employeeMsg/{employeeId}",produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity simpleGetMethodApi(@PathVariable String employeeId) {
		
		Employee emp = simpleEmpService.findEmployeeByEmpId(Long.parseLong(employeeId));
		
		log.info("Msg Sending : {}", emp);
		//queue.offer(emp);
        //return "Sent: " + emp.getLastName();
		
		
//		boolean result = streamBridge.send("msgreceiver-communication", emp);
//		System.out.println("result : >>> " + result);
		
		// Sending a message with custom header
		Message<Employee> message = MessageBuilder.withPayload(emp)
		        .setHeader("custom-header", "header-value")
		        .build();
//		streamBridge.send("msgreceiver-communication", message);
		
		// Sending a message to a specific output channel (binding name)
	    streamBridge.send("msgSender-out-0", message);
		    

		return ResponseEntity.ok(emp);
	}
	
//	@Bean
//    public Supplier<Message<Emp>> msgSender() {
//        return () -> {
//            Emp emp = queue.poll();
//            if (emp != null) {
//                return MessageBuilder.withPayload(emp).build();
//            }
//            return null; // Nothing to send
//        };
//    }

}

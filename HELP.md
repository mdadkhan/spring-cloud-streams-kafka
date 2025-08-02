<link href="style.css" rel="stylesheet"></link>

# Read Me First
URLs to invoke the service:
 
GET http://localhost:8096/api/employeeMsg/11

GET http://localhost:8096/api/employeeMsg/12

GET http://localhost:8096/api/employeeMsg/13


# Getting Started

### Spring Cloud 2023.0.0 used. (2023.0.3 has some object serialization issue)

#### Advantage of using spring-cloud-stream is that we can replace rabbitMQ to kafka or any other broker very easily. It can be used as abstraction.
#### Ideal way is to use function, supplier, consumer model which is functional programming model. For the sake of brevity, I have simplified it. 

		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-stream</artifactId>
		</dependency>
		<dependency>
		    <groupId>org.springframework.cloud</groupId>
		    <artifactId>spring-cloud-starter-stream-kafka</artifactId>
		</dependency>	
			
### Reference Documentation


### Guides
Total number of exchanges that will be created is based on how many *-in-0 you have.

Total number of queues that will be created is based on how many *-in-0 you have.

e.g.:<br>
total partitions in 1 topic: 10 & a consumer group with 5 consumers, 
each consumer will be assigned 2 partitions. 
total partitions in 1 topic: 5 & a consumer group with 10 consumers, 
5 consumers will be idle
<I>
* @KafkaListener: can be used that gives you direct Kafka-specific access but It will tie up with messaging platform and defeats the spring-cloud-stream's purpose.

* How to send messages from REST controller in Spring Cloud Stream Kafka (without StreamBridge)? <br> Ans: Use Supplier<Flux<T>> and Sinks.Many<T> to push messages asynchronously.

* What is idempotence in Kafka producer? <br>
Ans: when enable.idempotence=true, Kafka ensures messages are not duplicated due to retries.

</I>
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)

### Maven Parent overrides

Due to Maven's design, elements are inherited from the parent POM to the project POM.
While most of the inheritance is fine, it also inherits unwanted elements like `<license>` and `<developers>` from the parent.
To prevent this, the project POM contains empty overrides for these elements.
If you manually switch to a different parent and actually want the inheritance, you need to remove those overrides.


# Kafka Implementation with Spring Cloud Stream

## Table of Contents
- [What is Apache Kafka?](#what-is-apache-kafka)
- [How Kafka Works](#how-kafka-works)
- [Spring Cloud Stream Overview](#spring-cloud-stream-overview)
- [Project Implementation Details](#project-implementation-details)
- [Message Flow in This Project](#message-flow-in-this-project)
- [Configuration Breakdown](#configuration-breakdown)

---

## What is Apache Kafka?

Apache Kafka is a distributed event streaming platform capable of handling trillions of events a day. It's designed for high-throughput, fault-tolerant, scalable messaging between applications.

### Key Kafka Concepts

| Concept | Description |
|---------|-------------|
| **Topics** | Categories or feeds to which messages are published |
| **Producers** | Applications that publish messages to topics |
| **Consumers** | Applications that subscribe to topics and process messages |
| **Partitions** | Topics are split into partitions for parallelism and scalability |
| **Consumer Groups** | Consumers organized into groups for load balancing |
| **Brokers** | Kafka servers that store and serve messages |
| **Offsets** | Unique identifier for each message within a partition |

---

## How Kafka Works

### Architecture Overview

```
┌─────────────┐     ┌──────────────────────────────────┐     ┌─────────────┐
│  Producer   │────▶│         Kafka Cluster            │────▶│  Consumer   │
│             │     │  ┌────────────────────────────┐  │     │   Group     │
└─────────────┘     │  │    Topic: my-topic         │  │     └─────────────┘
                    │  │  ┌────┐ ┌────┐ ┌────┐      │  │
                    │  │  │ P0 │ │ P1 │ │ P2 │      │  │
                    │  │  └────┘ └────┘ └────┘      │  │
                    │  └────────────────────────────┘  │
                    │         Broker 1, 2, 3...        │
                    └──────────────────────────────────┘
```

### Message Flow
1. **Producers** send messages to topics
2. Messages are distributed across **partitions** (based on key or round-robin)
3. Each partition is an ordered, immutable sequence of messages
4. **Consumers** in a group share the work of reading partitions
5. Each partition is consumed by exactly one consumer in a group

### Key Characteristics
- **Durability**: Messages are persisted to disk and replicated
- **Scalability**: Horizontal scaling through partitioning
- **High Throughput**: Batching and compression for performance
- **Ordering**: Guaranteed order within a partition

---

## Spring Cloud Stream Overview

Spring Cloud Stream is a framework for building message-driven microservices. It provides an abstraction layer over messaging middleware.

### Core Features

1. **Binder Abstraction**: Switch between message brokers (Kafka, RabbitMQ, etc.) without code changes
2. **Functional Programming Model**: Use `Supplier`, `Function`, and `Consumer` interfaces
3. **Declarative Configuration**: Configure bindings through properties
4. **Built-in Error Handling**: Automatic retry and DLQ (Dead Letter Queue) support

### Programming Model

```java
// Supplier - Produces messages
@Bean
public Supplier<String> source() {
    return () -> "Hello World";
}

// Function - Transforms messages
@Bean
public Function<String, String> processor() {
    return payload -> payload.toUpperCase();
}

// Consumer - Consumes messages
@Bean
public Consumer<String> sink() {
    return payload -> System.out.println(payload);
}
```

### Why Spring Cloud Stream?

| Benefit | Description |
|---------|-------------|
| **Vendor Independence** | Switch between Kafka, RabbitMQ, or other brokers easily |
| **Simplified Development** | Focus on business logic, not infrastructure |
| **Production-Ready** | Built-in health checks, metrics, and error handling |
| **Spring Integration** | Seamlessly works with Spring Boot ecosystem |

---

## Project Implementation Details

This project implements a Kafka-based messaging system using Spring Cloud Stream.

### Dependencies (from pom.xml)

```xml
<!-- Spring Cloud Stream Core -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-stream</artifactId>
</dependency>

<!-- Kafka Binder -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-stream-kafka</artifactId>
</dependency>

<!-- JSON Serialization -->
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
</dependency>
```

### Project Structure

```
src/main/java/com/khan/
├── SpringCloudStreamsKafkaApplication.java  # Main application
├── controller/
│   └── SimpleRestController.java            # REST API & Producer
├── pojo/
│   └── Employee.java                        # Message payload
├── receiver/
│   └── MessageConsumer.java                 # Kafka Consumer
└── service/
    └── SimpleEmpService.java                # Business logic
```

### Components

#### 1. Message Producer (SimpleRestController)
- Uses `StreamBridge` to send messages programmatically
- Sends `Employee` objects as messages
- Adds custom headers to messages
- REST endpoint triggers message sending

#### 2. Message Consumer (MessageConsumer)
- Implements functional `Consumer<Message<Employee>>` interface
- Processes incoming messages asynchronously
- Logs message details and headers
- Handles errors gracefully

#### 3. Data Model (Employee)
- POJO representing the message payload
- Serialized to JSON for transport

---

## Message Flow in This Project

```
┌──────────────┐    ┌───────────────────┐    ┌─────────────┐    ┌───────────────┐    ┌─────────────────┐
│  REST Client │───▶│ SimpleRestController │───▶│ StreamBridge │───▶│  Kafka Topic  │───▶│ MessageConsumer │
│              │    │                   │    │             │    │               │    │                 │
│ HTTP GET     │    │ Employee Lookup   │    │ Serialize   │    │ Partition     │    │ Process Message │
│ /api/...     │    │ Add Headers       │    │ to JSON     │    │ Assignment    │    │ Log Details     │
└──────────────┘    └───────────────────┘    └─────────────┘    └───────────────┘    └─────────────────┘
```

### Step-by-Step Flow

1. **Client Request**: HTTP GET to `/api/employeeMsg/{employeeId}`
2. **Employee Lookup**: `SimpleEmpService` retrieves employee data
3. **Message Creation**: Controller creates message with payload and headers
4. **Publishing**: StreamBridge sends to configured output binding
5. **Kafka Broker**: Routes to topic based on configuration
6. **Consumption**: Consumer function processes the message
7. **Processing**: Logs employee details and headers

---

## Configuration Breakdown

### Typical application.yaml Structure

```yaml
spring:
  application:
    name: spring-cloud-streams-kafka
    
  cloud:
    stream:
      # Kafka Binder Configuration
      kafka:
        binder:
          brokers: localhost:9092
          auto-create-topics: true
          
      # Binding Configuration
      bindings:
        # Consumer Binding
        msgReceiver-in-0:
          destination: msgreceiver-communication
          group: ${spring.application.name}
          consumer:
            concurrency: 4
            max-attempts: 3
            
        # Producer Binding
        msgSender-out-0:
          destination: msgreceiver-communication
          producer:
            partitionCount: 8
            partitionKeyExpression: payload.id
```

### Key Configuration Elements

| Property | Purpose |
|----------|---------|
| `brokers` | Kafka broker address |
| `auto-create-topics` | Automatically create topics |
| `destination` | Kafka topic name |
| `group` | Consumer group ID |
| `concurrency` | Number of parallel consumers |
| `max-attempts` | Retry count before DLQ |
| `partitionCount` | Number of topic partitions |
| `partitionKeyExpression` | SpEL expression for partitioning |

---

## Running the Application

### Prerequisites
- Apache Kafka running on `localhost:9092`
- Java 17+
- Maven

### Start Kafka (Docker Option)

```bash
docker-compose up -d
```

Or manually:

```bash
# Start Zookeeper
bin/zookeeper-server-start.sh config/zookeeper.properties

# Start Kafka Broker
bin/kafka-server-start.sh config/server.properties
```

### Run Application

```bash
./mvnw spring-boot:run
```

### Test Endpoints

```bash
# Send employee message
curl http://localhost:8097/api/employeeMsg/11
curl http://localhost:8097/api/employeeMsg/12
```

### Monitor Topics

```bash
# List topics
kafka-topics --list --bootstrap-server localhost:9092

# View messages
kafka-console-consumer --topic msgreceiver-communication \
  --bootstrap-server localhost:9092 --from-beginning
```

---

## Advantages of This Implementation

| Advantage | Description |
|-----------|-------------|
| **Scalability** | Easily scale consumers by increasing concurrency |
| **Fault Tolerance** | Built-in retry and DLQ handling |
| **Flexibility** | Switch to RabbitMQ by changing dependencies |
| **Maintainability** | Clean separation of concerns |
| **Performance** | Asynchronous processing with partitioning |

---

## Best Practices Demonstrated

1. **Functional Programming**: Modern Spring Cloud Stream functional style
2. **Error Handling**: Graceful error handling with retry logic
3. **Logging**: Comprehensive logging for debugging
4. **Configuration**: Externalized configuration via YAML
5. **Separation of Concerns**: Controllers, services, and consumers are separated

---

## Additional Resources

- [Apache Kafka Documentation](https://kafka.apache.org/documentation/)
- [Spring Cloud Stream Reference](https://docs.spring.io/spring-cloud-stream/docs/current/reference/html/)
- [Spring Cloud Stream Kafka Binder](https://docs.spring.io/spring-cloud-stream-binder-kafka/docs/current/reference/html/spring-cloud-stream-binder-kafka.html)

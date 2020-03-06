#Aufgabe

Die Fluggesellschaft möchte die Anwendung so erweitern, das beim Erstellen oder Löschen von Routes,
über das User Interface, eine Nachricht an alle Apps gesendet wird. 

1. Rabbit MQ Message Service installieren 
2. Route Service Nachrichten Senden beim Erstellen oder Löschen von Routen 
3. Flight Service Nachrichten Empfangen beim Erstellen oder Löschen von Routen   

## Vorbereitung 
Bitte Löschen Sie alle Apps in der Cloud und 

## Login 
**Project: cna-pas-springio-asynchrone-route-service-start**

```
cf login -a api.run.pivotal.io -u <user> -p <password> 

```

## Create Message Broker  
**Project: cna-pas-springio-asynchrone-route-service-start**

Erstellen Sie einen RabbitMQ Cloud Service von **CloudAMQ**

```
cf marketplace 
cf cs cloudamqp lemur message-queue 
```

## Manifest erweitern 
**Project: cna-pas-springio-asynchrone-route-service-start**

Erweitern Sie das Manifest um den Cloud Service ```message-queue``. 

```
---
applications:
  - name: routeService 
    services:
    - sql-mysql-cleardb
    - service-registry
    - message-queue

```


## Spring Cloud Stream Dependency Prüfen 
**Project: cna-pas-springio-asynchrone-route-service-start**

Wir verwenden das Messaging Framework **Spring Cloud Stream** und binden die Messages an den Message Broker **RabbitMQ**. 

```
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-stream-rabbit</artifactId>
</dependency>

```

## Implement an Message Producer
**Project: cna-pas-springio-asynchrone-route-service-start**

Implementieren Sie nun einen Message Publisher, der ein **EntityChangedEvent** sendet. 

```java
@EnableBinding(Source.class)
public class RouteChangedMessagePublisher {
	
	protected final static Logger logger = LoggerFactory.getLogger(RouteChangedMessagePublisher.class);

	@Autowired
	private Source source;

	public void save(Long id) {
		source.output().send(MessageBuilder.withPayload(new EntityChangedCommand(id, "Route", "Create")).build());
		logger.info("Publish EntityChangedCommand Save Route with ID " + id);
	}

}

```

## Configuration of the Message Queue 
**Project: cna-pas-springio-asynchrone-route-service-start**

Die Nachricht soll an das **TopicExchange** mit dem Namen **EntityChangedCommand** geschickt 
und als JSON Dokument versendet werden.  

```java
# Output Channel 
spring.cloud.stream.bindings.output.contentType=application/json
spring.cloud.stream.bindings.output.destination=EntityChangedCommand
```

## Build & Deploy 

```
mvn clean package
cf push 

```

## Check Publishing  
Erstellen Sie eine neue Route über die Oberfläche. Die Prüfung erfolgt anhand des Logs. 

 
```
cf logs routeService --recent 

```






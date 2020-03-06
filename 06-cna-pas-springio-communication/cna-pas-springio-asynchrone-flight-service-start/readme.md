## Task 

Die Fluggesellschaft **Aero** möchte die Anwendung so erweitern, das die App Flight Service nachen dem 
Erstellen oder Löschen einer Route über die Obefläche die gesendete Nachricht empfängt und die Daten 
anpasst.  


1. Erwetiern Sie die App Flight Service so, dass diese eine Änderungsnachricht empfängt   
2. Synchronisieren Sie abhängig von der empfangenen Nachricht die Daten
 


## First Login 

```
cf login -a api.run.pivotal.io -u <user> -p <password> 

```

## Create Message Broker  
Erstellen Sie einen RabbitMQ Cloud Service von **CloudAMQ**

```
cf cs cloudamqp lemur message-queue 

```

## Manifest erweitern 
Erweitern Sie das Manifest um den Cloud Service ```message-queue``. 

```
---
applications:
  - name: routeService 
    services:
    - autoscaler
    - sql-mysql-cleardb
    - service-registry
    - message-queue

```


## Add Spring Cloud Stream Dependency  
Wir verwenden das Messaging Framework **Spring Cloud Stream** und binden die Messages an den Message Broker **RabbitMQ**. 

```
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-stream-rabbit</artifactId>
</dependency>

```

## Implement an Message Producer

```Java
@EnableBinding(Sink.class)
public class RouteChangedMessageConsumer {
	
	protected static Logger logger = LoggerFactory.getLogger(RouteChangedMessageConsumer.class);
	
	@Autowired
	private FlightRepository flightRepository;
	
	
	@StreamListener(target = Sink.INPUT)
	public void input(EntityChangedCommand entityChangedCommand) {
		
		if(entityChangedCommand.getType().equals("Route")&&entityChangedCommand.getCommand().equals("Save")) {
			// Handle Save ...
			logger.info("Received Command EntityCangedCommand for Route Entity with ID " + entityChangedCommand.getId() );
			flightRepository.save(new Flight(0L,LocalDate.now(),entityChangedCommand.getId()));
			
		}
	}
}
```

## Configuration of the Message Queue 

```java
# Output Channel 
spring.cloud.stream.bindings.input.contentType=application/json
spring.cloud.stream.bindings.input.destination=EntityChangedCommand
```

## Build & Deploy 

```
mvn clean package
cf push 

```

## Check Publishing  
Erstellen Sie eine neue Route über die Oberfläche. Die Prüfung erfolgt anhand des Logs. 

 
```
cf logs flightService --recent 

```

Sie können auch die Daten in der Datenbank überprüfen. 


#Task

Die Fluggesellschaft **Aero** möchte die Anwendung so erweitern, das beim Erstellen oder Löschen von Routes
über das User Interface eine Nachricht gesendet an alle Apps gesendet wird. 

1. Senden Sie eine Message, wenn eine Route neu erstelle oder gelöscht wird. Die Message soll die 
Operation, den Typen und die ID der Route enthalten. 


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






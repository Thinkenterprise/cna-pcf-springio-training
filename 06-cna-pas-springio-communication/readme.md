## Aufgabe Synchrone 



1. Erstellen Sie in **Pivotal Web Services einen** einen **Service Registry Cloud Service**
2. Erwetiern Sie den Route Service so, dass er sich selbstÃ¤ndig an der Service Registry
   anmeldet und andere Services in der Cloud Ã¼ber die Service Registry findet 
2. Erweitern Sie den Route Service so, dass er den Flight Service aufruft um die Anzahl der FlÃ¼ge fÃ¼r ein Route zu ermitteln


## Vorbereitung  
** Project: cna-pas-springio-synchrone-route-service-start**

Löschen Sie alle Apps in der Cloud.


## Login 
** Project: cna-pas-springio-synchrone-route-service-start**

```
cf login -a api.run.pivotal.io -u <user> -p <password> 

```


## Erstellen Sie die Service Registry als Cloud Service 
** Project: cna-pas-springio-synchrone-route-service-start**


```
cf marketplace
cf cs p-service-registry trial service-registry 

```

Add Service Registry Cloud Service to the Manifest 


## Manifest erweitern 
** Project: cna-pas-springio-synchrone-route-service-start**

```
applications:
  - name: routeService 
    services:
    - sql-mysql-cleardb
    - service-registry


```

## Pivotal Spring Cloud Services Dependency prüfen  
** Project: cna-pas-springio-synchrone-route-service-start**
   

Zunächst ist die Spring Cloud Services BOM einzufügen.

```
<dependencyManagement>
		<dependencies>
		<dependency>
			<groupId>io.pivotal.spring.cloud</groupId>
			<artifactId>spring-cloud-services-dependencies</artifactId>
			<version>3.1.1.RELEASE</version>
			<type>pom</type>
			<scope>import</scope>
		</dependency>
	</dependencies>
</dependencyManagement>
```

Es folgt dann der Spring Cloud Services Starter.

```
<dependency>
	<groupId>io.pivotal.spring.cloud</groupId>
	<artifactId>spring-cloud-services-starter-service-registry</artifactId>
</dependency>

```

## Enable  Discovery & Self-Registration
** Project: cna-pas-springio-synchrone-route-service-start**

Die Freigabe der **Discovery** und **Self Registration** Funktion erfolgt durch die Annotation **@EnableDiscoveryClient**


```
@SpringBootApplication
@EnableDiscoveryClient
public class Application implements ApplicationRunner {
	

```

## Build 
** Project: cna-pas-springio-synchrone-route-service-start**


```
mvn clean package

```

## Deployment
** Project: cna-pas-springio-synchrone-route-service-start**
 
```
cf push 

```

## Check
** Project: cna-pas-springio-synchrone-route-service-start**
 
Check the registration of the Route Service over the **Application Manager** Service Administration oder Ã¼ber das   

Nun ist zu überprüfen, ob der Route Service sich an der Registry angemeldet hat. Das können Sie über den **Application Manager** oder das 
**Spring Cloud Service Plugin** tun. 

Sofern Sie das Plugin verwenden möchten, ist dieses zuvor zu installieren. 

```
cf add-plugin-repo CF-Community https://plugins.cloudfoundry.org
cf install-plugin -r CF-Community "spring-cloud-services"
```

```
cf service-registry-info service-registry
cf service-registry-list service-registry

```

## Load Balancer Dependency prüfen 
** Project: cna-pas-springio-synchrone-route-service-start**

Der Zugriff auf den Flight Service soll über einen **Client-side Load Balancer** erfolgen. Wir wollen allerdings nicht 
Ribbon, sondern die Implementierung aus Spring Cloud Loadbalancer verwenden. 

```
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-loadbalancer</artifactId>
</dependency>

```

## Disable Ribbon over Properties
** Project: cna-pas-springio-synchrone-route-service-start**

Da nun zwei Implementierungen auf dem Classpath sind, ist Ribbon explizit auszuschalten. 

```
spring.cloud.loadbalancer.ribbon.enabled=false

```


## Load Balanced Rest Template 
** Project: cna-pas-springio-synchrone-route-service-start**

Fuer einen REST API Aufruf auf dem Flight Service, benoetigen Sie ein Rest API Template 

Um ein Load Balanced Rest API Template zu erzeugen ist die Annotation **@LoadBalanced** notwendig. 


```
@Bean
@LoadBalanced
public RestTemplate restTemplate() {
	return new RestTemplate();
}

```

## Add Resilience Dependency 
** Project: cna-pas-springio-synchrone-route-service-start**

Mit der Resilience4J Dependency steht der Circuite Breaker aus dem Spring Cloud Resilience4J zur Verfuegung, der Netflix Hystrix ersetzt. 

```
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-circuitbreaker-resilience4j</artifactId>
</dependency>

```

## Disable Hystrix over Properties 
** Project: cna-pas-springio-synchrone-route-service-start**


Da nun zwei Implementierungen auf dem Classpath sind, ist Hystrix explizit auszuschalten. 

```
spring.cloud.circuitbreaker.hystrix.enabled=false

```

## Circuit Breaker
** Project: cna-pas-springio-synchrone-route-service-start**
 

Umschliesen Sie den Aufruf auf den Fligt Service mit einem einfachen Circuite Breaker. 


```
@Component
public class RouteService {
	
	
	private RestTemplate restTemplate;
	
	private CircuitBreaker circuitBreaker;
	
	@Autowired
	public RouteService(RestTemplate restTemplate, CircuitBreakerFactory  circuitBreakerFactory) {
		this.restTemplate=restTemplate;
		this.circuitBreaker=circuitBreakerFactory.create("routeService");   
	
	}
	
	public Long flightCount(Long id) {
		return restTemplate.getForObject("http://flightService/flights/count/{id}", Long.class, id);
	}
	

	public Supplier<Long> flightCountSuppplier(Long id) {
		return () -> this.flightCount(id);
	}
	
	
	 public Long flightCountResilence(Long id) {
		 return circuitBreaker.run(this.flightCountSuppplier(id), t -> { return -1L; });
		 
	 }
	 
}

```

## Build 
** Project: cna-pas-springio-synchrone-route-service-start**


```
mvn clean package

```


## Deployment
** Project: cna-pas-springio-synchrone-route-service-start**
 
```
cf push 

```


## Route Service Prüfen 
** Project: cna-pas-springio-synchrone-route-service-start**
 
Rufen Sie die UI des Route Service über den Browser auf. Es sollten alle nun alle Routen mit Angabe der Anzahl der Flüge **-1** erscheinen. 
Da wir den Flight Service noch nicht deployed haben greift der Circuite Breaker und der Fallback gibt -1 zurück. 



## Flight Service Deployen  
** Project: cna-pas-springio-synchron-flight-service-start**

Der Flight Service ist soweit implementiert. Sie können sich gerne die Implementierung des Flight Service nochmal in Ruhe anschauen. 



## Build 
** Project: cna-pas-springio-synchron-flight-service-start**


```
mvn clean package

```


## Deployment
** Project: cna-pas-springio-synchron-flight-service-start**
 
```
cf push 

```

## Route Service Prüfen 
** Project: cna-pas-springio-synchrone-route-service-start**
 
Rufen Sie die UI des Route Service über den Browser auf. Es sollten alle nun die Anzahl der Flüge für eine Route 1 und die anderen Routes auf 0 stehen. 


#Aufgabe Asynchrone 

Die Fluggesellschaft möchte die Anwendung so erweitern, das beim Erstellen oder Löschen von Routes,
über das User Interface, eine Nachricht an alle Apps gesendet wird. 

1. Rabbit MQ Message Service installieren 
2. Route Service Nachrichten Senden beim Erstellen oder Löschen von Routen 
3. Flight Service Nachrichten Empfangen beim Erstellen oder Löschen von Routen   

## Vorbereitung 
Bitte Löschen Sie alle Apps in der Cloud.
 

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
**Project: cna-pas-springio-asynchrone-route-service-start**

```
mvn clean package
cf push 

```

## Check Publishing  
**Project: cna-pas-springio-asynchrone-route-service-start**

Starten Sie das Log und erstellen Sie eine neue Route über die Oberfläche. 

```
cf logs routeService  

```

## Manifest erweitern 
**Project: cna-pas-springio-asynchrone-flight-service-start**

Wir kommen nun zum Flight Service, der das **EntityChangedCommand** empfangen und verarbeiten soll. 

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
**Project: cna-pas-springio-asynchrone-flight-service-start**

Wir verwenden das Messaging Framework **Spring Cloud Stream** und binden die Messages an den Message Broker **RabbitMQ**. 

```
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-stream-rabbit</artifactId>
</dependency>

```

## Message Consumer implementieren  

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
spring.cloud.stream.bindings.input.group=Command
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





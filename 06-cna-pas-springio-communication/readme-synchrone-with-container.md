# Precondition
In der **Container** Umgebung wird die Infratruktur über die **Docker** bereitgestellt. Sie benötigen daher einen **Docker-Engine** Installation auf Ihrem Rechner. 


# Infrastructure Setup 
Bitte führen Sie dazu die folgenden Schritte aus. 


## Infrastruktur hochfahren 
Über **Docker Compose** wird die Infrastruktur für die synchrone Kommunikation, bestehend aus der **Service Registry** und **MySQL**, gestartet. 

```
docker compose -f synchrone-system-containers-startup.yml up -d
```


Die Funktionsfähigkeit der Infrastruktur kann wie folgt für die Service Registry 

 ```
 http://localhost:8761
```

und für die Datenbank über die MySQL Workbench mit den Zugangsdaten user ```Thinkenterprise``` und dem Passwort ```Thinkenterprise20!``` gestestet werden. 



# Tasks
1. Erweitern Sie den **Route Service** so, dass er sich selbständig an der Service Registry
   anmeldet und andere Services in der Cloud über die Service Registry findet 
2. Erweitern Sie den **Route Service** so, dass er den Flight Service aufruft um die Anzahl der Flüge für ein Route zu ermitteln
3. Erwetiern Sie den **Route Service** um einen **Load Balancer** 
4. Erweitern Sie den **Route Service** um einen ***Circuit Breaker** 


# Route Service an der Service Registry anmelden  

## Dependency
Bitte fügen Sie unter dem Profil **eureka** folgende Dependency für das Produkt **Service Registry** in das ``pom.pom`` ein. 

```
<profile>
	<id>eureka</id>
	<dependencies>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
		</dependency>
	</dependencies>
</profile>
```



## Configuration 
Bitte fügen Sie unter dem Profil **eureka** folgende Konfiguration für das Produkt **Netflix Eureka** in das ``application.yaml`` ein.

```
eureka:
  client:
    serviceUrl: 
      defaultZone: http://localhost:8761/eureka/
    initialInstanceInfoReplicationIntervalSeconds: 5 
    registryFetchIntervalSeconds: 5
  instance:
    leaseRenewalIntervalInSeconds: 5
    leaseExpirationDurationInSeconds: 5
    
```

## Implementation 
Die Freigabe der **Discovery** und **Self Registration** Funktion erfolgt durch die Annotation **@EnableDiscoveryClient**
Bitte fügen Sie folgende Implementierung in das File ``Application.java`` ein. 

```java

@SpringBootApplication
@EnableDiscoveryClient
public class Application implements ApplicationRunner {
}

```

## Build
Bauen Sie die Anwendung wie folgt. 

```
mvn clean package -Peureka.mysql
```

## Deployment
Die Anwendung wird derzeit nicht über einen Container z.B. in einem Kubernetes deoloyed sondern direkt über die Konsole gestartet, damit die Anwendung in den Übungen besser testbar ist. 

## Build
BDie Anwendung wird unter ``/target`` wie folgt gestartet.

```
java -jar cna-pas-springio-synchron-route-service-start-0.0.1-SNAPSHOT.jar
```


## Test 
Nun ist zu ueberpruefen, ob der Route Service sich an der Registry angemeldet hat. Dazu kann die Administrationskonsole der Service Registry aufgerufen werden um zu prüfen ob sich der Route Service angemeldet hat. 

 ```
 http://localhost:8761
```

# Route Service mit Loadbalancer austatten 


## Dependency
Bitte fügen Sie unter dem Profil **loadbalancer** folgende Dependency für das Produkt **Spring Cloud Loadbalancer** in das ``pom.pom`` ein. 

```
<profile>
	<id>loadbalancer</id>
	<dependencies>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-loadbalancer</artifactId>
		</dependency>
	</dependencies>
</profile>
```

## Implementation 

Bitte fügen Sie folgende Implementierung in das File ``LoadbalancerConfiguration.java`` ein. 

```java
@Bean
@LoadBalanced
public RestTemplate restTemplate() {
	return new RestTemplate();
}
```

# Route Service mit Circuit Breaker ausstatten 

## Dependency
Bitte fügen Sie unter dem Profil **resilience4j** folgende Dependency für das Produkt **Resilience4J** in das ``pom.pom`` ein. 


```
<profile>
	<id>resilience4j</id>
		<dependencies>
			<dependency>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>spring-cloud-starter-circuitbreaker-resilience4j</artifactId>
			</dependency>
		</dependencies>
</profile>
```

## Implementation 
Umschliesen Sie den Aufruf auf des Fligt Service mit einem einfachen Circuite Breaker. 
Bitte fügen Sie folgende Implementierung in das File ``RouteService`` ein. 

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
Bauen Sie die Anwendung wie folgt. 

```
mvn clean package -Peureka,loadbalancer,resilience4j,mysql

```

## Deployment
Die Anwendung wird derzeit nicht über einen Container z.B. in einem Kubernetes deoloyed sondern direkt über die Konsole gestartet, damit die Anwendung in den Übungen besser testbar ist. 


## Run
Die Anwendung wird unter ``/target`` wie folgt gestartet.

```
java -jar cna-pas-springio-synchron-route-service-start-0.0.1-SNAPSHOT.jar 
```


## Test 
Testen Sie die Anwendung wie folgt. Rufen Sie die UI des Route Service ueber den Browser auf. Es sollten nun alle Routen mit Angabe der Anzahl der Fluege **-1** erscheinen. Da wir den Flight Service noch nicht deployed haben greift der Circuite Breaker und der Fallback gibt -1 zurueck. 



# Flight Service Deployen  

Der Flight Service ist soweit implementiert, sodass er sich iwe der Route Service an der Service Registry anmeldet. Sie koennen sich gerne die Implementierung des Flight Service nochmal in Ruhe anschauen und danach die weiteren Schritte ausführen.  



## Build
Bauen Sie die Anwendung wie folgt. 

```
mvn clean package -Peureka,mysql 
```

## Deployment
Die Anwendung wird derzeit nicht über einen Container z.B. in einem Kubernetes deoloyed sondern direkt über die Konsole gestartet, damit die Anwendung in den Übungen besser testbar ist. 


## Run
Die Anwendung wird unter ``/target`` wie folgt gestartet.

```
java -jar cna-pas-springio-synchron-route-service-start-0.0.1-SNAPSHOT.jar 
```

# Route Service prüfen  


## Test  
Rufen Sie die den Route Service ueber den Browser auf. Es sollte nun die Anzahl der Fluege fuer eine Route 1 und die anderen Routes auf 0 stehen. 


# Postcondition
Bitte führen Sie noch folgende Schritte durch um die Aufgabe abzuschliesen. 
Das Herunterfahren der Infrastruktur erfolgt über 

```
docker compose -f synchrone-system-containers-startup.yml down
```







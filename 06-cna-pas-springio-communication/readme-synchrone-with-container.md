# Precondition
In der **Container** Umgebung wird die Infratruktur über **Docker** bereitgestellt. Sie benötigen daher einen **Docker-Desktop** Installation auf Ihrem Rechner. 


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
Die bestehende Service Architektur soll um das Discovery-Pattern erweitert werden. Zusätzlich sollen die Services besser skalieren 
und resileinter werden.    


1. Erweitern Sie die **Services** so, dass sie sich selbständig an der Service Registry
   anmelden und andere Services in der Cloud über die Service Registry finden 
2. Erweitern Sie den **Route Service** so, dass er den **Flight Service** aufruft um die Anzahl der Flüge für ein Route zu ermitteln
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


## Profil setzen 
Setzen Sie bitte die richtigen Profile für Ihr Build File. Im Fall von Maven sind das die Profile 
``eureka,mysql,loadbalancer,resilience4j`` die Sie über die IDE als auch beim bauen über die Konsole setzen können. 



## Configuration 
Bitte fügen Sie unter dem Profil **eureka** folgende Konfiguration für das Produkt **Netflix Eureka** in das ``application.yaml`` ein.

```
---
spring:
  config: 
    activate:
      on-profile: eureka
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
Bauen Sie die Anwendung wie folgt  

```
mvn clean package -Peureka -Pmysql 
```

oder über die IDE indem Sie das Maven Profil auf ``eureka,mysql`` setzen. 


## Deployment
Die Anwendung wird derzeit nicht über einen Container deoloyed, sondern direkt über die Konsole oder IDE gestartet, damit die Anwendung in den Übungen besser testbar ist. 

## Start
Sie können die Anwendung über die IDE oder wie folgt über die Konsole starten. Die Anwendung wird unter ``/target`` gestartet.

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

Bitte fügen Sie folgende Implementierung in das File ``Application.java`` ein. 

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
	
	public String flightCount(Long id) {
		return restTemplate.getForObject("http://flightService/flights/count/{id}", String.class, id);
	}
	
	public Supplier<String> flightCountSuppplier(Long id) {
		return () -> this.flightCount(id);
	}
		
	 public String flightCountResilence(Long id) {
		 return circuitBreaker.run(this.flightCountSuppplier(id), t -> { return "No Flights"; });
	 } 
}

```

## Build
Bauen Sie die Anwendung wie folgt. 

```
mvn clean package -Peureka,loadbalancer,resilience4j,mysql

```

Oder über die IDE indem Sie das Maven Profil auf eureka,mysql,loadbalancer,resilience4j setzen. 

## Deployment
Die Anwendung wird derzeit nicht über einen Container deoloyed sondern direkt über die Konsole gestartet, damit die Anwendung in den Übungen besser testbar ist. 


## Run
Sie können die Anwendung über die IDE oder wie folgt über die Konsole starten. Die Anwendung wird unter ``/target`` gestartet.

```
java -jar cna-pas-springio-synchron-route-service-start-0.0.1-SNAPSHOT.jar 
```


## Test 
Testen Sie die Anwendung wie folgt. Rufen Sie die UI des Route Service ueber den Browser ```localhost:8082`` auf. 
Es sollten nun alle Routen mit Angabe der Anzahl der Fluege **-1** erscheinen. Da wir den Flight Service noch nicht 
deployed haben greift der Circuite Breaker und der Fallback gibt "No Flights" zurueck. 


## Dependency
Wir kommen nun zum **Flight Service**. Bitte fügen Sie unter dem Profil **eureka** folgende Dependency für das Produkt **Eureka** in das ``pom.pom`` ein. 

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

Bitte setzen setzen Sie das Maven Profile über die IDE auf ``eureka,mysql``.
  
## Implementation 
Die Freigabe der **Discovery** und **Self Registration** Funktion erfolgt durch die Annotation **@EnableDiscoveryClient**
Bitte fügen Sie folgende Implementierung in das File ``Application.java`` ein. 

```java
@SpringBootApplication
@EnableDiscoveryClient
public class Application implements ApplicationRunner {
}


## Build
Bauen Sie die Anwendung wie folgt. 

```
mvn clean package -Peureka -Pmysql 
```

Oder über die IDE indem Sie das Maven Profil auf eureka,mysql setzen. 

## Deployment
Die Anwendung wird derzeit nicht über einen Container z.B. in einem Kubernetes deoloyed sondern direkt über die Konsole gestartet, damit die Anwendung in den Übungen besser testbar ist. 


## Run
Sie können die Anwendung über die IDE oder wie folgt über die Konsole starten. Die Anwendung wird unter ``/target`` gestartet.

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


# Contract Driven Tests 

Im folgenden Fall wird für die Ausführung anstatt MySQL eine H2 Datenbank vernwednet. Sie müssen daher über die IDE das Maven Profil von ``mysql``  auf ``h2`` ändern oder beim Bauen über die Konsole das h2 Profil verwenden. 

## Producer 
Der Producer ist in unserem Fall der Flight Service, da dieser dem Consumer Route Service eine API zur Verfügung stellt. 


### Contract   
Ergänzen Sie den Contract ``get-count-flights-by-id.groovy`` um die fehlenden Infromation in der URL ``101`` des Request und dem Body ``1`` des Resonse . 
 

### Dependency 
Aus dem Contract werden WireMock-JSON-Stubs und Tests erstellt. Dafür sind die folgenden Dependencies im POM-File notwendig. 

```xml

	  <dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-contract-verifier</artifactId>
			
	  </dependency>
	  
	  <dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
		</dependency>
	
```

Zusätzlich noch das Spring Cloud Contract Plugin für die Generierung. 

```xml
	
	<plugin>
		<groupId>org.springframework.cloud</groupId>
		<artifactId>spring-cloud-contract-maven-plugin</artifactId>
		<extensions>true</extensions>
		<version>3.1.4</version>
		<configuration>
			<baseClassForTests>com.thinkenterprise.BaseClass</baseClassForTests>
		</configuration>
	</plugin>
	
```

Sofern Sie nach dem Bauen gerne die Test in der IDE verwenden und ausführen möchten sollten Sie noch das folgende Plugin hinzufügen. Die Test werden in der IDE allerdings nach dem ersten bauen und ggf. Neustart oder Projekt Schliesen/Öffenen sichtbar. Zumindest in Eclipse. 


```xml

	<plugin>
		<groupId>org.codehaus.mojo</groupId>
		<artifactId>build-helper-maven-plugin</artifactId>
		<executions>
			<execution>
				<id>add-source</id>
				<phase>generate-test-sources</phase>
					<goals>
						<goal>add-test-source</goal>
					</goals>
					<configuration>
						<sources>
							<source>${project.build.directory}/generated-test-sources/contracts/</source>
						</sources>
					</configuration>
				</execution>
			</executions>
		</plugin>
			
```

### Implementierung 
Um den Contract Test gegen die eigene Implementierung durchzuführen ist eine Basisklasse zu implementieren die, die den FlightController bzw. die gesamte Flight Application bereitstellt.
Dazu ist folgende Basisklasse zu implementieren.  

```
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class BaseClass {
	
	@Autowired FlightController fligthController;
	
	@BeforeEach public void setup() {
		RestAssuredMockMvc.standaloneSetup(fligthController);

	}

}
```

# Test Settings
Im Testfall soll sich der Flight Service nicht an der Service Registry anmelden. Daher ist folgende Konfiguration der ``aplication.yaml'' hinzuzufügen. 

```
---
spring:
  config: 
    activate:
      on-profile: test
eureka:
  client:
    enabled: false
```
Und nicht vergessen, das Profil dann auch in der Konfiguration zu aktivieren - ganz oben in der Datei. 


### Build & Deployment 
Mit dem Build wird der Test ausgeführt. Sind die Tests erfolgreiche, werden die WireMock-JSON-Stubs erzeugt und das JAR im lokalen Maven Repositoriy installiert. 
 
```
mvn clean install -Peureka -Ph2 
```

### Test 
Mit dem oben ausgeführten Maven-Build werden auch die Tests ausgeführt. Nur wenn diese Erfolgreich sind wird das Artefakt im lokalen Repository installiert. 


## Consumer  
Bei dem Consumer handelt es sich um den **Route Service**.

### Dependency 

Für das Laden der vom Producer generierten Stubs is der folgende Starter notwendig. 

```
<dependency>
		<groupId>org.springframework.cloud</groupId>
		<artifactId>spring-cloud-starter-contract-stub-runner</artifactId>
</dependency>


<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-test</artifactId>
</dependency>
		
```

Auch hier muss das Maven-Profile auf eureka, h2, gesetzt werden. 



### Implementation 
Der Test muss nun den Stup laden und für den Test bereitstellen. Erstellen Sie den folgenden Test ``TestRouteService``.

```java
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureStubRunner(
		ids = "com.thinkenterprise:cna-pas-springio-synchron-flight-service-final:0.0.1-SNAPSHOT:stubs:8100",
		stubsMode = StubRunnerProperties.StubsMode.LOCAL
)
public class TestRouteService {
	
	@Test
	public void testflightCountResilence(@Autowired RouteService routeService) {	
		String count = routeService.flightCount(101L);
		Assertions.assertTrue(count.equals("1"));
	}
	
}	
```

### Configuration  
Im Testfall ist die echte Discovery Funktion abzuschalten, da Sie von Spring Cloud Contract als Stub zur Verfügung gestellt wird. 
Zudem müssen die in der Anwendung verwendeteten Service Namen ``flightService`` im ``RouteService`` auf die vom Stubrunner bereitgestellten Service cna-pas-springio-synchron-flight-service-start gemapped werden. 


```yaml
spring:
  config: 
    activate:
      on-profile: test  
stubrunner:
  ids-to-service-ids:
    cna-pas-springio-synchron-flight-service-start: flightService
eureka:
  client:
    enabled: false
```

### Test 
Führen Sie nun den Test ``TestRouteService`` aus.


# Spring Boot Chaos Monkey 

Für Spring Boot Chaos Monkey ist keine Übung vorgesehen, daher gibt es hier auch keine Ausführliche Beschreibung der Implementierung. Die Implementierung ist allerdings in den finalen Paketen zu finden. 

Die Implementierung führ SBCM für den Flight Service ein und veruhrsacht in diesem Latenzen von über 2 Sekunden. 

Die Implementierung für den Route Service konfiguriert den CurcuiteBraker so, dass dieser bereits nach 1 Sekunde greift und bei diesen Latenzen dann "No Flights" zurückgibt. Verschwindet der Fehler im Flight Service wieder, zeigt der Route Service die aktuellen Flüge pro Route an. 











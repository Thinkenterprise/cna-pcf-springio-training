# Precondition
Die der **PCF** Umgebung wird die Infratruktur über die **Pivotal Cloud Foundry** bereitgestellt. Sie benötigen daher einen Account auf einer PCF. 


# Infrastructure Setup 
Bitte führen Sie dazu die folgenden Schritte aus. 


## Login 


```
cf login -a api.run.pivotal.io -u <user> -p <password> 

```

## Erstellen Sie die Service Registry als Cloud Service 


```
cf marketplace
cf cs p-service-registry trial service-registry 

```


# Tasks
1. Erwetiern Sie den **Route Service** so, dass er sich selbständig an der Service Registry
   anmeldet und andere Services in der Cloud über die Service Registry findet 
2. Erweitern Sie den Route Service so, dass er den Flight Service aufruft um die Anzahl der Flüge für ein Route zu ermitteln


# Route Service an der Service Registry anmelden  

## Dependency
Bitte fügen Sie unter dem Profil **pcf** folgende Dependency für das Produkt **Service Registry** in das ``pom.pom`` ein. 

```
<profile>
			<id>pcf</id>
			<dependencyManagement>
				<dependencies>
					<dependency>
						<groupId>io.pivotal.spring.cloud</groupId>
						<artifactId>spring-cloud-services-dependencies</artifactId>
						<version>3.5.0</version>
						<type>pom</type>
						<scope>import</scope>
					</dependency>
				</dependencies>
			</dependencyManagement>
			<!-- Using Netflix Eureka a Discovery Client which is connected with the managed service from PCF  -->
			<dependencies>
				<dependency>
					<groupId>io.pivotal.spring.cloud</groupId>
					<artifactId>spring-cloud-services-starter-service-registry</artifactId>
				</dependency>
			</dependencies>
		</profile>
	</profiles>
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
Erstellen Sie das File ``File`` ,
Bitte fügen Sie folgende Implementierung in das File ``File`` ein. 



Die Freigabe der **Discovery** und **Self Registration** Funktion erfolgt durch die Annotation **@EnableDiscoveryClient**


```java

@SpringBootApplication
@EnableDiscoveryClient
public class Application implements ApplicationRunner {
}

```

## Build
Bauen Sie die Anwendung wie folgt. 

```
mvn clean package -Ppcf
```

## Deployment
Installieren Sie die Anwendung wie folgt. 

Erweitern Sie das PCF Manfiest ``manifest.yaml`` wie folgt. 


```
applications:
  - name: routeService 
    services:
    - sql-mysql-cleardb
    - service-registry
```

Installieren Sie die Anwendung über das CLI der PCF wie folgt. 

```
cf push 

```

## Test 
Nun ist zu ueberpruefen, ob der Route Service sich an der Registry angemeldet hat. Das koennen Sie ueber den **Application Manager** oder das **Spring Cloud Service Plugin** tun. 

Sofern Sie das Plugin verwenden moechten, ist dieses zuvor zu installieren. 

```
cf add-plugin-repo CF-Community https://plugins.cloudfoundry.org
cf install-plugin -r CF-Community "spring-cloud-services"
```

```
cf service-registry-info service-registry
cf service-registry-list service-registry

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

Bitte fügen Sie folgende Implementierung in das File ``LoadbalancerConfiguration`` ein. 

```java
@Bean
@LoadBalanced
public RestTemplate restTemplate() {
	return new RestTemplate();
}

```

# Route Service mit Circuit Breaker austatten 

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






## Configuration 
Bitte fügen Sie unter dem Profil **Profile** folgende Konfiguration für das Produkt **Product** in das ``application.yaml`` ein.

## Implementation 
Erstellen Sie das File ``File`` ,
Bitte fügen Sie folgende Implementierung in das File ``File`` ein. 


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
Bauen Sie die Anwendung wie folgt. 

```
mvn clean package

```

## Deployment
Installieren Sie die Anwendung wie folgt. 

```
cf push 

```

## Test 
Testeb Sie die Anwendung wie folgt. 

Rufen Sie die UI des Route Service ueber den Browser auf. Es sollten nun alle Routen mit Angabe der Anzahl der Fluege **-1** erscheinen. Da wir den Flight Service noch nicht deployed haben greift der Circuite Breaker und der Fallback gibt -1 zurueck. 



# Flight Service Deployen  

Der Flight Service ist soweit implementiert, sodass er sich iwe der Route Service an der Service Registry anmeldet. Sie koennen sich gerne die Implementierung des Flight Service nochmal in Ruhe anschauen und danach die weiteren Schritte ausführen.  



## Build
Bauen Sie die Anwendung wie folgt. 

```
mvn clean package -PProfile 
```

## Deployment
Installieren Sie die Anwendung wie folgt. 

```
cf push 

```

# Route Service prüfen  


## Test  

Rufen Sie die den Route Service ueber den Browser auf. Es sollte nun die Anzahl der Fluege fuer eine Route 1 und die anderen Routes auf 0 stehen. 


# Postcondition
Bitte führen Sie noch folgende Schritte durch um die Aufgabe abzuschliesen. 



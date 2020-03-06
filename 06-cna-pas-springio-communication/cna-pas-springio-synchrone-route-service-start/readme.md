## Task 



1. Erstellen Sie in Pivotal Web Services einen **Service Registry Cloud Service**
2. Erwetiern Sie den Route Service so, dass er sich selbständig an der Service Registry
   anmeldet und andere Services in der Cloud über die Service Registry findet 
2. Erweitern Sie den Route Service so, dass er den Flight Service aufruft um die Anzahl der Flüge für ein Route zu ermitteln



## Login 

```
cf login -a api.run.pivotal.io -u <user> -p <password> 

```


## Create Service Registry as Managed Cloud Service 


Create Service Registry Cloud Service in Pivotal Web Services 

```
cf cs p-service-registry trial service-registry 

```

Add Service Registry Cloud Service to the Manifest 


```
applications:
  - name: routeService 
    services:
    - autoscaler
    - sql-mysql-cleardb
    - service-registry


```

## Enhanced Maven  

First the Pivotal Spring Cloud BOM.  

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

Second the Dependency to support the connection to the **Pivotal Spring Cloud Service**.

```
<dependency>
	<groupId>io.pivotal.spring.cloud</groupId>
	<artifactId>spring-cloud-services-starter-service-registry</artifactId>
</dependency>

```

## Enable  

To Enable the **Discovery** and **Self Registration** Pattern enable the Autoconfiguration ``@EnableDiscoveryClient` of the Pivotal Spring Cloud Services. 


```
@SpringBootApplication
@RestController
@EnableDiscoveryClient
public class Application implements ApplicationRunner {
	

```

## Build 

```
mvn clean package

```


## Deployment 
```
cf push 

```

## Check 

Check the registration of the Route Service over the **Application Manager** Service Administration oder über das **Spring Cloud Service Plugin** .  


```
cf service-registry-info service-registry
cf service-registry-list service-registry

```





## Add Load Balancer Dependency 

Damit steht der Load Balancer von Spring Cloud Loadbalancer zur Verfügung, der Netflix Ribbon ersetzt. 

```
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-loadbalancer</artifactId>
</dependency>

```

## Disable Ribbon over Properties

```
spring.cloud.loadbalancer.ribbon.enabled=false

```


## Load Balanced Rest Template 

Für einen REST Aufruf auf dem Flight Service, benötigen wir ein Rest Template 

Um ein Load Balanced RestTemplate zu erzeugen ist die Annotation ``@LoadBalanced`` notwendig. 


```
@Bean
@LoadBalanced
public RestTemplate restTemplate() {
	return new RestTemplate();
}

```

## Add Resilience Dependency 

Mit der Resilience4J Dependency steht der Circuite Breaker aus dem Spring Cloud Resilience4J zur Verfügung, der Netflix Hystrix ersetzt. 

```
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-circuitbreaker-resilience4j</artifactId>
</dependency>

```

## Disable Hystrix over Properties 


```
spring.cloud.circuitbreaker.resilience4j.enabled=true
spring.cloud.circuitbreaker.hystrix.enabled=false

```

## Circuit Breaker 

Umschliesen Sie den Aufruf auf dem Fligt Service mit einem einfachen Circuite Breaker. 


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

```
mvn clean package

```


## Deployment 
```
cf push 

```

## Check 

Rufen Sie die UI des Route Service über den Browser auf. Es sollten alle nun alle Routen mit Angabe der Anzahl der Flüge erscheinen. 


## Check Ciruite Breaker 
Stoppen Sie den Fligt Service und rufen Sie die UI des  Route Serive erneut über den Browser auf. Es solle nun überall eine -1 erscheinen. 
Starten Sie den Flight Service wieder. Nach einiger Zeit sollen die korekten Werte wieder angezeigt werden. 

















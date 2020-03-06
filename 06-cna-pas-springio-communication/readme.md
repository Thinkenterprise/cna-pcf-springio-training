## Task 



1. Erstellen Sie in **Pivotal Web Services einen** einen **Service Registry Cloud Service**
2. Erwetiern Sie den Route Service so, dass er sich selbstÃ¤ndig an der Service Registry
   anmeldet und andere Services in der Cloud Ã¼ber die Service Registry findet 
2. Erweitern Sie den Route Service so, dass er den Flight Service aufruft um die Anzahl der FlÃ¼ge fÃ¼r ein Route zu ermitteln


## Vorbereitung  
** Project: cna-pas-springio-synchrone-route-service-start**

Löschen Sie alle Apps in der Cloud und Tabellen in der Datenbank.


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

Der Flight Service ist soweit implementiert. Sie können sich gerne die Implementierung des Flight Service nochmal in ruhe anschauen. 



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



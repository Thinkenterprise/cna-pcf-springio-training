## Task 

Die Fluggesellschaft  möchte einen Gateway vor die Anwendung schalten. 

1. Erstellen Sie einen Gateway, der die URL <aero-domain/*> auf den Route Service umleitet 
2. Fügen Sie dem Aufruf einen **Load Balancer** hinzu 
3. Fügen Sie dem Aufruf einen **Circuite Breaker** hinzu 


## Vorbereitung 
**Project: cna-pas-springio-gateway-start**

Löschen Sie alle Apps in der Cloud. 


## Gateway Dependency Prüfen   
**Project: cna-pas-springio-gateway-start**

Prüfen sie ob der Starter für die Implementierung des Gateway vorhanden ist. 

```
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-gateway</artifactId>
</dependency>

```

## Implement a Redirect
**Project: cna-pas-springio-gateway-start**

Eine Umleitung kann über die Konfiguration einer **RouteLocator** Instance implementiert werden.
 

```
@Bean
public RouteLocator myRoutes(RouteLocatorBuilder builder) {
	return builder.routes()
		  	 .route(p -> p.path("/**")
			 .uri("//routeService")
			 .id("routeService"))
			 .build();
	}

```

## Implementieren Sie den Load Balancer  
**Project: cna-pas-springio-gateway-start**

Dazu müssen Sie einfach nur die Deklaration **lb:** der URL hinzufügen. 

```
.uri("lb://routeService").id("routeService"))
	
```


## Implementieren Sie Circuite Breaker 
**Project: cna-pas-springio-gateway-start**

Dazu müssen Sie der Route einen **CircuiteBraker** Instanz hinzufügen und für diese einen Fallback definieren. 


```
.circuitBreaker(c -> c.setName("resilience4j").setFallbackUri("forward:/fallback")))
	
```

Im Fall dass die Umleitung nich erfolgen kann, wird auf die URL /fallback umgeleitet. 
Diese URL ist noch zu implementieren. 

```
@RequestMapping("fallback")
	public String fallback() {
		return "The application is temporarily unavailable";

	}

```
## Build 
**Project: cna-pas-springio-gateway-start**

```
mvn clean package

```

## Login 
**Project: cna-pas-springio-gateway-start**

```
cf login -a api.run.pivotal.io -u <user> -p <password> 

```

## Deployment 
**Project: cna-pas-springio-gateway-start**

```
cf push 

```

## Check 

Nach dem Aufruf der URL <domain>/ aus dem Browser sollte zunächst die Meldung 
**The application is temporarily unavailable** erscheinen. 
Starten Sie nun aus der vorangegangenen Übung **cna-pas-springio-synchron-route-service-start** den Route Service und 
aus **cna-pas-springio-synchron-flight-service-start** den Flight Service. Es sollte dann die Website der Fluggesellschaft erscheinen. 

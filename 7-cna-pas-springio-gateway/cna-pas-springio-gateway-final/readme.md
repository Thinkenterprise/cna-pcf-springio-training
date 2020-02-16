## Task 

Die Fluggesellschaft Aero möchte einen Gateway vor die Aero Anwendung schalten. 

1. Erstellen Sie einen Gateway, der die URL <aero-domain/aero> auf den Route Service / umleitet 
2. Fügen Sie dem Aufruf einen **Load Balancer** hinzu 
3. Fügen Sie dem Aufruf einen **Circuite Breaker** hinzu 



## Add Gateway Dependency  

```
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-gateway</artifactId>
</dependency>

```

## Implement a Redirect 

Ein Redirect kann über die Konfiguration einer **RouteLocator** Instance implementiert werden.
 

```
@Bean
public RouteLocator myRoutes(RouteLocatorBuilder builder) {
	return builder.routes()
		  	 .route(p -> p.path("/aero/**")
			 .filters(f -> f.rewritePath("/aero/(?<segment>.*)", "/${segment}"))
			 .uri("//routeService").id("routeService"))
			 .build();
	}

```

## Implement a Load Balancer  

Dazu müssen Sie einfach nur die Deklaration **lb:** der URI hinzufügen. 

```
.uri("lb://routeService").id("routeService"))
	
```


## Implement a Circuite Breaker 

Dazu müssen Sie der Route einen **CircuiteBraker** Instanz hinzufügen und für diese einen Fallback definieren. 


```
.circuitBreaker(c -> c.setName("resilience4j").setFallbackUri("forward:/fallback")))
	
```


## Build 

```
mvn clean package

```

## Login 

```
cf login -a api.run.pivotal.io -u <user> -p <password> 

```

## Deployment 
```
cf push 

```

## Check 

Nach dem Aufruf der URL <aero-domain/aero> sollte die Route Aero Website erscheinen, 
die vom Route Service bereitgestellt wird. 





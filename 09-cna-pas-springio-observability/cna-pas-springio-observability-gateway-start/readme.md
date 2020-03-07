## Task 

Die Fluggesellschaft **Aero** möchte die Anwendung so erweitern, dass es bei Problemen möglich ist die Anwendung besser 
zu untersuchen und mögliche Fehler erkennen zu können.   

1. Erweitern Sie den Gateway so, dass der **State** über HTTP gelesen werden kann 
2. Loggen Sie jeden eingehenden Request 
3. Fügen Sie dem Gateway eine Counter **Metric** hinzu. Der Counter soll 
   die Ausfälle des Route Service zählen. 
4. Der Aufruf der Aeror Webseite soll über ein **Tracing** von Gateway über den Route Service verfolgt werden. 

## Login 

```
cf login -a api.run.pivotal.io -u <user> -p <password> 

```

## Add Spring Boot Actuator Dependency  

```
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-actuator</artifactId>
</dependency>

```


## Build & Deploy 

```
mvn clean package
cf push 

```



## Investigate Actuator Endpoints    

Die  Domain ``<aero-domain/actuator` zeigt alle verfügbaren Endpoints. Untersuchen sie ein paar für Sie interessante Endpoints

```
curl <aero-domain>/actuator
curl <aero-domain>/actuator/helath
curl <aero-domain>/actuator/info
etc.

```

## Add Spring Boot Logging   
Spring Boot veerwendet **SLF4J** als Logging-Fasade und **Lockback** als Standardimplementierung. 
Erstellen Sie im Gateway einen Global Filter und Loggen Sie jeden eingehenden Zugriff 

```java
@Component
public class GlobalLoggingFilter implements GlobalFilter{
	
    protected final static Logger logger = LoggerFactory.getLogger(GlobalLoggingFilter.class);

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
	   logger.info("Global Logging Filter ... Log Correlation ID :" + exchange.getLogPrefix());
        return chain.filter(exchange);
	}
}

```

## Build & Deploy 

```
mvn clean package
cf push 

```

## Check Logging 
Gehen Sie in den Application Manager und öffnen Sie PCF Metrics. Öffenen Sie die Logging Einträge und Filtern Sie nach Apps. 
Dort müssten dann die Logging Einträge zu finden sein. 

Der Log kann auch über die Konsole gelesen werden 

```
cf logs aeroGateway --recent 

```

## Add Spring Boot Metrics 

Erstellen Sie über die ```MeterRegistry`` einen ``Counter`` und erhöhen diesen immer dann, wenn die **Fallback-Methode** aufgerufen wird. 


```
@SpringBootApplication
@EnableDiscoveryClient
@RestController
public class Application implements ApplicationRunner {

	
	@Autowired
	private MeterRegistry meterRegistry;
	
	private Counter counter;


	public void run(ApplicationArguments args) throws Exception {
		logger.info("Start: AeroGateway with version " + version);
		
		counter = Counter
				  .builder("Fallback Counter")
				  .description("counts number of sfallbacks")
				  .tags("service", "states")
				  .register(this.meterRegistry);

	}
	
	...

	@RequestMapping("fallback")
	public String fallback() {
		counter.count();
		return "The application is temporarily unavailable";

	}

}

```

## Build & Deploy 

```
mvn clean package
cf push 

```

## Check Metric 

Prüfen Sie die Metric über den Actuator Endpoint Metrics   


```
<aero-domain/actuators/metrics>

```


## Add Spring Cloud Sleuth Dependency  

```
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-sleuth</artifactId>
</dependency>

```


## Build & Deploy 

```
mvn clean package
cf push 

```

## Check Tracing 


```
INFO [aeroGateway,d3de9b77c21e2ddc,d3de9b77c21e2ddc,false] 

```

Prüfen Sie über das Logging, ob die Trace Informationen (Trace ID, Span ID) enthalten sind. 





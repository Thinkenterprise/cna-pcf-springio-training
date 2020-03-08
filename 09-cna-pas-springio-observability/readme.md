## Aufgabe 

Die Fluggesellschaft moechte die Anwendung so erweitern, dass es bei Problemen moeglich ist, die Anwendung  
zu untersuchen und moegliche Fehler zu erkennen.   

1. Erweitern Sie den Gateway so, dass der **State** über HTTP gelesen werden kann 
2. **Loggen** Sie jeden eingehenden Request 
3. Fuegen Sie dem Gateway eine Counter **Metric** hinzu. Der Counter soll die Ausaelle des Route Service zaehlen. 
4. Der Aufruf der Aeror Webseite soll Ã¼ber ein **Tracing** von Gateway Ã¼ber den Route Service verfolgt werden. 

## Vorbereitung  
** Project: cna-pas-springio-observability-gateway-start**

Löschen Sie alle Apps in der Cloud. 

## Login 

```
cf login -a api.run.pivotal.io -u <user> -p <password> 

```

## Spring Boot Actuator Dependency Prüfen 
** Project: cna-pas-springio-observability-gateway-start**

Prüfen Sie ob die Dependency für den Actuator vorhanden ist. 

```
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-actuator</artifactId>
</dependency>

```


## Build & Deploy 
** Project: cna-pas-springio-observability-gateway-start**

```
mvn clean package
cf push 

```



## Actuator Endpoints untersuchen 
** Project: cna-pas-springio-observability-gateway-start**
   
Die  Domain **<domain>/actuator** zeigt alle verfuegbaren Endpoints an. Untersuchen sie ein paar fuer Sie interessante Endpoints z.B. 
mit **Postman**.

```
curl <aero-domain>/actuator
curl <aero-domain>/actuator/health
curl <aero-domain>/actuator/info
etc.

```

## Spring Boot Logging hinzufügen
** Project: cna-pas-springio-observability-gateway-start**
 
Spring Boot verwendet **SLF4J** als Logging-Fasade und **Lockback** als Standardimplementierung. 
Erstellen Sie im Gateway einen Global Filter und Loggen Sie jeden eingehenden Zugriff. 

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
** Project: cna-pas-springio-observability-gateway-start**

```
mvn clean package
cf push 

```

## Check Logging
** Project: cna-pas-springio-observability-gateway-start**
 
Gehen Sie in den **Application Manager** und öffnen Sie **PCF Metrics**. Oeffnen Sie die Logging-Eintraege und Filtern Sie nach Apps. 
Dort muessten dann die Logging-Eintraege zu finden sein. 

Der Log kann auch ueber die Konsole gelesen werden. 

```
cf logs aeroGateway --recent 

```

## Spring Boot Metriken hinzufügen 
** Project: cna-pas-springio-observability-gateway-start**
 
Erstellen Sie über die **MeterRegistry** einen **Counter** und erhöhen Sie diesen immer dann, wenn die **Fallback-Methode** aufgerufen wird. 


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
** Project: cna-pas-springio-observability-gateway-start**


```
mvn clean package
cf push 

```

## Check Metric 
** Project: cna-pas-springio-observability-gateway-start**

Prüfen Sie die Metric mit **Postman** ueber den Actuator Endpoint **Metrics**. 


```
<aero-domain/actuators/metrics>

```

Ist ist auch möglich die Metrik über einen **Metrics-Forworder Cloud Service** an PCF-Metrics weiterzuleiten. 
Leider ist der Metrics-Forworder unter Pivotal Application Service nicht verfügbar. 


## Spring Cloud Sleuth Dependency 
** Project: cna-pas-springio-observability-gateway-start**  

Im letzen Schritt möchte wir der Anwendung ein **Tracing** hinzufügen. Dazu werden wir das Spring Cloud Framework **Sleuth** verwenden. 

```
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-sleuth</artifactId>
</dependency>

```


## Build & Deploy 
** Project: cna-pas-springio-observability-gateway-start**  

```
mvn clean package
cf push 

```

## Check Tracing 
** Project: cna-pas-springio-observability-gateway-start**  

```
INFO [aeroGateway,d3de9b77c21e2ddc,d3de9b77c21e2ddc,false] 

```

Prüfen Sie ueber das Logging, ob die Trace Informationen (Trace ID, Span ID) enthalten sind. 


## App to App Tracing  
** Project: cna-pas-springio-observability-route-service-start**

Sie wollen nun über mehrere App aufrufe hinweg die Trace-ID und Span-ID verfolgen. 



# Build & Deploy 
** Project: cna-pas-springio-observability-route-service-start**  

Der Route Service wurde bereits um Sleuth erweitert. Bauen und installieren Sie die Anwendung und führen Sie anschließend einen Erneuten Seitenaufruf durch. Sie können dann über den Application Manager und PCF Metrics den Trace prüfen, oder über das Kommando logs. Am besten 
ist es beide Anwendungen Gateway und Route Service zu loggen. 


```
mvn clean package
cf push 

cf logs aeroGateway
cf logs routeService

curl <gateway-domain>/routes




```
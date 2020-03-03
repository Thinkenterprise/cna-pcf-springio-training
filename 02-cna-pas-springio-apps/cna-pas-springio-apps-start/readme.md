## Aufgabe  

Die Fluggesellschaft hat einen Route Service in Spring Boot implementiert. Der Service soll als App über die **Pivotal Application Services** (PAS) unternehmensweit bereitgestellt werden. 

1. Deployen Sie die Spring Boot Anwendung mit dem Namen RouteService als App in die PAS. Verwenden Sie dabei die Standardeinstellungen.  


2. Führen Sie nun ein Manifest ein, um die App für die PAS zu konfigurieren. Bestimmen Sie in dem Manifest das zu deployende Artefakt, den Namen der App und die Route als Random Route. Pushen Sie dfie Anwendung und überprüfen Sie ihre Einstellungen 


3. Die App soll die aktuelle Java-Buildpack-Implementierung aus GitHub verwenden. Ändern Sie entsprechend das Manifest. Pushen Sie dfie Anwendung und überprüfen Sie ihre Einstellungen 

4. Definieren Sie über das Manifest eine Environment Variable ROUTE_SERVICE_VERSION, vergeben eine Route und geben diese bei Start der App über das Logging aus. 


5. Führen Sie beim Health Indicator eine Variable ein, die über das Application Environment VCAP_APPLICATION das Memory ermittelt. Setzen Sie den Health Status auf DOWN sobald der Speicher unter 1024 liegt. 

## Login 

```
cf login -a api.run.pivotal.io -u <user> -p <password> 

```


## Cloud Foundry Connector  
Damit der Zugriff der App auf das Environment der PAS zugreifen kann, ist der **Cloud Foundry Cloud Connector** hinzuzufügen. 

```
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-cloudfoundry-connector</artifactId>
</dependency>

```


## Build 

```
mvn clean package

```


## Deployment 
```
-p cf push helloWorldService -p cna-pas-springio-apps-final-0.0.1-SNAPSHOT.jar

```

## Manifest 

Erstellen Sie zun ein Manifest um die App zu konfigurieren. Über **random-route** wir eine zufällig Route erstellt. 

```
applications:
  - name: RouteService 
    random-route: true
    path: target/cna-pas-springio-apps-final-0.0.1-SNAPSHOT.jar
```



## Buildpack 

Add a Buildpack to the manifest. The sample show three different way to define the manifest by **name** by **repository-URL** and by **repository URL with version**


Fügen Sie nun dem Manifest ein dezidiertes Buildpack hinzu. Das Sample zeigt mehrere Möglichkeiten 


* Über den Namen 
* Über die URL 
* Über die URL mit Version 

wie Sie das tun können. Entcheiden Sie sich für eine. 


```
applications:
  - name: RouteService 
  ...
    buildpacks: 
      - java-buildpack
      - https://github.com/cloudfoundry/java-buildpack.git
      - https://github.com/cloudfoundry/java-buildpack.git#v4.27
```



Der folgende Befehl zeigt, wie Sie eigene Buildpacks in die PAS einstellen können. 


```
cf create-buildpack BUILDPACK PATH POSITION [--enable|--disable]
```

Allerdings können unter **Pivotal Web Services** keine eigenen Buildpacks eingestelt werden. 



## Environment 

Fügen Sie nun über das Manifest das Environment **ROUTE_SERVICE_VERSION** ein und geben Sie diesem einen Wert 1.


```
applications:
  - name: RouteService 
  ...
    env:
      ROUTE_SERVICE_VERSION: 1
```

Lesen Sie das Environment über die Anwendung aus.  
 

``` java
@SpringBootApplication
public class Application implements ApplicationRunner {
	
	private Log logger = LogFactory.getLog(Application.class); 
	
	@Value("${route.service.version}")
	private String routeServiceVersion;
	
	
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

	public void run(ApplicationArguments args) throws Exception {
		logger.info(routeServiceVersion);
		
	}

   
}

```

## Health Check  

Fügen Sie über das Manifest über die Properties **health-check-type** und **health-check-http-endpoint** einen Health-Check ein. 


```
applications:
  - name: RouteService 
  ...
    health-check-type: http
    health-check-http-endpoint: /actuator/health
```


Implementieren Sie einen eigenen Health-Indicator, der den Gesundheitszustand der App vom definiereten Speicher abhängig macht.  
 
``` java
@Component
public class RouteServiceMemoryHealthIndicator implements HealthIndicator {
	
	private Log logger = LogFactory.getLog(RouteServiceMemoryHealthIndicator.class); 
		
	@Value("${vcap.application.limits.mem}")
	private Long limitsMem;

	public Health health() {
		
		logger.info(limitsMem);

		if (limitsMem >= 1024)
			return Health.up().build();
		else
			return Health.down().build();

	}

}
 
```
 
## Scaling 
Für Sie für die Anwendung eine statische Skalierung ein indem Sie die Attribute **instance**, **memory** and **disk_quota** setzen. 


```
applications:
  - name: RouteService 
  ...
    memory: 2G 
    disk_quota: 512M
    instances: 2
```

Ändern Sie das Memory so, dass der Health-Indicator den Zustand Down anzeigt. 

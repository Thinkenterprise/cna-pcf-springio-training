## Task 

Die Fluggesellschaft hat einen Route Service in Spring Boot implementiert. Der Service soll als App über die Pivotal Application Services (PAS) unternehmensweit bereitgestellt werden. 

1. Deployen Sie die Spring Boot Anwendung mit dem Namen RouteService als App in der PAS. Verwenden Sie dabei die Standardeinstellungen.  


2. Führen Sie nun ein Manifest ein, um die App für die PAS zu konfigurieren. Bestimmen Sie in dem Manifest das zu deployende Artefakt, den Namen der App und die Route als Random Route. Pushen Sie dfie Anwendung und überprüfen Sie ihre Einstellungen 


3. Die App soll die aktuelle Java-Buildpack-Implementierung aus GitHub verwenden. Ändern Sie entsprechend das Manifest. Pushen Sie dfie Anwendung und überprüfen Sie ihre Einstellungen 

4. Definieren Sie über das Manifest eine Environment Variable ROUTE_SERVICE_VERSION, vergeben eine Route und geben diese bei Start der App über das Logging aus. 


5. Führen Sie beim Health Indicator eine Variable ein, die über das Application Environment VCAP_APPLICATION das Memory ermittelt. Setzen Sie den Health Status auf DOWN
sobald der Speicher unter 2048 liegt. 





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
-p cf push helloWorldService -p cna-pcf-springio-apps-final-0.0.1-SNAPSHOT.jar

```

## Manifest 

Create Manifest File manifest.yml 

```
applications:
  - name: RouteService 
    random-route: true
    path: target/cna-pas-springio-apps-final-0.0.1-SNAPSHOT.jar
```

The route oft application will be created by the name of the application.
In case of more than one developer work on the same organisation it gives conflicts.  Then the manifest variable ```random-route ``` should solve  the problem. 


## Buildpack 

Add a Buildpack to the manifest. The sample show three different way to define the manifest by **name** by **repository-URL** and by **repository URL with version**

```
applications:
  - name: RouteService 
  ...
    buildpacks: 
      - java-buildpack
      - https://github.com/cloudfoundry/java-buildpack.git
      - https://github.com/cloudfoundry/java-buildpack.git#v4.27
```

A list of the existing buildpacks 

How to build your own buildpacks is described in the documentation. 

A creation of a custom buildpack in Pivotal Web Services is not possible.

```
cf create-buildpack BUILDPACK PATH POSITION [--enable|--disable]
```




## Environment 

Add a a environment variables ROUTE_SERVICE_VERSION to the application and set it to 1 


```
applications:
  - name: RouteService 
  ...
    env:
      ROUTE_SERVICE_VERSION: 1
```


Use the environment variable in the Application implementation 

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

Add the manifest Variable ``health-check-type``and ``health-check-http-endpoint`` and define an Health Check HTTP as Type and the Endpoint of the Actuator.  

```
applications:
  - name: RouteService 
  ...
    health-check-type: http
    health-check-http-endpoint: /actuator/health
```


 Use the environment variable of the Source Service als health state. 
 
``` java
@Component
public class RouteServiceMemoryHealthIndicator implements HealthIndicator {
	
	private Log logger = LogFactory.getLog(RouteServiceMemoryHealthIndicator.class); 
		
	@Value("${vcap.application.limits.mem}")
	private Long limitsMem;

	public Health health() {
		
		logger.info(limitsMem);

		if (limitsMem >= 2048)
			return Health.up().build();
		else
			return Health.down().build();

	}

}
 
```
 
## Scaling 

The manifest variable ``instance``, ``memory`` and ``disk_quota`` are n to scale the application honizontal. 

```
applications:
  - name: RouteService 
  ...
    memory: 2G 
    disk_quota: 512M
    instances: 2
```

Change the value memory to 2 GByte

## Aufgabe  

Die Fluggesellschaft moechte die Anwendung so erweitern, dass diese mit einem JWT-Token über das OAuth2 Protokoll geschützt wird. 


1. Richten Sie über Github, Google oder Facebook einen OAuth2 Authorization Server ein 
2. Implementieren Sie einen OAuth2/JWT Client Gateway 
3. Implementieren Sie einen OAuth2/JWT Resource Service Route Service 
  

## Client ID/Secret in OAuth2 Authorization Server einrichten  
** Project: cna-pas-springio-security-gateway-client-start**


Im folgenden wird kurz erklärt wie das für Github funktioniert. 
In anderen Umgebungen wie Google oder Facebook sind die schritte ähnlich. 

1. Login Github 
2. Settings aufrufen 
3. Developer Settings aufrufen  
4. OAuth Apps
5. New OAuth App

Als Homepage URL geben Sie **http://gfu.net**
Wichtig ist die Vergabe der **Authorization callback URL**. Definieren Sie die URL wie folgt **http://aerosecuritygateway<org>.cfapps.io**
ein. 

Sie erhalten dann **Client ID** und **Client Secret**.  


## Login 

```
cf login -a api.run.pivotal.io -u <user> -p <password> 

```

## Spring Boot OAuth2 Client Dependency Pruefen  
** Project: cna-pas-springio-security-gateway-client-start**

Die folgende Dependency wird benoetigt, um einen OAuth2 Client zu realisieren 

```
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-oauth2-client</artifactId>
</dependency>

```

Die folgende Dependency wird benoetigt, um den OAuth2 Client JWT-Token Downstream an andere Apps weiterzureichen. 

``` java
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-security</artifactId>
</dependency>

```


## Spring Boot OAuth2 Client Property Konfiguration  
** Project: cna-pas-springio-security-gateway-client-start**
 
Hier tragen Sie nun die Client ID und Client Secret ein, damit der JWT-Token von der Anwendung über Github bezogen werden kann. 
Die dahinterliegenden Endpoint URLs für z.B. Github, sind Spring Boot bekannt. 

``` java
spring.security.oauth2.client.registration.github.client-id=edc49cc39421092777cc
spring.security.oauth2.client.registration.github.client-secret=78a81958aeed2914821d883973c8ea0d84b00515
```

## Spring Boot OAuth2 Client Security Konfiguration  
** Project: cna-pas-springio-security-gateway-client-start**

Über die Annotation **@EnableWebFluxSecurity** werden die Security Funktionen aktiviert. 
Über eine Security Konfiguration werden die URLs auf den Health-Endpoint freigeschaltet, damit der Health-Check 
keinen Security Restriktionen unterliegt. 


``` java

@Configuration
@EnableWebFluxSecurity
public class SecurityConfiguration {
	
	@Bean
	SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) throws Exception {
		http
			.csrf().disable()
			.authorizeExchange()
				.pathMatchers("/actuator/**").permitAll()
				.anyExchange().authenticated()
				.and()
			.oauth2Login();
		return http.build();
	}

}
```

## Toke Relay  
** Project: cna-pas-springio-security-gateway-client-start**
Damit der JWT-Token vom Gateway weitergereicht wird ist folgende Implementierung notwendig. 

``` java
@SpringBootApplication
@EnableDiscoveryClient
@RestController
public class Application implements ApplicationRunner {

	@Autowired
	private TokenRelayGatewayFilterFactory filterFactory;
		
	@Bean
	public RouteLocator myRoutes(RouteLocatorBuilder builder) {
		return builder.routes()
				.route(p -> p.path("/**")
						.filters(f -> f.filter(filterFactory.apply())
								.circuitBreaker(c -> c.setName("resilience4j").setFallbackUri("forward:/fallback")))
						.uri("lb://routeService").id("routeService"))
				.build();
	}


}
```

## Manifest   
** Project: cna-pas-springio-security-gateway-client-start**
Wir haben unter dem OAuth2 Authorization Server eine Redirect URL angegben, die auf unsere Anwendung zeigt. 
Damit diese mit unserer Anwendung überinastimmt dürfen Sie die Route nicht zufällig bestimmen sondern müssen diese festlegen. 
Verwenden Sie dazu den **host** Hosteintrag. Achten Sie auf den Appendix **Org1**. Tragen Sie dort den Namen Ihrer PAS Organisation ein, 
damit der Name eindueitg ist.   


```
---
applications:
  - name: aeroSecurityGateway
    host: aeroSecurityGatewayOrg1 
   
```


## Build & Deploy 
** Project: cna-pas-springio-security-gateway-client-start**

```
mvn clean package
cf push 

```

## Check 
** Project: cna-pas-springio-security-gateway-client-start**

Bevor Sie die Anwendung prüfen können, müssen Sie den Route Service aus dem Projekt **cna-pas-springio-synchron-route-service-start** starten. 
Sie können nun die Anwendung prüfen indem Sie über den Browser die WebPage der Fluggesellschaft aufrufen. 
Sie sollten auf die Github Anmeldeseite umgelengt werden. Nachdem Sie die Authentifizierung vorgenommen haben, sollte die WebPage angezeigt werden. 

## Check gesichertem Route Service
**Project: cna-pas-springio-security-route-service-start**

In der vorausgegangenen Prüfung haben Sie einen Route Service aufgerufen, der den weitergeliteten JWT-Token nicht weiter prüft. 
In diesem Projekt befindet sich eine vollständige Implementierung eines OAuth2/JWT Resource Server für den Route Service. 
Damit ist auch der Route Service über OAuth2/JWT gesichert. Sie können sich die Implementierung anschauen und anschliesen den Route 
Service bauen und deployen. 

## Build & Deploy 
** Project: cna-pas-springio-security-route-service-start**

```
mvn clean package
cf push 

```






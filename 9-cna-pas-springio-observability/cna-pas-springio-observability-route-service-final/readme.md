#Task

Die Fluggesellschaft **Aero** möchte die Anwendung so erweitern, dass es bei Problemen möglich ist die Anwendung besser 
zu untersuchen und mögliche Fehler erkennen zu können.   

1. Der Aufruf der Aeror Webseite soll über ein **Tracing** von Gateway über den Route Service verfolgt werden. 



## Login 

```
cf login -a api.run.pivotal.io -u <user> -p <password> 

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







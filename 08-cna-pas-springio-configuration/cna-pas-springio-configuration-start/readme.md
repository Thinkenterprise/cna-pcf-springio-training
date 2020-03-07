## Aufgabe 

Die Fluggesellschaft möchte den Gateway über eine zentrale Konfiguration Ein - und Auschalten können. 

 
1. Estellen Sie ein Configuration Repository (Git Repsoitory)
2. Legen Sie im Configuration Repository die Konfiguration ab
   über die der Gateway Ein - und Ausgeschaltet werden kann. 
3. Erstellen Sie einen Configuration Server


## Login 
**Project: cna-pas-springio-configuration-start**

```
cf login -a api.run.pivotal.io -u <user> -p <password> 

```

## Git Repository 
**Project: cna-pas-springio-configuration-start**

Sofern möglich nutzen Sie ein bestehendes Git Repository oder erstellen Sie ein neues. 
Sollte beides nicht möglich sein, dann können Sie das Git Repository der Schulung verwenden. 
In diesem Fall müssen Sie die Änderungen mit den Schulungsteilnehmern abstimmen. 

```
https://github.com/Thinkenterprise/cna-pas-springio-configuration-server-repository.git
```

Sofern Sie das Schulungsrepository nicht werden, legen Sie im Repository eine **application.properties** 
ab und erstellen Sie das Property **aero.gateway.enabled=true**. 



## Configuration Server erstellen  
**Project: cna-pas-springio-configuration-start**

```
cf cs p-config-server trial configuration-service -c '{"git": { "uri": "https://github.com/Thinkenterprise/cna-pas-springio-configuration-server-repository.git", "update-git-repos": "true"} }'

```


## Gateway Add Service to Manifest 
**Project: cna-pas-springio-configuration-start**

```
applications:
  - name: aeroGateway
    services:
    - configuration-service
```


## Gateway Configuration Server Dependency Prüfen 
**Project: cna-pas-springio-configuration-start**

Um den Pivotal Config Server als Configuration Server zu verwenden, ist die folgende Dependency hinzuzufügen. 


```
<dependency>
	<groupId>io.pivotal.spring.cloud</groupId>
	<artifactId>spring-cloud-services-starter-config-client</artifactId>
</dependency>

```

## Gateway Add Property  
**Project: cna-pas-springio-configuration-start**

Die Annotation **@RefreshScope** sorgt dafür, dass das Property **aero.gateway.enabled** nach einem **RefreshEvent** neu ausgelesen wird. 

```
@RefreshScope
@RestController
public class RefreshController {

	private Log logger = LogFactory.getLog(RefreshController.class);

	@Value("${aero.gateway.enabled}")
	private Boolean enabled;
```


## Build 
**Project: cna-pas-springio-configuration-start**

```
mvn clean package

```



## Deployment 
**Project: cna-pas-springio-configuration-start**

```
cf push 

```

## Check  
**Project: cna-pas-springio-configuration-start**

Prüfen Sie den Zustand des Properties. Der folgende Aufruf liefert den aktuellen Zustand zurück. 

```
curl <aero-domain>/enabled 

```

## Refresh  
**Project: cna-pas-springio-configuration-start**
 
Führen Sie nun Änderungen an der zentralen Konfiguration durch. 
Damit der Client die Änderungen liest, können Sie einen manuellen Refresh über den 
Actuator Endpoint durchführen. 


```
curl -X POST <aero-domain>/actuator/refresh
curl <aero-domain>/enabled 
```





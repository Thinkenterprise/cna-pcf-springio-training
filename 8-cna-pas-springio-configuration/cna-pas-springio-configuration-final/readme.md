## Task 

Die Fluggesellschaft Aero möchte den Gateway über eine zentrale Konfiguration Ein - und Auschalten können. 

1. Erstellen Sie einen Configuration Server 
2. Erweitern Sie den Gateway um ein Property über das Sie die Konfiguration Ein - und Auschalten können


## Login 

```
cf login -a api.run.pivotal.io -u <user> -p <password> 

```

## Precondition CLI Plugins

Sie benötigen das Spring Cloud Service Plugin. Sofern es nicht installiert ist, können Sie das wie folgt tun. 

```
cf add-plugin-repo CF-Community https://plugins.cloudfoundry.org
cf install-plugin -r CF-Community "spring-cloud-services"

```

## Create Configuration Server Cloud Service 


```
cf cs p-config-server trial configuration-service -c '{"git": { "uri": "https://github.com/Thinkenterprise/cna-pas-springio-configuration-server-repository.git", "update-git-repos": "true"} }'

```


## Gateway Add Service to Manifest 

```
applications:
  - name: aeroGateway
    services:
    - configuration-service
```


## Gateway Add Configuration Server Dependency  


Um den **PAS Cloud Service** als Configuration Server zu verwenden, ist die folgende Dependency hinzuzufügen. 


```
<dependency>
	<groupId>io.pivotal.spring.cloud</groupId>
	<artifactId>spring-cloud-services-starter-config-client</artifactId>
</dependency>

```

Werden Configuration Client und Server ohne die PAS Cloud Service betrieben, sind die Dependencies ``spring-cloud-config-client`` und ``spring-cloud-config-server``.


## Gateway Add Property  

```
@RefreshScope
@SpringBootApplication
@EnableDiscoveryClient
public class Application implements ApplicationRunner {
	
	@Value("${aero.gateway.enabled}")
	private Boolean enabled;
```


## Build 

```
mvn clean package

```



## Deployment 
```
cf push 

```

## Check 
Rufen Sie die Aero Website über die URL <aero-domain/enabled auf. Sie sollten dann den im Repository hinterlegten Wert (true/false) erhalten. 
Sie können dann Änderungen an der Konfiguration vornehmen. Damit der Client die Änderungen liest, können Sie einen manuellen Refresh über den 
Actuator Endpoint durchführen. 


```
<aero-domain/actuator/refresh

```





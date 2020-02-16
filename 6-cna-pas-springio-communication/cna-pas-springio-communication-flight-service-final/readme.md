## Task 

Dieser Flight Service beinhaltet bereits die Implementierung der Pattern Service Registry, Self Registration, Service Discovery
auf Grundlage der  Pivotal Spring Cloud Services und Eureka. 


1. Stellen Sie den Flight Service über Pivotal Web Services zur Verfügung  
 
 
## CLI Plugins
Sie benötigen das Spring Cloud Service Plugin. Sofern es nicht installiert ist, können Sie das wie folgt tun. 

```
cf add-plugin-repo CF-Community https://plugins.cloudfoundry.org
cf install-plugin -r CF-Community "spring-cloud-services"

```


## Login 

```
cf login -a api.run.pivotal.io -u <user> -p <password> 

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

Check the registration of the Flight Service over the **Application Manager** Service Administration oder über das **Spring Cloud Service Plugin** .  .  

```
cf service-registry-info service-registry
cf service-registry-list service-registry

```

## Check API 

Rufen Sie über das REST API GET <flight-service-domain>/flights alle verfügbaren Flüge auf. 
 




# Aufgabe 

Die Fluggesellschaft möchte die Anwendung so erweitern, dass der Route Service über ein **Blue/Green** 
Deployment in die Cloud deployed werden kann. 

1. Führen Sie für den Route Service ein manuelles Blue/Green Deployment  

## Vorbereitung 
Löschen Sie alle Apps in der Cloud. 


## Login 
**Project: cna-pas-springio-deployment-route-service-start**

```
cf login -a api.run.pivotal.io -u <user> -p <password> 

```

## Blau anzeigen 
**Project: cna-pas-springio-deployment-route-service-start**

Um die beiden Versionen des Route Service zu unterscheiden ändern wir in der **index.html** die Farbe der Anwendung. 

Die CSS-Referenz **bg-success** steht für **Grün** und **bg-primary** für **Blau**. 

```
<div class="container my-2 bg-primary">
```

Ändern Sie die Anwendung auf **Blau**. 


## Manifest & Build & Deploy 
**Project: cna-pas-springio-deployment-route-service-start**

Ändern Sie den Namen der Anwendung und die Route  vor dem Deployment auf **Blue**

```
---
applications:
  - name: routeServiceBlue 
    host: routeServiceBlueDeployment 
```

```
mvn clean package
cf push 

```

## Check 
**Project: cna-pas-springio-deployment-route-service-start**

Rufen Sie die Anwendung auf. Diese müsste nun in der Blauen Farbe erscheinen. 



## Grün anzeigen 
**Project: cna-pas-springio-deployment-route-service-start**

Um die beiden Versionen des Route Service zu unterscheiden ändern wir in der **index.html** die Farbe der Anwendung. 
Die CSS-Referenz **bg-success** steht für **Grün** und **bg-primary** für **Blau**. 
Ändern Sie die Anwendung auf **Grün**.

```
<div class="container my-2 bg-success">

```

## Manifest & Build & Deploy 
**Project: cna-pas-springio-deployment-route-service-start**
Ändern Sie den Namen der Anwendung im **Manfest.yaml** vor dem Deployment auf **Grün**.

Löschen Sie vor dem zweiten Deployment in der **application.properties** das Löschen der Datenbank. 

```
#spring.jpa.generate-ddl=true
#spring.jpa.hibernate.ddl-auto=create-drop
#spring.datasource.platform=mysql
#spring.datasource.initialization-mode=always
```


```
---
applications:
  - name: routeServiceGreen
    host: routeServiceGreenDeployment 
```


```
mvn clean package
cf push 

```

## Check 
**Project: cna-pas-springio-deployment-route-service-start**

Rufen Sie die Anwendung auf. Diese müsste nun in der Grünen Farbe erscheinen. 



## Parallelbetrieb  
**Project: cna-pas-springio-deployment-route-service-start**

Beide Anwendungen müssten nun für sich einmal über die Route **Blue** und einmal über die Route **Green** aufgerufen werden können. 
Die neue Anwendung  wird in der Regel mit z.B. einem Somoke-Tests etc. geprüft. Nun kann die Route von der Anwendung Blue auf die Anwendung Green umgeschaltet werden, sodass beide über die Route Blue erreichbar sind. 

```
cf map-route routeServiceGreen cfapps.io -n routeServiceBlueDeployment
```


## Check 
**Project: cna-pas-springio-deployment-route-service-start**
Rufen Sie über den Gateway den Anwendung auf. Diese müsste nun abwechselnd in der Blauen und Grünen Farbe erscheinen. 




## Löschen der Blue Route auf die Blue Anwendung    
**Project: cna-pas-springio-deployment-route-service-start**

Nun kann die Route von der alten Anwendung **Blue** entfernt werden.  

 ```
cf unmap-route routeServiceBlue cfapp.io -n routeServiceBlueDeployment

```


## Löschen der Green Route auf die Green Anwendung 
**Project: cna-pas-springio-deployment-route-service-start**

Nun kann die temporäre Route von der neuen Anwendung **Green** entfernt werden.  


 ```
cf unmap-route routeServiceGreen cfapp.io -n routeServiceGreenDeployment

```


## Install Blue/Green Deployment Plugin 
**Project: cna-pas-springio-deployment-route-service-start**

Die manuelle Umstellung kann auch automatisch mit dem Plugin ```blue-green-deploy`` durchgeführt werden. 
Wenn Sie noch Zeit haben, können Sie das ausprobieren. In der Regel können diese Befehle auch mit eigenen 
Scripts und oder über Build Piplines ausgeführt werden. Das  ``blue-green-deploy`` Plugin ist daher auch 
schon etwas in die Jahre gekommen und erfährt keine Änderungen mehr. 


```
cf add-plugin-repo CF-Community https://plugins.cloudfoundry.org
cf install-plugin blue-green-deploy -r CF-Community

```




## Open Points 

Nicht übersetzen!! 
Es gibt es SQL Exception Error bei dieser Variante ggf. Beim zweiten mal Das Drop weg!! 


#Task

Die Fluggesellschaft **Aero** möchte die Anwendung so erweitern, dass der Route Service über ein Blue/Green 
Deployment in die Cloud deployed werden kann. 

1. Führen Sie für den Route Service ein Blue/Green Deployment ein ohen das bestehende Plugin zu verwenden. 

## Login 

```
cf login -a api.run.pivotal.io -u <user> -p <password> 

```

## Green/Blue anzeigen 

Um die beiden Versionen des Route Service zu unterscheiden ändern wir in der index.html die Farbe der Anwendung. 

Das CSS Referenz bg-success steht für **Grün** und bg-primary für **Blau**. 

```

<div class="container my-2 bg-primary">
```

Ändern Sie die Anwendung auf **Blau**. 


## Build & Deploy 

Ändern Sie den Namen der Anwendung im ``Manfest.yaml` vor dem Deployment auf **Blue**

```
mvn clean package
cf push 

```

## Check 

Rufen Sie über den Gateway die Anwendung auf. Diese müsste nun in der Grünen Farbe erscheinen. 



## Green anzeigen 

Um die beiden Versionen des Route Service zu unterscheiden ändern wir in der index.html die Farbe der Anwendung. 

Das CSS Referenz bg-success steht für **Grün** und bg-primary für **Blau**. 

Ändern Sie die Anwendung auf **Grün**.

```
<div class="container my-2 bg-success">

```

## Build & Deploy 

Ändern Sie den Namen der Anwendung im ``Manfest.yaml` vor dem Deployment auf **Blue**

```
mvn clean package
cf push 

```

## Check 

Rufen Sie über den Gateway den Anwendung auf. Diese müsste nun abwechselnd in der Baluen und Grünen Farbe erscheinen. 




## Enable Green 

Nachdem die neue Anwendung nun läuft. In der Regel wir das gemessen, kann die Route der neuen Anwendung auf die Route der 
alten umgestellt werden. 

 ```
cf map-route green cfapp.io -n <Host from Route from Blue>

```


## Install Blue/Green Deployment Plugin 

Die manuelle Umstellung kann auch automatisch mit dem Plugin ```blue-green-deploy`` durchgeführt werden. 
Wenn Sie noch Zeit haben, können Sie das ausprobieren. In der Regel können diese Befehle auch mit eigenen 
Scripts und oder über Build Piplines ausgeführt werden. Das  ``blue-green-deploy`` Plugin ist daher auch 
schon etwas in die Jahre gekommen und erfährt keine Änderungen mehr. 


```
cf add-plugin-repo CF-Community https://plugins.cloudfoundry.org
cf install-plugin blue-green-deploy -r CF-Community

```




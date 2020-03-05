## Aufgabe 

Die Fluggesellschaft hat einen Route Service in Spring Boot implementiert und in die  Pivotal Application Services (PAS)
gestellt. Da die Anzahl der Benutzer nicht bekannt ist, soll der Route Service automatisch skaliert werden. 

1. Erstellen Sie einen **App Autoscaler Service** 
2. Binden Sie den Service an die Route Service App 
3. Konfigurieren Sie den App Autoscaler Service so, das bei einer **HTTP Latency** zwischen
   20 ms - 30 ms eine neue Route Servce App gestartet wird 
4. Testen Sie das Autoscaling über Apache Ben



## Login 

```
cf login -a api.run.pivotal.io -u <user> -p <password> 

```


## Install App Autoscaler Service 


```
cf create-service app-autoscaler standard autoscaler

```

## Build 

```
mvn clean package

```

## Manifest 

Binden Sie die App an den neuen autoscaler Service über das Manifest manifest.yml.  

```
applications:
  - name: RouteService 
    random-route: true
    path: target/cna-pas-springio-service-start-0.0.1-SNAPSHOT.jar
    services:
    - autoscaler
    
```

## Deployment 
```
cf push 

```

## App Autoscaler CLI Plugin 

Wir benötigen das **App Autoscaler CLI Plugin** um den Service über die Konsole einfacher konfigurieren zu können. 
Das Plugin wird wie folgt installiert.  

Öffenen Sie die Seite [Pivotal Network (Marketplace)](https://network.pivotal.io/products/pcf-app-autoscaler) und laden Sie von dort das Plugin herunter. Im Anschluss daran können Sie das Plugin installieren. 


```
cf install-plugin ./autoscaler-for-pcf-cliplugin-windows64-exe-2.0.233
cf install-plugin ./autoscaler-for-pcf-cliplugin-macosx64-binary-2.0.233

```

## App Autoscaler Configure Service 


Zunächst setzen wir die Grenzen auf 1-2 Instanzen 

```
cf update-autoscaling-limits routeService 1 2

```

Mit dem nächsten Befehl setzen  wir die Regeln **HTTP latency* für das Starten von eneuen Instanzen zwischen 10 and 20 ms. Es ist möglich, dass die Grenzen zu hoch oder zu tief gewählt sind. Das hängt vom Zustand der 
Cloud und mit dem Netzwerk ab, ggf. müssen die Grenzwerte erhöht werden. Rufen Sie über den Application Manager die App auf. Dort können Sie über das Dashboard die aktuellen Zahlen sehen. 



```
cf create-autoscaling-rule routeService http_latency 10 20 -s avg_99th

```

Zum Schluss geben wir den Scaller frei.  

```
cf enable-autoscaling routeService

```


Das ganze können wir nochmal prüfen. 


```
cf autoscaling-apps

```

## Einstellungen Prüfen 
Sie können über den **Application Manager** den **Autocaling Manager** aufrufen und dort 
die aktuellen Einstellungen überprüfen. 


## Apache Bench 

Nun müssen wir unsere Route Service App von aussen Stressen, damit der Autoscaller neue Instanzen startet. 


```
ab -n 10000000 -c 10 -t 60 <URL>/traffic


```
 


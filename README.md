# java-couchdb
Uso del API de Twitter para descargar tweets y almacenarlos en una base NoSQL CouchDB.

Instalar de CouchDB en Ubuntu

sudo apt-get install couchdb


Crear una base de datos en CouchDB vía curl. Ejecutar en la consola:

curl -X PUT http://127.0.0.1:5984/twitterdb


Entrar al sitio de desarrolladores de Twitter y crear una aplicación para obtener los claves correspondientes. Poner los valores en el archivo config.properties, por ejemplo:

consumerSecret=KUXXl1eS32klppoPUYo5aFQMK7d1qlQV7sw3SceoC5gnXXXXX
consumerKey=3LO49LkTSlZioyYvf4rMXXXXX
accessToken=94026329-ahHzE0UQ6yn1gwmuq8TRLgdFPFoxPoqmkP6XXXXX
accessSecret=KPgHqvIvZD206uAwEuP3LOPStz1bRfraFxAFrfXXXXX
couchdb=twitterdb
debug=false

Escribir también el nombre de la base de datos CouchDB creada previamente.


Compilar el proyecto. Se requiere tener instalado Maven,..

mvn clean compile package



Descargar los tweets del usuario "jvm_mx"

java -jar target/java-couchdb-1.0-SNAPSHOT-jar-with-dependencies.jar --history jvm_mx

o bien descargar los tweets relacionados al tema "JVM"

java -jar target/java-couchdb-1.0-SNAPSHOT-jar-with-dependencies.jar --search JVM

En ambos casos el número de tweets descargados está limitado a las restricciones del API de Twitter.


Abrir en el navegador la siguiente dirección para examinar la base de datos

http://127.0.0.1:5984/_utils/index.html
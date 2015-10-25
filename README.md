# java-couchdb
Uso del API de Twitter para descargar tweets y almacenarlos en una base NoSQL CouchDB.

Instalar CouchDB en Ubuntu o Debian.

<pre>
sudo apt-get install couchdb
</pre>

Crear una base de datos en CouchDB vía curl. Ejecutar en la consola:

<pre>
curl -X PUT http://127.0.0.1:5984/twitterdb
</pre>

Entrar al sitio de desarrolladores de Twitter y crear una aplicación para obtener las claves correspondientes. Poner los valores en el archivo config.properties. Escribir también el nombre de la base de datos CouchDB creada previamente. Por ejemplo:

consumerSecret=KUXXl1eS32klppoPUYo5aFQMK7d1qlQV7sw3SceoC5gnXXXXX
consumerKey=3LO49LkTSlZioyYvf4rMXXXXX
accessToken=94026329-ahHzE0UQ6yn1gwmuq8TRLgdFPFoxPoqmkP6XXXXX
accessSecret=KPgHqvIvZD206uAwEuP3LOPStz1bRfraFxAFrfXXXXX
couchdb=twitterdb
debug=false


Compilar el proyecto. Se requiere tener instalado Maven.

<pre>
mvn clean compile package
</pre>


Descargar los tweets del usuario "jvm_mx"

<pre>
java -jar target/java-couchdb-1.0-SNAPSHOT-jar-with-dependencies.jar --history jvm_mx
</pre>

o bien descargar los tweets relacionados al tema "JVM"

<pre>
java -jar target/java-couchdb-1.0-SNAPSHOT-jar-with-dependencies.jar --search JVM
</pre>

En ambos casos el número de tweets descargados está limitado a las restricciones del API de Twitter.


Abrir en el navegador la siguiente dirección para examinar la base de datos

http://127.0.0.1:5984/_utils/index.html

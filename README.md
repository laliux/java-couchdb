# java-couchdb
Uso del API de Twitter para descargar tweets y almacenarlos en una base NoSQL CouchDB.

<h3>CouchDB</h3>
Instalar CouchDB en Ubuntu o Debian.

<pre>
sudo apt-get install couchdb
</pre>

Crear una base de datos en CouchDB vía curl. Ejecutar en la consola:

<pre>
curl -X PUT http://127.0.0.1:5984/twitterdb
</pre>

<h3>Twitter</h3>
Entrar al sitio de desarrolladores de Twitter y crear una aplicación para obtener las claves correspondientes. Poner los valores en el archivo <i>config.properties</i>. Escribir también el nombre de la base de datos CouchDB creada previamente. Por ejemplo:

<p>
<b>consumerSecret</b>=KUXXl1eS32klppoPUYo5aFQMK7d1qlQV7sw3SceoC5gnXXXXX
<b>consumerKey</b>=3LO49LkTSlZioyYvf4rMXXXXX <br>
<b>accessToken</b>=94026329-ahHzE0UQ6yn1gwmuq8TRLgdFPFoxPoqmkP6XXXXX <br>
<b>accessSecret</b>=KPgHqvIvZD206uAwEuP3LOPStz1bRfraFxAFrfXXXXX <br>
<b>couchdb</b>=twitterdb <br>
<b>debug</b>=false
</p>

<h3>Java</h3>

Compilar el proyecto. Se requiere tener instalado Maven.

<pre>
mvn clean compile package
</pre>


Descargar los tweets del usuario "<b>jvm_mx</b>"

<pre>
java -jar target/java-couchdb-1.0-SNAPSHOT-jar-with-dependencies.jar --history jvm_mx
</pre>

o bien descargar los tweets relacionados al tema "<b>JVM</b>"

<pre>
java -jar target/java-couchdb-1.0-SNAPSHOT-jar-with-dependencies.jar --search JVM
</pre>

En ambos casos el número de tweets descargados está limitado a las restricciones del API de Twitter.


Abrir en el navegador la siguiente dirección para examinar la base de datos

http://127.0.0.1:5984/_utils/index.html

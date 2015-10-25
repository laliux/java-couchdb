# java-couchdb
Uso del API de Twitter para descargar tweets y almacenarlos en una base NoSQL CouchDB.

Instalación de CouchDB en Ubuntu

sudo apt-get install couchdb


Creación de una base de datos en CouchDB vía curl. Ejecutar en la consola:

curl -X PUT http://127.0.0.1:5984/twitterdb



Descargar por ejemplo los tweets del usuario "jvm_mx"

java -jar target/java-couchdb-1.0-SNAPSHOT-jar-with-dependencies.jar --history jvm_mx

o bien descargar los tweets relacionados al tema "JVM"

java -jar target/java-couchdb-1.0-SNAPSHOT-jar-with-dependencies.jar --search JVM

En ambos casos el número de tweets descargados está limitado a las restricciones del API de Twitter.


Abrir en el navegador la siguiente dirección para examinar la base de datos

http://127.0.0.1:5984/_utils/index.html
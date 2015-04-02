# B7
* Igor Dzinka (idzinkdz7@alumnes.ub.edu)
* Vicent Roig (vroigrip8@alumnes.ub.edu)

## SD | Pràctica 1 client-servidor.
L'objectiu docent de la pràctica és aprendre a utilitzar els mecanismes de programació Client/Servidor en JAVA. Concretament és necessari que aprengueu com programar amb:
* Sockets amb JAVA (utilitzant l'API Socket de Java.net)
* Servidor multi-petició amb i sense threads (JAVA)

## Set i mig
El Joc del Set i mig és un famós joc de cartes similar al blackjack que es juga en els casinos. En aquesta pràctica implementarem una versió client/servidor del joc. En aquesta versió un sol client jugarà contra el servidor, però el servidor podrà servir múltiples partides alhora.

###### Compilació i Execució
```
# recommended way (using system find to get a list for java files + creating special directory):
:~/B7/setimig.client$ mkdir classes
:~/B7/setimig.client$ find . -name "*.java" -print | xargs javac -d classes -cp ../lib/commons-cli-1.2.jar:

# execution client example...
:~/B7/setimig.client$ cd classes/
:~/B7/setimig.client/classes$ java -cp ../lib/commons-cli-1.2.jar:. Client -s localhost -p 1212

# explicit more typical-alike method:
:~/B7/setimig.client$ cd src/
:~/B7/setimig.client/src$ javac -cp ../lib/commons-cli-1.2.jar: Client.java controller/*.java model/*.java utils/*.java view/*.java

# execution
:~/B7/setimig.client/src$ java -cp ../lib/commons-cli-1.2.jar:. Client -s localhost -p 1212
```

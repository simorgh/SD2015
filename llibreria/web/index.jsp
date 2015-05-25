<%-- 
    Document   : index
    Created on : 02-may-2015, 16:52:26
    Author     : simorgh
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
    
    <head>
        <title>Apache Tomcat Examples</title>
        <META http-equiv=Content-Type content="text/html">
    </head>

    <body>
        <p><h3>Llibreria de Recursos Electrònics Online</h3></p>
    
        <ul>
            <!-- <li><a href="WEB-INF/jsp/non-protected.jsp">Pàgina pública (cataleg)</a>: no requereix estar identificat.</li> -->
            <li><a href="./cataleg">Cataleg</a>: no requereix estar identificat.</li>
            <li><a href="./protegit/llista">Llista de descàrregues</a>: requereix estar identificat.</li>
                <ul>
                    <li>cal que tingueu els usuaris declarats a tomcat/conf/tomcat-users.xml, per exemple:<br/>
                        &lt;user username="alumne1" password="alumne1" roles="Alumne"/&gt;
                    </li>
                </ul>
            <li>Documentació Tomcat: <a href="http://tomcat.apache.org/tomcat-6.0-doc/realm-howto.html">Realm-HowTo</a></li>
            <li>Exemple Tomcat: <a href="http://localhost:8080/examples/security/protected">exemple de seguretat</a></li>
        </ul>  
    
    </body>
</html>
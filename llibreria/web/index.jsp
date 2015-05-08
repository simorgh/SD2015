<%-- 
    Document   : index
    Created on : 02-may-2015, 16:52:26
    Author     : simorgh
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!--
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Hello World!</h1>
    </body>
</html>
-->

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
    
    <head>
        <title>Apache Tomcat Examples</title>
        <META http-equiv=Content-Type content="text/html">
    </head>

    <body>
        <P><h3>Exemple d'autentificació</h3></P>
        Exemple del mecanisme d'autentificació de Tomcat
        <ul>
            <!-- <li><a href="WEB-INF/jsp/non-protected.jsp">Pàgina pública (cataleg)</a>: no requereix estar identificat.</li> -->
            <li><a href="WEB-INF/jsp/cataleg.jsp">Cataleg</a>: no requereix estar identificat.</li>
            <li><a href="WEB-INF/jsp/protected/secret.jsp">Pàgina protegida</a>: requereix estar identificat.</li>
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



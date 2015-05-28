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
        <p><h1>Llibreria de Recursos Electrònics Online</h1></p>
    
        <ul>
            <!-- <li><a href="WEB-INF/jsp/non-protected.jsp">Pàgina pública (cataleg)</a>: no requereix estar identificat.</li> -->
            <li><a href="./cataleg">Cataleg</a>: no requereix estar identificat.</li>
            <li><a href="./protegit/llista">Llista de descàrregues</a>: requereix estar identificat.</li>
        </ul>  
    
    </body>
</html>
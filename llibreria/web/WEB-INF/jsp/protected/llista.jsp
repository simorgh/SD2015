<%-- 
    Document   : llista.jsp
    Created on : 24-may-2015, 17:20:23
    Author     : simorgh
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    
    <body>
        <h1>Llista de descàrregues</h1>
        
        Si heu arribat a aquesta pàgina és perquè us heu identificat correctament com a
        <b><%= request.getRemoteUser() %></b>. <br><br>
        
        Rols:<br>
        Rol "Professor" <%= (request.isUserInRole("Professor"))?"assignat":"no assignat" %><br>
        Rol "Alumne" <%= (request.isUserInRole("Alumne"))?"assignat":"no assignat" %><br>
        <br><br>
        
        <a href="/llibreria/index.jsp?logoff=true">Sortir de l'usuari actual</a>
    </body>
</html>

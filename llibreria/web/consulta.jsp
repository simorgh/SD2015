<%-- 
    Document   : consulta
    Created on : May 27, 2015, 8:52:49 PM
    Author     : igor
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>WebService</title>
        <link href="/llibreria/static/css/style.css" rel="stylesheet" type="text/css"/>
    </head>
    <body>
        <!-- header module -->
        <div id="header">
            <h1>Llibreria de Recursos Electrònics Online</h1>
            <ul id="nav" >
              <li><a href="./">Inici</a></li>
              <li><a href="./protegit/llista">Llista</a></li>
              <li><a href="./cataleg">Cataleg</a></li>
              <li><a href="">Consulta</a></li>
            </ul>
        </div><!--header module -->
        
        <h1>Consulta de item</h1>
        <form action="" method="POST">
            <input type="text" name ="consulta_text" value="Consulta">
            <input type="submit" value="Consulta">
        </form> 
    </body>
</html>

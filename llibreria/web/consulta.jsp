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
        <script src="/llibreria/static/js/jquery-1.11.3.min.js"></script><!-- jQuery -->
        <script src="/llibreria/static/js/consulta.js"></script>

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
        <div>
            <input id="search-product" type="text" name ="search-product" value="Consulta">
            <input id="update-btn" type="submit" value="Consulta">
        </div>
        
        <!-- results should come here -->
        <div id="search-result"></div>
        
    </body>
</html>

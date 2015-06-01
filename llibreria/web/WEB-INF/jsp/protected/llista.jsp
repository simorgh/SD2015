<%-- 
    Document   : llista.jsp
    Created on : 24-may-2015, 17:20:23
    Author     : simorgh
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Llista de descarregues</title>
        <link href="/llibreria/static/css/style.css" rel="stylesheet" type="text/css"/>
        
        <style>
            ul.products li {
                width: 200px;
                height: 300px;
                background-color: gray;
                padding: 5px;
                vertical-align: text-top;
                display: inline-block;
                border-style: solid;
                border-color: blue;
                border-width: 2px;
                margin:auto;
            }
            .myButton {
                background-color:#44c767;
                -moz-border-radius:28px;
                -webkit-border-radius:28px;
                border-radius:28px;
                border:1px solid #18ab29;
                display:inline-block;
                cursor:pointer;
                color:#ffffff;
                font-family:Arial;
                font-size:13px;
                padding:10px 15px;
                text-decoration:none;
                text-shadow:0px 1px 0px #2f6627;
                margin-left:auto;
                margin-right:auto;
            }
            .myButton:hover {
                background-color:#5cbf2a;
            }
            .myButton:active {
                position:relative;
                top:1px;
            }
        </style>
        
    </head>
    
    <body>
        Benvingut <b><%= request.getRemoteUser() %></b> <a href="../logout">Sortir</a><br><br>
        <!-- header module -->
        <div id="header">
            <h1>Llibreria de Recursos Electrònics Online</h1>

            <ul id="nav" >
              <li><a href="../">Inici</a></li>
              <li><a href="#">Llista</a></li>
              <li><a href="../cataleg">Cataleg</a></li>
            </ul>
        </div><!--header module -->
        
        
        
        <ul class="products">
            <h3>Descarregues disponibles</h3>
            <c:forEach var="p" items="${purchased}">
                <li>
                    <img src=${p.getThumbnail()}>
                    <h4><c:out value="${p.getName()}"/></h4>
                    <p><c:out value="${p.getDescription()}"/></p>
                    <a class="myButton" href="./download?pid=${p.getPid()}">Descarrega</a>
                </li>   
            </c:forEach>
        </ul>
        
                
        <ul class="products">
            <h3>Actualment al carret</h3>
            <c:forEach var="i" items="${cart}">
                <li>
                    <img src=${i.getThumbnail()}>
                    <h4><c:out value="${i.getName()}"/></h4>
                    <p><c:out value="${i.getDescription()}"/></p>
                    <a class="myButton" href="./comprar?pid=${i.getPid()}">Compra!</a>
                </li>   
            </c:forEach>
        </ul>

        
    </body>
</html>

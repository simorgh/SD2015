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
        <title>JSP Page</title>
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
        <h1>Llista de descàrregues</h1>
        Benvingut <b><%= request.getRemoteUser() %></b>
     <!--   
        Rols:<br>
        Rol "Professor" <%= (request.isUserInRole("Professor"))?"assignat":"no assignat" %><br>
        Rol "Alumne" <%= (request.isUserInRole("Alumne"))?"assignat":"no assignat" %><br>  
     -->
        <a href="logout">Sortir</a><br><br>
        
        
        <ul class="products">
            <h3>Downloads Available</h3>
            <c:forEach var="p" items="${products}">
                <li>
                    <img src=../${p.getThumbnail()}>
                    <h4><c:out value="${p.getName()}"/></h4>
                    <p><c:out value="${p.getDescription()}"/></p>
                    <a class="myButton" href="llibreria//download?param=${p.getPath()}">Descarrega</a>
                </li>
                
            </c:forEach>
        </ul>
        
    </body>
</html>

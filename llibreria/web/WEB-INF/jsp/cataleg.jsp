<%-- 
    Document   : cataleg
    Created on : 07-may-2015, 17:58:18
    Author     : simorgh
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<jsp:useBean id="books" class="java.util.ArrayList" scope="request"/>
<jsp:useBean id="audio" class="java.util.ArrayList" scope="request"/>
<jsp:useBean id="video" class="java.util.ArrayList" scope="request"/>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
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
            }
        </style>
        <title>Cataleg</title>
    </head>
    
    <body>
        <h1>Cataleg</h1>
        
        <ul class="products">
            
            <h3>Books</h3>
            <c:forEach var="b" items="${books}">
                <li>
                    <img src=.${b.getThumbnail()}>
                    <h4><c:out value="${b.getName()}"/></h4>
                    <p><c:out value="${b.getDescription()}"/></p>
                    <p>$<c:out value="${b.getPrice()}"/></p>
                    <a href="llibreria/compra?item=${p.getPid()}">Afegeix al carret</a>
                </li>
            </c:forEach>

           <h3>Audio</h3>
            <c:forEach var="a" items="${audio}">
                <li>
                    <img src=.${a.getThumbnail()}>
                    <h4><c:out value="${a.getName()}"/></h4>
                    <p><c:out value="${a.getDescription()}"/></p>
                    <p>$<c:out value="${a.getPrice()}"/></p>
                    <a href="llibreria/compra?item=${a.getPid()}">Afegeix al carret</a>
                </li>
            </c:forEach>

            <h3>Videos</h3>
            <c:forEach var="v" items="${video}">
                <li>
                    <img src=.${v.getThumbnail()}>
                    <h4><c:out value="${v.getName()}"/></h4>
                    <p><c:out value="${v.getDescription()}"/></p>
                    <p>$<c:out value="${v.getPrice()}"/></p>
                    <a href="llibreria/compra?item=${v.getPid()}">Afegeix al carret</a>
                </li>
            </c:forEach>
        </ul>
    </body>
</html>

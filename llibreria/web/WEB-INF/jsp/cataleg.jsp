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
        <title>Llibreria - Cataleg</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link href="/llibreria/static/css/bootstrap.css" rel="stylesheet">
        <link href="/llibreria/static/css/bootstrap-responsive.css" rel="stylesheet" media="screen">
        <link href="/llibreria/static/css/hosting.css" rel="stylesheet" media="all">
        <link href="/llibreria/static/css/style.css" rel="stylesheet" type="text/css"/>
    </head>
    
    <body>
        <!-- header module -->
        <div id="header">
            <h1>Llibreria de Recursos Electrònics Online</h1>

            <ul id="nav" >
              <li><a href="./">Inici</a></li>
              <li><a href="./protegit/llista">Llista</a></li>
              <li><a href="">Cataleg</a></li>
            </ul>
        </div><!--header module -->
       
        
        <div class="container">  <!-- Contaner Starts -->
            
            <div class="row-fluid"><!-- BOOKS (Row2) start --> 
                <c:forEach var="b" items="${books}"> 
                <div class="span3 PlanPricing template4">
                    <div class="planName">
                        <span class="price">${b.getPrice()}€</span>
                        <h2><c:out value="${b.getType()}"/></h2>
                        <p><c:out value="${b.getName()}"/></p>
                    </div>
                    <div class="planFeatures">
                        <ul>
                           <li><img src=${b.getThumbnail()}></li>
                           <li><c:out value="${b.getDescription()}"/></li>
                        </ul>
                    </div>
                    <p><a href="./afegir?item=${b.getPid()}" role="button" data-toggle="modal" class="btn btn-success btn-large">Afegeix</a></p>
                </div>
                </c:forEach>
            </div><!-- Row2 ends -->
 
            
            <div class="row-fluid"><!-- AUDIO (Row3) start -->
                <c:forEach var="a" items="${audio}"> 
                <div class="span3 PlanPricing template4">   
                    <div class="planName">
                        <span class="price">${a.getPrice()}€</span>
                        <h2><c:out value="${a.getType()}"/></h2>
                        <p><c:out value="${a.getName()}"/></p>
                    </div>
                    <div class="planFeatures">
                        <ul>
                           <li><img src=${a.getThumbnail()}></li>
                           <li><c:out value="${a.getDescription()}"/></li>
                        </ul>
                    </div>
                     <p><a href="./afegir?item=${a.getPid()}" role="button" data-toggle="modal" class="btn btn-success btn-large">Afegeix</a></p>
                </div>
                </c:forEach>
            </div><!-- Row3 ends -->

            
            <div class="row-fluid"><!-- VIDEO (Row4) start --> 
                <c:forEach var="v" items="${video}"> 
                <div class="span3 PlanPricing template4">   
                    <div class="planName">
                        <span class="price">${v.getPrice()}€</span>
                        <h2><c:out value="${v.getType()}"/></h2>
                        <p><c:out value="${v.getName()}"/></p>
                    </div>
                    <div class="planFeatures">
                        <ul>
                           <li><img src=${v.getThumbnail()}></li>
                           <li><c:out value="${v.getDescription()}"/></li>
                        </ul>
                    </div>
                    <p><a href="./afegir?item=${v.getPid()}" role="button" data-toggle="modal" class="btn btn-success btn-large">Afegeix</a></p>
                </div>
                </c:forEach>
            </div><!-- Row3 ends -->

                
        </div> <!-- Container ends -->
        <br><br><br><br><br>
    </body>
</html>

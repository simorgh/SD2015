<%-- 
    Document   : index
    Created on : 02-may-2015, 16:52:26
    Author     : simorgh
--%>

<%@page import="model.User"%>
<%@page import="controller.DataManager"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
    
    <head>
        <title>Llibreria - Inici</title>
        <META http-equiv=Content-Type content="text/html">
        <link href="/llibreria/static/css/style.css" rel="stylesheet" type="text/css"/>
    </head>

    <body>

        <!-- header module -->
        <div id="header">
            <h1>Llibreria de Recursos Electrònics Online</h1>
            <ul id="nav" >
              <li><a href="">Inici</a></li>
              <li><a href="./protegit/llista">Llista</a></li>
              <li><a href="./cataleg">Cataleg</a></li>
              <li><a href="./consulta">Consulta</a></li>
            </ul>
        </div><!--header module -->
        <br><br>
       
        <div>
            <h2>Pàgina de Benvinguda</h2>

            <h3>Objectius de la pràctica</h3>
            <p>Es vol que l'alumne aprengui a fer ús de les tecnologies de desenvolupament web en la plataforma Java.</p>

            <h3>Què cal fer?</h3>
            <p>Es desitja fer una llibreria Online de recursos digitals, que permeti als usuaris baixar-se llibres, cançons, vídeos curts.. en qualsevol tipus de format mp3, pdf, avi... si tenen prou crèdit per adquirir-les.</p>

            <h3>Tecnologies</h3>
            <p>Els requeriments d'aquest sistema fan ús de les següents tecnologies:
            La implementació ha de seguir el patró de programació de Model-Vista-Controlador.
            La programació ha de ser en llenguatge Java usant Servlets i JSP.
            Ús de Tomcat com a contenidor de servlets.
            L'autenticació usarà l'especificació JASS.
            Implementació d'un Servei Web RESTful usant servlets i JSON.</p>
        <div>
        
    </body>
</html>
<%-- 
    Document   : error404
    Created on : 08-may-2015, 17:39:09
    Author     : simorgh
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<html>
    <head>
        <title>- ooops! -</title>
        <style>
        
            body {
                background:#0000aa;
                color:#ffffff;
                font-family:courier;
                font-size:12pt;
                text-align:center;
                margin:100px;
            }

            blink {
                color:yellow;
            }

            .neg {
                background:#fff;
                color:#0000aa;
                padding:2px 8px;
                font-weight:bold;
            }

            p {
                margin:30px 100px;
                text-align:left;
            }

            a,a:hover {
                color:inherit;
                font:inherit;
            }

            .menu {
                text-align:center;
                margin-top:50px;
            }
        
        </style>
        
        
        <link rel="stylesheet" type="text/css" href="/static/css/404.css"/>
    </head>
    
    <body>
        <span class="neg">ERROR 404</span>
        <p>
            The page is missing or never was written. You can wait and<br/>
            see if it becomes available again, or you can restart your computer.	
        </p>
        <p>
            * Send us an e-mail to notify this and try it later.<br/>
            * Press CTRL+ALT+DEL to restart your computer. You will<br/>
             &nbsp; lose unsaved information in any programs that are running.
        </p>
        Press any link to continue <blink>_</blink>
        <div class="menu">
            <a href="/llibreria">index</a> | <a href="/llibreria/cataleg">cataleg</a>
        </div>
    </body>
</html>

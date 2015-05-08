<html>
    <head>
        <title>Autentificació</title>
        <link rel="stylesheet" type="text/css" href="/static/css/login.css"/>
    </head>
    
    <body>
        <h1>Entrada amb autentificació</h1>
        <form method='POST' action='<%= response.encodeURL("j_security_check") %>'>
            Usuari:
            <input type='text' name='j_username'><br>
            Paraula de pas:
            <input type='password' name='j_password'><br>
            <input type="submit" value="Enviar">
        </form>	
    </body>
</html>

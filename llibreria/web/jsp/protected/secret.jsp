<html>
<head>
<title>P�gina protegida</title>
</head>
<body bgcolor="white">

Si heu arribat a aquesta p�gina �s perqu� us heu identificat correctament com a
<b><%= request.getRemoteUser() %></b>.  <br><br>

Rols:<br>
<br>
Rol "Professor" <%= (request.isUserInRole("Professor"))?"assignat":"no assignat" %><br>
Rol "Alumne"    <%= (request.isUserInRole("Alumne"   ))?"assignat":"no assignat" %><br>

<br>
<br>


<a href='../non-protected.jsp?logoff=true'>Sortir de l'usuari actual</a>.

</body>
</html>

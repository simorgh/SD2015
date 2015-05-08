<%
  if (request.getParameter("logoff") != null) {
    session.invalidate();
  }
%>

<html>
<head>
<title>Pàgina pública</title>
</head>
<body bgcolor="white">

Pàgina pública per a la que no es necessita autentificació.
<br/>
<br/>

Tornar a la pàgina <a href="../index.jsp">inicial</a> de l'exemple.

</body>
</html>

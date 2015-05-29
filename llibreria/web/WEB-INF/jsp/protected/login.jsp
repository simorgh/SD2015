<html>
    <head>
        <title>Autentificació</title>
        <link rel="stylesheet" type="text/css" href="/llibreria/static/css/login.css"/>
    </head>
    
    <body>
        <!-- Form Code Start -->
        <div id='membersite' >
          <form id='login'  method='POST' action='<%= response.encodeURL("j_security_check") %>' accept-charset='UTF-8'>
            <fieldset >
                <legend>Login</legend>

                <div class='container'>
                    <label for='username' >Username:</label><br/>
                    <input type='text' id='username' name='j_username' maxlength="50"/>
                </div>

                <div class='container'>
                    <label for='password' >Password:</label><br/>
                    <input type='password' id='password' name='j_password' maxlength="50" /><br/>
                </div>

                <div class='container'>
                    <input id='submit' name='submit' type='submit' value='Submit' />
                </div>
            </fieldset>
          </form>

        </div><!-- Form Code End -->
        
    </body>
</html>

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author simorgh
 */
public class ServletDispatcher extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        } finally {
            out.close();
        }
    }
    
    // SERVLET ==================================================
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        locationProxy(request, response);
        //processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        locationProxy(request, response);
        //processRequest(request, response);
        //TODO. Implement POST-Redirect-GET Pattern Design.
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
    
    
    
    
    
    // LOCATIONS ================================================
    public void locationProxy(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("@locationProxy");
        String CONTEXT = request.getContextPath();
        String location = request.getRequestURI();
	
        if (location.equals(CONTEXT + "/")) {
	    showPage(request, response, "index.jsp");
	} else if(location.equals(CONTEXT + "/cataleg")) {
            showPage(request, response, "non-protected.jsp");
        }
/*      else if (location.equals(CONTEXT + "/login")) {
	    showPage(request, response, "login.jsp");
	} else if (location.equals(CONTEXT + "/logout")) {
	    request.getSession().invalidate();
	    response.sendRedirect(CONTEXT + "/");        
	} else if (location.equals(CONTEXT + "/Cataleg")) {
	    showCataleg(request, response);
	} else if (location.equals(CONTEXT + "/Cistell")) {
	    showCistell(request, response);
	} else if (location.equals(CONTEXT + "/Historial")) {
	    showHistorial(request, response);
	} else if (location.contains(CONTEXT + "/Producte")) {
	    controlProduct(request, response);
	} else if (location.contains(CONTEXT + "/augsaldo")) {
	    controlWebServices(request, response);
	} else {
	    showPage(request, response, "error404.jsp");
	}
*/
    }


    // PAGES ====================================================
    public void showPage1ini(HttpServletRequest request, HttpServletResponse response, String currentTime) throws ServletException, IOException {
        HttpSession session = request.getSession();
        session.setAttribute("currentTime", currentTime);
        showPage( request, response, "page1ini.jsp" );
    }

    public void showPage1end(HttpServletRequest request, HttpServletResponse response, int points) throws ServletException, IOException {
        HttpSession session = request.getSession();
        session.setAttribute("points", points);
        showPage( request, response, "page1end.jsp" );
    }

    public void showPageInternalError(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        showPage( request, response, "internalError.jsp" );
    }

    // PROCESS ==================================================
    public void processPage1(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (true) showPage1ini(request, response, "100");
        else showPage1end(request, response, 100);
    }

    public void showPage(HttpServletRequest request, HttpServletResponse response, String jspPage) throws ServletException, IOException{
        ServletContext sc = getServletContext();
        RequestDispatcher rd = sc.getRequestDispatcher( "/jsp/" + jspPage);
        rd.forward(request, response);
    }

}
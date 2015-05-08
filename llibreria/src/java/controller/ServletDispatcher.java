/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Data;
import model.Product;

/**
 *
 * @author simorgh
 */
public class ServletDispatcher extends HttpServlet {
    private Data data;
    
    @Override
    public void init() throws ServletException {
	super.init();
	ServletContext c = getServletContext();
	String users = c.getRealPath("WEB-INF/users.json");
	String products = c.getRealPath("WEB-INF/products.json");

	data = new Data(users, products);
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
        //System.out.println("@locationProxy");
        String CONTEXT = request.getContextPath();
        String location = request.getRequestURI();
	
        if (location.equals(CONTEXT + "/")) {
	    showPage(request, response, "/index.jsp");
	} else if(location.equals(CONTEXT + "/cataleg")) {
            showCataleg(request, response);
        }
/*      
        else if (location.equals(CONTEXT + "/login")) {
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
	}
*/
        else {
	    showPage(request, response, "/error404.jsp");
	}
    }


    // PAGES ====================================================
    private void showCataleg(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	HashMap<String, Product> basket = null;/* = getBasket(request); */

        ArrayList<Product> lib_books = new ArrayList();
	ArrayList<Product> lib_audio = new ArrayList();
	ArrayList<Product> lib_video = new ArrayList();

        /* Let's show only the products which are not purchased yet.*/
	for (Product p : data.getProducts().values()) {
            if(p.getType()==Product.FileType.BOOK && !basket.containsKey(p.getName())) lib_books.add(p); 
            else if(p.getType()==Product.FileType.AUDIO && !basket.containsKey(p.getName())) lib_audio.add(p);
            else if(p.getType()==Product.FileType.VIDEO && !basket.containsKey(p.getName())) lib_video.add(p);  
	}
        
        request.setAttribute("BOOKS", lib_books);
	request.setAttribute("AUDIO", lib_audio);
	request.setAttribute("VIDEO", lib_video);
        
	showPage(request, response, "/WEB-INF/jsp/cataleg.jsp");
    }
    
/*
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
*/


    public void showPage(HttpServletRequest request, HttpServletResponse response, String jspPage) throws ServletException, IOException{
        ServletContext sc = getServletContext();
        RequestDispatcher rd = sc.getRequestDispatcher(jspPage);
        rd.forward(request, response);
    }

    
    
}
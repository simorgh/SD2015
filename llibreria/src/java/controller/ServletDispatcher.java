package controller;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import beans.Product;

/**
 * @author simorgh
 */
public class ServletDispatcher extends HttpServlet {
    private DataManager data;
    
    @Override
    public void init() throws ServletException {
	super.init();
	ServletContext c = getServletContext();
	String users = c.getRealPath("WEB-INF/users.json");
	String products = c.getRealPath("WEB-INF/products.json");

	data = DataManager.getInstance(users, products);
    }
    
    
    ////////////////////////////////////////////////////////
    //                      SERVLET
    ////////////////////////////////////////////////////////
    
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
    }
    
    
    ////////////////////////////////////////////////////////////////
    //                       LOCATIONS
    ////////////////////////////////////////////////////////////////
    public void locationProxy(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String CONTEXT = request.getContextPath();
        String location = request.getRequestURI();
	
        if (location.equals(CONTEXT + "/")) {
	    showPage(request, response, "/index.jsp");
	} else if(location.equals(CONTEXT + "/cataleg")) {
            showCataleg(request, response);
        } else if (location.equals(CONTEXT + "/protegit/llista")) {
	    showBasket(request, response);
        } else {
	    showPage(request, response, "/error404.jsp");
	}
    }


    ////////////////////////////////////////////////////////////////
    //                       PAGES
    ////////////////////////////////////////////////////////////////
    private void showCataleg(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //HashMap<String, Product> basket = getBasket(request);
        ArrayList<Product> books = new ArrayList();
	ArrayList<Product> audio = new ArrayList();
	ArrayList<Product> video = new ArrayList();

        /* Let's show only the products which are not purchased yet.*/
	for (Product p : data.getProducts().values()) {
            if(p.getType()==DataManager.FileType.BOOK) books.add(p);
            else if (p.getType()==DataManager.FileType.AUDIO) audio.add(p);
            else if (p.getType()==DataManager.FileType.VIDEO) video.add(p);
            //if(p.getType()==Product.FileType.BOOK && !basket.containsKey(p.getName())) arr_books.add(p); 
            //else if(p.getType()==Product.FileType.AUDIO && !basket.containsKey(p.getName())) arr_audio.add(p);
            //else if(p.getType()==Product.FileType.VIDEO && !basket.containsKey(p.getName())) arr_video.add(p);  
	}
        
        request.setAttribute("books", books);
	request.setAttribute("audio", audio);
	request.setAttribute("video", video); 
	showPage(request, response, "/WEB-INF/jsp/cataleg.jsp");
    }
    
    private void showBasket(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        showPage(request, response, "/WEB-INF/jsp/protected/llista.jsp");
    }

    public void showPage(HttpServletRequest request, HttpServletResponse response, String jspPage) throws ServletException, IOException{
        ServletContext sc = getServletContext();
        RequestDispatcher rd = sc.getRequestDispatcher(jspPage);
        rd.forward(request, response);
    }
/*
    private void downloadResource(HttpServletResponse response) throws IOException {
        ServletOutputStream outStream = response.getOutputStream();
        response.setContentLength((int) file.length());
        response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");

        byte[] byteBuffer = new byte[1024];
        DataInputStream in = new DataInputStream(new FileInputStream(file));
        while ((length = in.read(byteBuffer)) != -1) {
            outStream.write(byteBuffer, 0, length);
        }
        in.close();
        outStream.close();
    }
 */   
}
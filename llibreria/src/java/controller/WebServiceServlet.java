/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import beans.Product;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import controller.DataManager.FileType;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author igor
 */
@WebServlet(name = "WebServiceServlet", urlPatterns = {"/WebServiceServlet"})
public class WebServiceServlet extends HttpServlet {
     private DataManager data;
     
     
    @Override
    public void init() throws ServletException {
	super.init();
	ServletContext c = getServletContext();
	String users = c.getRealPath("WEB-INF/users.json");
	String products = c.getRealPath("WEB-INF/products.json");

	data = DataManager.getInstance(users, products);
    }

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
            response.setHeader("Access-Control-Allow-Origin","*");
            response.setContentType("application/json");
            //String text = getStringFile(products);
            //TEST GSON
            for (Product p : data.getProducts().values()) {
                if(p.getType().equals(FileType.BOOK)){
                    Gson gson = new Gson();
                    out.println(gson.toJson(p));
                }
            }
           
        /*} finally {
            out.close();*/
    }
    
        public void locationProxy(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String CONTEXT = request.getContextPath();
        String location = request.getRequestURI();
	PrintWriter out = response.getWriter();
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin","*");
        response.setContentType("application/json");
        
        if (location.contains(CONTEXT + "/API/AUDIO/")) {
             if (location.equals(CONTEXT + "/API/AUDIO/cataleg")) {
                for (Product p : data.getProducts().values()) {
                    if(p.getType().equals(FileType.AUDIO)){
                        //Gson gson = new Gson();
                        //out.println(gson.toJson(p));
                        JsonObject obj = new JsonObject();
                        obj.addProperty("NAME", p.getName());
                        obj.addProperty("DESC", p.getDescription());
                        out.println(obj);    
                    }
                }
             }
             
             else if (location.contains(CONTEXT + "/API/AUDIO/item/")){
                 String idStr = location.substring(location.lastIndexOf('/') + 1);
                  for (Product p : data.getProducts().values()) {
                    if(p.getType().equals(FileType.AUDIO) && p.getName().endsWith(idStr)){
                        JsonObject obj = new JsonObject();
                        obj.addProperty("PRICE", p.getPrice());
                        obj.addProperty("LINK", p.getPath());
                        out.println(obj);    
                    }
                }
             } 

	} else if (location.contains(CONTEXT + "/API/VIDEO/")) {
             if (location.equals(CONTEXT + "/API/VIDEO/cataleg")) {
                for (Product p : data.getProducts().values()) {
                    if(p.getType().equals(FileType.VIDEO)){
                        //Gson gson = new Gson();
                        //out.println(gson.toJson(p));
                         JsonObject obj = new JsonObject();
                        obj.addProperty("NAME", p.getName());
                        obj.addProperty("DESC", p.getDescription());
                        out.println(obj);    
                    }
                }
             }
             
             else if (location.contains(CONTEXT + "/API/VIDEO/item/")){
                 String idStr = location.substring(location.lastIndexOf('/') + 1);
                  for (Product p : data.getProducts().values()) {
                    if(p.getType().equals(FileType.VIDEO) && p.getName().endsWith(idStr)){
                        JsonObject obj = new JsonObject();
                        obj.addProperty("PRICE", p.getPrice());
                        obj.addProperty("LINK", p.getPath());
                        out.println(obj);
                    }
                }
             }

	}else if (location.contains(CONTEXT + "/API/BOOK/")) {
             if (location.equals(CONTEXT + "/API/BOOK/cataleg")) {
                for (Product p : data.getProducts().values()) {
                    if(p.getType().equals(FileType.BOOK)){
                        //Gson gson = new Gson();
                        //out.println(gson.toJson(p));
                        JsonObject obj = new JsonObject();
                        obj.addProperty("NAME", p.getName());
                        obj.addProperty("DESC", p.getDescription());
                        out.println(obj); 
                    }
                }
             }
             
             else if (location.contains(CONTEXT + "/API/BOOK/item/")) {
                 String idStr = location.substring(location.lastIndexOf('/') + 1).replace("%20", " ");
                 System.out.println(idStr);
                  for (Product p : data.getProducts().values()) {
                    if(p.getType().equals(FileType.BOOK) && p.getName().endsWith(idStr)){
                        //Gson gson = new Gson();
                        //out.println(gson.toJson(p));
                        JsonObject obj = new JsonObject();
                        obj.addProperty("PRICE", p.getPrice());
                        obj.addProperty("LINK", p.getPath());
                        out.println(obj); 
                    }
                }
             }

	} 
    }
        
        void showPage(HttpServletRequest request, HttpServletResponse response, String jspPage) throws ServletException, IOException{
            ServletContext sc = getServletContext();
            RequestDispatcher rd = sc.getRequestDispatcher(jspPage);
            rd.forward(request, response);
        }
    
    
    
    

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
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
       locationProxy(request, response);
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        locationProxy(request, response);
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

}

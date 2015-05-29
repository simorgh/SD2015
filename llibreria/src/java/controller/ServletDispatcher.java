package controller;

import model.User;
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
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author simorgh
 */
public class ServletDispatcher extends HttpServlet {
    private DataManager data;
    
    @Override
    public void init() throws ServletException {
	super.init();
        loadState();
    }
    
    @Override
    public void destroy() {
        saveState();
    }
    

    ////////////////////////////////////////////////////////
    //                     PERSISTENCE
    ////////////////////////////////////////////////////////
   
    private void saveState(){
        System.out.println("@saveState()"); 
        try {  
            this.data.saveUsers( getServletContext().getRealPath("/WEB-INF/users.json") );
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ServletDispatcher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
   
    private void loadState() {
        System.out.println("@loadState()");
        
        ServletContext c = getServletContext();
        String users = c.getRealPath("/WEB-INF/users.json");
	String products = c.getRealPath("/WEB-INF/products.json");
	this.data = DataManager.getInstance(users, products);
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
            System.out.println("entering testLogout_00...");
	    //boolean logout = Boolean.getBoolean(request.getParameter("logoff"));
            //request.getSession().invalidate();
            //showPage(request, response, "/index.jsp");
	} else if(location.equals(CONTEXT + "/cataleg")) {
            showCataleg(request, response);
        } else if (location.equals(CONTEXT + "/protegit/llista")) {

	    showPurchases(request, response);
        } else if (location.equals(CONTEXT + "/afegir")) {    
            /*
             * User recovery */
            String name = request.getRemoteUser();
            User u;
            if(data.getUsers().containsKey(name)) u = data.getUsers().get(name);
            else {
                u = new User(name, 500.0f);
                data.addUser(u); // user needs to be added for persistence purposes
            }
            
            /*
             * Add product to cart */
            String pid = request.getParameter("item");
            Product p = data.getProducts().get(pid);
            if(!u.getCart().contains(p) && !u.getProducts().contains(p)) {
                System.out.println(u.getName() + " adding item " + p.getName() + " to cart...");
                u.addToCart(p);
            }
            
            System.out.println("CART ITEMS: " + u.getCart().size());
            request.setAttribute("cart", u.getCart().size());
            showCataleg(request, response);
        }else if (location.contains(CONTEXT + "/protegit/comprar")) {    
            /*
             * User recovery */
            buyResource(request,response);
            showPurchases(request, response);
        } else if (location.contains("/download")) {
            downloadResource(request, response);
            
        } else if (location.contains("logout")){
            //System.out.println("entering testLogout_01..");
            //boolean logout = Boolean.getBoolean(request.getParameter("logoff"));
            request.getSession().invalidate();
            showPage(request, response, "/index.jsp");
        } else {
	    showPage(request, response, "/error404.jsp");
	}
    }


    ////////////////////////////////////////////////////////////////
    //                          PAGES
    ////////////////////////////////////////////////////////////////
    
    /**
     * 
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException 
     */
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
    
    /**
     * 
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException 
     */
    private void showPurchases(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HashMap <String, User> users = data.getUsers();
        if(users.containsKey(request.getRemoteUser())){
            request.setAttribute("purchased", users.get(request.getRemoteUser()).getProducts());
            request.setAttribute("cart", users.get(request.getRemoteUser()).getCart());
        }
        showPage(request, response, "/WEB-INF/jsp/protected/llista.jsp");
    }

    /**
     * 
     * @param request
     * @param response
     * @param jspPage
     * @throws ServletException
     * @throws IOException 
     */
    public void showPage(HttpServletRequest request, HttpServletResponse response, String jspPage) throws ServletException, IOException{
        ServletContext sc = getServletContext();
        RequestDispatcher rd = sc.getRequestDispatcher(jspPage);
        rd.forward(request, response);
    }
 
      /**
   * 
   * @param request
   * @param response
   * @throws IOException 
   */
    private void buyResource(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String name = request.getRemoteUser();
        User u;
        if(data.getUsers().containsKey(name)) u = data.getUsers().get(name);
            else {
                u = new User(name, 500.0f);
                data.addUser(u); // user needs to be added for persistence purposes
            }
        String pid = request.getParameter("param");
        request.removeAttribute("param");
        //String price = request.getParameter("param");  
        
        Product p = data.getProducts().get(pid);
        float price = p.getPrice();
        if(!(price > u.getCredits())){
            u.addToPurchased(p);
            u.removeFromCart(p);
            u.setCredits(u.getCredits() - price);
            System.out.println("Item "+ p.getName() +" ha sido comprado;" + " Dispones de "+ u.getCredits()+" creditos");
        }
        else  System.out.println("No hay saldo suficiente para comprar Item "+ p.getName());
    } 
  /**
   * 
   * @param request
   * @param response
   * @throws IOException 
   */
    private void downloadResource(HttpServletRequest request, HttpServletResponse response) throws IOException {
        
        String link = request.getParameter("param");
        String apath = this.getServletContext().getRealPath("/WEB-INF/");
        File file = new File(apath + link);
        
        ServletOutputStream outStream = response.getOutputStream();
        response.setContentLength((int) file.length());
        response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");

        byte[] byteBuffer = new byte[1024];
        DataInputStream in = new DataInputStream(new FileInputStream(file));
        int length;
        while ((length = in.read(byteBuffer)) != -1) {
            outStream.write(byteBuffer, 0, length);
        }
        in.close();
        outStream.close();
    }   
  
}
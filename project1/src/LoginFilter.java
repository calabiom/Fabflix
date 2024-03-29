import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet Filter implementation class: LoginFilter.
 * All URL patterns will go through the LoginFilter
 */
//@WebFilter(filterName = "LoginFilter", urlPatterns = "/*")
public class LoginFilter implements Filter {

    /**
     * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        System.out.println("LoginFilter: " + httpRequest.getRequestURI());

        // Check if the URL is allowed to be accessed without log in
        if (this.isUrlAllowedWithoutLogin(httpRequest.getRequestURI())) {
            // Keep default action: pass along the filter chain
            System.out.println("LoginFilter: CHECK IF url is allowed to be accessed without login");

            chain.doFilter(request, response);
            return;
        }

        // check if URL is specifically "dashboard", then check if "employee" attribute exist in session
        if (httpRequest.getRequestURI().toLowerCase().endsWith("dashboard.html")) { // httpRequest.getRequestURI().toLowerCase().endsWith("employee-login.html")
        	 if (httpRequest.getSession().getAttribute("employee") == null) {
                 System.out.println("LoginFilter: employee doesn't exist in session");
                 httpResponse.sendRedirect("employee-login.html");
        	 } 
        	 else {
                System.out.println("LoginFilter: employee exists in session, redirect to corresponding url");
             	chain.doFilter(request, response);
        	 }
        	 
        } else {
        
	        // Redirect to login page if the "user" attribute doesn't exist in session
	        if (httpRequest.getSession().getAttribute("user") == null) {
	            System.out.println("LoginFilter: user doesn't exist in session... null");
	            String projectRootPath = httpRequest.getContextPath();
	            httpResponse.sendRedirect(projectRootPath + "/login.html");
	        } else {
	            // If the user exists in current session, redirects the user to the corresponding URL
	            System.out.println("LoginFilter: user exists in session, redirect to corresponding url... chain");
	            
	        	chain.doFilter(request, response);
	        }
        }
    }

    // Setup your own rules here to allow accessing some resources without logged in
    // Always allow your own login related requests (html, js, servlet, etc..)
    // You might also want to allow some CSS files, etc..
    private boolean isUrlAllowedWithoutLogin(String requestURI) {
        requestURI = requestURI.toLowerCase();

        return requestURI.endsWith("login.html") || requestURI.endsWith("login.js")
                || requestURI.endsWith("api/login") || requestURI.endsWith("login.css") 
                || requestURI.endsWith("employee-login.html") || requestURI.endsWith("api/employee-login")
                || requestURI.endsWith("employee-login.js");
    }

    /**
     * This class implements the interface: Filter. In Java, a class that implements an interface
     * must implemented all the methods declared in the interface. Therefore, we include the methods
    * below.
     * @see Filter#init(FilterConfig)
     */
    public void init(FilterConfig fConfig) {
    }

    public void destroy() {
    }
}

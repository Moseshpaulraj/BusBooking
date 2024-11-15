package com.busbooking;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebFilter("/*")
public class BookingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            HttpSession session = httpRequest.getSession(false);
            String path = httpRequest.getRequestURI();
            System.out.println(path);   

            if (session == null || session.getAttribute("user") == null) {
                if (!path.endsWith("login.jsp") && !path.endsWith("signup.jsp") && !path.endsWith("login")&& !path.endsWith("signup")) {
                    httpResponse.sendRedirect(httpRequest.getContextPath() + "/publicpages/login.jsp");
                    return;
                }
            } else {
                User currentUser = (User) session.getAttribute("user");
                UserContext.setUser(currentUser);
                if (path.contains("/admin/") && currentUser.getTypeId() != Constants.ADMIN) {
                    httpResponse.sendRedirect(httpRequest.getContextPath() + "/common/unauthorized.jsp");
                    return;
                } else if (path.contains("/customer/") && currentUser.getTypeId() != Constants.CUSTOMER) {
                    httpResponse.sendRedirect(httpRequest.getContextPath() + "/common/unauthorized.jsp");
                    return;
                }
            }
            System.out.println("chain dofilter called ");
            chain.doFilter(request, response);
        } finally {
            UserContext.clear();
        }
    }
}

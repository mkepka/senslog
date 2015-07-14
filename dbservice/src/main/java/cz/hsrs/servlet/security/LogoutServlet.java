package cz.hsrs.servlet.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LogoutServlet extends HttpServlet{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            ((LoginUser)req.getSession().getAttribute(JSPHelper.USERATTRIBUTE)).logOut(req);
            req.getSession().setAttribute(JSPHelper.USERATTRIBUTE, null);
            req.getSession().setAttribute(JSPHelper.FEEDBACKATTRIBUTE, null);
        } catch (NullPointerException e) {
            throw new ServletException("You are not logged in!");
        }

        JSPHelper.redirect(resp, req.getContextPath()+"/signin.jsp");
    }
}

package cz.hsrs.servlet.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cz.hsrs.servlet.provider.DBServlet;

public class ControllerServlet extends DBServlet {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    //private String feedback = "";
    private HttpSession session = null;

    protected void procesRequest(HttpServletRequest req,
            HttpServletResponse resp) throws ServletException, IOException {
        req.getSession().invalidate();        
        
        session = req.getSession(true);
        session.setMaxInactiveInterval(60*10);

        // if (session.getAttribute(JSPHelper.USERATTRIBUTE)==null) {
        /** Session je prazdna... Uzivatel se musi nalogovat */
        LoginUser user = new LoginUser(req);

        if (user.athenticate(req.getParameter("username"), req
                .getParameter("password"))) {
            /** uspesny login - dej uzivatele do session a presmeruj */
            session.setAttribute(JSPHelper.USERATTRIBUTE, user);    
            //session.setAttribute(JSPHelper.LANGATTRIBUTE, user.getUserLanguage());
            
            Cookie sescookie = new Cookie("sessionid",req.getSession().getId());
            Cookie langcookie = new Cookie("language",user.getUserLanguage());
            Cookie audiocookie = new Cookie("audio",String.valueOf(user.isAudio()));
            sescookie.setPath("/");
            langcookie.setPath("/");
            audiocookie.setPath("/");
            resp.addCookie(sescookie);
            resp.addCookie(langcookie);
            resp.addCookie(audiocookie);
            
            String coming = req.getParameter("coming");
            if (coming.equalsIgnoreCase("null") == false){
                if(coming.equalsIgnoreCase("/insert.jsp")==true){
                    JSPHelper.redirect(resp, req.getContextPath() + "/insert.jsp?unit_id");
                }
                else if(coming.equalsIgnoreCase("/vypis.jsp")==true){
                    JSPHelper.redirect(resp, req.getContextPath() + "/index.jsp");
                }
                else{
                    JSPHelper.redirect(resp, req.getContextPath() + coming);
                }
            }
            else{
                JSPHelper.redirect(resp, req.getContextPath() + "/crossroad.jsp");
            }
            
            // RequestDispatcher rd =
            // req.getRequestDispatcher("/crossroad.jsp");
            // rd.forward(req, resp);

        } else {
            /** spatny login - presmeruj na stranku pro nalogovani */

            session.setAttribute(JSPHelper.FEEDBACKATTRIBUTE,
                    "Wrong login or user name");
            JSPHelper.redirect(resp, req.getContextPath() + "/signin.jsp");
            // RequestDispatcher rd = req
            // .getRequestDispatcher("/signin.jsp");
            // rd.forward(req, resp);
            // }
            /*
             * } else {
             * 
             * if (((LoginUser)session.getAttribute(JSPHelper.USERATTRIBUTE))
             * .isAuthenticated()) { RequestDispatcher rd =
             * req.getRequestDispatcher("/map.html"); rd.forward(req, resp); }
             * else { RequestDispatcher rd =
             * req.getRequestDispatcher("/SigninPage.jsp"); rd.forward(req,
             * resp); }
             */
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        procesRequest(req, resp);
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        procesRequest(req, resp);
    }
}
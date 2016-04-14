package cz.hsrs.servlet.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cz.hsrs.servlet.provider.DBServlet;

/**
 * Controller servlet for logging in the application,
 * solves if login request is coming from GUI or over REST API
 * @author jezekjan
 *
 */
public class ControllerServlet extends DBServlet {

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
        
        /** tests if request came from GUI or from light REST client*/
        String coming = req.getParameter("coming");

        if (user.athenticate(req.getParameter("username"), req.getParameter("password"))) {
            /** uspesny login - dej uzivatele do session a presmeruj */
            session.setAttribute(JSPHelper.USERATTRIBUTE, user);
            
            Cookie sescookie = new Cookie("sessionid",req.getSession().getId());
            Cookie langcookie = new Cookie("language",user.getUserLanguage());
            Cookie audiocookie = new Cookie("audio",String.valueOf(user.isAudio()));
            sescookie.setPath("/");
            langcookie.setPath("/");
            audiocookie.setPath("/");
            resp.addCookie(sescookie);
            resp.addCookie(langcookie);
            resp.addCookie(audiocookie);
            
            if(coming != null){
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
            }
            /** request doesn't contain coming parameter - came from REST client*/
            else{
                resp.setStatus(200);
                resp.setHeader("Access-Control-Allow-Origin", "*");
                resp.getWriter().println("{\"sessionid\":\""+req.getSession().getId()+"\", "
                        + "\"language\":\""+user.getUserLanguage()+"\", \"audio\":\""+String.valueOf(user.isAudio())+"\"}");
            }
        } else {
            /** Login prichazi z webove stranky, vrat webovou stranku */
            if(coming != null){
                /** spatny login - presmeruj na stranku pro nalogovani */
                session.setAttribute(JSPHelper.FEEDBACKATTRIBUTE, "Wrong login or user name");
                JSPHelper.redirect(resp, req.getContextPath() + "/signin.jsp");
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
                /** Login prichazi z REST klienta, vrat jen zpravu*/
            } else{
                resp.setStatus(401);
                resp.setHeader("Access-Control-Allow-Origin", "*");
                resp.setHeader("Content-Type", "text/plain; charset=utf-8");
                resp.getWriter().println("Wrong username or password!");
            }
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
package cz.hsrs.servlet.security;

import java.sql.SQLException;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import cz.hsrs.db.util.UserUtil;

public class SessionListener implements HttpSessionListener {

    public  synchronized void sessionDestroyed(HttpSessionEvent event) {
        synchronized (this) {
            try {
                UserUtil util = new UserUtil();
                util.delUserSession(event.getSession().getId());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void sessionCreated(HttpSessionEvent arg0) {
        // TODO Auto-generated method stub
    }
}
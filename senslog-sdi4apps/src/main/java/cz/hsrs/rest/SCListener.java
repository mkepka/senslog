package cz.hsrs.rest;

import java.io.FileInputStream;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import cz.hsrs.db.pool.SQLExecutor;

/**
 * 
 * @author mkepka
 *
 */
public class SCListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String propFile = sce.getServletContext().getRealPath("WEB-INF/database.properties");
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(propFile));
        } catch (Exception e) {
            e.printStackTrace();
        } 
        SQLExecutor.setProperties(prop);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        SQLExecutor.close();
    }
}
package cz.hsrs.main;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.webapp.WebAppContext;

import cz.hsrs.db.pool.SQLExecutor;

public class Start {

    public static Server server = new Server();

    public static void start() throws Exception {
        try {

            SocketConnector connector = new SocketConnector();
            connector.setPort(8080);

            server.setConnectors(new Connector[] { connector });
            WebAppContext context = new WebAppContext();
            context.setServer(server);
            context.setContextPath("/DBService");
            //context.setContextPath("/");
            context.setWar("src/main/webapp");
            server.addHandler(context);
            SQLExecutor.setConfigfile("local_logging.properties");
            
            server.start();
            
        } catch (Exception e) {
            if (server != null) {
                try {
                    server.stop();
                } catch (Exception e1) {
                    throw new RuntimeException(e1);
                }
            }
        }
    }
    public static void stop() throws Exception {
        server.stop();
    }
    public static void main(String[] args) throws Exception {
        start();
    }
}
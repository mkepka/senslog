package cz.hsrs.servlet.lang;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;

public class Labels {

    public static String map = "MAP";
    public static String administration = "ADMINISTRATION";
    public static String logbook = "LOGBOOK";
    public static String mapW = "MAPWORLD";
    public static String adress = "ADRESS";
    public static String loginas = "LOGINAS";
    public static String contact = "CONTACT";
    public static String intro = "INTRODUCTION";
    public static String logout = "LOGOUT";

    final Map<String, String> cz;
    final Map<String, String> en;

    static Labels l;

    private Labels() {
        cz = new HashMap<String, String>();
        en = new HashMap<String, String>();
        cz.put(map, "Mapa");
        en.put(map, "Map");
        cz.put(administration, "Administrace");
        en.put(administration, "Administration");
        cz.put(logbook, "Kniha jízd");
        en.put(logbook, "Traveling Book");
        cz.put(mapW, "Mapa - Evropa");
        en.put(mapW, "Map - Europe");
        cz.put(adress, "Adresa");
        en.put(adress, "Adress");
        cz.put(loginas, "Přihlášen jako:");
        en.put(loginas, "Logged in as:");
        cz.put(contact, "Kontakt");
        en.put(contact, "Contact");
        cz.put(intro, "Úvod");
        en.put(intro, "Intro");
        cz.put(logout, "Odhlásit");
        en.put(logout, "Logout");


    }

    public static Labels getLabels() {
        if (l == null) {
            l = new Labels();
        }
        return l;
    }

    public Map<String, String> get(String l) {
        if (l.equals("cz")) {
            return cz;
        } else if (l.equals("en")) {
            return en;
        }
        return en;
    }

    public Map<String, String> get(Cookie[] c) {
        if(c != null){
            for(int i = 0; i<c.length;i++){
                if(c[i].getName().equalsIgnoreCase("language")==true){
                    return get(c[i].getValue());
                }
            }
        }
        return en;
    }
}
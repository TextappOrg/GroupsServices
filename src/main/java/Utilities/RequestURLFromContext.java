package Utilities;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

public class RequestURLFromContext {
    public static String getUrl(){
        try {
            InitialContext context = new InitialContext();
            Properties properties = (Properties) context.lookup("BaseURL");
            return String.valueOf(properties.get("URL"));
        } catch (NamingException e) {
            e.printStackTrace(); // TODO : debug
            return "";
        }
    }
}

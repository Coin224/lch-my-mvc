import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AtmController {

   public String login(HttpServletRequest request ,HttpServletResponse response) {


       return "welcome.jsp";
   }

    public String update(HttpServletRequest request ,HttpServletResponse response) {


        return "update.jsp";
    }

}

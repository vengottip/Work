import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/example")
public class ExampleServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        if (session != null) {
            String sessionId = session.getId();

            if (SessionManager.isSessionActive(sessionId)) {
                // Your regular request handling code goes here
                // ...

            } else {
                // Session is invalidated; terminate this request
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Session invalid. Please log in again.");
                return;
            }
        } else {
            // Session not found; terminate this request
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("No active session found. Please log in.");
            return;
        }
    }
}

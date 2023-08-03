import java.util.HashMap;
import java.util.Map;

public class SessionManager {
    private static final Map<String, Boolean> activeSessions = new HashMap<>();

    public static synchronized boolean isSessionActive(String sessionId) {
        return activeSessions.containsKey(sessionId) && activeSessions.get(sessionId);
    }

    public static synchronized void invalidateSession(String sessionId) {
        activeSessions.put(sessionId, false);
    }

    public static synchronized void createSession(String sessionId) {
        activeSessions.put(sessionId, true);
    }
}

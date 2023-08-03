// Invalidate the session when you want to log the user out
// Example: After calling session.invalidate() in your logout servlet or method
String sessionIdToInvalidate = "session_id_to_invalidate";
SessionManager.invalidateSession(sessionIdToInvalidate);

// Create a session when a user logs in or a new session is created
String newSessionId = "newly_created_session_id";
SessionManager.createSession(newSessionId);
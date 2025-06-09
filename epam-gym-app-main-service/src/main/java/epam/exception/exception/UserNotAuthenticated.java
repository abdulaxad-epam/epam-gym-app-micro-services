package epam.exception.exception;

public class UserNotAuthenticated extends RuntimeException {
    public UserNotAuthenticated(String unauthorizedAccess) {
        super(unauthorizedAccess);
    }
}

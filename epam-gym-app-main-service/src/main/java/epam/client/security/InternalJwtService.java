package epam.client.security;

public interface InternalJwtService {
    String generateServiceToken(String audienceId, String scope);
}

package epam.util;


import epam.entity.User;
import epam.exception.exception.UsernameGenerateException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PrePersist;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Field;

public class UsernameGeneratorListener {

    private static final Log log = LogFactory.getLog(UsernameGeneratorListener.class);

    @PrePersist
    public void generateUsername(Object entity) {
        Field[] fields = entity.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(GenerateUsername.class)) {
                field.setAccessible(true);
                try {
                    if (field.get(entity) == null || field.get(entity).toString().isEmpty()) {
                        User user = (User) entity;
                        String baseUsername = user.getFirstname().toLowerCase() + "." + user.getLastname().toLowerCase();
                        long nextIndex = findNextAvailableIndex(baseUsername);
                        String newUsername = nextIndex == 0 ? baseUsername : baseUsername + nextIndex;

                        field.set(user, newUsername);
                        log.info("Generated Username: " + newUsername);
                    }
                } catch (IllegalAccessException e) {
                    throw new UsernameGenerateException("Could not set generated username", e);
                }
            }
        }
    }

    private long findNextAvailableIndex(String baseUsername) {
        try (EntityManager entityManager = ApplicationContextProvider.getBean(EntityManagerFactory.class).createEntityManager()) {
            log.info("Finding next available index...");
            Long serials = entityManager.createQuery(
                            "SELECT COUNT(*) FROM User u WHERE u.username LIKE :username", Long.class)
                    .setParameter("username", baseUsername + "%")
                    .getSingleResult();
            log.info("Next available index found: " + serials);
            return serials;
        } catch (Exception e) {
            log.error("Error fetching existing usernames", e);
            return 0;
        }
    }

}
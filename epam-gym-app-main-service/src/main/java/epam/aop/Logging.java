package epam.aop;

import org.jboss.logging.Logger;

import java.util.UUID;

public class Logging {
    private final Logger logger;
    private static final ThreadLocal<String> transactionId = ThreadLocal.withInitial(() -> UUID.randomUUID().toString().substring(4, 16));

    public Logging(Class<?> clazz) {
        this.logger = Logger.getLogger(clazz);
    }

    public static String getTransactionId() {
        return transactionId.get();
    }

    public void info(String message, Object... args) {
        logger.infov("TransactionID: {0} - " + message, mergeArgs(getTransactionId(), args));
    }

    public void error(String message, Object... args) {
        logger.errorv("TransactionID: {0} - " + message, mergeArgs(getTransactionId(), args));
    }

    public void debug(String message, Object... args) {
        logger.debugv("TransactionID: {0} - " + message, mergeArgs(getTransactionId(), args));
    }

    public void warn(String message, Object... args) {
        logger.warnv("TransactionID: {0} - " + message, mergeArgs(getTransactionId(), args));
    }

    public void resetTransactionId() {
        transactionId.set(UUID.randomUUID().toString());
    }

    private Object[] mergeArgs(String transactionId, Object... args) {
        Object[] newArgs = new Object[args.length + 1];
        newArgs[0] = transactionId;
        System.arraycopy(args, 0, newArgs, 1, args.length);
        return newArgs;
    }
}

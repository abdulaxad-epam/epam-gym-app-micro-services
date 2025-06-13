package epam.aop;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private final Logging logger = new Logging(LoggingAspect.class);

    @Before("execution(* epam.controller.*.*(..))")
    public void logRequest(JoinPoint joinPoint) {

        logger.info("Controller Method: {0}.{1}(), Args: {2}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                joinPoint.getArgs());
    }


    @Before("execution(* epam.service.impl.*.*(..))&& " +
            "!within(epam.service.impl.TrainerWorkloadCleaner)")
    public void logServiceMethod(JoinPoint joinPoint) {
        logger.info("Service Method: {0}.{1}(), Args: {2}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                joinPoint.getArgs());
    }


    @Before("execution(* epam.repostiory.*.*(..))")
    public void logRepositoryMethod(JoinPoint joinPoint) {
        logger.info("Repository Method: {0}.{1}(), Args: {2}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                joinPoint.getArgs());
    }


    @AfterReturning(value = "execution(* epam.controller.*.*(..))", returning = "result")
    public void logAfterSuccess(JoinPoint joinPoint, Object result) {
        logger.info("Executed REST: {0}.{1}(), Response: {2}, Status: 200",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                result);
    }

    @AfterThrowing(value = "execution(* epam..*.*(..))", throwing = "exception")
    @Pointcut("!@target(epam.security.*.*)")
    public void logExceptions(JoinPoint joinPoint, Throwable exception) {
        logger.error("Exception in Method: {0}.{1}(), Message: {2}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                exception.getMessage());
    }

}

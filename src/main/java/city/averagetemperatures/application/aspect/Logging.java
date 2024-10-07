package city.averagetemperatures.application.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class Logging {

    private static final Logger log = LoggerFactory.getLogger(Logging.class);

    @Pointcut("execution(* city.averagetemperatures.application.service.TemperatureService.findAverageTemperaturesByCity(*))")
    private void findingDataMethodFromService() {
    }

    @Pointcut("execution(* city.averagetemperatures.application.dao.DataReaderDao.externalDataSourceAvailable(..))")
    private void externalDataAvailabilityCheckInDao() {
    }

    //After failed reading operation
    @AfterThrowing("execution(* city.averagetemperatures.application.dao.DataReaderDao.readCityData(..))")
    public void logReaderExceptionInDao(JoinPoint joinPoint) {
        this.logDataReadingError(joinPoint);
    }

    @AfterThrowing(pointcut = "findingDataMethodFromService()")
    public void logFileExceptionAtFindingDataInService(JoinPoint joinPoint) {
        this.logDataReadingError(joinPoint);
    }

    @AfterReturning(value = "externalDataAvailabilityCheckInDao()",  returning = "result")
    public void logAfter(JoinPoint joinPoint, Object result) {
        if (result instanceof Boolean isExisting && !isExisting) {
            if (log.isErrorEnabled()) {
                log.error("{}: Data access error in {} (Aspect)", Thread.currentThread().getName(),
                        joinPoint.getSourceLocation().getWithinType().getName());
            }
        }
    }

    private void logDataReadingError(JoinPoint joinPoint) {
        String typeName = joinPoint.getSourceLocation().getWithinType().getName();
        String methodName = joinPoint.getSignature().getName();
        if (log.isErrorEnabled()) {
            log.error("{}: Data reading error in {}.{} (Aspect)", Thread.currentThread().getName(),
                    typeName, methodName);
        }
    }
}

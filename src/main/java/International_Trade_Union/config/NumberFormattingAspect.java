package International_Trade_Union.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Aspect
@Component
public class NumberFormattingAspect {

    @Around("execution(* International_Trade_Union.controllers..*.*(..))")
    public Object formatNumbers(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        return formatObject(result);
    }

    private Object formatObject(Object obj) {
        if (obj instanceof BigDecimal) {
            return ((BigDecimal) obj).setScale(10, RoundingMode.HALF_UP);
        } else if (obj instanceof Double) {
            return BigDecimal.valueOf((Double) obj).setScale(10, RoundingMode.HALF_UP).doubleValue();
        } else if (obj instanceof Iterable) {
            ((Iterable<?>) obj).forEach(this::formatObject);
        } else if (obj != null && obj.getClass().isArray()) {
            for (int i = 0; i < java.lang.reflect.Array.getLength(obj); i++) {
                java.lang.reflect.Array.set(obj, i, formatObject(java.lang.reflect.Array.get(obj, i)));
            }
        }
        return obj;
    }
}
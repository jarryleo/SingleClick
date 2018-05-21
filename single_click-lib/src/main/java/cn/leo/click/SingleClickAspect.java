package cn.leo.click;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

/**
 * Created by Leo on 2018/5/2.
 */
@Aspect
public class SingleClickAspect {
    private static long mLastClickTime;
    private static Object mLastClickObject;

    private static final String POINTCUT_METHOD =
            "execution(* onClick(..))";
    private static final String POINTCUT_ANNOTATION =
            "execution(@cn.leo.click.SingleClick * *(..))";
    private static final String POINTCUT_BUTTER_KNIFE =
            "execution(@butterknife.OnClick * *(..))";

    @Pointcut(POINTCUT_METHOD)
    public void methodPointcut() {

    }

    @Pointcut(POINTCUT_ANNOTATION)
    public void annotationPointcut() {

    }

    @Pointcut(POINTCUT_BUTTER_KNIFE)
    public void butterKnifePointcut() {

    }

    @Around("methodPointcut() || annotationPointcut() || butterKnifePointcut()")
    public void aroundJoinPoint(final ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        //点击的不同对象不计算点击间隔
        Object[] args = joinPoint.getArgs();
        if (args.length >= 1) {
            if (mLastClickObject != args[0]) {
                joinPoint.proceed();
                mLastClickObject = args[0];
                mLastClickTime = System.currentTimeMillis();
                return;
            }
        }
        //计算点击间隔，没有注解默认500，有注解按注解参数来，注解参数为空默认500；
        int interval = 500;
        boolean hasAnnotation = method.isAnnotationPresent(SingleClick.class);
        if (hasAnnotation) {
            SingleClick annotation = method.getAnnotation(SingleClick.class);
            interval = annotation.value();
        }
        if (canClick(interval)) joinPoint.proceed();
    }

    private boolean canClick(int interval) {
        long l = System.currentTimeMillis() - mLastClickTime;
        if (l > interval) {
            mLastClickTime = System.currentTimeMillis();
            return true;
        }
        return false;
    }
}

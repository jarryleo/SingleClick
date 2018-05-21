package cn.leo.click;

import android.view.View;

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
        boolean hasAnnotation = method.isAnnotationPresent(SingleClick.class);
        //点击的不同对象不计算点击间隔
        Object[] args = joinPoint.getArgs();
        if (args.length >= 1) {
            Object arg = args[0];
            if (mLastClickObject != arg) {
                joinPoint.proceed();
                mLastClickObject = arg;
                mLastClickTime = System.currentTimeMillis();
                return;
            }
            //注解排除某个控件不防止点击
            if (arg instanceof View && hasAnnotation) {
                SingleClick annotation = method.getAnnotation(SingleClick.class);
                int id = ((View) arg).getId();
                int[] except = annotation.except();
                for (int i : except) {
                    if (i == id) {
                        joinPoint.proceed();
                        return;
                    }
                }
            }
        }
        //计算点击间隔，没有注解默认500，有注解按注解参数来，注解参数为空默认500；
        int interval = 500;
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

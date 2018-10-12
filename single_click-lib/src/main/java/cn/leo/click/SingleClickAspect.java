package cn.leo.click;

import android.content.res.Resources;
import android.os.Looper;
import android.os.MessageQueue;
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
    private static int mLastClickId;
    private boolean mThreadIdle = true;

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
        boolean hasAnnotation = method != null && method.isAnnotationPresent(SingleClick.class);
        //点击的不同对象不计算点击间隔
        Object[] args = joinPoint.getArgs();
        if (args.length >= 1 && args[0] instanceof View) {
            View view = (View) args[0];
            int id = view.getId();
            if (mLastClickId != id) {
                //如果线程忙碌则不点击,防止不同按钮打开不同的页面
                if (!mThreadIdle) {
                    return;
                }
                checkThreadIdle();
                joinPoint.proceed();
                mLastClickId = id;
                mLastClickTime = System.currentTimeMillis();
                return;
            }
            //注解排除某个控件不防止双击
            if (hasAnnotation) {
                SingleClick annotation = method.getAnnotation(SingleClick.class);
                //按id排除点击
                int[] except = annotation.except();
                for (int i : except) {
                    if (i == id) {
                        joinPoint.proceed();
                        return;
                    }
                }
                //按id名排除点击
                String[] idName = annotation.exceptIdName();
                Resources resources = view.getResources();
                for (String name : idName) {
                    int resId = resources.getIdentifier(name, "id", view.getContext().getPackageName());
                    if (resId == id) {
                        joinPoint.proceed();
                        return;
                    }
                }
            }
        }
        //计算点击间隔，没有注解默认500，有注解按注解参数来，注解参数为空默认500；
        int interval = SingleClickManager.clickInterval;
        if (hasAnnotation) {
            SingleClick annotation = method.getAnnotation(SingleClick.class);
            interval = annotation.value();
        }
        if (canClick(interval)) {
            checkThreadIdle();
            joinPoint.proceed();
        }
    }

    public boolean canClick(int interval) {
        long l = System.currentTimeMillis() - mLastClickTime;
        if (l > interval) {
            mLastClickTime = System.currentTimeMillis();
            return true;
        }
        return false;
    }

    public void checkThreadIdle() {
        if (SingleClickManager.isCheckThreadIdle) {
            mThreadIdle = false;
            Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() {
                @Override
                public boolean queueIdle() {
                    mThreadIdle = true;
                    return false;
                }
            });
        } else {
            mThreadIdle = true;
        }
    }
}

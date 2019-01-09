package cn.leo.click;

import android.content.res.Resources;
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

    private static final String POINTCUT_METHOD =
            "execution(* on*Click(..))";
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
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            //检查方法是否有注解
            boolean hasAnnotation = method != null && method.isAnnotationPresent(SingleClick.class);
            //计算点击间隔，没有注解默认500，有注解按注解参数来，注解参数为空默认500；
            int interval = SingleClickManager.clickInterval;
            if (hasAnnotation) {
                SingleClick annotation = method.getAnnotation(SingleClick.class);
                interval = annotation.value();
            }
            //获取被点击的view对象
            Object[] args = joinPoint.getArgs();
            View view = findViewInMethodArgs(args);
            if (view != null) {
                int id = view.getId();
                //注解排除某个控件不防止双击
                if (hasAnnotation) {
                    SingleClick annotation = method.getAnnotation(SingleClick.class);
                    //按id值排除不防止双击的按钮点击
                    int[] except = annotation.except();
                    for (int i : except) {
                        if (i == id) {
                            mLastClickId = id;
                            mLastClickTime = System.currentTimeMillis();
                            joinPoint.proceed();
                            return;
                        }
                    }
                    //按id名排除不防止双击的按钮点击（非app模块）
                    String[] idName = annotation.exceptIdName();
                    Resources resources = view.getResources();
                    for (String name : idName) {
                        int resId = resources.getIdentifier(name, "id", view.getContext().getPackageName());
                        if (resId == id) {
                            mLastClickId = id;
                            mLastClickTime = System.currentTimeMillis();
                            joinPoint.proceed();
                            return;
                        }
                    }
                }
                if (canClick(interval)) {
                    mLastClickId = id;
                    mLastClickTime = System.currentTimeMillis();
                    joinPoint.proceed();
                    return;
                }
            }

            //检测间隔时间是否达到预设时间并且线程空闲
            if (canClick(interval)) {
                mLastClickTime = System.currentTimeMillis();
                joinPoint.proceed();
            }
        } catch (Exception e) {
            //出现异常不拦截点击事件
            joinPoint.proceed();
        }
    }

    public View findViewInMethodArgs(Object[] args) {
        if (args == null || args.length == 0) {
            return null;
        }
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof View) {
                View view = (View) args[i];
                if (view.getId() != View.NO_ID) {
                    return view;
                }
            }
        }
        return null;
    }

    public boolean canClick(int interval) {
        long l = System.currentTimeMillis() - mLastClickTime;
        if (l > interval) {
            mLastClickTime = System.currentTimeMillis();
            return true;
        }
        return false;
    }
}

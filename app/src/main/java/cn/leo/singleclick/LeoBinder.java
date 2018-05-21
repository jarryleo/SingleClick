package cn.leo.singleclick;

import android.app.Activity;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by Leo on 2017/12/8.
 * 仿butterKnife 注解控件和点击事件，可以防止双击，和本库无关，作为学习保存
 */

public class LeoBinder {
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Bind {
        int value();
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface OnClick {
        int[] value() default {-1};
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface PreventDoubleClick {
        long value() default 200;
    }

    public static void bind(Object object) {
        bindView(object);
        bindClick(object);
    }

    private static void bindView(Object object) {
        Class<?> aClass = object.getClass();
        for (Field field : aClass.getDeclaredFields()) {
            Bind bind = field.getAnnotation(Bind.class);
            if (null == bind) continue;
            int value = bind.value();
            View viewById = getView(object, value);
            try {
                field.setAccessible(true);
                field.set(object, viewById);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private static void bindClick(Object object) {
        Class<?> aClass = object.getClass();
        for (Method method : aClass.getDeclaredMethods()) {
            OnClick click = method.getAnnotation(OnClick.class);
            PreventDoubleClick present = method.getAnnotation(PreventDoubleClick.class);
            if (null == click) continue;
            int[] value = click.value();
            for (int viewId : value) {
                if (viewId == -1) continue;
                View view = getView(object, viewId);
                try {
                    method.setAccessible(true);
                    addListener(view, object, method, present == null ? 0 : present.value());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Nullable
    private static View getView(Object object, int value) {
        View viewById = null;
        if (object instanceof Activity) {
            viewById = ((Activity) object).getWindow().findViewById(value);
        } else if (object instanceof View) {
            viewById = ((View) object).findViewById(value);
        } else if (object instanceof Fragment) {
            viewById = ((Fragment) object).getView().findViewById(value);
        }
        return viewById;
    }

    private static void addListener(final Object focusView,
                                    final Object client,
                                    final Method m,
                                    final long prevent) throws Exception {
        InvocationHandler handler = new InvocationHandler() {
            long clickTime = 0;

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Class<?>[] types = m.getParameterTypes();
                if (prevent > 0) {
                    if (System.currentTimeMillis() - clickTime < prevent) {
                        Log.e("doubleClick", "invoke: 阻止双击");
                        return null;
                    }
                    clickTime = System.currentTimeMillis();
                }
                if (types.length > 0)
                    return m.invoke(client, focusView);
                else
                    return m.invoke(client);

            }
        };

        Object onClickListener = Proxy.newProxyInstance(null,
                new Class[]{View.OnClickListener.class}, handler);
        Method setOnClickListenerMethod = focusView.getClass()
                .getMethod("setOnClickListener", View.OnClickListener.class);
        setOnClickListenerMethod.invoke(focusView, onClickListener);
    }
}

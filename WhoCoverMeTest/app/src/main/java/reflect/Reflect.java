package reflect;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by makunle on 2017/9/19.
 */

public class Reflect {

    private Class<?> classToReflect;

    public static class ReMethod {
        Method method;
        Object instance;

        public ReMethod(Method method) {
            this.method = method;
            method.setAccessible(true);
        }

        public ReMethod with(Object instance) {
            this.instance = instance;
            return this;
        }

        public Object exec(Object... args) {
            try {
                return method.invoke(instance, args);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static class ReField {
        Field field;
        Object instance;

        public ReField(Field field) {
            this.field = field;
        }

        public ReField with(Object instance) {
            this.instance = instance;
            return this;
        }

        public Object get() {
            try {
                return field.get(instance);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public ReMethod getMethod(String method, Class<?>... param) {
        try {
            Method declaredMethod = classToReflect.getDeclaredMethod(method, param);
            ReMethod reMethod = new ReMethod(declaredMethod);
            return reMethod;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ReField getField(String field) {
        try {
            Field declaredField = classToReflect.getDeclaredField(field);
            ReField reField = new ReField(declaredField);
            return reField;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Reflect(String className) {
        try {
            this.classToReflect = Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
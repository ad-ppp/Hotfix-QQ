package com.jacky.hitfix.qq;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ShareReflectUtil {


    public static Field findField(Object instance, String name) throws NoSuchFieldException {
        Class<?> cls = instance.getClass();
        while (cls != null) {
            try {
                Field field = cls.getDeclaredField(name);
                //设置访问权限
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException e) {
                cls = cls.getSuperclass();
            }
        }
        throw new NoSuchFieldException("");
    }


    //java 重载函数


    public static Method findMethod(Object instance, String name, Class<?>... parameterTypes) throws NoSuchMethodException {
        Class<?> cls = instance.getClass();
        while (cls != null) {
            try {
                Method method = cls.getDeclaredMethod(name, parameterTypes);
                //设置访问权限
                method.setAccessible(true);
                return method;
            } catch (NoSuchMethodException e) {
                cls = cls.getSuperclass();
            }
        }
        throw new NoSuchMethodException("");
    }
}

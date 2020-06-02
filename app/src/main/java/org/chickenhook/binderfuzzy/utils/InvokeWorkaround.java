package org.chickenhook.binderfuzzy.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class InvokeWorkaround {
    public static Object invoke(Method m, Object receiver, Object[] args) throws InvocationTargetException, IllegalAccessException {
        return m.invoke(receiver, args);
    }
}

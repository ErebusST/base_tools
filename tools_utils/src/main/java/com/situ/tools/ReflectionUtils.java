/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.tools;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ArrayUtils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.*;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Optional;


/**
 * 反射工具类. <p> 提供访问私有变量,获取泛型类型Class, 提取集合中元素的属性, 转换字符串到对象等Util函数.
 */
@Slf4j
public class ReflectionUtils {
    /**
     * Gets root path.
     *
     * @param clazz the clazz
     * @return the root path
     * @author ErebusST
     * @since 2022 -01-07 15:36:08
     */
    public static String getRootPath(Class<?> clazz) {
        String packagePath = getPackagePath(clazz);
        if (!clazz.getPackage().getName().contains(".base.")) {
            packagePath = packagePath.substring(0, packagePath.lastIndexOf("/"));
            return packagePath.substring(0, packagePath.lastIndexOf("/"));
        } else {
            return packagePath.substring(0, packagePath.lastIndexOf("/"));
        }

    }

    /**
     * Gets package path.
     *
     * @param clazz the clazz
     * @return the package path
     * @author ErebusST
     * @since 2022 -01-07 15:36:08
     */
    public static String getPackagePath(Class<?> clazz) {
        String classPath = getResourcePath(clazz);
        return classPath + (ObjectUtils.isNull(clazz.getPackage()) ? "" : clazz.getPackage().getName().replace(".", "/"));
    }

    /**
     * Gets resource path.
     *
     * @param clazz the clazz
     * @return the resource path
     * @author ErebusST
     * @since 2022 -01-07 15:36:08
     */
    public static String getResourcePath(Class<?> clazz) {
        String path = clazz.getResource("/").getPath();
        try {
            return URLDecoder.decode(path, StaticValue.ENCODING);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 调用Getter方法.
     *
     * @param obj          the obj
     * @param propertyName the property name
     * @return the object
     * @author ErebusST
     * @since 2022 -01-07 15:36:08
     */
    public static Object invokeGetterMethod(Object obj, String propertyName) {
        String getterMethodName = "get" + StringUtils.capitalize(propertyName);
        return invokeMethod(obj, getterMethodName, new Class[]{}, new Object[]{});
    }

    /**
     * 调用Setter方法.使用value的Class来查找Setter方法.
     *
     * @param obj          the obj
     * @param propertyName the property name
     * @param value        the value
     * @author ErebusST
     * @since 2022 -01-07 15:36:08
     */
    public static void invokeSetterMethod(Object obj, String propertyName, Object value) {
        invokeSetterMethod(obj, propertyName, value, null);
    }

    /**
     * 获取get方法
     *
     * @param clazz        the clazz
     * @param propertyName the property name
     * @return getter method
     * @author ErebusST
     * @since 2022 -01-07 15:36:08
     */
    public static Method getGetterMethod(Class clazz, String propertyName) {
        String getterMethodName = "get" + StringUtils.capitalize(propertyName);
        Method[] methods = clazz.getMethods();
        Optional<Method> methodOptional = Arrays.stream(methods).filter(method -> method.getName().equalsIgnoreCase(getterMethodName)).findFirst();
        if (Optional.empty().equals(methodOptional)) {
            return null;
        } else {
            return methodOptional.get();
        }

    }

    /**
     * 调用Setter方法.
     *
     * @param obj          the obj
     * @param propertyName the property name
     * @param value        the value
     * @param propertyType 用于查找Setter方法,为空时使用value的Class替代.
     * @author ErebusST
     * @since 2022 -01-07 15:36:08
     */
    public static void invokeSetterMethod(Object obj, String propertyName, Object value, Class<?> propertyType) {
        Class<?> type = propertyType != null ? propertyType : value.getClass();
        String setterMethodName = "set" + StringUtils.capitalize(propertyName);
        invokeMethod(obj, setterMethodName, new Class[]{type}, new Object[]{value});
    }

    /**
     * 直接读取对象属性值, 无视private/protected修饰符, 不经过getter函数.
     *
     * @param obj       the obj
     * @param fieldName the field name
     * @return the field value
     * @author ErebusST
     * @since 2022 -01-07 15:36:08
     */
    public static Object getFieldValue(final Object obj, final String fieldName) {
        Field field = getAccessibleField(obj, fieldName);

        if (field == null) {
            throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + obj + "]");
        }

        Object result = null;
        try {
            result = field.get(obj);
        } catch (IllegalAccessException e) {
            log.error("不可能抛出的异常{}", e.getMessage());
        }
        return result;
    }

    /**
     * 直接设置对象属性值, 无视private/protected修饰符, 不经过setter函数.
     *
     * @param obj       the obj
     * @param fieldName the field name
     * @param value     the value
     */
    public static void setFieldValue(final Object obj, final String fieldName, Object value) {
        Field field = getAccessibleField(obj, fieldName);

        if (field == null) {
            log.info("Could not find field [" + fieldName + "] on target [" + obj + "]");
            return;
        }

        boolean isFinal = isFinal(field);

        if (isFinal) {
            return;
        }
        try {
            if (ObjectUtils.isNotNull(value)) {
                Class<?> type = field.getType();
                Class<?> clazz = value.getClass();
                if (!ObjectUtils.equals(type, clazz)) {
                    value = DataSwitch.getDefaultValue(value, type);
                }
            }

            field.set(obj, value);
        } catch (IllegalAccessException e) {
            log.error("不可能抛出的异常:{}", e.getMessage());
        }
    }

    /**
     * Is final boolean.
     *
     * @param field the field
     * @return the boolean
     * @author ErebusST
     * @since 2022 -01-07 15:36:08
     */
    public static boolean isFinal(Field field) {
        boolean isFinal = Modifier.isFinal(field.getModifiers());
        return isFinal;
    }


    /**
     * 循环向上转型, 获取对象的DeclaredField, 并强制设置为可访问. <p> 如向上转型到Object仍无法找到, 返回null.
     *
     * @param obj       the obj
     * @param fieldName the field name
     * @return the accessible field
     * @author ErebusST
     * @since 2022 -01-07 15:36:08
     */
    public static Field getAccessibleField(final Object obj, final String fieldName) {
        Assert.notNull(obj, "object不能为空");
        Assert.hasText(fieldName, "fieldName");
        for (Class<?> superClass = obj.getClass(); superClass != Object.class; superClass = superClass.getSuperclass()) {
            try {
                Field field = superClass.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException e) {//NOSONAR
                // Field不在当前类定义,继续向上转型
            }
        }
        return null;
    }

    /**
     * Get fileds field [ ].
     *
     * @param <T>   the type parameter
     * @param clazz the obj
     * @return the field [ ]
     * @author ErebusST
     * @since 2022 -01-07 15:36:08
     */
    public static <T> Field[] getFields(Class<T> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        Class superclass = clazz.getSuperclass();
        if (superclass == null) {
            return fields;
        }
        while (!superclass.equals(Object.class)) {
            Field[] superFields = superclass.getDeclaredFields();
            fields = (Field[]) ArrayUtils.addAll(fields, superFields);
            superclass = superclass.getSuperclass();
        }
        return fields;
    }


    /**
     * 直接调用对象方法, 无视private/protected修饰符. 用于一次性调用的情况.
     *
     * @param obj            the obj
     * @param methodName     the method name
     * @param parameterTypes the parameter types
     * @param args           the args
     * @return the object
     * @author ErebusST
     * @since 2022 -01-07 15:36:08
     */
    public static Object invokeMethod(final Object obj, final String methodName, final Class<?>[] parameterTypes, final Object[] args) {
        Method method = getAccessibleMethod(obj, methodName, parameterTypes);
        if (method == null) {
            throw new IllegalArgumentException("Could not find method [" + methodName + "] on target [" + obj + "]");
        }

        try {
            return method.invoke(obj, args);
        } catch (Exception e) {
            throw convertReflectionExceptionToUnchecked(e);
        }
    }

    /**
     * 循环向上转型, 获取对象的DeclaredMethod,并强制设置为可访问. 如向上转型到Object仍无法找到, 返回null. <p> 用于方法需要被多次调用的情况. 先使用本函数先取得Method,然后调用Method.invoke(Object obj, Object... args)
     *
     * @param obj            the obj
     * @param methodName     the method name
     * @param parameterTypes the parameter types
     * @return the accessible method
     * @author ErebusST
     * @since 2022 -01-07 15:36:08
     */
    public static Method getAccessibleMethod(final Object obj, final String methodName, final Class<?>... parameterTypes) {
        Assert.notNull(obj, "object不能为空");

        for (Class<?> superClass = obj.getClass(); superClass != Object.class; superClass = superClass.getSuperclass()) {
            try {
                Method method = superClass.getDeclaredMethod(methodName, parameterTypes);

                method.setAccessible(true);

                return method;

            } catch (NoSuchMethodException e) {//NOSONAR
                // Method不在当前类定义,继续向上转型
            }
        }
        return null;
    }

    /**
     * 通过反射, 获得Class定义中声明的父类的泛型参数的类型. 如无法找到, 返回Object.class. eg. public UserDao extends HibernateDao<User>
     *
     * @param <T>   the type parameter
     * @param clazz The class to introspect
     * @return the first generic declaration, or Object.class if cannot be determined
     * @author ErebusST
     * @since 2022 -01-07 15:36:08
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> Class<T> getSuperClassGenricType(final Class clazz) {
        return getSuperClassGenricType(clazz, 0);
    }

    /**
     * 通过反射, 获得Class定义中声明的父类的泛型参数的类型. 如无法找到, 返回Object.class. <p> 如public UserDao extends HibernateDao<User,Long>
     *
     * @param clazz clazz The class to introspect
     * @param index the Index of the generic ddeclaration,start from 0.
     * @return the index generic declaration, or Object.class if cannot be determined
     * @author ErebusST
     * @since 2022 -01-07 15:36:08
     */
    @SuppressWarnings("rawtypes")
    public static Class getSuperClassGenricType(final Class clazz, final int index) {

        Type genType = clazz.getGenericSuperclass();

        if (!(genType instanceof ParameterizedType)) {
            log.warn(clazz.getSimpleName() + "'s superclass not ParameterizedType");
            return Object.class;
        }

        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

        if (index >= params.length || index < 0) {
            log.warn("Index: " + index + ", Size of " + clazz.getSimpleName() + "'s Parameterized Type: " + params.length);
            return Object.class;
        }
        if (!(params[index] instanceof Class)) {
            log.warn(clazz.getSimpleName() + " not set the actual class on superclass generic parameter");
            return Object.class;
        }

        return (Class) params[index];
    }

    /**
     * 将反射时的checked exception转换为unchecked exception.
     *
     * @param e the e
     * @return the runtime exception
     * @author ErebusST
     * @since 2022 -01-07 15:36:08
     */
    public static RuntimeException convertReflectionExceptionToUnchecked(Exception e) {
        if (e instanceof IllegalAccessException || e instanceof IllegalArgumentException || e instanceof NoSuchMethodException) {
            return new IllegalArgumentException("Reflection Exception.", e);
        } else if (e instanceof InvocationTargetException) {
            return new RuntimeException("Reflection Exception.", ((InvocationTargetException) e).getTargetException());
        } else if (e instanceof RuntimeException) {
            return (RuntimeException) e;
        }
        return new RuntimeException("Unexpected Checked Exception.", e);
    }


}

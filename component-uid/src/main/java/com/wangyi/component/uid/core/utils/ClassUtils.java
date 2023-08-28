package com.wangyi.component.uid.core.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class ClassUtils {
    public static final char PACKAGE_SEPARATOR_CHAR = 46;
    public static final String PACKAGE_SEPARATOR = String.valueOf( '.' );
    public static final char INNER_CLASS_SEPARATOR_CHAR = 36;
    public static final String INNER_CLASS_SEPARATOR = String.valueOf( '$' );
    private static final Map<Class<?>, Class<?>> primitiveWrapperMap = new HashMap();
    private static final Map<Class<?>, Class<?>> wrapperPrimitiveMap;
    private static final Map<String, String> abbreviationMap;
    private static final Map<String, String> reverseAbbreviationMap;

    public static String getShortClassName(Object object, String valueIfNull) {
        if (object == null)
            return valueIfNull;

        return getShortClassName( object.getClass() );
    }

    public static String getShortClassName(Class<?> cls) {
        if (cls == null)
            return "";

        return getShortClassName( cls.getName() );
    }

    public static String getShortClassName(String className) {
        if (StringUtils.isEmpty( className )) {
            return "";
        }

        StringBuilder arrayPrefix = new StringBuilder();

        if (className.startsWith( "[" )) {
            while (className.charAt( 0 ) == '[') {
                className = className.substring( 1 );
                arrayPrefix.append( "[]" );
            }

            if ((className.charAt( 0 ) == 'L') && (className.charAt( className.length() - 1 ) == ';')) {
                className = className.substring( 1, className.length() - 1 );
            }

            if (reverseAbbreviationMap.containsKey( className ))
                className = reverseAbbreviationMap.get( className );

        }

        int lastDotIdx = className.lastIndexOf( 46 );
        int innerIdx = className.indexOf( 36, (lastDotIdx == -1) ? 0 : lastDotIdx + 1 );

        String out = className.substring( lastDotIdx + 1 );
        if (innerIdx != -1)
            out = out.replace( '$', '.' );

        return new StringBuilder().append( out ).append( arrayPrefix ).toString();
    }

    public static String getSimpleName(Class<?> cls) {
        if (cls == null)
            return "";

        return cls.getSimpleName();
    }

    public static String getSimpleName(Object object, String valueIfNull) {
        if (object == null)
            return valueIfNull;

        return getSimpleName( object.getClass() );
    }

    public static String getPackageName(Object object, String valueIfNull) {
        if (object == null)
            return valueIfNull;

        return getPackageName( object.getClass() );
    }

    public static String getPackageName(Class<?> cls) {
        if (cls == null)
            return "";

        return getPackageName( cls.getName() );
    }

    public static String getPackageName(String className) {
        if (StringUtils.isEmpty( className )) {
            return "";
        }

        while (className.charAt( 0 ) == '[') {
            className = className.substring( 1 );
        }

        if ((className.charAt( 0 ) == 'L') && (className.charAt( className.length() - 1 ) == ';')) {
            className = className.substring( 1 );
        }

        int i = className.lastIndexOf( 46 );
        if (i == -1)
            return "";

        return className.substring( 0, i );
    }

    public static List<Class<?>> getAllSuperclasses(Class<?> cls) {
        if (cls == null)
            return null;

        List classes = new ArrayList();
        Class superclass = cls.getSuperclass();
        while (superclass != null) {
            classes.add( superclass );
            superclass = superclass.getSuperclass();
        }
        return classes;
    }

    public static List<Class<?>> getAllInterfaces(Class<?> cls) {
        if (cls == null) {
            return null;
        }

        LinkedHashSet interfacesFound = new LinkedHashSet();
        getAllInterfaces( cls, interfacesFound );

        return new ArrayList( interfacesFound );
    }

    private static void getAllInterfaces(Class<?> cls, HashSet<Class<?>> interfacesFound) {
        while (cls != null) {
            Class[] interfaces = cls.getInterfaces();

            Class[] arr$ = interfaces;
            int len$ = arr$.length;
            for (int i$ = 0; i$ < len$; ++i$) {
                Class i = arr$[i$];
                if (interfacesFound.add( i ))
                    getAllInterfaces( i, interfacesFound );

            }

            cls = cls.getSuperclass();
        }
    }

    public static List<Class<?>> convertClassNamesToClasses(List<String> classNames) {
        if (classNames == null)
            return null;

        List classes = new ArrayList( classNames.size() );
        for (String className : classNames)
            try {
                classes.add( Class.forName( className ) );
            } catch (Exception ex) {
                classes.add( null );
            }

        return classes;
    }

    public static List<String> convertClassesToClassNames(List<Class<?>> classes) {
        if (classes == null)
            return null;

        List classNames = new ArrayList( classes.size() );
        for (Class cls : classes)
            if (cls == null)
                classNames.add( null );
            else
                classNames.add( cls.getName() );


        return classNames;
    }

    public static boolean isPrimitiveOrWrapper(Class<?> type) {
        if (type == null)
            return false;

        return ((type.isPrimitive()) || (isPrimitiveWrapper( type )));
    }

    public static boolean isPrimitiveWrapper(Class<?> type) {
        return wrapperPrimitiveMap.containsKey( type );
    }

    public static boolean isAssignable(Class<?> cls, Class<?> toClass, boolean autoboxing) {
        if (toClass == null) {
            return false;
        }

        if (cls == null) {
            return (!(toClass.isPrimitive()));
        }

        if (autoboxing) {
            if ((cls.isPrimitive()) && (!(toClass.isPrimitive()))) {
                cls = primitiveToWrapper( cls );
                if (cls == null)
                    return false;
            }

            if ((toClass.isPrimitive()) && (!(cls.isPrimitive()))) {
                cls = wrapperToPrimitive( cls );
                if (cls == null)
                    return false;
            }
        }

        if (cls.equals( toClass ))
            return true;

        if (cls.isPrimitive()) {
            if (!(toClass.isPrimitive()))
                return false;

            if (Integer.TYPE.equals( cls )) {
                return ((Long.TYPE.equals( toClass )) || (Float.TYPE.equals( toClass )) || (Double.TYPE.equals( toClass )));
            }

            if (Long.TYPE.equals( cls )) {
                return ((Float.TYPE.equals( toClass )) || (Double.TYPE.equals( toClass )));
            }

            if (Boolean.TYPE.equals( cls ))
                return false;

            if (Double.TYPE.equals( cls ))
                return false;

            if (Float.TYPE.equals( cls ))
                return Double.TYPE.equals( toClass );

            if (Character.TYPE.equals( cls )) {
                return ((Integer.TYPE.equals( toClass )) || (Long.TYPE.equals( toClass )) || (Float.TYPE.equals( toClass )) || (Double.TYPE.equals( toClass )));
            }

            if (Short.TYPE.equals( cls )) {
                return ((Integer.TYPE.equals( toClass )) || (Long.TYPE.equals( toClass )) || (Float.TYPE.equals( toClass )) || (Double.TYPE.equals( toClass )));
            }

            if (Byte.TYPE.equals( cls )) {
                return ((Short.TYPE.equals( toClass )) || (Integer.TYPE.equals( toClass )) || (Long.TYPE.equals( toClass )) || (Float.TYPE.equals( toClass )) || (Double.TYPE.equals( toClass )));
            }

            return false;
        }
        return toClass.isAssignableFrom( cls );
    }

    public static Class<?> primitiveToWrapper(Class<?> cls) {
        Class convertedClass = cls;
        if ((cls != null) && (cls.isPrimitive()))
            convertedClass = primitiveWrapperMap.get( cls );

        return convertedClass;
    }

    public static Class<?>[] primitivesToWrappers(Class<?>[] classes) {
        if (classes == null) {
            return null;
        }

        if (classes.length == 0) {
            return classes;
        }

        Class[] convertedClasses = new Class[classes.length];
        for (int i = 0; i < classes.length; ++i)
            convertedClasses[i] = primitiveToWrapper( classes[i] );

        return convertedClasses;
    }

    public static Class<?> wrapperToPrimitive(Class<?> cls) {
        return wrapperPrimitiveMap.get( cls );
    }

    public static Class<?>[] wrappersToPrimitives(Class<?>[] classes) {
        if (classes == null) {
            return null;
        }

        if (classes.length == 0) {
            return classes;
        }

        Class[] convertedClasses = new Class[classes.length];
        for (int i = 0; i < classes.length; ++i)
            convertedClasses[i] = wrapperToPrimitive( classes[i] );

        return convertedClasses;
    }

    public static boolean isInnerClass(Class<?> cls) {
        return ((cls != null) && (cls.getEnclosingClass() != null));
    }

    static {
        primitiveWrapperMap.put( Boolean.TYPE, Boolean.class );
        primitiveWrapperMap.put( Byte.TYPE, Byte.class );
        primitiveWrapperMap.put( Character.TYPE, Character.class );
        primitiveWrapperMap.put( Short.TYPE, Short.class );
        primitiveWrapperMap.put( Integer.TYPE, Integer.class );
        primitiveWrapperMap.put( Long.TYPE, Long.class );
        primitiveWrapperMap.put( Double.TYPE, Double.class );
        primitiveWrapperMap.put( Float.TYPE, Float.class );
        primitiveWrapperMap.put( Void.TYPE, Void.TYPE );

        wrapperPrimitiveMap = new HashMap();

        for (Class primitiveClass : primitiveWrapperMap.keySet()) {
            Class wrapperClass = primitiveWrapperMap.get( primitiveClass );
            if (!(primitiveClass.equals( wrapperClass ))) {
                wrapperPrimitiveMap.put( wrapperClass, primitiveClass );
            }

        }

        Map<String, String> m = new HashMap<>();
        m.put( "int", "I" );
        m.put( "boolean", "Z" );
        m.put( "float", "F" );
        m.put( "long", "J" );
        m.put( "short", "S" );
        m.put( "byte", "B" );
        m.put( "double", "D" );
        m.put( "char", "C" );
        m.put( "void", "V" );
        Map r = new HashMap();
        for (Map.Entry e : m.entrySet())
            r.put( e.getValue(), e.getKey() );

        abbreviationMap = Collections.unmodifiableMap( m );
        reverseAbbreviationMap = Collections.unmodifiableMap( r );
    }

    public enum Interfaces {
        INCLUDE, EXCLUDE
    }
}

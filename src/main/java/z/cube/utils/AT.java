package z.cube.utils;

import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Predicate;

/**
 * Annotation 注解工具类
 * 基于Fluent API设计，用于获取包、类、构造函数、字段和方法上的注解，以及方法和构造函数中参数上的注解；
 * <p/>
 * [注] Package注解只能在package-info.java上
 * <p/>
 * 该工具类的灵感来源于[JOOR](https://github.com/jOOQ/jOOR)
 */
public class AT {
    /**
     * 存放对象
     */
    private final Object object;
    /**
     * 参数名称发现类，用于寻找方法或构造函数中参数的名称
     * ParameterNameDiscoverer 线程安全
     */
    private static final ParameterNameDiscoverer PND = new DefaultParameterNameDiscoverer();

    /**
     * 私有构造函数
     *
     * @param obj
     */
    private AT(Object obj) {
        this.object = obj;
    }

    /**
     * 用于统一 at方法
     */
    private static AT _at(Object obj) {
        return new AT(obj);
    }

    /**
     * 在Class上获取注解
     */
    public static AT at(Class<?> clazz) {
        return _at(clazz);
    }

    /**
     * 在Method上获取注解
     */
    public static AT at(Method method) {
        return _at(method);
    }

    /**
     * 在Field上获取注解
     */
    public static AT at(Field field) {
        return _at(field);
    }

    /**
     * 在Constructor上获取注解
     */
    public static AT at(Constructor<?> constructor) {
        return _at(constructor);
    }

    /**
     * 在Package上获取注解
     */
    public static AT at(Package p) {
        return _at(p);
    }

    /**
     * 获取匹配的字段
     *
     * @param name 字段名称
     * @return 根据对应字段创建的AT对象
     */
    public AT field(String name) {
        if (this.object instanceof Class) {
            try {
                Class<?> clazz = (Class<?>) this.object;
                Field field = clazz.getDeclaredField(name);
                return new AT(field);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        } else {
            throw new RuntimeException("非Class对象无法获取Field!");
        }
    }

    /**
     * 获取匹配的方法
     *
     * @param name 方法名称
     * @param args 方法参数类型列表
     * @return 根据对应方法创建的AT对象
     */
    public AT method(String name, Class<?>... args) {
        if (this.object instanceof Class) {
            try {
                Class<?> clazz = (Class<?>) this.object;
                Method method = clazz.getDeclaredMethod(name, args);
                return new AT(method);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        } else {
            throw new RuntimeException("非Class对象无法获取Method!");
        }
    }

    /**
     * 获取匹配的无参数方法
     *
     * @param name 方法名称
     * @return 根据对应方法创建的AT对象
     */
    public AT method(String name) {
        return method(name, new Class[]{});
    }

    /**
     * 获取匹配的构造函数
     *
     * @param parameterTypes 构造函数参数类型列表
     * @return 根据对应构造函数创建的AT对象
     */
    public AT constructor(Class<?>... parameterTypes) {
        if (this.object instanceof Class) {
            try {
                Class<?> clazz = (Class<?>) this.object;
                Constructor<?> constructor = clazz.getConstructor(parameterTypes);
                return new AT(constructor);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        } else {
            throw new RuntimeException("非Class对象无法获取Constructor!");
        }
    }

    /**
     * 获取包信息
     * (package是关键字，只能加"_"处理)
     *
     * @return 根据对应包创建的AT对象
     */
    @SuppressWarnings("rawtypes")
	public AT package_() {
        if (this.object instanceof Class) {
            Package p = ((Class) this.object).getPackage();
            return new AT(p);
        } else {
            throw new RuntimeException("非Class对象无法获取Package!");
        }
    }

    /**
     * 获取指定的注解
     *
     * @param annotationClass 指定的注解类
     * @return 根据指定注解创建的AT对象
     */
    public AT annotation(Class<? extends Annotation> annotationClass) {
        Annotation annObject = null;
        if (this.object instanceof AnnotatedElement) {
            AnnotatedElement annotatedElement = (AnnotatedElement) this.object;
            annObject = annotatedElement.getAnnotation(annotationClass);
        } else if (object instanceof Annotation[]) {
            Annotation[] annotations = (Annotation[]) object;
            for (Annotation annotation : annotations) {
                if (annotationClass.equals(annotation.annotationType())) {
                    annObject = annotation;
                    break;
                }
            }
        }
        return new AT(annObject);
    }

    /**
     * 获取所有的注解
     *
     * @return 根据所有注解创建的AT对象
     */
    public AT annotation() {
        Annotation[] annotations = null;
        if (this.object instanceof AnnotatedElement) {
            AnnotatedElement annotatedElement = (AnnotatedElement) this.object;
            annotations = annotatedElement.getDeclaredAnnotations();
        } else if (this.object instanceof Annotation[]) {
            annotations = (Annotation[]) object;
        }
        if (annotations == null || annotations.length == 0) {
            throw new RuntimeException(String.format("在[%s]上无法获取到Annotation!", this.object));
        }
        return new AT(Arrays.asList(annotations));
    }

    /**
     * 根据名称获取匹配的参数
     *
     * @param name 参数名称
     * @return 根据匹配的参数位置上的注解创建的AT对象
     */
    public AT param(String name) {
        //传入参数的名称数组
        String[] parameterNames = parameterNames();

        int index = Arrays.asList(parameterNames).indexOf(name);
        return arg(index);
    }

    /**
     * 获取方法或是构造函数的参数名称
     *
     * @return 参数名称数组
     */
    private String[] parameterNames() {
        Object methodOrConstructor = this.object;
        if (methodOrConstructor instanceof Method) {
            Method method = (Method) methodOrConstructor;
            return PND.getParameterNames(method);
        } else if (methodOrConstructor instanceof Constructor) {
            Constructor<?> constructor = (Constructor<?>) methodOrConstructor;
            return PND.getParameterNames(constructor);
        } else {
            throw new RuntimeException("除Method和Constructor类外无法获取参数名称!");
        }
    }

    /**
     * 获取方法或是构造函数的注解
     *
     * @return 注解数组
     */
    private Annotation[][] parameterAnnotations() {
        Object methodOrConstructor = this.object;
        if (methodOrConstructor instanceof Method) {
            Method method = (Method) methodOrConstructor;
            return method.getParameterAnnotations();
        } else if (methodOrConstructor instanceof Constructor) {
            Constructor<?> constructor = (Constructor<?>) methodOrConstructor;
            return constructor.getParameterAnnotations();
        } else {
            throw new RuntimeException("除Method和Constructor类外无法获取参数注解!");
        }
    }

    /**
     * 根据索引获取匹配的参数
     *
     * @param i 参数索引位置
     * @return 根据匹配的参数位置上的注解创建的AT对象
     */
    public AT arg(int i) {
        Annotation[][] annotations = parameterAnnotations();
        if (i > (annotations.length - 1)) {
            //数组下标越界自动抛异常，是否还需手工抛异常?
        }
        return new AT(annotations[i]);
    }

    /**
     * 获取指定注解
     *
     * @param <T> Annotation
     * @return Annotation实例对象
     */
    @SuppressWarnings("unchecked")
    public <T extends Annotation> T get() {
        return (T) this.object;
    }

    /**
     * 获取所有注解列表
     *
     * @param <T> 继承Iterable的集合类
     * @return 存放Annotation的集合类
     */
    @SuppressWarnings("unchecked")
    public <T extends Iterable<? extends Annotation>> T list() {
        return (T) this.object;
    }


    /**
     * 获取注解类对应的实例化对象
     * (annotation instance)
     *
     * @param annotationClass 注解类class
     * @param <T>             注解具体类
     * @return 注解具体类实例对象
     */
    @SuppressWarnings("unchecked")
    public <T extends Annotation> T ai(Class<T> annotationClass) {
        T t = null;
        if (this.object instanceof AnnotatedElement) {
            AnnotatedElement annotatedElement = (AnnotatedElement) this.object;
            t = annotatedElement.getAnnotation(annotationClass);
        } else if (object instanceof Annotation[]) {
            Annotation[] annotations = (Annotation[]) object;
            for (Annotation annotation : annotations) {
                if (annotationClass.equals(annotation.annotationType())) {
                    t = (T) annotation;
                    break;
                }
            }
        }
        if (t == null) {
            throw new RuntimeException(String.format("在[%s]上无法获取到%s!", this.object, annotationClass));
        }
        return t;
    }

    /**
     * 是否存在
     *
     * @return true表示存在，false表示不存在
     */
    public boolean isPresent() {
        return this.object != null;
    }

    /**
     * 是否存在指定的注解
     *
     * @param annotationClass 指定注解
     * @return true表示存在，false表示不存在
     */
    public boolean isPresent(Class<? extends Annotation> annotationClass) {
        return this.annotation(annotationClass).isPresent();
    }

    /**
     * 是否存在
     *
     * @return true表示存在，false表示不存在
     */
    public boolean has() {
        return isPresent();
    }

    /**
     * 是否存在指定的注解
     *
     * @param annotationClass 指定注解
     * @return true表示存在，false表示不存在
     */
    public boolean has(Class<? extends Annotation> annotationClass) {
        return isPresent(annotationClass);
    }

    /**
     * 组装由参数名称和对应参数上的注解数组构成的map
     */
    public AT param() {
        Annotation[][] annotations = this.parameterAnnotations();
        String[] names = this.parameterNames();
        Map<String, Annotation[]> map = new HashMap<String, Annotation[]>(8);
        for (int i = 0; i < names.length; i++) {
            map.put(names[i], annotations[i]);
        }
        return new AT(map);
    }

    /**
     * 组装由参数索引和对应参数上的注解数组构成的map
     */
    public AT arg() {
        Annotation[][] annotations = this.parameterAnnotations();
        Map<String, Annotation[]> map = new HashMap<String, Annotation[]>(8);
        for (int i = 0; i < annotations.length; i++) {
            map.put(String.valueOf(i), annotations[i]);
        }
        return new AT(map);
    }

    /**
     * 获取参数注解对应Map
     */
    @SuppressWarnings("unchecked")
	public Map<String, Annotation[]> map() {
        if (this.object instanceof Map) {
            return (Map<String, Annotation[]>) this.object;
        }
        throw new RuntimeException("无法正确获取map对象!");
    }

    public List<AT> fields(Predicate<AT> filter){
        if(this.object instanceof Class){
            Class clazz = (Class) this.object;
            Field[] fields = clazz.getDeclaredFields();
            List<AT> ats = new ArrayList<>(fields.length);
            for(Field field : fields){
                AT at = at(field);
                if(filter == null || filter.test(at)){
                    ats.add(at);
                }
            }
            return ats;
        }
        throw new RuntimeException("非Class对象无法获取Field!");
    }
    public List<AT> fields(){
        return fields(null);
    }
}

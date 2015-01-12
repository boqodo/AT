# AT
Java 获取注解(Annotation)的工具类

基于Fluent API设计，用于获取包、类、构造函数、字段和方法上的注解，以及方法和构造函数中参数上的注解；

>该工具类的灵感来源于[JOOR](https://github.com/jOOQ/jOOR)

####获取类上注解

```java

XmlRootElement x = at(Person.class)
                .annotation(XmlRootElement.class)
                .get();

```

####获取构造函数上的注解
```java
 Deprecated deprecated = at(Person.class)
                .constructor(String.class, Integer.class)
                .annotation(Deprecated.class).get();
 
 List<Annotation> anns = at(Person.class)
                .constructor(String.class, Integer.class)
                .annotation().list();
```

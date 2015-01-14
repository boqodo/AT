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

####获取字段上的注解
```java

XmlAttribute att = at(Person.class)
            .field("name")
            .annotation(XmlAttribute.class)
            .get();
```

####获取方法上的注解
```java

NotNull nn = at(Person.class)
                .method("setName", String.class)
                .annotation(NotNull.class)
                .get();
```

###获取参数上的注解
```java
// 通过参数名称的方式
// 获取方法参数名称为name上的注解
NotNull nn = at(Person.class)
                .method("setName", String.class)
                .param("name")
                .annotation(NotNull.class)
                .get();
                
// 获取构造函数参数名称为name上的注解
List<Annotation> anns = at(Person.class)
                .constructor(String.class, Integer.class)
                .param("name")
                .annotation().list();

// 通过参数索引的方式
NotNull nn2 = at(Person.class)
                .method("setName", String.class)
                .arg(0)
                .annotation(NotNull.class)
                .get();

Max max = at(Person.class).constructor(String.class, Integer.class)
                .arg(1)
                .annotation(Max.class).get();
```

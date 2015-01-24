package z.cube.utils;

import javax.persistence.Transient;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "z.cube.utils.Person")
@XmlAccessorType(XmlAccessType.FIELD)
public class Person {
    @XmlAttribute(name = "NAME")
    @Deprecated
    private String name;

    private Integer age;


    public Person() {
    }

    @Deprecated
    public Person(@NotNull String name, @Max(value = 20) Integer age) {
        this.name = name;
        this.age = age;
    }

    @Deprecated
    @Transient
    public String getName() {
        return name;
    }

    @Deprecated
    public void setName(@NotNull String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }


    public void mutlEle(@NotNull String name, @Max(value = 20) Integer age){
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}

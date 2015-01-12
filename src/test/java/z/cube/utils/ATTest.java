package z.cube.utils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.Transient;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static z.cube.utils.AT.at;


public class ATTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public final void testAt() throws Exception {
        Method method = Person.class.getDeclaredMethod("setName", String.class);
        NotNull nn = at(method).param("name").annotation(NotNull.class).get();
        assertThat(nn).isNotNull();

        Field field = Person.class.getDeclaredField("name");
        List<Annotation> anns = at(field).annotation().list();
        for (Annotation ann : anns) {
            assertThat(ann).isInstanceOfAny(XmlAttribute.class, Deprecated.class);
        }
        Constructor<Person> constructor = Person.class.getConstructor(String.class, Integer.class);
        Deprecated deprecated = at(constructor).annotation(Deprecated.class).get();
        assertThat(deprecated).isNotNull();
    }

    @Test
    public final void testClassAnn() {
        XmlRootElement x = at(Person.class)
                .annotation(XmlRootElement.class)
                .get();
        assertThat(x).isNotNull();
        assertThat(x.name()).isNotNull().isEqualTo("z.cube.utils.Person");
        List<Annotation> anns = at(Person.class)
                .annotation()
                .list();
        for (Annotation ann : anns) {
            assertThat(ann).isInstanceOfAny(XmlRootElement.class, XmlAccessorType.class);
        }
    }

    @Test
    public final void testFieldAnn() throws NoSuchFieldException, SecurityException {
        XmlAttribute att = at(Person.class)
                .field("name")
                .annotation(XmlAttribute.class)
                .get();
        assertThat(att).isNotNull();
        assertThat(att.name()).isNotNull().isEqualTo("NAME");
        List<Annotation> anns = at(Person.class)
                .field("name")
                .annotation()
                .list();
        for (Annotation ann : anns) {
            assertThat(ann).isInstanceOfAny(XmlAttribute.class, Deprecated.class);
        }
    }

    @Test
    public final void testMethodAnn() throws NoSuchFieldException, SecurityException {
        Deprecated att = at(Person.class)
                .method("getName")
                .annotation(Deprecated.class)
                .get();
        assertThat(att).isNotNull();
        List<Annotation> anns = at(Person.class)
                .method("getName")
                .annotation()
                .list();
        for (Annotation ann : anns) {
            assertThat(ann).isInstanceOfAny(Deprecated.class, Transient.class);
        }

    }

    @Test
    public final void testConstructor() {
        List<Annotation> anns = at(Person.class)
                .constructor(String.class, Integer.class)
                .annotation().list();
        for (Annotation ann : anns) {
            assertThat(ann).isInstanceOfAny(Deprecated.class);
        }
        Deprecated deprecated = at(Person.class)
                .constructor(String.class, Integer.class)
                .annotation(Deprecated.class).get();
        assertThat(deprecated).isNotNull();
    }

    @Test
    public final void testConstructorParam() {
        List<Annotation> anns = at(Person.class)
                .constructor(String.class, Integer.class)
                .param("name")
                .annotation().list();
        for (Annotation ann : anns) {
            assertThat(ann).isInstanceOfAny(NotNull.class);
        }
        Max max = at(Person.class).constructor(String.class, Integer.class)
                .param("age")
                .annotation(Max.class).get();
        assertThat(max).isNotNull();
        assertThat(max.value()).isEqualTo(20);

        List<Annotation> anns2 = at(Person.class)
                .constructor(String.class, Integer.class)
                .arg(0)
                .annotation().list();
        for (Annotation ann : anns2) {
            assertThat(ann).isInstanceOfAny(NotNull.class);
        }
        Max max2 = at(Person.class).constructor(String.class, Integer.class)
                .arg(1)
                .annotation(Max.class).get();
        assertThat(max2).isNotNull();
        assertThat(max2.value()).isEqualTo(20);
    }

    @Test
    public final void testMethodParam() {
        NotNull nn = at(Person.class)
                .method("setName", String.class)
                .param("name")
                .annotation(NotNull.class)
                .get();
        assertThat(nn).isNotNull();

        List<Annotation> anns = at(Person.class)
                .method("setName", String.class)
                .param("name")
                .annotation()
                .list();
        for (Annotation ann : anns) {
            assertThat(ann).isInstanceOfAny(NotNull.class);
        }

        NotNull nn2 = at(Person.class)
                .method("setName", String.class)
                .arg(0)
                .annotation(NotNull.class)
                .get();
        assertThat(nn2).isNotNull();

        List<Annotation> anns2 = at(Person.class)
                .method("setName", String.class)
                .arg(0)
                .annotation()
                .list();
        for (Annotation ann : anns2) {
            assertThat(ann).isInstanceOfAny(NotNull.class);
        }
    }

    @Test
    public final void testAnn() {
        String name = at(Person.class)
                .field("name")
                .ai(XmlAttribute.class).name();
        assertThat(name).isNotEmpty().isEqualTo("NAME");
        String rname = at(Person.class).ai(XmlRootElement.class).name();
        assertThat(rname).isNotEmpty().isEqualTo("z.cube.utils.Person");

        Long value = at(Person.class).constructor(String.class, Integer.class)
                .param("age").ai(Max.class).value();
        assertThat(value).isNotNull().isEqualTo(20l);
    }

    @Test
    public final void testPackage() {
        List<Annotation> annotations = at(Person.class.getPackage()).annotation().list();
        for (Annotation annotation : annotations) {
            assertThat(annotation).isInstanceOfAny(PackageAnnotationTest.class);
        }
    }

    @Test(expected = RuntimeException.class)
    public final void testException() {
        at(Person.class).method("getName").field("name");
    }

    @Test(expected = RuntimeException.class)
    public final void testException2() {
        at(Person.class).method("getName").method("getName");
    }

    @Test(expected = RuntimeException.class)
    public final void testExceeption3() {
        at(Person.class).method("getName").constructor(String.class, Integer.class);
    }

    @Test(expected = RuntimeException.class)
    public final void testExceeption4() {
        at(Person.class).method("getName").annotation(XmlAttribute.class).get();
    }

    @Test(expected = RuntimeException.class)
    public final void testExceeption5() {
        at(Person.class).method("getName").ai(XmlAttribute.class);
    }

    @Test(expected = RuntimeException.class)
    public final void testExceeption6() {
        at(Person.class).field("name").param("name");
    }

    @Test(expected = RuntimeException.class)
    public final void testExceeption7() {
        at(Person.class).field("name").arg(0);
    }


    @Test(expected = RuntimeException.class)
    public final void testExceeption8() {
        at(Person.class).constructor().annotation();
    }

    @Test(expected = RuntimeException.class)
    public final void testConstructorExceeption() {
        at(Person.class).constructor(Integer.class).annotation();
    }

    @Test(expected = RuntimeException.class)
    public final void testMethodExceeption() {
        at(Person.class).method("setPerson").annotation();
    }

    @Test(expected = RuntimeException.class)
    public final void testFeildExceeption() {
        at(Person.class).field("bean").annotation();
    }

}

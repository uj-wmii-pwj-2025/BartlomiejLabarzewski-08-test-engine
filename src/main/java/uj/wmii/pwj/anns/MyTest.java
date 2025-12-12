package uj.wmii.pwj.anns;

import java.lang.annotation.*;

@Repeatable(MyTests.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MyTest {
    String param() default "";
    TestResult expectedResult() default TestResult.PASS;
}
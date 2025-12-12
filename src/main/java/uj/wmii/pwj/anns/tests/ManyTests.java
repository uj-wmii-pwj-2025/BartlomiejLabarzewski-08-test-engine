package uj.wmii.pwj.anns.tests;

import uj.wmii.pwj.anns.Asserts;
import uj.wmii.pwj.anns.MyTest;
import uj.wmii.pwj.anns.TestResult;

public class ManyTests {

    @MyTest
    public void testSoemthing() {
        System.out.println("I'm testing something!");
    }

    @MyTest(param = "a param")
    @MyTest(param = "b param")
    @MyTest(param = "c param. Long, long C param.")
    public void testWithParam(String param) {
        System.out.printf("I was invoked with parameter: %s\n", param);
    }

    public void notATest() {
        System.out.println("I'm not a test.");
    }

    @MyTest
    public void imFailue() {
        System.out.println("I AM EVIL.");
        throw new NullPointerException();
    }

    @MyTest
    public void isNullNull() {
        Asserts.assertNull(null);
    }

    @MyTest
    public void isEmptyStringNotNull() {
        Asserts.assertNotNull("");
    }

    @MyTest
    public void isNullNotNull() {
        Asserts.assertNotNull(null);
    }

    @MyTest(expectedResult = TestResult.FAIL)
    public void imPesimistic() {
        Asserts.assertNotNull(null);
    }

}

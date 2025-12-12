package uj.wmii.pwj.anns.tests;

import uj.wmii.pwj.anns.Asserts;
import uj.wmii.pwj.anns.MyTest;

public class PassesOnly {

    @MyTest
    public void foo() {
        Asserts.assertNull(null);
    }

    @MyTest
    public void szczurSzczurowiRowny() {
        Asserts.assertEquals("szczur", "szczur");
    }
}

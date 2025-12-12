package uj.wmii.pwj.anns;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class MyTestEngine {

    private final Object testClassObject;
    private final boolean verbose;
    private final boolean fileLog;
    private final TestDisplayer displayer;
    private final Logger logger;
    int spottedErrors;

    public MyTestEngine(Object testClassObject, TestDisplayer displayer, Logger logger, boolean verbose, boolean fileLog) {
        this.testClassObject = testClassObject;
        this.displayer = displayer;
        this.verbose = verbose;
        this.fileLog = fileLog;
        this.logger = logger;
        spottedErrors = 0;
    }

    public void runTests() {

        logger.initialize();

        displayer.displayHeader();

        displayer.displayTestedClass(testClassObject.getClass());

        List<Method> testMethods = getTestMethods(testClassObject);

        Map<TestResult, List<String>> resultToTests = new HashMap<>();
        for (TestResult r : TestResult.values()) {
            resultToTests.put(r, new LinkedList<String>());
        }

        for (Method m: testMethods) {

            Map<TestResult, List<String>> methodResults = launchSingleMethod(m, testClassObject);

            for (TestResult result : TestResult.values()) {
                resultToTests.get(result).addAll(methodResults.get(result));
            }

        }

        displayer.displaySummary(resultToTests.get(TestResult.PASS), resultToTests.get(TestResult.FAIL), resultToTests.get(TestResult.ERROR));
    }

    private Map<TestResult, List<String>> launchSingleMethod(Method m, Object unit) {

        MyTest[] annotations = m.getAnnotationsByType(MyTest.class);

        Map<TestResult, List<String>> resultToTests = new HashMap<>();
        for (TestResult r: TestResult.values()) {
            resultToTests.put(r, new LinkedList<String>());
        }

        for (MyTest annotation: annotations) {

            displayer.displayLaunchMessage(m, annotation.param(), annotation.expectedResult());

            Throwable[] error = new Throwable[1];

            TestResult actualResult = annotation.param().isEmpty() ? launchSingleTest(m, unit, annotation.expectedResult(), error) : launchSingleTest(m, unit, annotation.param(), annotation.expectedResult(), error);

            TestResult finalResult;
            if (actualResult == TestResult.ERROR) finalResult = TestResult.ERROR;
            else if (annotation.expectedResult() == actualResult) finalResult = TestResult.PASS;
            else finalResult = TestResult.FAIL;

            resultToTests.get(finalResult).add(m.getName() + "(" + annotation.param() + ")");

            displayer.displayResultMessage(m, annotation.param(), annotation.expectedResult(), finalResult);

            if (error[0] != null && (finalResult == TestResult.ERROR || finalResult == TestResult.FAIL)) {
                if (verbose) {
                    displayer.displayError(error[0]);
                }
                if (fileLog) {
                    logger.logError(error[0], spottedErrors);
                    displayer.displayLogErrorInfo(error[0], spottedErrors);
                }
                spottedErrors++;
            }

        }

        return resultToTests;
    }

    private TestResult launchSingleTest(Method test, Object unit, String param, TestResult expectedResult, Throwable[] error) {

        try {
            test.invoke(unit, param);
        }
        catch (IllegalArgumentException e) {
            System.out.println("Illegal test parameter!");
            return TestResult.ERROR;
        }
        catch (IllegalAccessException e) {
            System.out.println("Test method is not accessible!");
            return TestResult.ERROR;
        }
        catch (InvocationTargetException e) {
            if (e.getCause() instanceof MyAssertError asserte) {
                error[0] = asserte;
                return TestResult.FAIL;
            }
            error[0] = e.getCause();
            return TestResult.ERROR;
        }

        return TestResult.PASS;
    }

    private TestResult launchSingleTest(Method test, Object unit, TestResult expectedResult, Throwable[] error) {

        try {
            test.invoke(unit);
        }
        catch (IllegalArgumentException e) {
            System.out.println("Illegal test parameter!");
            return TestResult.ERROR;
        }
        catch (IllegalAccessException e) {
            System.out.println("Test method is not accessible!");
            return TestResult.ERROR;
        }
        catch (InvocationTargetException e) {
            if (e.getCause() instanceof MyAssertError asserte) {
                if (expectedResult == TestResult.PASS) {
                    error[0] = asserte;
                    return TestResult.FAIL;
                }
                else {
                    return TestResult.FAIL;
                }
            }
            error[0] = e.getCause();
            return TestResult.ERROR;
        }

        return expectedResult == TestResult.PASS ?  TestResult.PASS : TestResult.FAIL;
    }

    private static List<Method> getTestMethods(Object unit) {
        Method[] methods = unit.getClass().getDeclaredMethods();
        return Arrays.stream(methods).filter(
                m -> m.getAnnotation(MyTests.class) != null || m.getAnnotation(MyTest.class) != null).collect(Collectors.toList());
    }

    private static Object getObject(String className) {
        try {
            Class<?> unitClass = Class.forName(className);
            return unitClass.getConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            System.err.println(e.getStackTrace());
            return new Object();
        }
    }
}

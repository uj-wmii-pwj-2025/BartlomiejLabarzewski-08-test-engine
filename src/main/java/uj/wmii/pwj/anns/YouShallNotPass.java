package uj.wmii.pwj.anns;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Optional;

public class YouShallNotPass {

    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println("Incorrect usage...");
            System.out.println("For more information, use: YouShallNotPass --help");
            return;
        }

        if (Arrays.asList(args).contains("--help")) {
            System.out.println("Usage: YouShallNotPass [OPTION]... class=CLASS");
            System.out.println("Execute tests in CLASS.\n");
            System.out.println("CLASS has to be provided with its package, eg. 'package.of.the.tested.CLASS' (without the apostrophes).\n");
            System.out.println("If CLASS has an explicitly defined constructor, it also must have a public no argument one.\n");
            System.out.println("In order to execute tests, the user has to define test methods. Test method can have a string parameter and should not return any value.\n");
            System.out.println("Each test method has to be annotated with @MyTest. Otherwise, it will not be executed. This annotation takes values:");
            System.out.println("param               the string parameter that is going to be used as the method's argument. If a method has a parameter, this value should be provided");
            System.out.println("expectedResult      the expected result of a test. It can be either PASS or FAIL. Default value is PASS.\n");
            System.out.println("A test method can be executed multiple times by using the annotation more than one time.\n");
            System.out.println("Optional flags: ");
            System.out.println("--help              display this message and exit");
            System.out.println("--verbose           if a test fails or throws an error, print the stack trace in the console");
            System.out.println("--filelog           if a test fails or throws an error, print the stack trace to a file");
            System.out.println();
            return;
        }

        boolean verbose = isVerbose(args);
        boolean fileLog = isFileLog(args);
        String className = findTestClass(args);
        if (className == null) {
            System.err.println("Invalid usage...");
            System.err.println("The tested class name has to be preceded with \"class=\"");
            System.err.println("For more information, use: YouShallNotPass --help");
            return;
        }

        try {
            Class<?> testClass = Class.forName(className);
            Constructor<?> testClassConstructor = testClass.getDeclaredConstructor();

            MyTestEngine engine = new MyTestEngine(testClassConstructor.newInstance(), new TestDisplayer(), new Logger(), verbose, fileLog);
            engine.runTests();
        }
        catch (ClassNotFoundException e) {
            System.err.println("Class " + className + " not found.");
            System.err.println("For more information, use: YouShallNotPass --help");
            System.exit(-1);
        }
        catch (NoSuchMethodException | InstantiationException e) {
            System.err.println("Class " + className + " does not have a public no-arg constructor.");
            System.err.println("For more information, use: YouShallNotPass --help");
            System.exit(-1);
        }
        catch (IllegalAccessException e) {
            System.err.println("Class " + className + "'s access rights are invalid.");
            System.err.println("For more information, use: YouShallNotPass --help");
            System.exit(-1);
        }
        catch (InvocationTargetException e) {
            System.err.println("Class " + className + "'s constructor has thrown an exception");
            System.err.println("For more information, use: YouShallNotPass --help");
            System.exit(-1);
        }
    }

    private static boolean isVerbose(String[] args) {
        return Arrays.asList(args).contains("--verbose");
    }

    private static boolean isFileLog(String[] args) {
        return Arrays.asList(args).contains("--filelog");
    }

    private static String findTestClass(String[] args) {
        Optional<String> optArg = Arrays.stream(args).filter(arg -> arg.startsWith("class=")).findFirst();
        return optArg.map(s -> s.substring(6)).orElse(null);
    }
}

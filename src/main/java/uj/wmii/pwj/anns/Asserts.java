package uj.wmii.pwj.anns;

public class Asserts {

    /** <p>
     * This method checks if the provided object reference is not null.
     * </p>
     * @param object the object that one wants to check
     * @throws MyAssertError if the object is null.
     */
    public static void assertNotNull(Object object) {
        if (object == null) {
            throw new MyAssertError("Object '" + object + "' is null.");
        }
    }

    /** <p>
     * This method checks if the provided object reference is null.
     * </p>
     * @param object the object that one wants to check
     * @throws MyAssertError if the object is not null.
     */
    public static void assertNull(Object object) {
        if (object != null) {
            throw new MyAssertError("Object '" + object + "' is not null.");
        }
    }

    /** <p>
     * This method checks if two provided objects are equal (according to Object::equals).
     * </p>
     * <p>
     * Both parameters have to be non-null. If one wants to check if an object is null, they shall use Asserts::assertNull.)
     * </p>
     * @param expected the expected value
     * @param actual the actual value
     * @throws MyAssertError if one of the parameters is null or the objects are not equal.
     */
    public static void assertEquals(Object expected, Object actual) {

        assertNotNull(expected);
        assertNotNull(actual);

        if (!actual.equals(expected)) {
            throw new MyAssertError("Expected '" + expected + "', actual '" + actual + "'. These are not equal.");
        }

    }

    /** <p>
     * This method checks if two provided objects are different (according to Object::equals).
     * </p>
     * <p>
     * Both parameters have to be non-null. If one wants to check if an object is non-null, they shall use Asserts::assertNotNull.)
     * </p>
     * @param unexpected the unexpected value
     * @param actual the actual value
     * @throws MyAssertError if one of the parameters is null or the objects are equal.
     */
    public static void assertNotEquals(Object unexpected, Object actual) {

        assertNotNull(unexpected);
        assertNotNull(actual);
        if (actual.equals(unexpected)) {
            throw new MyAssertError("Unexpected '" + unexpected + "', actual '" + actual + "'. These are equal.");
        }

    }
}

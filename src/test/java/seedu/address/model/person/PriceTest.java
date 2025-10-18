package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class PriceTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Price(null));
    }

    @Test
    public void constructor_invalidPrice_throwsIllegalArgumentException() {
        String invalidPrice = "";
        assertThrows(IllegalArgumentException.class, () -> new Price(invalidPrice));
    }

    @Test
    public void isValidPrice() {
        // null price
        assertThrows(NullPointerException.class, () -> Price.isValidPrice(null));

        // invalid prices
        assertFalse(Price.isValidPrice("")); // empty string
        assertFalse(Price.isValidPrice(" ")); // spaces only
        assertFalse(Price.isValidPrice("abc")); // letters only
        assertFalse(Price.isValidPrice("123abc")); // alphanumeric
        assertFalse(Price.isValidPrice(" 123")); // leading space
        assertFalse(Price.isValidPrice("123 ")); // trailing space
        assertFalse(Price.isValidPrice("-100")); // negative number
        assertFalse(Price.isValidPrice("+100")); // plus sign
        assertFalse(Price.isValidPrice("100 dollars")); // with text

        // valid prices
        assertTrue(Price.isValidPrice("0")); // zero
        assertTrue(Price.isValidPrice("1")); // single digit
        assertTrue(Price.isValidPrice("100")); // multiple digits
        assertTrue(Price.isValidPrice("1000000")); // large number
        assertTrue(Price.isValidPrice("0001")); // leading zeros
        assertTrue(Price.isValidPrice("999999999999999")); // very large number
        assertTrue(Price.isValidPrice("12.34")); // decimal point
        assertTrue(Price.isValidPrice("1,000")); // comma separator
    }

    @Test
    public void equals() {
        Price price = new Price("1000000");

        // same values -> returns true
        assertTrue(price.equals(new Price("1000000")));

        // same object -> returns true
        assertTrue(price.equals(price));

        // null -> returns false
        assertFalse(price.equals(null));

        // different types -> returns false
        assertFalse(price.equals(1000000)); // integer
        assertFalse(price.equals("1000000")); // string
        assertFalse(price.equals(1000000.0)); // double

        // different values -> returns false
        assertFalse(price.equals(new Price("500000")));
        assertFalse(price.equals(new Price("1000001")));
        assertFalse(price.equals(new Price("0")));
    }

    @Test
    public void hashCode_test() {
        Price price1 = new Price("1000000");
        Price price2 = new Price("1000000");
        Price price3 = new Price("500000");

        // same value -> same hashcode
        assertEquals(price1.hashCode(), price2.hashCode());

        // multiple calls should return same hashcode
        int hashCode1 = price1.hashCode();
        int hashCode2 = price1.hashCode();
        assertEquals(hashCode1, hashCode2);
    }

    @Test
    public void toString_test() {
        Price price = new Price("850000");
        assertEquals("850000", price.toString());
    }

    @Test
    public void edgeCases() {
        // Test with maximum integer string length
        Price longPrice = new Price("12345678901234567890");
        assertEquals("12345678901234567890", longPrice.toString());

        // Test zero price
        Price zeroPrice = new Price("0");
        assertEquals("0", zeroPrice.toString());
        assertTrue(zeroPrice.isValidPrice("0"));
    }
}

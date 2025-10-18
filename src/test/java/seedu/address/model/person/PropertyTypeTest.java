package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class PropertyTypeTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new PropertyType(null));
    }

    @Test
    public void constructor_invalidPropertyType_throwsIllegalArgumentException() {
        String invalidPropertyType = "";
        assertThrows(IllegalArgumentException.class, () -> new PropertyType(invalidPropertyType));
    }

    @Test
    public void isValidPropertyType() {
        // null property type
        assertThrows(NullPointerException.class, () -> PropertyType.isValidPropertyType(null));

        // invalid property types
        assertFalse(PropertyType.isValidPropertyType("")); // empty string
        assertFalse(PropertyType.isValidPropertyType(" ")); // spaces only

        // valid property types
        assertTrue(Address.isValidAddress("condominium"));
        assertTrue(Address.isValidAddress("hdb 3-room flat"));
        assertTrue(Address.isValidAddress("5 room flat hdb (furnished)"));
    }

    @Test
    public void equals() {
        PropertyType propertyType = new PropertyType("Valid property type");

        // same values -> returns true
        assertTrue(propertyType.equals(new PropertyType("Valid property type")));

        // same object -> returns true
        assertTrue(propertyType.equals(propertyType));

        // null -> returns false
        assertFalse(propertyType.equals(null));

        // different types -> returns false
        assertFalse(propertyType.equals(5.0f));

        // different values -> returns false
        assertFalse(propertyType.equals(new Address("Other valid property type")));
    }
}

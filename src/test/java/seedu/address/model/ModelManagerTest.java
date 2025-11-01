package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.GuiSettings;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.testutil.AddressBookBuilder;
import seedu.address.testutil.PersonBuilder;

public class ModelManagerTest {

    private ModelManager modelManager = new ModelManager();

    @Test
    public void constructor() {
        assertEquals(new UserPrefs(), modelManager.getUserPrefs());
        assertEquals(new GuiSettings(), modelManager.getGuiSettings());
        assertEquals(new AddressBook(), new AddressBook(modelManager.getAddressBook()));
    }

    @Test
    public void setUserPrefs_nullUserPrefs_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setUserPrefs(null));
    }

    @Test
    public void setUserPrefs_validUserPrefs_copiesUserPrefs() {
        UserPrefs userPrefs = new UserPrefs();
        userPrefs.setAddressBookFilePath(Paths.get("address/book/file/path"));
        userPrefs.setGuiSettings(new GuiSettings(1, 2, 3, 4));
        modelManager.setUserPrefs(userPrefs);
        assertEquals(userPrefs, modelManager.getUserPrefs());

        // Modifying userPrefs should not modify modelManager's userPrefs
        UserPrefs oldUserPrefs = new UserPrefs(userPrefs);
        userPrefs.setAddressBookFilePath(Paths.get("new/address/book/file/path"));
        assertEquals(oldUserPrefs, modelManager.getUserPrefs());
    }

    @Test
    public void setGuiSettings_nullGuiSettings_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setGuiSettings(null));
    }

    @Test
    public void setGuiSettings_validGuiSettings_setsGuiSettings() {
        GuiSettings guiSettings = new GuiSettings(1, 2, 3, 4);
        modelManager.setGuiSettings(guiSettings);
        assertEquals(guiSettings, modelManager.getGuiSettings());
    }

    @Test
    public void setAddressBookFilePath_nullPath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setAddressBookFilePath(null));
    }

    @Test
    public void setAddressBookFilePath_validPath_setsAddressBookFilePath() {
        Path path = Paths.get("address/book/file/path");
        modelManager.setAddressBookFilePath(path);
        assertEquals(path, modelManager.getAddressBookFilePath());
    }

    @Test
    public void hasPerson_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.hasPerson(null));
    }

    @Test
    public void hasPerson_personNotInAddressBook_returnsFalse() {
        assertFalse(modelManager.hasPerson(ALICE));
    }

    @Test
    public void hasPerson_personInAddressBook_returnsTrue() {
        modelManager.addPerson(ALICE);
        assertTrue(modelManager.hasPerson(ALICE));
    }

    @Test
    public void getFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> modelManager.getFilteredPersonList().remove(0));
    }

    @Test
    public void equals() {
        AddressBook addressBook = new AddressBookBuilder().withPerson(ALICE).withPerson(BENSON).build();
        AddressBook differentAddressBook = new AddressBook();
        UserPrefs userPrefs = new UserPrefs();

        // same values -> returns true
        modelManager = new ModelManager(addressBook, userPrefs);
        ModelManager modelManagerCopy = new ModelManager(addressBook, userPrefs);
        assertTrue(modelManager.equals(modelManagerCopy));

        // same object -> returns true
        assertTrue(modelManager.equals(modelManager));

        // null -> returns false
        assertFalse(modelManager.equals(null));

        // different types -> returns false
        assertFalse(modelManager.equals(5));

        // different addressBook -> returns false
        assertFalse(modelManager.equals(new ModelManager(differentAddressBook, userPrefs)));

        // different filteredList -> returns false
        String[] keywords = ALICE.getName().fullName.split("\\s+");
        modelManager.updateFilteredPersonList(new NameContainsKeywordsPredicate(Arrays.asList(keywords)));
        assertFalse(modelManager.equals(new ModelManager(addressBook, userPrefs)));

        // resets modelManager to initial state for upcoming tests
        modelManager.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        // different userPrefs -> returns false
        UserPrefs differentUserPrefs = new UserPrefs();
        differentUserPrefs.setAddressBookFilePath(Paths.get("differentFilePath"));
        assertFalse(modelManager.equals(new ModelManager(addressBook, differentUserPrefs)));
    }

    @Test
    public void sortFilteredPersonListByName_sortsAlphabetically() {
        AddressBook addressBook = new AddressBookBuilder()
                .withPerson(BENSON)
                .withPerson(ALICE)
                .build();
        ModelManager modelManager = new ModelManager(addressBook, new UserPrefs());

        assertEquals("Benson Meier", modelManager.getFilteredPersonList().get(0).getName().fullName);
        modelManager.sortFilteredPersonListByName();
        assertEquals("Alice Pauline", modelManager.getFilteredPersonList().get(0).getName().fullName);
        assertEquals("Benson Meier", modelManager.getFilteredPersonList().get(1).getName().fullName);
    }

    @Test
    public void sortFilteredPersonListByName_sortsDuplicatesByOtherFields() {
        Person alice1 = new PersonBuilder(ALICE).withPhone("11111111").withEmail("a@example.com").build();
        Person alice2 = new PersonBuilder(ALICE).withPhone("22222222").withEmail("b@example.com").build();
        Person alice3 = new PersonBuilder(ALICE).withPhone("22222222").withEmail("a@example.com").build();
        Person benson1 = new PersonBuilder(BENSON).withAddress("123 Alpha Street").build();
        Person benson2 = new PersonBuilder(BENSON).withAddress("456 Beta Avenue").build();

        AddressBook addressBook = new AddressBookBuilder()
                .withPerson(alice2)
                .withPerson(benson2)
                .withPerson(alice3)
                .withPerson(benson1)
                .withPerson(alice1)
                .build();
        ModelManager modelManager = new ModelManager(addressBook, new UserPrefs());

        modelManager.sortFilteredPersonListByName();

        assertEquals(alice1, modelManager.getFilteredPersonList().get(0));
        assertEquals(alice3, modelManager.getFilteredPersonList().get(1));
        assertEquals(alice2, modelManager.getFilteredPersonList().get(2));
        assertEquals(benson1, modelManager.getFilteredPersonList().get(3));
        assertEquals(benson2, modelManager.getFilteredPersonList().get(4));

    }

    @Test
    public void sortFilteredPersonListByName_sortsByPriceWhenOtherFieldsIdentical() {
        Person alice1 = new PersonBuilder(ALICE).withPrice("500000").build();
        Person alice2 = new PersonBuilder(ALICE).withPrice("1000000").build();
        Person alice3 = new PersonBuilder(ALICE).withPrice("750000").build();
        Person alice4 = new PersonBuilder(ALICE).withPrice("600,000").build(); // sort by commas
        Person alice5 = new PersonBuilder(ALICE).withPrice("2,000,000").build();

        AddressBook addressBook = new AddressBookBuilder()
                .withPerson(alice2)
                .withPerson(alice4)
                .withPerson(alice3)
                .withPerson(alice5)
                .withPerson(alice1)
                .build();
        ModelManager modelManager = new ModelManager(addressBook, new UserPrefs());

        modelManager.sortFilteredPersonListByName();

        assertEquals(alice1, modelManager.getFilteredPersonList().get(0));
        assertEquals(alice4, modelManager.getFilteredPersonList().get(1));
        assertEquals(alice3, modelManager.getFilteredPersonList().get(2));
        assertEquals(alice2, modelManager.getFilteredPersonList().get(3));
        assertEquals(alice5, modelManager.getFilteredPersonList().get(4));
    }

    @Test
    public void sortFilteredPersonListByName_sortsByPropertyTypeWhenPriceIdentical() {
        Person alice1 = new PersonBuilder(ALICE).withPropertyType("hdb").build();
        Person alice2 = new PersonBuilder(ALICE).withPropertyType("condo").build();
        Person alice3 = new PersonBuilder(ALICE).withPropertyType("landed").build();
        Person alice4 = new PersonBuilder(ALICE).withPropertyType("condominium").build();
        Person alice5 = new PersonBuilder(ALICE).withPropertyType("HDB3").build();

        AddressBook addressBook = new AddressBookBuilder()
                .withPerson(alice1)
                .withPerson(alice2)
                .withPerson(alice3)
                .withPerson(alice4)
                .withPerson(alice5)
                .build();
        ModelManager modelManager = new ModelManager(addressBook, new UserPrefs());

        modelManager.sortFilteredPersonListByName();

        assertEquals(alice2, modelManager.getFilteredPersonList().get(0));
        assertEquals(alice4, modelManager.getFilteredPersonList().get(1));
        assertEquals(alice1, modelManager.getFilteredPersonList().get(2));
        assertEquals(alice5, modelManager.getFilteredPersonList().get(3));
        assertEquals(alice3, modelManager.getFilteredPersonList().get(4));
    }
}

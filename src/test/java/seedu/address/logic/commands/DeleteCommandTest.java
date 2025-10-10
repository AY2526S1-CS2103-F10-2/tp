package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code DeleteCommand}.
 */
public class DeleteCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validIndexUnfilteredList_success() {
        Person personToDelete = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        DeleteCommand deleteCommand = new DeleteCommand(INDEX_FIRST_PERSON);

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS,
                Messages.format(personToDelete));

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(personToDelete);

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        DeleteCommand deleteCommand = new DeleteCommand(outOfBoundIndex);

        assertCommandFailure(deleteCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person personToDelete = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        DeleteCommand deleteCommand = new DeleteCommand(INDEX_FIRST_PERSON);

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS,
                Messages.format(personToDelete));

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(personToDelete);
        showNoPerson(expectedModel);

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Index outOfBoundIndex = INDEX_SECOND_PERSON;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getPersonList().size());

        DeleteCommand deleteCommand = new DeleteCommand(outOfBoundIndex);

        assertCommandFailure(deleteCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validName_success() {
        Person personToDelete = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        DeleteCommand deleteCommand = new DeleteCommand(personToDelete.getName());

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS,
                Messages.format(personToDelete));

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(personToDelete);

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidName_throwsCommandException() {
        Name invalidName = new Name("Nonexistent Person");
        DeleteCommand deleteCommand = new DeleteCommand(invalidName);

        assertCommandFailure(deleteCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        DeleteCommand deleteFirstCommand = new DeleteCommand(INDEX_FIRST_PERSON);
        DeleteCommand deleteSecondCommand = new DeleteCommand(INDEX_SECOND_PERSON);
        DeleteCommand deleteByNameCommand = new DeleteCommand(new Name("Alice Pauline"));

        // same object -> returns true
        assertTrue(deleteFirstCommand.equals(deleteFirstCommand));

        // same values -> returns true
        DeleteCommand deleteFirstCommandCopy = new DeleteCommand(INDEX_FIRST_PERSON);
        assertTrue(deleteFirstCommand.equals(deleteFirstCommandCopy));

        // different types -> returns false
        assertFalse(deleteFirstCommand.equals(1));

        // null -> returns false
        assertFalse(deleteFirstCommand.equals(null));

        // different person -> returns false
        assertFalse(deleteFirstCommand.equals(deleteSecondCommand));

        // different target type -> returns false
        assertFalse(deleteFirstCommand.equals(deleteByNameCommand));
    }

    @Test
    public void hashCodeMethod() {
        DeleteCommand deleteFirstCommand = new DeleteCommand(INDEX_FIRST_PERSON);
        DeleteCommand deleteFirstCommandCopy = new DeleteCommand(INDEX_FIRST_PERSON);
        DeleteCommand deleteByNameCommand = new DeleteCommand(new Name("Alice Pauline"));

        // same values -> same hashcode
        assertEquals(deleteFirstCommand.hashCode(), deleteFirstCommandCopy.hashCode());

        // different values -> different hashcode
        assertNotEquals(deleteFirstCommand.hashCode(), deleteByNameCommand.hashCode());
    }

    @Test
    public void hashCodeMethod_multipleNames() {
        Person firstPerson = model.getFilteredPersonList().get(0);
        Person secondPerson = model.getFilteredPersonList().get(1);
        List<Name> names1 = Arrays.asList(firstPerson.getName(), secondPerson.getName());
        List<Name> names2 = Arrays.asList(firstPerson.getName(), secondPerson.getName());
        DeleteCommand deleteCommand1 = new DeleteCommand(names1, true);
        DeleteCommand deleteCommand2 = new DeleteCommand(names2, true);
        DeleteCommand deleteCommand3 = new DeleteCommand(names1, false);

        // same values -> same hashcode
        assertEquals(deleteCommand1.hashCode(), deleteCommand2.hashCode());
        // different confirmation -> different hashcode
        assertNotEquals(deleteCommand1.hashCode(), deleteCommand3.hashCode());
    }

    @Test
    public void toStringMethod() {
        Index targetIndex = Index.fromOneBased(1);
        DeleteCommand deleteCommand = new DeleteCommand(targetIndex);
        String expected = DeleteCommand.class.getCanonicalName() + "{targetType=INDEX, target=" + targetIndex + "}";
        assertEquals(expected, deleteCommand.toString());
    }

    @Test
    public void toStringMethod_multipleNames() {
        Person firstPerson = model.getFilteredPersonList().get(0);
        Person secondPerson = model.getFilteredPersonList().get(1);
        List<Name> names = Arrays.asList(firstPerson.getName(), secondPerson.getName());
        DeleteCommand deleteCommand = new DeleteCommand(names, true);
        String expected = DeleteCommand.class.getCanonicalName()
                + "{targetType=MULTIPLE_NAMES, targets=" + names + ", confirmed=true}";
        assertEquals(expected, deleteCommand.toString());
    }

    @Test
    public void execute_multipleValidNames_success() throws Exception {
        // Get the first two persons from the typical address book
        Person firstPerson = model.getFilteredPersonList().get(0);
        Person secondPerson = model.getFilteredPersonList().get(1);

        List<Name> namesToDelete = Arrays.asList(firstPerson.getName(), secondPerson.getName());
        DeleteCommand deleteCommand = new DeleteCommand(namesToDelete, true); // confirmed

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_MULTIPLE_PERSONS_SUCCESS,
                2, Messages.format(firstPerson) + ", " + Messages.format(secondPerson));

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(firstPerson);
        expectedModel.deletePerson(secondPerson);

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_multipleValidNamesWithoutConfirmation_showsConfirmationPrompt() throws Exception {
        // Get the first two persons from the typical address book
        Person firstPerson = model.getFilteredPersonList().get(0);
        Person secondPerson = model.getFilteredPersonList().get(1);

        List<Name> namesToDelete = Arrays.asList(firstPerson.getName(), secondPerson.getName());
        DeleteCommand deleteCommand = new DeleteCommand(namesToDelete, false); // not confirmed

        String expectedMessage = String.format(DeleteCommand.MESSAGE_CONFIRM_DELETE_MULTIPLE,
                2, Messages.format(firstPerson) + ", " + Messages.format(secondPerson));

        // Model should remain unchanged - no deletion should occur
        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_singleNameInMultipleList_deletesSuccessfully() throws Exception {
        Person firstPerson = model.getFilteredPersonList().get(0);

        List<Name> namesToDelete = Arrays.asList(firstPerson.getName());
        // single deletion doesn't need confirmation
        DeleteCommand deleteCommand = new DeleteCommand(namesToDelete, false);

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS,
                Messages.format(firstPerson));

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(firstPerson);

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_multipleNamesWithNonExistentName_throwsCommandException() {
        Person firstPerson = model.getFilteredPersonList().get(0);
        Name nonExistentName = new Name("Non Existent Person");

        List<Name> namesToDelete = Arrays.asList(firstPerson.getName(), nonExistentName);
        DeleteCommand deleteCommand = new DeleteCommand(namesToDelete, true);

        assertCommandFailure(deleteCommand, model, "The following persons were not found: Non Existent Person");
    }

    @Test
    public void execute_allNonExistentNames_throwsCommandException() {
        Name nonExistentName1 = new Name("Non Existent Person 1");
        Name nonExistentName2 = new Name("Non Existent Person 2");

        List<Name> namesToDelete = Arrays.asList(nonExistentName1, nonExistentName2);
        DeleteCommand deleteCommand = new DeleteCommand(namesToDelete, true);

        assertCommandFailure(deleteCommand, model,
                "The following persons were not found: Non Existent Person 1, Non Existent Person 2");
    }

    @Test
    public void equals_multipleNames() {
        Person firstPerson = model.getFilteredPersonList().get(0);
        Person secondPerson = model.getFilteredPersonList().get(1);

        List<Name> names1 = Arrays.asList(firstPerson.getName(), secondPerson.getName());
        List<Name> names2 = Arrays.asList(firstPerson.getName(), secondPerson.getName());
        List<Name> names3 = Arrays.asList(firstPerson.getName());

        DeleteCommand deleteCommand1 = new DeleteCommand(names1, true);
        DeleteCommand deleteCommand2 = new DeleteCommand(names2, true);
        DeleteCommand deleteCommand3 = new DeleteCommand(names1, false);
        DeleteCommand deleteCommand4 = new DeleteCommand(names3, true);

        // same object -> returns true
        assertTrue(deleteCommand1.equals(deleteCommand1));

        // same values -> returns true
        assertTrue(deleteCommand1.equals(deleteCommand2));

        // different confirmation -> returns false
        assertFalse(deleteCommand1.equals(deleteCommand3));

        // different names -> returns false
        assertFalse(deleteCommand1.equals(deleteCommand4));

        // null -> returns false
        assertFalse(deleteCommand1.equals(null));

        // different types -> returns false
        assertFalse(deleteCommand1.equals(1));
    }

    /**
     * Updates {@code model}'s filtered list to show no one.
     */
    private void showNoPerson(Model model) {
        model.updateFilteredPersonList(p -> false);

        assertTrue(model.getFilteredPersonList().isEmpty());
    }
}

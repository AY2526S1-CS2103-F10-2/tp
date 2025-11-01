package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;

/**
 * Contains integration tests (interaction with the Model) and unit tests for ListCommand.
 */
public class ListCommandTest {

    private Model model;
    private Model expectedModel;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_listIsNotFiltered_showsSameList() {
        int expectedCount = model.getFilteredPersonList().size();
        String expectedMessage = String.format(ListCommand.MESSAGE_SUCCESS, expectedCount);
        assertCommandSuccess(new ListCommand(), model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_listIsFiltered_showsEverything() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        ListCommand listCommand = new ListCommand();
        expectedModel.updateFilteredPersonList(Model.PREDICATE_SHOW_ALL_PERSONS);
        expectedModel.sortFilteredPersonListByName();
        int expectedSize = expectedModel.getFilteredPersonList().size();
        String expectedMessage = String.format(ListCommand.MESSAGE_SUCCESS, expectedSize);
        CommandTestUtil.assertCommandSuccess(listCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_listIsSortedAlphabetically_showsAlphabeticalList() {
        ListCommand listCommand = new ListCommand();
        expectedModel.updateFilteredPersonList(Model.PREDICATE_SHOW_ALL_PERSONS);
        expectedModel.sortFilteredPersonListByName();
        int expectedSize = expectedModel.getFilteredPersonList().size();
        String expectedMessage = String.format(ListCommand.MESSAGE_SUCCESS, expectedSize);
        CommandTestUtil.assertCommandSuccess(listCommand, expectedModel, expectedMessage, expectedModel);
    }
}

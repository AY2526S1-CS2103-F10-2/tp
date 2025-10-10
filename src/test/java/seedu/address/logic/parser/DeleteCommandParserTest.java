package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.DeleteCommand;
import seedu.address.model.person.Name;

/**
 * As we are only doing white-box testing, our test cases do not cover path
 * variations outside of the DeleteCommand code. For example, inputs "1" and "1
 * abc" take the same path through the DeleteCommand, and therefore we test only
 * one of them. The path variation for those two cases occur inside the
 * ParserUtil, and therefore should be covered by the ParserUtilTest.
 */
public class DeleteCommandParserTest {

    private DeleteCommandParser parser = new DeleteCommandParser();

    @Test
    public void parse_validArgs_returnsDeleteCommand() {
        assertParseSuccess(parser, "1", new DeleteCommand(INDEX_FIRST_PERSON));
    }

    @Test
    public void parse_validNameArgs_returnsDeleteCommand() {
        assertParseSuccess(parser, " n/Alice Pauline", new DeleteCommand(new Name("Alice Pauline")));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        assertParseFailure(parser, "a", String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_invalidNameArgsWithPreamble_throwsParseException() {
        assertParseFailure(parser, "extra n/Alice Pauline",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_multipleValidNames_returnsDeleteCommand() {
        // Two names
        assertParseSuccess(parser, " n/Alice Pauline n/Bob Charlie",
                new DeleteCommand(Arrays.asList(new Name("Alice Pauline"), new Name("Bob Charlie")), false));

        // Three names
        assertParseSuccess(parser, " n/Alice n/Bob n/Charlie",
                new DeleteCommand(Arrays.asList(new Name("Alice"), new Name("Bob"), new Name("Charlie")), false));
    }

    @Test
    public void parse_multipleValidNamesWithConfirmation_returnsDeleteCommand() {
        assertParseSuccess(parser, " n/Alice Pauline n/Bob Charlie confirm/yes",
                new DeleteCommand(Arrays.asList(new Name("Alice Pauline"), new Name("Bob Charlie")), true));
    }

    @Test
    public void parse_singleNameWithValidConfirmation_returnsSingleDeleteCommand() {
        // Single name with confirmation should still create single name delete command
        assertParseSuccess(parser, " n/Alice Pauline confirm/yes",
                new DeleteCommand(new Name("Alice Pauline")));
    }

    @Test
    public void parse_invalidConfirmationValue_throwsParseException() {
        assertParseFailure(parser, " n/Alice n/Bob confirm/no",
                "Confirmation value must be 'yes' or leave it empty");
    }

    @Test
    public void parse_confirmationWithIndex_throwsParseException() {
        assertParseFailure(parser, "1 confirm/yes",
                "Confirmation is not required for deletion by index");
    }

    @Test
    public void parse_emptyNameValue_throwsParseException() {
        assertParseFailure(parser, " n/ n/Bob",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_duplicateNames_returnsDeleteCommand() {
        // Should allow duplicate names in input - the command will handle deduplication if needed
        assertParseSuccess(parser, " n/Alice n/Alice",
                new DeleteCommand(Arrays.asList(new Name("Alice"), new Name("Alice")), false));
    }
}

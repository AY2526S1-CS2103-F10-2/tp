package seedu.address.logic.parser;

import org.junit.jupiter.api.Test;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import seedu.address.logic.commands.DeleteCommand;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import seedu.address.model.person.Name;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

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
}

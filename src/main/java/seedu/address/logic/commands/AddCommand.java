package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INTENTION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PRICE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PROPERTY_TYPE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Adds a person to the address book.
 */
public class AddCommand extends Command {

    public static final String COMMAND_WORD = "add";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a person to the address book. "
            + "Parameters: "
            + PREFIX_INTENTION + "INTENTION(sell|rent) "
            + PREFIX_NAME + "NAME "
            + PREFIX_PHONE + "PHONE "
            + PREFIX_EMAIL + "EMAIL "
            + PREFIX_ADDRESS + "ADDRESS "
            + PREFIX_PROPERTY_TYPE + "PROPERTY TYPE "
            + PREFIX_PRICE + "PRICE "
            + "[" + PREFIX_TAG + "TAG]...\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_INTENTION + "sell "
            + PREFIX_NAME + "John Doe "
            + PREFIX_PHONE + "98765432 "
            + PREFIX_EMAIL + "johnd@example.com "
            + PREFIX_ADDRESS + "311, Clementi Ave 2, #02-25 "
            + PREFIX_PROPERTY_TYPE + "hdb 3-room flat "
            + PREFIX_PRICE + "450000 "
            + PREFIX_TAG + "friends "
            + PREFIX_TAG + "owesMoney";

    public static final String MESSAGE_SUCCESS = "New person added: %1$s";
    public static final String MESSAGE_DUPLICATE_NAME = "New person added: %1$s \n"
        + "WARNING: A person with the same name already exists in the address book";
    public static final String MESSAGE_DUPLICATE_ADDRESS = "New person added: %1$s \n"
            + "WARNING: A person with the same address already exists in the address book";
    public static final String MESSAGE_DUPLICATE_NAME_AND_ADDRESS = "New person added: %1$s \n"
            + "WARNING: A person with the same name and a person with the same address"
            + "already exists in the address book";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the address book";

    private final Person toAdd;

    /**
     * Creates an AddCommand to add the specified {@code Person}
     */
    public AddCommand(Person person) {
        requireNonNull(person);
        toAdd = person;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (model.hasPerson(toAdd)) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }

        if (model.hasName(toAdd) && model.hasAddress(toAdd)) {
            model.addPerson(toAdd);
            return new CommandResult(String.format(MESSAGE_DUPLICATE_NAME_AND_ADDRESS, Messages.format(toAdd)));
        } else if (model.hasName(toAdd)) {
            model.addPerson(toAdd);
            return new CommandResult(String.format(MESSAGE_DUPLICATE_NAME, Messages.format(toAdd)));
        } else if (model.hasAddress(toAdd)) {
            model.addPerson(toAdd);
            return new CommandResult(String.format(MESSAGE_DUPLICATE_ADDRESS, Messages.format(toAdd)));
        } else {
            model.addPerson(toAdd);
            return new CommandResult(String.format(MESSAGE_SUCCESS, Messages.format(toAdd)));
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddCommand)) {
            return false;
        }

        AddCommand otherAddCommand = (AddCommand) other;
        return toAdd.equals(otherAddCommand.toAdd);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("toAdd", toAdd)
                .toString();
    }
}

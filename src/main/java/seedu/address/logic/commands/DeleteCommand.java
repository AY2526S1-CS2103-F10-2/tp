package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;

/**
 * Deletes a person identified using it's displayed index or name from the
 * address book.
 */
public class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the person identified by the index number used in the displayed person list "
            + "or by name. Multiple names can be deleted at once.\n"
            + "Parameters: INDEX (must be a positive integer) or n/NAME [n/NAME]...\n"
            + "Example: " + COMMAND_WORD + " 1\n"
            + "Example: " + COMMAND_WORD + " n/John Doe\n"
            + "Example: " + COMMAND_WORD + " n/John Doe n/Mary Jane";

    public static final String MESSAGE_DELETE_PERSON_SUCCESS = "Deleted Person: %1$s";
    public static final String MESSAGE_DELETE_MULTIPLE_PERSONS_SUCCESS = "Deleted %1$d persons: %2$s";
    public static final String MESSAGE_CONFIRM_DELETE_MULTIPLE = "This will delete %1$d persons. "
            + "Please confirm by adding 'confirm/yes' to your command.\n"
            + "Persons to be deleted: %2$s";

    private enum TargetType {
        INDEX, NAME, MULTIPLE_NAMES
    }

    private final TargetType targetType;
    private final Index targetIndex;
    private final Name targetName;
    private final List<Name> targetNames;
    private final boolean isConfirmed;

    /**
     * Creates a DeleteCommand to delete the person at the specified index.
     */
    public DeleteCommand(Index targetIndex) {
        this.targetType = TargetType.INDEX;
        this.targetIndex = targetIndex;
        this.targetName = null;
        this.targetNames = null;
        this.isConfirmed = false;
    }

    /**
     * Creates a DeleteCommand to delete the person with the specified name.
     */
    public DeleteCommand(Name targetName) {
        this.targetType = TargetType.NAME;
        this.targetIndex = null;
        this.targetName = targetName;
        this.targetNames = null;
        this.isConfirmed = false;
    }

    /**
     * Creates a DeleteCommand to delete multiple persons with the specified
     * names.
     */
    public DeleteCommand(List<Name> targetNames, boolean isConfirmed) {
        this.targetType = TargetType.MULTIPLE_NAMES;
        this.targetIndex = null;
        this.targetName = null;
        this.targetNames = new ArrayList<>(targetNames);
        this.isConfirmed = isConfirmed;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        return switch (targetType) {
        case INDEX ->
            executeDeleteByIndex(model);
        case NAME ->
            executeDeleteByName(model);
        case MULTIPLE_NAMES ->
            executeDeleteMultipleNames(model);
        };
    }

    private CommandResult executeDeleteByIndex(Model model) throws CommandException {
        List<Person> lastShownList = model.getFilteredPersonList();
        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }
        Person personToDelete = lastShownList.get(targetIndex.getZeroBased());
        model.deletePerson(personToDelete);
        return new CommandResult(String.format(MESSAGE_DELETE_PERSON_SUCCESS, Messages.format(personToDelete)));
    }

    private CommandResult executeDeleteByName(Model model) throws CommandException {
        Person personToDelete = model.getAddressBook().getPersonList().stream()
                .filter(person -> person.getName().equals(targetName))
                .findFirst()
                .orElseThrow(() -> new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX));
        model.deletePerson(personToDelete);
        return new CommandResult(String.format(MESSAGE_DELETE_PERSON_SUCCESS, Messages.format(personToDelete)));
    }

    private CommandResult executeDeleteMultipleNames(Model model) throws CommandException {
        // Find all persons that match the target names
        List<Person> personsToDelete = new ArrayList<>();
        List<Name> notFoundNames = new ArrayList<>();

        for (Name name : targetNames) {
            Person person = model.getAddressBook().getPersonList().stream()
                    .filter(p -> p.getName().equals(name))
                    .findFirst()
                    .orElse(null);

            if (person != null) {
                personsToDelete.add(person);
            } else {
                notFoundNames.add(name);
            }
        }

        // If some names were not found, throw an exception
        if (!notFoundNames.isEmpty()) {
            String notFoundNamesStr = notFoundNames.stream()
                    .map(Name::toString)
                    .collect(Collectors.joining(", "));
            throw new CommandException("The following persons were not found: " + notFoundNamesStr);
        }

        // If no confirmation provided and multiple persons to delete, ask for confirmation
        if (!isConfirmed && personsToDelete.size() > 1) {
            String personsStr = personsToDelete.stream()
                    .map(Messages::format)
                    .collect(Collectors.joining(", "));
            return new CommandResult(String.format(MESSAGE_CONFIRM_DELETE_MULTIPLE,
                    personsToDelete.size(), personsStr));
        }

        // Delete all persons
        for (Person person : personsToDelete) {
            model.deletePerson(person);
        }

        // Return appropriate success message
        if (personsToDelete.size() == 1) {
            return new CommandResult(String.format(MESSAGE_DELETE_PERSON_SUCCESS,
                    Messages.format(personsToDelete.get(0))));
        } else {
            String personsStr = personsToDelete.stream()
                    .map(Messages::format)
                    .collect(Collectors.joining(", "));
            return new CommandResult(String.format(MESSAGE_DELETE_MULTIPLE_PERSONS_SUCCESS,
                    personsToDelete.size(), personsStr));
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DeleteCommand)) {
            return false;
        }

        DeleteCommand otherDeleteCommand = (DeleteCommand) other;
        return targetType == otherDeleteCommand.targetType
                && (targetIndex == null ? otherDeleteCommand.targetIndex == null
                        : targetIndex.equals(otherDeleteCommand.targetIndex))
                && (targetName == null ? otherDeleteCommand.targetName == null
                        : targetName.equals(otherDeleteCommand.targetName))
                && (targetNames == null ? otherDeleteCommand.targetNames == null
                        : targetNames.equals(otherDeleteCommand.targetNames))
                && isConfirmed == otherDeleteCommand.isConfirmed;
    }

    @Override
    public int hashCode() {
        return switch (targetType) {
        case INDEX ->
            targetIndex.hashCode();
        case NAME ->
            targetName.hashCode();
        case MULTIPLE_NAMES ->
            targetNames.hashCode() + Boolean.hashCode(isConfirmed);
        };
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this)
                .add("targetType", targetType);

        switch (targetType) {
        case INDEX:
            builder.add("target", targetIndex);
            break;
        case NAME:
            builder.add("target", targetName);
            break;
        case MULTIPLE_NAMES:
            builder.add("targets", targetNames).add("confirmed", isConfirmed);
            break;
        default:
            throw new AssertionError("Unknown target type: " + targetType);
        }

        return builder.toString();
    }
}

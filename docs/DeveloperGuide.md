---
  layout: default.md
  title: "Developer Guide"
  pageNav: 3
---

# 🏠 PropertyPal Developer Guide

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

* This project is based on the AddressBook-Level3 project created by the [SE-EDU initiative](https://se-education.org).
* Libraries used: [JavaFX](https://openjfx.io/), [Jackson](https://github.com/FasterXML/jackson), [JUnit5](https://github.com/junit-team/junit5)

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

### Architecture

<puml src="diagrams/ArchitectureDiagram.puml" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

Given below is a quick overview of main components and how they interact with each other.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/AY2526S1-CS2103-F10-2/tp/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/AY2526S1-CS2103-F10-2/tp/tree/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete 1`.

<puml src="diagrams/ArchitectureSequenceDiagram.puml" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* implements its functionality using a concrete `{Component Name}Manager` class (which follows the corresponding API `interface` mentioned in the previous point.

For example, the `Logic` component defines its API in the `Logic.java` interface and implements its functionality using the `LogicManager.java` class which follows the `Logic` interface. Other components interact with a given component through its interface rather than the concrete class (reason: to prevent outside component's being coupled to the implementation of a component), as illustrated in the (partial) class diagram below.

<puml src="diagrams/ComponentManagers.puml" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/AY2526S1-CS2103-F10-2/tp/tree/master/src/main/java/seedu/address/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `PersonListPanel`, `StatusBarFooter` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class which captures the commonalities between classes that represent parts of the visible GUI.

The `UI` component uses the JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the [`MainWindow`](https://github.com/AY2526S1-CS2103-F10-2/tp/tree/master/src/main/java/seedu/address/ui/MainWindow.java) is specified in [`MainWindow.fxml`](https://github.com/AY2526S1-CS2103-F10-2/tp/tree/master/src/main/resources/view/MainWindow.fxml)

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component, as it displays `Person` object residing in the `Model`.

### Logic component

**API** : [`Logic.java`](https://github.com/AY2526S1-CS2103-F10-2/tp/tree/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<puml src="diagrams/LogicClassDiagram.puml" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1")` API call as an example.

<puml src="diagrams/DeleteSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `delete 1` Command" />

<box type="info" seamless>

**Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</box>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, it is passed to an `AddressBookParser` object which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a person).<br>
   Note that although this is shown as a single step in the diagram above (for simplicity), in the code it can take several interactions (between the command object and the `Model`) to achieve.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned back from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<puml src="diagrams/ParserClasses.puml" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name e.g., `AddCommandParser`) which uses the other classes shown above to parse the user command and create a `XYZCommand` object (e.g., `AddCommand`) which the `AddressBookParser` returns back as a `Command` object.
* All `XYZCommandParser` classes (e.g., `AddCommandParser`, `DeleteCommandParser`, ...) inherit from the `Parser` interface so that they can be treated similarly where possible e.g, during testing.

### Model component
**API** : [`Model.java`](https://github.com/AY2526S1-CS2103-F10-2/tp/tree/master/src/main/java/seedu/address/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" width="450" />


The `Model` component,

* stores the address book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* stores the currently 'selected' `Person` objects (e.g., results of a search query) as a separate _filtered_ list which is exposed to outsiders as an unmodifiable `ObservableList<Person>` that can be 'observed' e.g. the UI can be bound to this list so that the UI automatically updates when the data in the list change.
* stores a `UserPref` object that represents the user’s preferences. This is exposed to the outside as a `ReadOnlyUserPref` objects.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

<box type="info" seamless>

**Note:** In this model, the AddressBook manages a single list of Person objects through a UniquePersonList. Each Person stores their own attributes (such as name, phone, email, address, price, property type, and intention).<br>

<puml src="diagrams/BetterModelClassDiagram.puml" width="450" />

</box>


### Storage component

**API** : [`Storage.java`](https://github.com/AY2526S1-CS2103-F10-2/tp/tree/master/src/main/java/seedu/address/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="550" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* inherits from both `AddressBookStorage` and `UserPrefStorage`, which means it can be treated as either one (if only the functionality of only one is needed).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### `OR` search in `find` command

#### Implementation

The find command supports searching across multiple fields (e.g., name, phone, email, address, price, etc.) using `OR` semantics. 
This behaviour is implemented in the `PersonContainsKeywordsPredicate` class, which defines how each `Person` is tested against the given search keywords.
<br>
<br>
When the user enters a `find` command such as:
```
find n/Alice e/gmail
```
the system constructs a `PersonContainsKeywordsPredicate` containing separate lists of keywords for each prefix (`n/` → name, `e/` → email).
The predicate then evaluates to `true` if any of the person’s fields contain any of the corresponding keywords.
This is done through a series of `anyMatch` calls and a final ``||`` chain:
```java
return nameMatches || phoneMatches || emailMatches || addressMatches
        || priceMatches || propertyTypeMatches || intentionMatches;
```
#### Rationale for `OR` search
1. Intuitive user experience
    1. Users typically expect inclusive search results and typing multiple keywords should broaden the search, not restrict it.
    2. For example, searching `find n/Alice e/gmail` should return both:
        1. Alice Tan (name contains "Alice")
        2. Bob Lee (email contains "gmail")
2. Consistency with common search mode
    1. Search behaviour in file explorers, contact apps, and web engines generally follows `OR` semantics.
       Matching this mental model makes the feature easier to use and reduces confusion.
3. Flexibility in partial recall
    1. Users may not remember exact details (e.g., only the email domain or property type).
       `OR` semantics let them find relevant entries even when they recall only part of the information.
4. Performance and maintainability
   1. The predicate short-circuits on the first match, improving performance.
      The modular field checks also make it straightforward to add or remove searchable fields in the future.

--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, configuration, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [Configuration guide](Configuration.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Proposed future features**

### \[Proposed\] Undo/redo feature

#### Proposed Implementation

The proposed undo/redo mechanism is facilitated by `VersionedAddressBook`. It extends `AddressBook` with an undo/redo history, stored internally as an `addressBookStateList` and `currentStatePointer`. Additionally, it implements the following operations:

* `VersionedAddressBook#commit()` — Saves the current address book state in its history.
* `VersionedAddressBook#undo()` — Restores the previous address book state from its history.
* `VersionedAddressBook#redo()` — Restores a previously undone address book state from its history.

These operations are exposed in the `Model` interface as `Model#commitAddressBook()`, `Model#undoAddressBook()` and `Model#redoAddressBook()` respectively.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedAddressBook` will be initialized with the initial address book state, and the `currentStatePointer` pointing to that single address book state.

<puml src="diagrams/UndoRedoState0.puml" alt="UndoRedoState0" />

Step 2. The user executes `delete 5` command to delete the 5th person in the address book. The `delete` command calls `Model#commitAddressBook()`, causing the modified state of the address book after the `delete 5` command executes to be saved in the `addressBookStateList`, and the `currentStatePointer` is shifted to the newly inserted address book state.

<puml src="diagrams/UndoRedoState1.puml" alt="UndoRedoState1" />

Step 3. The user executes `add n/David …​` to add a new person. The `add` command also calls `Model#commitAddressBook()`, causing another modified address book state to be saved into the `addressBookStateList`.

<puml src="diagrams/UndoRedoState2.puml" alt="UndoRedoState2" />

<box type="info" seamless>

**Note:** If a command fails its execution, it will not call `Model#commitAddressBook()`, so the address book state will not be saved into the `addressBookStateList`.

</box>

Step 4. The user now decides that adding the person was a mistake, and decides to undo that action by executing the `undo` command. The `undo` command will call `Model#undoAddressBook()`, which will shift the `currentStatePointer` once to the left, pointing it to the previous address book state, and restores the address book to that state.

<puml src="diagrams/UndoRedoState3.puml" alt="UndoRedoState3" />


<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index 0, pointing to the initial AddressBook state, then there are no previous AddressBook states to restore. The `undo` command uses `Model#canUndoAddressBook()` to check if this is the case. If so, it will return an error to the user rather
than attempting to perform the undo.

</box>

The following sequence diagram shows how an undo operation goes through the `Logic` component:

<puml src="diagrams/UndoSequenceDiagram-Logic.puml" alt="UndoSequenceDiagram-Logic" />

<box type="info" seamless>

**Note:** The lifeline for `UndoCommand` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline reaches the end of diagram.

</box>

Similarly, how an undo operation goes through the `Model` component is shown below:

<puml src="diagrams/UndoSequenceDiagram-Model.puml" alt="UndoSequenceDiagram-Model" />

The `redo` command does the opposite — it calls `Model#redoAddressBook()`, which shifts the `currentStatePointer` once to the right, pointing to the previously undone state, and restores the address book to that state.

<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index `addressBookStateList.size() - 1`, pointing to the latest address book state, then there are no undone AddressBook states to restore. The `redo` command uses `Model#canRedoAddressBook()` to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.

</box>

Step 5. The user then decides to execute the command `list`. Commands that do not modify the address book, such as `list`, will usually not call `Model#commitAddressBook()`, `Model#undoAddressBook()` or `Model#redoAddressBook()`. Thus, the `addressBookStateList` remains unchanged.

<puml src="diagrams/UndoRedoState4.puml" alt="UndoRedoState4" />

Step 6. The user executes `clear`, which calls `Model#commitAddressBook()`. Since the `currentStatePointer` is not pointing at the end of the `addressBookStateList`, all address book states after the `currentStatePointer` will be purged. Reason: It no longer makes sense to redo the `add n/David …​` command. This is the behavior that most modern desktop applications follow.

<puml src="diagrams/UndoRedoState5.puml" alt="UndoRedoState5" />

The following activity diagram summarizes what happens when a user executes a new command:

<puml src="diagrams/CommitActivityDiagram.puml" width="250" />

#### Design considerations:

**Aspect: How undo & redo executes:**

* **Alternative 1 (current choice):** Saves the entire address book.
    * Pros: Easy to implement.
    * Cons: May have performance issues in terms of memory usage.

* **Alternative 2:** Individual command knows how to undo/redo by
  itself.
    * Pros: Will use less memory (e.g. for `delete`, just save the person being deleted).
    * Cons: We must ensure that the implementation of each individual command are correct.

_{more aspects and alternatives to be added}_

### \[Proposed\] Find by Area Feature (Postal Code Prefix)

#### Proposed Implementation

The proposed feature extends the existing `find` command to allow users to search for persons by postal code prefix using the pc/ prefix.
The implementation adds a new `PostalCode` field to the `Person` class.
For example, the command `find pc/64` will list all persons whose postal code begins with “64”.

Internally, the feature flows through the following components:

1. `LogicManager` receives the command text and delegates parsing to `AddressBookParser`.

2. `AddressBookParser` uses `FindCommandParser`, which tokenizes input arguments and constructs a `PersonContainsKeywordsPredicate` for the postal code prefix.

3. `FindCommand` is created with the predicate and returned to `LogicManager`.

4. `LogicManager` executes the command with the `Model`, which updates the filtered person list by testing each person with the predicate.

5. A `CommandResult` is created and returned to `LogicManager`.


This flow is illustrated in the sequence diagram below:

<puml src="diagrams/FindByPostalCodeSequenceDiagram-Logic.puml" alt="FindByPostalCodeSequenceDiagram-Logic" />

<box type="info" seamless>

**Note:** The lifeline for `FindCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</box>

#### Design considerations:

**Aspect: How area filtering works:**

* **Alternative 1 (current choice):** Filter by prefix matching (e.g. first 2 characters of the postal code).
* Pros: Simple to implement and directly maps to postal district prefixes.
* Cons: May be less precise for smaller subzones.

* **Alternative 2:** Use an external postal code–to–region lookup table.
* Pros: Enables filtering by named region.
* Cons: Requires maintaining additional data and mappings.

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**:

* real estate agents who manage a large number of client contacts
* frequently need to search or filter clients by specific details (e.g., name, phone)
* prefers typing to mouse interactions

**Value proposition**:

* allows efficient searching and filtering of clients by any field (name, phone, email, address)
* enables quick addition, editing, and deletion of client contact details


### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …​           | I want to …​                        | So that I can…​                                                 |
|----------|-------------------|-------------------------------------|-----------------------------------------------------------------|
| `* * *`  | real estate agent | add a new client’s contact details  | manage potential sellers                                        |
| `* * *`  | real estate agent | delete a client's contact           | remove entries that I no longer need                            |
| `* * *`  | real estate agent | view a list of all clients          | get an overview of my client database                           |
| `* * *`  | real estate agent | find a client by any field          | locate a client even if I only recall part of their details     |
| `* *`    | real estate agent | sort clients alphabetically by name | locate a client more easily                                     |
| `* *`    | real estate agent | filter contacts by intention        | group and manage contacts based on relationship type or purpose |

*{More to be added}*

### Use cases

(For all use cases below, the **System** is `PropertyPal` and the **Actor** is the `user (real estate agent)`, unless specified otherwise)

**Use case 1: Add a client contact**

**Guarantees:**
* Client details are saved if all fields are valid and at least 1 field is unique.
* No duplicates will be created.

**MSS**

1.  User enters the add command with all required details in a single line.
2.  PropertyPal adds the new client and displays a success message.

    Use case ends.

**Extensions**

* 1a. Invalid input (e.g. wrong format)

    * 1a1. PropertyPal displays an error message indicating the correct format.
    * 1a2. User re-enters data.

        Steps 1a1 - 1a2 are repeated until the input entered is valid.

        Use case resumes from step 2.


* 1b. Duplicate client detected

    * 1b1. PropertyPal displays a message indicating client already exists.

      Use case ends.

* 1c. Duplicate name or address detected, but at least 1 other detail is unique

    * 1c1. PropertyPal adds the new client and displays a success message, with a warning that a contact with the same name or address is detected.

      Use case ends.


**Use case 2: Delete a client contact**

**Guarantees:**
* The specified client is removed from the list if a valid name is provided.
* No deletion occurs without user confirmation when multiple matches exist.

**MSS**

1.  User enters delete command with the full name of the client.
2.  If exactly one match is found, PropertyPal deletes the client and displays a success message.

    Use case ends.

**Extensions**

* 1a. No matching record found

    * 1a1. PropertyPal displays a message indicating no such record found.

      Use case ends.


* 1b. Multiple matches found

    * 1b1. PropertyPal lists all matching clients with indices.
    * 1b2. User enters the index of the client to delete.
    * 1b3. PropertyPal deletes the client and displays a success message.

      Use case ends.


* 1c. Invalid input (e.g. empty name, invalid characters).

    * 1c1. PropertyPal displays an error message indicating the correct format.
    * 1c2. User re-enters data.

        Steps 1c1 - 1c2 are repeated until the input entered is valid.

        Use case resumes from step 2.


**Use case 3: List all client contacts**

**MSS**

1.  User enters list command.
2.  PropertyPal displays a list of all clients in lexicographical (alphabetical and numerical) order and a success message.

    Use case ends.

**Extensions**

* 1a. No clients stored

    * 1a1. PropertyPal displays an empty table and a success message.

      Use case ends.


* 1b. Invalid input (e.g. typo)

    * 1b1. PropertyPal displays an error message indicating that the command is not recognized.

      Use case ends.


**Use case 4: Find clients by specific field(s)**

**Guarantees:**
* PropertyPal displays all clients that match the given search keyword(s).
* Each matching client is displayed only once, even if it matches multiple criteria.

**MSS**

1.  User enters find command with keyword(s).
2.  PropertyPal displays a list of all matching clients in the order they were added.

    Use case ends.

**Extensions**

* 1a. Invalid input (e.g. missing prefix or keyword, input too short)

    * 1a1. PropertyPal displays an error message indicating the correct format.
    * 1a2. User re-enters data.

        Steps 1a1 - 1a2 are repeated until the input entered is valid.

        Use case resumes from step 2.


* 1b. No matches found

    * 1b1. PropertyPal displays a message indicating no matching clients found.

      Use case ends.


**Use Case 5: Edit a client contact**

**Guarantees:**
* The specified client's details are updated if all provided fields are valid.
* No duplicate clients are created as a result of the edit operation.

**MSS**

1.  User requests to edit a specific client by index and provides one or more fields to update .
2.  PropertyPal updates the client's details and displays a success message.

    Use case ends.

**Extensions**

* 1a. Invalid index (e.g. -1 or index is larger than total number of clients)

    * 1a1. PropertyPal displays an error message indicating invalid index.
    * 1a2. User re-enters data.

      Steps 1a1 - 1a2 are repeated until the input entered is valid.

      Use case resumes from step 2.


* 1b. No fields are provided for editing

    * 1b1. PropertyPal displays an error message indicating that at least one field must be provided.
    * 1b2. User re-enters data.

      Steps 1b1 - 1b2 are repeated until the input entered is valid.

      Use case resumes from step 2.

* 1c. Edited details would create a duplicate client (all fields identical to an existing client)

    * 1c1. PropertyPal displays an error message indicating that the client already exists.
    * 1c2. User re-enters data.

      Steps 1c1 - 1c2 are repeated until the input entered is valid.

      Use case resumes from step 2.

* 1d. Invalid input (e.g. missing index)

    * 1d1. PropertyPal displays an error message indicating the correct format.
    * 1d2. User re-enters data.

      Steps 1d1 - 1d2 are repeated until the input entered is valid.

      Use case resumes from step 2.


### Non-Functional Requirements

1.  Should work on any _mainstream OS_ as long as it has Java `17` or above installed.
2.  Should be able to hold up to 1000 persons without a noticeable sluggishness in performance for typical usage.
3.  A user with above average typing speed for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse.
4.  Should be able to start up quickly within a few seconds on a typical computer.
5.  Should be able to run on laptops with 4GB RAM and above.
6.  Error messages should be easy to understand by an average user.
7.  Change in user preferences should be persisted across sessions.
8.  All features work without internet access.

### Glossary

* **Mainstream OS**: An operating system that is widely used, actively maintained, and has substantial market share and community support. These systems typically receive regular security updates and support running modern Java applications. (e.g. Windows, Linux, Unix, macOS)
* **Private contact detail**: A contact detail that is not meant to be shared with others
* **Command**: A text instruction entered by the user (e.g., add, delete) to perform an action.
* **CLI**: Command Line Interface
* **GUI**: Graphical User Interface
* **JavaFx**: A Java library for building GUI applications
* **FXML**: An XML-based language for defining the layout of JavaFx GUIs
* **Argument/Parameter**: Extra information provided with a command (e.g., `n/John Doe` in add).
* **Field**: A specific data component of a client record, such as “name”, “email”, or “phone number”.
* **Prefix**: A short identifier (e.g., n/, p/, e/, a/) used to indicate the type of information in a command.
* **Duplicate entry**: A contact record that has the same intention, name, phone, email, address, property type, and price as another existing record.
* **Intention**: Refers to the client's property transaction goal (either `sell` or `rent`). This field helps categorize clients based on their property-related objectives.
* **Property Type**: Describes the type of property a client is trying to sell/rent (e.g. hdb, condo etc.)
* **UniquePersonList**: A custom list implementation used within the `Model` to store `Person` objects, ensuring all entries are unique based on defined criteria.
* **VersionedAddressBook**: A proposed future extension of the `AddressBook` class that supports undo/redo functionality by maintaining a history of address book states.
* **Postal Code**: A short series of letters and/or numbers assigned to a specific geographic area.

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<box type="info" seamless>

**Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</box>

### Launch and shutdown

1. Initial launch

   1. Download the JAR file and copy into an empty folder

   1. Open a terminal in the location of the JAR file and run `java -jar "PropertyPal.jar"`<br>
   Expected: Shows the GUI with a set of sample contacts. The window size may not be optimum.

1. Saving window preferences

   1. Resize the window to an optimum size. Move the window to a different location. Close the window.

   2. Re-launch the app by re-doing step `1.ii`.<br>
       Expected: The most recent window size and location is retained.

### Deleting a person

1. Deleting a person while all persons are being shown

   1. Prerequisites: List all persons using the `list` command. Multiple persons in the list.

   1. Test case: `delete 1`<br>
      Expected: First contact is deleted from the list. Details of the deleted contact shown in the status message. Timestamp in the status bar is updated.

   1. Test case: `delete 0`<br>
      Expected: No person is deleted. Error details shown in the status message. Status bar remains the same.

   1. Other incorrect delete commands to try: `delete`, `delete x`, `...` (where x is larger than the list size)<br>
      Expected: Similar to previous.

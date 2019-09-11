package duke.utils;

import duke.exceptions.DukeException;
import duke.commands.Command;
import duke.commands.DeleteCommand;
import duke.commands.SaveCommand;
import duke.commands.AddCommand;
import duke.commands.ListCommand;
import duke.commands.DoneCommand;
import duke.commands.FindCommand;
import duke.commands.HelpCommand;

import java.util.Scanner;
import java.util.ArrayList;

/**
 * Class used to parse all the string input coming from the user, manage error handling
 * and generate Commands that can then be invoked via their .execute() methods to trigger certain
 * behaviours. This class is meant as an abstraction to abstract away the details and complexities
 * behind parsing using input.
 */
public class Parser {
    /**
     * Parses the entire line of user input and generates a Command object.
     *
     * @param fullCommand entire line of user input
     * @return Command object whose .execute() method can be called to achieve some desired behaviour
     * @throws DukeException thrown when an unrecognised command is encountered.
     */
    public static Command parse(String fullCommand) throws DukeException {
        Scanner sc = new Scanner(fullCommand);
        String command = sc.next();
        switch (command.toLowerCase()) {
        case "save":
            return new SaveCommand();
        case "list":
            return new ListCommand();
        case "todo":
            return new AddCommand(parseToDo(sc.nextLine()));
        case "event":
            return new AddCommand(parseEvent(sc.nextLine()));
        case "deadline":
            return new AddCommand(parseDeadline(sc.nextLine()));
        case "done":
            return new DoneCommand(sc.nextInt());
        case "delete":
            return new DeleteCommand(sc.nextInt());
        case "find":
            return new FindCommand(sc.nextLine());
        case "help":
            return new HelpCommand();
        default:
            throw new DukeException("Unrecognised command: " + command);
        }
    }

    /**
     * Method to parse the parameters of the ToDo object.
     *
     * @param remainingParams remaining parameters required to initialise ToDo object
     * @return ArrayList containing the processed parameters, ready to be passed to a Command object
     * @throws DukeException thrown when task description is empty/invalid.
     */
    public static ArrayList<String> parseToDo(String remainingParams) throws DukeException {
        String taskDescription = remainingParams.trim();
        if (taskDescription.length() == 0) {
            throw new DukeException("Task Description is empty!");
        } else {
            ArrayList<String> commandParams = new ArrayList<String>();
            commandParams.add("todo");
            commandParams.add(taskDescription);
            return commandParams;
        }
    }

    /**
     * Method to parse the parameters of the Event object.
     *
     * @param remainingParams remaining parameters required to initialise Event object
     * @return ArrayList containing the processed paramters, ready to be passed to a Command object
     * @throws DukeException thrown when event parameters are invalid.
     */
    public static ArrayList<String> parseEvent(String remainingParams) throws DukeException {
        try {
            String[] strArr = remainingParams.split("/at");
            String taskDescription = strArr[0].trim();
            String duration = strArr[1].trim();
            String[] durationArr = duration.split("-");

            if (taskDescription.length() == 0 || durationArr.length == 0) {
                throw new DukeException("Event is of invalid form!");
            }

            ArrayList<String> commandParams = new ArrayList<String>();
            commandParams.add("event");
            commandParams.add(taskDescription);
            commandParams.add(durationArr[0].trim());
            commandParams.add(durationArr[1].trim());

            return commandParams;
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new DukeException("Event is of invalid form!");
        }
    }

    /**
     * Method to parse the parameters of the Deadline object.
     *
     * @param remainingParams remaining parameters required to initialise Deadline object
     * @return ArrayList containing the processed parameters, ready to be passed to a Command object
     * @throws DukeException thrown when deadline parameters are invalid.
     */
    public static ArrayList<String> parseDeadline(String remainingParams) throws DukeException {
        try {
            String[] strArr = remainingParams.split("/by");
            String taskDescription = strArr[0].trim();
            String deadline = strArr[1].trim();

            if (taskDescription.length() == 0 || deadline.length() == 0) {
                throw new DukeException("Deadline is of invalid form!");
            }

            ArrayList<String> commandParams = new ArrayList<String>();
            commandParams.add("deadline");
            commandParams.add(taskDescription);
            commandParams.add(deadline);

            return commandParams;
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new DukeException("Deadline is of invalid form!");
        }
    }
}

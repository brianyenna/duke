import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Scanner;
import java.util.ArrayList;

public class Duke {
    public static final String saveFilePath = "data/savedTasks.txt";
    private ArrayList<Task> storedTasks;
    private Storage s;

    public Duke() {
        this.s = new Storage(Duke.saveFilePath);
        try {
            this.storedTasks = this.s.load();
        }
        catch (FileNotFoundException e) {
            System.out.println("No saved tasks were found. Initialising with empty TaskList");
            this.storedTasks = new ArrayList<Task>();
        }
        catch (ParseException e) {
            System.out.println("Unable to parse date string: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Duke d = new Duke();

        d.initDuke();
        d.runDuke();
        d.terminateDuke();
    }

    /**
     * Method to be run the Duke object. This method is in charge of taking in input from the console
     * and dealing with the different commands.
     */
    public void runDuke() {
        Scanner sc = new Scanner(System.in);
        boolean contRunning = true;

        while (contRunning) {
            String command = sc.next();
            try {
                switch (command) {
                    case "bye":
                        contRunning = false;
                        break;
                    case "list":
                        listStoredTasks();
                        break;
                    case "done":
                        completeTask(sc.nextInt());
                        break;
                    case "todo":
                        addToDo(sc.nextLine());
                        break;
                    case "deadline":
                        addDeadline(sc.nextLine());
                        break;
                    case "event":
                        addEvent(sc.nextLine());
                        break;
                    case "delete":
                        deleteTask(sc.nextInt());
                        break;
                    default:
                        //Provided input is a task
                        throw new DukeException("\u2639" + " OOPS!!! I'm sorry, but I don't know what that means :-(");
                }
            }
            catch (DukeException e) {
                printErrorMessage(e);
            }

        }
    }

    /**
     * For printing out the exception to console
     * @param e DukeException class containing an error message that will be printed to the console
     */
    public void printErrorMessage(DukeException e) {
        System.out.println("\t____________________________________________________________");
        System.out.println("\t" + e.getMessage());
        System.out.println("\t____________________________________________________________");
    }

    public void saveUpdatedTasks() {
        try {
            this.s.saveToFile(this.storedTasks);
        }
        catch (IOException e) {
            System.out.println("\u2639" + " OOPS!!! Seems like there was a problem saving your updated list!");
        }
    }

    /**
     * Method to delete a task from the storedTasks ArrayList
     * Method is invoked when "delete" is input to console
     * @param taskNum
     */
    public void deleteTask(int taskNum) throws DukeException{
        try {
            Task t = this.storedTasks.get(taskNum-1);
            this.storedTasks.remove(taskNum-1);

            System.out.println("\t____________________________________________________________");
            System.out.println("\tNoted. I've removed this task:");
            System.out.println("\t\t" + t);
            System.out.println("\tNow you have " + this.storedTasks.size() + " tasks in the list.");
            System.out.println("\t____________________________________________________________");

            saveUpdatedTasks();
        }
        catch (IndexOutOfBoundsException e) {
            throw new DukeException("\u2639" + " OOPS!!! The task number is invalid!");
        }
    }

    /**
     * Method to add a ToDo object
     * @param taskDescription the string containing the description of the task
     */
    public void addToDo(String taskDescription) throws DukeException{
        if (taskDescription.trim().length() == 0) {
            throw new DukeException("\u2639" + " OOPS!!! The description of a todo cannot be empty.");
        }

        Task t = new ToDo(taskDescription.trim());
        this.storedTasks.add(t);
        printAddTaskOutput(t);
        saveUpdatedTasks();
    }

    /**
     * Method to add a Deadline object
     * @param taskDescriptionwithDeadline the string containing the description and deadline
     *                                    of the task (unsplit). Will split using "/by" as
     *                                    delimiter
     */
    public void addDeadline(String taskDescriptionwithDeadline) throws DukeException{
        try {
            //If the input is missing /by, the input is of the wrong form
            String[] strArr = taskDescriptionwithDeadline.split("/by");
            String description = strArr[0].trim();
            String deadline = strArr[1].trim();

            //Either description or duration is missing, but /by is possibly present
            if (description.length() == 0 || deadline.length() == 0) {
                throw new DukeException("\u2639" + " OOPS!!! The description/deadline of a deadline cannot be empty.");
            }

            Task t = new Deadline(description, deadline);
            this.storedTasks.add(t);
            printAddTaskOutput(t);
            saveUpdatedTasks();
        }
        catch (ArrayIndexOutOfBoundsException e) {
            throw new DukeException("\u2639" + " OOPS!!! The description/deadline of a deadline cannot be empty.");
        }
        catch (ParseException e) {
            throw new DukeException("\u2639" + " OOPS!!! The format of the deadline is wrong!");
        }
    }

    /**
     * Method to add an Event object.
     * @param taskDescriptionwithDuration the string containing the description and duration
     *                                    of the event (unsplit). Will split using "/at" as
     *                                    delimiter
     */
    public void addEvent(String taskDescriptionwithDuration) throws DukeException{
        try {
            //If the input is missing /at, the input is of the wrong form
            String[] strArr = taskDescriptionwithDuration.split("/at");
            String description = strArr[0].trim();
            String duration = strArr[1].trim();
            String[] durationArr = duration.split("-");

            //Either description or duration is missing, but /at is possibly present
            if (description.length() == 0 || duration.length() == 0) {
                throw new DukeException("\u2639" + " OOPS!!! The description/duration of a deadline cannot be empty.");
            }

            Task t = new Event(description, durationArr[0].trim(), durationArr[1].trim());
            this.storedTasks.add(t);
            printAddTaskOutput(t);
            saveUpdatedTasks();
        }
        catch (ArrayIndexOutOfBoundsException e) {
            throw new DukeException("\u2639" + " OOPS!!! The description/duration of an event cannot be empty.");
        }
        catch (ParseException e) {
            throw new DukeException("\u2639" + " OOPS!!! The format of the duration is wrong!");
        }
    }

    /**
     * Helper function to print output to console whenever a task is added (applies
     * to ToDos, Deadlines and Events)
     * @param t Can be a Deadline, Event or Todo object
     */
    public void printAddTaskOutput(Task t) {
        System.out.println("\t____________________________________________________________");
        System.out.println("\tGot it. I've added this task:");
        System.out.println("\t\t" + t);
        System.out.println("\tNow you have " + this.storedTasks.size() + " tasks in the list");
        System.out.println("\t____________________________________________________________");
    }

    /**
     * Method to be invoked whenever a task is completed.
     * Method is invoked when "done" is input to console
     * @param taskNum the number of the task as shown whenever `list` is typed in the console
     */
    public void completeTask(int taskNum) throws DukeException{
        try {
            Task t = this.storedTasks.get(taskNum - 1); //Because storedTasks is zero-indexed
            t.markAsDone();
            saveUpdatedTasks();

            System.out.println("\t____________________________________________________________");
            System.out.println("\tNice! I've marked this task as done: ");
            System.out.println("\t\t" + t);
            System.out.println("\t____________________________________________________________");
        }
        catch (IndexOutOfBoundsException e) {
            throw new DukeException("\u2639" + " OOPS!!! The task number is invalid!");
        }
    }

    /**
     * Method to be invoked to list all existing tasks
     * Method is invoked when "list" is input to console
     */
    public void listStoredTasks() {
        System.out.println("\t____________________________________________________________");
        System.out.println("\tHere are the tasks in your list:");

        int counter = 1;
        for (Task t : this.storedTasks) {
            System.out.println("\t" + counter + ". " + t);
            counter++;
        }

        System.out.println("\t____________________________________________________________");
    }

    /**
     * Initialisation method for Duke object. This is meant to encapsulate all the greetings
     * and other initialisations required in the future.
     */
    public void initDuke() {
        System.out.println("\t____________________________________________________________\n"
                          +"\tHello! I'm Duke\n"
                          +"\tWhat can I do for you?\n"
                          +"\t____________________________________________________________");
    }

    /**
     * A method to clean-up the Duke object when it is no longer needed. This is meant to
     * encapsulation all termination logic required now and in the future.
     */
    public void terminateDuke() {
        System.out.println("\t____________________________________________________________\n"
                          +"\tBye. Hope to see you again soon!\n"
                          +"\t____________________________________________________________");
    }
}

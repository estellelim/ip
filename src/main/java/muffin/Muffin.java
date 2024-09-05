package muffin;

import java.util.Scanner;

/**
 * The Muffin class represents the main application logic for a todo list.
 */
public class Muffin {

    /**
     * The file path to the task list file.
     */
    private final String filePath = "../taskList.txt";

    /**
     * Scanner object to capture user input.
     */
    private Scanner sc = new Scanner(System.in);

    /**
     * An instance of FileProcessor to handle reading and writing tasks to a file.
     */
    private FileProcessor fp = new FileProcessor();

    /**
     * An instance of Parser to parse user input commands.
     */
    private Parser parser = new Parser();

    /**
     * The TaskList object that stores the list of tasks read from the file.
     */
    private TaskList list = new TaskList(fp.readFromFile(filePath));

    /**
     * Enum representing the possible commands the user can input.
     */
    enum Command {
        BYE, LIST, MARK, UNMARK, DELETE, FIND, TODO, DEADLINE, EVENT
    }

    /**
     * Starts the Muffin application, displays the welcome message and
     * processes the user's commands.
     */
    public void run() {
        String logo = " __  __       __  __ _\n"
                + "|  \\/  |_  _ / _|/ _(_)_ _\n"
                + "| |\\/| | || |  _|  _| | ' \\\n"
                + "|_|  |_|\\_,_|_| |_| |_|_||_|\n";

        String helloMsg = "Hello~ I am Muffin\n"
                + "What can I do for you?";

        System.out.println(logo + "\n" + helloMsg);

        try {
            command();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        new Muffin().run();
    }

    /**
     * Processes user commands. This method continuously reads user input, parses the command,
     * and performs the corresponding action, such as adding a task, marking it as done,
     * or deleting it.
     *
     * @throws MuffinException if the user input is invalid or an unsupported command is given.
     */
    public void command() throws MuffinException {
        try {
            String userInput = sc.nextLine();
            int len = list.length();
            String[] parts = parser.parseInput(userInput);

            Command command;
            try {
                command = Command.valueOf(parts[0].toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new MuffinException("Um... Not sure what you mean...");
            }

            if (command == Command.MARK || command == Command.UNMARK || command == Command.DELETE) {
                try {
                    int index = Integer.parseInt(parts[1]);
                    if (index > len) {
                        throw new MuffinException("Oh no! There is only " + len + " tasks.");
                    } else if (index < 1) {
                        throw new MuffinException("Oh no! This task does not exist!");
                    }
                } catch (NumberFormatException e) {
                    throw new MuffinException("Oh no! You must state the number of the task you'd like to edit!");
                }
            }

            switch (command) {
            case BYE:
                System.out.println("Goodbye~ Hope to see you again soon!");
                break;

            case LIST:
                System.out.println("Here are the tasks in your list:");
                list.list();
                command();
                break;

            case MARK:
                Task t = list.mark(Integer.parseInt(parts[1]) - 1);
                System.out.println("Yay! Marked as done:\n" + "\t" + t);
                fp.writeToFile(filePath, list.getList());
                command();
                break;

            case UNMARK:
                Task s = list.unmark(Integer.parseInt(parts[1]) - 1);
                System.out.println("Ok. Marked as not done yet:\n" + "\t" + s);
                fp.writeToFile(filePath, list.getList());
                command();
                break;

            case DELETE:
                Task r = list.delete(Integer.parseInt(parts[1]) - 1);
                System.out.println("Ok. Task has been removed:\n" + "\t" + r);
                System.out.println("Now you have " + list.length() + " tasks in your list.");
                fp.writeToFile(filePath, list.getList());
                command();
                break;

            case FIND:
                System.out.println("Here are the matching tasks in your list:");
                list.find(parts[1]);
                command();
                break;

            case TODO:
                if (parts[1].isEmpty()) {
                    throw new MuffinException("Oh no! You must have a description for a todo task.");
                }
                list.add(new Todo(parts[1]));
                System.out.println("Ok. Added this task:\n" + "\t" + list.get(len));
                System.out.println("Now you have " + (len + 1) + " tasks in your list.");
                fp.writeToFile(filePath, list.getList());
                command();
                break;

            case DEADLINE:
                if (parts.length < 3) {
                    throw new MuffinException("Oh no! You must have a description and a deadline for a deadline task!");
                }
                list.add(new Deadline(parts[1], parts[2]));
                System.out.println("Ok. Added this task:\n" + "\t" + list.get(len));
                System.out.println("Now you have " + (len + 1) + " tasks in your list.");
                fp.writeToFile(filePath, list.getList());
                command();
                break;

            case EVENT:
                if (parts.length < 4) {
                    throw new MuffinException("Oh no! You must have a description and a timeframe for an event task!");
                }
                list.add(new Event(parts[1], parts[2], parts[3]));
                System.out.println("Ok. Added this task:\n" + "\t" + list.get(len));
                System.out.println("Now you have " + (len + 1) + " tasks in your list.");
                fp.writeToFile(filePath, list.getList());
                command();
                break;

            default:
                break;
            }
        } catch (MuffinException e) {
            System.out.println(e.getMessage());
            command();
        }
    }
}

package muffin;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.util.Duration;

/**
 * The Muffin class represents the main application logic for a todo list.
 */
public class Muffin {

    /**
     * An instance of Parser to parse user input commands.
     */
    private Parser parser = new Parser();

    /**
     * The TaskList object that stores the list of tasks read from the file.
     */
    private TaskList list = new TaskList();

    /**
     * Enum representing the possible commands the user can input.
     */
    enum Command {
        HI, HELLO, HEY, BYE, LIST, MARK, UNMARK, DELETE, FIND, TODO, DEADLINE, EVENT, UNDO
    }

    /**
     * Processes user commands. This method continuously reads user input, parses the command,
     * and performs the corresponding action, such as adding a task, marking it as done,
     * or deleting it.
     *
     * @param userInput The input string from the user, representing a command and its parameters.
     * @return A String that represents the result of executing the command.
     * @throws MuffinException If the command was of unexpected format.
     */
    public String command(String userInput) throws MuffinException {
        assert userInput != null;

        try {
            int len = list.length();
            String[] parts = parser.parseInput(userInput);

            Command command;
            try {
                command = Command.valueOf(parts[0].toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new MuffinException("Muffin does not understand this command :c");
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
            case HI, HEY, HELLO:
                return "Hi there! My name is Muffin!! \n How can I help you today?";

            case BYE:
                // Pause for 3 seconds before exiting
                PauseTransition pause = new PauseTransition(Duration.seconds(3));
                pause.setOnFinished(event -> Platform.exit()); // Close the app after the delay
                pause.play();
                return "Goodbye~ Hope to see you again soon!";

            case LIST:
                return String.format("Here are the tasks in your list: \n %s", list.list());

            case MARK:
                Task t = list.mark(Integer.parseInt(parts[1]) - 1);
                return String.format("Yay! Marked as done:\n \t %s", t);

            case UNMARK:
                Task s = list.unmark(Integer.parseInt(parts[1]) - 1);
                return String.format("Ok. Marked as not done yet:\n \t %s", s);

            case DELETE:
                Task r = list.delete(Integer.parseInt(parts[1]) - 1);
                return String.format("Ok. Task has been removed:\n \t %s \n "
                        + "Now you have %d tasks in your list.", r, list.length());

            case FIND:
                return String.format("Here are the matching tasks in your list: \n %s", list.find(parts[1]));

            case TODO:
                if (parts[1].isEmpty()) {
                    throw new MuffinException("Oh no! You must have a description for a todo task.");
                }
                list.add(len, new Todo(parts[1]));
                return String.format("Ok. Added this task:\n \t %s \n"
                        + "Now you have %d tasks in your list.", list.get(len), len + 1);

            case DEADLINE:
                String formatMsgDeadline = "Use this format: \n deadline do something /by 2024-12-31";

                if (parts.length < 3) {
                    throw new MuffinException("Oh no! You must have a description and a deadline for a deadline task! "
                            + formatMsgDeadline);
                }

                try {
                    LocalDate.parse(parts[2]);
                } catch (DateTimeParseException e) {
                    return "The date format for your task was wrong! " + formatMsgDeadline;
                }

                list.add(len, new Deadline(parts[1], parts[2]));
                return String.format("Ok. Added this task: \n \t %s \n"
                        + "Now you have %d tasks in your list.", list.get(len), len + 1);

            case EVENT:
                String formatMsgEvent = "Use this format: \n event do something /from 2024-12-01 /to 2024-12-31";
                if (parts.length < 4) {
                    throw new MuffinException("Oh no! You must have a description and a timeframe for an event task! "
                            + formatMsgEvent);
                }

                try {
                    LocalDate.parse(parts[2]);
                    LocalDate.parse(parts[3]);
                } catch (DateTimeParseException e) {
                    return "The date format for your task was wrong! " + formatMsgEvent;
                }

                list.add(len, new Event(parts[1], parts[2], parts[3]));
                return String.format("Ok. Added this task:\n \t %s \n"
                        + "Now you have %d tasks in your list.", list.get(len), len + 1);

            case UNDO:
                return list.undo();

            default:
                break;
            }
        } catch (MuffinException e) {
            return e.getMessage();
        }

        return "";
    }

    /**
     * Generates a response for the user's chat message.
     */
    public String getResponse(String input) {
        assert input != null;

        try {
            return command(input);
        } catch (MuffinException e) {
            System.out.println(e.getMessage());
        }

        return input;
    }
}

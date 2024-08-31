package muffin;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task with a deadline.
 * The task must be completed before the deadline.
 */
public class Deadline extends Task {

    /**
     * The date of the deadline of the event.
     */
    protected LocalDate by;

    /**
     * Constructs a Deadline with the specified description and deadline.
     *
     * @param description A brief description of the task.
     * @param by The date by which this task must be done.
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = LocalDate.parse(by);
    }

    @Override
    public String toString() {
        String date = by.format(DateTimeFormatter.ofPattern("E, MMM d yyyy"));
        return String.format("[D]%s (by %s)", super.toString(), date);
    }
}

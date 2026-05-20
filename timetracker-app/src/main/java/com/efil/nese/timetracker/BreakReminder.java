/**
 * @file BreakReminder.java
 * @brief This file handles break reminder functionality.
 * @details This file contains the BreakReminder class which provides methods
 *          to set and manage break reminders. Implements:
 *          - Queue for notification queuing
 *          - Stack for undo functionality
 */
package com.efil.nese.timetracker;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.Stack;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;

/**
 * @class BreakReminder
 * @brief This class handles break reminder operations.
 * @details The BreakReminder class provides methods to set break reminders,
 *          notify users when it's time to take a break, and manage break schedules.
 *          Uses Queue (FIFO) for reminder processing and Stack (LIFO) for undo functionality.
 * @author efil.saylam.nese.sarp
 * @version 1.0
 * @since 2024
 */
public class BreakReminder {

  /** @brief Logger instance for logging errors and debug information. */
  private static final Logger logger = (Logger) LoggerFactory.getLogger(BreakReminder.class);

  /** @brief Scanner instance for reading user input from console. */
  private Scanner scanner;

  /** @brief Queue (FIFO) for managing reminder notifications in order. */
  private Queue<Reminder> reminderQueue; // Queue for notification queuing

  /** @brief Stack (LIFO) for tracking reminder history and enabling undo functionality. */
  private Stack<Reminder> reminderHistory; // Stack for undo functionality

  /**
   * @brief Default constructor for BreakReminder class.
   * @details Initializes the Scanner for user input and creates empty Queue and Stack
   *          data structures for managing reminders. The queue is used for FIFO processing
   *          of reminders, while the stack enables undo functionality through LIFO access.
   * @note Both data structures are initialized as empty collections.
   */
  public BreakReminder() {
    this.scanner = new Scanner(System.in);
    this.reminderQueue = new LinkedList<>();
    this.reminderHistory = new Stack<>();
  }

  /**
   * @brief Main method to run the Break Reminder feature.
   * @details Displays the break reminder menu in a loop and handles user interactions.
   *          The menu offers options to set reminders (using queue), view reminders
   *          (using stack for undo), and return to the main menu. Validates user input
   *          and handles invalid choices gracefully. The loop continues until the user
   *          chooses to go back to the main menu.
   * @note Invalid input (non-numeric) is handled by setting choice to -1, which triggers
   *       the default case displaying an error message.
   * @see #setReminder()
   * @see #viewReminder()
   */
  public void run() {
    int choice;
    boolean back = false;

    while (!back) {
      System.out.println("\n========================================");
      System.out.println("         Break Reminder Menu             ");
      System.out.println("========================================");
      System.out.println("1. Set Reminder (Queue)");
      System.out.println("2. View Reminder (Stack/Undo)");
      System.out.println("3. Back to Main Menu");
      System.out.println("========================================");
      System.out.print("Enter your choice: ");

      try {
        choice = Integer.parseInt(scanner.nextLine().trim());
      } catch (NumberFormatException e) {
        choice = -1;
      }

      switch (choice) {
        case 1:
          setReminder();
          break;

        case 2:
          viewReminder();
          break;

        case 3:
          back = true;
          break;

        default:
          System.out.println("Invalid choice. Please try again.");
      }
    }
  }

  /**
   * @brief Sets a new reminder and adds it to the queue.
   * @details Prompts the user to enter a reminder message and interval. Validates
   *          the input and creates a new Reminder object. The reminder is added to
   *          both the queue (for FIFO processing) and the history stack (for undo
   *          functionality). Displays queue status information after successful creation.
   * @note Reminder message cannot be empty. Interval must be a positive integer.
   * @note The reminder is added to both queue and stack to enable both processing
   *       and undo operations.
   * @see #viewReminder()
   */
  private void setReminder() {
    System.out.println("\n=== Set Reminder (Queue) ===");
    System.out.print("Enter reminder message: ");
    String message = scanner.nextLine().trim();

    if (message.isEmpty()) {
      System.out.println("Reminder message cannot be empty!");
      waitForEnter();
      return;
    }

    System.out.print("Enter reminder interval (minutes): ");
    int interval;

    try {
      interval = Integer.parseInt(scanner.nextLine().trim());

      if (interval <= 0) {
        System.out.println("Interval must be positive!");
        waitForEnter();
        return;
      }
    } catch (NumberFormatException e) {
      System.out.println("Invalid interval!");
      waitForEnter();
      return;
    }

    // Create reminder
    Reminder reminder = new Reminder(message, interval);
    // Add to queue (FIFO - first in, first out)
    reminderQueue.offer(reminder);
    // Push to history stack for undo
    reminderHistory.push(reminder);
    System.out.println("\nReminder added to queue successfully!");
    System.out.println("Reminder: " + message);
    System.out.println("Interval: " + interval + " minutes");
    System.out.println("Queue position: " + reminderQueue.size());
    System.out.println("\nQueue Status:");
    System.out.println("  Total reminders in queue: " + reminderQueue.size());
    waitForEnter();
  }

  /**
   * @brief Views reminders and provides undo functionality using stack.
   * @details Displays a menu for viewing and managing reminders. Shows the current
   *          queue status, the next reminder to be processed, and the last reminder
   *          that can be undone. Provides options to process reminders from the queue,
   *          undo the last reminder, view all reminders in the queue, and view the
   *          reminder history stack. The stack enables LIFO-based undo operations.
   * @note If both queue and history are empty, displays an error message and returns.
   * @see #processNextReminder()
   * @see #undoLastReminder()
   * @see #viewQueue()
   * @see #viewHistory()
   */
  private void viewReminder() {
    if (reminderQueue.isEmpty() && reminderHistory.isEmpty()) {
      System.out.println("\nNo reminders found. Set some reminders first!");
      waitForEnter();
      return;
    }

    int choice;
    boolean back = false;

    while (!back) {
      System.out.println("\n=== View Reminder (Stack/Undo) ===");
      System.out.println("\nCurrent Queue Status:");
      System.out.println("  Reminders in queue: " + reminderQueue.size());
      System.out.println("  Reminders in history: " + reminderHistory.size());

      if (!reminderQueue.isEmpty()) {
        System.out.println("\nNext Reminder to Process:");
        Reminder next = reminderQueue.peek();
        System.out.println("  Message: " + next.message);
        System.out.println("  Interval: " + next.interval + " minutes");
      }

      if (!reminderHistory.isEmpty()) {
        System.out.println("\nLast Reminder Added (can be undone):");
        Reminder last = reminderHistory.peek();
        System.out.println("  Message: " + last.message);
        System.out.println("  Interval: " + last.interval + " minutes");
      }

      System.out.println("\nOptions:");
      System.out.println("1. Process next reminder from queue");
      System.out.println("2. Undo last reminder (remove from queue and history)");
      System.out.println("3. View all reminders in queue");
      System.out.println("4. View all reminders in history (stack)");
      System.out.println("5. Back to Break Reminder Menu");
      System.out.print("\nEnter your choice: ");

      try {
        choice = Integer.parseInt(scanner.nextLine().trim());
      } catch (NumberFormatException e) {
        choice = -1;
      }

      switch (choice) {
        case 1:
          processNextReminder();
          break;

        case 2:
          undoLastReminder();
          break;

        case 3:
          viewQueue();
          break;

        case 4:
          viewHistory();
          break;

        case 5:
          back = true;
          break;

        default:
          System.out.println("Invalid choice. Please try again.");
      }
    }
  }

  /**
   * @brief Processes the next reminder from the queue using FIFO order.
   * @details Removes and displays the next reminder from the front of the queue.
   *          This implements the queue's FIFO (First In, First Out) behavior where
   *          the oldest reminder is processed first. Displays reminder details and
   *          the remaining queue size after processing.
   * @note If the queue is empty, displays an error message and returns without processing.
   * @note The reminder is permanently removed from the queue after processing.
   */
  private void processNextReminder() {
    if (reminderQueue.isEmpty()) {
      System.out.println("\nQueue is empty! No reminders to process.");
      waitForEnter();
      return;
    }

    Reminder reminder = reminderQueue.poll();
    System.out.println("\n=== Processing Reminder ===");
    System.out.println("Message: " + reminder.message);
    System.out.println("Interval: " + reminder.interval + " minutes");
    System.out.println("\nReminder processed and removed from queue.");
    System.out.println("Remaining in queue: " + reminderQueue.size());
    waitForEnter();
  }

  /**
   * @brief Undoes the last reminder using stack (LIFO) behavior.
   * @details Implements undo functionality by removing the most recently added reminder
   *          from both the history stack and the queue. The stack's LIFO (Last In, First Out)
   *          property ensures that the most recent action is undone first. Searches the
   *          queue to find and remove the matching reminder if it exists there.
   * @note If the history stack is empty, displays an error message and returns.
   * @note The reminder is removed from both stack and queue to maintain consistency.
   * @note This operation cannot be undone itself (no redo functionality).
   */
  private void undoLastReminder() {
    if (reminderHistory.isEmpty()) {
      System.out.println("\nNo reminders to undo!");
      waitForEnter();
      return;
    }

    Reminder lastReminder = reminderHistory.pop();
    // Remove from queue if it exists there
    Queue<Reminder> tempQueue = new LinkedList<>();
    boolean found = false;

    while (!reminderQueue.isEmpty()) {
      Reminder r = reminderQueue.poll();

      if (!found && r.message.equals(lastReminder.message) &&
          r.interval == lastReminder.interval) {
        found = true;
        // Skip this one (undo it)
      } else {
        tempQueue.offer(r);
      }
    }

    reminderQueue = tempQueue;
    System.out.println("\n=== Undo Last Reminder ===");
    System.out.println("Removed reminder:");
    System.out.println("  Message: " + lastReminder.message);
    System.out.println("  Interval: " + lastReminder.interval + " minutes");
    System.out.println("\nReminder undone successfully!");
    System.out.println("Remaining in queue: " + reminderQueue.size());
    System.out.println("Remaining in history: " + reminderHistory.size());
    waitForEnter();
  }

  /**
   * @brief Views all reminders in the queue in FIFO order.
   * @details Displays all reminders currently in the queue without removing them.
   *          The reminders are shown in the order they will be processed (FIFO).
   *          Each reminder is displayed with its message and interval. Shows the
   *          total count of reminders in the queue.
   * @note If the queue is empty, displays an appropriate message and returns.
   * @note This operation does not modify the queue - it is read-only.
   */
  private void viewQueue() {
    if (reminderQueue.isEmpty()) {
      System.out.println("\nQueue is empty!");
      waitForEnter();
      return;
    }

    System.out.println("\n=== All Reminders in Queue (FIFO) ===");
    System.out.println("Total: " + reminderQueue.size());
    System.out.println("----------------------------------------");
    int position = 1;

    for (Reminder reminder : reminderQueue) {
      System.out.println(position + ". Message: " + reminder.message);
      System.out.println("   Interval: " + reminder.interval + " minutes");
      System.out.println();
      position++;
    }

    waitForEnter();
  }

  /**
   * @brief Views all reminders in the history stack in LIFO order.
   * @details Displays all reminders currently in the history stack without removing them.
   *          The reminders are shown in reverse chronological order (most recent first)
   *          due to the stack's LIFO property. Creates a temporary copy of the stack
   *          to avoid modifying the original during display. Each reminder is shown
   *          with its message and interval.
   * @note If the history stack is empty, displays an appropriate message and returns.
   * @note This operation does not modify the stack - it uses a temporary copy for display.
   */
  private void viewHistory() {
    if (reminderHistory.isEmpty()) {
      System.out.println("\nHistory is empty!");
      waitForEnter();
      return;
    }

    System.out.println("\n=== All Reminders in History (Stack - LIFO) ===");
    System.out.println("Total: " + reminderHistory.size());
    System.out.println("(Most recent at top)");
    System.out.println("----------------------------------------");
    // Create a copy to view without modifying the stack
    Stack<Reminder> tempStack = new Stack<>();
    tempStack.addAll(reminderHistory);
    int position = 1;

    while (!tempStack.isEmpty()) {
      Reminder reminder = tempStack.pop();
      System.out.println(position + ". Message: " + reminder.message);
      System.out.println("   Interval: " + reminder.interval + " minutes");
      System.out.println();
      position++;
    }

    waitForEnter();
  }

  /**
   * @brief Waits for the user to press the Enter key.
   * @details Displays a prompt and blocks execution until the user presses Enter.
   *          This is used to pause execution and allow users to read messages before
   *          continuing. Any exceptions during input are silently ignored.
   * @note The method catches and ignores all exceptions to ensure execution continues
   *       even if there are input issues.
   */
  private void waitForEnter() {
    System.out.print("\nPress Enter to continue...");

    try {
      scanner.nextLine();
    } catch (Exception e) {
      // Ignore
    }
  }

  /**
   * @brief Closes the scanner resource and performs cleanup.
   * @details Closes the Scanner instance if it is not null. This method should be
   *          called when the BreakReminder instance is no longer needed to ensure
   *          proper resource cleanup.
   * @note This method is idempotent - it can be called multiple times safely.
   * @note Does not save any data - only closes the scanner resource.
   */
  public void close() {
    if (scanner != null) {
      scanner.close();
    }
  }

  /**
   * @class Reminder
   * @brief Represents a break reminder with message and interval.
   * @details This inner class encapsulates a single reminder with its message text
   *          and time interval. Reminders are stored in both the queue (for processing)
   *          and the stack (for undo functionality).
   * @author efil.saylam.nese.sarp
   */
  private static class Reminder {
    /** @brief The reminder message text to display to the user. */
    String message;

    /** @brief The time interval in minutes between reminder notifications. */
    int interval; // in minutes

    /**
     * @brief Constructor for Reminder class.
     * @param message The reminder message text. Should not be null or empty.
     * @param interval The time interval in minutes. Must be a positive integer.
     * @note Both parameters are required and should be validated before construction.
     */
    Reminder(String message, int interval) {
      this.message = message;
      this.interval = interval;
    }
  }
}

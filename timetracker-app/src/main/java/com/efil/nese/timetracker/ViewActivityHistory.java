/**
 * @file ViewActivityHistory.java
 * @brief This file handles viewing activity history functionality.
 * @details This file contains the ViewActivityHistory class which provides methods
 *          to view and browse past activity logs. Implements XOR checksum for
 *          data integrity verification during forward/backward navigation.
 */
package com.efil.nese.timetracker;

import java.util.List;
import java.util.Scanner;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;

/**
 * @class ViewActivityHistory
 * @brief This class handles viewing activity history operations.
 * @details The ViewActivityHistory class provides methods to view, filter,
 *          and search through past activity logs and history. Uses XOR checksum
 *          for data integrity verification during navigation. Provides interactive
 *          forward/backward navigation through activity history.
 * @author efil.saylam.nese.sarp
 * @version 1.0
 * @since 2024
 */
public class ViewActivityHistory {

  /** @brief Logger instance for logging errors and debug information. */
  private static final Logger logger = (Logger) LoggerFactory.getLogger(ViewActivityHistory.class);

  /** @brief Scanner instance for reading user input from console. */
  private Scanner scanner;

  /** @brief Reference to ActivityLogging instance for accessing activity data. */
  private ActivityLogging activityLogging;

  /**
   * @brief Constructor for ViewActivityHistory class.
   */
  public ViewActivityHistory() {
    this.scanner = new Scanner(System.in);
  }

  /**
   * @brief Constructor with ActivityLogging reference.
   * @param activityLogging Reference to ActivityLogging instance.
   */
  public ViewActivityHistory(ActivityLogging activityLogging) {
    this.scanner = new Scanner(System.in);
    this.activityLogging = activityLogging;
  }

  /**
   * @brief Sets the ActivityLogging reference.
   * @param activityLogging Reference to ActivityLogging instance.
   */
  public void setActivityLogging(ActivityLogging activityLogging) {
    this.activityLogging = activityLogging;
  }

  /**
   * @brief Main method to run the View Activity History feature.
   * @details Displays the activity history menu and handles user interactions.
   */
  public void run() {
    if (activityLogging == null) {
      System.out.println("\n=== View Activity History ===");
      System.out.println("Error: ActivityLogging not initialized.");
      System.out.println("Press Enter to return to main menu...");
      scanner.nextLine();
      return;
    }

    viewActivityHistory();
  }

  /**
   * @brief Views activity history with forward/backward navigation and XOR checksum verification.
   * @details Implements an interactive viewer for browsing through activity history.
   *          Users can navigate forward (D key), backward (A key), jump to first (F key),
   *          jump to last (L key), recalculate checksum (R key), or quit (Q key).
   *          The XOR checksum is calculated for all activities and verified after each
   *          navigation to ensure data integrity. Displays current activity details,
   *          position information, and checksum status.
   * @note If no activities exist, displays an error message and returns.
   * @note Navigation keys: A (backward), D (forward), F (first), L (last), R (recalc), Q (quit).
   * @see #calculateXORChecksum(List)
   * @see #verifyIntegrity(List, int)
   * @see #printActivity(Activity)
   */
  private void viewActivityHistory() {
    List<Activity> activities = activityLogging.getActivities();

    if (activities.isEmpty()) {
      System.out.println("\nNo activity history found. Start by logging some activities!");
      waitForEnter();
      return;
    }

    int currentIndex = 0;
    boolean navigating = true;
    // Calculate XOR checksum for all activities
    int xorChecksum = calculateXORChecksum(activities);

    while (navigating) {
      clearScreen();
      System.out.println("\n=== Activity History (XOR Checksum Verification) ===");
      System.out.println("Current Activity:");
      System.out.println("----------------------------------------");
      printActivity(activities.get(currentIndex));
      System.out.println("----------------------------------------");
      // Show position info
      System.out.println("\nPosition: [" + (currentIndex + 1) + " / " + activities.size() + "]");
      // Verify data integrity with XOR checksum
      int currentChecksum = calculateXORChecksum(activities);
      System.out.println("\nData Integrity Check:");
      System.out.println("  XOR Checksum: " + String.format("0x%08X", currentChecksum));
      // Show navigation hints
      System.out.println("\nNavigation:");

      if (currentIndex > 0) {
        System.out.println("  [A] or Left Arrow  : Previous activity (backward)");
      } else {
        System.out.println("  [A] or Left Arrow  : (Already at first)");
      }

      if (currentIndex < activities.size() - 1) {
        System.out.println("  [D] or Right Arrow : Next activity (forward)");
      } else {
        System.out.println("  [D] or Right Arrow : (Already at last)");
      }

      System.out.println("  [F]                : Go to first activity");
      System.out.println("  [L]                : Go to last activity");
      System.out.println("  [R]                : Recalculate XOR checksum");
      System.out.println("  [Q]                : Quit and return to main menu");
      System.out.print("\nEnter command: ");

      try {
        String input = scanner.nextLine().trim().toLowerCase();

        if (input.isEmpty()) {
          continue;
        }

        char key = input.charAt(0);

        switch (key) {
          case 'a': // Left - Previous (backward)
            if (currentIndex > 0) {
              currentIndex--;
              // Verify integrity after navigation
              verifyIntegrity(activities, xorChecksum);
            } else {
              System.out.println("\nAlready at the first activity!");
              waitForEnter();
            }

            break;

          case 'd': // Right - Next (forward)
            if (currentIndex < activities.size() - 1) {
              currentIndex++;
              // Verify integrity after navigation
              verifyIntegrity(activities, xorChecksum);
            } else {
              System.out.println("\nAlready at the last activity!");
              waitForEnter();
            }

            break;

          case 'f': // First
            currentIndex = 0;
            verifyIntegrity(activities, xorChecksum);
            break;

          case 'l': // Last
            currentIndex = activities.size() - 1;
            verifyIntegrity(activities, xorChecksum);
            break;

          case 'r': // Recalculate checksum
            xorChecksum = calculateXORChecksum(activities);
            System.out.println("\nXOR Checksum recalculated: " +
                               String.format("0x%08X", xorChecksum));
            waitForEnter();
            break;

          case 'q': // Quit
            navigating = false;
            break;

          default:
            System.out.println("\nInvalid key! Use A (back), D (forward), F (first), L (last), R (recalc), or Q (quit)");
            waitForEnter();
        }
      } catch (Exception e) {
        System.out.println("\nError: " + e.getMessage());
        waitForEnter();
      }
    }
  }

  /**
   * @brief Calculates XOR checksum for activity list to verify data integrity.
   * @param activities List of Activity objects to calculate checksum for. Must not be null.
   * @return XOR checksum value as a 32-bit integer. Returns 0 if the list is empty.
   * @details Computes a checksum by XORing hash codes of all relevant activity fields
   *          including ID, name, duration, timestamp, category, and description.
   *          The XOR operation provides a simple but effective way to detect changes
   *          in the data. Any modification to any activity will change the checksum.
   * @note The checksum is sensitive to any changes in activity data.
   * @note Timestamp is converted to int by taking the lower 32 bits.
   * @see #verifyIntegrity(List, int)
   */
  private int calculateXORChecksum(List<Activity> activities) {
    int checksum = 0;

    for (Activity activity : activities) {
      // XOR all relevant fields for integrity check
      checksum ^= activity.id;
      checksum ^= activity.name.hashCode();
      checksum ^= activity.duration;
      checksum ^= (int)(activity.timestamp & 0xFFFFFFFF);
      checksum ^= activity.category.hashCode();
      checksum ^= activity.description.hashCode();
    }

    return checksum;
  }

  /**
   * @brief Verifies data integrity using XOR checksum comparison.
   * @param activities List of Activity objects to verify. Must not be null.
   * @param expectedChecksum The expected checksum value to compare against.
   * @details Calculates the current checksum of the activities list and compares it
   *          with the expected value. If they don't match, displays a warning message
   *          indicating potential data corruption or modification. This provides a
   *          simple but effective integrity check.
   * @note A mismatch indicates that data may have been modified since the last checksum calculation.
   * @see #calculateXORChecksum(List)
   */
  private void verifyIntegrity(List<Activity> activities, int expectedChecksum) {
    int currentChecksum = calculateXORChecksum(activities);

    if (currentChecksum != expectedChecksum) {
      System.out.println("\n⚠ Warning: Data integrity check failed!");
      System.out.println("  Expected: " + String.format("0x%08X", expectedChecksum));
      System.out.println("  Current:  " + String.format("0x%08X", currentChecksum));
      System.out.println("  Data may have been modified!");
    }
  }

  /**
   * @brief Clears the screen by printing multiple newlines.
   * @details Prints 30 newline characters to create a visual screen clear effect
   *          in console applications. This provides a simple way to refresh the
   *          display without using platform-specific clear commands.
   * @note This is a simple implementation that may not work perfectly on all terminals.
   *       For better results, consider using ANSI escape sequences or platform-specific commands.
   */
  private void clearScreen() {
    for (int i = 0; i < 30; i++) {
      System.out.println();
    }
  }

  /**
   * @brief Formats Unix timestamp to human-readable date and time string.
   * @param timestamp Unix timestamp in seconds (since January 1, 1970 UTC).
   * @return Formatted string in "yyyy-MM-dd HH:mm" format (e.g., "2024-01-15 14:30").
   * @details Converts a Unix timestamp (seconds since epoch) to a formatted date-time
   *          string using SimpleDateFormat. The format includes year, month, day,
   *          hour, and minute in a standard format.
   * @note The timestamp is multiplied by 1000 to convert from seconds to milliseconds
   *       as required by Java's Date constructor.
   */
  private String formatTimestamp(long timestamp) {
    java.util.Date date = new java.util.Date(timestamp * 1000);
    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
    return sdf.format(date);
  }

  /**
   * @brief Prints detailed information about an activity to the console.
   * @param activity The Activity object to print. Must not be null.
   * @details Displays all activity fields in a formatted manner including ID, name,
   *          duration, category, description, and formatted timestamp. The output
   *          is formatted for easy reading in the console.
   * @see #formatTimestamp(long)
   */
  private void printActivity(Activity activity) {
    System.out.println("ID: " + activity.id);
    System.out.println("Name: " + activity.name);
    System.out.println("Duration: " + activity.duration + " minutes");
    System.out.println("Category: " + activity.category);
    System.out.println("Description: " + activity.description);
    System.out.println("Date & Time: " + formatTimestamp(activity.timestamp));
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
   *          called when the ViewActivityHistory instance is no longer needed to
   *          ensure proper resource cleanup.
   * @note This method is idempotent - it can be called multiple times safely.
   * @note Does not save any data - only closes the scanner resource.
   */
  public void close() {
    if (scanner != null) {
      scanner.close();
    }
  }
}

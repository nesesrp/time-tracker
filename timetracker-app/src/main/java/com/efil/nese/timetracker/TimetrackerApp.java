/**
 * @file TimetrackerApp.java
 * @brief This file serves as the main application file for the Timetracker App.
 * @details This file contains the entry point of the application, which is the main method.
 *          It initializes the necessary components and executes the Timetracker App.
 */
package com.efil.nese.timetracker;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;

/**
 * @class TimetrackerApp
 * @brief This class represents the main application class for the Timetracker App.
 * @details The TimetrackerApp class provides the entry point for the Timetracker App.
 *          It handles user authentication, initializes the main menu system, and manages
 *          the overall application lifecycle. The application follows a simple flow:
 *          authenticate user -> show main menu -> save data on exit.
 *          All exceptions are caught and logged to ensure graceful error handling.
 * @author efil.saylam.nese.sarp
 * @version 1.0
 * @since 2024
 */
public class TimetrackerApp {

  /** @brief Logger instance for logging errors and application events. */
  private static final Logger logger = (Logger) LoggerFactory.getLogger(TimetrackerApp.class);

  /**
   * @brief Main method - entry point of the application.
   * @param args Command line arguments. Currently not used by the application.
   * @details This is the main entry point for the Timetracker application. It initializes
   *          the UserAuthentication and Timetracker components, handles the authentication
   *          flow, and manages the application lifecycle. If authentication is successful,
   *          the main menu is displayed. All exceptions are caught and logged, and data
   *          is saved before the application exits.
   * @note The application requires successful authentication before accessing the main menu.
   * @note All data is automatically saved in the finally block before exit.
   * @see UserAuthentication#authenticate()
   * @see Timetracker#run()
   * @see UserAuthentication#close()
   * @see Timetracker#close()
   */
  public static void main(String[] args) {
    UserAuthentication auth = new UserAuthentication();
    Timetracker timetracker = new Timetracker();

    try {
      // Authenticate user
      if (auth.authenticate()) {
        // If authentication successful, show main menu
        timetracker.run();
      }
    } catch (Exception e) {
      logger.error("An error occurred: ", e);
      System.out.println("An error occurred. Please try again.");
    }

    finally {
      // Save all data before closing
      System.out.println("\nSaving data...");
      auth.close();
      timetracker.close();
      System.out.println("Data saved successfully. Goodbye!");
    }
  }
}

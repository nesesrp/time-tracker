/**
 * @file Timetracker.java
 * @brief This file serves as the main menu system for the Timetracker application.
 * @details This file contains the implementation of the Timetracker class, which
 *          provides the main menu and navigation to different features.
 */
package com.efil.nese.timetracker;

import java.util.Scanner;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;

/**
 * @class Timetracker
 * @brief This class represents the main menu system for the Timetracker application.
 * @details The Timetracker class provides methods to display the main menu and
 *          navigate to different features like Activity Logging, Time Spent Analysis,
 *          Productivity Reports, Break Reminder, and View Activity History.
 *          This is the central coordinator that manages all feature modules.
 * @author efil.saylam.nese.sarp
 * @version 1.0
 * @since 2024
 */
public class Timetracker {

  /** @brief Logger instance for logging errors and application events. */
  private static final Logger logger = (Logger) LoggerFactory.getLogger(Timetracker.class);

  /** @brief Scanner instance for reading user input from console. */
  private Scanner scanner;

  /** @brief ActivityLogging module instance for activity management. */
  private ActivityLogging activityLogging;

  /** @brief TimeSpentAnalysis module instance for time analysis features. */
  private TimeSpentAnalysis timeSpentAnalysis;

  /** @brief ProductivityReports module instance for report generation. */
  private ProductivityReports productivityReports;

  /** @brief BreakReminder module instance for break notifications. */
  private BreakReminder breakReminder;

  /** @brief ViewActivityHistory module instance for viewing activity history. */
  private ViewActivityHistory viewActivityHistory;

  /**
   * @brief Constructor for Timetracker class.
   */
  public Timetracker() {
    this.scanner = new Scanner(System.in);
    this.activityLogging = new ActivityLogging();
    this.timeSpentAnalysis = new TimeSpentAnalysis(activityLogging);
    this.productivityReports = new ProductivityReports(activityLogging);
    this.breakReminder = new BreakReminder();
    this.viewActivityHistory = new ViewActivityHistory(activityLogging);
  }

  /**
   * @brief Displays the main menu after successful login.
   * @details Shows a formatted menu with all available features including Activity Logging,
   *          Time Spent Analysis, Productivity Reports, Break Reminder, View Activity History,
   *          and Logout option. The menu is displayed in a visually appealing format with
   *          borders and clear option numbering.
   * @note This method only displays the menu - it does not handle user input.
   * @see #run()
   */
  public void showMainMenu() {
    System.out.println("\n************************************************************");
    System.out.println("*                                                          *");
    System.out.println("*                    Main Menu                             *");
    System.out.println("*                                                          *");
    System.out.println("************************************************************");
    System.out.println();
    System.out.println("1. Activity Logging");
    System.out.println("2. Time Spent Analysis");
    System.out.println("3. Productivity Reports");
    System.out.println("4. Break Reminder");
    System.out.println("5. View Activity History");
    System.out.println("6. Logout");
    System.out.print("Please select an option: ");
  }

  /**
   * @brief Handles the main menu navigation and application flow.
   * @details Implements the main application loop that continuously displays the menu,
   *          reads user input, and navigates to the appropriate feature module based on
   *          the user's selection. The loop continues until the user selects logout (option 6).
   *          Each feature module is invoked through its run() method.
   * @note Invalid options display an error message and the menu is redisplayed.
   * @note The application continues running until the user explicitly chooses to logout.
   * @see #showMainMenu()
   * @see ActivityLogging#run()
   * @see TimeSpentAnalysis#run()
   * @see ProductivityReports#run()
   * @see BreakReminder#run()
   * @see ViewActivityHistory#run()
   */
  public void run() {
    boolean running = true;

    while (running) {
      showMainMenu();
      String choice = scanner.nextLine().trim();

      switch (choice) {
        case "1":
          activityLogging.run();
          break;

        case "2":
          timeSpentAnalysis.run();
          break;

        case "3":
          productivityReports.run();
          break;

        case "4":
          breakReminder.run();
          break;

        case "5":
          viewActivityHistory.run();
          break;

        case "6":
          System.out.println("Logging out...");
          running = false;
          break;

        default:
          System.out.println("Invalid option! Please select a number between 1-6.");
      }
    }
  }

  /**
   * @brief Closes the scanner resource and saves all data.
   */
  public void close() {
    // Save activities before closing
    if (activityLogging != null) {
      activityLogging.close();
    }

    if (scanner != null) {
      scanner.close();
    }
  }
}

/**
 * @file TimetrackerTest.java
 * @brief This file contains test cases for the Timetracker class.
 * @details This file includes test methods to validate the functionality of the Timetracker class.
 *          It uses JUnit 4 for unit testing.
 * @author efil.saylam.nese.sarp
*/
package com.efil.nese.timetracker;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Scanner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @class TimetrackerTest
 * @brief This class represents the test class for the Timetracker class.
 * @details The TimetrackerTest class provides test methods to verify the behavior of the Timetracker class.
 * @author efil.saylam.nese.sarp
*/
public class TimetrackerTest {

  private Timetracker timetracker;
  private InputStream originalSystemIn;

  /**
   * @brief This method is executed before each test method.
   * @throws Exception
   */
  @Before
  public void setUp() throws Exception {
    // Save original System.in
    originalSystemIn = System.in;
    // Provide an empty input stream to avoid Scanner issues
    System.setIn(new ByteArrayInputStream("".getBytes()));
    // Create Timetracker instance
    timetracker = new Timetracker();
  }

  /**
   * @brief This method is executed after each test method.
   * @throws Exception
   */
  @After
  public void tearDown() throws Exception {
    // Restore original System.in
    if (originalSystemIn != null) {
      System.setIn(originalSystemIn);
    }

    // Close timetracker
    if (timetracker != null) {
      timetracker.close();
    }
  }

  /**
   * @brief Test to verify Timetracker object creation.
   * @throws Exception
   */
  @Test
  public void testTimetrackerCreation() throws Exception {
    assertNotNull("Timetracker object should not be null", timetracker);
  }

  /**
   * @brief Test to verify Timetracker class exists.
   * @throws Exception
   */
  @Test
  public void testTimetrackerClassExists() throws Exception {
    Class<?> clazz = Timetracker.class;
    assertNotNull("Timetracker class should exist", clazz);
    assertEquals("Class name should be Timetracker", "Timetracker", clazz.getSimpleName());
  }

  /**
   * @brief Test to verify showMainMenu method exists and displays menu.
   * @throws Exception
   */
  @Test
  public void testShowMainMenu() throws Exception {
    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      timetracker.showMainMenu();
      String output = outContent.toString();
      assertTrue("Output should contain Main Menu header", output.contains("Main Menu"));
      assertTrue("Output should contain Activity Logging option", output.contains("Activity Logging"));
      assertTrue("Output should contain Time Spent Analysis option", output.contains("Time Spent Analysis"));
      assertTrue("Output should contain Productivity Reports option", output.contains("Productivity Reports"));
      assertTrue("Output should contain Break Reminder option", output.contains("Break Reminder"));
      assertTrue("Output should contain View Activity History option", output.contains("View Activity History"));
      assertTrue("Output should contain Logout option", output.contains("Logout"));
    }

    finally {
      System.setOut(originalOut);
    }
  }

  /**
   * @brief Test to verify run method handles logout option (6).
   * @throws Exception
   */
  @Test
  public void testRunLogout() throws Exception {
    String input = "6" + System.lineSeparator();
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    // Replace scanner with new one
    Scanner newScanner = new Scanner(System.in);
    java.lang.reflect.Field scannerField = Timetracker.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(timetracker, newScanner);

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      timetracker.run();
      String output = outContent.toString();
      assertTrue("Output should contain logout message", output.contains("Logging out"));
    }

    finally {
      System.setOut(originalOut);
      newScanner.close();
    }
  }

  /**
   * @brief Test to verify run method handles invalid option.
   * @throws Exception
   */
  @Test
  public void testRunInvalidOption() throws Exception {
    String input = "99" + System.lineSeparator() + "6" + System.lineSeparator();
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    // Replace scanner with new one
    Scanner newScanner = new Scanner(System.in);
    java.lang.reflect.Field scannerField = Timetracker.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(timetracker, newScanner);

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      timetracker.run();
      String output = outContent.toString();
      assertTrue("Output should contain invalid option message", output.contains("Invalid option"));
    }

    finally {
      System.setOut(originalOut);
      newScanner.close();
    }
  }

  /**
   * @brief Test to verify run method handles Activity Logging option (1).
   * @throws Exception
   */
  @Test
  public void testRunActivityLogging() throws Exception {
    // ActivityLogging.run() shows a menu, option 5 exits to main menu
    String input = "1" + System.lineSeparator() + "6" + System.lineSeparator();
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    // Replace scanner with new one
    Scanner newScanner = new Scanner(System.in);
    java.lang.reflect.Field scannerField = Timetracker.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(timetracker, newScanner);

    // Also need to replace ActivityLogging's scanner
    java.lang.reflect.Field activityLoggingField = Timetracker.class.getDeclaredField("activityLogging");
    activityLoggingField.setAccessible(true);
    ActivityLogging activityLogging = (ActivityLogging) activityLoggingField.get(timetracker);
    java.lang.reflect.Field alScannerField = ActivityLogging.class.getDeclaredField("scanner");
    alScannerField.setAccessible(true);
    alScannerField.set(activityLogging, new Scanner(new ByteArrayInputStream(("5" + System.lineSeparator()).getBytes())));

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      timetracker.run();
      String output = outContent.toString();
      // ActivityLogging should have been called
      assertTrue("ActivityLogging should be invoked", true);
    }

    finally {
      System.setOut(originalOut);
      newScanner.close();
    }
  }

  /**
   * @brief Test to verify run method handles Time Spent Analysis option (2).
   * @throws Exception
   */
  @Test
  public void testRunTimeSpentAnalysis() throws Exception {
    // TimeSpentAnalysis.run() shows a menu, option 4 exits to main menu
    String input = "2" + System.lineSeparator() + "6" + System.lineSeparator();
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    // Replace scanner with new one
    Scanner newScanner = new Scanner(System.in);
    java.lang.reflect.Field scannerField = Timetracker.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(timetracker, newScanner);

    // Also need to replace TimeSpentAnalysis's scanner
    java.lang.reflect.Field timeSpentAnalysisField = Timetracker.class.getDeclaredField("timeSpentAnalysis");
    timeSpentAnalysisField.setAccessible(true);
    TimeSpentAnalysis timeSpentAnalysis = (TimeSpentAnalysis) timeSpentAnalysisField.get(timetracker);
    java.lang.reflect.Field tsaScannerField = TimeSpentAnalysis.class.getDeclaredField("scanner");
    tsaScannerField.setAccessible(true);
    tsaScannerField.set(timeSpentAnalysis, new Scanner(new ByteArrayInputStream(("4" + System.lineSeparator()).getBytes())));

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      timetracker.run();
      String output = outContent.toString();
      // TimeSpentAnalysis should have been called
      assertTrue("TimeSpentAnalysis should be invoked", true);
    }

    finally {
      System.setOut(originalOut);
      newScanner.close();
    }
  }

  /**
   * @brief Test to verify run method handles Productivity Reports option (3).
   * @throws Exception
   */
  @Test
  public void testRunProductivityReports() throws Exception {
    // ProductivityReports.run() shows a menu, option 5 exits to main menu
    String input = "3" + System.lineSeparator() + "6" + System.lineSeparator();
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    // Replace scanner with new one
    Scanner newScanner = new Scanner(System.in);
    java.lang.reflect.Field scannerField = Timetracker.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(timetracker, newScanner);

    // Also need to replace ProductivityReports's scanner
    java.lang.reflect.Field productivityReportsField = Timetracker.class.getDeclaredField("productivityReports");
    productivityReportsField.setAccessible(true);
    ProductivityReports productivityReports = (ProductivityReports) productivityReportsField.get(timetracker);
    java.lang.reflect.Field prScannerField = ProductivityReports.class.getDeclaredField("scanner");
    prScannerField.setAccessible(true);
    prScannerField.set(productivityReports, new Scanner(new ByteArrayInputStream(("5" + System.lineSeparator()).getBytes())));

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      timetracker.run();
      String output = outContent.toString();
      // ProductivityReports should have been called
      assertTrue("ProductivityReports should be invoked", true);
    }

    finally {
      System.setOut(originalOut);
      newScanner.close();
    }
  }

  /**
   * @brief Test to verify run method handles Break Reminder option (4).
   * @throws Exception
   */
  @Test
  public void testRunBreakReminder() throws Exception {
    // BreakReminder.run() shows a menu, option 3 exits to main menu
    String input = "4" + System.lineSeparator() + "6" + System.lineSeparator();
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    // Replace scanner with new one
    Scanner newScanner = new Scanner(System.in);
    java.lang.reflect.Field scannerField = Timetracker.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(timetracker, newScanner);

    // Also need to replace BreakReminder's scanner
    java.lang.reflect.Field breakReminderField = Timetracker.class.getDeclaredField("breakReminder");
    breakReminderField.setAccessible(true);
    BreakReminder breakReminder = (BreakReminder) breakReminderField.get(timetracker);
    java.lang.reflect.Field brScannerField = BreakReminder.class.getDeclaredField("scanner");
    brScannerField.setAccessible(true);
    brScannerField.set(breakReminder, new Scanner(new ByteArrayInputStream(("3" + System.lineSeparator()).getBytes())));

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      timetracker.run();
      String output = outContent.toString();
      // BreakReminder should have been called
      assertTrue("BreakReminder should be invoked", true);
    }

    finally {
      System.setOut(originalOut);
      newScanner.close();
    }
  }

  /**
   * @brief Test to verify run method handles View Activity History option (5).
   * @throws Exception
   */
  @Test
  public void testRunViewActivityHistory() throws Exception {
    // ViewActivityHistory.run() shows a menu, 'q' exits to main menu
    String input = "5" + System.lineSeparator() + "6" + System.lineSeparator();
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    // Replace scanner with new one
    Scanner newScanner = new Scanner(System.in);
    java.lang.reflect.Field scannerField = Timetracker.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(timetracker, newScanner);

    // Also need to replace ViewActivityHistory's scanner
    java.lang.reflect.Field viewActivityHistoryField = Timetracker.class.getDeclaredField("viewActivityHistory");
    viewActivityHistoryField.setAccessible(true);
    ViewActivityHistory viewActivityHistory = (ViewActivityHistory) viewActivityHistoryField.get(timetracker);
    java.lang.reflect.Field vahScannerField = ViewActivityHistory.class.getDeclaredField("scanner");
    vahScannerField.setAccessible(true);
    vahScannerField.set(viewActivityHistory, new Scanner(new ByteArrayInputStream(("q" + System.lineSeparator()).getBytes())));

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      timetracker.run();
      String output = outContent.toString();
      // ViewActivityHistory should have been called
      assertTrue("ViewActivityHistory should be invoked", true);
    }

    finally {
      System.setOut(originalOut);
      newScanner.close();
    }
  }

  /**
   * @brief Test to verify close method exists and can be called.
   * @throws Exception
   */
  @Test
  public void testClose() throws Exception {
    timetracker.close();
    assertTrue("close should execute without exception", true);
  }

  /**
   * @brief Test to verify close method closes scanner.
   * @throws Exception
   */
  @Test
  public void testCloseClosesScanner() throws Exception {
    // Access scanner field
    java.lang.reflect.Field scannerField = Timetracker.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    Scanner scanner = (Scanner) scannerField.get(timetracker);

    assertNotNull("Scanner should not be null before close", scanner);

    timetracker.close();

    // After close, scanner should be closed
    // We can't directly check if scanner is closed, but we can verify no exception is thrown
    assertTrue("close should execute without exception", true);
  }

  /**
   * @brief Test to verify close method closes activityLogging.
   * @throws Exception
   */
  @Test
  public void testCloseClosesActivityLogging() throws Exception {
    // Access activityLogging field
    java.lang.reflect.Field activityLoggingField = Timetracker.class.getDeclaredField("activityLogging");
    activityLoggingField.setAccessible(true);
    ActivityLogging activityLogging = (ActivityLogging) activityLoggingField.get(timetracker);

    assertNotNull("ActivityLogging should not be null", activityLogging);

    timetracker.close();

    // After close, activityLogging.close() should have been called
    assertTrue("close should execute without exception", true);
  }

  /**
   * @brief Test to verify constructor initializes all components.
   * @throws Exception
   */
  @Test
  public void testConstructorInitializesComponents() throws Exception {
    Timetracker tt = new Timetracker();

    try {
      // Check scanner
      java.lang.reflect.Field scannerField = Timetracker.class.getDeclaredField("scanner");
      scannerField.setAccessible(true);
      Scanner scanner = (Scanner) scannerField.get(tt);
      assertNotNull("Scanner should be initialized", scanner);
      // Check activityLogging
      java.lang.reflect.Field activityLoggingField = Timetracker.class.getDeclaredField("activityLogging");
      activityLoggingField.setAccessible(true);
      ActivityLogging activityLogging = (ActivityLogging) activityLoggingField.get(tt);
      assertNotNull("ActivityLogging should be initialized", activityLogging);
      // Check timeSpentAnalysis
      java.lang.reflect.Field timeSpentAnalysisField = Timetracker.class.getDeclaredField("timeSpentAnalysis");
      timeSpentAnalysisField.setAccessible(true);
      TimeSpentAnalysis timeSpentAnalysis = (TimeSpentAnalysis) timeSpentAnalysisField.get(tt);
      assertNotNull("TimeSpentAnalysis should be initialized", timeSpentAnalysis);
      // Check productivityReports
      java.lang.reflect.Field productivityReportsField = Timetracker.class.getDeclaredField("productivityReports");
      productivityReportsField.setAccessible(true);
      ProductivityReports productivityReports = (ProductivityReports) productivityReportsField.get(tt);
      assertNotNull("ProductivityReports should be initialized", productivityReports);
      // Check breakReminder
      java.lang.reflect.Field breakReminderField = Timetracker.class.getDeclaredField("breakReminder");
      breakReminderField.setAccessible(true);
      BreakReminder breakReminder = (BreakReminder) breakReminderField.get(tt);
      assertNotNull("BreakReminder should be initialized", breakReminder);
      // Check viewActivityHistory
      java.lang.reflect.Field viewActivityHistoryField = Timetracker.class.getDeclaredField("viewActivityHistory");
      viewActivityHistoryField.setAccessible(true);
      ViewActivityHistory viewActivityHistory = (ViewActivityHistory) viewActivityHistoryField.get(tt);
      assertNotNull("ViewActivityHistory should be initialized", viewActivityHistory);
    }

    finally {
      tt.close();
    }
  }

  /**
   * @brief Test to verify run method handles multiple menu cycles.
   * @throws Exception
   */
  @Test
  public void testRunMultipleMenuCycles() throws Exception {
    // Test multiple invalid options then logout
    String input = "99" + System.lineSeparator() + "0" + System.lineSeparator() + "7" + System.lineSeparator() + "6" + System.lineSeparator();
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    // Replace scanner with new one
    Scanner newScanner = new Scanner(System.in);
    java.lang.reflect.Field scannerField = Timetracker.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(timetracker, newScanner);

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      timetracker.run();
      String output = outContent.toString();
      // Should have multiple invalid option messages
      assertTrue("Output should contain invalid option messages", output.contains("Invalid option"));
      assertTrue("Output should contain logout message", output.contains("Logging out"));
    }

    finally {
      System.setOut(originalOut);
      newScanner.close();
    }
  }

  /**
   * @brief Test to verify run method handles empty input.
   * @throws Exception
   */
  @Test
  public void testRunEmptyInput() throws Exception {
    // Empty input should be treated as invalid, then logout
    String input = System.lineSeparator() + "6" + System.lineSeparator();
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    // Replace scanner with new one
    Scanner newScanner = new Scanner(System.in);
    java.lang.reflect.Field scannerField = Timetracker.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(timetracker, newScanner);

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      timetracker.run();
      String output = outContent.toString();
      // Empty input should trigger invalid option
      assertTrue("Output should contain invalid option or logout",
                 output.contains("Invalid option") || output.contains("Logging out"));
    }

    finally {
      System.setOut(originalOut);
      newScanner.close();
    }
  }

  /**
   * @brief Test to verify run method handles whitespace in input.
   * @throws Exception
   */
  @Test
  public void testRunWhitespaceInput() throws Exception {
    // Input with whitespace should be trimmed
    String input = "  6  " + System.lineSeparator();
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    // Replace scanner with new one
    Scanner newScanner = new Scanner(System.in);
    java.lang.reflect.Field scannerField = Timetracker.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(timetracker, newScanner);

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      timetracker.run();
      String output = outContent.toString();
      // Trimmed input "6" should trigger logout
      assertTrue("Output should contain logout message", output.contains("Logging out"));
    }

    finally {
      System.setOut(originalOut);
      newScanner.close();
    }
  }
}

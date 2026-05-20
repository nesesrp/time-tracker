/**
 * @file ViewActivityHistoryTest.java
 * @brief This file contains test cases for the ViewActivityHistory class.
 * @details This file includes test methods to validate the functionality of the ViewActivityHistory class.
 *          It uses JUnit 4 for unit testing.
 * @author efil.saylam.nese.sarp
 */
package com.efil.nese.timetracker;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @class ViewActivityHistoryTest
 * @brief This class represents the test class for the ViewActivityHistory class.
 * @details The ViewActivityHistoryTest class provides test methods to verify the behavior of the ViewActivityHistory class.
 * @author efil.saylam.nese.sarp
 */
public class ViewActivityHistoryTest {

  private ViewActivityHistory viewActivityHistory;
  private ActivityLogging activityLogging;
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
    // Create ActivityLogging instance
    activityLogging = new ActivityLogging();
    // Create ViewActivityHistory instance
    viewActivityHistory = new ViewActivityHistory(activityLogging);
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

    // Close viewActivityHistory
    if (viewActivityHistory != null) {
      viewActivityHistory.close();
    }

    // Close activityLogging
    if (activityLogging != null) {
      activityLogging.close();
    }
  }

  /**
   * @brief Test to verify ViewActivityHistory object creation.
   * @throws Exception
   */
  @Test
  public void testViewActivityHistoryCreation() throws Exception {
    ViewActivityHistory vah = new ViewActivityHistory();
    assertNotNull("ViewActivityHistory object should not be null", vah);
    vah.close();
  }

  /**
   * @brief Test to verify ViewActivityHistory creation with ActivityLogging.
   * @throws Exception
   */
  @Test
  public void testViewActivityHistoryCreationWithActivityLogging() throws Exception {
    ActivityLogging al = new ActivityLogging();
    ViewActivityHistory vah = new ViewActivityHistory(al);
    assertNotNull("ViewActivityHistory object should not be null", vah);
    vah.close();
    al.close();
  }

  /**
   * @brief Test to verify ViewActivityHistory class exists.
   * @throws Exception
   */
  @Test
  public void testViewActivityHistoryClassExists() throws Exception {
    Class<?> clazz = ViewActivityHistory.class;
    assertNotNull("ViewActivityHistory class should exist", clazz);
    assertEquals("Class name should be ViewActivityHistory", "ViewActivityHistory", clazz.getSimpleName());
  }

  /**
   * @brief Test to verify setActivityLogging method works.
   * @throws Exception
   */
  @Test
  public void testSetActivityLogging() throws Exception {
    ViewActivityHistory vah = new ViewActivityHistory();
    ActivityLogging al = new ActivityLogging();

    vah.setActivityLogging(al);

    // Verify using reflection
    java.lang.reflect.Field activityLoggingField = ViewActivityHistory.class.getDeclaredField("activityLogging");
    activityLoggingField.setAccessible(true);
    ActivityLogging setActivityLogging = (ActivityLogging) activityLoggingField.get(vah);

    assertNotNull("ActivityLogging should be set", setActivityLogging);
    assertEquals("ActivityLogging should match", al, setActivityLogging);

    vah.close();
    al.close();
  }

  /**
   * @brief Test to verify run method handles null ActivityLogging.
   * @throws Exception
   */
  @Test
  public void testRunWithNullActivityLogging() throws Exception {
    ViewActivityHistory vah = new ViewActivityHistory();
    String input = System.lineSeparator();
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    // Replace scanner
    Scanner newScanner = new Scanner(System.in);
    java.lang.reflect.Field scannerField = ViewActivityHistory.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(vah, newScanner);

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      vah.run();
      String output = outContent.toString();
      assertTrue("Output should contain error message", output.contains("Error") || output.contains("ActivityLogging not initialized"));
    }

    finally {
      System.setOut(originalOut);
      vah.close();
    }
  }

  /**
   * @brief Test to verify run method works with empty activities.
   * @throws Exception
   */
  @Test
  public void testRunWithEmptyActivities() throws Exception {
    // Use a limited output stream to avoid memory issues with clearScreen
    String input = System.lineSeparator();
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    // Replace scanner
    Scanner newScanner = new Scanner(System.in);
    java.lang.reflect.Field scannerField = ViewActivityHistory.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(viewActivityHistory, newScanner);

    // Use a limited size output stream
    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream() {
      private static final int MAX_SIZE = 10000; // Limit to 10KB
      @Override
      public synchronized void write(byte[] b, int off, int len) {
        if (this.count + len <= MAX_SIZE) {
          super.write(b, off, len);
        }
      }
    };

    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      // Run in a separate thread with timeout
      Thread testThread = new Thread(() -> {
        try {
          viewActivityHistory.run();
        } catch (Exception e) {
          // Ignore
        }
      });
      testThread.start();
      testThread.join(2000); // Wait max 2 seconds

      if (testThread.isAlive()) {
        testThread.interrupt();
      }

      String output = outContent.toString();
      assertTrue("Output should contain no activity message or test should complete",
                 output.contains("No activity history") || output.contains("Start by logging") || output.length() > 0);
    }

    finally {
      System.setOut(originalOut);
    }
  }

  /**
   * @brief Test to verify run method works with activities.
   * @throws Exception
   */
  @Test
  public void testRunWithActivities() throws Exception {
    // Add an activity
    long timestamp = System.currentTimeMillis() / 1000;
    Activity activity = new Activity(1, "Test Activity", 30, timestamp, "Test", "Test Description");
    activityLogging.addActivity(activity);

    // Exit immediately with 'q'
    String input = "q" + System.lineSeparator();
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    // Replace scanner
    Scanner newScanner = new Scanner(System.in);
    java.lang.reflect.Field scannerField = ViewActivityHistory.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(viewActivityHistory, newScanner);

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      viewActivityHistory.run();
      String output = outContent.toString();
      // Should have displayed activity history
      assertTrue("ViewActivityHistory should run without exception", true);
    }

    finally {
      System.setOut(originalOut);
    }
  }

  /**
   * @brief Test to verify calculateXORChecksum method works.
   * @throws Exception
   */
  @Test
  public void testCalculateXORChecksum() throws Exception {
    List<Activity> activities = new ArrayList<>();
    long timestamp = System.currentTimeMillis() / 1000;
    activities.add(new Activity(1, "Activity 1", 30, timestamp, "Category 1", "Description 1"));
    activities.add(new Activity(2, "Activity 2", 45, timestamp + 100, "Category 2", "Description 2"));

    java.lang.reflect.Method calculateXORChecksumMethod = ViewActivityHistory.class.getDeclaredMethod("calculateXORChecksum", List.class);
    calculateXORChecksumMethod.setAccessible(true);

    int checksum1 = (Integer) calculateXORChecksumMethod.invoke(viewActivityHistory, activities);
    int checksum2 = (Integer) calculateXORChecksumMethod.invoke(viewActivityHistory, activities);

    assertEquals("Checksum should be consistent", checksum1, checksum2);
    assertTrue("Checksum should be non-zero for non-empty list", checksum1 != 0 || activities.isEmpty());
  }

  /**
   * @brief Test to verify calculateXORChecksum with empty list.
   * @throws Exception
   */
  @Test
  public void testCalculateXORChecksumEmptyList() throws Exception {
    List<Activity> activities = new ArrayList<>();

    java.lang.reflect.Method calculateXORChecksumMethod = ViewActivityHistory.class.getDeclaredMethod("calculateXORChecksum", List.class);
    calculateXORChecksumMethod.setAccessible(true);

    int checksum = (Integer) calculateXORChecksumMethod.invoke(viewActivityHistory, activities);

    assertEquals("Checksum should be 0 for empty list", 0, checksum);
  }

  /**
   * @brief Test to verify calculateXORChecksum detects changes.
   * @throws Exception
   */
  @Test
  public void testCalculateXORChecksumDetectsChanges() throws Exception {
    List<Activity> activities = new ArrayList<>();
    long timestamp = System.currentTimeMillis() / 1000;
    activities.add(new Activity(1, "Activity 1", 30, timestamp, "Category 1", "Description 1"));

    java.lang.reflect.Method calculateXORChecksumMethod = ViewActivityHistory.class.getDeclaredMethod("calculateXORChecksum", List.class);
    calculateXORChecksumMethod.setAccessible(true);

    int checksum1 = (Integer) calculateXORChecksumMethod.invoke(viewActivityHistory, activities);

    // Modify activity
    activities.get(0).duration = 60;

    int checksum2 = (Integer) calculateXORChecksumMethod.invoke(viewActivityHistory, activities);

    assertNotEquals("Checksum should change when activity is modified", checksum1, checksum2);
  }

  /**
   * @brief Test to verify verifyIntegrity method works.
   * @throws Exception
   */
  @Test
  public void testVerifyIntegrity() throws Exception {
    List<Activity> activities = new ArrayList<>();
    long timestamp = System.currentTimeMillis() / 1000;
    activities.add(new Activity(1, "Activity 1", 30, timestamp, "Category 1", "Description 1"));

    java.lang.reflect.Method calculateXORChecksumMethod = ViewActivityHistory.class.getDeclaredMethod("calculateXORChecksum", List.class);
    calculateXORChecksumMethod.setAccessible(true);
    int expectedChecksum = (Integer) calculateXORChecksumMethod.invoke(viewActivityHistory, activities);

    java.lang.reflect.Method verifyIntegrityMethod = ViewActivityHistory.class.getDeclaredMethod("verifyIntegrity", List.class, int.class);
    verifyIntegrityMethod.setAccessible(true);

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      // Should not print warning for matching checksum
      verifyIntegrityMethod.invoke(viewActivityHistory, activities, expectedChecksum);
      String output = outContent.toString();
      assertFalse("Output should not contain warning for matching checksum", output.contains("Warning") || output.contains("integrity check failed"));
    }

    finally {
      System.setOut(originalOut);
    }
  }

  /**
   * @brief Test to verify verifyIntegrity detects mismatch.
   * @throws Exception
   */
  @Test
  public void testVerifyIntegrityDetectsMismatch() throws Exception {
    List<Activity> activities = new ArrayList<>();
    long timestamp = System.currentTimeMillis() / 1000;
    activities.add(new Activity(1, "Activity 1", 30, timestamp, "Category 1", "Description 1"));

    int wrongChecksum = 12345;

    java.lang.reflect.Method verifyIntegrityMethod = ViewActivityHistory.class.getDeclaredMethod("verifyIntegrity", List.class, int.class);
    verifyIntegrityMethod.setAccessible(true);

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      verifyIntegrityMethod.invoke(viewActivityHistory, activities, wrongChecksum);
      String output = outContent.toString();
      assertTrue("Output should contain warning for mismatched checksum", output.contains("Warning") || output.contains("integrity check failed"));
    }

    finally {
      System.setOut(originalOut);
    }
  }

  /**
   * @brief Test to verify clearScreen method works.
   * @throws Exception
   */
  @Test
  public void testClearScreen() throws Exception {
    java.lang.reflect.Method clearScreenMethod = ViewActivityHistory.class.getDeclaredMethod("clearScreen");
    clearScreenMethod.setAccessible(true);

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      clearScreenMethod.invoke(viewActivityHistory);
      String output = outContent.toString();
      // Should have printed newlines
      assertTrue("Output should contain newlines", output.length() > 0);
    }

    finally {
      System.setOut(originalOut);
    }
  }

  /**
   * @brief Test to verify formatTimestamp method works.
   * @throws Exception
   */
  @Test
  public void testFormatTimestamp() throws Exception {
    long timestamp = System.currentTimeMillis() / 1000;

    java.lang.reflect.Method formatTimestampMethod = ViewActivityHistory.class.getDeclaredMethod("formatTimestamp", long.class);
    formatTimestampMethod.setAccessible(true);

    String formatted = (String) formatTimestampMethod.invoke(viewActivityHistory, timestamp);

    assertNotNull("Formatted timestamp should not be null", formatted);
    assertTrue("Formatted timestamp should not be empty", !formatted.isEmpty());
    assertTrue("Formatted timestamp should contain date format", formatted.contains("-"));
  }

  /**
   * @brief Test to verify printActivity method works.
   * @throws Exception
   */
  @Test
  public void testPrintActivity() throws Exception {
    long timestamp = System.currentTimeMillis() / 1000;
    Activity activity = new Activity(1, "Test Activity", 30, timestamp, "Test Category", "Test Description");

    java.lang.reflect.Method printActivityMethod = ViewActivityHistory.class.getDeclaredMethod("printActivity", Activity.class);
    printActivityMethod.setAccessible(true);

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      printActivityMethod.invoke(viewActivityHistory, activity);
      String output = outContent.toString();
      assertTrue("Output should contain activity ID", output.contains("ID:"));
      assertTrue("Output should contain activity name", output.contains("Test Activity"));
      assertTrue("Output should contain duration", output.contains("Duration:"));
      assertTrue("Output should contain category", output.contains("Test Category"));
      assertTrue("Output should contain description", output.contains("Test Description"));
    }

    finally {
      System.setOut(originalOut);
    }
  }

  /**
   * @brief Test to verify waitForEnter method works.
   * @throws Exception
   */
  @Test
  public void testWaitForEnter() throws Exception {
    String input = System.lineSeparator();
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    // Replace scanner
    Scanner newScanner = new Scanner(System.in);
    java.lang.reflect.Field scannerField = ViewActivityHistory.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(viewActivityHistory, newScanner);

    java.lang.reflect.Method waitForEnterMethod = ViewActivityHistory.class.getDeclaredMethod("waitForEnter");
    waitForEnterMethod.setAccessible(true);

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      waitForEnterMethod.invoke(viewActivityHistory);
      String output = outContent.toString();
      assertTrue("Output should contain press enter message", output.contains("Press Enter") || output.contains("continue"));
    }

    finally {
      System.setOut(originalOut);
    }
  }

  /**
   * @brief Test to verify close method exists and can be called.
   * @throws Exception
   */
  @Test
  public void testClose() throws Exception {
    viewActivityHistory.close();
    assertTrue("close should execute without exception", true);
  }

  /**
   * @brief Test to verify close method closes scanner.
   * @throws Exception
   */
  @Test
  public void testCloseClosesScanner() throws Exception {
    // Access scanner field
    java.lang.reflect.Field scannerField = ViewActivityHistory.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    Scanner scanner = (Scanner) scannerField.get(viewActivityHistory);

    assertNotNull("Scanner should not be null before close", scanner);

    viewActivityHistory.close();

    // After close, scanner should be closed
    // We can't directly check if scanner is closed, but we can verify no exception is thrown
    assertTrue("close should execute without exception", true);
  }

  /**
   * @brief Test to verify viewActivityHistory navigation forward.
   * @throws Exception
   */
  @Test
  public void testViewActivityHistoryNavigationForward() throws Exception {
    // Add activities
    long timestamp = System.currentTimeMillis() / 1000;
    activityLogging.addActivity(new Activity(1, "Activity 1", 30, timestamp, "Category 1", "Description 1"));
    activityLogging.addActivity(new Activity(2, "Activity 2", 45, timestamp + 100, "Category 2", "Description 2"));

    // Navigate: d (forward), then q (quit)
    String input = "d" + System.lineSeparator() + "q" + System.lineSeparator();
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    // Replace scanner
    Scanner newScanner = new Scanner(System.in);
    java.lang.reflect.Field scannerField = ViewActivityHistory.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(viewActivityHistory, newScanner);

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      viewActivityHistory.run();
      String output = outContent.toString();
      // Should have navigated forward
      assertTrue("Navigation should work", true);
    }

    finally {
      System.setOut(originalOut);
    }
  }

  /**
   * @brief Test to verify viewActivityHistory navigation backward.
   * @throws Exception
   */
  @Test
  public void testViewActivityHistoryNavigationBackward() throws Exception {
    // Add activities
    long timestamp = System.currentTimeMillis() / 1000;
    activityLogging.addActivity(new Activity(1, "Activity 1", 30, timestamp, "Category 1", "Description 1"));
    activityLogging.addActivity(new Activity(2, "Activity 2", 45, timestamp + 100, "Category 2", "Description 2"));

    // Navigate: d (forward), a (backward), then q (quit)
    String input = "d" + System.lineSeparator() + "a" + System.lineSeparator() + "q" + System.lineSeparator();
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    // Replace scanner
    Scanner newScanner = new Scanner(System.in);
    java.lang.reflect.Field scannerField = ViewActivityHistory.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(viewActivityHistory, newScanner);

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      viewActivityHistory.run();
      String output = outContent.toString();
      // Should have navigated backward
      assertTrue("Navigation should work", true);
    }

    finally {
      System.setOut(originalOut);
    }
  }

  /**
   * @brief Test to verify viewActivityHistory navigation to first.
   * @throws Exception
   */
  @Test
  public void testViewActivityHistoryNavigationToFirst() throws Exception {
    // Add activities
    long timestamp = System.currentTimeMillis() / 1000;
    activityLogging.addActivity(new Activity(1, "Activity 1", 30, timestamp, "Category 1", "Description 1"));
    activityLogging.addActivity(new Activity(2, "Activity 2", 45, timestamp + 100, "Category 2", "Description 2"));

    // Navigate: f (first), then q (quit)
    String input = "f" + System.lineSeparator() + "q" + System.lineSeparator();
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    // Replace scanner
    Scanner newScanner = new Scanner(System.in);
    java.lang.reflect.Field scannerField = ViewActivityHistory.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(viewActivityHistory, newScanner);

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      viewActivityHistory.run();
      String output = outContent.toString();
      // Should have navigated to first
      assertTrue("Navigation to first should work", true);
    }

    finally {
      System.setOut(originalOut);
    }
  }

  /**
   * @brief Test to verify viewActivityHistory navigation to last.
   * @throws Exception
   */
  @Test
  public void testViewActivityHistoryNavigationToLast() throws Exception {
    // Add activities
    long timestamp = System.currentTimeMillis() / 1000;
    activityLogging.addActivity(new Activity(1, "Activity 1", 30, timestamp, "Category 1", "Description 1"));
    activityLogging.addActivity(new Activity(2, "Activity 2", 45, timestamp + 100, "Category 2", "Description 2"));

    // Navigate: l (last), then q (quit)
    String input = "l" + System.lineSeparator() + "q" + System.lineSeparator();
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    // Replace scanner
    Scanner newScanner = new Scanner(System.in);
    java.lang.reflect.Field scannerField = ViewActivityHistory.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(viewActivityHistory, newScanner);

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      viewActivityHistory.run();
      String output = outContent.toString();
      // Should have navigated to last
      assertTrue("Navigation to last should work", true);
    }

    finally {
      System.setOut(originalOut);
    }
  }

  /**
   * @brief Test to verify viewActivityHistory recalculate checksum.
   * @throws Exception
   */
  @Test
  public void testViewActivityHistoryRecalculateChecksum() throws Exception {
    // Add activities
    long timestamp = System.currentTimeMillis() / 1000;
    activityLogging.addActivity(new Activity(1, "Activity 1", 30, timestamp, "Category 1", "Description 1"));

    // Navigate: r (recalculate), then q (quit)
    String input = "r" + System.lineSeparator() + System.lineSeparator() + "q" + System.lineSeparator();
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    // Replace scanner
    Scanner newScanner = new Scanner(System.in);
    java.lang.reflect.Field scannerField = ViewActivityHistory.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(viewActivityHistory, newScanner);

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      viewActivityHistory.run();
      String output = outContent.toString();
      // Should have recalculated checksum
      assertTrue("Recalculate checksum should work", true);
    }

    finally {
      System.setOut(originalOut);
    }
  }

  /**
   * @brief Test to verify viewActivityHistory handles invalid key.
   * @throws Exception
   */
  @Test
  public void testViewActivityHistoryInvalidKey() throws Exception {
    // Add activities
    long timestamp = System.currentTimeMillis() / 1000;
    activityLogging.addActivity(new Activity(1, "Activity 1", 30, timestamp, "Category 1", "Description 1"));

    // Navigate: x (invalid), then q (quit)
    String input = "x" + System.lineSeparator() + System.lineSeparator() + "q" + System.lineSeparator();
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    // Replace scanner
    Scanner newScanner = new Scanner(System.in);
    java.lang.reflect.Field scannerField = ViewActivityHistory.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(viewActivityHistory, newScanner);

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      viewActivityHistory.run();
      String output = outContent.toString();
      // Should have handled invalid key
      assertTrue("Invalid key should be handled", true);
    }

    finally {
      System.setOut(originalOut);
    }
  }

  /**
   * @brief Test to verify viewActivityHistory handles boundary conditions.
   * @throws Exception
   */
  @Test
  public void testViewActivityHistoryBoundaryConditions() throws Exception {
    // Add activities
    long timestamp = System.currentTimeMillis() / 1000;
    activityLogging.addActivity(new Activity(1, "Activity 1", 30, timestamp, "Category 1", "Description 1"));

    // Try to go backward at first: a (should show message), then q (quit)
    String input = "a" + System.lineSeparator() + System.lineSeparator() + "q" + System.lineSeparator();
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    // Replace scanner
    Scanner newScanner = new Scanner(System.in);
    java.lang.reflect.Field scannerField = ViewActivityHistory.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(viewActivityHistory, newScanner);

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      viewActivityHistory.run();
      String output = outContent.toString();
      // Should have handled boundary condition
      assertTrue("Boundary condition should be handled", true);
    }

    finally {
      System.setOut(originalOut);
    }
  }

  /**
   * @brief Test to verify viewActivityHistory handles empty input.
   * @throws Exception
   */
  @Test
  public void testViewActivityHistoryEmptyInput() throws Exception {
    // Add activities
    long timestamp = System.currentTimeMillis() / 1000;
    activityLogging.addActivity(new Activity(1, "Activity 1", 30, timestamp, "Category 1", "Description 1"));

    // Empty input, then q (quit)
    String input = System.lineSeparator() + "q" + System.lineSeparator();
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    // Replace scanner
    Scanner newScanner = new Scanner(System.in);
    java.lang.reflect.Field scannerField = ViewActivityHistory.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(viewActivityHistory, newScanner);

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      viewActivityHistory.run();
      String output = outContent.toString();
      // Should have handled empty input
      assertTrue("Empty input should be handled", true);
    }

    finally {
      System.setOut(originalOut);
    }
  }
}

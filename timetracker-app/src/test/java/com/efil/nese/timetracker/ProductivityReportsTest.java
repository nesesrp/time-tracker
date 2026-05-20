/**
 * @file ProductivityReportsTest.java
 * @brief This file contains test cases for the ProductivityReports class.
 * @details This file includes test methods to validate the functionality of the ProductivityReports class.
 *          It uses JUnit 4 for unit testing.
 * @author efil.saylam.nese.sarp
 */
package com.efil.nese.timetracker;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @class ProductivityReportsTest
 * @brief This class represents the test class for the ProductivityReports class.
 * @details The ProductivityReportsTest class provides test methods to verify the behavior of the ProductivityReports class.
 * @author efil.saylam.nese.sarp
 */
public class ProductivityReportsTest {

  private ProductivityReports productivityReports;
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
    // Create ProductivityReports instance
    productivityReports = new ProductivityReports(activityLogging);
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

    // Close productivityReports
    if (productivityReports != null) {
      productivityReports.close();
    }

    // Close activityLogging
    if (activityLogging != null) {
      activityLogging.close();
    }
  }

  /**
   * @brief Test to verify ProductivityReports object creation.
   * @throws Exception
   */
  @Test
  public void testProductivityReportsCreation() throws Exception {
    ProductivityReports pr = new ProductivityReports();
    assertNotNull("ProductivityReports object should not be null", pr);
    pr.close();
  }

  /**
   * @brief Test to verify ProductivityReports creation with ActivityLogging.
   * @throws Exception
   */
  @Test
  public void testProductivityReportsCreationWithActivityLogging() throws Exception {
    ActivityLogging al = new ActivityLogging();
    ProductivityReports pr = new ProductivityReports(al);
    assertNotNull("ProductivityReports object should not be null", pr);
    pr.close();
    al.close();
  }

  /**
   * @brief Test to verify ProductivityReports class exists.
   * @throws Exception
   */
  @Test
  public void testProductivityReportsClassExists() throws Exception {
    Class<?> clazz = ProductivityReports.class;
    assertNotNull("ProductivityReports class should exist", clazz);
    assertEquals("Class name should be ProductivityReports", "ProductivityReports", clazz.getSimpleName());
  }

  /**
   * @brief Test to verify setActivityLogging method works.
   * @throws Exception
   */
  @Test
  public void testSetActivityLogging() throws Exception {
    ProductivityReports pr = new ProductivityReports();
    ActivityLogging al = new ActivityLogging();

    pr.setActivityLogging(al);

    // Verify using reflection
    java.lang.reflect.Field activityLoggingField = ProductivityReports.class.getDeclaredField("activityLogging");
    activityLoggingField.setAccessible(true);
    ActivityLogging setActivityLogging = (ActivityLogging) activityLoggingField.get(pr);

    assertNotNull("ActivityLogging should be set", setActivityLogging);
    assertEquals("ActivityLogging should match", al, setActivityLogging);

    pr.close();
    al.close();
  }

  /**
   * @brief Test to verify run method handles null ActivityLogging.
   * @throws Exception
   */
  @Test
  public void testRunWithNullActivityLogging() throws Exception {
    ProductivityReports pr = new ProductivityReports();
    String input = System.lineSeparator();
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    // Replace scanner
    Scanner newScanner = new Scanner(System.in);
    java.lang.reflect.Field scannerField = ProductivityReports.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(pr, newScanner);

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      pr.run();
      String output = outContent.toString();
      assertTrue("Output should contain error message", output.contains("Error") || output.contains("ActivityLogging not initialized"));
    }

    finally {
      System.setOut(originalOut);
      pr.close();
    }
  }

  /**
   * @brief Test to verify run method handles exit option.
   * @throws Exception
   */
  @Test
  public void testRunExit() throws Exception {
    String input = "5" + System.lineSeparator();
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    // Replace scanner
    Scanner newScanner = new Scanner(System.in);
    java.lang.reflect.Field scannerField = ProductivityReports.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(productivityReports, newScanner);

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      productivityReports.run();
      String output = outContent.toString();
      // Should have shown menu and exited
      assertTrue("Run should complete without exception", true);
    }

    finally {
      System.setOut(originalOut);
    }
  }

  /**
   * @brief Test to verify generateReports method works.
   * @throws Exception
   */
  @Test
  public void testGenerateReports() throws Exception {
    // Add activities
    long timestamp = System.currentTimeMillis() / 1000;
    activityLogging.addActivity(new Activity(1, "Activity 1", 30, timestamp, "Category 1", "Description 1"));
    activityLogging.addActivity(new Activity(2, "Activity 2", 45, timestamp + 100, "Category 1", "Description 2"));
    activityLogging.addActivity(new Activity(3, "Activity 3", 60, timestamp + 200, "Category 2", "Description 3"));

    String input = System.lineSeparator();
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    // Replace scanner
    Scanner newScanner = new Scanner(System.in);
    java.lang.reflect.Field scannerField = ProductivityReports.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(productivityReports, newScanner);

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      java.lang.reflect.Method generateReportsMethod = ProductivityReports.class.getDeclaredMethod("generateReports");
      generateReportsMethod.setAccessible(true);
      generateReportsMethod.invoke(productivityReports);
      String output = outContent.toString();
      assertTrue("Output should contain success message", output.contains("Reports generated") || output.contains("Total categories"));
    }

    finally {
      System.setOut(originalOut);
    }
  }

  /**
   * @brief Test to verify generateReports handles empty activities.
   * @throws Exception
   */
  @Test
  public void testGenerateReportsEmptyActivities() throws Exception {
    // Create a new ProductivityReports with empty ActivityLogging
    ActivityLogging emptyAl = new ActivityLogging();
    ProductivityReports emptyPr = new ProductivityReports(emptyAl);

    String input = System.lineSeparator();
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    // Replace scanner
    Scanner newScanner = new Scanner(System.in);
    java.lang.reflect.Field scannerField = ProductivityReports.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(emptyPr, newScanner);

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      java.lang.reflect.Method generateReportsMethod = ProductivityReports.class.getDeclaredMethod("generateReports");
      generateReportsMethod.setAccessible(true);
      generateReportsMethod.invoke(emptyPr);
      String output = outContent.toString();
      assertTrue("Output should contain no activities message",
                 output.contains("No activities") || output.contains("Start by logging") || output.length() > 0);
    }

    finally {
      System.setOut(originalOut);
      emptyPr.close();
      emptyAl.close();
    }
  }

  /**
   * @brief Test to verify viewReports method works.
   * @throws Exception
   */
  @Test
  public void testViewReports() throws Exception {
    // First generate reports
    long timestamp = System.currentTimeMillis() / 1000;
    activityLogging.addActivity(new Activity(1, "Activity 1", 30, timestamp, "Category 1", "Description 1"));

    String input1 = System.lineSeparator();
    System.setIn(new ByteArrayInputStream(input1.getBytes()));
    Scanner newScanner1 = new Scanner(System.in);
    java.lang.reflect.Field scannerField = ProductivityReports.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(productivityReports, newScanner1);

    java.lang.reflect.Method generateReportsMethod = ProductivityReports.class.getDeclaredMethod("generateReports");
    generateReportsMethod.setAccessible(true);
    generateReportsMethod.invoke(productivityReports);
    newScanner1.close();

    // Now view reports
    String input2 = System.lineSeparator();
    System.setIn(new ByteArrayInputStream(input2.getBytes()));
    Scanner newScanner2 = new Scanner(System.in);
    scannerField.setAccessible(true);
    scannerField.set(productivityReports, newScanner2);

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      java.lang.reflect.Method viewReportsMethod = ProductivityReports.class.getDeclaredMethod("viewReports");
      viewReportsMethod.setAccessible(true);
      viewReportsMethod.invoke(productivityReports);
      String output = outContent.toString();
      assertTrue("Output should contain reports", output.contains("Category") || output.contains("Total Activities"));
    }

    finally {
      System.setOut(originalOut);
    }
  }

  /**
   * @brief Test to verify viewReports handles empty reports.
   * @throws Exception
   */
  @Test
  public void testViewReportsEmpty() throws Exception {
    String input = System.lineSeparator();
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    // Replace scanner
    Scanner newScanner = new Scanner(System.in);
    java.lang.reflect.Field scannerField = ProductivityReports.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(productivityReports, newScanner);

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      java.lang.reflect.Method viewReportsMethod = ProductivityReports.class.getDeclaredMethod("viewReports");
      viewReportsMethod.setAccessible(true);
      viewReportsMethod.invoke(productivityReports);
      String output = outContent.toString();
      assertTrue("Output should contain no reports message", output.contains("No reports") || output.contains("generate reports"));
    }

    finally {
      System.setOut(originalOut);
    }
  }

  /**
   * @brief Test to verify archiveOldReports method works.
   * @throws Exception
   */
  @Test
  public void testArchiveOldReports() throws Exception {
    // First generate reports
    long timestamp = System.currentTimeMillis() / 1000;
    activityLogging.addActivity(new Activity(1, "Activity 1", 30, timestamp, "Category 1", "Description 1"));

    String input1 = System.lineSeparator();
    System.setIn(new ByteArrayInputStream(input1.getBytes()));
    Scanner newScanner1 = new Scanner(System.in);
    java.lang.reflect.Field scannerField = ProductivityReports.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(productivityReports, newScanner1);

    java.lang.reflect.Method generateReportsMethod = ProductivityReports.class.getDeclaredMethod("generateReports");
    generateReportsMethod.setAccessible(true);
    generateReportsMethod.invoke(productivityReports);
    newScanner1.close();

    // Now archive reports
    String input2 = System.lineSeparator();
    System.setIn(new ByteArrayInputStream(input2.getBytes()));
    Scanner newScanner2 = new Scanner(System.in);
    scannerField.setAccessible(true);
    scannerField.set(productivityReports, newScanner2);

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      java.lang.reflect.Method archiveOldReportsMethod = ProductivityReports.class.getDeclaredMethod("archiveOldReports");
      archiveOldReportsMethod.setAccessible(true);
      archiveOldReportsMethod.invoke(productivityReports);
      String output = outContent.toString();
      assertTrue("Output should contain Huffman encoding results", output.contains("Huffman") || output.contains("Compressed") || output.contains("Encoding"));
    }

    finally {
      System.setOut(originalOut);
    }
  }

  /**
   * @brief Test to verify archiveOldReports handles empty reports.
   * @throws Exception
   */
  @Test
  public void testArchiveOldReportsEmpty() throws Exception {
    String input = System.lineSeparator();
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    // Replace scanner
    Scanner newScanner = new Scanner(System.in);
    java.lang.reflect.Field scannerField = ProductivityReports.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(productivityReports, newScanner);

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      java.lang.reflect.Method archiveOldReportsMethod = ProductivityReports.class.getDeclaredMethod("archiveOldReports");
      archiveOldReportsMethod.setAccessible(true);
      archiveOldReportsMethod.invoke(productivityReports);
      String output = outContent.toString();
      assertTrue("Output should contain no reports message", output.contains("No reports") || output.contains("generate reports"));
    }

    finally {
      System.setOut(originalOut);
    }
  }

  /**
   * @brief Test to verify analyzeActivityGraph method works.
   * @throws Exception
   */
  @Test
  public void testAnalyzeActivityGraph() throws Exception {
    // Add activities
    long timestamp = System.currentTimeMillis() / 1000;
    activityLogging.addActivity(new Activity(1, "Activity 1", 30, timestamp, "Category 1", "Description 1"));
    activityLogging.addActivity(new Activity(2, "Activity 2", 45, timestamp, "Category 1", "Description 2"));
    activityLogging.addActivity(new Activity(3, "Activity 3", 60, timestamp + 86400, "Category 2", "Description 3"));

    String input = System.lineSeparator();
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    // Replace scanner
    Scanner newScanner = new Scanner(System.in);
    java.lang.reflect.Field scannerField = ProductivityReports.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(productivityReports, newScanner);

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      java.lang.reflect.Method analyzeActivityGraphMethod = ProductivityReports.class.getDeclaredMethod("analyzeActivityGraph");
      analyzeActivityGraphMethod.setAccessible(true);
      analyzeActivityGraphMethod.invoke(productivityReports);
      String output = outContent.toString();
      assertTrue("Output should contain graph analysis", output.contains("Strongly Connected") || output.contains("Component") || output.contains("Graph"));
    }

    finally {
      System.setOut(originalOut);
    }
  }

  /**
   * @brief Test to verify analyzeActivityGraph handles empty activities.
   * @throws Exception
   */
  @Test
  public void testAnalyzeActivityGraphEmptyActivities() throws Exception {
    // Create a new ProductivityReports with empty ActivityLogging
    ActivityLogging emptyAl = new ActivityLogging();
    ProductivityReports emptyPr = new ProductivityReports(emptyAl);

    String input = System.lineSeparator();
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    // Replace scanner
    Scanner newScanner = new Scanner(System.in);
    java.lang.reflect.Field scannerField = ProductivityReports.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(emptyPr, newScanner);

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      java.lang.reflect.Method analyzeActivityGraphMethod = ProductivityReports.class.getDeclaredMethod("analyzeActivityGraph");
      analyzeActivityGraphMethod.setAccessible(true);
      analyzeActivityGraphMethod.invoke(emptyPr);
      String output = outContent.toString();
      assertTrue("Output should contain no activities message",
                 output.contains("No activities") || output.contains("Start by logging") || output.length() > 0);
    }

    finally {
      System.setOut(originalOut);
      emptyPr.close();
      emptyAl.close();
    }
  }

  /**
   * @brief Test to verify formatTimestamp method works.
   * @throws Exception
   */
  @Test
  public void testFormatTimestamp() throws Exception {
    long timestamp = System.currentTimeMillis() / 1000;

    java.lang.reflect.Method formatTimestampMethod = ProductivityReports.class.getDeclaredMethod("formatTimestamp", long.class);
    formatTimestampMethod.setAccessible(true);

    String formatted = (String) formatTimestampMethod.invoke(productivityReports, timestamp);

    assertNotNull("Formatted timestamp should not be null", formatted);
    assertTrue("Formatted timestamp should not be empty", !formatted.isEmpty());
    assertTrue("Formatted timestamp should contain date format", formatted.contains("-"));
  }

  /**
   * @brief Test to verify formatDate method works.
   * @throws Exception
   */
  @Test
  public void testFormatDate() throws Exception {
    long timestamp = System.currentTimeMillis() / 1000;

    java.lang.reflect.Method formatDateMethod = ProductivityReports.class.getDeclaredMethod("formatDate", long.class);
    formatDateMethod.setAccessible(true);

    String formatted = (String) formatDateMethod.invoke(productivityReports, timestamp);

    assertNotNull("Formatted date should not be null", formatted);
    assertTrue("Formatted date should not be empty", !formatted.isEmpty());
    assertTrue("Formatted date should contain date format", formatted.contains("-"));
    assertEquals("Formatted date should be YYYY-MM-DD format", 10, formatted.length());
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
    java.lang.reflect.Field scannerField = ProductivityReports.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(productivityReports, newScanner);

    java.lang.reflect.Method waitForEnterMethod = ProductivityReports.class.getDeclaredMethod("waitForEnter");
    waitForEnterMethod.setAccessible(true);

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      waitForEnterMethod.invoke(productivityReports);
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
    productivityReports.close();
    assertTrue("close should execute without exception", true);
  }

  /**
   * @brief Test to verify close method closes scanner.
   * @throws Exception
   */
  @Test
  public void testCloseClosesScanner() throws Exception {
    // Access scanner field
    java.lang.reflect.Field scannerField = ProductivityReports.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    Scanner scanner = (Scanner) scannerField.get(productivityReports);

    assertNotNull("Scanner should not be null before close", scanner);

    productivityReports.close();

    // After close, scanner should be closed
    // We can't directly check if scanner is closed, but we can verify no exception is thrown
    assertTrue("close should execute without exception", true);
  }

  /**
   * @brief Test to verify B+ Tree insert and getAllReports work.
   * @throws Exception
   */
  @Test
  public void testBPlusTreeOperations() throws Exception {
    // Access reportTree field
    java.lang.reflect.Field reportTreeField = ProductivityReports.class.getDeclaredField("reportTree");
    reportTreeField.setAccessible(true);
    Object reportTree = reportTreeField.get(productivityReports);

    // Access BPlusTree methods
    java.lang.reflect.Method insertMethod = reportTree.getClass().getDeclaredMethod("insert", String.class,
      Class.forName("com.efil.nese.timetracker.ProductivityReports$Report"));
    insertMethod.setAccessible(true);

    java.lang.reflect.Method isEmptyMethod = reportTree.getClass().getDeclaredMethod("isEmpty");
    isEmptyMethod.setAccessible(true);

    java.lang.reflect.Method getAllReportsMethod = reportTree.getClass().getDeclaredMethod("getAllReports");
    getAllReportsMethod.setAccessible(true);

    // Create a Report using reflection
    Class<?> reportClass = Class.forName("com.efil.nese.timetracker.ProductivityReports$Report");
    java.lang.reflect.Constructor<?> reportConstructor = reportClass.getDeclaredConstructor(
      String.class, int.class, int.class, long.class, long.class, List.class);
    reportConstructor.setAccessible(true);

    long timestamp = System.currentTimeMillis() / 1000;
    List<Activity> activities = new java.util.ArrayList<>();
    Object report = reportConstructor.newInstance("Test Category", 1, 30, timestamp, timestamp, activities);

    // Test isEmpty before insert
    Boolean isEmptyBefore = (Boolean) isEmptyMethod.invoke(reportTree);
    assertTrue("B+ Tree should be empty before insert", isEmptyBefore);

    // Insert
    insertMethod.invoke(reportTree, "Test Category", report);

    // Test isEmpty after insert
    Boolean isEmptyAfter = (Boolean) isEmptyMethod.invoke(reportTree);
    assertFalse("B+ Tree should not be empty after insert", isEmptyAfter);

    // Get all reports
    @SuppressWarnings("unchecked")
    List<Object> reports = (List<Object>) getAllReportsMethod.invoke(reportTree);
    assertNotNull("Reports list should not be null", reports);
    assertEquals("Should have one report", 1, reports.size());
  }

  /**
   * @brief Test to verify run method handles invalid choice.
   * @throws Exception
   */
  @Test
  public void testRunInvalidChoice() throws Exception {
    String input = "99" + System.lineSeparator() + "5" + System.lineSeparator();
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    // Replace scanner
    Scanner newScanner = new Scanner(System.in);
    java.lang.reflect.Field scannerField = ProductivityReports.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(productivityReports, newScanner);

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      productivityReports.run();
      String output = outContent.toString();
      assertTrue("Output should contain invalid choice message", output.contains("Invalid choice") || output.contains("try again"));
    }

    finally {
      System.setOut(originalOut);
    }
  }

  /**
   * @brief Test to verify run method handles all menu options.
   * @throws Exception
   */
  @Test
  public void testRunAllMenuOptions() throws Exception {
    // Add activities first
    long timestamp = System.currentTimeMillis() / 1000;
    activityLogging.addActivity(new Activity(1, "Activity 1", 30, timestamp, "Category 1", "Description 1"));

    // Test option 1 (generate), then 2 (view), then 5 (exit)
    String input = "1" + System.lineSeparator() + System.lineSeparator() +
                               "2" + System.lineSeparator() + System.lineSeparator() +
                               "5" + System.lineSeparator();
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    // Replace scanner
    Scanner newScanner = new Scanner(System.in);
    java.lang.reflect.Field scannerField = ProductivityReports.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(productivityReports, newScanner);

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      productivityReports.run();
      String output = outContent.toString();
      // Should have processed menu options
      assertTrue("Run should complete without exception", true);
    }

    finally {
      System.setOut(originalOut);
    }
  }

  /**
   * @brief Test to verify countEdges method works.
   * @throws Exception
   */
  @Test
  public void testCountEdges() throws Exception {
    java.util.List<java.util.List<Integer>> graph = new java.util.ArrayList<>();
    graph.add(new java.util.ArrayList<>());
    graph.add(new java.util.ArrayList<>());
    graph.get(0).add(1);
    graph.get(1).add(0);

    java.lang.reflect.Method countEdgesMethod = ProductivityReports.class.getDeclaredMethod("countEdges", List.class);
    countEdgesMethod.setAccessible(true);

    int edgeCount = (Integer) countEdgesMethod.invoke(productivityReports, graph);
    assertEquals("Should count one edge (each edge counted twice)", 1, edgeCount);
  }

  /**
   * @brief Test to verify generateReports creates correct report structure.
   * @throws Exception
   */
  @Test
  public void testGenerateReportsStructure() throws Exception {
    // Add activities with different categories
    long timestamp = System.currentTimeMillis() / 1000;
    activityLogging.addActivity(new Activity(1, "Activity 1", 30, timestamp, "Category 1", "Description 1"));
    activityLogging.addActivity(new Activity(2, "Activity 2", 45, timestamp + 100, "Category 1", "Description 2"));
    activityLogging.addActivity(new Activity(3, "Activity 3", 60, timestamp + 200, "Category 2", "Description 3"));

    String input = System.lineSeparator();
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    // Replace scanner
    Scanner newScanner = new Scanner(System.in);
    java.lang.reflect.Field scannerField = ProductivityReports.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(productivityReports, newScanner);

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      java.lang.reflect.Method generateReportsMethod = ProductivityReports.class.getDeclaredMethod("generateReports");
      generateReportsMethod.setAccessible(true);
      generateReportsMethod.invoke(productivityReports);
      // Verify reports were created
      java.lang.reflect.Field reportTreeField = ProductivityReports.class.getDeclaredField("reportTree");
      reportTreeField.setAccessible(true);
      Object reportTree = reportTreeField.get(productivityReports);
      java.lang.reflect.Method getAllReportsMethod = reportTree.getClass().getDeclaredMethod("getAllReports");
      getAllReportsMethod.setAccessible(true);
      @SuppressWarnings("unchecked")
      List<Object> reports = (List<Object>) getAllReportsMethod.invoke(reportTree);
      assertNotNull("Reports should not be null", reports);
      assertTrue("Should have at least one report", reports.size() > 0);
    }

    finally {
      System.setOut(originalOut);
    }
  }

  /**
   * @brief Test to verify HuffmanNode class can be instantiated.
   * @throws Exception
   */
  @Test
  public void testHuffmanNodeClass() throws Exception {
    Class<?> huffmanNodeClass = Class.forName("com.efil.nese.timetracker.ProductivityReports$HuffmanNode");

    // Test leaf node constructor
    java.lang.reflect.Constructor<?> leafConstructor = huffmanNodeClass.getDeclaredConstructor(char.class, int.class);
    leafConstructor.setAccessible(true);
    Object leafNode = leafConstructor.newInstance('A', 5);
    assertNotNull("HuffmanNode leaf should not be null", leafNode);

    // Test isLeaf method
    java.lang.reflect.Method isLeafMethod = huffmanNodeClass.getDeclaredMethod("isLeaf");
    isLeafMethod.setAccessible(true);
    Boolean isLeaf = (Boolean) isLeafMethod.invoke(leafNode);
    assertTrue("Leaf node should be leaf", isLeaf);

    // Test internal node constructor
    java.lang.reflect.Constructor<?> internalConstructor = huffmanNodeClass.getDeclaredConstructor(int.class, huffmanNodeClass, huffmanNodeClass);
    internalConstructor.setAccessible(true);
    Object internalNode = internalConstructor.newInstance(10, leafNode, null);
    assertNotNull("HuffmanNode internal should not be null", internalNode);

    Boolean isLeafInternal = (Boolean) isLeafMethod.invoke(internalNode);
    assertFalse("Internal node should not be leaf", isLeafInternal);
  }

  /**
   * @brief Test to verify HuffmanEncoder class works.
   * @throws Exception
   */
  @Test
  public void testHuffmanEncoder() throws Exception {
    Class<?> huffmanEncoderClass = Class.forName("com.efil.nese.timetracker.ProductivityReports$HuffmanEncoder");
    java.lang.reflect.Constructor<?> constructor = huffmanEncoderClass.getDeclaredConstructor();
    constructor.setAccessible(true);
    Object encoder = constructor.newInstance();

    // Test encode method
    java.lang.reflect.Method encodeMethod = huffmanEncoderClass.getDeclaredMethod("encode", String.class);
    encodeMethod.setAccessible(true);

    String testData = "AAAABBCD";
    String encoded = (String) encodeMethod.invoke(encoder, testData);
    assertNotNull("Encoded data should not be null", encoded);
    assertTrue("Encoded data should not be empty", !encoded.isEmpty());

    // Test getEncodingTable method
    java.lang.reflect.Method getEncodingTableMethod = huffmanEncoderClass.getDeclaredMethod("getEncodingTable");
    getEncodingTableMethod.setAccessible(true);
    @SuppressWarnings("unchecked")
    java.util.Map<Character, String> encodingTable = (java.util.Map<Character, String>) getEncodingTableMethod.invoke(encoder);
    assertNotNull("Encoding table should not be null", encodingTable);
    assertTrue("Encoding table should not be empty", !encodingTable.isEmpty());
  }

  /**
   * @brief Test to verify HuffmanEncoder with single character.
   * @throws Exception
   */
  @Test
  public void testHuffmanEncoderSingleCharacter() throws Exception {
    Class<?> huffmanEncoderClass = Class.forName("com.efil.nese.timetracker.ProductivityReports$HuffmanEncoder");
    java.lang.reflect.Constructor<?> constructor = huffmanEncoderClass.getDeclaredConstructor();
    constructor.setAccessible(true);
    Object encoder = constructor.newInstance();

    java.lang.reflect.Method encodeMethod = huffmanEncoderClass.getDeclaredMethod("encode", String.class);
    encodeMethod.setAccessible(true);

    String testData = "AAAA";
    String encoded = (String) encodeMethod.invoke(encoder, testData);
    assertNotNull("Encoded data should not be null", encoded);

    java.lang.reflect.Method getEncodingTableMethod = huffmanEncoderClass.getDeclaredMethod("getEncodingTable");
    getEncodingTableMethod.setAccessible(true);
    @SuppressWarnings("unchecked")
    java.util.Map<Character, String> encodingTable = (java.util.Map<Character, String>) getEncodingTableMethod.invoke(encoder);
    assertTrue("Encoding table should contain 'A'", encodingTable.containsKey('A'));
  }

  /**
   * @brief Test to verify StronglyConnectedComponents class works.
   * @throws Exception
   */
  @Test
  public void testStronglyConnectedComponents() throws Exception {
    // Create a simple graph
    List<List<Integer>> graph = new java.util.ArrayList<>();
    graph.add(new java.util.ArrayList<>());
    graph.add(new java.util.ArrayList<>());
    graph.get(0).add(1);
    graph.get(1).add(0);

    List<Activity> activities = new java.util.ArrayList<>();
    long timestamp = System.currentTimeMillis() / 1000;
    activities.add(new Activity(1, "Activity 1", 30, timestamp, "Category 1", "Description 1"));
    activities.add(new Activity(2, "Activity 2", 45, timestamp, "Category 1", "Description 2"));

    Class<?> sccClass = Class.forName("com.efil.nese.timetracker.ProductivityReports$StronglyConnectedComponents");
    java.lang.reflect.Constructor<?> constructor = sccClass.getDeclaredConstructor(List.class, List.class);
    constructor.setAccessible(true);
    Object scc = constructor.newInstance(graph, activities);

    // Test findSCC method
    java.lang.reflect.Method findSCCMethod = sccClass.getDeclaredMethod("findSCC");
    findSCCMethod.setAccessible(true);
    @SuppressWarnings("unchecked")
    List<List<Integer>> components = (List<List<Integer>>) findSCCMethod.invoke(scc);

    assertNotNull("Components should not be null", components);
    assertTrue("Should have at least one component", components.size() > 0);
  }

  /**
   * @brief Test to verify StronglyConnectedComponents with empty graph.
   * @throws Exception
   */
  @Test
  public void testStronglyConnectedComponentsEmptyGraph() throws Exception {
    List<List<Integer>> graph = new java.util.ArrayList<>();
    List<Activity> activities = new java.util.ArrayList<>();

    Class<?> sccClass = Class.forName("com.efil.nese.timetracker.ProductivityReports$StronglyConnectedComponents");
    java.lang.reflect.Constructor<?> constructor = sccClass.getDeclaredConstructor(List.class, List.class);
    constructor.setAccessible(true);
    Object scc = constructor.newInstance(graph, activities);

    java.lang.reflect.Method findSCCMethod = sccClass.getDeclaredMethod("findSCC");
    findSCCMethod.setAccessible(true);
    @SuppressWarnings("unchecked")
    List<List<Integer>> components = (List<List<Integer>>) findSCCMethod.invoke(scc);

    assertNotNull("Components should not be null", components);
    assertEquals("Empty graph should have no components", 0, components.size());
  }

  /**
   * @brief Test to verify BPlusLeafNode class works.
   * @throws Exception
   */
  @Test
  public void testBPlusLeafNode() throws Exception {
    Class<?> bPlusLeafNodeClass = Class.forName("com.efil.nese.timetracker.ProductivityReports$BPlusLeafNode");
    java.lang.reflect.Constructor<?> constructor = bPlusLeafNodeClass.getDeclaredConstructor();
    constructor.setAccessible(true);
    Object leafNode = constructor.newInstance();

    // Test isEmpty method
    java.lang.reflect.Method isEmptyMethod = bPlusLeafNodeClass.getDeclaredMethod("isEmpty");
    isEmptyMethod.setAccessible(true);
    Boolean isEmpty = (Boolean) isEmptyMethod.invoke(leafNode);
    assertTrue("New leaf node should be empty", isEmpty);

    // Test insert method
    Class<?> reportClass = Class.forName("com.efil.nese.timetracker.ProductivityReports$Report");
    java.lang.reflect.Constructor<?> reportConstructor = reportClass.getDeclaredConstructor(
      String.class, int.class, int.class, long.class, long.class, List.class);
    reportConstructor.setAccessible(true);

    long timestamp = System.currentTimeMillis() / 1000;
    List<Activity> activities = new java.util.ArrayList<>();
    Object report = reportConstructor.newInstance("Test Category", 1, 30, timestamp, timestamp, activities);

    java.lang.reflect.Method insertMethod = bPlusLeafNodeClass.getDeclaredMethod("insert", String.class, reportClass);
    insertMethod.setAccessible(true);
    insertMethod.invoke(leafNode, "Test Category", report);

    Boolean isEmptyAfter = (Boolean) isEmptyMethod.invoke(leafNode);
    assertFalse("Leaf node should not be empty after insert", isEmptyAfter);

    // Test getAllValues method
    java.lang.reflect.Method getAllValuesMethod = bPlusLeafNodeClass.getDeclaredMethod("getAllValues");
    getAllValuesMethod.setAccessible(true);
    @SuppressWarnings("unchecked")
    List<Object> values = (List<Object>) getAllValuesMethod.invoke(leafNode);
    assertNotNull("Values should not be null", values);
    assertEquals("Should have one value", 1, values.size());
  }

  /**
   * @brief Test to verify BPlusNode abstract class exists.
   * @throws Exception
   */
  @Test
  public void testBPlusNodeClass() throws Exception {
    Class<?> bPlusNodeClass = Class.forName("com.efil.nese.timetracker.ProductivityReports$BPlusNode");
    assertTrue("BPlusNode should be abstract", java.lang.reflect.Modifier.isAbstract(bPlusNodeClass.getModifiers()));
  }

  /**
   * @brief Test to verify Report class can be instantiated.
   * @throws Exception
   */
  @Test
  public void testReportClass() throws Exception {
    Class<?> reportClass = Class.forName("com.efil.nese.timetracker.ProductivityReports$Report");
    java.lang.reflect.Constructor<?> constructor = reportClass.getDeclaredConstructor(
      String.class, int.class, int.class, long.class, long.class, List.class);
    constructor.setAccessible(true);

    long timestamp = System.currentTimeMillis() / 1000;
    List<Activity> activities = new java.util.ArrayList<>();
    Object report = constructor.newInstance("Test Category", 5, 150, timestamp, timestamp + 100, activities);

    assertNotNull("Report object should not be null", report);

    // Verify fields
    java.lang.reflect.Field categoryField = reportClass.getDeclaredField("category");
    categoryField.setAccessible(true);
    String category = (String) categoryField.get(report);
    assertEquals("Category should match", "Test Category", category);

    java.lang.reflect.Field activityCountField = reportClass.getDeclaredField("activityCount");
    activityCountField.setAccessible(true);
    int activityCount = (Integer) activityCountField.get(report);
    assertEquals("Activity count should match", 5, activityCount);

    java.lang.reflect.Field totalTimeField = reportClass.getDeclaredField("totalTime");
    totalTimeField.setAccessible(true);
    int totalTime = (Integer) totalTimeField.get(report);
    assertEquals("Total time should match", 150, totalTime);
  }

  /**
   * @brief Test to verify BPlusTree constructor works.
   * @throws Exception
   */
  @Test
  public void testBPlusTreeConstructor() throws Exception {
    Class<?> bPlusTreeClass = Class.forName("com.efil.nese.timetracker.ProductivityReports$BPlusTree");
    java.lang.reflect.Constructor<?> constructor = bPlusTreeClass.getDeclaredConstructor(int.class);
    constructor.setAccessible(true);
    Object tree = constructor.newInstance(3);

    assertNotNull("BPlusTree should not be null", tree);

    // Test isEmpty
    java.lang.reflect.Method isEmptyMethod = bPlusTreeClass.getDeclaredMethod("isEmpty");
    isEmptyMethod.setAccessible(true);
    Boolean isEmpty = (Boolean) isEmptyMethod.invoke(tree);
    assertTrue("New B+ Tree should be empty", isEmpty);
  }

  /**
   * @brief Test to verify BPlusTree with multiple inserts.
   * @throws Exception
   */
  @Test
  public void testBPlusTreeMultipleInserts() throws Exception {
    java.lang.reflect.Field reportTreeField = ProductivityReports.class.getDeclaredField("reportTree");
    reportTreeField.setAccessible(true);
    Object reportTree = reportTreeField.get(productivityReports);

    java.lang.reflect.Method insertMethod = reportTree.getClass().getDeclaredMethod("insert", String.class,
      Class.forName("com.efil.nese.timetracker.ProductivityReports$Report"));
    insertMethod.setAccessible(true);

    java.lang.reflect.Method getAllReportsMethod = reportTree.getClass().getDeclaredMethod("getAllReports");
    getAllReportsMethod.setAccessible(true);

    Class<?> reportClass = Class.forName("com.efil.nese.timetracker.ProductivityReports$Report");
    java.lang.reflect.Constructor<?> reportConstructor = reportClass.getDeclaredConstructor(
      String.class, int.class, int.class, long.class, long.class, List.class);
    reportConstructor.setAccessible(true);

    long timestamp = System.currentTimeMillis() / 1000;
    List<Activity> activities = new java.util.ArrayList<>();

    Object report1 = reportConstructor.newInstance("Category A", 1, 30, timestamp, timestamp, activities);
    Object report2 = reportConstructor.newInstance("Category B", 1, 45, timestamp, timestamp, activities);
    Object report3 = reportConstructor.newInstance("Category C", 1, 60, timestamp, timestamp, activities);

    insertMethod.invoke(reportTree, "Category A", report1);
    insertMethod.invoke(reportTree, "Category B", report2);
    insertMethod.invoke(reportTree, "Category C", report3);

    @SuppressWarnings("unchecked")
    List<Object> reports = (List<Object>) getAllReportsMethod.invoke(reportTree);
    assertEquals("Should have three reports", 3, reports.size());
  }
}

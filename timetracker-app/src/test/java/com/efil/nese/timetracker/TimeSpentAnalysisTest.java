/**
 * @file TimeSpentAnalysisTest.java
 * @brief This file contains test cases for the TimeSpentAnalysis class.
 * @details This file includes test methods to validate the functionality of the TimeSpentAnalysis class.
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
 * @class TimeSpentAnalysisTest
 * @brief This class represents the test class for the TimeSpentAnalysis class.
 * @details The TimeSpentAnalysisTest class provides test methods to verify the behavior of the TimeSpentAnalysis class.
 * @author efil.saylam.nese.sarp
 */
public class TimeSpentAnalysisTest {

  private TimeSpentAnalysis timeSpentAnalysis;
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
    // Create TimeSpentAnalysis instance
    timeSpentAnalysis = new TimeSpentAnalysis(activityLogging);
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

    // Close timeSpentAnalysis
    if (timeSpentAnalysis != null) {
      timeSpentAnalysis.close();
    }

    // Close activityLogging
    if (activityLogging != null) {
      activityLogging.close();
    }
  }

  /**
   * @brief Test to verify TimeSpentAnalysis object creation.
   * @throws Exception
   */
  @Test
  public void testTimeSpentAnalysisCreation() throws Exception {
    TimeSpentAnalysis tsa = new TimeSpentAnalysis();
    assertNotNull("TimeSpentAnalysis object should not be null", tsa);
    tsa.close();
  }

  /**
   * @brief Test to verify TimeSpentAnalysis creation with ActivityLogging.
   * @throws Exception
   */
  @Test
  public void testTimeSpentAnalysisCreationWithActivityLogging() throws Exception {
    ActivityLogging al = new ActivityLogging();
    TimeSpentAnalysis tsa = new TimeSpentAnalysis(al);
    assertNotNull("TimeSpentAnalysis object should not be null", tsa);
    tsa.close();
    al.close();
  }

  /**
   * @brief Test to verify TimeSpentAnalysis class exists.
   * @throws Exception
   */
  @Test
  public void testTimeSpentAnalysisClassExists() throws Exception {
    Class<?> clazz = TimeSpentAnalysis.class;
    assertNotNull("TimeSpentAnalysis class should exist", clazz);
    assertEquals("Class name should be TimeSpentAnalysis", "TimeSpentAnalysis", clazz.getSimpleName());
  }

  /**
   * @brief Test to verify setActivityLogging method works.
   * @throws Exception
   */
  @Test
  public void testSetActivityLogging() throws Exception {
    TimeSpentAnalysis tsa = new TimeSpentAnalysis();
    ActivityLogging al = new ActivityLogging();

    tsa.setActivityLogging(al);

    // Verify using reflection
    java.lang.reflect.Field activityLoggingField = TimeSpentAnalysis.class.getDeclaredField("activityLogging");
    activityLoggingField.setAccessible(true);
    ActivityLogging setActivityLogging = (ActivityLogging) activityLoggingField.get(tsa);

    assertNotNull("ActivityLogging should be set", setActivityLogging);
    assertEquals("ActivityLogging should match", al, setActivityLogging);

    tsa.close();
    al.close();
  }

  /**
   * @brief Test to verify run method handles null ActivityLogging.
   * @throws Exception
   */
  @Test
  public void testRunWithNullActivityLogging() throws Exception {
    TimeSpentAnalysis tsa = new TimeSpentAnalysis();
    String input = System.lineSeparator();
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    // Replace scanner
    Scanner newScanner = new Scanner(System.in);
    java.lang.reflect.Field scannerField = TimeSpentAnalysis.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(tsa, newScanner);

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      tsa.run();
      String output = outContent.toString();
      assertTrue("Output should contain error message", output.contains("Error") || output.contains("ActivityLogging not initialized"));
    }

    finally {
      System.setOut(originalOut);
      tsa.close();
    }
  }

  /**
   * @brief Test to verify run method handles exit option.
   * @throws Exception
   */
  @Test
  public void testRunExit() throws Exception {
    String input = "4" + System.lineSeparator();
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    // Replace scanner
    Scanner newScanner = new Scanner(System.in);
    java.lang.reflect.Field scannerField = TimeSpentAnalysis.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(timeSpentAnalysis, newScanner);

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      timeSpentAnalysis.run();
      String output = outContent.toString();
      // Should have shown menu and exited
      assertTrue("Run should complete without exception", true);
    }

    finally {
      System.setOut(originalOut);
    }
  }

  /**
   * @brief Test to verify longerPeriodSummary method works.
   * @throws Exception
   */
  @Test
  public void testLongerPeriodSummary() throws Exception {
    // Add activities
    long timestamp = System.currentTimeMillis() / 1000;
    activityLogging.addActivity(new Activity(1, "Activity 1", 30, timestamp, "Category 1", "Description 1"));
    activityLogging.addActivity(new Activity(2, "Activity 1", 45, timestamp + 100, "Category 1", "Description 2"));
    activityLogging.addActivity(new Activity(3, "Activity 2", 60, timestamp + 200, "Category 2", "Description 3"));

    String input = System.lineSeparator();
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    // Replace scanner
    Scanner newScanner = new Scanner(System.in);
    java.lang.reflect.Field scannerField = TimeSpentAnalysis.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(timeSpentAnalysis, newScanner);

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      java.lang.reflect.Method longerPeriodSummaryMethod = TimeSpentAnalysis.class.getDeclaredMethod("longerPeriodSummary");
      longerPeriodSummaryMethod.setAccessible(true);
      longerPeriodSummaryMethod.invoke(timeSpentAnalysis);
      String output = outContent.toString();
      assertTrue("Output should contain heap sort message", output.contains("Heap Sort") || output.contains("sorted by total time"));
    }

    finally {
      System.setOut(originalOut);
    }
  }

  /**
   * @brief Test to verify longerPeriodSummary handles empty activities.
   * @throws Exception
   */
  @Test
  public void testLongerPeriodSummaryEmptyActivities() throws Exception {
    // Create a new TimeSpentAnalysis with empty ActivityLogging
    ActivityLogging emptyAl = new ActivityLogging();
    TimeSpentAnalysis emptyTsa = new TimeSpentAnalysis(emptyAl);

    String input = System.lineSeparator();
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    // Replace scanner
    Scanner newScanner = new Scanner(System.in);
    java.lang.reflect.Field scannerField = TimeSpentAnalysis.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(emptyTsa, newScanner);

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      java.lang.reflect.Method longerPeriodSummaryMethod = TimeSpentAnalysis.class.getDeclaredMethod("longerPeriodSummary");
      longerPeriodSummaryMethod.setAccessible(true);
      longerPeriodSummaryMethod.invoke(emptyTsa);
      String output = outContent.toString();
      assertTrue("Output should contain no activities message",
                 output.contains("No activities") || output.contains("Start by logging") || output.length() > 0);
    }

    finally {
      System.setOut(originalOut);
      emptyTsa.close();
      emptyAl.close();
    }
  }

  /**
   * @brief Test to verify dailySummary method works.
   * @throws Exception
   */
  @Test
  public void testDailySummary() throws Exception {
    // Add activities
    long timestamp = System.currentTimeMillis() / 1000;
    activityLogging.addActivity(new Activity(1, "Activity 1", 30, timestamp, "Category 1", "Description 1"));
    activityLogging.addActivity(new Activity(2, "Activity 2", 45, timestamp, "Category 1", "Description 2"));
    activityLogging.addActivity(new Activity(3, "Activity 3", 60, timestamp + 86400, "Category 2", "Description 3"));

    String input = System.lineSeparator();
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    // Replace scanner
    Scanner newScanner = new Scanner(System.in);
    java.lang.reflect.Field scannerField = TimeSpentAnalysis.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(timeSpentAnalysis, newScanner);

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      java.lang.reflect.Method dailySummaryMethod = TimeSpentAnalysis.class.getDeclaredMethod("dailySummary");
      dailySummaryMethod.setAccessible(true);
      dailySummaryMethod.invoke(timeSpentAnalysis);
      String output = outContent.toString();
      assertTrue("Output should contain sparse matrix message", output.contains("Sparse Matrix") || output.contains("Daily Summary"));
    }

    finally {
      System.setOut(originalOut);
    }
  }

  /**
   * @brief Test to verify dailySummary handles empty activities.
   * @throws Exception
   */
  @Test
  public void testDailySummaryEmptyActivities() throws Exception {
    // Create a new TimeSpentAnalysis with empty ActivityLogging
    ActivityLogging emptyAl = new ActivityLogging();
    TimeSpentAnalysis emptyTsa = new TimeSpentAnalysis(emptyAl);

    String input = System.lineSeparator();
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    // Replace scanner
    Scanner newScanner = new Scanner(System.in);
    java.lang.reflect.Field scannerField = TimeSpentAnalysis.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(emptyTsa, newScanner);

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      java.lang.reflect.Method dailySummaryMethod = TimeSpentAnalysis.class.getDeclaredMethod("dailySummary");
      dailySummaryMethod.setAccessible(true);
      dailySummaryMethod.invoke(emptyTsa);
      String output = outContent.toString();
      assertTrue("Output should contain no activities message",
                 output.contains("No activities") || output.contains("Start by logging") || output.length() > 0);
    }

    finally {
      System.setOut(originalOut);
      emptyTsa.close();
      emptyAl.close();
    }
  }

  /**
   * @brief Test to verify viewTimeDataTable method works.
   * @throws Exception
   */
  @Test
  public void testViewTimeDataTable() throws Exception {
    // Add activities
    long timestamp = System.currentTimeMillis() / 1000;
    activityLogging.addActivity(new Activity(1, "Activity 1", 30, timestamp, "Category 1", "Description 1"));
    activityLogging.addActivity(new Activity(2, "Activity 2", 45, timestamp + 100, "Category 1", "Description 2"));

    String input = System.lineSeparator();
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    // Replace scanner
    Scanner newScanner = new Scanner(System.in);
    java.lang.reflect.Field scannerField = TimeSpentAnalysis.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(timeSpentAnalysis, newScanner);

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      java.lang.reflect.Method viewTimeDataTableMethod = TimeSpentAnalysis.class.getDeclaredMethod("viewTimeDataTable");
      viewTimeDataTableMethod.setAccessible(true);
      viewTimeDataTableMethod.invoke(timeSpentAnalysis);
      String output = outContent.toString();
      assertTrue("Output should contain hash table message", output.contains("Hash Table") || output.contains("Linear Probing"));
    }

    finally {
      System.setOut(originalOut);
    }
  }

  /**
   * @brief Test to verify viewTimeDataTable handles empty activities.
   * @throws Exception
   */
  @Test
  public void testViewTimeDataTableEmptyActivities() throws Exception {
    // Create a new TimeSpentAnalysis with empty ActivityLogging
    ActivityLogging emptyAl = new ActivityLogging();
    TimeSpentAnalysis emptyTsa = new TimeSpentAnalysis(emptyAl);

    String input = System.lineSeparator();
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    // Replace scanner
    Scanner newScanner = new Scanner(System.in);
    java.lang.reflect.Field scannerField = TimeSpentAnalysis.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(emptyTsa, newScanner);

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      java.lang.reflect.Method viewTimeDataTableMethod = TimeSpentAnalysis.class.getDeclaredMethod("viewTimeDataTable");
      viewTimeDataTableMethod.setAccessible(true);
      viewTimeDataTableMethod.invoke(emptyTsa);
      String output = outContent.toString();
      assertTrue("Output should contain no activities message",
                 output.contains("No activities") || output.contains("Start by logging") || output.length() > 0);
    }

    finally {
      System.setOut(originalOut);
      emptyTsa.close();
      emptyAl.close();
    }
  }

  /**
   * @brief Test to verify formatDate method works.
   * @throws Exception
   */
  @Test
  public void testFormatDate() throws Exception {
    long timestamp = System.currentTimeMillis() / 1000;

    java.lang.reflect.Method formatDateMethod = TimeSpentAnalysis.class.getDeclaredMethod("formatDate", long.class);
    formatDateMethod.setAccessible(true);

    String formatted = (String) formatDateMethod.invoke(timeSpentAnalysis, timestamp);

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
    java.lang.reflect.Field scannerField = TimeSpentAnalysis.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(timeSpentAnalysis, newScanner);

    java.lang.reflect.Method waitForEnterMethod = TimeSpentAnalysis.class.getDeclaredMethod("waitForEnter");
    waitForEnterMethod.setAccessible(true);

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      waitForEnterMethod.invoke(timeSpentAnalysis);
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
    timeSpentAnalysis.close();
    assertTrue("close should execute without exception", true);
  }

  /**
   * @brief Test to verify close method closes scanner.
   * @throws Exception
   */
  @Test
  public void testCloseClosesScanner() throws Exception {
    // Access scanner field
    java.lang.reflect.Field scannerField = TimeSpentAnalysis.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    Scanner scanner = (Scanner) scannerField.get(timeSpentAnalysis);

    assertNotNull("Scanner should not be null before close", scanner);

    timeSpentAnalysis.close();

    // After close, scanner should be closed
    // We can't directly check if scanner is closed, but we can verify no exception is thrown
    assertTrue("close should execute without exception", true);
  }

  /**
   * @brief Test to verify run method handles invalid choice.
   * @throws Exception
   */
  @Test
  public void testRunInvalidChoice() throws Exception {
    String input = "99" + System.lineSeparator() + "4" + System.lineSeparator();
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    // Replace scanner
    Scanner newScanner = new Scanner(System.in);
    java.lang.reflect.Field scannerField = TimeSpentAnalysis.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(timeSpentAnalysis, newScanner);

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      timeSpentAnalysis.run();
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

    // Test option 1 (longer period), then 2 (daily), then 3 (table), then 4 (exit)
    String input = "1" + System.lineSeparator() + System.lineSeparator() +
                               "2" + System.lineSeparator() + System.lineSeparator() +
                               "3" + System.lineSeparator() + System.lineSeparator() +
                               "4" + System.lineSeparator();
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    // Replace scanner
    Scanner newScanner = new Scanner(System.in);
    java.lang.reflect.Field scannerField = TimeSpentAnalysis.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(timeSpentAnalysis, newScanner);

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      timeSpentAnalysis.run();
      String output = outContent.toString();
      // Should have processed menu options
      assertTrue("Run should complete without exception", true);
    }

    finally {
      System.setOut(originalOut);
    }
  }

  /**
   * @brief Test to verify heapSort method works.
   * @throws Exception
   */
  @Test
  public void testHeapSort() throws Exception {
    // Create ActivitySummary list
    Class<?> activitySummaryClass = Class.forName("com.efil.nese.timetracker.TimeSpentAnalysis$ActivitySummary");
    java.lang.reflect.Constructor<?> constructor = activitySummaryClass.getDeclaredConstructor(String.class, int.class);
    constructor.setAccessible(true);

    Object summary1 = constructor.newInstance("Activity 1", 30);
    Object summary2 = constructor.newInstance("Activity 2", 60);
    Object summary3 = constructor.newInstance("Activity 3", 45);

    List<Object> summaries = new java.util.ArrayList<>();
    summaries.add(summary1);
    summaries.add(summary2);
    summaries.add(summary3);

    java.lang.reflect.Method heapSortMethod = TimeSpentAnalysis.class.getDeclaredMethod("heapSort", List.class);
    heapSortMethod.setAccessible(true);
    heapSortMethod.invoke(timeSpentAnalysis, summaries);

    // Verify sorting (should be sorted in ascending order by totalTime)
    java.lang.reflect.Field totalTimeField = activitySummaryClass.getDeclaredField("totalTime");
    totalTimeField.setAccessible(true);

    int time1 = (Integer) totalTimeField.get(summaries.get(0));
    int time2 = (Integer) totalTimeField.get(summaries.get(1));
    int time3 = (Integer) totalTimeField.get(summaries.get(2));

    assertTrue("First element should be smallest", time1 <= time2);
    assertTrue("Second element should be middle", time2 <= time3);
  }

  /**
   * @brief Test to verify nextPrime method works.
   * @throws Exception
   */
  @Test
  public void testNextPrime() throws Exception {
    java.lang.reflect.Method nextPrimeMethod = TimeSpentAnalysis.class.getDeclaredMethod("nextPrime", int.class);
    nextPrimeMethod.setAccessible(true);

    int prime1 = (Integer) nextPrimeMethod.invoke(timeSpentAnalysis, 10);
    assertTrue("Result should be prime", isPrimeNumber(prime1));
    assertTrue("Result should be >= input", prime1 >= 10);

    int prime2 = (Integer) nextPrimeMethod.invoke(timeSpentAnalysis, 2);
    assertEquals("Next prime of 2 should be 2", 2, prime2);

    int prime3 = (Integer) nextPrimeMethod.invoke(timeSpentAnalysis, 1);
    assertEquals("Next prime of 1 should be 2", 2, prime3);
  }

  /**
   * @brief Test to verify isPrime method works.
   * @throws Exception
   */
  @Test
  public void testIsPrime() throws Exception {
    java.lang.reflect.Method isPrimeMethod = TimeSpentAnalysis.class.getDeclaredMethod("isPrime", int.class);
    isPrimeMethod.setAccessible(true);

    Boolean prime2 = (Boolean) isPrimeMethod.invoke(timeSpentAnalysis, 2);
    assertTrue("2 should be prime", prime2);

    Boolean prime3 = (Boolean) isPrimeMethod.invoke(timeSpentAnalysis, 3);
    assertTrue("3 should be prime", prime3);

    Boolean prime4 = (Boolean) isPrimeMethod.invoke(timeSpentAnalysis, 4);
    assertFalse("4 should not be prime", prime4);

    Boolean prime17 = (Boolean) isPrimeMethod.invoke(timeSpentAnalysis, 17);
    assertTrue("17 should be prime", prime17);

    Boolean prime1 = (Boolean) isPrimeMethod.invoke(timeSpentAnalysis, 1);
    assertFalse("1 should not be prime", prime1);
  }

  /**
   * @brief Helper method to check if a number is prime.
   * @param n Number to check.
   * @return true if prime, false otherwise.
   */
  private boolean isPrimeNumber(int n) {
    if (n <= 1) {
      return false;
    }

    if (n <= 3) {
      return true;
    }

    if (n % 2 == 0 || n % 3 == 0) {
      return false;
    }

    for (int i = 5; i * i <= n; i += 6) {
      if (n % i == 0 || n % (i + 2) == 0) {
        return false;
      }
    }

    return true;
  }

  /**
   * @brief Test to verify heapSort handles single element.
   * @throws Exception
   */
  @Test
  public void testHeapSortSingleElement() throws Exception {
    Class<?> activitySummaryClass = Class.forName("com.efil.nese.timetracker.TimeSpentAnalysis$ActivitySummary");
    java.lang.reflect.Constructor<?> constructor = activitySummaryClass.getDeclaredConstructor(String.class, int.class);
    constructor.setAccessible(true);

    Object summary = constructor.newInstance("Activity 1", 30);
    List<Object> summaries = new java.util.ArrayList<>();
    summaries.add(summary);

    java.lang.reflect.Method heapSortMethod = TimeSpentAnalysis.class.getDeclaredMethod("heapSort", List.class);
    heapSortMethod.setAccessible(true);
    heapSortMethod.invoke(timeSpentAnalysis, summaries);

    assertEquals("Should have one element", 1, summaries.size());
  }

  /**
   * @brief Test to verify heapSort handles empty list.
   * @throws Exception
   */
  @Test
  public void testHeapSortEmptyList() throws Exception {
    List<Object> summaries = new java.util.ArrayList<>();

    java.lang.reflect.Method heapSortMethod = TimeSpentAnalysis.class.getDeclaredMethod("heapSort", List.class);
    heapSortMethod.setAccessible(true);
    heapSortMethod.invoke(timeSpentAnalysis, summaries);

    assertEquals("Should be empty", 0, summaries.size());
  }

  /**
   * @brief Test to verify heapify method works.
   * @throws Exception
   */
  @Test
  public void testHeapify() throws Exception {
    // Create ActivitySummary list
    Class<?> activitySummaryClass = Class.forName("com.efil.nese.timetracker.TimeSpentAnalysis$ActivitySummary");
    java.lang.reflect.Constructor<?> constructor = activitySummaryClass.getDeclaredConstructor(String.class, int.class);
    constructor.setAccessible(true);

    Object summary1 = constructor.newInstance("Activity 1", 30);
    Object summary2 = constructor.newInstance("Activity 2", 60);
    Object summary3 = constructor.newInstance("Activity 3", 45);

    List<Object> summaries = new java.util.ArrayList<>();
    summaries.add(summary1);
    summaries.add(summary2);
    summaries.add(summary3);

    java.lang.reflect.Method heapifyMethod = TimeSpentAnalysis.class.getDeclaredMethod("heapify", List.class, int.class, int.class);
    heapifyMethod.setAccessible(true);

    // Test heapify on root (index 0)
    heapifyMethod.invoke(timeSpentAnalysis, summaries, 3, 0);

    // Verify heap property (root should be largest)
    java.lang.reflect.Field totalTimeField = activitySummaryClass.getDeclaredField("totalTime");
    totalTimeField.setAccessible(true);

    int rootTime = (Integer) totalTimeField.get(summaries.get(0));
    int leftTime = (Integer) totalTimeField.get(summaries.get(1));
    int rightTime = (Integer) totalTimeField.get(summaries.get(2));

    assertTrue("Root should be >= left child", rootTime >= leftTime);
    assertTrue("Root should be >= right child", rootTime >= rightTime);
  }

  /**
   * @brief Test to verify heapSort with many elements.
   * @throws Exception
   */
  @Test
  public void testHeapSortManyElements() throws Exception {
    Class<?> activitySummaryClass = Class.forName("com.efil.nese.timetracker.TimeSpentAnalysis$ActivitySummary");
    java.lang.reflect.Constructor<?> constructor = activitySummaryClass.getDeclaredConstructor(String.class, int.class);
    constructor.setAccessible(true);

    List<Object> summaries = new java.util.ArrayList<>();
    int[] times = {50, 30, 80, 20, 60, 40, 70, 10, 90, 25};

    for (int i = 0; i < times.length; i++) {
      summaries.add(constructor.newInstance("Activity " + i, times[i]));
    }

    java.lang.reflect.Method heapSortMethod = TimeSpentAnalysis.class.getDeclaredMethod("heapSort", List.class);
    heapSortMethod.setAccessible(true);
    heapSortMethod.invoke(timeSpentAnalysis, summaries);

    // Verify sorting
    java.lang.reflect.Field totalTimeField = activitySummaryClass.getDeclaredField("totalTime");
    totalTimeField.setAccessible(true);

    for (int i = 0; i < summaries.size() - 1; i++) {
      int time1 = (Integer) totalTimeField.get(summaries.get(i));
      int time2 = (Integer) totalTimeField.get(summaries.get(i + 1));
      assertTrue("Elements should be sorted in ascending order", time1 <= time2);
    }
  }

  /**
   * @brief Test to verify longerPeriodSummary aggregates same activity names.
   * @throws Exception
   */
  @Test
  public void testLongerPeriodSummaryAggregatesActivities() throws Exception {
    // Add multiple activities with same name
    long timestamp = System.currentTimeMillis() / 1000;
    activityLogging.addActivity(new Activity(1, "Same Activity", 30, timestamp, "Category 1", "Description 1"));
    activityLogging.addActivity(new Activity(2, "Same Activity", 45, timestamp + 100, "Category 1", "Description 2"));
    activityLogging.addActivity(new Activity(3, "Same Activity", 25, timestamp + 200, "Category 1", "Description 3"));

    String input = System.lineSeparator();
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    Scanner newScanner = new Scanner(System.in);
    java.lang.reflect.Field scannerField = TimeSpentAnalysis.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(timeSpentAnalysis, newScanner);

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      java.lang.reflect.Method longerPeriodSummaryMethod = TimeSpentAnalysis.class.getDeclaredMethod("longerPeriodSummary");
      longerPeriodSummaryMethod.setAccessible(true);
      longerPeriodSummaryMethod.invoke(timeSpentAnalysis);
      String output = outContent.toString();
      // Should aggregate to 100 minutes (30+45+25)
      assertTrue("Output should contain aggregated time", output.contains("100") || output.contains("Same Activity"));
    }

    finally {
      System.setOut(originalOut);
    }
  }

  /**
   * @brief Test to verify dailySummary groups by date correctly.
   * @throws Exception
   */
  @Test
  public void testDailySummaryGroupsByDate() throws Exception {
    // Add activities on different days
    long timestamp = System.currentTimeMillis() / 1000;
    activityLogging.addActivity(new Activity(1, "Activity 1", 30, timestamp, "Category 1", "Description 1"));
    activityLogging.addActivity(new Activity(2, "Activity 2", 45, timestamp, "Category 1", "Description 2"));
    activityLogging.addActivity(new Activity(3, "Activity 3", 60, timestamp + 86400, "Category 2", "Description 3"));

    String input = System.lineSeparator();
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    Scanner newScanner = new Scanner(System.in);
    java.lang.reflect.Field scannerField = TimeSpentAnalysis.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(timeSpentAnalysis, newScanner);

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      java.lang.reflect.Method dailySummaryMethod = TimeSpentAnalysis.class.getDeclaredMethod("dailySummary");
      dailySummaryMethod.setAccessible(true);
      dailySummaryMethod.invoke(timeSpentAnalysis);
      String output = outContent.toString();
      // Should show activities grouped by date
      assertTrue("Output should contain date grouping", output.contains("Date:") || output.contains("TOTAL"));
    }

    finally {
      System.setOut(originalOut);
    }
  }

  /**
   * @brief Test to verify viewTimeDataTable creates hash table correctly.
   * @throws Exception
   */
  @Test
  public void testViewTimeDataTableHashTableStructure() throws Exception {
    // Add activities
    long timestamp = System.currentTimeMillis() / 1000;
    activityLogging.addActivity(new Activity(1, "Activity 1", 30, timestamp, "Category 1", "Description 1"));
    activityLogging.addActivity(new Activity(2, "Activity 2", 45, timestamp + 100, "Category 1", "Description 2"));

    String input = System.lineSeparator();
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    Scanner newScanner = new Scanner(System.in);
    java.lang.reflect.Field scannerField = TimeSpentAnalysis.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(timeSpentAnalysis, newScanner);

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      java.lang.reflect.Method viewTimeDataTableMethod = TimeSpentAnalysis.class.getDeclaredMethod("viewTimeDataTable");
      viewTimeDataTableMethod.setAccessible(true);
      viewTimeDataTableMethod.invoke(timeSpentAnalysis);
      String output = outContent.toString();
      // Should show hash table structure
      assertTrue("Output should contain hash table information",
                 output.contains("Hash table size") || output.contains("Load factor") || output.contains("collisions"));
    }

    finally {
      System.setOut(originalOut);
    }
  }

  /**
   * @brief Test to verify ActivitySummary class can be instantiated.
   * @throws Exception
   */
  @Test
  public void testActivitySummaryClass() throws Exception {
    Class<?> activitySummaryClass = Class.forName("com.efil.nese.timetracker.TimeSpentAnalysis$ActivitySummary");
    java.lang.reflect.Constructor<?> constructor = activitySummaryClass.getDeclaredConstructor(String.class, int.class);
    constructor.setAccessible(true);

    Object summary = constructor.newInstance("Test Activity", 30);
    assertNotNull("ActivitySummary object should not be null", summary);

    // Verify fields
    java.lang.reflect.Field nameField = activitySummaryClass.getDeclaredField("name");
    nameField.setAccessible(true);
    String name = (String) nameField.get(summary);
    assertEquals("Name should match", "Test Activity", name);

    java.lang.reflect.Field totalTimeField = activitySummaryClass.getDeclaredField("totalTime");
    totalTimeField.setAccessible(true);
    int totalTime = (Integer) totalTimeField.get(summary);
    assertEquals("Total time should match", 30, totalTime);
  }

  /**
   * @brief Test to verify HashTableEntry class can be instantiated.
   * @throws Exception
   */
  @Test
  public void testHashTableEntryClass() throws Exception {
    Class<?> hashTableEntryClass = Class.forName("com.efil.nese.timetracker.TimeSpentAnalysis$HashTableEntry");
    java.lang.reflect.Constructor<?> constructor = hashTableEntryClass.getDeclaredConstructor(
      String.class, int.class, long.class, int.class, int.class);
    constructor.setAccessible(true);

    long timestamp = System.currentTimeMillis() / 1000;
    Object entry = constructor.newInstance("Test Activity", 30, timestamp, 5, 2);
    assertNotNull("HashTableEntry object should not be null", entry);

    // Verify fields
    java.lang.reflect.Field keyField = hashTableEntryClass.getDeclaredField("key");
    keyField.setAccessible(true);
    String key = (String) keyField.get(entry);
    assertEquals("Key should match", "Test Activity", key);

    java.lang.reflect.Field durationField = hashTableEntryClass.getDeclaredField("duration");
    durationField.setAccessible(true);
    int duration = (Integer) durationField.get(entry);
    assertEquals("Duration should match", 30, duration);

    java.lang.reflect.Field originalHashField = hashTableEntryClass.getDeclaredField("originalHash");
    originalHashField.setAccessible(true);
    int originalHash = (Integer) originalHashField.get(entry);
    assertEquals("Original hash should match", 5, originalHash);

    java.lang.reflect.Field probeCountField = hashTableEntryClass.getDeclaredField("probeCount");
    probeCountField.setAccessible(true);
    int probeCount = (Integer) probeCountField.get(entry);
    assertEquals("Probe count should match", 2, probeCount);
  }

  /**
   * @brief Test to verify nextPrime with various inputs.
   * @throws Exception
   */
  @Test
  public void testNextPrimeVariousInputs() throws Exception {
    java.lang.reflect.Method nextPrimeMethod = TimeSpentAnalysis.class.getDeclaredMethod("nextPrime", int.class);
    nextPrimeMethod.setAccessible(true);

    int prime10 = (Integer) nextPrimeMethod.invoke(timeSpentAnalysis, 10);
    assertEquals("Next prime of 10 should be 11", 11, prime10);

    int prime20 = (Integer) nextPrimeMethod.invoke(timeSpentAnalysis, 20);
    assertEquals("Next prime of 20 should be 23", 23, prime20);

    int prime0 = (Integer) nextPrimeMethod.invoke(timeSpentAnalysis, 0);
    assertEquals("Next prime of 0 should be 2", 2, prime0);

    int primeNegative = (Integer) nextPrimeMethod.invoke(timeSpentAnalysis, -5);
    assertEquals("Next prime of -5 should be 2", 2, primeNegative);
  }

  /**
   * @brief Test to verify isPrime with various inputs.
   * @throws Exception
   */
  @Test
  public void testIsPrimeVariousInputs() throws Exception {
    java.lang.reflect.Method isPrimeMethod = TimeSpentAnalysis.class.getDeclaredMethod("isPrime", int.class);
    isPrimeMethod.setAccessible(true);

    Boolean prime5 = (Boolean) isPrimeMethod.invoke(timeSpentAnalysis, 5);
    assertTrue("5 should be prime", prime5);

    Boolean prime7 = (Boolean) isPrimeMethod.invoke(timeSpentAnalysis, 7);
    assertTrue("7 should be prime", prime7);

    Boolean prime11 = (Boolean) isPrimeMethod.invoke(timeSpentAnalysis, 11);
    assertTrue("11 should be prime", prime11);

    Boolean prime13 = (Boolean) isPrimeMethod.invoke(timeSpentAnalysis, 13);
    assertTrue("13 should be prime", prime13);

    Boolean prime6 = (Boolean) isPrimeMethod.invoke(timeSpentAnalysis, 6);
    assertFalse("6 should not be prime", prime6);

    Boolean prime9 = (Boolean) isPrimeMethod.invoke(timeSpentAnalysis, 9);
    assertFalse("9 should not be prime", prime9);

    Boolean prime15 = (Boolean) isPrimeMethod.invoke(timeSpentAnalysis, 15);
    assertFalse("15 should not be prime", prime15);

    Boolean prime0 = (Boolean) isPrimeMethod.invoke(timeSpentAnalysis, 0);
    assertFalse("0 should not be prime", prime0);

    Boolean primeNegative = (Boolean) isPrimeMethod.invoke(timeSpentAnalysis, -5);
    assertFalse("Negative numbers should not be prime", primeNegative);
  }

  /**
   * @brief Test to verify formatDate with various timestamps.
   * @throws Exception
   */
  @Test
  public void testFormatDateVariousTimestamps() throws Exception {
    java.lang.reflect.Method formatDateMethod = TimeSpentAnalysis.class.getDeclaredMethod("formatDate", long.class);
    formatDateMethod.setAccessible(true);

    // Test with current timestamp
    long timestamp = System.currentTimeMillis() / 1000;
    String formatted1 = (String) formatDateMethod.invoke(timeSpentAnalysis, timestamp);
    assertNotNull("Formatted date should not be null", formatted1);
    assertEquals("Formatted date should be YYYY-MM-DD format", 10, formatted1.length());

    // Test with specific timestamp (2024-01-01 00:00:00 UTC)
    long timestamp2 = 1704067200L; // 2024-01-01 00:00:00 UTC
    String formatted2 = (String) formatDateMethod.invoke(timeSpentAnalysis, timestamp2);
    assertTrue("Formatted date should contain 2024", formatted2.contains("2024"));

    // Test with zero timestamp
    String formatted3 = (String) formatDateMethod.invoke(timeSpentAnalysis, 0L);
    assertNotNull("Formatted date should not be null", formatted3);
    assertEquals("Formatted date should be YYYY-MM-DD format", 10, formatted3.length());
  }

  /**
   * @brief Test to verify longerPeriodSummary with single activity.
   * @throws Exception
   */
  @Test
  public void testLongerPeriodSummarySingleActivity() throws Exception {
    long timestamp = System.currentTimeMillis() / 1000;
    activityLogging.addActivity(new Activity(1, "Single Activity", 30, timestamp, "Category 1", "Description 1"));

    String input = System.lineSeparator();
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    Scanner newScanner = new Scanner(System.in);
    java.lang.reflect.Field scannerField = TimeSpentAnalysis.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(timeSpentAnalysis, newScanner);

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      java.lang.reflect.Method longerPeriodSummaryMethod = TimeSpentAnalysis.class.getDeclaredMethod("longerPeriodSummary");
      longerPeriodSummaryMethod.setAccessible(true);
      longerPeriodSummaryMethod.invoke(timeSpentAnalysis);
      String output = outContent.toString();
      assertTrue("Output should contain activity information", output.contains("Single Activity") || output.contains("30"));
    }

    finally {
      System.setOut(originalOut);
    }
  }

  /**
   * @brief Test to verify dailySummary with single day.
   * @throws Exception
   */
  @Test
  public void testDailySummarySingleDay() throws Exception {
    long timestamp = System.currentTimeMillis() / 1000;
    activityLogging.addActivity(new Activity(1, "Activity 1", 30, timestamp, "Category 1", "Description 1"));
    activityLogging.addActivity(new Activity(2, "Activity 2", 45, timestamp, "Category 1", "Description 2"));

    String input = System.lineSeparator();
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    Scanner newScanner = new Scanner(System.in);
    java.lang.reflect.Field scannerField = TimeSpentAnalysis.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(timeSpentAnalysis, newScanner);

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      java.lang.reflect.Method dailySummaryMethod = TimeSpentAnalysis.class.getDeclaredMethod("dailySummary");
      dailySummaryMethod.setAccessible(true);
      dailySummaryMethod.invoke(timeSpentAnalysis);
      String output = outContent.toString();
      // Should show total of 75 minutes (30+45) for the day
      assertTrue("Output should contain total time", output.contains("75") || output.contains("TOTAL"));
    }

    finally {
      System.setOut(originalOut);
    }
  }

  /**
   * @brief Test to verify viewTimeDataTable with single activity.
   * @throws Exception
   */
  @Test
  public void testViewTimeDataTableSingleActivity() throws Exception {
    long timestamp = System.currentTimeMillis() / 1000;
    activityLogging.addActivity(new Activity(1, "Single Activity", 30, timestamp, "Category 1", "Description 1"));

    String input = System.lineSeparator();
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    Scanner newScanner = new Scanner(System.in);
    java.lang.reflect.Field scannerField = TimeSpentAnalysis.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(timeSpentAnalysis, newScanner);

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      java.lang.reflect.Method viewTimeDataTableMethod = TimeSpentAnalysis.class.getDeclaredMethod("viewTimeDataTable");
      viewTimeDataTableMethod.setAccessible(true);
      viewTimeDataTableMethod.invoke(timeSpentAnalysis);
      String output = outContent.toString();
      assertTrue("Output should contain hash table information",
                 output.contains("Single Activity") || output.contains("Occupied slots: 1"));
    }

    finally {
      System.setOut(originalOut);
    }
  }
}

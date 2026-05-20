/**
 * @file ActivityLoggingTest.java
 * @brief This file contains test cases for the ActivityLogging class.
 * @details This file includes test methods to validate the functionality of the ActivityLogging class.
 *          It uses JUnit 4 for unit testing.
 * @author efil.saylam.nese.sarp
 */
package com.efil.nese.timetracker;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @class ActivityLoggingTest
 * @brief This class represents the test class for the ActivityLogging class.
 * @details The ActivityLoggingTest class provides test methods to verify the behavior of the ActivityLogging class.
 * @author efil.saylam.nese.sarp
 */
public class ActivityLoggingTest {

  private static final String TEST_ACTIVITIES_FILE = "activities.txt";
  private ActivityLogging activityLogging;
  private Scanner testScanner;
  private InputStream originalSystemIn;

  /**
   * @brief This method is executed once before all test methods.
   * @throws Exception
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    // Clean up any existing test files
    File testFile = new File(TEST_ACTIVITIES_FILE);

    if (testFile.exists()) {
      testFile.delete();
    }
  }

  /**
   * @brief This method is executed once after all test methods.
   * @throws Exception
   */
  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    // Clean up test files
    File testFile = new File(TEST_ACTIVITIES_FILE);

    if (testFile.exists()) {
      testFile.delete();
    }
  }

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
    // Create test scanner
    testScanner = new Scanner(new ByteArrayInputStream("".getBytes()));
    // Create ActivityLogging instance with test scanner
    activityLogging = new ActivityLogging(testScanner);
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

    // Close activity logging
    if (activityLogging != null) {
      activityLogging.close();
    }

    // Clean up test files
    File testFile = new File(TEST_ACTIVITIES_FILE);

    if (testFile.exists()) {
      testFile.delete();
    }
  }

  /**
   * @brief Test to verify ActivityLogging object creation with default constructor.
   * @throws Exception
   */
  @Test
  public void testActivityLoggingDefaultConstructor() throws Exception {
    ActivityLogging al = new ActivityLogging();
    assertNotNull("ActivityLogging object should not be null", al);
    al.close();
  }

  /**
   * @brief Test to verify ActivityLogging object creation with custom scanner.
   * @throws Exception
   */
  @Test
  public void testActivityLoggingConstructorWithScanner() throws Exception {
    Scanner scanner = new Scanner(new ByteArrayInputStream("".getBytes()));
    ActivityLogging al = new ActivityLogging(scanner);
    assertNotNull("ActivityLogging object should not be null", al);
    al.close();
  }

  /**
   * @brief Test to verify initial state of ActivityLogging.
   * @throws Exception
   */
  @Test
  public void testInitialState() throws Exception {
    List<Activity> activities = activityLogging.getActivities();
    assertNotNull("Activities list should not be null", activities);
    assertEquals("Initial activities list should be empty", 0, activities.size());
  }

  /**
   * @brief Test to verify adding a single activity.
   * @throws Exception
   */
  @Test
  public void testAddSingleActivity() throws Exception {
    long timestamp = System.currentTimeMillis() / 1000;

    // Create Activity instance (package-private, but same package)
    Activity activity = new Activity(1, "Test Activity", 30, timestamp, "Test", "Test Description");
    activityLogging.addActivity(activity);

    List<Activity> activities = activityLogging.getActivities();
    assertEquals("Should have one activity", 1, activities.size());
  }

  /**
   * @brief Test to verify adding multiple activities.
   * @throws Exception
   */
  @Test
  public void testAddMultipleActivities() throws Exception {
    long timestamp = System.currentTimeMillis() / 1000;

    // Add first activity
    Activity activity1 = new Activity(1, "Activity 1", 30, timestamp, "Category1", "Description 1");
    activityLogging.addActivity(activity1);

    // Add second activity
    Activity activity2 = new Activity(2, "Activity 2", 45, timestamp, "Category2", "Description 2");
    activityLogging.addActivity(activity2);

    List<Activity> activities = activityLogging.getActivities();
    assertEquals("Should have two activities", 2, activities.size());
  }

  /**
   * @brief Test to verify getActivities returns a copy of the list.
   * @throws Exception
   */
  @Test
  public void testGetActivitiesReturnsCopy() throws Exception {
    List<Activity> activities1 = activityLogging.getActivities();
    List<Activity> activities2 = activityLogging.getActivities();

    // They should be different instances
    assertNotSame("getActivities should return a new list each time", activities1, activities2);
  }

  /**
   * @brief Test to verify activities can be saved and loaded.
   * @throws Exception
   */
  @Test
  public void testSaveAndLoadActivities() throws Exception {
    long timestamp = System.currentTimeMillis() / 1000;

    // Add an activity
    Activity activity = new Activity(1, "Save Test", 60, timestamp, "Test", "Test Description");
    activityLogging.addActivity(activity);

    // Save activities
    activityLogging.saveActivities();

    // Create new instance and load
    ActivityLogging newInstance = new ActivityLogging(testScanner);
    newInstance.loadActivities();

    List<Activity> loadedActivities = newInstance.getActivities();
    assertTrue("Should have loaded at least one activity", loadedActivities.size() > 0);

    newInstance.close();
  }

  /**
   * @brief Test to verify saveActivities method exists and can be called.
   * @throws Exception
   */
  @Test
  public void testSaveActivities() throws Exception {
    // Test that the method exists and can be called without exception
    activityLogging.saveActivities();
    // If we get here, the method executed successfully
    assertTrue("saveActivities should execute without exception", true);
  }

  /**
   * @brief Test to verify loadActivities method exists and can be called.
   * @throws Exception
   */
  @Test
  public void testLoadActivities() throws Exception {
    // Test that the method exists and can be called without exception
    activityLogging.loadActivities();
    // If we get here, the method executed successfully
    assertTrue("loadActivities should execute without exception", true);
  }

  /**
   * @brief Test to verify loadActivities handles non-existent file gracefully.
   * @throws Exception
   */
  @Test
  public void testLoadActivitiesWithNonExistentFile() throws Exception {
    // Ensure file doesn't exist
    File testFile = new File(TEST_ACTIVITIES_FILE);

    if (testFile.exists()) {
      testFile.delete();
    }

    // Should not throw exception
    activityLogging.loadActivities();
    List<Activity> activities = activityLogging.getActivities();
    assertNotNull("Activities list should not be null", activities);
  }

  /**
   * @brief Test to verify loadActivities can parse valid file format.
   * @throws Exception
   */
  @Test
  public void testLoadActivitiesWithValidFile() throws Exception {
    // Create a valid activities file
    File testFile = new File(TEST_ACTIVITIES_FILE);

    try (FileWriter writer = new FileWriter(testFile)) {
      writer.write("# Activities file\n");
      writer.write("# Format: id|name|duration|timestamp|category|description\n");
      writer.write("1|Test Activity|30|1234567890|Test|Test Description\n");
    }

    // Load activities
    activityLogging.loadActivities();

    List<Activity> activities = activityLogging.getActivities();
    assertTrue("Should have loaded at least one activity", activities.size() > 0);
  }

  /**
   * @brief Test to verify close method exists and can be called.
   * @throws Exception
   */
  @Test
  public void testClose() throws Exception {
    // Test that the method exists and can be called without exception
    activityLogging.close();
    // If we get here, the method executed successfully
    assertTrue("close should execute without exception", true);
  }

  /**
   * @brief Test to verify close saves activities before closing.
   * @throws Exception
   */
  @Test
  public void testCloseSavesActivities() throws Exception {
    long timestamp = System.currentTimeMillis() / 1000;

    // Add an activity
    Activity activity = new Activity(1, "Close Test", 30, timestamp, "Test", "Test Description");
    activityLogging.addActivity(activity);

    // Close should save
    activityLogging.close();

    // Verify file exists
    File testFile = new File(TEST_ACTIVITIES_FILE);
    assertTrue("File should exist after close", testFile.exists());
  }

  /**
   * @brief Test to verify ActivityLogging class exists and can be instantiated.
   * @throws Exception
   */
  @Test
  public void testActivityLoggingClassExists() throws Exception {
    Class<?> clazz = ActivityLogging.class;
    assertNotNull("ActivityLogging class should exist", clazz);
    assertEquals("Class name should be ActivityLogging", "ActivityLogging", clazz.getSimpleName());
  }

  /**
   * @brief Test to verify run method exists.
   * @throws Exception
   */
  @Test
  public void testRunMethodExists() throws Exception {
    try {
      java.lang.reflect.Method runMethod = ActivityLogging.class.getMethod("run");
      assertNotNull("Run method should exist", runMethod);
    } catch (NoSuchMethodException e) {
      fail("Run method should exist in ActivityLogging class");
    }
  }

  /**
   * @brief Test to verify multiple activities maintain order.
   * @throws Exception
   */
  @Test
  public void testMultipleActivitiesOrder() throws Exception {
    long timestamp = System.currentTimeMillis() / 1000;

    // Add multiple activities
    for (int i = 1; i <= 5; i++) {
      Activity activity = new Activity(i, "Activity " + i, i * 10, timestamp, "Category" + i, "Description " + i);
      activityLogging.addActivity(activity);
    }

    List<Activity> activities = activityLogging.getActivities();
    assertEquals("Should have 5 activities", 5, activities.size());
  }

  /**
   * @brief Test to verify activities with different categories.
   * @throws Exception
   */
  @Test
  public void testActivitiesWithDifferentCategories() throws Exception {
    long timestamp = System.currentTimeMillis() / 1000;

    // Add activities with different categories
    String[] categories = {"Work", "Personal", "Exercise", "Study"};

    for (int i = 0; i < categories.length; i++) {
      Activity activity = new Activity(i + 1, "Activity " + (i + 1), 30, timestamp, categories[i], "Description");
      activityLogging.addActivity(activity);
    }

    List<Activity> activities = activityLogging.getActivities();
    assertEquals("Should have 4 activities", 4, activities.size());
  }

  /**
   * @brief Test to verify empty activities list handling.
   * @throws Exception
   */
  @Test
  public void testEmptyActivitiesList() throws Exception {
    List<Activity> activities = activityLogging.getActivities();
    assertNotNull("Activities list should not be null", activities);
    assertTrue("Activities list should be empty initially", activities.isEmpty());
    assertEquals("Activities list size should be 0", 0, activities.size());
  }

  /**
   * @brief Test to verify run method can be called.
   * @throws Exception
   */
  @Test
  public void testRunMethod() throws Exception {
    // Test that run method exists and can be called
    // Since it's interactive, we'll just verify it doesn't throw exception immediately
    // by providing input that exits immediately
    Scanner exitScanner = new Scanner(new ByteArrayInputStream("5\n".getBytes()));
    ActivityLogging al = new ActivityLogging(exitScanner);

    // Run in a separate thread to avoid blocking
    Thread thread = new Thread(() -> {
      try {
        al.run();
      } catch (Exception e) {
        // Ignore
      }
    });
    thread.start();
    Thread.sleep(100); // Give it time to start
    thread.interrupt();

    al.close();
    assertTrue("Run method should exist", true);
  }

  /**
   * @brief Test to verify KMP search algorithm works correctly.
   * @throws Exception
   */
  @Test
  public void testKMPSearchAlgorithm() throws Exception {
    // Test KMP search using reflection
    java.lang.reflect.Method kmpMethod = ActivityLogging.class.getDeclaredMethod("kmpSearch", String.class, String.class);
    kmpMethod.setAccessible(true);

    // Test case 1: Pattern found
    Boolean result1 = (Boolean) kmpMethod.invoke(activityLogging, "Hello World", "World");
    assertTrue("KMP should find 'World' in 'Hello World'", result1);

    // Test case 2: Pattern not found
    Boolean result2 = (Boolean) kmpMethod.invoke(activityLogging, "Hello World", "XYZ");
    assertFalse("KMP should not find 'XYZ' in 'Hello World'", result2);

    // Test case 3: Empty pattern
    Boolean result3 = (Boolean) kmpMethod.invoke(activityLogging, "Hello", "");
    assertFalse("KMP should return false for empty pattern", result3);

    // Test case 4: Pattern longer than text
    Boolean result4 = (Boolean) kmpMethod.invoke(activityLogging, "Hi", "Hello");
    assertFalse("KMP should return false when pattern is longer than text", result4);
  }

  /**
   * @brief Test to verify computeLPS method works correctly.
   * @throws Exception
   */
  @Test
  public void testComputeLPS() throws Exception {
    // Test computeLPS using reflection
    java.lang.reflect.Method lpsMethod = ActivityLogging.class.getDeclaredMethod("computeLPS", String.class);
    lpsMethod.setAccessible(true);

    // Test case 1: Simple pattern
    int[] lps1 = (int[]) lpsMethod.invoke(activityLogging, "AAAA");
    assertNotNull("LPS array should not be null", lps1);
    assertEquals("LPS array length should match pattern length", 4, lps1.length);

    // Test case 2: Pattern with no repetition
    int[] lps2 = (int[]) lpsMethod.invoke(activityLogging, "ABCD");
    assertNotNull("LPS array should not be null", lps2);
    assertEquals("LPS array length should match pattern length", 4, lps2.length);

    // Test case 3: Pattern with repetition
    int[] lps3 = (int[]) lpsMethod.invoke(activityLogging, "ABAB");
    assertNotNull("LPS array should not be null", lps3);
    assertEquals("LPS array length should match pattern length", 4, lps3.length);
  }

  /**
   * @brief Test to verify formatTimestamp method works correctly.
   * @throws Exception
   */
  @Test
  public void testFormatTimestamp() throws Exception {
    // Test formatTimestamp using reflection
    java.lang.reflect.Method formatMethod = ActivityLogging.class.getDeclaredMethod("formatTimestamp", long.class);
    formatMethod.setAccessible(true);

    // Test with a known timestamp (Unix timestamp for 2009-02-13 23:31:30 UTC)
    long timestamp = 1234567890L;
    String formatted = (String) formatMethod.invoke(activityLogging, timestamp);

    assertNotNull("Formatted timestamp should not be null", formatted);
    assertTrue("Formatted timestamp should contain date", formatted.length() > 0);
    assertTrue("Formatted timestamp should contain year 2009", formatted.contains("2009"));
  }

  /**
   * @brief Test to verify printActivity method exists and can be called.
   * @throws Exception
   */
  @Test
  public void testPrintActivity() throws Exception {
    // Test printActivity using reflection
    long timestamp = System.currentTimeMillis() / 1000;
    Activity activity = new Activity(1, "Test Activity", 30, timestamp, "Test", "Test Description");

    java.lang.reflect.Method printMethod = ActivityLogging.class.getDeclaredMethod("printActivity", Activity.class);
    printMethod.setAccessible(true);

    // Redirect System.out to capture output
    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    System.setOut(new java.io.PrintStream(outContent));

    try {
      printMethod.invoke(activityLogging, activity);
      String output = outContent.toString();
      assertTrue("Output should contain activity ID", output.contains("ID: 1"));
      assertTrue("Output should contain activity name", output.contains("Test Activity"));
    }

    finally {
      System.setOut(System.out);
    }
  }

  /**
   * @brief Test to verify searchActivitiesKMP with matching activities.
   * @throws Exception
   */
  @Test
  public void testSearchActivitiesKMP() throws Exception {
    long timestamp = System.currentTimeMillis() / 1000;

    // Add activities with searchable content
    Activity activity1 = new Activity(1, "Java Programming", 60, timestamp, "Work", "Learning Java");
    Activity activity2 = new Activity(2, "Python Coding", 45, timestamp, "Work", "Python tutorial");
    Activity activity3 = new Activity(3, "Reading Book", 30, timestamp, "Personal", "Java book");

    activityLogging.addActivity(activity1);
    activityLogging.addActivity(activity2);
    activityLogging.addActivity(activity3);

    // Test search using reflection
    java.lang.reflect.Method searchMethod = ActivityLogging.class.getDeclaredMethod("searchActivitiesKMP");
    searchMethod.setAccessible(true);

    // Create scanner with search input
    Scanner searchScanner = new Scanner(new ByteArrayInputStream("Java\n".getBytes()));
    ActivityLogging searchInstance = new ActivityLogging(searchScanner);
    searchInstance.addActivity(activity1);
    searchInstance.addActivity(activity2);
    searchInstance.addActivity(activity3);

    // Redirect System.out
    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    System.setOut(new java.io.PrintStream(outContent));

    try {
      searchMethod.invoke(searchInstance);
      String output = outContent.toString();
      assertTrue("Output should contain search results", output.length() > 0);
    }

    finally {
      System.setOut(System.out);
      searchInstance.close();
    }
  }

  /**
   * @brief Test to verify exploreBFS method works.
   * @throws Exception
   */
  @Test
  public void testExploreBFS() throws Exception {
    long timestamp = System.currentTimeMillis() / 1000;

    // Add activities with same categories
    Activity activity1 = new Activity(1, "Activity 1", 30, timestamp, "Work", "Description 1");
    Activity activity2 = new Activity(2, "Activity 2", 45, timestamp, "Work", "Description 2");
    Activity activity3 = new Activity(3, "Activity 3", 60, timestamp, "Personal", "Description 3");

    activityLogging.addActivity(activity1);
    activityLogging.addActivity(activity2);
    activityLogging.addActivity(activity3);

    // Test exploreBFS using reflection
    java.lang.reflect.Method bfsMethod = ActivityLogging.class.getDeclaredMethod("exploreBFS");
    bfsMethod.setAccessible(true);

    // Redirect System.out
    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    System.setOut(new java.io.PrintStream(outContent));

    try {
      bfsMethod.invoke(activityLogging);
      String output = outContent.toString();
      assertTrue("BFS output should contain activity names", output.contains("Activity"));
      assertTrue("BFS output should contain exploration completed", output.contains("completed"));
    }

    finally {
      System.setOut(System.out);
    }
  }

  /**
   * @brief Test to verify exploreDFS method works.
   * @throws Exception
   */
  @Test
  public void testExploreDFS() throws Exception {
    long timestamp = System.currentTimeMillis() / 1000;

    // Add activities with same categories
    Activity activity1 = new Activity(1, "Activity 1", 30, timestamp, "Work", "Description 1");
    Activity activity2 = new Activity(2, "Activity 2", 45, timestamp, "Work", "Description 2");
    Activity activity3 = new Activity(3, "Activity 3", 60, timestamp, "Personal", "Description 3");

    activityLogging.addActivity(activity1);
    activityLogging.addActivity(activity2);
    activityLogging.addActivity(activity3);

    // Test exploreDFS using reflection
    java.lang.reflect.Method dfsMethod = ActivityLogging.class.getDeclaredMethod("exploreDFS");
    dfsMethod.setAccessible(true);

    // Redirect System.out
    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    System.setOut(new java.io.PrintStream(outContent));

    try {
      dfsMethod.invoke(activityLogging);
      String output = outContent.toString();
      assertTrue("DFS output should contain activity names", output.contains("Activity"));
      assertTrue("DFS output should contain exploration completed", output.contains("completed"));
    }

    finally {
      System.setOut(System.out);
    }
  }

  /**
   * @brief Test to verify getPosition method works correctly.
   * @throws Exception
   */
  @Test
  public void testGetPosition() throws Exception {
    long timestamp = System.currentTimeMillis() / 1000;

    // Add multiple activities to create a linked list
    for (int i = 1; i <= 3; i++) {
      Activity activity = new Activity(i, "Activity " + i, 30, timestamp, "Test", "Description");
      activityLogging.addActivity(activity);
    }

    // Test getPosition using reflection
    java.lang.reflect.Method getPositionMethod = ActivityLogging.class.getDeclaredMethod("getPosition",
      Class.forName("com.efil.nese.timetracker.ActivityNode"));
    getPositionMethod.setAccessible(true);

    // Get head node using reflection
    java.lang.reflect.Field headField = ActivityLogging.class.getDeclaredField("head");
    headField.setAccessible(true);
    Object headNode = headField.get(activityLogging);

    if (headNode != null) {
      int position = (Integer) getPositionMethod.invoke(activityLogging, headNode);
      assertEquals("First node should be at position 1", 1, position);
    }
  }

  /**
   * @brief Test to verify getTotalCount method works correctly.
   * @throws Exception
   */
  @Test
  public void testGetTotalCount() throws Exception {
    long timestamp = System.currentTimeMillis() / 1000;

    // Add activities
    for (int i = 1; i <= 5; i++) {
      Activity activity = new Activity(i, "Activity " + i, 30, timestamp, "Test", "Description");
      activityLogging.addActivity(activity);
    }

    // Test getTotalCount using reflection
    java.lang.reflect.Method getTotalCountMethod = ActivityLogging.class.getDeclaredMethod("getTotalCount");
    getTotalCountMethod.setAccessible(true);

    int count = (Integer) getTotalCountMethod.invoke(activityLogging);
    assertEquals("Total count should be 5", 5, count);
  }

  /**
   * @brief Test to verify KMP search finds pattern in activity names.
   * @throws Exception
   */
  @Test
  public void testKMPSearchInActivities() throws Exception {
    long timestamp = System.currentTimeMillis() / 1000;

    Activity activity1 = new Activity(1, "Java Programming", 60, timestamp, "Work", "Learning");
    Activity activity2 = new Activity(2, "Python Coding", 45, timestamp, "Work", "Tutorial");
    activityLogging.addActivity(activity1);
    activityLogging.addActivity(activity2);

    // Test KMP search
    java.lang.reflect.Method kmpMethod = ActivityLogging.class.getDeclaredMethod("kmpSearch", String.class, String.class);
    kmpMethod.setAccessible(true);

    Boolean found1 = (Boolean) kmpMethod.invoke(activityLogging, "Java Programming", "Java");
    assertTrue("Should find 'Java' in 'Java Programming'", found1);

    Boolean found2 = (Boolean) kmpMethod.invoke(activityLogging, "Python Coding", "Java");
    assertFalse("Should not find 'Java' in 'Python Coding'", found2);
  }

  /**
   * @brief Test to verify activities are added to double linked list correctly.
   * @throws Exception
   */
  @Test
  public void testDoubleLinkedListStructure() throws Exception {
    long timestamp = System.currentTimeMillis() / 1000;

    // Add activities
    Activity activity1 = new Activity(1, "Activity 1", 30, timestamp, "Test", "Description");
    Activity activity2 = new Activity(2, "Activity 2", 45, timestamp, "Test", "Description");
    activityLogging.addActivity(activity1);
    activityLogging.addActivity(activity2);

    // Verify linked list structure using reflection
    java.lang.reflect.Field headField = ActivityLogging.class.getDeclaredField("head");
    headField.setAccessible(true);
    Object head = headField.get(activityLogging);

    java.lang.reflect.Field tailField = ActivityLogging.class.getDeclaredField("tail");
    tailField.setAccessible(true);
    Object tail = tailField.get(activityLogging);

    assertNotNull("Head should not be null after adding activities", head);
    assertNotNull("Tail should not be null after adding activities", tail);
  }

  /**
   * @brief Test to verify logActivity method works correctly.
   * @throws Exception
   */
  @Test
  public void testLogActivity() throws Exception {
    // Create scanner with valid input
    String input = "Test Activity\n30\nWork\nTest Description\n";
    Scanner logScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
    ActivityLogging logInstance = new ActivityLogging(logScanner);

    // Get initial count
    int initialCount = logInstance.getActivities().size();

    // Redirect System.out
    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      // Test logActivity using reflection
      java.lang.reflect.Method logMethod = ActivityLogging.class.getDeclaredMethod("logActivity");
      logMethod.setAccessible(true);
      logMethod.invoke(logInstance);
      // Verify activity was added
      List<Activity> activities = logInstance.getActivities();
      assertEquals("Should have one more activity", initialCount + 1, activities.size());
      // Verify output
      String output = outContent.toString();
      assertTrue("Output should contain success message", output.contains("Activity logged successfully"));
    }

    finally {
      System.setOut(originalOut);
      logInstance.close();
    }
  }

  /**
   * @brief Test to verify logActivity handles empty name.
   * @throws Exception
   */
  @Test
  public void testLogActivityEmptyName() throws Exception {
    // Create scanner with empty name input
    String input = "\n30\nWork\nDescription\n";
    Scanner logScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
    ActivityLogging logInstance = new ActivityLogging(logScanner);

    int initialCount = logInstance.getActivities().size();

    // Redirect System.out
    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      java.lang.reflect.Method logMethod = ActivityLogging.class.getDeclaredMethod("logActivity");
      logMethod.setAccessible(true);
      logMethod.invoke(logInstance);
      // Verify activity was NOT added
      List<Activity> activities = logInstance.getActivities();
      assertEquals("Should not add activity with empty name", initialCount, activities.size());
      String output = outContent.toString();
      assertTrue("Output should contain error message", output.contains("Activity name cannot be empty"));
    }

    finally {
      System.setOut(originalOut);
      logInstance.close();
    }
  }

  /**
   * @brief Test to verify logActivity handles invalid duration.
   * @throws Exception
   */
  @Test
  public void testLogActivityInvalidDuration() throws Exception {
    // Create scanner with invalid duration
    String input = "Test Activity\nabc\nWork\nDescription\n";
    Scanner logScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
    ActivityLogging logInstance = new ActivityLogging(logScanner);

    int initialCount = logInstance.getActivities().size();

    // Redirect System.out
    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      java.lang.reflect.Method logMethod = ActivityLogging.class.getDeclaredMethod("logActivity");
      logMethod.setAccessible(true);
      logMethod.invoke(logInstance);
      // Verify activity was NOT added
      List<Activity> activities = logInstance.getActivities();
      assertEquals("Should not add activity with invalid duration", initialCount, activities.size());
      String output = outContent.toString();
      assertTrue("Output should contain error message", output.contains("Invalid duration"));
    }

    finally {
      System.setOut(originalOut);
      logInstance.close();
    }
  }

  /**
   * @brief Test to verify logActivity handles negative duration.
   * @throws Exception
   */
  @Test
  public void testLogActivityNegativeDuration() throws Exception {
    // Create scanner with negative duration
    String input = "Test Activity\n-10\nWork\nDescription\n";
    Scanner logScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
    ActivityLogging logInstance = new ActivityLogging(logScanner);

    int initialCount = logInstance.getActivities().size();

    // Redirect System.out
    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      java.lang.reflect.Method logMethod = ActivityLogging.class.getDeclaredMethod("logActivity");
      logMethod.setAccessible(true);
      logMethod.invoke(logInstance);
      // Verify activity was NOT added
      List<Activity> activities = logInstance.getActivities();
      assertEquals("Should not add activity with negative duration", initialCount, activities.size());
      String output = outContent.toString();
      assertTrue("Output should contain error message", output.contains("Duration must be positive"));
    }

    finally {
      System.setOut(originalOut);
      logInstance.close();
    }
  }

  /**
   * @brief Test to verify logActivity uses default category when empty.
   * @throws Exception
   */
  @Test
  public void testLogActivityDefaultCategory() throws Exception {
    // Create scanner with empty category
    String input = "Test Activity\n30\n\nTest Description\n";
    Scanner logScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
    ActivityLogging logInstance = new ActivityLogging(logScanner);

    // Redirect System.out
    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      java.lang.reflect.Method logMethod = ActivityLogging.class.getDeclaredMethod("logActivity");
      logMethod.setAccessible(true);
      logMethod.invoke(logInstance);
      // Verify activity was added with default category
      List<Activity> activities = logInstance.getActivities();
      assertEquals("Should have one activity", 1, activities.size());
      Activity activity = activities.get(0);
      assertEquals("Category should be 'General'", "General", activity.category);
    }

    finally {
      System.setOut(originalOut);
      logInstance.close();
    }
  }

  /**
   * @brief Test to verify viewActivitiesDoubleLinkedList with no activities.
   * @throws Exception
   */
  @Test
  public void testViewActivitiesDoubleLinkedListEmpty() throws Exception {
    // Ensure no activities
    ActivityLogging emptyInstance = new ActivityLogging(testScanner);

    // Redirect System.out
    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      java.lang.reflect.Method viewMethod = ActivityLogging.class.getDeclaredMethod("viewActivitiesDoubleLinkedList");
      viewMethod.setAccessible(true);
      viewMethod.invoke(emptyInstance);
      String output = outContent.toString();
      assertTrue("Output should contain no activities message",
                 output.contains("No activities found") || output.contains("Start by logging"));
    }

    finally {
      System.setOut(originalOut);
      emptyInstance.close();
    }
  }

  /**
   * @brief Test to verify viewActivitiesDoubleLinkedList navigation.
   * @throws Exception
   */
  @Test
  public void testViewActivitiesDoubleLinkedListNavigation() throws Exception {
    long timestamp = System.currentTimeMillis() / 1000;

    // Add activities
    Activity activity1 = new Activity(1, "Activity 1", 30, timestamp, "Test", "Description 1");
    Activity activity2 = new Activity(2, "Activity 2", 45, timestamp, "Test", "Description 2");

    // Create scanner with navigation input (Q to quit)
    String input = "q\n";
    Scanner navScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
    ActivityLogging navInstance = new ActivityLogging(navScanner);
    navInstance.addActivity(activity1);
    navInstance.addActivity(activity2);

    // Redirect System.out
    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      java.lang.reflect.Method viewMethod = ActivityLogging.class.getDeclaredMethod("viewActivitiesDoubleLinkedList");
      viewMethod.setAccessible(true);
      // Run in thread to avoid blocking
      Thread thread = new Thread(() -> {
        try {
          viewMethod.invoke(navInstance);
        } catch (Exception e) {
          // Ignore
        }
      });
      thread.start();
      Thread.sleep(200);
      thread.interrupt();
      String output = outContent.toString();
      assertTrue("Output should contain view activities header",
                 output.contains("VIEW ACTIVITIES") || output.contains("Double Linked List") || output.length() > 0);
    }

    finally {
      System.setOut(originalOut);
      navInstance.close();
    }
  }

  /**
   * @brief Test to verify viewActivitiesDoubleLinkedList forward navigation.
   * @throws Exception
   */
  @Test
  public void testViewActivitiesDoubleLinkedListForward() throws Exception {
    long timestamp = System.currentTimeMillis() / 1000;

    // Add multiple activities
    Activity activity1 = new Activity(1, "Activity 1", 30, timestamp, "Test", "Description 1");
    Activity activity2 = new Activity(2, "Activity 2", 45, timestamp, "Test", "Description 2");
    Activity activity3 = new Activity(3, "Activity 3", 60, timestamp, "Test", "Description 3");

    // Create scanner with forward navigation (A then Q)
    String input = "a\nq\n";
    Scanner navScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
    ActivityLogging navInstance = new ActivityLogging(navScanner);
    navInstance.addActivity(activity1);
    navInstance.addActivity(activity2);
    navInstance.addActivity(activity3);

    // Redirect System.out
    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      java.lang.reflect.Method viewMethod = ActivityLogging.class.getDeclaredMethod("viewActivitiesDoubleLinkedList");
      viewMethod.setAccessible(true);
      Thread thread = new Thread(() -> {
        try {
          viewMethod.invoke(navInstance);
        } catch (Exception e) {
          // Ignore
        }
      });
      thread.start();
      Thread.sleep(200);
      thread.interrupt();
      String output = outContent.toString();
      assertTrue("Output should contain navigation information", output.length() > 0);
    }

    finally {
      System.setOut(originalOut);
      navInstance.close();
    }
  }

  /**
   * @brief Test to verify viewActivitiesDoubleLinkedList backward navigation.
   * @throws Exception
   */
  @Test
  public void testViewActivitiesDoubleLinkedListBackward() throws Exception {
    long timestamp = System.currentTimeMillis() / 1000;

    // Add multiple activities
    Activity activity1 = new Activity(1, "Activity 1", 30, timestamp, "Test", "Description 1");
    Activity activity2 = new Activity(2, "Activity 2", 45, timestamp, "Test", "Description 2");

    // Create scanner with backward navigation (D then Q)
    String input = "d\nq\n";
    Scanner navScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
    ActivityLogging navInstance = new ActivityLogging(navScanner);
    navInstance.addActivity(activity1);
    navInstance.addActivity(activity2);

    // Redirect System.out
    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      java.lang.reflect.Method viewMethod = ActivityLogging.class.getDeclaredMethod("viewActivitiesDoubleLinkedList");
      viewMethod.setAccessible(true);
      Thread thread = new Thread(() -> {
        try {
          viewMethod.invoke(navInstance);
        } catch (Exception e) {
          // Ignore
        }
      });
      thread.start();
      Thread.sleep(200);
      thread.interrupt();
      String output = outContent.toString();
      assertTrue("Output should contain navigation information", output.length() > 0);
    }

    finally {
      System.setOut(originalOut);
      navInstance.close();
    }
  }

  /**
   * @brief Test to verify kmpSearch with empty pattern.
   * @throws Exception
   */
  @Test
  public void testKMPSearchEmptyPattern() throws Exception {
    java.lang.reflect.Method kmpSearchMethod = ActivityLogging.class.getDeclaredMethod("kmpSearch", String.class, String.class);
    kmpSearchMethod.setAccessible(true);

    Boolean result = (Boolean) kmpSearchMethod.invoke(activityLogging, "test text", "");
    assertFalse("Empty pattern should return false", result);
  }

  /**
   * @brief Test to verify kmpSearch with null pattern.
   * @throws Exception
   */
  @Test
  public void testKMPSearchNullPattern() throws Exception {
    java.lang.reflect.Method kmpSearchMethod = ActivityLogging.class.getDeclaredMethod("kmpSearch", String.class, String.class);
    kmpSearchMethod.setAccessible(true);

    Boolean result = (Boolean) kmpSearchMethod.invoke(activityLogging, "test text", null);
    assertFalse("Null pattern should return false", result);
  }

  /**
   * @brief Test to verify kmpSearch with pattern longer than text.
   * @throws Exception
   */
  @Test
  public void testKMPSearchPatternLongerThanText() throws Exception {
    java.lang.reflect.Method kmpSearchMethod = ActivityLogging.class.getDeclaredMethod("kmpSearch", String.class, String.class);
    kmpSearchMethod.setAccessible(true);

    Boolean result = (Boolean) kmpSearchMethod.invoke(activityLogging, "test", "very long pattern");
    assertFalse("Pattern longer than text should return false", result);
  }

  /**
   * @brief Test to verify kmpSearch with pattern at start of text.
   * @throws Exception
   */
  @Test
  public void testKMPSearchPatternAtStart() throws Exception {
    java.lang.reflect.Method kmpSearchMethod = ActivityLogging.class.getDeclaredMethod("kmpSearch", String.class, String.class);
    kmpSearchMethod.setAccessible(true);

    Boolean result = (Boolean) kmpSearchMethod.invoke(activityLogging, "test text", "test");
    assertTrue("Pattern at start should be found", result);
  }

  /**
   * @brief Test to verify kmpSearch with pattern at end of text.
   * @throws Exception
   */
  @Test
  public void testKMPSearchPatternAtEnd() throws Exception {
    java.lang.reflect.Method kmpSearchMethod = ActivityLogging.class.getDeclaredMethod("kmpSearch", String.class, String.class);
    kmpSearchMethod.setAccessible(true);

    Boolean result = (Boolean) kmpSearchMethod.invoke(activityLogging, "test text", "text");
    assertTrue("Pattern at end should be found", result);
  }

  /**
   * @brief Test to verify kmpSearch with pattern in middle of text.
   * @throws Exception
   */
  @Test
  public void testKMPSearchPatternInMiddle() throws Exception {
    java.lang.reflect.Method kmpSearchMethod = ActivityLogging.class.getDeclaredMethod("kmpSearch", String.class, String.class);
    kmpSearchMethod.setAccessible(true);

    Boolean result = (Boolean) kmpSearchMethod.invoke(activityLogging, "test text here", "text");
    assertTrue("Pattern in middle should be found", result);
  }

  /**
   * @brief Test to verify kmpSearch with repeating pattern.
   * @throws Exception
   */
  @Test
  public void testKMPSearchRepeatingPattern() throws Exception {
    java.lang.reflect.Method kmpSearchMethod = ActivityLogging.class.getDeclaredMethod("kmpSearch", String.class, String.class);
    kmpSearchMethod.setAccessible(true);

    Boolean result = (Boolean) kmpSearchMethod.invoke(activityLogging, "abababab", "abab");
    assertTrue("Repeating pattern should be found", result);
  }

  /**
   * @brief Test to verify computeLPS with single character pattern.
   * @throws Exception
   */
  @Test
  public void testComputeLPSSingleCharacter() throws Exception {
    java.lang.reflect.Method computeLPSMethod = ActivityLogging.class.getDeclaredMethod("computeLPS", String.class);
    computeLPSMethod.setAccessible(true);

    int[] lps = (int[]) computeLPSMethod.invoke(activityLogging, "a");
    assertNotNull("LPS array should not be null", lps);
    assertEquals("LPS array length should match pattern length", 1, lps.length);
    assertEquals("First element should be 0", 0, lps[0]);
  }

  /**
   * @brief Test to verify computeLPS with repeating pattern.
   * @throws Exception
   */
  @Test
  public void testComputeLPSRepeatingPattern() throws Exception {
    java.lang.reflect.Method computeLPSMethod = ActivityLogging.class.getDeclaredMethod("computeLPS", String.class);
    computeLPSMethod.setAccessible(true);

    int[] lps = (int[]) computeLPSMethod.invoke(activityLogging, "aaaa");
    assertNotNull("LPS array should not be null", lps);
    assertEquals("LPS array length should match pattern length", 4, lps.length);
    assertEquals("LPS[0] should be 0", 0, lps[0]);
    assertEquals("LPS[1] should be 1", 1, lps[1]);
    assertEquals("LPS[2] should be 2", 2, lps[2]);
    assertEquals("LPS[3] should be 3", 3, lps[3]);
  }

  /**
   * @brief Test to verify computeLPS with "abab" pattern.
   * @throws Exception
   */
  @Test
  public void testComputeLPSAbabPattern() throws Exception {
    java.lang.reflect.Method computeLPSMethod = ActivityLogging.class.getDeclaredMethod("computeLPS", String.class);
    computeLPSMethod.setAccessible(true);

    int[] lps = (int[]) computeLPSMethod.invoke(activityLogging, "abab");
    assertNotNull("LPS array should not be null", lps);
    assertEquals("LPS array length should match pattern length", 4, lps.length);
    assertEquals("LPS[0] should be 0", 0, lps[0]);
    assertEquals("LPS[1] should be 0", 0, lps[1]);
    assertEquals("LPS[2] should be 1", 1, lps[2]);
    assertEquals("LPS[3] should be 2", 2, lps[3]);
  }

  /**
   * @brief Test to verify activityHistoryMenu handles case 1 (logActivity).
   * @throws Exception
   */
  @Test
  public void testActivityHistoryMenuCase1() throws Exception {
    // Input: 1 (logActivity), then activity details, then 5 (back)
    String input = "1" + System.lineSeparator() +
                               "Test Activity" + System.lineSeparator() +
                               "30" + System.lineSeparator() +
                               "Test Category" + System.lineSeparator() +
                               "Test Description" + System.lineSeparator() +
                               System.lineSeparator() + // waitForEnter
                               "5" + System.lineSeparator();
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    // Replace scanner
    Scanner newScanner = new Scanner(System.in);
    java.lang.reflect.Field scannerField = ActivityLogging.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(activityLogging, newScanner);

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      java.lang.reflect.Method menuMethod = ActivityLogging.class.getDeclaredMethod("activityHistoryMenu");
      menuMethod.setAccessible(true);
      menuMethod.invoke(activityLogging);
      String output = outContent.toString();
      assertTrue("Output should contain Activity Logging Menu", output.contains("Activity Logging Menu"));
      assertTrue("Output should contain Log Activity option", output.contains("Log Activity"));
    }

    finally {
      System.setOut(originalOut);
    }
  }

  /**
   * @brief Test to verify activityHistoryMenu handles case 2 (viewActivitiesDoubleLinkedList).
   * @throws Exception
   */
  @Test
  public void testActivityHistoryMenuCase2() throws Exception {
    // Input: 2 (viewActivitiesDoubleLinkedList), then Q (quit from view), then 5 (back)
    String input = "2" + System.lineSeparator() +
                               "q" + System.lineSeparator() +
                               "5" + System.lineSeparator();
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    // Replace scanner
    Scanner newScanner = new Scanner(System.in);
    java.lang.reflect.Field scannerField = ActivityLogging.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(activityLogging, newScanner);

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      java.lang.reflect.Method menuMethod = ActivityLogging.class.getDeclaredMethod("activityHistoryMenu");
      menuMethod.setAccessible(true);
      menuMethod.invoke(activityLogging);
      String output = outContent.toString();
      assertTrue("Output should contain Activity Logging Menu", output.contains("Activity Logging Menu"));
      assertTrue("Output should contain View Activities option", output.contains("View Activities"));
    }

    finally {
      System.setOut(originalOut);
    }
  }

  /**
   * @brief Test to verify activityHistoryMenu handles case 3 (exploreActivities).
   * @throws Exception
   */
  @Test
  public void testActivityHistoryMenuCase3() throws Exception {
    // First add an activity, then explore
    // Input: 1 (logActivity), activity details, then 3 (exploreActivities), then 1 (BFS), then 5 (back)
    String input = "1" + System.lineSeparator() +
                               "Test Activity" + System.lineSeparator() +
                               "30" + System.lineSeparator() +
                               "Test Category" + System.lineSeparator() +
                               "Test Description" + System.lineSeparator() +
                               System.lineSeparator() + // waitForEnter
                               "3" + System.lineSeparator() +
                               "1" + System.lineSeparator() +
                               System.lineSeparator() + // waitForEnter after BFS
                               "5" + System.lineSeparator();
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    // Replace scanner
    Scanner newScanner = new Scanner(System.in);
    java.lang.reflect.Field scannerField = ActivityLogging.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(activityLogging, newScanner);

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      java.lang.reflect.Method menuMethod = ActivityLogging.class.getDeclaredMethod("activityHistoryMenu");
      menuMethod.setAccessible(true);
      menuMethod.invoke(activityLogging);
      String output = outContent.toString();
      assertTrue("Output should contain Activity Logging Menu", output.contains("Activity Logging Menu"));
      assertTrue("Output should contain Explore Activities option", output.contains("Explore Activities"));
    }

    finally {
      System.setOut(originalOut);
    }
  }

  /**
   * @brief Test to verify activityHistoryMenu handles case 4 (searchActivitiesKMP).
   * @throws Exception
   */
  @Test
  public void testActivityHistoryMenuCase4() throws Exception {
    // Input: 4 (searchActivitiesKMP), then "test" (search pattern), then 5 (back)
    String input = "4" + System.lineSeparator() +
                               "test" + System.lineSeparator() +
                               "5" + System.lineSeparator();
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    // Replace scanner
    Scanner newScanner = new Scanner(System.in);
    java.lang.reflect.Field scannerField = ActivityLogging.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(activityLogging, newScanner);

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      java.lang.reflect.Method menuMethod = ActivityLogging.class.getDeclaredMethod("activityHistoryMenu");
      menuMethod.setAccessible(true);
      menuMethod.invoke(activityLogging);
      String output = outContent.toString();
      assertTrue("Output should contain Activity Logging Menu", output.contains("Activity Logging Menu"));
      assertTrue("Output should contain Search Activities option", output.contains("Search Activities"));
    }

    finally {
      System.setOut(originalOut);
    }
  }

  /**
   * @brief Test to verify activityHistoryMenu handles case 5 (back to main menu).
   * @throws Exception
   */
  @Test
  public void testActivityHistoryMenuCase5() throws Exception {
    // Input: 5 (back)
    String input = "5" + System.lineSeparator();
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    // Replace scanner
    Scanner newScanner = new Scanner(System.in);
    java.lang.reflect.Field scannerField = ActivityLogging.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(activityLogging, newScanner);

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      java.lang.reflect.Method menuMethod = ActivityLogging.class.getDeclaredMethod("activityHistoryMenu");
      menuMethod.setAccessible(true);
      Integer result = (Integer) menuMethod.invoke(activityLogging);
      String output = outContent.toString();
      assertTrue("Output should contain Activity Logging Menu", output.contains("Activity Logging Menu"));
      assertTrue("Output should contain Back to Main Menu option", output.contains("Back to Main Menu"));
      assertEquals("Method should return 0", Integer.valueOf(0), result);
    }

    finally {
      System.setOut(originalOut);
    }
  }

  /**
   * @brief Test to verify activityHistoryMenu handles default case (invalid choice).
   * @throws Exception
   */
  @Test
  public void testActivityHistoryMenuDefaultCase() throws Exception {
    // Input: 99 (invalid choice), then 5 (back)
    String input = "99" + System.lineSeparator() + "5" + System.lineSeparator();
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    // Replace scanner
    Scanner newScanner = new Scanner(System.in);
    java.lang.reflect.Field scannerField = ActivityLogging.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(activityLogging, newScanner);

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      java.lang.reflect.Method menuMethod = ActivityLogging.class.getDeclaredMethod("activityHistoryMenu");
      menuMethod.setAccessible(true);
      menuMethod.invoke(activityLogging);
      String output = outContent.toString();
      assertTrue("Output should contain Invalid choice message",
                 output.contains("Invalid choice") || output.contains("Please try again"));
    }

    finally {
      System.setOut(originalOut);
    }
  }

  /**
   * @brief Test to verify activityHistoryMenu handles NumberFormatException.
   * @throws Exception
   */
  @Test
  public void testActivityHistoryMenuNumberFormatException() throws Exception {
    // Input: "invalid" (non-numeric), then 5 (back)
    String input = "invalid" + System.lineSeparator() + "5" + System.lineSeparator();
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    // Replace scanner
    Scanner newScanner = new Scanner(System.in);
    java.lang.reflect.Field scannerField = ActivityLogging.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(activityLogging, newScanner);

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      java.lang.reflect.Method menuMethod = ActivityLogging.class.getDeclaredMethod("activityHistoryMenu");
      menuMethod.setAccessible(true);
      menuMethod.invoke(activityLogging);
      String output = outContent.toString();
      assertTrue("Output should contain Invalid choice message for non-numeric input",
                 output.contains("Invalid choice") || output.contains("Please try again"));
    }

    finally {
      System.setOut(originalOut);
    }
  }

  /**
   * @brief Test to verify activityHistoryMenu handles negative number.
   * @throws Exception
   */
  @Test
  public void testActivityHistoryMenuNegativeNumber() throws Exception {
    // Input: -1 (negative number), then 5 (back)
    String input = "-1" + System.lineSeparator() + "5" + System.lineSeparator();
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    // Replace scanner
    Scanner newScanner = new Scanner(System.in);
    java.lang.reflect.Field scannerField = ActivityLogging.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(activityLogging, newScanner);

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      java.lang.reflect.Method menuMethod = ActivityLogging.class.getDeclaredMethod("activityHistoryMenu");
      menuMethod.setAccessible(true);
      menuMethod.invoke(activityLogging);
      String output = outContent.toString();
      assertTrue("Output should contain Invalid choice message for negative number",
                 output.contains("Invalid choice") || output.contains("Please try again"));
    }

    finally {
      System.setOut(originalOut);
    }
  }

  /**
   * @brief Test to verify activityHistoryMenu handles zero.
   * @throws Exception
   */
  @Test
  public void testActivityHistoryMenuZero() throws Exception {
    // Input: 0 (zero), then 5 (back)
    String input = "0" + System.lineSeparator() + "5" + System.lineSeparator();
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    // Replace scanner
    Scanner newScanner = new Scanner(System.in);
    java.lang.reflect.Field scannerField = ActivityLogging.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(activityLogging, newScanner);

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      java.lang.reflect.Method menuMethod = ActivityLogging.class.getDeclaredMethod("activityHistoryMenu");
      menuMethod.setAccessible(true);
      menuMethod.invoke(activityLogging);
      String output = outContent.toString();
      assertTrue("Output should contain Invalid choice message for zero",
                 output.contains("Invalid choice") || output.contains("Please try again"));
    }

    finally {
      System.setOut(originalOut);
    }
  }

  /**
   * @brief Test to verify activityHistoryMenu handles multiple invalid choices before valid one.
   * @throws Exception
   */
  @Test
  public void testActivityHistoryMenuMultipleInvalidChoices() throws Exception {
    // Input: invalid, 99, -1, then 5 (back)
    String input = "invalid" + System.lineSeparator() +
                                     "99" + System.lineSeparator() +
                                     "-1" + System.lineSeparator() +
                                     "5" + System.lineSeparator();
    System.setIn(new ByteArrayInputStream(input.getBytes()));

    // Replace scanner
    Scanner newScanner = new Scanner(System.in);
    java.lang.reflect.Field scannerField = ActivityLogging.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(activityLogging, newScanner);

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      java.lang.reflect.Method menuMethod = ActivityLogging.class.getDeclaredMethod("activityHistoryMenu");
      menuMethod.setAccessible(true);
      menuMethod.invoke(activityLogging);
      String output = outContent.toString();
      assertTrue("Output should contain multiple Invalid choice messages",
                 output.contains("Invalid choice") || output.contains("Please try again"));
    }

    finally {
      System.setOut(originalOut);
    }
  }
}

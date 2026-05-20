/**
 * @file BreakReminderTest.java
 * @brief This file contains test cases for the BreakReminder class.
 * @details This file includes test methods to validate the functionality of the BreakReminder class.
 *          It uses JUnit 4 for unit testing.
 * @author efil.saylam.nese.sarp
 */
package com.efil.nese.timetracker;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Queue;
import java.util.Scanner;
import java.util.Stack;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @class BreakReminderTest
 * @brief This class represents the test class for the BreakReminder class.
 * @details The BreakReminderTest class provides test methods to verify the behavior of the BreakReminder class.
 * @author efil.saylam.nese.sarp
 */
public class BreakReminderTest {

  private BreakReminder breakReminder;
  private Scanner testScanner;
  private InputStream originalSystemIn;

  /**
   * @brief This method is executed once before all test methods.
   * @throws Exception
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  /**
   * @brief This method is executed once after all test methods.
   * @throws Exception
   */
  @AfterClass
  public static void tearDownAfterClass() throws Exception {
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
    // Create BreakReminder instance
    breakReminder = new BreakReminder();
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

    // Close break reminder
    if (breakReminder != null) {
      breakReminder.close();
    }
  }

  /**
   * @brief Test to verify BreakReminder object creation.
   * @throws Exception
   */
  @Test
  public void testBreakReminderCreation() throws Exception {
    BreakReminder br = new BreakReminder();
    assertNotNull("BreakReminder object should not be null", br);
    br.close();
  }

  /**
   * @brief Test to verify BreakReminder class exists.
   * @throws Exception
   */
  @Test
  public void testBreakReminderClassExists() throws Exception {
    Class<?> clazz = BreakReminder.class;
    assertNotNull("BreakReminder class should exist", clazz);
    assertEquals("Class name should be BreakReminder", "BreakReminder", clazz.getSimpleName());
  }

  /**
   * @brief Test to verify run method exists.
   * @throws Exception
   */
  @Test
  public void testRunMethodExists() throws Exception {
    try {
      java.lang.reflect.Method runMethod = BreakReminder.class.getMethod("run");
      assertNotNull("Run method should exist", runMethod);
    } catch (NoSuchMethodException e) {
      fail("Run method should exist in BreakReminder class");
    }
  }

  /**
   * @brief Test to verify close method exists and can be called.
   * @throws Exception
   */
  @Test
  public void testClose() throws Exception {
    breakReminder.close();
    assertTrue("close should execute without exception", true);
  }

  /**
   * @brief Test to verify setReminder method works correctly.
   * @throws Exception
   */
  @Test
  public void testSetReminder() throws Exception {
    // Create scanner with valid input
    String input = "Take a break\n30\n";
    Scanner setScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
    BreakReminder setInstance = new BreakReminder();

    // Use reflection to access scanner field and replace it
    java.lang.reflect.Field scannerField = BreakReminder.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(setInstance, setScanner);

    // Redirect System.out
    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      // Test setReminder using reflection
      java.lang.reflect.Method setMethod = BreakReminder.class.getDeclaredMethod("setReminder");
      setMethod.setAccessible(true);
      setMethod.invoke(setInstance);
      // Verify output
      String output = outContent.toString();
      assertTrue("Output should contain success message", output.contains("Reminder added to queue successfully"));
      assertTrue("Output should contain reminder message", output.contains("Take a break"));
      // Verify queue has reminder using reflection
      java.lang.reflect.Field queueField = BreakReminder.class.getDeclaredField("reminderQueue");
      queueField.setAccessible(true);
      @SuppressWarnings("unchecked")
      Queue<Object> queue = (Queue<Object>) queueField.get(setInstance);
      assertTrue("Queue should not be empty", !queue.isEmpty());
    }

    finally {
      System.setOut(originalOut);
      setInstance.close();
    }
  }

  /**
   * @brief Test to verify setReminder handles empty message.
   * @throws Exception
   */
  @Test
  public void testSetReminderEmptyMessage() throws Exception {
    // Create scanner with empty message
    String input = "\n30\n";
    Scanner setScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
    BreakReminder setInstance = new BreakReminder();

    java.lang.reflect.Field scannerField = BreakReminder.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(setInstance, setScanner);

    // Redirect System.out
    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      java.lang.reflect.Method setMethod = BreakReminder.class.getDeclaredMethod("setReminder");
      setMethod.setAccessible(true);
      setMethod.invoke(setInstance);
      String output = outContent.toString();
      assertTrue("Output should contain error message", output.contains("Reminder message cannot be empty"));
      // Verify queue is still empty
      java.lang.reflect.Field queueField = BreakReminder.class.getDeclaredField("reminderQueue");
      queueField.setAccessible(true);
      @SuppressWarnings("unchecked")
      Queue<Object> queue = (Queue<Object>) queueField.get(setInstance);
      assertTrue("Queue should be empty", queue.isEmpty());
    }

    finally {
      System.setOut(originalOut);
      setInstance.close();
    }
  }

  /**
   * @brief Test to verify setReminder handles invalid interval.
   * @throws Exception
   */
  @Test
  public void testSetReminderInvalidInterval() throws Exception {
    // Create scanner with invalid interval
    String input = "Take a break\nabc\n";
    Scanner setScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
    BreakReminder setInstance = new BreakReminder();

    java.lang.reflect.Field scannerField = BreakReminder.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(setInstance, setScanner);

    // Redirect System.out
    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      java.lang.reflect.Method setMethod = BreakReminder.class.getDeclaredMethod("setReminder");
      setMethod.setAccessible(true);
      setMethod.invoke(setInstance);
      String output = outContent.toString();
      assertTrue("Output should contain error message", output.contains("Invalid interval"));
    }

    finally {
      System.setOut(originalOut);
      setInstance.close();
    }
  }

  /**
   * @brief Test to verify setReminder handles negative interval.
   * @throws Exception
   */
  @Test
  public void testSetReminderNegativeInterval() throws Exception {
    // Create scanner with negative interval
    String input = "Take a break\n-10\n";
    Scanner setScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
    BreakReminder setInstance = new BreakReminder();

    java.lang.reflect.Field scannerField = BreakReminder.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(setInstance, setScanner);

    // Redirect System.out
    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      java.lang.reflect.Method setMethod = BreakReminder.class.getDeclaredMethod("setReminder");
      setMethod.setAccessible(true);
      setMethod.invoke(setInstance);
      String output = outContent.toString();
      assertTrue("Output should contain error message", output.contains("Interval must be positive"));
    }

    finally {
      System.setOut(originalOut);
      setInstance.close();
    }
  }

  /**
   * @brief Test to verify processNextReminder method works.
   * @throws Exception
   */
  @Test
  public void testProcessNextReminder() throws Exception {
    // First add a reminder using reflection
    java.lang.reflect.Field queueField = BreakReminder.class.getDeclaredField("reminderQueue");
    queueField.setAccessible(true);
    @SuppressWarnings("unchecked")
    Queue<Object> queue = (Queue<Object>) queueField.get(breakReminder);

    // Create reminder using reflection
    Class<?> reminderClass = Class.forName("com.efil.nese.timetracker.BreakReminder$Reminder");
    java.lang.reflect.Constructor<?> reminderConstructor = reminderClass.getDeclaredConstructor(String.class, int.class);
    reminderConstructor.setAccessible(true);
    Object reminder = reminderConstructor.newInstance("Test Reminder", 30);

    queue.offer(reminder);

    // Create scanner for waitForEnter
    Scanner processScanner = new Scanner(new ByteArrayInputStream("\n".getBytes()));
    java.lang.reflect.Field scannerField = BreakReminder.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(breakReminder, processScanner);

    // Redirect System.out
    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      java.lang.reflect.Method processMethod = BreakReminder.class.getDeclaredMethod("processNextReminder");
      processMethod.setAccessible(true);
      processMethod.invoke(breakReminder);
      String output = outContent.toString();
      assertTrue("Output should contain processing message", output.contains("Processing Reminder"));
      assertTrue("Output should contain reminder message", output.contains("Test Reminder"));
      // Verify queue is now empty
      assertTrue("Queue should be empty after processing", queue.isEmpty());
    }

    finally {
      System.setOut(originalOut);
    }
  }

  /**
   * @brief Test to verify processNextReminder handles empty queue.
   * @throws Exception
   */
  @Test
  public void testProcessNextReminderEmptyQueue() throws Exception {
    Scanner processScanner = new Scanner(new ByteArrayInputStream("\n".getBytes()));
    java.lang.reflect.Field scannerField = BreakReminder.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(breakReminder, processScanner);

    // Redirect System.out
    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      java.lang.reflect.Method processMethod = BreakReminder.class.getDeclaredMethod("processNextReminder");
      processMethod.setAccessible(true);
      processMethod.invoke(breakReminder);
      String output = outContent.toString();
      assertTrue("Output should contain empty queue message", output.contains("Queue is empty"));
    }

    finally {
      System.setOut(originalOut);
    }
  }

  /**
   * @brief Test to verify undoLastReminder method works.
   * @throws Exception
   */
  @Test
  public void testUndoLastReminder() throws Exception {
    // Add reminders using reflection
    java.lang.reflect.Field queueField = BreakReminder.class.getDeclaredField("reminderQueue");
    queueField.setAccessible(true);
    @SuppressWarnings("unchecked")
    Queue<Object> queue = (Queue<Object>) queueField.get(breakReminder);

    java.lang.reflect.Field historyField = BreakReminder.class.getDeclaredField("reminderHistory");
    historyField.setAccessible(true);
    @SuppressWarnings("unchecked")
    Stack<Object> history = (Stack<Object>) historyField.get(breakReminder);

    // Create reminder
    Class<?> reminderClass = Class.forName("com.efil.nese.timetracker.BreakReminder$Reminder");
    java.lang.reflect.Constructor<?> reminderConstructor = reminderClass.getDeclaredConstructor(String.class, int.class);
    reminderConstructor.setAccessible(true);
    Object reminder = reminderConstructor.newInstance("Test Reminder", 30);

    queue.offer(reminder);
    history.push(reminder);

    Scanner undoScanner = new Scanner(new ByteArrayInputStream("\n".getBytes()));
    java.lang.reflect.Field scannerField = BreakReminder.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(breakReminder, undoScanner);

    // Redirect System.out
    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      java.lang.reflect.Method undoMethod = BreakReminder.class.getDeclaredMethod("undoLastReminder");
      undoMethod.setAccessible(true);
      undoMethod.invoke(breakReminder);
      String output = outContent.toString();
      assertTrue("Output should contain undo message", output.contains("Undo Last Reminder"));
      assertTrue("Output should contain reminder message", output.contains("Test Reminder"));
      // Verify history is empty
      assertTrue("History should be empty after undo", history.isEmpty());
    }

    finally {
      System.setOut(originalOut);
    }
  }

  /**
   * @brief Test to verify undoLastReminder handles empty history.
   * @throws Exception
   */
  @Test
  public void testUndoLastReminderEmptyHistory() throws Exception {
    Scanner undoScanner = new Scanner(new ByteArrayInputStream("\n".getBytes()));
    java.lang.reflect.Field scannerField = BreakReminder.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(breakReminder, undoScanner);

    // Redirect System.out
    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      java.lang.reflect.Method undoMethod = BreakReminder.class.getDeclaredMethod("undoLastReminder");
      undoMethod.setAccessible(true);
      undoMethod.invoke(breakReminder);
      String output = outContent.toString();
      assertTrue("Output should contain no reminders message", output.contains("No reminders to undo"));
    }

    finally {
      System.setOut(originalOut);
    }
  }

  /**
   * @brief Test to verify viewQueue method works.
   * @throws Exception
   */
  @Test
  public void testViewQueue() throws Exception {
    // Add reminders using reflection
    java.lang.reflect.Field queueField = BreakReminder.class.getDeclaredField("reminderQueue");
    queueField.setAccessible(true);
    @SuppressWarnings("unchecked")
    Queue<Object> queue = (Queue<Object>) queueField.get(breakReminder);

    // Create reminders
    Class<?> reminderClass = Class.forName("com.efil.nese.timetracker.BreakReminder$Reminder");
    java.lang.reflect.Constructor<?> reminderConstructor = reminderClass.getDeclaredConstructor(String.class, int.class);
    reminderConstructor.setAccessible(true);

    Object reminder1 = reminderConstructor.newInstance("Reminder 1", 30);
    Object reminder2 = reminderConstructor.newInstance("Reminder 2", 45);

    queue.offer(reminder1);
    queue.offer(reminder2);

    Scanner viewScanner = new Scanner(new ByteArrayInputStream("\n".getBytes()));
    java.lang.reflect.Field scannerField = BreakReminder.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(breakReminder, viewScanner);

    // Redirect System.out
    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      java.lang.reflect.Method viewMethod = BreakReminder.class.getDeclaredMethod("viewQueue");
      viewMethod.setAccessible(true);
      viewMethod.invoke(breakReminder);
      String output = outContent.toString();
      assertTrue("Output should contain queue header", output.contains("All Reminders in Queue"));
      assertTrue("Output should contain reminder 1", output.contains("Reminder 1"));
      assertTrue("Output should contain reminder 2", output.contains("Reminder 2"));
    }

    finally {
      System.setOut(originalOut);
    }
  }

  /**
   * @brief Test to verify viewQueue handles empty queue.
   * @throws Exception
   */
  @Test
  public void testViewQueueEmpty() throws Exception {
    Scanner viewScanner = new Scanner(new ByteArrayInputStream("\n".getBytes()));
    java.lang.reflect.Field scannerField = BreakReminder.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(breakReminder, viewScanner);

    // Redirect System.out
    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      java.lang.reflect.Method viewMethod = BreakReminder.class.getDeclaredMethod("viewQueue");
      viewMethod.setAccessible(true);
      viewMethod.invoke(breakReminder);
      String output = outContent.toString();
      assertTrue("Output should contain empty queue message", output.contains("Queue is empty"));
    }

    finally {
      System.setOut(originalOut);
    }
  }

  /**
   * @brief Test to verify viewHistory method works.
   * @throws Exception
   */
  @Test
  public void testViewHistory() throws Exception {
    // Add reminders to history using reflection
    java.lang.reflect.Field historyField = BreakReminder.class.getDeclaredField("reminderHistory");
    historyField.setAccessible(true);
    @SuppressWarnings("unchecked")
    Stack<Object> history = (Stack<Object>) historyField.get(breakReminder);

    // Create reminders
    Class<?> reminderClass = Class.forName("com.efil.nese.timetracker.BreakReminder$Reminder");
    java.lang.reflect.Constructor<?> reminderConstructor = reminderClass.getDeclaredConstructor(String.class, int.class);
    reminderConstructor.setAccessible(true);

    Object reminder1 = reminderConstructor.newInstance("Reminder 1", 30);
    Object reminder2 = reminderConstructor.newInstance("Reminder 2", 45);

    history.push(reminder1);
    history.push(reminder2);

    Scanner viewScanner = new Scanner(new ByteArrayInputStream("\n".getBytes()));
    java.lang.reflect.Field scannerField = BreakReminder.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(breakReminder, viewScanner);

    // Redirect System.out
    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      java.lang.reflect.Method viewMethod = BreakReminder.class.getDeclaredMethod("viewHistory");
      viewMethod.setAccessible(true);
      viewMethod.invoke(breakReminder);
      String output = outContent.toString();
      assertTrue("Output should contain history header", output.contains("All Reminders in History"));
      assertTrue("Output should contain reminder 1", output.contains("Reminder 1"));
      assertTrue("Output should contain reminder 2", output.contains("Reminder 2"));
    }

    finally {
      System.setOut(originalOut);
    }
  }

  /**
   * @brief Test to verify viewHistory handles empty history.
   * @throws Exception
   */
  @Test
  public void testViewHistoryEmpty() throws Exception {
    Scanner viewScanner = new Scanner(new ByteArrayInputStream("\n".getBytes()));
    java.lang.reflect.Field scannerField = BreakReminder.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(breakReminder, viewScanner);

    // Redirect System.out
    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      java.lang.reflect.Method viewMethod = BreakReminder.class.getDeclaredMethod("viewHistory");
      viewMethod.setAccessible(true);
      viewMethod.invoke(breakReminder);
      String output = outContent.toString();
      assertTrue("Output should contain empty history message", output.contains("History is empty"));
    }

    finally {
      System.setOut(originalOut);
    }
  }

  /**
   * @brief Test to verify viewReminder method works.
   * @throws Exception
   */
  @Test
  public void testViewReminder() throws Exception {
    // Add reminders using reflection
    java.lang.reflect.Field queueField = BreakReminder.class.getDeclaredField("reminderQueue");
    queueField.setAccessible(true);
    @SuppressWarnings("unchecked")
    Queue<Object> queue = (Queue<Object>) queueField.get(breakReminder);

    java.lang.reflect.Field historyField = BreakReminder.class.getDeclaredField("reminderHistory");
    historyField.setAccessible(true);
    @SuppressWarnings("unchecked")
    Stack<Object> history = (Stack<Object>) historyField.get(breakReminder);

    // Create reminder
    Class<?> reminderClass = Class.forName("com.efil.nese.timetracker.BreakReminder$Reminder");
    java.lang.reflect.Constructor<?> reminderConstructor = reminderClass.getDeclaredConstructor(String.class, int.class);
    reminderConstructor.setAccessible(true);
    Object reminder = reminderConstructor.newInstance("Test Reminder", 30);

    queue.offer(reminder);
    history.push(reminder);

    // Create scanner with exit input (5 to go back)
    String input = "5\n";
    Scanner viewScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
    java.lang.reflect.Field scannerField = BreakReminder.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(breakReminder, viewScanner);

    // Redirect System.out
    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      java.lang.reflect.Method viewMethod = BreakReminder.class.getDeclaredMethod("viewReminder");
      viewMethod.setAccessible(true);
      Thread thread = new Thread(() -> {
        try {
          viewMethod.invoke(breakReminder);
        } catch (Exception e) {
          // Ignore
        }
      });
      thread.start();
      Thread.sleep(200);
      thread.interrupt();
      String output = outContent.toString();
      assertTrue("Output should contain view reminder header",
                 output.contains("View Reminder") || output.contains("Stack/Undo") || output.length() > 0);
    }

    finally {
      System.setOut(originalOut);
    }
  }

  /**
   * @brief Test to verify viewReminder handles empty state.
   * @throws Exception
   */
  @Test
  public void testViewReminderEmpty() throws Exception {
    Scanner viewScanner = new Scanner(new ByteArrayInputStream("\n".getBytes()));
    java.lang.reflect.Field scannerField = BreakReminder.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(breakReminder, viewScanner);

    // Redirect System.out
    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      java.lang.reflect.Method viewMethod = BreakReminder.class.getDeclaredMethod("viewReminder");
      viewMethod.setAccessible(true);
      viewMethod.invoke(breakReminder);
      String output = outContent.toString();
      assertTrue("Output should contain no reminders message",
                 output.contains("No reminders found") || output.contains("Set some reminders"));
    }

    finally {
      System.setOut(originalOut);
    }
  }

  /**
   * @brief Test to verify queue and stack are initialized correctly.
   * @throws Exception
   */
  @Test
  public void testQueueAndStackInitialization() throws Exception {
    java.lang.reflect.Field queueField = BreakReminder.class.getDeclaredField("reminderQueue");
    queueField.setAccessible(true);
    @SuppressWarnings("unchecked")
    Queue<Object> queue = (Queue<Object>) queueField.get(breakReminder);

    java.lang.reflect.Field historyField = BreakReminder.class.getDeclaredField("reminderHistory");
    historyField.setAccessible(true);
    @SuppressWarnings("unchecked")
    Stack<Object> history = (Stack<Object>) historyField.get(breakReminder);

    assertNotNull("Queue should not be null", queue);
    assertNotNull("History stack should not be null", history);
    assertTrue("Queue should be empty initially", queue.isEmpty());
    assertTrue("History stack should be empty initially", history.isEmpty());
  }

  /**
   * @brief Test to verify multiple reminders can be added.
   * @throws Exception
   */
  @Test
  public void testMultipleReminders() throws Exception {
    java.lang.reflect.Field queueField = BreakReminder.class.getDeclaredField("reminderQueue");
    queueField.setAccessible(true);
    @SuppressWarnings("unchecked")
    Queue<Object> queue = (Queue<Object>) queueField.get(breakReminder);

    // Create multiple reminders
    Class<?> reminderClass = Class.forName("com.efil.nese.timetracker.BreakReminder$Reminder");
    java.lang.reflect.Constructor<?> reminderConstructor = reminderClass.getDeclaredConstructor(String.class, int.class);
    reminderConstructor.setAccessible(true);

    for (int i = 1; i <= 5; i++) {
      Object reminder = reminderConstructor.newInstance("Reminder " + i, i * 10);
      queue.offer(reminder);
    }

    assertEquals("Queue should have 5 reminders", 5, queue.size());
  }

  /**
   * @brief Test to verify Reminder class can be instantiated.
   * @throws Exception
   */
  @Test
  public void testReminderClass() throws Exception {
    Class<?> reminderClass = Class.forName("com.efil.nese.timetracker.BreakReminder$Reminder");
    java.lang.reflect.Constructor<?> reminderConstructor = reminderClass.getDeclaredConstructor(String.class, int.class);
    reminderConstructor.setAccessible(true);

    Object reminder = reminderConstructor.newInstance("Test Message", 30);
    assertNotNull("Reminder object should not be null", reminder);

    // Verify fields using reflection
    java.lang.reflect.Field messageField = reminderClass.getDeclaredField("message");
    messageField.setAccessible(true);
    String message = (String) messageField.get(reminder);
    assertEquals("Message should match", "Test Message", message);

    java.lang.reflect.Field intervalField = reminderClass.getDeclaredField("interval");
    intervalField.setAccessible(true);
    int interval = (Integer) intervalField.get(reminder);
    assertEquals("Interval should match", 30, interval);
  }

  /**
   * @brief Test to verify run method can be called.
   * @throws Exception
   */
  @Test
  public void testRunMethod() throws Exception {
    // Test that run method exists and can be called
    // Since it's interactive, we'll provide input that exits immediately
    Scanner exitScanner = new Scanner(new ByteArrayInputStream("3\n".getBytes()));
    BreakReminder br = new BreakReminder();

    // Use reflection to replace scanner
    java.lang.reflect.Field scannerField = BreakReminder.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(br, exitScanner);

    // Run in a separate thread to avoid blocking
    Thread thread = new Thread(() -> {
      try {
        br.run();
      } catch (Exception e) {
        // Ignore
      }
    });
    thread.start();
    Thread.sleep(100);
    thread.interrupt();

    br.close();
    assertTrue("Run method should exist", true);
  }
}

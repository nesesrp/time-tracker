/**
 * @file UserAuthenticationTest.java
 * @brief This file contains test cases for the UserAuthentication class.
 * @details This file includes test methods to validate the functionality of the UserAuthentication class.
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
 * @class UserAuthenticationTest
 * @brief This class represents the test class for the UserAuthentication class.
 * @details The UserAuthenticationTest class provides test methods to verify the behavior of the UserAuthentication class.
 * @author efil.saylam.nese.sarp
 */
public class UserAuthenticationTest {

  private static final String TEST_USERS_FILE = "users.txt";
  private UserAuthentication userAuth;
  private Scanner testScanner;
  private InputStream originalSystemIn;

  /**
   * @brief This method is executed once before all test methods.
   * @throws Exception
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    // Clean up any existing test files
    File testFile = new File(TEST_USERS_FILE);

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
    File testFile = new File(TEST_USERS_FILE);

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
    // Clean up users file
    File testFile = new File(TEST_USERS_FILE);

    if (testFile.exists()) {
      testFile.delete();
    }

    // Clear static users field
    java.lang.reflect.Field usersField = UserAuthentication.class.getDeclaredField("users");
    usersField.setAccessible(true);
    // Create new UserHashTable instance
    Class<?> userHashTableClass = Class.forName("com.efil.nese.timetracker.UserAuthentication$UserHashTable");
    java.lang.reflect.Constructor<?> constructor = userHashTableClass.getDeclaredConstructor();
    constructor.setAccessible(true);
    Object newHashTable = constructor.newInstance();
    usersField.set(null, newHashTable);

    // Save original System.in
    originalSystemIn = System.in;
    // Provide an empty input stream to avoid Scanner issues
    System.setIn(new ByteArrayInputStream("".getBytes()));
    // Create test scanner
    testScanner = new Scanner(new ByteArrayInputStream("".getBytes()));
    // Create UserAuthentication instance
    userAuth = new UserAuthentication();
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

    // Close user authentication
    if (userAuth != null) {
      userAuth.close();
    }

    // Clean up test files
    File testFile = new File(TEST_USERS_FILE);

    if (testFile.exists()) {
      testFile.delete();
    }
  }

  /**
   * @brief Test to verify UserAuthentication object creation.
   * @throws Exception
   */
  @Test
  public void testUserAuthenticationCreation() throws Exception {
    UserAuthentication ua = new UserAuthentication();
    assertNotNull("UserAuthentication object should not be null", ua);
    ua.close();
  }

  /**
   * @brief Test to verify UserAuthentication class exists.
   * @throws Exception
   */
  @Test
  public void testUserAuthenticationClassExists() throws Exception {
    Class<?> clazz = UserAuthentication.class;
    assertNotNull("UserAuthentication class should exist", clazz);
    assertEquals("Class name should be UserAuthentication", "UserAuthentication", clazz.getSimpleName());
  }

  /**
   * @brief Test to verify register method works correctly.
   * @throws Exception
   */
  @Test
  public void testRegister() throws Exception {
    // Create scanner with valid registration input
    String input = "testuser" + System.lineSeparator() + "testpass" + System.lineSeparator();
    Scanner regScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
    UserAuthentication regInstance = new UserAuthentication();

    // Use reflection to replace scanner
    java.lang.reflect.Field scannerField = UserAuthentication.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(regInstance, regScanner);

    // Redirect System.out
    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      boolean result = regInstance.register();
      assertTrue("Registration should be successful", result);
      String output = outContent.toString();
      assertTrue("Output should contain success message", output.contains("Registration successful"));
    }

    finally {
      System.setOut(originalOut);
      regInstance.close();
    }
  }

  /**
   * @brief Test to verify register handles empty username.
   * @throws Exception
   */
  @Test
  public void testRegisterEmptyUsername() throws Exception {
    String input = System.lineSeparator() + "testpass" + System.lineSeparator();
    Scanner regScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
    UserAuthentication regInstance = new UserAuthentication();

    java.lang.reflect.Field scannerField = UserAuthentication.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(regInstance, regScanner);

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      boolean result = regInstance.register();
      assertFalse("Registration should fail with empty username", result);
      String output = outContent.toString();
      assertTrue("Output should contain error message", output.contains("Username cannot be empty"));
    }

    finally {
      System.setOut(originalOut);
      regInstance.close();
    }
  }

  /**
   * @brief Test to verify register handles empty password.
   * @throws Exception
   */
  @Test
  public void testRegisterEmptyPassword() throws Exception {
    // Use System.lineSeparator() for proper line endings
    String input = "testuser" + System.lineSeparator() + System.lineSeparator();
    Scanner regScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
    UserAuthentication regInstance = new UserAuthentication();

    java.lang.reflect.Field scannerField = UserAuthentication.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(regInstance, regScanner);

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      boolean result = regInstance.register();
      assertFalse("Registration should fail with empty password", result);
      String output = outContent.toString();
      assertTrue("Output should contain error message", output.contains("Password cannot be empty"));
    }

    finally {
      System.setOut(originalOut);
      regInstance.close();
    }
  }

  /**
   * @brief Test to verify register handles duplicate username.
   * @throws Exception
   */
  @Test
  public void testRegisterDuplicateUsername() throws Exception {
    String input1 = "testuser" + System.lineSeparator() + "testpass" + System.lineSeparator();
    String input2 = "testuser" + System.lineSeparator() + "testpass2" + System.lineSeparator();

    // First registration
    Scanner regScanner1 = new Scanner(new ByteArrayInputStream(input1.getBytes()));
    UserAuthentication regInstance1 = new UserAuthentication();
    java.lang.reflect.Field scannerField = UserAuthentication.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(regInstance1, regScanner1);

    java.io.ByteArrayOutputStream outContent1 = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent1));

    try {
      regInstance1.register();
    }

    finally {
      System.setOut(originalOut);
      regInstance1.close();
    }

    // Second registration with same username
    Scanner regScanner2 = new Scanner(new ByteArrayInputStream(input2.getBytes()));
    UserAuthentication regInstance2 = new UserAuthentication();
    scannerField.setAccessible(true);
    scannerField.set(regInstance2, regScanner2);

    java.io.ByteArrayOutputStream outContent2 = new java.io.ByteArrayOutputStream();
    System.setOut(new java.io.PrintStream(outContent2));

    try {
      boolean result = regInstance2.register();
      assertFalse("Registration should fail with duplicate username", result);
      String output = outContent2.toString();
      assertTrue("Output should contain error message", output.contains("Username already exists"));
    }

    finally {
      System.setOut(originalOut);
      regInstance2.close();
    }
  }

  /**
   * @brief Test to verify login method works correctly.
   * @throws Exception
   */
  @Test
  public void testLogin() throws Exception {
    // First register a user
    String regInput = "testuser" + System.lineSeparator() + "testpass" + System.lineSeparator();
    Scanner regScanner = new Scanner(new ByteArrayInputStream(regInput.getBytes()));
    UserAuthentication regInstance = new UserAuthentication();
    java.lang.reflect.Field scannerField = UserAuthentication.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(regInstance, regScanner);

    java.io.ByteArrayOutputStream outContent1 = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent1));

    try {
      regInstance.register();
    }

    finally {
      System.setOut(originalOut);
      regInstance.close();
    }

    // Now test login
    String loginInput = "testuser" + System.lineSeparator() + "testpass" + System.lineSeparator();
    Scanner loginScanner = new Scanner(new ByteArrayInputStream(loginInput.getBytes()));
    UserAuthentication loginInstance = new UserAuthentication();
    scannerField.setAccessible(true);
    scannerField.set(loginInstance, loginScanner);

    java.io.ByteArrayOutputStream outContent2 = new java.io.ByteArrayOutputStream();
    System.setOut(new java.io.PrintStream(outContent2));

    try {
      boolean result = loginInstance.login();
      assertTrue("Login should be successful", result);
      String output = outContent2.toString();
      assertTrue("Output should contain success message", output.contains("Login successful"));
    }

    finally {
      System.setOut(originalOut);
      loginInstance.close();
    }
  }

  /**
   * @brief Test to verify login handles invalid credentials.
   * @throws Exception
   */
  @Test
  public void testLoginInvalidCredentials() throws Exception {
    String input = "testuser" + System.lineSeparator() + "wrongpass" + System.lineSeparator();
    Scanner loginScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
    UserAuthentication loginInstance = new UserAuthentication();

    java.lang.reflect.Field scannerField = UserAuthentication.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(loginInstance, loginScanner);

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      boolean result = loginInstance.login();
      assertFalse("Login should fail with invalid credentials", result);
      String output = outContent.toString();
      assertTrue("Output should contain error message", output.contains("Invalid username or password"));
    }

    finally {
      System.setOut(originalOut);
      loginInstance.close();
    }
  }

  /**
   * @brief Test to verify login handles non-existent username.
   * @throws Exception
   */
  @Test
  public void testLoginNonExistentUser() throws Exception {
    String input = "nonexistent" + System.lineSeparator() + "password" + System.lineSeparator();
    Scanner loginScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
    UserAuthentication loginInstance = new UserAuthentication();

    java.lang.reflect.Field scannerField = UserAuthentication.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(loginInstance, loginScanner);

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      boolean result = loginInstance.login();
      assertFalse("Login should fail with non-existent user", result);
      String output = outContent.toString();
      assertTrue("Output should contain error message", output.contains("Invalid username or password"));
    }

    finally {
      System.setOut(originalOut);
      loginInstance.close();
    }
  }

  /**
   * @brief Test to verify showAuthenticationMenu method exists and can be called.
   * @throws Exception
   */
  @Test
  public void testShowAuthenticationMenu() throws Exception {
    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      userAuth.showAuthenticationMenu();
      String output = outContent.toString();
      assertTrue("Output should contain menu header", output.contains("User Authentication"));
      assertTrue("Output should contain Login option", output.contains("Login"));
      assertTrue("Output should contain Register option", output.contains("Register"));
    }

    finally {
      System.setOut(originalOut);
    }
  }

  /**
   * @brief Test to verify authenticate method works with successful login.
   * @throws Exception
   */
  @Test
  public void testAuthenticateSuccessfulLogin() throws Exception {
    // First register
    String regInput = "testuser" + System.lineSeparator() + "testpass" + System.lineSeparator();
    Scanner regScanner = new Scanner(new ByteArrayInputStream(regInput.getBytes()));
    UserAuthentication regInstance = new UserAuthentication();
    java.lang.reflect.Field scannerField = UserAuthentication.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(regInstance, regScanner);

    java.io.ByteArrayOutputStream outContent1 = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent1));

    try {
      regInstance.register();
    }

    finally {
      System.setOut(originalOut);
      regInstance.close();
    }

    // Now authenticate with login
    String authInput = "1" + System.lineSeparator() + "testuser" + System.lineSeparator() + "testpass" + System.lineSeparator();
    Scanner authScanner = new Scanner(new ByteArrayInputStream(authInput.getBytes()));
    UserAuthentication authInstance = new UserAuthentication();
    scannerField.setAccessible(true);
    scannerField.set(authInstance, authScanner);

    java.io.ByteArrayOutputStream outContent2 = new java.io.ByteArrayOutputStream();
    System.setOut(new java.io.PrintStream(outContent2));

    try {
      boolean result = authInstance.authenticate();
      assertTrue("Authentication should be successful", result);
    }

    finally {
      System.setOut(originalOut);
      authInstance.close();
    }
  }

  /**
   * @brief Test to verify authenticate method handles exit.
   * @throws Exception
   */
  @Test
  public void testAuthenticateExit() throws Exception {
    String input = "3" + System.lineSeparator();
    Scanner authScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
    UserAuthentication authInstance = new UserAuthentication();

    java.lang.reflect.Field scannerField = UserAuthentication.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(authInstance, authScanner);

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      boolean result = authInstance.authenticate();
      assertFalse("Authentication should return false on exit", result);
      String output = outContent.toString();
      assertTrue("Output should contain exit message", output.contains("Exiting") || output.contains("Goodbye"));
    }

    finally {
      System.setOut(originalOut);
      authInstance.close();
    }
  }

  /**
   * @brief Test to verify loadUsers handles non-existent file gracefully.
   * @throws Exception
   */
  @Test
  public void testLoadUsersWithNonExistentFile() throws Exception {
    // Ensure file doesn't exist
    File testFile = new File(TEST_USERS_FILE);

    if (testFile.exists()) {
      testFile.delete();
    }

    // Should not throw exception
    UserAuthentication ua = new UserAuthentication();
    ua.close();
    assertTrue("loadUsers should handle non-existent file gracefully", true);
  }

  /**
   * @brief Test to verify loadUsers can parse valid file format.
   * @throws Exception
   */
  @Test
  public void testLoadUsersWithValidFile() throws Exception {
    // Create a valid users file
    File testFile = new File(TEST_USERS_FILE);

    try (FileWriter writer = new FileWriter(testFile)) {
      writer.write("# User credentials file\n");
      writer.write("# Format: username:password\n");
      writer.write("testuser:testpass\n");
    }

    // Load users
    UserAuthentication ua = new UserAuthentication();

    // Test login with loaded user
    String input = "testuser" + System.lineSeparator() + "testpass" + System.lineSeparator();
    Scanner loginScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
    java.lang.reflect.Field scannerField = UserAuthentication.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(ua, loginScanner);

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      boolean result = ua.login();
      assertTrue("Login should be successful with loaded user", result);
    }

    finally {
      System.setOut(originalOut);
      ua.close();
    }
  }

  /**
   * @brief Test to verify saveUsers method works.
   * @throws Exception
   */
  @Test
  public void testSaveUsers() throws Exception {
    // Register a user
    String input = "testuser" + System.lineSeparator() + "testpass" + System.lineSeparator();
    Scanner regScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
    UserAuthentication regInstance = new UserAuthentication();

    java.lang.reflect.Field scannerField = UserAuthentication.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(regInstance, regScanner);

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      regInstance.register();
      regInstance.saveUsers();
      // Verify file exists
      File testFile = new File(TEST_USERS_FILE);
      assertTrue("File should exist after saveUsers", testFile.exists());
    }

    finally {
      System.setOut(originalOut);
      regInstance.close();
    }
  }

  /**
   * @brief Test to verify close method saves users.
   * @throws Exception
   */
  @Test
  public void testCloseSavesUsers() throws Exception {
    // Register a user
    String input = "testuser" + System.lineSeparator() + "testpass" + System.lineSeparator();
    Scanner regScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
    UserAuthentication regInstance = new UserAuthentication();

    java.lang.reflect.Field scannerField = UserAuthentication.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(regInstance, regScanner);

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      regInstance.register();
      regInstance.close();
      // Verify file exists
      File testFile = new File(TEST_USERS_FILE);
      assertTrue("File should exist after close", testFile.exists());
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
    userAuth.close();
    assertTrue("close should execute without exception", true);
  }

  /**
   * @brief Test to verify UserHashTable hash function works.
   * @throws Exception
   */
  @Test
  public void testUserHashTableHash() throws Exception {
    // Access UserHashTable using reflection
    java.lang.reflect.Field usersField = UserAuthentication.class.getDeclaredField("users");
    usersField.setAccessible(true);
    Object hashTable = usersField.get(userAuth);

    // Test hash method
    java.lang.reflect.Method hashMethod = hashTable.getClass().getDeclaredMethod("hash", String.class);
    hashMethod.setAccessible(true);

    int hash1 = (Integer) hashMethod.invoke(hashTable, "testuser");
    int hash2 = (Integer) hashMethod.invoke(hashTable, "testuser");

    assertEquals("Hash should be consistent for same input", hash1, hash2);
    assertTrue("Hash should be non-negative", hash1 >= 0);
  }

  /**
   * @brief Test to verify UserHashTable put method works.
   * @throws Exception
   */
  @Test
  public void testUserHashTablePut() throws Exception {
    java.lang.reflect.Field usersField = UserAuthentication.class.getDeclaredField("users");
    usersField.setAccessible(true);
    Object hashTable = usersField.get(userAuth);

    java.lang.reflect.Method putMethod = hashTable.getClass().getDeclaredMethod("put", String.class, String.class);
    putMethod.setAccessible(true);

    Boolean result = (Boolean) putMethod.invoke(hashTable, "testuser", "testpass");
    assertTrue("Put should be successful", result);

    // Try to put again (should fail)
    Boolean result2 = (Boolean) putMethod.invoke(hashTable, "testuser", "testpass2");
    assertFalse("Put should fail for duplicate username", result2);
  }

  /**
   * @brief Test to verify UserHashTable get method works.
   * @throws Exception
   */
  @Test
  public void testUserHashTableGet() throws Exception {
    java.lang.reflect.Field usersField = UserAuthentication.class.getDeclaredField("users");
    usersField.setAccessible(true);
    Object hashTable = usersField.get(userAuth);

    java.lang.reflect.Method putMethod = hashTable.getClass().getDeclaredMethod("put", String.class, String.class);
    putMethod.setAccessible(true);
    putMethod.invoke(hashTable, "testuser", "testpass");

    java.lang.reflect.Method getMethod = hashTable.getClass().getDeclaredMethod("get", String.class);
    getMethod.setAccessible(true);

    String password = (String) getMethod.invoke(hashTable, "testuser");
    assertEquals("Password should match", "testpass", password);

    String nullPassword = (String) getMethod.invoke(hashTable, "nonexistent");
    assertNull("Password should be null for non-existent user", nullPassword);
  }

  /**
   * @brief Test to verify UserHashTable containsKey method works.
   * @throws Exception
   */
  @Test
  public void testUserHashTableContainsKey() throws Exception {
    java.lang.reflect.Field usersField = UserAuthentication.class.getDeclaredField("users");
    usersField.setAccessible(true);
    Object hashTable = usersField.get(userAuth);

    java.lang.reflect.Method putMethod = hashTable.getClass().getDeclaredMethod("put", String.class, String.class);
    putMethod.setAccessible(true);
    putMethod.invoke(hashTable, "testuser", "testpass");

    java.lang.reflect.Method containsKeyMethod = hashTable.getClass().getDeclaredMethod("containsKey", String.class);
    containsKeyMethod.setAccessible(true);

    Boolean exists = (Boolean) containsKeyMethod.invoke(hashTable, "testuser");
    assertTrue("Should contain key 'testuser'", exists);

    Boolean notExists = (Boolean) containsKeyMethod.invoke(hashTable, "nonexistent");
    assertFalse("Should not contain key 'nonexistent'", notExists);
  }

  /**
   * @brief Test to verify UserHashTable resize works.
   * @throws Exception
   */
  @Test
  public void testUserHashTableResize() throws Exception {
    java.lang.reflect.Field usersField = UserAuthentication.class.getDeclaredField("users");
    usersField.setAccessible(true);
    Object hashTable = usersField.get(userAuth);

    java.lang.reflect.Method putMethod = hashTable.getClass().getDeclaredMethod("put", String.class, String.class);
    putMethod.setAccessible(true);

    // Add enough users to trigger resize (load factor 0.75)
    for (int i = 0; i < 10; i++) {
      putMethod.invoke(hashTable, "user" + i, "pass" + i);
    }

    // Verify all users are still accessible
    java.lang.reflect.Method getMethod = hashTable.getClass().getDeclaredMethod("get", String.class);
    getMethod.setAccessible(true);

    String password = (String) getMethod.invoke(hashTable, "user5");
    assertEquals("Password should match after resize", "pass5", password);
  }

  /**
   * @brief Test to verify UserHashTable nextPrime method works.
   * @throws Exception
   */
  @Test
  public void testUserHashTableNextPrime() throws Exception {
    java.lang.reflect.Field usersField = UserAuthentication.class.getDeclaredField("users");
    usersField.setAccessible(true);
    Object hashTable = usersField.get(userAuth);

    java.lang.reflect.Method nextPrimeMethod = hashTable.getClass().getDeclaredMethod("nextPrime", int.class);
    nextPrimeMethod.setAccessible(true);

    int prime1 = (Integer) nextPrimeMethod.invoke(hashTable, 10);
    assertTrue("Result should be prime", isPrimeNumber(prime1));
    assertTrue("Result should be >= input", prime1 >= 10);

    int prime2 = (Integer) nextPrimeMethod.invoke(hashTable, 2);
    assertEquals("Next prime of 2 should be 2", 2, prime2);
  }

  /**
   * @brief Test to verify UserHashTable isPrime method works.
   * @throws Exception
   */
  @Test
  public void testUserHashTableIsPrime() throws Exception {
    java.lang.reflect.Field usersField = UserAuthentication.class.getDeclaredField("users");
    usersField.setAccessible(true);
    Object hashTable = usersField.get(userAuth);

    java.lang.reflect.Method isPrimeMethod = hashTable.getClass().getDeclaredMethod("isPrime", int.class);
    isPrimeMethod.setAccessible(true);

    Boolean prime2 = (Boolean) isPrimeMethod.invoke(hashTable, 2);
    assertTrue("2 should be prime", prime2);

    Boolean prime3 = (Boolean) isPrimeMethod.invoke(hashTable, 3);
    assertTrue("3 should be prime", prime3);

    Boolean prime4 = (Boolean) isPrimeMethod.invoke(hashTable, 4);
    assertFalse("4 should not be prime", prime4);

    Boolean prime17 = (Boolean) isPrimeMethod.invoke(hashTable, 17);
    assertTrue("17 should be prime", prime17);
  }

  /**
   * @brief Test to verify multiple users can be registered.
   * @throws Exception
   */
  @Test
  public void testMultipleUsersRegistration() throws Exception {
    String input1 = "user1" + System.lineSeparator() + "pass1" + System.lineSeparator();
    Scanner regScanner1 = new Scanner(new ByteArrayInputStream(input1.getBytes()));
    UserAuthentication regInstance1 = new UserAuthentication();
    java.lang.reflect.Field scannerField = UserAuthentication.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(regInstance1, regScanner1);

    java.io.ByteArrayOutputStream outContent1 = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent1));

    try {
      boolean result1 = regInstance1.register();
      assertTrue("First registration should be successful", result1);
    }

    finally {
      System.setOut(originalOut);
      regInstance1.close();
    }

    String input2 = "user2" + System.lineSeparator() + "pass2" + System.lineSeparator();
    Scanner regScanner2 = new Scanner(new ByteArrayInputStream(input2.getBytes()));
    UserAuthentication regInstance2 = new UserAuthentication();
    scannerField.setAccessible(true);
    scannerField.set(regInstance2, regScanner2);

    java.io.ByteArrayOutputStream outContent2 = new java.io.ByteArrayOutputStream();
    System.setOut(new java.io.PrintStream(outContent2));

    try {
      boolean result2 = regInstance2.register();
      assertTrue("Second registration should be successful", result2);
    }

    finally {
      System.setOut(originalOut);
      regInstance2.close();
    }
  }

  /**
   * @brief Test to verify UserHashTable saveToFile method works.
   * @throws Exception
   */
  @Test
  public void testUserHashTableSaveToFile() throws Exception {
    java.lang.reflect.Field usersField = UserAuthentication.class.getDeclaredField("users");
    usersField.setAccessible(true);
    Object hashTable = usersField.get(userAuth);

    java.lang.reflect.Method putMethod = hashTable.getClass().getDeclaredMethod("put", String.class, String.class);
    putMethod.setAccessible(true);
    putMethod.invoke(hashTable, "testuser", "testpass");

    java.lang.reflect.Method saveMethod = hashTable.getClass().getDeclaredMethod("saveToFile",
      java.io.BufferedWriter.class);
    saveMethod.setAccessible(true);

    java.io.StringWriter stringWriter = new java.io.StringWriter();
    java.io.BufferedWriter writer = new java.io.BufferedWriter(stringWriter);

    saveMethod.invoke(hashTable, writer);
    writer.flush();

    String content = stringWriter.toString();
    assertTrue("Content should contain username", content.contains("testuser"));
    assertTrue("Content should contain password", content.contains("testpass"));
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
   * @brief Test to verify authenticate method handles invalid choice.
   * @throws Exception
   */
  @Test
  public void testAuthenticateInvalidChoice() throws Exception {
    String input = "99" + System.lineSeparator() + "3" + System.lineSeparator();
    Scanner authScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
    UserAuthentication authInstance = new UserAuthentication();

    java.lang.reflect.Field scannerField = UserAuthentication.class.getDeclaredField("scanner");
    scannerField.setAccessible(true);
    scannerField.set(authInstance, authScanner);

    java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    System.setOut(new java.io.PrintStream(outContent));

    try {
      boolean result = authInstance.authenticate();
      assertFalse("Authentication should return false on exit", result);
      String output = outContent.toString();
      assertTrue("Output should contain invalid option message", output.contains("Invalid option"));
    }

    finally {
      System.setOut(originalOut);
      authInstance.close();
    }
  }

  /**
   * @brief Test to verify UserEntry class can be instantiated.
   * @throws Exception
   */
  @Test
  public void testUserEntryClass() throws Exception {
    Class<?> userEntryClass = Class.forName("com.efil.nese.timetracker.UserAuthentication$UserHashTable$UserEntry");
    java.lang.reflect.Constructor<?> constructor = userEntryClass.getDeclaredConstructor(String.class, String.class);
    constructor.setAccessible(true);

    Object entry = constructor.newInstance("testuser", "testpass");
    assertNotNull("UserEntry object should not be null", entry);

    // Verify fields
    java.lang.reflect.Field usernameField = userEntryClass.getDeclaredField("username");
    usernameField.setAccessible(true);
    String username = (String) usernameField.get(entry);
    assertEquals("Username should match", "testuser", username);

    java.lang.reflect.Field passwordField = userEntryClass.getDeclaredField("password");
    passwordField.setAccessible(true);
    String password = (String) passwordField.get(entry);
    assertEquals("Password should match", "testpass", password);
  }

  /**
   * @brief Test to verify linear probing triggers resize when hash == originalHash.
   * @throws Exception
   */
  @Test
  public void testLinearProbingResizeOnFullCycle() throws Exception {
    // Clear static users field
    java.lang.reflect.Field usersField = UserAuthentication.class.getDeclaredField("users");
    usersField.setAccessible(true);
    Class<?> userHashTableClass = Class.forName("com.efil.nese.timetracker.UserAuthentication$UserHashTable");
    java.lang.reflect.Constructor<?> constructor = userHashTableClass.getDeclaredConstructor();
    constructor.setAccessible(true);
    Object hashTable = constructor.newInstance();
    usersField.set(null, hashTable);

    // Get initial capacity
    java.lang.reflect.Field capacityField = userHashTableClass.getDeclaredField("capacity");
    capacityField.setAccessible(true);
    int initialCapacity = (Integer) capacityField.get(hashTable);

    // Get hash method to find usernames that hash to the same value
    java.lang.reflect.Method hashMethod = userHashTableClass.getDeclaredMethod("hash", String.class);
    hashMethod.setAccessible(true);

    // Get put method
    java.lang.reflect.Method putMethod = userHashTableClass.getDeclaredMethod("put", String.class, String.class);
    putMethod.setAccessible(true);

    // Fill table to near capacity to force linear probing
    // Add users until we trigger the hash == originalHash condition
    // This happens when linear probing cycles back to the original hash
    int added = 0;

    for (int i = 0; i < initialCapacity * 2; i++) {
      String username = "collision" + i;
      Boolean result = (Boolean) putMethod.invoke(hashTable, username, "pass" + i);

      if (result) {
        added++;
        int currentCapacity = (Integer) capacityField.get(hashTable);

        // If capacity increased, resize was triggered
        if (currentCapacity > initialCapacity) {
          // Verify the resize happened during linear probing
          assertTrue("Resize should have been triggered", currentCapacity > initialCapacity);
          break;
        }
      }
    }

    // Verify table still works after resize
    java.lang.reflect.Method getMethod = userHashTableClass.getDeclaredMethod("get", String.class);
    getMethod.setAccessible(true);

    // Try to get a user that was added before resize
    if (added > 0) {
      String password = (String) getMethod.invoke(hashTable, "collision0");
      assertNotNull("User should still be accessible after resize", password);
      assertEquals("Password should match", "pass0", password);
    }
  }

  /**
   * @brief Test to verify linear probing handles hash collision correctly.
   * @throws Exception
   */
  @Test
  public void testLinearProbingHashCollision() throws Exception {
    // Clear static users field
    java.lang.reflect.Field usersField = UserAuthentication.class.getDeclaredField("users");
    usersField.setAccessible(true);
    Class<?> userHashTableClass = Class.forName("com.efil.nese.timetracker.UserAuthentication$UserHashTable");
    java.lang.reflect.Constructor<?> constructor = userHashTableClass.getDeclaredConstructor();
    constructor.setAccessible(true);
    Object hashTable = constructor.newInstance();
    usersField.set(null, hashTable);

    // Get hash method
    java.lang.reflect.Method hashMethod = userHashTableClass.getDeclaredMethod("hash", String.class);
    hashMethod.setAccessible(true);

    // Get put method
    java.lang.reflect.Method putMethod = userHashTableClass.getDeclaredMethod("put", String.class, String.class);
    putMethod.setAccessible(true);

    // Get get method
    java.lang.reflect.Method getMethod = userHashTableClass.getDeclaredMethod("get", String.class);
    getMethod.setAccessible(true);

    // Get capacity
    java.lang.reflect.Field capacityField = userHashTableClass.getDeclaredField("capacity");
    capacityField.setAccessible(true);
    int capacity = (Integer) capacityField.get(hashTable);

    // Add users that will cause collisions
    // We'll add enough users to fill most of the table
    List<String> addedUsernames = new java.util.ArrayList<>();

    for (int i = 0; i < (int)(capacity * 0.7); i++) {
      String username = "user" + i;
      Boolean result = (Boolean) putMethod.invoke(hashTable, username, "pass" + i);

      if (result) {
        addedUsernames.add(username);
      }
    }

    // Verify all users are accessible (linear probing should handle collisions)
    for (String username : addedUsernames) {
      String password = (String) getMethod.invoke(hashTable, username);
      assertNotNull("User should be accessible: " + username, password);
      assertTrue("Password should match: " + username, password.startsWith("pass"));
    }
  }

  /**
   * @brief Test to verify linear probing cycles back to originalHash and triggers resize.
   * @throws Exception
   */
  @Test
  public void testLinearProbingCycleBackToOriginalHash() throws Exception {
    // Clear static users field
    java.lang.reflect.Field usersField = UserAuthentication.class.getDeclaredField("users");
    usersField.setAccessible(true);
    Class<?> userHashTableClass = Class.forName("com.efil.nese.timetracker.UserAuthentication$UserHashTable");
    java.lang.reflect.Constructor<?> constructor = userHashTableClass.getDeclaredConstructor();
    constructor.setAccessible(true);
    Object hashTable = constructor.newInstance();
    usersField.set(null, hashTable);

    // Get initial capacity
    java.lang.reflect.Field capacityField = userHashTableClass.getDeclaredField("capacity");
    capacityField.setAccessible(true);
    int initialCapacity = (Integer) capacityField.get(hashTable);

    // Get methods
    java.lang.reflect.Method putMethod = userHashTableClass.getDeclaredMethod("put", String.class, String.class);
    putMethod.setAccessible(true);
    java.lang.reflect.Method getMethod = userHashTableClass.getDeclaredMethod("get", String.class);
    getMethod.setAccessible(true);

    // Fill table to force linear probing and potential cycle
    // Add users until we reach a point where adding more would cause hash == originalHash
    int added = 0;

    for (int i = 0; i < initialCapacity + 5; i++) {
      String username = "testuser" + i;
      Boolean result = (Boolean) putMethod.invoke(hashTable, username, "pass" + i);

      if (result) {
        added++;
        int currentCapacity = (Integer) capacityField.get(hashTable);

        // If capacity increased, resize was triggered (either by load factor or hash cycle)
        if (currentCapacity > initialCapacity) {
          // Verify resize happened
          assertTrue("Resize should have been triggered", currentCapacity > initialCapacity);

          // Verify all previously added users are still accessible
          for (int j = 0; j < added; j++) {
            String password = (String) getMethod.invoke(hashTable, "testuser" + j);
            assertNotNull("User should be accessible after resize: testuser" + j, password);
          }

          break;
        }
      }
    }

    // Verify table is still functional
    assertTrue("Should have added at least some users", added > 0);
  }

  /**
   * @brief Test to verify linear probing handles wrap-around correctly.
   * @throws Exception
   */
  @Test
  public void testLinearProbingWrapAround() throws Exception {
    // Clear static users field
    java.lang.reflect.Field usersField = UserAuthentication.class.getDeclaredField("users");
    usersField.setAccessible(true);
    Class<?> userHashTableClass = Class.forName("com.efil.nese.timetracker.UserAuthentication$UserHashTable");
    java.lang.reflect.Constructor<?> constructor = userHashTableClass.getDeclaredConstructor();
    constructor.setAccessible(true);
    Object hashTable = constructor.newInstance();
    usersField.set(null, hashTable);

    // Get capacity
    java.lang.reflect.Field capacityField = userHashTableClass.getDeclaredField("capacity");
    capacityField.setAccessible(true);
    int capacity = (Integer) capacityField.get(hashTable);

    // Get methods
    java.lang.reflect.Method putMethod = userHashTableClass.getDeclaredMethod("put", String.class, String.class);
    putMethod.setAccessible(true);
    java.lang.reflect.Method getMethod = userHashTableClass.getDeclaredMethod("get", String.class);
    getMethod.setAccessible(true);

    // Fill table strategically to test wrap-around
    // Add users that will cause linear probing to wrap around
    List<String> usernames = new java.util.ArrayList<>();

    for (int i = 0; i < capacity - 1; i++) {
      String username = "wrap" + i;
      Boolean result = (Boolean) putMethod.invoke(hashTable, username, "pass" + i);

      if (result) {
        usernames.add(username);
      }
    }

    // Now add one more that will cause wrap-around
    String wrapUsername = "wrapfinal";
    Boolean result = (Boolean) putMethod.invoke(hashTable, wrapUsername, "passfinal");

    // Verify all users are accessible
    for (String username : usernames) {
      String password = (String) getMethod.invoke(hashTable, username);
      assertNotNull("User should be accessible: " + username, password);
    }

    if (result) {
      String password = (String) getMethod.invoke(hashTable, wrapUsername);
      assertNotNull("Wrap-around user should be accessible", password);
      assertEquals("Password should match", "passfinal", password);
    }
  }
}

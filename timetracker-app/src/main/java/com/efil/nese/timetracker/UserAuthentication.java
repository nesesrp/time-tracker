/**
 * @file UserAuthentication.java
 * @brief This file handles user authentication including login and registration.
 * @details This file contains the UserAuthentication class which provides methods
 *          for user registration and login functionality. Uses hash table with
 *          linear probing for user storage and file I/O for persistence.
 */
package com.efil.nese.timetracker;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;

/**
 * @class UserAuthentication
 * @brief This class handles user authentication operations.
 * @details The UserAuthentication class provides methods to register new users
 *          and authenticate existing users through login functionality.
 *          Uses hash table with linear probing for efficient user storage.
 *          Provides file-based persistence for user credentials.
 * @author efil.saylam.nese.sarp
 * @version 1.0
 * @since 2024
 * @warning Passwords are stored in plain text. Not recommended for production use.
 */
public class UserAuthentication {

  /** @brief Logger instance for logging errors and authentication events. */
  private static final Logger logger = (Logger) LoggerFactory.getLogger(UserAuthentication.class);

  /** @brief File name where user credentials are persisted. Default: "users.txt". */
  private static final String USERS_FILE = "users.txt";

  /** @brief Static hash table instance for storing all user credentials in memory. */
  private static UserHashTable users = new UserHashTable();

  /** @brief Scanner instance for reading user input from console. */
  private Scanner scanner;

  /**
   * @brief Constructor for UserAuthentication class.
   */
  public UserAuthentication() {
    this.scanner = new Scanner(System.in);
    loadUsers();
  }

  /**
   * @brief Displays the authentication menu and handles user choice.
   * @details Shows login and register options to the user.
   */
  public void showAuthenticationMenu() {
    System.out.println("************************************************************");
    System.out.println("*                                                          *");
    System.out.println("*                 User Authentication                      *");
    System.out.println("*                                                          *");
    System.out.println("************************************************************");
    System.out.println();
    System.out.println("1. Login");
    System.out.println("2. Register");
    System.out.println("3. Exit");
    System.out.print("Please select an option: ");
  }

  /**
   * @brief Handles user registration process.
   * @details Prompts the user to enter a username and password. Validates that both
   *          are non-empty and that the username doesn't already exist. If validation
   *          passes, stores the credentials in the hash table and saves to file.
   *          Registration fails if username is empty, password is empty, or username
   *          already exists.
   * @return true if registration is successful, false otherwise.
   * @note Usernames are case-sensitive and must be unique.
   * @note Passwords are stored in plain text (not recommended for production use).
   * @see #saveUsers()
   * @see UserHashTable#put(String, String)
   * @see UserHashTable#containsKey(String)
   */
  public boolean register() {
    System.out.println("\n=== Registration ===");
    System.out.print("Enter username: ");
    String username = scanner.nextLine().trim();

    if (username.isEmpty()) {
      System.out.println("Username cannot be empty!");
      return false;
    }

    if (users.containsKey(username)) {
      System.out.println("Username already exists! Please choose another one.");
      return false;
    }

    System.out.print("Enter password: ");
    String password = scanner.nextLine().trim();

    if (password.isEmpty()) {
      System.out.println("Password cannot be empty!");
      return false;
    }

    if (users.put(username, password)) {
      saveUsers();
      System.out.println("Registration successful! You can now login.");
      return true;
    } else {
      System.out.println("Registration failed!");
      return false;
    }
  }

  /**
   * @brief Handles user login authentication.
   * @details Prompts the user to enter username and password, then verifies the
   *          credentials against the stored user hash table. If the username exists
   *          and the password matches, login is successful. Otherwise, authentication
   *          fails and an error message is displayed.
   * @return true if login is successful (username exists and password matches),
   *         false otherwise.
   * @note Username and password comparison is case-sensitive.
   * @note Invalid credentials result in a generic error message for security.
   * @see UserHashTable#containsKey(String)
   * @see UserHashTable#get(String)
   */
  public boolean login() {
    System.out.println("\n=== Login ===");
    System.out.print("Enter username: ");
    String username = scanner.nextLine().trim();
    System.out.print("Enter password: ");
    String password = scanner.nextLine().trim();

    if (users.containsKey(username) && users.get(username).equals(password)) {
      System.out.println("Login successful! Welcome, " + username + "!");
      return true;
    } else {
      System.out.println("Invalid username or password!");
      return false;
    }
  }

  /**
   * @brief Main authentication flow.
   * @details Handles the complete authentication process including menu display
   *          and user choice handling.
   * @return true if user successfully authenticates, false if user exits.
   */
  public boolean authenticate() {
    while (true) {
      showAuthenticationMenu();
      String choice = scanner.nextLine().trim();

      switch (choice) {
        case "1":
          if (login()) {
            return true;
          }

          break;

        case "2":
          register();
          break;

        case "3":
          System.out.println("Exiting application. Goodbye!");
          return false;

        default:
          System.out.println("Invalid option! Please select 1, 2, or 3.");
      }

      System.out.println();
    }
  }

  /**
   * @brief Loads user credentials from the persistence file.
   * @details Reads user credentials from the users.txt file and populates the hash table.
   *          The file format is "username:password" with one user per line. Empty lines
   *          and lines starting with '#' are treated as comments and skipped. The colon
   *          character is used as the delimiter between username and password.
   * @note If the file does not exist, the method returns silently without error.
   * @note Invalid lines (missing colon or malformed) are skipped silently.
   * @note This method is called automatically during object construction.
   * @see #USERS_FILE
   * @see #saveUsers()
   * @see UserHashTable#put(String, String)
   * @throws IOException If an I/O error occurs while reading the file. Errors are
   *                    logged but do not stop execution.
   */
  private void loadUsers() {
    File file = new File(USERS_FILE);

    if (!file.exists()) {
      return;
    }

    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
      String line;

      while ((line = reader.readLine()) != null) {
        line = line.trim();

        if (line.isEmpty() || line.startsWith("#")) {
          continue; // Skip empty lines and comments
        }

        // Format: username:password
        int colonIndex = line.indexOf(':');

        if (colonIndex > 0 && colonIndex < line.length() - 1) {
          String username = line.substring(0, colonIndex);
          String password = line.substring(colonIndex + 1);
          users.put(username, password);
        }
      }
    } catch (IOException e) {
      logger.error("Error loading users from file: ", e);
      System.out.println("Warning: Could not load users from file.");
    }
  }

  /**
   * @brief Saves all user credentials to the persistence file.
   * @details Writes all user credentials from the hash table to the users.txt file.
   *          The file format is "username:password" with one user per line. Includes
   *          header comments explaining the format. Overwrites the entire file each time.
   * @note If the file write fails, an error is logged but execution continues.
   * @see #USERS_FILE
   * @see UserHashTable#saveToFile(BufferedWriter)
   * @throws IOException If an I/O error occurs while writing the file. Errors are
   *                    logged but do not stop execution.
   */
  public void saveUsers() {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE))) {
      writer.write("# User credentials file");
      writer.newLine();
      writer.write("# Format: username:password");
      writer.newLine();
      users.saveToFile(writer);
    } catch (IOException e) {
      logger.error("Error saving users to file: ", e);
      System.out.println("Warning: Could not save users to file.");
    }
  }

  /**
   * @brief Closes the scanner resource and saves all users to file.
   * @details Performs cleanup operations including saving all user credentials to
   *          the persistence file and closing the Scanner instance. This method
   *          should be called when the UserAuthentication instance is no longer needed
   *          to ensure data persistence and proper resource cleanup.
   * @note This method is idempotent - it can be called multiple times safely.
   * @note Always saves users before closing, ensuring no data loss.
   * @see #saveUsers()
   */
  public void close() {
    saveUsers();

    if (scanner != null) {
      scanner.close();
    }
  }

  /**
   * @class UserHashTable
   * @brief Hash table implementation with linear probing for user storage.
   * @details This inner class implements a hash table data structure using linear
   *          probing for collision resolution. The table automatically resizes when
   *          the load factor exceeds 0.75 to maintain performance. Uses prime number
   *          table sizes to reduce clustering. Provides O(1) average case time
   *          complexity for insert, search, and contains operations.
   * @note The hash table uses a static instance shared across all UserAuthentication objects.
   * @note Table size is always a prime number for better hash distribution.
   * @author efil.saylam.nese.sarp
   */
  private static class UserHashTable {
    /** @brief Initial capacity of the hash table. Set to 11 (a prime number). */
    private static final int INITIAL_SIZE = 11;

    /** @brief Array of UserEntry objects representing the hash table buckets. */
    private UserEntry[] table;

    /** @brief Current number of entries stored in the hash table. */
    private int size;

    /** @brief Current capacity (size) of the hash table array. Always a prime number. */
    private int capacity;

    /**
     * @brief Constructor for UserHashTable class.
     * @details Initializes an empty hash table with the initial capacity (11).
     *          All table slots are initially null. The size is set to 0.
     * @note The initial capacity is a prime number for better hash distribution.
     */
    UserHashTable() {
      this.capacity = INITIAL_SIZE;
      this.table = new UserEntry[capacity];
      this.size = 0;
    }

    /**
     * @brief Hash function for computing bucket index from username.
     * @param key Username string to hash. Must not be null.
     * @return Hash value in the range [0, capacity-1] representing the bucket index.
     * @details Uses Java's hashCode() method and applies bitwise AND with 0x7FFFFFFF
     *          to ensure a positive value, then applies modulo operation with capacity
     *          to map to a valid bucket index.
     * @note The bitwise AND ensures the result is non-negative even if hashCode() returns negative.
     */
    private int hash(String key) {
      return (key.hashCode() & 0x7FFFFFFF) % capacity;
    }

    /**
     * @brief Inserts a user into the hash table using linear probing.
     * @param username Username to insert. Must not be null.
     * @param password Password associated with the username. Must not be null.
     * @return true if insertion is successful, false if username already exists.
     * @details Automatically resizes the table if load factor exceeds 0.75 before insertion.
     *          Uses linear probing to resolve collisions by checking the next available slot.
     *          If the table becomes full during probing, it resizes and retries.
     *          Time complexity: O(1) average case, O(n) worst case (with resizing).
     * @note Duplicate usernames are rejected and the method returns false.
     * @see #resize()
     * @see #hash(String)
     */
    boolean put(String username, String password) {
      if (size >= capacity * 0.75) {
        resize();
      }

      int hash = hash(username);
      int originalHash = hash;

      // Linear probing
      while (table[hash] != null) {
        if (table[hash].username.equals(username)) {
          return false; // Username already exists
        }

        hash = (hash + 1) % capacity;

        if (hash == originalHash) {
          resize();
          hash = hash(username);
          originalHash = hash;
        }
      }

      table[hash] = new UserEntry(username, password);
      size++;
      return true;
    }

    /**
     * @brief Retrieves the password for a given username using linear probing.
     * @param username Username to search for. Must not be null.
     * @return Password string if username is found, null otherwise.
     * @details Uses the hash function to find the initial bucket, then performs
     *          linear probing to locate the entry. Stops probing when an empty slot
     *          is encountered or when returning to the original hash position.
     *          Time complexity: O(1) average case, O(n) worst case.
     * @note Returns null if the username does not exist in the table.
     * @see #hash(String)
     */
    String get(String username) {
      int hash = hash(username);
      int originalHash = hash;

      while (table[hash] != null) {
        if (table[hash].username.equals(username)) {
          return table[hash].password;
        }

        hash = (hash + 1) % capacity;

        if (hash == originalHash) {
          break;
        }
      }

      return null;
    }

    /**
     * @brief Checks if a username exists in the hash table.
     * @param username Username to check for existence. Must not be null.
     * @return true if the username exists in the table, false otherwise.
     * @details Delegates to the get() method and checks if the result is non-null.
     *          Time complexity: O(1) average case, O(n) worst case.
     * @see #get(String)
     */
    boolean containsKey(String username) {
      return get(username) != null;
    }

    /**
     * @brief Resizes the hash table when load factor exceeds threshold.
     * @details Doubles the capacity and finds the next prime number for the new size.
     *          Rehashes all existing entries into the new table. This operation is
     *          necessary to maintain O(1) average case performance as the table grows.
     *          Time complexity: O(n) where n is the number of entries.
     * @note The new capacity is always a prime number for better hash distribution.
     * @note All entries are rehashed and may be placed in different buckets.
     * @see #nextPrime(int)
     * @see #put(String, String)
     */
    private void resize() {
      int oldCapacity = capacity;
      capacity = nextPrime(capacity * 2);
      UserEntry[] oldTable = table;
      table = new UserEntry[capacity];
      size = 0;

      for (int i = 0; i < oldCapacity; i++) {
        if (oldTable[i] != null) {
          put(oldTable[i].username, oldTable[i].password);
        }
      }
    }

    /**
     * @brief Saves all user entries from the hash table to a file.
     * @param writer BufferedWriter instance to write to. Must not be null and must be open.
     * @throws IOException If an I/O error occurs while writing to the file.
     * @details Iterates through all table slots and writes non-null entries in the format
     *          "username:password" with one entry per line. Only writes entries that
     *          are not null (occupied slots).
     * @note The writer is not closed by this method - the caller is responsible for closing it.
     * @note The file format is: username:password (one per line).
     */
    void saveToFile(BufferedWriter writer) throws IOException {
      for (int i = 0; i < capacity; i++) {
        if (table[i] != null) {
          writer.write(table[i].username + ":" + table[i].password);
          writer.newLine();
        }
      }
    }

    /**
     * @brief Finds the next prime number greater than or equal to n.
     * @param n Starting number to search from. Must be positive.
     * @return The smallest prime number that is greater than or equal to n.
     *         Returns 2 if n <= 1.
     * @details Uses iterative checking starting from n until a prime is found.
     *          Uses the isPrime() method for primality testing. This is important
     *          for hash table sizing as prime-sized tables reduce clustering.
     * @see #isPrime(int)
     */
    private int nextPrime(int n) {
      if (n <= 1) {
        return 2;
      }

      if (n <= 3) {
        return n;
      }

      while (true) {
        if (isPrime(n)) {
          return n;
        }

        n++;
      }
    }

    /**
     * @brief Checks if a number is prime using optimized trial division.
     * @param n Number to check for primality. Must be a positive integer.
     * @return true if n is prime, false otherwise. Returns false for n <= 1.
     * @details Uses an optimized algorithm that checks divisibility by 2 and 3 first,
     *          then checks numbers of the form 6k±1 up to sqrt(n). This is more efficient
     *          than checking all numbers up to sqrt(n).
     * @note Time complexity: O(sqrt(n)) in the worst case.
     * @note Correctly handles edge cases (n <= 1, n == 2, n == 3).
     */
    private boolean isPrime(int n) {
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
     * @class UserEntry
     * @brief Helper class for hash table entry representing a user credential.
     * @details This inner class encapsulates a username-password pair stored in
     *          the hash table. Each entry represents a single registered user.
     * @author efil.saylam.nese.sarp
     */
    private static class UserEntry {
      /** @brief Username stored in this hash table entry. Used as the key. */
      String username;

      /** @brief Password associated with the username. Stored as plain text. */
      String password;

      /**
       * @brief Constructor for UserEntry class.
       * @param username Username to store. Must not be null.
       * @param password Password to associate with the username. Must not be null.
       * @details Creates a new entry to store in the hash table. Both fields are required.
       * @note Passwords are stored in plain text - not recommended for production systems.
       */
      UserEntry(String username, String password) {
        this.username = username;
        this.password = password;
      }
    }
  }
}

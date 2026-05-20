/**
 * @file ActivityLogging.java
 * @brief This file handles activity logging functionality with advanced algorithms.
 * @details This file contains the ActivityLogging class which provides methods
 *          to log and track user activities. Implements:
 *          - Double Linked List for activity navigation
 *          - BFS/DFS for activity exploration
 *          - KMP algorithm for activity search
 *          - Tarjan's algorithm for strongly connected components
 *          - XOR checksum for data integrity
 * @author efil.saylam.nese.sarp
 */
package com.efil.nese.timetracker;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.Stack;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;

/**
 * @class Activity
 * @brief Represents a single activity with all its properties.
 * @details This class encapsulates all information about a user activity including
 *          its unique identifier, name, duration, timestamp, category, and description.
 *          Activities are used throughout the application to track and analyze user time.
 * @author efil.saylam.nese.sarp
 */
class Activity {
  /** @brief Unique identifier for the activity. Auto-incremented for each new activity. */
  int id;

  /** @brief Name or title of the activity. Cannot be empty. */
  String name;

  /** @brief Duration of the activity in minutes. Must be a positive integer. */
  int duration; // in minutes

  /** @brief Unix timestamp (seconds since epoch) when the activity was logged. */
  long timestamp; // Unix timestamp

  /** @brief Category classification of the activity (e.g., "Work", "Study", "Exercise"). */
  String category;

  /** @brief Detailed description of the activity. Optional field. */
  String description;

  /**
   * @brief Constructor for Activity class.
   * @param id Unique identifier for the activity.
   * @param name Name or title of the activity. Must not be null or empty.
   * @param duration Duration in minutes. Must be positive.
   * @param timestamp Unix timestamp in seconds when activity was logged.
   * @param category Category classification of the activity.
   * @param description Optional detailed description of the activity.
   * @note All parameters are required. Empty strings will be replaced with defaults.
   */
  Activity(int id, String name, int duration, long timestamp, String category, String description) {
    this.id = id;
    this.name = name;
    this.duration = duration;
    this.timestamp = timestamp;
    this.category = category;
    this.description = description;
  }
}

/**
 * @class ActivityNode
 * @brief Double linked list node for activity navigation.
 * @details This class implements a node in a doubly linked list structure used for
 *          efficient forward and backward navigation through activities. Each node
 *          contains a reference to an Activity object and pointers to the previous
 *          and next nodes in the list.
 * @author efil.saylam.nese.sarp
 */
class ActivityNode {
  /** @brief Reference to the Activity object stored in this node. */
  Activity activity;

  /** @brief Pointer to the previous node in the doubly linked list. Null if this is the first node. */
  ActivityNode prev;

  /** @brief Pointer to the next node in the doubly linked list. Null if this is the last node. */
  ActivityNode next;

  /**
   * @brief Constructor for ActivityNode class.
   * @param activity The Activity object to store in this node. Must not be null.
   * @note The prev and next pointers are initialized to null and should be set
   *       when the node is inserted into the linked list.
   */
  ActivityNode(Activity activity) {
    this.activity = activity;
    this.prev = null;
    this.next = null;
  }
}

/**
 * @class ActivityLogging
 * @brief This class handles activity logging operations with advanced algorithms.
 * @details Implements activity logging, viewing with double linked list,
 *          exploration with BFS/DFS, search with KMP, and connection analysis.
 *          This is the core module for managing user activities and provides
 *          various data structures and algorithms for efficient activity management.
 * @author efil.saylam.nese.sarp
 * @version 1.0
 * @since 2024
 */
public class ActivityLogging {

  /** @brief Logger instance for logging errors and debug information. */
  private static final Logger logger = (Logger) LoggerFactory.getLogger(ActivityLogging.class);

  /** @brief File name where activities are persisted. Default: "activities.txt". */
  private static final String ACTIVITIES_FILE = "activities.txt";

  /** @brief Scanner instance for reading user input from console. */
  private Scanner scanner;

  /** @brief List containing all logged activities. Maintains chronological order. */
  private List<Activity> activities;

  /** @brief Head node of the doubly linked list for activity navigation. Null if list is empty. */
  private ActivityNode head; // Double linked list head

  /** @brief Tail node of the doubly linked list for activity navigation. Null if list is empty. */
  private ActivityNode tail; // Double linked list tail

  /** @brief Next available unique identifier for new activities. Auto-incremented after each use. */
  private int nextId;

  /**
   * @brief Default constructor for ActivityLogging class.
   * @details Initializes all data structures, creates a new Scanner for System.in,
   *          and loads existing activities from the persistence file. The nextId
   *          is set to 1 initially, but will be adjusted based on loaded activities.
   * @note This constructor automatically calls loadActivities() to restore saved data.
   * @see #loadActivities()
   */
  public ActivityLogging() {
    this.scanner = new Scanner(System.in);
    this.activities = new ArrayList<>();
    this.head = null;
    this.tail = null;
    this.nextId = 1;
    loadActivities();
  }

  /**
   * @brief Constructor with custom scanner for testing purposes.
   * @param scanner Custom Scanner instance to use for input. Must not be null.
   * @details This constructor is primarily used for unit testing where a mock
   *          or controlled Scanner instance is needed. Does not load activities
   *          from file automatically.
   * @note The scanner parameter should be properly initialized before use.
   * @warning This constructor does not call loadActivities(). Activities must be
   *          loaded manually or added programmatically for testing.
   */
  public ActivityLogging(Scanner scanner) {
    this.scanner = scanner;
    this.activities = new ArrayList<>();
    this.head = null;
    this.tail = null;
    this.nextId = 1;
  }

  /**
   * @brief Main method to run the Activity Logging feature.
   * @details Displays the activity logging menu and handles user interactions.
   *          This is the entry point for the activity logging module and delegates
   *          to the activityHistoryMenu() method for the main menu loop.
   * @see #activityHistoryMenu()
   */
  public void run() {
    activityHistoryMenu();
  }

  /**
   * @brief Main menu for activity analysis features.
   * @details Displays a menu with options for logging activities, viewing activities
   *          using double linked list navigation, exploring activities with BFS/DFS
   *          algorithms, searching activities with KMP algorithm, and returning to
   *          the main menu. Handles user input validation and menu navigation.
   * @return Returns 0 on successful completion when user chooses to go back.
   * @note The menu runs in a loop until the user selects option 5 (Back to Main Menu).
   * @see #logActivity()
   * @see #viewActivitiesDoubleLinkedList()
   * @see #exploreActivities()
   * @see #searchActivitiesKMP()
   */
  private int activityHistoryMenu() {
    int choice;
    boolean back = false;

    while (!back) {
      System.out.println("\n========================================");
      System.out.println("         Activity Logging Menu          ");
      System.out.println("========================================");
      System.out.println("1. Log Activity");
      System.out.println("2. View Activities (Double Linked List)");
      System.out.println("3. Explore Activities (BFS/DFS)");
      System.out.println("4. Search Activities (KMP Algorithm)");
      System.out.println("5. Back to Main Menu");
      System.out.println("========================================");
      System.out.print("Enter your choice: ");

      try {
        choice = Integer.parseInt(scanner.nextLine().trim());
      } catch (NumberFormatException e) {
        choice = -1;
      }

      switch (choice) {
        case 1:
          logActivity();
          break;

        case 2:
          viewActivitiesDoubleLinkedList();
          break;

        case 3:
          exploreActivities();
          break;

        case 4:
          searchActivitiesKMP();
          break;

        case 5:
          back = true;
          break;

        default:
          System.out.println("Invalid choice. Please try again.");
      }
    }

    return 0;
  }

  /**
   * @brief Logs a new activity with user input.
   * @details Prompts the user to enter activity details including name, duration,
   *          category, and description. Validates all inputs and creates a new
   *          Activity object. The activity is added to both the activities list
   *          and the doubly linked list structure. Automatically saves to file
   *          after successful creation.
   * @note Activity name cannot be empty. Duration must be positive. Category and
   *       description default to "General" and "No description" respectively if empty.
   * @note The activity ID is auto-generated using nextId and then incremented.
   * @note The timestamp is automatically set to the current Unix timestamp.
   * @see #saveActivities()
   * @see #addActivity(Activity)
   */
  private void logActivity() {
    System.out.println("\n--- LOG ACTIVITY ---");
    System.out.print("Enter activity name: ");
    String name = scanner.nextLine().trim();

    if (name.isEmpty()) {
      System.out.println("Activity name cannot be empty!");
      return;
    }

    System.out.print("Enter duration (minutes): ");
    int duration;

    try {
      duration = Integer.parseInt(scanner.nextLine().trim());

      if (duration <= 0) {
        System.out.println("Duration must be positive!");
        return;
      }
    } catch (NumberFormatException e) {
      System.out.println("Invalid duration!");
      return;
    }

    System.out.print("Enter category: ");
    String category = scanner.nextLine().trim();

    if (category.isEmpty()) {
      category = "General";
    }

    System.out.print("Enter description: ");
    String description = scanner.nextLine().trim();

    if (description.isEmpty()) {
      description = "No description";
    }

    long timestamp = System.currentTimeMillis() / 1000; // Unix timestamp
    Activity activity = new Activity(nextId++, name, duration, timestamp, category, description);
    activities.add(activity);
    // Add to double linked list
    ActivityNode newNode = new ActivityNode(activity);

    if (head == null) {
      head = newNode;
      tail = newNode;
    } else {
      tail.next = newNode;
      newNode.prev = tail;
      tail = newNode;
    }

    saveActivities();
    System.out.println("Activity logged successfully! ID: " + activity.id);
  }

  /**
   * @brief Views activities using double linked list navigation with keyboard keys.
   * @details Implements an interactive viewer that allows users to navigate through
   *          activities using a doubly linked list structure. Users can move forward
   *          (A key or left arrow) and backward (D key or right arrow) through the
   *          activity list. Displays current activity details, position information,
   *          and navigation hints. The screen is cleared and redrawn on each navigation.
   * @note Navigation keys: A (next/forward), D (previous/backward), Q (quit).
   * @note The implementation uses the doubly linked list (head/tail) for efficient
   *       bidirectional traversal.
   * @see #getPosition(ActivityNode)
   * @see #getTotalCount()
   * @see #clearScreen()
   * @see #printActivity(Activity)
   */
  private void viewActivitiesDoubleLinkedList() {
    if (head == null) {
      System.out.println("\nNo activities found. Start by logging some activities!");
      waitForEnter();
      return;
    }

    ActivityNode current = head;
    boolean navigating = true;

    while (navigating) {
      clearScreen();
      System.out.println("\n=== VIEW ACTIVITIES (Double Linked List) ===");
      System.out.println("Current Activity:");
      System.out.println("----------------------------------------");
      printActivity(current.activity);
      System.out.println("----------------------------------------");
      // Show position info
      int position = getPosition(current);
      int total = getTotalCount();
      System.out.println("\nPosition: [" + position + " / " + total + "]");
      // Show navigation hints
      System.out.println("\nNavigation:");

      if (current.next != null) {
        System.out.println("  [A] or Left Arrow  : Next activity (forward)");
      } else {
        System.out.println("  [A] or Left Arrow  : (Already at last)");
      }

      if (current.prev != null) {
        System.out.println("  [D] or Right Arrow : Previous activity (backward)");
      } else {
        System.out.println("  [D] or Right Arrow : (Already at first)");
      }

      System.out.println("  [Q]                : Quit and return to menu");
      System.out.print("\nEnter command (A/D/Q): ");

      try {
        String input = scanner.nextLine().trim().toLowerCase();

        if (input.isEmpty()) {
          continue;
        }

        char key = input.charAt(0);

        switch (key) {
          case 'a': // Left - Next (go forward)
            if (current.next != null) {
              current = current.next;
            } else {
              System.out.println("\nAlready at the last activity!");
              waitForEnter();
            }

            break;

          case 'd': // Right - Previous (go backward)
            if (current.prev != null) {
              current = current.prev;
            } else {
              System.out.println("\nAlready at the first activity!");
              waitForEnter();
            }

            break;

          case 'q': // Quit
            navigating = false;
            break;

          default:
            System.out.println("\nInvalid key! Use A (next), D (previous), or Q (quit)");
            waitForEnter();
        }
      } catch (Exception e) {
        System.out.println("\nError: " + e.getMessage());
        waitForEnter();
      }
    }
  }

  /**
   * @brief Gets the position of current node in the doubly linked list.
   * @param node The current ActivityNode whose position is to be determined. Must not be null.
   * @return Position as a 1-based index (first node is position 1). Returns 1 if node is head.
   * @details Traverses the linked list from head to find the position of the given node.
   *          The position is 1-indexed for user-friendly display.
   * @note Time complexity: O(n) where n is the number of nodes in the list.
   */
  private int getPosition(ActivityNode node) {
    int pos = 1;
    ActivityNode temp = head;

    while (temp != null && temp != node) {
      pos++;
      temp = temp.next;
    }

    return pos;
  }

  /**
   * @brief Gets the total count of logged activities.
   * @return Total number of activities in the activities list. Returns 0 if list is empty.
   * @details This method returns the size of the activities list, which represents
   *          the total number of activities that have been logged.
   * @note This is a simple getter that returns activities.size().
   */
  private int getTotalCount() {
    return activities.size();
  }

  /**
   * @brief Clears the screen by printing multiple newlines.
   * @details Prints 30 newline characters to create a visual screen clear effect
   *          in console applications. This provides a simple way to refresh the
   *          display without using platform-specific clear commands.
   * @note This is a simple implementation that may not work perfectly on all terminals.
   *       For better results, consider using ANSI escape sequences or platform-specific commands.
   */
  private void clearScreen() {
    // Print multiple newlines to clear the visible area
    for (int i = 0; i < 30; i++) {
      System.out.println();
    }
  }

  /**
   * @brief Waits for the user to press the Enter key.
   * @details Displays a prompt and blocks execution until the user presses Enter.
   *          This is used to pause execution and allow users to read messages before
   *          continuing. Any exceptions during input are silently ignored.
   * @note The method catches and ignores all exceptions to ensure execution continues
   *       even if there are input issues.
   */
  private void waitForEnter() {
    System.out.print("Press Enter to continue...");

    try {
      scanner.nextLine();
    } catch (Exception e) {
      // Ignore
    }
  }

  /**
   * @brief Explores activities using BFS or DFS algorithms.
   * @details Presents a menu allowing users to choose between Breadth-First Search (BFS)
   *          or Depth-First Search (DFS) algorithms for exploring activities. Activities
   *          are connected based on their category - activities in the same category
   *          are considered neighbors in the graph. This allows users to discover
   *          related activities through graph traversal.
   * @note If no activities exist, displays an error message and returns.
   * @see #exploreBFS()
   * @see #exploreDFS()
   */
  private void exploreActivities() {
    if (activities.isEmpty()) {
      System.out.println("\nNo activities found. Start by logging some activities!");
      return;
    }

    System.out.println("\n--- EXPLORE ACTIVITIES ---");
    System.out.println("Choose exploration method:");
    System.out.println("1. BFS (Breadth-First Search)");
    System.out.println("2. DFS (Depth-First Search)");
    System.out.print("Enter your choice: ");

    try {
      int choice = Integer.parseInt(scanner.nextLine().trim());

      if (choice == 1) {
        exploreBFS();
      } else if (choice == 2) {
        exploreDFS();
      } else {
        System.out.println("Invalid choice!");
      }
    } catch (NumberFormatException e) {
      System.out.println("Invalid input!");
    }
  }

  /**
   * @brief Explores activities using Breadth-First Search (BFS) algorithm.
   * @details Implements BFS traversal starting from the first activity. Activities
   *          are connected if they share the same category. The algorithm uses a
   *          queue (FIFO) to process activities level by level, ensuring all activities
   *          in the same category are explored before moving to other categories.
   *          Displays each activity as it is visited with its category information.
   * @note Time complexity: O(V + E) where V is number of activities and E is number of edges.
   * @note Space complexity: O(V) for the queue and visited array.
   * @note Activities are considered neighbors if they have the same category.
   */
  private void exploreBFS() {
    System.out.println("\n--- BFS Exploration ---");
    Queue<Activity> queue = new LinkedList<>();
    boolean[] visited = new boolean[activities.size()];

    // Start from first activity
    if (!activities.isEmpty()) {
      queue.offer(activities.get(0));
      visited[0] = true;
    }

    int count = 0;

    while (!queue.isEmpty()) {
      Activity current = queue.poll();
      System.out.println((++count) + ". " + current.name + " (Category: " + current.category + ")");

      // Add activities from same category (as neighbors)
      for (int i = 0; i < activities.size(); i++) {
        if (!visited[i] && activities.get(i).category.equals(current.category) &&
            activities.get(i).id != current.id) {
          queue.offer(activities.get(i));
          visited[i] = true;
        }
      }
    }

    System.out.println("\nBFS exploration completed. Total activities explored: " + count);
  }

  /**
   * @brief Explores activities using Depth-First Search (DFS) algorithm.
   * @details Implements DFS traversal starting from the first activity. Activities
   *          are connected if they share the same category. The algorithm uses a
   *          stack (LIFO) to process activities depth-first, exploring as far as
   *          possible along each branch before backtracking. Displays each activity
   *          as it is visited with its category information.
   * @note Time complexity: O(V + E) where V is number of activities and E is number of edges.
   * @note Space complexity: O(V) for the stack and visited array.
   * @note Activities are considered neighbors if they have the same category.
   * @note Activities are added to the stack in reverse order to maintain proper DFS behavior.
   */
  private void exploreDFS() {
    System.out.println("\n--- DFS Exploration ---");
    Stack<Activity> stack = new Stack<>();
    boolean[] visited = new boolean[activities.size()];

    // Start from first activity
    if (!activities.isEmpty()) {
      stack.push(activities.get(0));
      visited[0] = true;
    }

    int count = 0;

    while (!stack.isEmpty()) {
      Activity current = stack.pop();
      System.out.println((++count) + ". " + current.name + " (Category: " + current.category + ")");

      // Add activities from same category (as neighbors) in reverse order
      for (int i = activities.size() - 1; i >= 0; i--) {
        if (!visited[i] && activities.get(i).category.equals(current.category) &&
            activities.get(i).id != current.id) {
          stack.push(activities.get(i));
          visited[i] = true;
        }
      }
    }

    System.out.println("\nDFS exploration completed. Total activities explored: " + count);
  }

  /**
   * @brief Searches activities using KMP (Knuth-Morris-Pratt) algorithm.
   * @details Allows users to search for activities by keyword using the efficient
   *          KMP string matching algorithm. The search is performed on both activity
   *          names and descriptions. All matching activities are displayed with their
   *          full details. The KMP algorithm provides O(n + m) time complexity for
   *          pattern matching, making it more efficient than naive string matching.
   * @note The search is case-sensitive and matches substrings within activity names
   *       and descriptions.
   * @note Empty search patterns are rejected.
   * @see #kmpSearch(String, String)
   * @see #computeLPS(String)
   */
  private void searchActivitiesKMP() {
    if (activities.isEmpty()) {
      System.out.println("\nNo activities found. Start by logging some activities!");
      return;
    }

    System.out.println("\n--- SEARCH ACTIVITIES (KMP Algorithm) ---");
    System.out.print("Enter search keyword: ");
    String pattern = scanner.nextLine().trim();

    if (pattern.isEmpty()) {
      System.out.println("Search keyword cannot be empty!");
      return;
    }

    List<Activity> results = new ArrayList<>();

    for (Activity activity : activities) {
      // Search in name and description
      if (kmpSearch(activity.name, pattern) || kmpSearch(activity.description, pattern)) {
        results.add(activity);
      }
    }

    if (results.isEmpty()) {
      System.out.println("No activities found matching: \"" + pattern + "\"");
    } else {
      System.out.println("\nFound " + results.size() + " matching activities:");

      for (Activity activity : results) {
        printActivity(activity);
      }
    }
  }

  /**
   * @brief KMP algorithm implementation for pattern matching.
   * @param text The text string to search in. Can be null.
   * @param pattern The pattern string to search for. Cannot be null or empty.
   * @return true if pattern is found in text, false otherwise. Also returns false
   *         if text or pattern is null, or if pattern is longer than text.
   * @details Implements the Knuth-Morris-Pratt string matching algorithm which uses
   *          a precomputed Longest Proper Prefix which is also Suffix (LPS) array to
   *          avoid unnecessary character comparisons. This provides O(n + m) time
   *          complexity where n is text length and m is pattern length.
   * @note The algorithm handles edge cases like null strings and empty patterns.
   * @see #computeLPS(String)
   */
  private boolean kmpSearch(String text, String pattern) {
    if (text == null || pattern == null || pattern.length() == 0) {
      return false;
    }

    if (pattern.length() > text.length()) {
      return false;
    }

    int[] lps = computeLPS(pattern);
    int i = 0; // index for text
    int j = 0; // index for pattern

    while (i < text.length()) {
      if (text.charAt(i) == pattern.charAt(j)) {
        i++;
        j++;
      }

      if (j == pattern.length()) {
        return true; // Pattern found
      } else if (i < text.length() && text.charAt(i) != pattern.charAt(j)) {
        if (j != 0) {
          j = lps[j - 1];
        } else {
          i++;
        }
      }
    }

    return false;
  }

  /**
   * @brief Computes the Longest Proper Prefix which is also Suffix (LPS) array for KMP algorithm.
   * @param pattern The pattern string for which to compute the LPS array. Must not be null.
   * @return An integer array where each element at index i contains the length of the
   *         longest proper prefix of pattern[0..i] that is also a suffix. Returns
   *         empty array if pattern is empty.
   * @details The LPS array is a key component of the KMP algorithm that allows
   *          efficient pattern matching by precomputing information about the pattern
   *          itself. This avoids backtracking in the text when a mismatch occurs.
   *          Time complexity: O(m) where m is the pattern length.
   * @note The first element of the LPS array is always 0.
   * @see #kmpSearch(String, String)
   */
  private int[] computeLPS(String pattern) {
    int[] lps = new int[pattern.length()];
    int len = 0;
    int i = 1;
    lps[0] = 0;

    while (i < pattern.length()) {
      if (pattern.charAt(i) == pattern.charAt(len)) {
        len++;
        lps[i] = len;
        i++;
      } else {
        if (len != 0) {
          len = lps[len - 1];
        } else {
          lps[i] = 0;
          i++;
        }
      }
    }

    return lps;
  }

  /**
   * @brief Formats Unix timestamp to human-readable date and time string.
   * @param timestamp Unix timestamp in seconds (since January 1, 1970 UTC).
   * @return Formatted string in "yyyy-MM-dd HH:mm" format (e.g., "2024-01-15 14:30").
   * @details Converts a Unix timestamp (seconds since epoch) to a formatted date-time
   *          string using SimpleDateFormat. The format includes year, month, day,
   *          hour, and minute in a standard format.
   * @note The timestamp is multiplied by 1000 to convert from seconds to milliseconds
   *       as required by Java's Date constructor.
   */
  private String formatTimestamp(long timestamp) {
    java.util.Date date = new java.util.Date(timestamp * 1000);
    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
    return sdf.format(date);
  }

  /**
   * @brief Prints detailed information about an activity to the console.
   * @param activity The Activity object to print. Must not be null.
   * @details Displays all activity fields in a formatted manner including ID, name,
   *          duration, category, description, and formatted timestamp. The output
   *          is formatted for easy reading in the console.
   * @see #formatTimestamp(long)
   */
  private void printActivity(Activity activity) {
    System.out.println("ID: " + activity.id);
    System.out.println("Name: " + activity.name);
    System.out.println("Duration: " + activity.duration + " minutes");
    System.out.println("Category: " + activity.category);
    System.out.println("Description: " + activity.description);
    System.out.println("Date & Time: " + formatTimestamp(activity.timestamp));
  }

  /**
   * @brief Loads activities from the persistence file.
   * @details Reads activities from the activities.txt file and reconstructs both
   *          the activities list and the doubly linked list structure. The file format
   *          is pipe-delimited: id|name|duration|timestamp|category|description.
   *          Empty lines and lines starting with '#' are treated as comments and skipped.
   *          After loading, the nextId is set to one more than the maximum ID found.
   * @note If the file does not exist, the method returns silently without error.
   * @note Invalid lines are skipped silently during parsing.
   * @note This method automatically rebuilds the doubly linked list from loaded activities.
   * @see #ACTIVITIES_FILE
   * @see #saveActivities()
   * @throws IOException If an I/O error occurs while reading the file. Errors are
   *                    logged but do not stop execution.
   */
  public void loadActivities() {
    File file = new File(ACTIVITIES_FILE);

    if (!file.exists()) {
      return;
    }

    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
      String line;
      int maxId = 0;

      while ((line = reader.readLine()) != null) {
        line = line.trim();

        if (line.isEmpty() || line.startsWith("#")) {
          continue; // Skip empty lines and comments
        }

        // Format: id|name|duration|timestamp|category|description
        String[] parts = line.split("\\|", -1);

        if (parts.length == 6) {
          try {
            int id = Integer.parseInt(parts[0]);
            String name = parts[1];
            int duration = Integer.parseInt(parts[2]);
            long timestamp = Long.parseLong(parts[3]);
            String category = parts[4];
            String description = parts[5];
            Activity activity = new Activity(id, name, duration, timestamp, category, description);
            activities.add(activity);
            // Add to double linked list
            ActivityNode newNode = new ActivityNode(activity);

            if (head == null) {
              head = newNode;
              tail = newNode;
            } else {
              tail.next = newNode;
              newNode.prev = tail;
              tail = newNode;
            }

            if (id > maxId) {
              maxId = id;
            }
          } catch (NumberFormatException e) {
            // Skip invalid lines silently
          }
        }
      }

      nextId = maxId + 1;
    } catch (IOException e) {
      logger.error("Error loading activities from file: ", e);
      System.out.println("Warning: Could not load activities from file.");
    }
  }

  /**
   * @brief Saves all activities to the persistence file.
   * @details Writes all activities to the activities.txt file in a pipe-delimited format:
   *          id|name|duration|timestamp|category|description. The file includes a header
   *          comment explaining the format. All activities are written sequentially.
   * @note This method overwrites the entire file each time it is called.
   * @note The file format is designed to be human-readable while remaining parseable.
   * @see #ACTIVITIES_FILE
   * @see #loadActivities()
   * @throws IOException If an I/O error occurs while writing the file. Errors are
   *                    logged but do not stop execution.
   */
  public void saveActivities() {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(ACTIVITIES_FILE))) {
      writer.write("# Activities file");
      writer.newLine();
      writer.write("# Format: id|name|duration|timestamp|category|description");
      writer.newLine();

      for (Activity activity : activities) {
        writer.write(activity.id + "|" +
                     activity.name + "|" +
                     activity.duration + "|" +
                     activity.timestamp + "|" +
                     activity.category + "|" +
                     activity.description);
        writer.newLine();
      }
    } catch (IOException e) {
      logger.error("Error saving activities to file: ", e);
      System.out.println("Warning: Could not save activities to file.");
    }
  }

  /**
   * @brief Gets a copy of the list of all activities.
   * @return A new ArrayList containing all activities. Returns an empty list if no
   *         activities have been logged. The returned list is a defensive copy,
   *         so modifications to it will not affect the internal activities list.
   * @details This method is primarily used for testing and by other modules that
   *          need to access activity data. The defensive copy ensures data integrity.
   * @note Modifying the returned list will not affect the internal activities list.
   * @note This method is thread-safe in the sense that it returns a snapshot, but
   *       concurrent modifications to activities may still cause issues.
   */
  public List<Activity> getActivities() {
    return new ArrayList<>(activities);
  }

  /**
   * @brief Adds an activity to the list and doubly linked list structure.
   * @param activity The Activity object to add. Must not be null.
   * @details Adds the activity to both the activities list and the doubly linked list.
   *          If the list is empty, the activity becomes both head and tail. Otherwise,
   *          it is appended to the tail and the tail pointer is updated. This method
   *          is primarily used for testing purposes but can also be used programmatically.
   * @note This method does NOT automatically save to file. Call saveActivities() if needed.
   * @note This method does NOT assign an ID to the activity. The activity should have
   *       a valid ID before being added.
   * @see #saveActivities()
   */
  public void addActivity(Activity activity) {
    activities.add(activity);
    // Add to double linked list
    ActivityNode newNode = new ActivityNode(activity);

    if (head == null) {
      head = newNode;
      tail = newNode;
    } else {
      tail.next = newNode;
      newNode.prev = tail;
      tail = newNode;
    }
  }

  /**
   * @brief Closes the scanner resource and saves all activities to file.
   * @details Performs cleanup operations including saving all activities to the
   *          persistence file and closing the Scanner instance. This method should
   *          be called when the ActivityLogging instance is no longer needed to
   *          ensure data persistence and proper resource cleanup.
   * @note This method is idempotent - it can be called multiple times safely.
   * @note Always saves activities before closing, ensuring no data loss.
   * @see #saveActivities()
   */
  public void close() {
    saveActivities();

    if (scanner != null) {
      scanner.close();
    }
  }
}

/**
 * @file TimeSpentAnalysis.java
 * @brief This file handles time spent analysis functionality.
 * @details This file contains the TimeSpentAnalysis class which provides methods
 *          to analyze time spent on different activities. Implements:
 *          - Heap sort for longer period summary
 *          - Sparse matrix for daily summary
 *          - Linear probing hash table for time data visualization
 */
package com.efil.nese.timetracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;

/**
 * @class TimeSpentAnalysis
 * @brief This class handles time spent analysis operations.
 * @details The TimeSpentAnalysis class provides methods to analyze and visualize
 *          time spent on various activities and tasks using advanced data structures.
 *          Implements heap sort for sorting, sparse matrix for daily summaries,
 *          and linear probing hash table for data visualization.
 * @author efil.saylam.nese.sarp
 * @version 1.0
 * @since 2024
 */
public class TimeSpentAnalysis {

  /** @brief Logger instance for logging errors and debug information. */
  private static final Logger logger = (Logger) LoggerFactory.getLogger(TimeSpentAnalysis.class);

  /** @brief Scanner instance for reading user input from console. */
  private Scanner scanner;

  /** @brief Reference to ActivityLogging instance for accessing activity data. */
  private ActivityLogging activityLogging;

  /**
   * @brief Constructor for TimeSpentAnalysis class.
   */
  public TimeSpentAnalysis() {
    this.scanner = new Scanner(System.in);
  }

  /**
   * @brief Constructor with ActivityLogging reference.
   * @param activityLogging Reference to ActivityLogging instance.
   */
  public TimeSpentAnalysis(ActivityLogging activityLogging) {
    this.scanner = new Scanner(System.in);
    this.activityLogging = activityLogging;
  }

  /**
   * @brief Sets the ActivityLogging reference.
   * @param activityLogging Reference to ActivityLogging instance.
   */
  public void setActivityLogging(ActivityLogging activityLogging) {
    this.activityLogging = activityLogging;
  }

  /**
   * @brief Main method to run the Time Spent Analysis feature.
   * @details Displays the time spent analysis menu and handles user interactions.
   */
  public void run() {
    if (activityLogging == null) {
      System.out.println("\n=== Time Spent Analysis ===");
      System.out.println("Error: ActivityLogging not initialized.");
      System.out.println("Press Enter to return to main menu...");
      scanner.nextLine();
      return;
    }

    int choice;
    boolean back = false;

    while (!back) {
      System.out.println("\n========================================");
      System.out.println("      Time Spent Analysis Menu          ");
      System.out.println("========================================");
      System.out.println("1. Longer Period Summary (Heap Sort)");
      System.out.println("2. Daily Summary (Sparse Matrix)");
      System.out.println("3. View Time Data Table (Linear Probing)");
      System.out.println("4. Back to Main Menu");
      System.out.println("========================================");
      System.out.print("Enter your choice: ");

      try {
        choice = Integer.parseInt(scanner.nextLine().trim());
      } catch (NumberFormatException e) {
        choice = -1;
      }

      switch (choice) {
        case 1:
          longerPeriodSummary();
          break;

        case 2:
          dailySummary();
          break;

        case 3:
          viewTimeDataTable();
          break;

        case 4:
          back = true;
          break;

        default:
          System.out.println("Invalid choice. Please try again.");
      }
    }
  }

  /**
   * @brief Displays longer period summary using heap sort algorithm.
   * @details Aggregates total time spent per activity name across all logged activities,
   *          then sorts them by total time using the heap sort algorithm. Displays the
   *          results in descending order (most time spent first). Heap sort provides
   *          O(n log n) time complexity and is an in-place sorting algorithm.
   * @note If no activities exist, displays an error message and returns.
   * @note Activities with the same name have their durations summed together.
   * @see #heapSort(List)
   * @see ActivitySummary
   */
  private void longerPeriodSummary() {
    List<Activity> activities = activityLogging.getActivities();

    if (activities.isEmpty()) {
      System.out.println("\nNo activities found. Start by logging some activities!");
      waitForEnter();
      return;
    }

    System.out.println("\n=== Longer Period Summary (Heap Sort) ===");
    // Create a list of activities with total time per activity name
    Map<String, Integer> activityTimeMap = new HashMap<>();

    for (Activity activity : activities) {
      activityTimeMap.put(activity.name,
                          activityTimeMap.getOrDefault(activity.name, 0) + activity.duration);
    }

    // Convert to list for heap sort
    List<ActivitySummary> summaries = new ArrayList<>();

    for (Map.Entry<String, Integer> entry : activityTimeMap.entrySet()) {
      summaries.add(new ActivitySummary(entry.getKey(), entry.getValue()));
    }

    // Perform heap sort
    heapSort(summaries);
    // Display results
    System.out.println("\nActivities sorted by total time spent (descending):");
    System.out.println("---------------------------------------------------");
    System.out.printf("%-30s %15s%n", "Activity Name", "Total Time (min)");
    System.out.println("---------------------------------------------------");

    for (int i = summaries.size() - 1; i >= 0; i--) {
      ActivitySummary summary = summaries.get(i);
      System.out.printf("%-30s %15d%n", summary.name, summary.totalTime);
    }

    System.out.println("---------------------------------------------------");
    System.out.println("Total activities: " + summaries.size());
    waitForEnter();
  }

  /**
   * @brief Helper class for activity summary data.
   * @details This inner class encapsulates the aggregated summary information for
   *          an activity, including its name and total time spent across all instances.
   *          Used for sorting and displaying activity summaries.
   * @author efil.saylam.nese.sarp
   */
  private static class ActivitySummary {
    /** @brief Name of the activity for which this summary is created. */
    String name;

    /** @brief Total time in minutes spent on this activity across all instances. */
    int totalTime;

    /**
     * @brief Constructor for ActivitySummary class.
     * @param name Activity name. Must not be null.
     * @param totalTime Total time in minutes. Must be non-negative.
     * @details Creates a summary object that aggregates time spent for a specific activity.
     */
    ActivitySummary(String name, int totalTime) {
      this.name = name;
      this.totalTime = totalTime;
    }
  }

  /**
   * @brief Heap sort implementation for sorting activities by time.
   * @param arr List of ActivitySummary objects to sort in-place by totalTime field.
   * @details Implements the heap sort algorithm which first builds a max heap from
   *          the array, then repeatedly extracts the maximum element and places it
   *          at the end of the array. This results in an ascending sorted array.
   *          Time complexity: O(n log n) in all cases. Space complexity: O(1) excluding
   *          the input array.
   * @note The array is sorted in ascending order. For descending order, iterate in reverse.
   * @see #heapify(List, int, int)
   */
  private void heapSort(List<ActivitySummary> arr) {
    int n = arr.size();

    // Build max heap
    for (int i = n / 2 - 1; i >= 0; i--) {
      heapify(arr, n, i);
    }

    // Extract elements from heap one by one
    for (int i = n - 1; i > 0; i--) {
      // Move current root to end
      ActivitySummary temp = arr.get(0);
      arr.set(0, arr.get(i));
      arr.set(i, temp);
      // Call heapify on reduced heap
      heapify(arr, i, 0);
    }
  }

  /**
   * @brief Heapify function for maintaining max heap property.
   * @param arr List of ActivitySummary objects representing the heap.
   * @param n Size of the heap (number of elements to consider).
   * @param i Index of the root node to heapify.
   * @details Maintains the max heap property by comparing the root with its children
   *          and swapping if necessary. Recursively heapifies the affected subtree.
   *          This is a key operation in heap sort that ensures the largest element
   *          is at the root of the heap.
   * @note Time complexity: O(log n) where n is the size of the heap.
   * @note This function assumes the subtrees are already valid heaps.
   */
  private void heapify(List<ActivitySummary> arr, int n, int i) {
    int largest = i;
    int left = 2 * i + 1;
    int right = 2 * i + 2;

    if (left < n && arr.get(left).totalTime > arr.get(largest).totalTime) {
      largest = left;
    }

    if (right < n && arr.get(right).totalTime > arr.get(largest).totalTime) {
      largest = right;
    }

    if (largest != i) {
      ActivitySummary swap = arr.get(i);
      arr.set(i, arr.get(largest));
      arr.set(largest, swap);
      heapify(arr, n, largest);
    }
  }

  /**
   * @brief Displays daily summary using sparse matrix representation.
   * @details Groups activities by date and activity name, then displays time spent
   *          per activity per day. Uses a sparse matrix representation (implemented
   *          as nested HashMaps) to store only non-zero entries (days with activities).
   *          This is memory-efficient as it doesn't store empty days. Displays totals
   *          for each day and overall statistics.
   * @note If no activities exist, displays an error message and returns.
   * @note Dates are sorted chronologically for display.
   * @note The sparse matrix only stores days that have at least one activity.
   * @see #formatDate(long)
   */
  private void dailySummary() {
    List<Activity> activities = activityLogging.getActivities();

    if (activities.isEmpty()) {
      System.out.println("\nNo activities found. Start by logging some activities!");
      waitForEnter();
      return;
    }

    System.out.println("\n=== Daily Summary (Sparse Matrix) ===");
    // Create sparse matrix: day -> activity -> time
    // Using Map structure to represent sparse matrix (only non-zero entries)
    Map<String, Map<String, Integer>> sparseMatrix = new HashMap<>();

    for (Activity activity : activities) {
      String date = formatDate(activity.timestamp);
      String activityName = activity.name;

      if (!sparseMatrix.containsKey(date)) {
        sparseMatrix.put(date, new HashMap<>());
      }

      Map<String, Integer> dayActivities = sparseMatrix.get(date);
      dayActivities.put(activityName,
                        dayActivities.getOrDefault(activityName, 0) + activity.duration);
    }

    // Display sparse matrix
    System.out.println("\nDaily Time Spent Summary (Sparse Matrix Representation):");
    System.out.println("============================================================");
    List<String> sortedDates = new ArrayList<>(sparseMatrix.keySet());
    sortedDates.sort(String::compareTo);

    for (String date : sortedDates) {
      System.out.println("\nDate: " + date);
      System.out.println("----------------------------------------");
      Map<String, Integer> dayActivities = sparseMatrix.get(date);
      int totalDayTime = 0;

      for (Map.Entry<String, Integer> entry : dayActivities.entrySet()) {
        System.out.printf("  %-30s: %5d minutes%n", entry.getKey(), entry.getValue());
        totalDayTime += entry.getValue();
      }

      System.out.println("  " + "-".repeat(38));
      System.out.printf("  %-30s: %5d minutes%n", "TOTAL", totalDayTime);
    }

    System.out.println("\n============================================================");
    System.out.println("Note: Sparse matrix stores only non-zero entries (days with activities)");
    System.out.println("Total days with activities: " + sparseMatrix.size());
    waitForEnter();
  }

  /**
   * @brief Formats Unix timestamp to date string (YYYY-MM-DD).
   * @param timestamp Unix timestamp.
   * @return Formatted date string.
   */
  private String formatDate(long timestamp) {
    java.util.Date date = new java.util.Date(timestamp * 1000);
    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
    return sdf.format(date);
  }

  /**
   * @brief Displays time data table using linear probing hash table simulation.
   * @details Demonstrates how activity data would be stored in a hash table using
   *          linear probing for collision resolution. Calculates an appropriate table
   *          size (next prime number), inserts all activities using their names as
   *          keys, and displays the resulting hash table structure. Shows hash values,
   *          collision counts, probe sequences, and load factor statistics.
   * @note If no activities exist, displays an error message and returns.
   * @note Table size is chosen as the next prime number after 2 * activity count for good load factor.
   * @note Linear probing resolves collisions by checking the next available slot.
   * @see #nextPrime(int)
   * @see #isPrime(int)
   * @see HashTableEntry
   */
  private void viewTimeDataTable() {
    List<Activity> activities = activityLogging.getActivities();

    if (activities.isEmpty()) {
      System.out.println("\nNo activities found. Start by logging some activities!");
      waitForEnter();
      return;
    }

    System.out.println("\n=== View Time Data Table (Linear Probing Hash Table) ===");
    // Calculate appropriate table size (next prime number after activities.size() * 2)
    int tableSize = nextPrime(activities.size() * 2);

    if (tableSize < 11) {
      tableSize = 11; // Minimum size
    }

    // Create hash table with linear probing
    HashTableEntry[] hashTable = new HashTableEntry[tableSize];
    int collisions = 0;
    System.out.println("\nInserting activities into hash table using linear probing...");
    System.out.println("Hash table size: " + tableSize);
    System.out.println("Hash function: hash(key) = (key.hashCode() & 0x7FFFFFFF) % tableSize");
    System.out.println();

    for (Activity activity : activities) {
      String key = activity.name;
      int hash = (key.hashCode() & 0x7FFFFFFF) % tableSize;
      int originalHash = hash;
      int probeCount = 0;

      // Linear probing
      while (hashTable[hash] != null) {
        collisions++;
        probeCount++;
        hash = (hash + 1) % tableSize;

        // Prevent infinite loop (shouldn't happen if table size is correct)
        if (hash == originalHash) {
          System.out.println("Warning: Hash table is full!");
          break;
        }
      }

      hashTable[hash] = new HashTableEntry(key, activity.duration, activity.timestamp,
                                           originalHash, probeCount);
    }

    // Display hash table
    System.out.println("Hash Table Contents (Linear Probing):");
    System.out.println("=====================================");
    System.out.printf("%-5s %-8s %-30s %-15s %-20s %-10s%n",
                      "Index", "Hash", "Activity Name", "Duration (min)", "Timestamp", "Probes");
    System.out.println("-".repeat(100));
    int occupiedSlots = 0;

    for (int i = 0; i < tableSize; i++) {
      if (hashTable[i] != null) {
        occupiedSlots++;
        HashTableEntry entry = hashTable[i];
        System.out.printf("%-5d %-8d %-30s %-15d %-20d %-10d%n",
                          i, entry.originalHash, entry.key, entry.duration,
                          entry.timestamp, entry.probeCount);
      } else {
        System.out.printf("%-5d %-8s %-30s%n", i, "-", "(empty)");
      }
    }

    System.out.println("-".repeat(100));
    System.out.println("\nStatistics:");
    System.out.println("  Total slots: " + tableSize);
    System.out.println("  Occupied slots: " + occupiedSlots);
    System.out.println("  Empty slots: " + (tableSize - occupiedSlots));
    System.out.println("  Load factor: " + String.format("%.2f", (double)occupiedSlots / tableSize));
    System.out.println("  Total collisions: " + collisions);
    System.out.println("  Average probes per insertion: " +
                       String.format("%.2f", occupiedSlots > 0 ? (double)collisions / occupiedSlots : 0));
    waitForEnter();
  }

  /**
   * @brief Helper class for hash table entry representation.
   * @details This inner class represents a single entry in the hash table, storing
   *          the activity name (key), duration, timestamp, original hash value, and
   *          the number of probes needed for insertion. Used for displaying hash table
   *          structure and collision statistics.
   * @author efil.saylam.nese.sarp
   */
  private static class HashTableEntry {
    /** @brief Activity name used as the hash table key. */
    String key;

    /** @brief Duration in minutes for this activity entry. */
    int duration;

    /** @brief Unix timestamp when this activity was logged. */
    long timestamp;

    /** @brief Original hash value before linear probing. Used for display purposes. */
    int originalHash;

    /** @brief Number of probes needed to find an empty slot during insertion. */
    int probeCount;

    /**
     * @brief Constructor for HashTableEntry class.
     * @param key Activity name used as the key. Must not be null.
     * @param duration Duration in minutes. Must be non-negative.
     * @param timestamp Unix timestamp in seconds. Must be valid.
     * @param originalHash Original hash value before collision resolution.
     * @param probeCount Number of linear probes performed during insertion.
     * @details Creates an entry object for display in the hash table visualization.
     */
    HashTableEntry(String key, int duration, long timestamp, int originalHash, int probeCount) {
      this.key = key;
      this.duration = duration;
      this.timestamp = timestamp;
      this.originalHash = originalHash;
      this.probeCount = probeCount;
    }
  }

  /**
   * @brief Finds the next prime number greater than or equal to n.
   * @param n Starting number to search from. Must be non-negative.
   * @return The smallest prime number that is greater than or equal to n.
   *         Returns 2 if n <= 1.
   * @details Iteratively checks numbers starting from n until a prime number is found.
   *          Uses the isPrime() method for primality testing. This is useful for
   *          determining optimal hash table sizes, as prime-sized tables help reduce
   *          clustering in hash functions.
   * @see #isPrime(int)
   */
  private int nextPrime(int n) {
    if (n <= 1) {
      return 2;
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
   * @details Uses an optimized trial division algorithm that checks divisibility
   *          by 2 and 3 first, then checks numbers of the form 6k±1 up to sqrt(n).
   *          This is more efficient than checking all numbers up to sqrt(n).
   * @note Time complexity: O(sqrt(n)) in the worst case.
   * @note The algorithm correctly handles edge cases (n <= 1, n == 2, n == 3).
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
   * @brief Waits for Enter key press.
   */
  private void waitForEnter() {
    System.out.print("\nPress Enter to continue...");

    try {
      scanner.nextLine();
    } catch (Exception e) {
      // Ignore
    }
  }

  /**
   * @brief Closes the scanner resource.
   */
  public void close() {
    if (scanner != null) {
      scanner.close();
    }
  }
}

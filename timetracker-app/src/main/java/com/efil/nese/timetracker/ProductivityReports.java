/**
 * @file ProductivityReports.java
 * @brief This file handles productivity reports functionality.
 * @details This file contains the ProductivityReports class which provides methods
 *          to generate and display productivity reports. Implements:
 *          - B+ Tree for storing reports by category
 *          - Huffman encoding for report compression
 *          - Strongly connected components for activity graph analysis
 */
package com.efil.nese.timetracker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;
import java.util.Stack;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;

/**
 * @class ProductivityReports
 * @brief This class handles productivity reports operations.
 * @details The ProductivityReports class provides methods to generate, view,
 *          and export productivity reports based on user activity data.
 *          Implements B+ Tree for report storage, Huffman encoding for compression,
 *          and Tarjan's algorithm for graph analysis.
 * @author efil.saylam.nese.sarp
 * @version 1.0
 * @since 2024
 */
public class ProductivityReports {

  /** @brief Logger instance for logging errors and debug information. */
  private static final Logger logger = (Logger) LoggerFactory.getLogger(ProductivityReports.class);

  /** @brief Scanner instance for reading user input from console. */
  private Scanner scanner;

  /** @brief Reference to ActivityLogging instance for accessing activity data. */
  private ActivityLogging activityLogging;

  /** @brief B+ Tree data structure for storing and organizing reports by category. */
  private BPlusTree reportTree; // B+ Tree for storing reports by category

  /**
   * @brief Default constructor for ProductivityReports class.
   * @details Initializes the Scanner for user input and creates a new B+ Tree
   *          with order 3 for storing reports. The ActivityLogging reference is
   *          set to null and must be set later using setActivityLogging().
   * @note The B+ Tree order of 3 provides a good balance between tree height
   *       and node size for typical report volumes.
   * @see #setActivityLogging(ActivityLogging)
   */
  public ProductivityReports() {
    this.scanner = new Scanner(System.in);
    this.reportTree = new BPlusTree(3); // Order 3 B+ Tree
  }

  /**
   * @brief Constructor with ActivityLogging reference.
   * @param activityLogging Reference to ActivityLogging instance. Must not be null
   *                       for the reports feature to function properly.
   * @details Initializes the Scanner and B+ Tree, and sets the ActivityLogging
   *         reference. This constructor is preferred when the ActivityLogging instance
   *         is already available.
   * @note The ActivityLogging reference is required for generating reports from activity data.
   */
  public ProductivityReports(ActivityLogging activityLogging) {
    this.scanner = new Scanner(System.in);
    this.activityLogging = activityLogging;
    this.reportTree = new BPlusTree(3); // Order 3 B+ Tree
  }

  /**
   * @brief Sets the ActivityLogging reference for accessing activity data.
   * @param activityLogging Reference to ActivityLogging instance. Should not be null.
   * @details This method allows setting or updating the ActivityLogging reference
   *          after object construction. This is useful when the ActivityLogging
   *          instance is created after the ProductivityReports instance.
   * @note All report generation features require a valid ActivityLogging reference.
   */
  public void setActivityLogging(ActivityLogging activityLogging) {
    this.activityLogging = activityLogging;
  }

  /**
   * @brief Main method to run the Productivity Reports feature.
   * @details Displays the productivity reports menu and handles user interactions.
   */
  public void run() {
    if (activityLogging == null) {
      System.out.println("\n=== Productivity Reports ===");
      System.out.println("Error: ActivityLogging not initialized.");
      System.out.println("Press Enter to return to main menu...");
      scanner.nextLine();
      return;
    }

    int choice;
    boolean back = false;

    while (!back) {
      System.out.println("\n========================================");
      System.out.println("      Productivity Reports Menu          ");
      System.out.println("========================================");
      System.out.println("1. Generate Reports (B+ Tree)");
      System.out.println("2. View Reports");
      System.out.println("3. Archive Old Reports (Huffman)");
      System.out.println("4. Analyze Activity Graph (Strongly Connected)");
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
          generateReports();
          break;

        case 2:
          viewReports();
          break;

        case 3:
          archiveOldReports();
          break;

        case 4:
          analyzeActivityGraph();
          break;

        case 5:
          back = true;
          break;

        default:
          System.out.println("Invalid choice. Please try again.");
      }
    }
  }

  /**
   * @brief Generates reports and stores them in B+ Tree by category.
   * @details Analyzes all activities from ActivityLogging and creates summary reports
   *          grouped by category. For each category, calculates total time spent,
   *          activity count, earliest and latest timestamps. Each report is stored
   *          in the B+ Tree using the category name as the key, enabling efficient
   *          sorted retrieval. Clears existing reports before generating new ones.
   * @note If no activities exist, displays an error message and returns.
   * @note The B+ Tree automatically maintains sorted order by category name.
   * @see #viewReports()
   * @see BPlusTree#insert(String, Report)
   */
  private void generateReports() {
    List<Activity> activities = activityLogging.getActivities();

    if (activities.isEmpty()) {
      System.out.println("\nNo activities found. Start by logging some activities!");
      waitForEnter();
      return;
    }

    System.out.println("\n=== Generate Reports (B+ Tree) ===");
    // Clear existing reports
    reportTree = new BPlusTree(3);
    // Group activities by category
    Map<String, List<Activity>> categoryMap = new HashMap<>();

    for (Activity activity : activities) {
      String category = activity.category;

      if (!categoryMap.containsKey(category)) {
        categoryMap.put(category, new ArrayList<>());
      }

      categoryMap.get(category).add(activity);
    }

    // Generate report for each category and store in B+ Tree
    int reportCount = 0;

    for (Map.Entry<String, List<Activity>> entry : categoryMap.entrySet()) {
      String category = entry.getKey();
      List<Activity> categoryActivities = entry.getValue();
      // Calculate summary statistics
      int totalTime = 0;
      int activityCount = categoryActivities.size();
      long earliestTime = Long.MAX_VALUE;
      long latestTime = Long.MIN_VALUE;

      for (Activity activity : categoryActivities) {
        totalTime += activity.duration;

        if (activity.timestamp < earliestTime) {
          earliestTime = activity.timestamp;
        }

        if (activity.timestamp > latestTime) {
          latestTime = activity.timestamp;
        }
      }

      // Create report
      Report report = new Report(category, activityCount, totalTime,
                                 earliestTime, latestTime, categoryActivities);
      // Insert into B+ Tree (using category as key)
      reportTree.insert(category, report);
      reportCount++;
    }

    System.out.println("\nReports generated successfully!");
    System.out.println("Total categories: " + reportCount);
    System.out.println("Reports stored in B+ Tree structure.");
    System.out.println("\nB+ Tree Statistics:");
    System.out.println("  Order: 3");
    System.out.println("  Total keys: " + reportCount);
    waitForEnter();
  }

  /**
   * @brief Views reports stored in B+ Tree.
   * @details Retrieves all reports from the B+ Tree in sorted order (by category)
   *          and displays them with detailed statistics. For each report, shows
   *          category name, total activities, total time, average time per activity,
   *          and date range. The B+ Tree structure ensures reports are displayed
   *          in alphabetical order by category.
   * @note If no reports exist, displays an error message and returns.
   * @note Reports must be generated first using generateReports().
   * @see #generateReports()
   * @see BPlusTree#getAllReports()
   */
  private void viewReports() {
    if (reportTree.isEmpty()) {
      System.out.println("\nNo reports found. Please generate reports first!");
      waitForEnter();
      return;
    }

    System.out.println("\n=== View Reports ===");
    System.out.println("\nReports stored in B+ Tree (sorted by category):");
    System.out.println("==================================================");
    List<Report> reports = reportTree.getAllReports();

    for (Report report : reports) {
      System.out.println("\nCategory: " + report.category);
      System.out.println("  Total Activities: " + report.activityCount);
      System.out.println("  Total Time: " + report.totalTime + " minutes");
      System.out.println("  Average Time per Activity: " +
                         String.format("%.2f", (double)report.totalTime / report.activityCount) + " minutes");
      System.out.println("  Date Range: " + formatTimestamp(report.earliestTime) +
                         " to " + formatTimestamp(report.latestTime));
    }

    System.out.println("\n==================================================");
    System.out.println("Total reports: " + reports.size());
    waitForEnter();
  }

  /**
   * @brief Archives old reports using Huffman encoding compression.
   * @details Compresses all reports using the Huffman encoding algorithm for
   *          efficient storage. Converts all reports to a string representation,
   *          builds a Huffman tree based on character frequencies, generates
   *          variable-length codes, and encodes the data. Displays compression
   *          statistics including original size, compressed size, compression ratio,
   *          and space saved. Shows a sample of the encoding table.
   * @note If no reports exist, displays an error message and returns.
   * @note Huffman encoding provides optimal prefix codes for lossless compression.
   * @see HuffmanEncoder#encode(String)
   * @see HuffmanEncoder#getEncodingTable()
   */
  private void archiveOldReports() {
    if (reportTree.isEmpty()) {
      System.out.println("\nNo reports found. Please generate reports first!");
      waitForEnter();
      return;
    }

    System.out.println("\n=== Archive Old Reports (Huffman Encoding) ===");
    // Get all reports and convert to string for compression
    List<Report> reports = reportTree.getAllReports();
    StringBuilder reportData = new StringBuilder();

    for (Report report : reports) {
      reportData.append(report.category).append("|")
                .append(report.activityCount).append("|")
                .append(report.totalTime).append("|")
                .append(report.earliestTime).append("|")
                .append(report.latestTime).append("\n");
    }

    String originalData = reportData.toString();
    int originalSize = originalData.length() * 8; // Size in bits
    System.out.println("Original data size: " + originalSize + " bits (" +
                       originalData.length() + " bytes)");
    // Build Huffman tree and encode
    HuffmanEncoder encoder = new HuffmanEncoder();
    String encodedData = encoder.encode(originalData);
    Map<Character, String> encodingTable = encoder.getEncodingTable();
    int compressedSize = encodedData.length();
    double compressionRatio = (1.0 - (double)compressedSize / originalSize) * 100;
    System.out.println("\nHuffman Encoding Results:");
    System.out.println("  Compressed size: " + compressedSize + " bits");
    System.out.println("  Compression ratio: " + String.format("%.2f", compressionRatio) + "%");
    System.out.println("  Space saved: " + (originalSize - compressedSize) + " bits");
    System.out.println("\nEncoding Table (first 10 characters):");
    int count = 0;

    for (Map.Entry<Character, String> entry : encodingTable.entrySet()) {
      if (count++ >= 10) break;

      char ch = entry.getKey();
      String code = entry.getValue();
      System.out.println("  '" + (ch == '\n' ? "\\n" : ch) + "' -> " + code);
    }

    if (encodingTable.size() > 10) {
      System.out.println("  ... and " + (encodingTable.size() - 10) + " more");
    }

    System.out.println("\nReports archived successfully using Huffman encoding!");
    waitForEnter();
  }

  /**
   * @brief Analyzes activity graph using strongly connected components.
   * @details Builds a directed graph where activities are connected if they share
   *          the same category or were performed on the same day. Uses Tarjan's
   *          algorithm to find all strongly connected components (SCCs) in the graph.
   *          Displays the graph structure, number of components, and groups activities
   *          within each component by category. This analysis helps identify clusters
   *          of related activities.
   * @note If no activities exist, displays an error message and returns.
   * @note Time complexity: O(V + E) where V is vertices (activities) and E is edges.
   * @see StronglyConnectedComponents#findSCC()
   */
  private void analyzeActivityGraph() {
    List<Activity> activities = activityLogging.getActivities();

    if (activities.isEmpty()) {
      System.out.println("\nNo activities found. Start by logging some activities!");
      waitForEnter();
      return;
    }

    System.out.println("\n=== Analyze Activity Graph (Strongly Connected Components) ===");
    // Build graph: activities are connected if they share same category or
    // are performed on the same day
    int n = activities.size();
    List<List<Integer>> graph = new ArrayList<>();

    for (int i = 0; i < n; i++) {
      graph.add(new ArrayList<>());
    }

    // Create adjacency list
    for (int i = 0; i < n; i++) {
      Activity a1 = activities.get(i);

      for (int j = 0; j < n; j++) {
        if (i != j) {
          Activity a2 = activities.get(j);

          // Connect if same category or same day
          if (a1.category.equals(a2.category) ||
              formatDate(a1.timestamp).equals(formatDate(a2.timestamp))) {
            graph.get(i).add(j);
          }
        }
      }
    }

    // Find strongly connected components using Tarjan's algorithm
    StronglyConnectedComponents scc = new StronglyConnectedComponents(graph, activities);
    List<List<Integer>> components = scc.findSCC();
    System.out.println("\nActivity Graph Analysis:");
    System.out.println("  Total activities: " + n);
    System.out.println("  Total edges: " + countEdges(graph));
    System.out.println("  Strongly connected components: " + components.size());
    System.out.println("\nStrongly Connected Components:");
    System.out.println("==========================================");

    for (int i = 0; i < components.size(); i++) {
      List<Integer> component = components.get(i);
      System.out.println("\nComponent " + (i + 1) + " (Size: " + component.size() + "):");
      // Group by category
      Map<String, List<String>> categoryGroups = new HashMap<>();

      for (int idx : component) {
        Activity activity = activities.get(idx);

        if (!categoryGroups.containsKey(activity.category)) {
          categoryGroups.put(activity.category, new ArrayList<>());
        }

        categoryGroups.get(activity.category).add(activity.name);
      }

      for (Map.Entry<String, List<String>> entry : categoryGroups.entrySet()) {
        System.out.println("  Category: " + entry.getKey());

        for (String name : entry.getValue()) {
          System.out.println("    - " + name);
        }
      }
    }

    System.out.println("\n==========================================");
    System.out.println("Graph represents relationships between activities");
    System.out.println("(connected if same category or performed on same day)");
    waitForEnter();
  }

  /**
   * @brief Counts total edges in the graph.
   * @param graph Adjacency list representation of the graph. Each list contains
   *              indices of connected vertices.
   * @return Total number of edges in the graph. Since the graph is undirected
   *         (each edge appears twice in adjacency lists), the result is divided by 2.
   * @details Iterates through all adjacency lists and sums their sizes, then divides
   *          by 2 to account for each edge being represented twice in an undirected
   *          graph representation.
   */
  private int countEdges(List<List<Integer>> graph) {
    int count = 0;

    for (List<Integer> neighbors : graph) {
      count += neighbors.size();
    }

    return count / 2; // Each edge counted twice
  }

  /**
   * @brief Formats Unix timestamp to readable string.
   * @param timestamp Unix timestamp.
   * @return Formatted timestamp string.
   */
  private String formatTimestamp(long timestamp) {
    java.util.Date date = new java.util.Date(timestamp * 1000);
    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
    return sdf.format(date);
  }

  /**
   * @brief Formats Unix timestamp to date string (YYYY-MM-DD).
   * @param timestamp Unix timestamp in seconds (since January 1, 1970 UTC).
   * @return Formatted date string in "yyyy-MM-dd" format (e.g., "2024-01-15").
   * @details Converts a Unix timestamp to a formatted date string using SimpleDateFormat.
   *          Only includes the date portion, not the time.
   * @note The timestamp is multiplied by 1000 to convert from seconds to milliseconds.
   * @see #formatTimestamp(long)
   */
  private String formatDate(long timestamp) {
    java.util.Date date = new java.util.Date(timestamp * 1000);
    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
    return sdf.format(date);
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
    System.out.print("\nPress Enter to continue...");

    try {
      scanner.nextLine();
    } catch (Exception e) {
      // Ignore
    }
  }

  /**
   * @brief Closes the scanner resource and performs cleanup.
   * @details Closes the Scanner instance if it is not null. This method should be
   *          called when the ProductivityReports instance is no longer needed to
   *          ensure proper resource cleanup.
   * @note This method is idempotent - it can be called multiple times safely.
   * @note Does not save any data - only closes the scanner resource.
   */
  public void close() {
    if (scanner != null) {
      scanner.close();
    }
  }

  /**
   * @class Report
   * @brief Represents a productivity report for a category.
   * @details This inner class encapsulates all statistics and data for a productivity
   *          report grouped by category. Contains aggregated information including
   *          total activities, total time, time range, and the list of activities
   *          in the category.
   * @author efil.saylam.nese.sarp
   */
  private static class Report {
    /** @brief Category name for this report. Used as the key in B+ Tree. */
    String category;

    /** @brief Total number of activities in this category. */
    int activityCount;

    /** @brief Total time spent in minutes across all activities in this category. */
    int totalTime;

    /** @brief Earliest timestamp (Unix seconds) among activities in this category. */
    long earliestTime;

    /** @brief Latest timestamp (Unix seconds) among activities in this category. */
    long latestTime;

    /** @brief List of all Activity objects belonging to this category. */
    List<Activity> activities;

    /**
     * @brief Constructor for Report class.
     * @param category Category name for grouping activities. Must not be null.
     * @param activityCount Number of activities in this category. Must be non-negative.
     * @param totalTime Total time in minutes spent on activities in this category. Must be non-negative.
     * @param earliestTime Earliest Unix timestamp among activities. Must be valid timestamp.
     * @param latestTime Latest Unix timestamp among activities. Must be >= earliestTime.
     * @param activities List of Activity objects in this category. Must not be null.
     * @note All parameters are required. The list should not be modified after construction.
     */
    Report(String category, int activityCount, int totalTime,
           long earliestTime, long latestTime, List<Activity> activities) {
      this.category = category;
      this.activityCount = activityCount;
      this.totalTime = totalTime;
      this.earliestTime = earliestTime;
      this.latestTime = latestTime;
      this.activities = activities;
    }
  }

  /**
   * @class BPlusTree
   * @brief Simplified B+ Tree implementation for storing reports by category.
   * @details This inner class implements a simplified B+ Tree data structure for
   *          efficient storage and retrieval of reports. The B+ Tree maintains
   *          sorted order by category name and provides efficient range queries.
   *          The implementation uses a simplified structure with leaf nodes only
   *          for demonstration purposes.
   * @note This is a simplified implementation. A full B+ Tree would include
   *       internal nodes for better performance with large datasets.
   * @author efil.saylam.nese.sarp
   */
  private static class BPlusTree {
    /** @brief Order of the B+ Tree (maximum number of keys per node). Currently set to 3. */
    private int order;

    /** @brief Root node of the B+ Tree. Initially a leaf node. */
    private BPlusNode root;

    /**
     * @brief Constructor for BPlusTree class.
     * @param order Maximum number of keys per node. Currently fixed at 3.
     * @details Initializes an empty B+ Tree with the specified order. The root
     *          is initialized as an empty leaf node.
     * @note The order parameter is currently not fully utilized in this simplified implementation.
     */
    BPlusTree(int order) {
      this.order = order;
      this.root = new BPlusLeafNode();
    }

    /**
     * @brief Inserts a key-value pair into the B+ Tree.
     * @param key Category name to use as the key. Must not be null.
     * @param value Report object to associate with the key. Must not be null.
     * @details Delegates insertion to the root node, which handles the actual
     *          insertion logic. The root may be replaced if tree restructuring occurs.
     * @note Keys are automatically sorted within nodes.
     */
    void insert(String key, Report value) {
      root = root.insert(key, value);
    }

    /**
     * @brief Checks if the B+ Tree is empty.
     * @return true if the tree contains no reports, false otherwise.
     * @details Delegates to the root node's isEmpty() method.
     */
    boolean isEmpty() {
      return root.isEmpty();
    }

    /**
     * @brief Retrieves all reports from the B+ Tree in sorted order.
     * @return List of all Report objects stored in the tree, sorted by category name.
     * @details Delegates to the root node's getAllValues() method which performs
     *          an in-order traversal to collect all values.
     * @note Returns an empty list if the tree is empty.
     */
    List<Report> getAllReports() {
      return root.getAllValues();
    }
  }

  /**
   * @class BPlusNode
   * @brief Abstract base class for B+ Tree nodes.
   * @details Defines the common interface for all B+ Tree nodes. This abstract
   *          class ensures that all node types implement the required operations
   *          for insertion, emptiness checking, and value retrieval.
   * @author efil.saylam.nese.sarp
   */
  private abstract static class BPlusNode {
    /**
     * @brief Inserts a key-value pair into the node or subtree.
     * @param key Category name to use as the key. Must not be null.
     * @param value Report object to associate with the key. Must not be null.
     * @return The root node of the (possibly restructured) tree after insertion.
     * @details This abstract method must be implemented by concrete node types.
     *          The implementation handles insertion logic specific to the node type.
     */
    abstract BPlusNode insert(String key, Report value);

    /**
     * @brief Checks if the node or subtree is empty.
     * @return true if no data is stored, false otherwise.
     * @details This abstract method must be implemented by concrete node types.
     */
    abstract boolean isEmpty();

    /**
     * @brief Retrieves all values stored in the node or subtree.
     * @return List of all Report objects in sorted order by category name.
     * @details This abstract method must be implemented by concrete node types.
     *          For leaf nodes, returns stored values. For internal nodes, traverses children.
     */
    abstract List<Report> getAllValues();
  }

  /**
   * @class BPlusLeafNode
   * @brief Leaf node implementation for B+ Tree.
   * @details This class implements a leaf node in the B+ Tree structure. Leaf nodes
   *          store key-value pairs (category name -> Report) and maintain them in
   *          sorted order. This simplified implementation uses a list that is sorted
   *          after each insertion.
   * @note In a full B+ Tree implementation, leaf nodes would be linked together
   *       for efficient range scans.
   * @author efil.saylam.nese.sarp
   */
  private static class BPlusLeafNode extends BPlusNode {
    /** @brief List of key-value pairs stored in this leaf node. Maintained in sorted order by key. */
    private List<Map.Entry<String, Report>> entries;

    /**
     * @brief Constructor for BPlusLeafNode class.
     * @details Initializes an empty leaf node with an empty entries list.
     */
    BPlusLeafNode() {
      this.entries = new ArrayList<>();
    }

    /**
     * @brief Inserts a key-value pair into this leaf node.
     * @param key Category name to use as the key. Must not be null.
     * @param value Report object to associate with the key. Must not be null.
     * @return This node (unchanged) as leaf nodes don't require restructuring in this simplified implementation.
     * @details Adds the key-value pair to the entries list and sorts the list
     *          to maintain sorted order by category name.
     * @note Time complexity: O(n log n) due to sorting, where n is the number of entries.
     */
    @Override
    BPlusNode insert(String key, Report value) {
      entries.add(new java.util.AbstractMap.SimpleEntry<>(key, value));
      // Sort by key
      entries.sort(Comparator.comparing(Map.Entry::getKey));
      return this;
    }

    /**
     * @brief Checks if this leaf node is empty.
     * @return true if the entries list is empty, false otherwise.
     */
    @Override
    boolean isEmpty() {
      return entries.isEmpty();
    }

    /**
     * @brief Retrieves all report values from this leaf node.
     * @return List of all Report objects stored in this node, in sorted order by category name.
     * @details Extracts all Report objects from the entries list and returns them
     *          in a new list. The order matches the sorted order of entries.
     */
    @Override
    List<Report> getAllValues() {
      List<Report> reports = new ArrayList<>();

      for (Map.Entry<String, Report> entry : entries) {
        reports.add(entry.getValue());
      }

      return reports;
    }
  }

  /**
   * @class HuffmanNode
   * @brief Node for Huffman encoding tree.
   * @details Represents a node in the Huffman binary tree used for encoding.
   *          Leaf nodes contain characters and their frequencies, while internal
   *          nodes contain only frequencies and pointers to left and right children.
   *          The tree structure enables efficient encoding and decoding of data.
   * @author efil.saylam.nese.sarp
   */
  private static class HuffmanNode {
    /** @brief Character stored in this node. '\0' for internal nodes. */
    char character;

    /** @brief Frequency of the character (for leaf nodes) or sum of child frequencies (for internal nodes). */
    int frequency;

    /** @brief Left child node in the Huffman tree. Null for leaf nodes. */
    HuffmanNode left;

    /** @brief Right child node in the Huffman tree. Null for leaf nodes. */
    HuffmanNode right;

    /**
     * @brief Constructor for leaf nodes in the Huffman tree.
     * @param character The character this node represents. Must be a valid character.
     * @param frequency Frequency count of this character in the input data. Must be positive.
     * @details Creates a leaf node with no children. Used for individual characters
     *          during initial tree construction.
     */
    HuffmanNode(char character, int frequency) {
      this.character = character;
      this.frequency = frequency;
      this.left = null;
      this.right = null;
    }

    /**
     * @brief Constructor for internal nodes in the Huffman tree.
     * @param frequency Sum of frequencies from left and right child nodes. Must be positive.
     * @param left Left child node. Must not be null.
     * @param right Right child node. Must not be null.
     * @details Creates an internal node that combines two subtrees. The character
     *          is set to '\0' to indicate this is not a leaf node.
     */
    HuffmanNode(int frequency, HuffmanNode left, HuffmanNode right) {
      this.character = '\0';
      this.frequency = frequency;
      this.left = left;
      this.right = right;
    }

    /**
     * @brief Checks if this node is a leaf node.
     * @return true if both left and right children are null, false otherwise.
     * @details Leaf nodes have no children and represent actual characters in the encoding.
     */
    boolean isLeaf() {
      return left == null && right == null;
    }
  }

  /**
   * @class HuffmanEncoder
   * @brief Huffman encoding implementation for data compression.
   * @details This class implements the Huffman encoding algorithm for lossless data
   *          compression. It builds a Huffman tree based on character frequencies,
   *          generates variable-length prefix codes, and encodes data using these codes.
   *          More frequent characters get shorter codes, resulting in optimal compression.
   * @note Huffman encoding provides optimal prefix codes for a given character frequency distribution.
   * @author efil.saylam.nese.sarp
   */
  private static class HuffmanEncoder {
    /** @brief Mapping from characters to their Huffman-encoded binary strings. */
    private Map<Character, String> encodingTable;

    /**
     * @brief Constructor for HuffmanEncoder class.
     * @details Initializes an empty encoding table. The table will be populated
     *          during the encoding process when generateCodes() is called.
     */
    HuffmanEncoder() {
      this.encodingTable = new HashMap<>();
    }

    /**
     * @brief Encodes input data using Huffman encoding algorithm.
     * @param data The string data to encode. Must not be null.
     * @return Encoded binary string where each character is replaced by its Huffman code.
     * @details Implements the complete Huffman encoding process: (1) Counts character
     *          frequencies, (2) Builds a Huffman tree using a priority queue, (3) Generates
     *          encoding table by traversing the tree, (4) Encodes the data using the table.
     *          Time complexity: O(n log k) where n is data length and k is unique character count.
     * @note Empty input strings result in empty encoded output.
     * @see #generateCodes(HuffmanNode, String, Map)
     */
    String encode(String data) {
      // Count frequencies
      Map<Character, Integer> frequencies = new HashMap<>();

      for (char ch : data.toCharArray()) {
        frequencies.put(ch, frequencies.getOrDefault(ch, 0) + 1);
      }

      // Build Huffman tree
      PriorityQueue<HuffmanNode> pq = new PriorityQueue<>(
        Comparator.comparingInt(n -> n.frequency));

      for (Map.Entry<Character, Integer> entry : frequencies.entrySet()) {
        pq.offer(new HuffmanNode(entry.getKey(), entry.getValue()));
      }

      // Build tree
      while (pq.size() > 1) {
        HuffmanNode left = pq.poll();
        HuffmanNode right = pq.poll();
        HuffmanNode parent = new HuffmanNode(
          left.frequency + right.frequency, left, right);
        pq.offer(parent);
      }

      HuffmanNode root = pq.poll();
      // Generate encoding table
      generateCodes(root, "", encodingTable);
      // Encode data
      StringBuilder encoded = new StringBuilder();

      for (char ch : data.toCharArray()) {
        encoded.append(encodingTable.get(ch));
      }

      return encoded.toString();
    }

    /**
     * @brief Recursively generates Huffman codes by traversing the tree.
     * @param node Current node in the Huffman tree. Must not be null.
     * @param code Binary code string accumulated so far. Empty string for root.
     * @param table Map to store character-to-code mappings. Must not be null.
     * @details Performs a depth-first traversal of the Huffman tree. For each leaf node,
     *          stores the accumulated code in the encoding table. For internal nodes,
     *          recursively traverses left (appending '0') and right (appending '1') children.
     *          Handles special case where tree has only one node.
     * @note This is a recursive helper method called by encode().
     */
    private void generateCodes(HuffmanNode node, String code, Map<Character, String> table) {
      if (node.isLeaf()) {
        if (!code.isEmpty()) {
          table.put(node.character, code);
        } else {
          // Special case: only one character
          table.put(node.character, "0");
        }
      } else {
        if (node.left != null) {
          generateCodes(node.left, code + "0", table);
        }

        if (node.right != null) {
          generateCodes(node.right, code + "1", table);
        }
      }
    }

    /**
     * @brief Retrieves the encoding table mapping characters to codes.
     * @return Map containing character-to-Huffman-code mappings. Returns empty map if not yet encoded.
     * @details Returns the encoding table that was generated during the encoding process.
     *          This table can be used for decoding or displaying encoding information.
     * @note The returned map is the actual internal map, not a copy. Modifications will affect the encoder.
     */
    Map<Character, String> getEncodingTable() {
      return encodingTable;
    }
  }

  /**
   * @class StronglyConnectedComponents
   * @brief Tarjan's algorithm implementation for finding strongly connected components.
   * @details This class implements Tarjan's algorithm to find all strongly connected
   *          components (SCCs) in a directed graph. An SCC is a maximal set of vertices
   *          where every vertex is reachable from every other vertex. The algorithm
   *          uses depth-first search with tracking of discovery times and low-link values.
   * @note Time complexity: O(V + E) where V is vertices and E is edges.
   * @note Space complexity: O(V) for the stack and tracking arrays.
   * @author efil.saylam.nese.sarp
   */
  private static class StronglyConnectedComponents {
    /** @brief Adjacency list representation of the activity graph. */
    private List<List<Integer>> graph;

    /** @brief List of all activities for reference during component analysis. */
    private List<Activity> activities;

    /** @brief Current discovery time index used in Tarjan's algorithm. */
    private int index;

    /** @brief Discovery time indices for each vertex. -1 means not yet discovered. */
    private int[] indices;

    /** @brief Low-link values for each vertex in Tarjan's algorithm. */
    private int[] lowlinks;

    /** @brief Tracks which vertices are currently on the recursion stack. */
    private boolean[] onStack;

    /** @brief Stack used to track vertices during depth-first search. */
    private Stack<Integer> stack;

    /** @brief List of strongly connected components, each containing vertex indices. */
    private List<List<Integer>> components;

    /**
     * @brief Constructor for StronglyConnectedComponents class.
     * @param graph Adjacency list representation of the directed graph. Must not be null.
     * @param activities List of Activity objects corresponding to graph vertices. Must not be null.
     * @details Initializes all data structures needed for Tarjan's algorithm. Sets all
     *          indices to -1 (undiscovered) and initializes empty stack and components list.
     * @note The graph size must match the activities list size.
     */
    StronglyConnectedComponents(List<List<Integer>> graph, List<Activity> activities) {
      this.graph = graph;
      this.activities = activities;
      this.index = 0;
      this.indices = new int[graph.size()];
      this.lowlinks = new int[graph.size()];
      this.onStack = new boolean[graph.size()];
      this.stack = new Stack<>();
      this.components = new ArrayList<>();

      for (int i = 0; i < graph.size(); i++) {
        indices[i] = -1;
      }
    }

    /**
     * @brief Finds all strongly connected components using Tarjan's algorithm.
     * @return List of strongly connected components, where each component is a list of vertex indices.
     * @details Implements Tarjan's algorithm to find all SCCs in the graph. Iterates through
     *          all vertices and calls strongConnect() for each undiscovered vertex. Time complexity:
     *          O(V + E) where V is vertices and E is edges.
     * @note Returns an empty list if the graph is empty.
     * @see #strongConnect(int)
     */
    List<List<Integer>> findSCC() {
      for (int i = 0; i < graph.size(); i++) {
        if (indices[i] == -1) {
          strongConnect(i);
        }
      }

      return components;
    }

    /**
     * @brief Recursive helper method for Tarjan's algorithm to find SCCs.
     * @param v Current vertex index being processed. Must be a valid vertex index.
     * @details Recursively processes vertex v and all its descendants. Sets discovery
     *          time, low-link value, and pushes vertex onto stack. For each neighbor,
     *          recursively processes if undiscovered, or updates low-link if on stack.
     *          When low-link equals discovery time, a new SCC is found and all vertices
     *          on stack up to v are popped to form the component.
     * @note This is the core recursive function of Tarjan's algorithm.
     * @note Time complexity: O(V + E) for the entire graph when called from findSCC().
     */
    private void strongConnect(int v) {
      indices[v] = index;
      lowlinks[v] = index;
      index++;
      stack.push(v);
      onStack[v] = true;

      for (int w : graph.get(v)) {
        if (indices[w] == -1) {
          strongConnect(w);
          lowlinks[v] = Math.min(lowlinks[v], lowlinks[w]);
        } else if (onStack[w]) {
          lowlinks[v] = Math.min(lowlinks[v], indices[w]);
        }
      }

      if (lowlinks[v] == indices[v]) {
        List<Integer> component = new ArrayList<>();
        int w;

        do {
          w = stack.pop();
          onStack[w] = false;
          component.add(w);
        } while (w != v);

        components.add(component);
      }
    }
  }
}

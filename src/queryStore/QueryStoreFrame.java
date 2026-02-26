package querystore;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class QueryStoreFrame extends JFrame {

    private RSyntaxTextArea queryTextArea;
    private JTextField queryNameField;
    private JList<UserQuery> queryList;
    private DefaultListModel<UserQuery> queryListModel;
    private JLabel statusLabel;
    private JLabel usernameLabel;
    private JTextField searchKeywordField; // New: Search field
    private QueryFileManager fileManager;
    private String loggedInUsername;

    public QueryStoreFrame(String username) {
        this.loggedInUsername = username;
        setTitle("SQL Query Store - User: " + loggedInUsername);
//        setSize(1100, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Store queries in a 'queries' subfolder within the stable app storage path
        String storagePath = QueryStore.getBaseStoragePath() + java.io.File.separator + "queries";
        fileManager = new QueryFileManager(storagePath, loggedInUsername); 

        initComponents();
        loadQueryList(); // Load initial list of queries (all of them)

        setVisible(true);
    }

    private void initComponents() {
        // Define fonts
        Font uiFont = new Font("SansSerif", Font.PLAIN, 16);
        Font titleFont = new Font("SansSerif", Font.BOLD, 16);

        // Main panel with BorderLayout and a 20px vertical gap
        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        add(mainPanel);

        // --- Controls Panel (Left side of NORTH) ---
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));

        // Top row
        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        JLabel queryNameLabel = new JLabel("Query Name:");
        queryNameLabel.setFont(uiFont);
        topRow.add(queryNameLabel);
        
        queryNameField = new JTextField(35);
        queryNameField.setFont(uiFont);
        queryNameField.setPreferredSize(new Dimension(queryNameField.getPreferredSize().width, 40));
        queryNameField.putClientProperty("JTextField.placeholderText", "Enter a name for your query...");
        topRow.add(queryNameField);

        JButton saveButton = new JButton("Save Query");
        saveButton.setFont(uiFont);
        saveButton.putClientProperty("JButton.buttonType", "roundRect");
        saveButton.setBackground(new Color(0, 120, 215));
        saveButton.setForeground(Color.WHITE);
        saveButton.addActionListener(e -> saveQuery());
        topRow.add(saveButton);
        
        controlPanel.add(topRow);

        // --- User Info Panel (Right side of NORTH) ---
        JPanel userInfoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        
        usernameLabel = new JLabel("Logged in as: " + loggedInUsername);
        usernameLabel.setFont(uiFont);
        userInfoPanel.add(usernameLabel);
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(uiFont);
        logoutButton.putClientProperty("JButton.buttonType", "roundRect");
        logoutButton.addActionListener(e -> logout());
        userInfoPanel.add(logoutButton);
        
        // --- North Panel Wrapper (combines controls and user info) ---
        JPanel northPanelWrapper = new JPanel(new BorderLayout());
        northPanelWrapper.setBorder(BorderFactory.createEmptyBorder(15, 10, 0, 10)); // Added 15px top gap and 10px side padding
        northPanelWrapper.add(controlPanel, BorderLayout.WEST);
        northPanelWrapper.add(userInfoPanel, BorderLayout.EAST);

        mainPanel.add(northPanelWrapper, BorderLayout.NORTH);

        // --- RSyntaxTextArea for SQL Syntax Highlighting ---
        queryTextArea = new RSyntaxTextArea(25, 80);
        queryTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_SQL);
        queryTextArea.setCodeFoldingEnabled(true);
        queryTextArea.setLineWrap(true);
        queryTextArea.setWrapStyleWord(true);
        queryTextArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
        
        // Load a Light Theme for the editor
        try {
            Theme theme = Theme.load(getClass().getResourceAsStream(
                "/org/fife/ui/rsyntaxtextarea/themes/default.xml"));
            theme.apply(queryTextArea);
            
            // Explicitly override the theme font to 16px
            Font font = new Font("Monospaced", Font.PLAIN, 16);
            queryTextArea.setFont(font);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        RTextScrollPane queryScrollPane = new RTextScrollPane(queryTextArea);
        queryScrollPane.setLineNumbersEnabled(true);
        queryScrollPane.setFoldIndicatorEnabled(true);
        // Create a TitledBorder with the new font
        queryScrollPane.setBorder(BorderFactory.createTitledBorder(null, "SQL Query Editor", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, titleFont));


        // Copy Query Button
        JButton copyQueryButton = new JButton("Copy Query");
        copyQueryButton.setFont(uiFont);
        copyQueryButton.putClientProperty("JButton.buttonType", "roundRect");
        copyQueryButton.addActionListener(e -> {
            StringSelection stringSelection = new StringSelection(queryTextArea.getText());
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
            statusLabel.setText("SQL Query copied to clipboard.");
        });

        // Panel to hold query text area and copy button
        JPanel queryAreaPanel = new JPanel(new BorderLayout());
        queryAreaPanel.add(queryScrollPane, BorderLayout.CENTER);
        queryAreaPanel.add(copyQueryButton, BorderLayout.SOUTH);


        // Right side panel for Search and Query List
        JPanel searchAndListPanel = new JPanel(new BorderLayout());
        // Create a TitledBorder with the new font
        searchAndListPanel.setBorder(BorderFactory.createTitledBorder(null, "Stored Queries", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, titleFont));


        // Search elements panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        JLabel searchLabel = new JLabel("Search Keyword:");
        searchLabel.setFont(uiFont);
        searchPanel.add(searchLabel);

        searchKeywordField = new JTextField(15);
        searchKeywordField.setFont(uiFont);
        searchKeywordField.putClientProperty("JTextField.placeholderText", "Search...");
        searchPanel.add(searchKeywordField);

        JButton searchButton = new JButton("Search");
        searchButton.setFont(uiFont);
        searchButton.putClientProperty("JButton.buttonType", "roundRect");
        searchButton.addActionListener(e -> searchQueries());
        searchPanel.add(searchButton);
        searchAndListPanel.add(searchPanel, BorderLayout.NORTH);

        // Query list
        queryListModel = new DefaultListModel<>();
        queryList = new JList<>(queryListModel);
        queryList.setFont(new Font("SansSerif", Font.BOLD, 14)); 
        queryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        queryList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && queryList.getSelectedValue() != null) {
                loadSelectedQuery(queryList.getSelectedValue());
            }
        });
        JScrollPane listScrollPane = new JScrollPane(queryList);
        searchAndListPanel.add(listScrollPane, BorderLayout.CENTER);
        
        // Buttons for query list
        JPanel listButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        JButton deleteButton = new JButton("Delete Query");
        deleteButton.setFont(uiFont);
        deleteButton.putClientProperty("JButton.buttonType", "roundRect");
        deleteButton.setForeground(new Color(200, 0, 0)); // Red for delete
        deleteButton.addActionListener(e -> deleteQuery());
        listButtonsPanel.add(deleteButton);

        JButton refreshButton = new JButton("Refresh List");
        refreshButton.setFont(uiFont);
        refreshButton.putClientProperty("JButton.buttonType", "roundRect");
        refreshButton.addActionListener(e -> loadQueryList());
        listButtonsPanel.add(refreshButton);
        searchAndListPanel.add(listButtonsPanel, BorderLayout.SOUTH);

        // JSplitPane to get the 2/3 - 1/3 split
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, queryAreaPanel, searchAndListPanel); // Use queryAreaPanel here
        splitPane.setResizeWeight(0.66); // Give 2/3 of the space to the left component

        mainPanel.add(splitPane, BorderLayout.CENTER);

        // Status bar
        statusLabel = new JLabel("Ready");
        statusLabel.setFont(uiFont);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        mainPanel.add(statusLabel, BorderLayout.SOUTH);
    }

    private void saveQuery() {
        String queryName = queryNameField.getText().trim();
        String queryContent = queryTextArea.getText();

        if (queryName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a query name.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (queryContent.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Query content cannot be empty.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2-factor surety: Check if query exists and ask for confirmation to overwrite
        if (fileManager.queryExists(queryName)) {
            int response = JOptionPane.showConfirmDialog(this, 
                                "A query named '" + queryName + "' already exists. Do you want to overwrite it?", 
                                "Overwrite Existing Query?", 
                                JOptionPane.YES_NO_OPTION, 
                                JOptionPane.WARNING_MESSAGE);
            if (response == JOptionPane.NO_OPTION) {
                statusLabel.setText("Save operation cancelled.");
                return; // User chose not to overwrite
            }
        }

        try {
            fileManager.saveQuery(queryName, queryContent);
            statusLabel.setText("Query '" + queryName + "' saved successfully.");
            queryTextArea.setText(""); // Clear text area after saving
            queryNameField.setText(""); // Clear name field
            loadQueryList(); // Refresh the list of queries
        } catch (IOException e) {
            statusLabel.setText("Error saving query: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error saving query: " + e.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void deleteQuery() {
        UserQuery selectedQuery = queryList.getSelectedValue();
        if (selectedQuery == null) {
            JOptionPane.showMessageDialog(this, "Please select a query to delete.", "No Query Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 1st confirmation
        int response1 = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete the query '" + selectedQuery.getTitle() + "'?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (response1 == JOptionPane.YES_OPTION) {
            try {
                fileManager.deleteQuery(selectedQuery.getId());
                statusLabel.setText("Query '" + selectedQuery.getTitle() + "' deleted successfully.");
                queryTextArea.setText(""); // Clear text area
                queryNameField.setText(""); // Clear name field
                loadQueryList(); // Refresh the list
            } catch (IOException e) {
                statusLabel.setText("Error deleting query: " + e.getMessage());
                JOptionPane.showMessageDialog(this, "Error deleting query: " + e.getMessage(), "Delete Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void loadSelectedQuery(UserQuery query) {
        if (query == null) {
            return;
        }
        queryNameField.setText(query.getTitle()); 
        queryTextArea.setText(query.getContent());
        statusLabel.setText("Query '" + query.getTitle() + "' loaded successfully.");
    }

    private void loadQueryList() {
        queryListModel.clear();
        try {
            List<UserQuery> queries = fileManager.listQueries();
            if (queries.isEmpty()) {
                statusLabel.setText("No stored queries found.");
            } else {
                for (UserQuery query : queries) {
                    queryListModel.addElement(query);
                }
                statusLabel.setText(queries.size() + " queries loaded for " + loggedInUsername + ".");
            }
        } catch (IOException e) {
            statusLabel.setText("Error loading query list: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error loading query list: " + e.getMessage(), "Load Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void logout() {
        dispose(); // Close the current QueryStoreFrame
        SwingUtilities.invokeLater(() -> new AuthFrame()); // Open a new AuthFrame
    }

    private void searchQueries() {
        String keyword = searchKeywordField.getText().trim().toLowerCase();
        queryListModel.clear();
        if (keyword.isEmpty()) {
            loadQueryList(); // If search field is empty, show all queries
            return;
        }

        try {
            List<UserQuery> allQueries = fileManager.listQueries();
            List<UserQuery> filtered = allQueries.stream()
                    .filter(q -> q.getTitle().toLowerCase().contains(keyword))
                    .toList();
            
            if (filtered.isEmpty()) {
                statusLabel.setText("No queries found matching '" + keyword + "'.");
            } else {
                for (UserQuery query : filtered) {
                    queryListModel.addElement(query);
                }
                statusLabel.setText(filtered.size() + " queries found matching '" + keyword + "'.");
            }
        } catch (IOException e) {
            statusLabel.setText("Error searching: " + e.getMessage());
        }
    }
}

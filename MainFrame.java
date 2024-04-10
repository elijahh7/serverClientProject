import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Class: MainFrame
 * Author: Elijah Hutchison
 * Course: CEN-3024C-24667
 * Date: 4/6/2024
 *
 * Description:
 * This class represents the main frame of the library management system.
 * It contains buttons for various operations such as removing books, checking in/out books,
 * refreshing the library contents, and exiting the application.
 */
public class MainFrame extends JFrame {
    private JPanel mainPanel;
    private JButton removeWithBarcodeButton;
    private JButton removeWithTitleButton;
    private JButton checkoutBookButton;
    private JButton checkInBookButton;
    private JButton exitButton;
    private JButton refreshButton;
    private JTextArea libraryContentsTextArea;

    public MainFrame() {
        setContentPane(mainPanel);
        setSize(450, 300);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);

        // Call refreshLibraryContents to display library contents initially
        refreshLibraryContents();

        removeWithBarcodeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String barcode = JOptionPane.showInputDialog("Enter barcode:");
                if (barcode != null && !barcode.isEmpty()) {
                    removeBookByBarcode(barcode);
                } else {
                    JOptionPane.showMessageDialog(null, "Barcode cannot be empty.");
                }
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshLibraryContents();
            }
        });
        removeWithTitleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String title = JOptionPane.showInputDialog("Enter title:");
                if (title != null && !title.isEmpty()) {
                    removeBookByTitle(title);
                } else {
                    JOptionPane.showMessageDialog(null, "Title cannot be empty.");
                }
            }
        });

        checkoutBookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String title = JOptionPane.showInputDialog("Enter title to checkout:");
                if (title != null && !title.isEmpty()) {
                    checkoutBook(title);
                } else {
                    JOptionPane.showMessageDialog(null, "Title cannot be empty.");
                }
            }
        });

        checkInBookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String title = JOptionPane.showInputDialog("Enter title to check in:");
                if (title != null && !title.isEmpty()) {
                    checkInBook(title);
                } else {
                    JOptionPane.showMessageDialog(null, "Title cannot be empty.");
                }
            }
        });
    }

    /**
     * Method: removeBookByBarcode
     * Description: Removes a book from the library by its barcode.
     * Arguments:
     * - barcode: The barcode of the book to be removed
     */
    private void removeBookByBarcode(String barcode) {
        String url = "jdbc:sqlite:C:/Users/purpl/Downloads/sqlite3/gui/LibraryDatabase1";
        String userName = "ElijahHutchison";
        String password = "Thor22";
        String deleteQuery = "DELETE FROM Book WHERE barcode = ?";

        try {
            Class.forName("org.sqlite.JDBC");
            try (Connection connection = DriverManager.getConnection(url, userName, password);
                 PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {

                preparedStatement.setString(1, barcode);
                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(null, "Book with barcode " + barcode + " removed successfully.");
                    // Refresh the library contents after removing the book
                    refreshLibraryContents();
                } else {
                    JOptionPane.showMessageDialog(null, "Book with barcode " + barcode + " not found.");
                }
            }
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "An error occurred while removing the book.");
        }
    }

    /**
     * Method: removeBookByTitle
     * Description: Removes a book from the library by its title.
     * Arguments:
     * - title: The title of the book to be removed
     */
    private void removeBookByTitle(String title) {
        String url = "jdbc:sqlite:C:/Users/purpl/Downloads/sqlite3/gui/LibraryDatabase1";
        String userName = "ElijahHutchison";
        String password = "Thor22";
        String deleteQuery = "DELETE FROM Book WHERE Title = ?";

        try {
            Class.forName("org.sqlite.JDBC");
            try (Connection connection = DriverManager.getConnection(url, userName, password);
                 PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {

                preparedStatement.setString(1, title);
                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(null, "Book with title " + title + " removed successfully.");
                    // Refresh the library contents after removing the book
                    refreshLibraryContents();
                } else {
                    JOptionPane.showMessageDialog(null, "Book with title " + title + " not found.");
                }
            }
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "An error occurred while removing the book.");
        }
    }

    /**
     * Method: checkoutBook
     * Description: Checks out a book from the library.
     * Arguments:
     * - title: The title of the book to be checked out
     */
    private void checkoutBook(String title) {
        String url = "jdbc:sqlite:C:/Users/purpl/Downloads/sqlite3/gui/LibraryDatabase1";
        String userName = "ElijahHutchison";
        String password = "Thor22";

        // Check if the book is already checked out
        if (isBookCheckedOut(title)) {
            JOptionPane.showMessageDialog(null, "Book with title " + title + " is already checked out.");
            return;
        }

        // Get today's date
        LocalDate currentDate = LocalDate.now();
        // Calculate the due date (four weeks from today)
        LocalDate dueDate = currentDate.plusWeeks(4);
        // Format the due date as a string
        String formattedDueDate = dueDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String updateQuery = "UPDATE Book SET Status = 'checked_out', dueDate = null WHERE Title = ?"; // Update query for checking out the book

        try {
            Class.forName("org.sqlite.JDBC");
            try (Connection connection = DriverManager.getConnection(url, userName, password);
                 PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

                preparedStatement.setString(1, title);
                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(null, "Book with title " + title + " checked out successfully.");
                    // Refresh the library contents after checking out the book
                    refreshLibraryContents();
                } else {
                    JOptionPane.showMessageDialog(null, "Book with title " + title + " not found.");
                }
            }
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "An error occurred while checking out the book.");
        }
    }

    // Helper method to check if the book is already checked out
    private boolean isBookCheckedOut(String title) {
        String url = "jdbc:sqlite:C:/Users/purpl/Downloads/sqlite3/gui/LibraryDatabase1";
        String userName = "ElijahHutchison";
        String password = "Thor22";
        String checkQuery = "SELECT Status FROM Book WHERE Title = ?";

        try {
            Class.forName("org.sqlite.JDBC");
            try (Connection connection = DriverManager.getConnection(url, userName, password);
                 PreparedStatement preparedStatement = connection.prepareStatement(checkQuery)) {

                preparedStatement.setString(1, title);
                ResultSet resultSet = preparedStatement.executeQuery();

                // If the query returns a result, check if the book is checked out
                if (resultSet.next()) {
                    String status = resultSet.getString("Status");
                    return status.equals("checked_out");
                }
            }
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "An error occurred while checking the book status.");
        }

        // If an error occurs or the book is not found, return false
        return false;
    }


    private void checkInBook(String title) {
        String url = "jdbc:sqlite:C:/Users/purpl/Downloads/sqlite3/gui/LibraryDatabase1";
        String userName = "ElijahHutchison";
        String password = "Thor22";
        String updateQuery = "UPDATE Book SET Status = 'checked_in', dueDate = null WHERE Title = ?"; // Update query for checking in the book

        try {
            Class.forName("org.sqlite.JDBC");
            try (Connection connection = DriverManager.getConnection(url, userName, password);
                 PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

                preparedStatement.setString(1, title);
                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(null, "Book with title " + title + " checked in successfully.");
                    // Refresh the library contents after checking in the book
                    refreshLibraryContents();
                } else {
                    JOptionPane.showMessageDialog(null, "Book with title " + title + " not found.");
                }
            }
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "An error occurred while checking in the book.");
        }
    }

    private void refreshLibraryContents() {
        String url = "jdbc:sqlite:C:/Users/purpl/Downloads/sqlite3/gui/LibraryDatabase1";
        String userName = "ElijahHutchison";
        String password = "Thor22";
        String query = "SELECT * FROM Book";

        StringBuilder libraryContents = new StringBuilder();
        try {
            // Load the SQLite JDBC driver (optional for JDBC 4.0 and later)
            Class.forName("org.sqlite.JDBC");

            // Establish connection to the SQLite database
            try (Connection connection = DriverManager.getConnection(url, userName, password);
                 Statement statement = connection.createStatement();
                 ResultSet result = statement.executeQuery(query)) {

                // Iterate over the ResultSet to retrieve data
                while (result.next()) {
                    int columnCount = result.getMetaData().getColumnCount();
                    for (int x = 1; x <= columnCount; x++) {
                        libraryContents.append(result.getString(x)).append(", ");
                    }
                    libraryContents.append("\n"); // Add new line after each record
                }
                // Set the retrieved data to the JTextArea
                libraryContentsTextArea.setText(libraryContents.toString());
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "An error occurred while refreshing the library contents.");
        }
    }

    public static void main(String[] args) {
        MainFrame myFrame = new MainFrame();

        String url = "jdbc:sqlite:C:/Users/purpl/Downloads/sqlite3/gui/LibraryDatabase1";
        String userName = "ElijahHutchison";
        String password = "Thor22";
        String query = "SELECT * FROM Book";

        try {
            // Load the SQLite JDBC driver (optional for JDBC 4.0 and later)
            Class.forName("org.sqlite.JDBC");

            // Establish connection to the SQLite database
            try (Connection connection = DriverManager.getConnection(url, userName, password);
                 Statement statement = connection.createStatement();
                 ResultSet result = statement.executeQuery(query)) {

                // Iterate over the ResultSet to retrieve data
                while (result.next()) {
                    StringBuilder libraryContents = new StringBuilder();
                    int columnCount = result.getMetaData().getColumnCount();
                    for (int x = 1; x <= columnCount; x++) {
                        libraryContents.append(result.getString(x)).append(", ");
                    }
                    System.out.println(libraryContents);
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
}

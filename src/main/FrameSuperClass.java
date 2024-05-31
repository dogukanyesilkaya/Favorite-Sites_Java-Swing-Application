import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.List;

public class FrameSuperClass extends JFrame {

    private Connection databaseConnection;


    public void SetupDatabaseConnection() {
        try {

            String connectionURL = "jdbc:mysql://localhost:3306/favoritesites?useSSL=false&allowPublicKeyRetrieval=true";
            String user = "root";
            String password = "1234";

            databaseConnection = DriverManager.getConnection(connectionURL, user, password);

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    public Connection GetDatabaseConnection(){
        return databaseConnection;
    }

    public PreparedStatement FillQueryWithInputs(String query, List<String> inputs){
        PreparedStatement preparedStatement;
        try {
            preparedStatement = databaseConnection.prepareStatement(query);

            for (int i = 1; i <= inputs.size(); i++) {
                preparedStatement.setString(i, inputs.get(i - 1));
            }

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return preparedStatement;

    }

    public void RunQueryOnce(PreparedStatement preparedStatement,String validMessage,String invalidMessage){
        int addedRow = 0;

        try {
            addedRow = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (addedRow > 0) {
            JOptionPane.showMessageDialog(null, validMessage);
        } else {
            JOptionPane.showMessageDialog(null, invalidMessage);
        }
    }

    public DefaultTableModel FillSQLDataIntoTable(PreparedStatement preparedStatement){
        DefaultTableModel tableModel;
        try {
            ResultSet resultSet = preparedStatement.executeQuery();

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            tableModel=new DefaultTableModel();
            // Add column's labels to the table model
            for (int i = 1; i <= columnCount; i++) {
                tableModel.addColumn(metaData.getColumnLabel(i));
            }

            //Fill sql data into table
            while (resultSet.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = resultSet.getObject(i);
                }
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return tableModel;
    }
}

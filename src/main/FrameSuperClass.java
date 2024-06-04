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


    public void DefaultJFrameSetup(JFrame jFrame, JPanel mainPanel, int width, int height, String frameTitle, int closeOperationIndex){
        jFrame.add(mainPanel);
        jFrame.setSize(width,height);
        jFrame.setTitle(frameTitle);
        jFrame.setDefaultCloseOperation(closeOperationIndex); //EXIT_ON_CLOSE=3 || DISPOSE_ON_CLOSE=2
        jFrame.setVisible(true);
    }

    public void RunQueryWithoutInput(String query){
        try {
            Statement statement = databaseConnection.createStatement();
            statement.executeUpdate(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public PreparedStatement FillQueryWithAnInput(String query, String input){
        PreparedStatement preparedStatement;
        try {
            preparedStatement = databaseConnection.prepareStatement(query);

            preparedStatement.setString(1, input);

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return preparedStatement;

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

    public boolean CheckEmptyInputs(List<String> inputs){
        for (String input:inputs){
            if(input.isEmpty()){
                return false;
            }
        }
        return true;
    }

    public boolean CheckUsernameValidity(String username){
        String query="SELECT * FROM userinfo WHERE username=?";
        PreparedStatement preparedStatement = FillQueryWithAnInput(query,username);

        try {
            ResultSet resultSet= preparedStatement.executeQuery();

            if (resultSet.next()) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


}

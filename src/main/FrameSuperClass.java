import javax.swing.*;
import java.sql.*;
import java.util.List;

public class FrameSuperClass extends JFrame {

    private Connection databaseConnection;


    public void SetupDatabaseConnection_new() {
        try {

            String connectionURL = "jdbc:mysql://localhost:3306/favoritesites?useSSL=false&allowPublicKeyRetrieval=true";
            String user = "root";
            String password = "1234";

            databaseConnection = DriverManager.getConnection(connectionURL, user, password);

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    public Connection GetDatabaseConnection_new(){
        return databaseConnection;
    }

    public PreparedStatement FillQueryWithInputs(String query, List<String> inputs){
        PreparedStatement preparedStatement;
        try {
            preparedStatement = databaseConnection.prepareStatement(query);

            for (int i = 1; i <= inputs.size(); i++) {
                preparedStatement.setString(1, inputs.get(i - 1));
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
}

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LoginFrame extends FrameSuperClass{

    private JPanel mainPanel;
    private JTextField usernameTField;
    private JPasswordField passwordPField;
    private JButton loginButton;
    private JLabel passwordLabel;
    private JLabel usernameLabel;
    private JButton registerButton;
    private JCheckBox registerCheckBox;

    Connection databaseConnection;
    PreparedStatement preparedStatement;
    ResultSet resultSet;

    public LoginFrame() throws SQLException{
        SetupDatabaseConnection();


        add(mainPanel);
        setSize(400,400);
        setTitle("FavoriteSites Login Frame");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);

        registerButton.setEnabled(false);


        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if(CheckLoginInformation()){
                        dispose();
                        MainFrame mainFrame = new MainFrame(databaseConnection,usernameTField.getText());
                    }else{
                        JOptionPane.showMessageDialog(null, "Login failed.Please try again");
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        registerCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(registerCheckBox.isSelected()){
                    registerButton.setEnabled(true);
                    loginButton.setEnabled(false);
                }else{
                    registerButton.setEnabled(false);
                    loginButton.setEnabled(true);
                }
            }
        });
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RegisterUser();
            }
        });
    }

    private void RegisterUser(){
        String username = usernameTField.getText();
        String password = new String(passwordPField.getPassword());
        try {
            String query="INSERT INTO userinfo(username,password) VALUES (?,?)";
            preparedStatement = databaseConnection.prepareStatement(query);
            preparedStatement.setString(1,username);
            preparedStatement.setString(2,password);

            preparedStatement.executeUpdate();

        }catch (SQLException sqlException){
            sqlException.printStackTrace();
        }
    }

    private boolean CheckLoginInformation() throws SQLException{
        String username = usernameTField.getText();
        String password = new String(passwordPField.getPassword());
        try {
            String query="SELECT * FROM userinfo WHERE username=? AND password=?";
            preparedStatement = databaseConnection.prepareStatement(query);
            preparedStatement.setString(1,username);
            preparedStatement.setString(2,password);

            resultSet = preparedStatement.executeQuery();

            return resultSet.next();

        }catch (SQLException sqlException){
            sqlException.printStackTrace();
        }

        return false;
    }

    private void SetupDatabaseConnection() throws SQLException {
        try {

            String connectionURL="jdbc:mysql://localhost:3306/favoritesites?useSSL=false&allowPublicKeyRetrieval=true";
            String user="root";
            String password="1234";

            databaseConnection = DriverManager.getConnection(connectionURL,user,password);

        }catch (SQLException sqlException){
            sqlException.printStackTrace();
        }
    }
}

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoginFrame extends FrameSuperClass{

    private JPanel mainPanel;
    private JTextField usernameTField;
    private JPasswordField passwordPField;
    private JButton loginButton;
    private JLabel passwordLabel;
    private JLabel usernameLabel;
    private JButton registerButton;
    private JCheckBox registerCheckBox;

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
                        String loggedInUser = usernameTField.getText();
                        MainFrame mainFrame = new MainFrame(loggedInUser);
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
        String query="INSERT INTO userinfo(username,password) VALUES (?,?)";

        String username = usernameTField.getText();
        String password = new String(passwordPField.getPassword());

        List<String> inputs=new ArrayList<>();
        inputs.add(username);
        inputs.add(password);
        PreparedStatement preparedStatement = FillQueryWithInputs(query,inputs);

        RunQueryOnce(preparedStatement,"User Successfully Registered!","Registery Failed!");
    }

    private boolean CheckLoginInformation() throws SQLException{
        String query="SELECT * FROM userinfo WHERE username=? AND password=?";

        String username = usernameTField.getText();
        String password = new String(passwordPField.getPassword());

        List<String> inputs=new ArrayList<>();
        inputs.add(username);
        inputs.add(password);
        PreparedStatement preparedStatement = FillQueryWithInputs(query,inputs);

        ResultSet resultSet = preparedStatement.executeQuery();

        return resultSet.next();

    }

}

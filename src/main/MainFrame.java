import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;

public class MainFrame extends FrameSuperClass{
    private JPanel mainPanel;
    private JLabel usernameDisplay;
    private JButton displayFunctionsButton;
    private JButton locationFunctionsButton;
    private JButton shareFrameButton;
    private JButton backButton;


    public MainFrame(String username){
        add(mainPanel);
        setSize(400,400);
        setTitle("FavoriteSites Main Frame");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);

        usernameDisplay.setText("Welcome "+username+" !!");

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                try {
                    LoginFrame loginFrame = new LoginFrame();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        locationFunctionsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LocationFunctionsFrame locationFunctionsFrame = new LocationFunctionsFrame(username);
            }
        });

        displayFunctionsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DisplayFunctionsFrame displayFunctionsFrame = new DisplayFunctionsFrame(username);
            }
        });

        shareFrameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ShareFunctionsFrame shareFunctionsFrame = new ShareFunctionsFrame(username);
            }
        });


    }
}



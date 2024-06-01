import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ShareFunctionsFrame extends FrameSuperClass {
    private JRadioButton shareVisitIDRButton;
    private JRadioButton showSharedVisitsRButton;
    private JTextField friendsUsernameTField;
    private JComboBox visitidComboBox;
    private JTextArea visitsTArea;
    private JButton executeButton;
    private JLabel friendUsernameLabel;
    private JLabel visitidLabel;
    private JLabel visitsLabel;
    private JPanel mainPanel;
    private JButton backButton;
    String username;

    ButtonGroup buttonGroup = new ButtonGroup();
    public ShareFunctionsFrame(String username){
        SetupDatabaseConnection();
        DefaultJFrameSetup(this,mainPanel,900,600,"FavoriteSites Share Functions Frame",3);
        this.username = username;

        visitsTArea.setFont(new Font("Arial", Font.PLAIN, 14));


        SetupSelectionRadioButtons();
        SetupVisitIdComboBox();

        HandleFieldSelectionLogic();

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        shareVisitIDRButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                HandleFieldSelectionLogic();

            }
        });

        showSharedVisitsRButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                HandleFieldSelectionLogic();

            }
        });

        executeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                visitsTArea.setText("");

                String selection = buttonGroup.getSelection().getActionCommand();
                switch (selection){
                    case "shareVisit":
                        ShareVisitFunctionality();
                        break;
                    case "showVisits":
                        ShowVisitsFunctionality();
                        break;
                }
            }
        });

    }
    private void ShowUserVisits(){

        String query = "SELECT * FROM visits WHERE username=?";

        PreparedStatement preparedStatement=FillQueryWithAnInput(query,username);
        DefaultTableModel tableModel=FillSQLDataIntoTable(preparedStatement);
        int rowCount = tableModel.getRowCount();
        if(rowCount != 0){
            for (int r = 0; r < rowCount; r++) {
                String visitInfo="";
                for (int c = 0; c < tableModel.getColumnCount(); c++) {
                    visitInfo+=tableModel.getValueAt(r,c).toString()+" | ";
                }
                addTextToDisplayArea(visitInfo);
            }
        }else{
            JOptionPane.showMessageDialog(null, "There was an error!");
        }

    }

    private void ShowVisitsFunctionality(){

        String query = "SELECT sharingUsername,sv.visitid,countryName,cityName,yearVisited,seasonVisited,bestFeature,comment,rating " +
                        "FROM sharedvisits sv JOIN visits v ON v.username = sv.sharingUsername AND " +
                        "v.visitid = sv.visitid WHERE sv.username = ?";

        PreparedStatement preparedStatement=FillQueryWithAnInput(query,username);
        DefaultTableModel tableModel = FillSQLDataIntoTable(preparedStatement);

        int rowCount = tableModel.getRowCount();
        if(rowCount != 0){
            for (int r = 0; r < rowCount; r++) {
                String visitInfo="";
                for (int c = 0; c < tableModel.getColumnCount(); c++) {
                    visitInfo+=tableModel.getValueAt(r,c).toString()+" | ";
                }
                addTextToDisplayArea(visitInfo);
            }
        }else{
            JOptionPane.showMessageDialog(null, "There was an error!");
        }

    }

    private void ShareVisitFunctionality(){
        String query = "INSERT INTO sharedvisits VALUES(?,?,?)";

        List<String> inputs=new ArrayList<>();
        inputs.add(visitidComboBox.getSelectedItem().toString());
        inputs.add(friendsUsernameTField.getText());
        inputs.add(username);
        PreparedStatement preparedStatement=FillQueryWithInputs(query,inputs);
        RunQueryOnce(preparedStatement,"You have successfully shared visit","There was an error!");
    }

    private void addTextToDisplayArea(String text){
        visitsTArea.append(text+"\n");
    }

    private void SetupSelectionRadioButtons(){
        buttonGroup = new ButtonGroup();
        buttonGroup.add(shareVisitIDRButton);
        buttonGroup.add(showSharedVisitsRButton);

        shareVisitIDRButton.setActionCommand("shareVisit");
        showSharedVisitsRButton.setActionCommand("showVisits");
    }

    private void SetupVisitIdComboBox(){
        visitidComboBox.removeAllItems();
        visitidComboBox.addItem("Not Selected");

        String query = "SELECT visitid FROM visits WHERE username=?;";

        try {
            ResultSet resultSet=FillQueryWithAnInput(query,username).executeQuery();
            while (resultSet.next()) {
                visitidComboBox.addItem(resultSet.getObject(1));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "User doesn't have a recorded location!");
            ex.printStackTrace();
        }
    }


    private void HandleFieldSelectionLogic(){
        String selection="";
        if(buttonGroup.getSelection() != null){
            selection = buttonGroup.getSelection().getActionCommand();
        }

        visitsTArea.setEnabled(false);

        switch (selection){
            case "":
                friendsUsernameTField.setEditable(false);
                visitidComboBox.setEnabled(false);
                executeButton.setEnabled(false);

                visitsLabel.setText("Display Screen");
                break;

            case "shareVisit":
                friendsUsernameTField.setEditable(true);
                visitidComboBox.setEnabled(true);
                executeButton.setEnabled(true);

                visitsTArea.setText("");
                ShowUserVisits();
                visitsLabel.setText(username+"'s visits (Country Name | City Name | Year Visited | Season Visited | " +
                        "Best Feature | Comment | Rating | Username | Visit ID)");
                break;
            case "showVisits":
                friendsUsernameTField.setEditable(false);
                visitidComboBox.setEnabled(false);
                executeButton.setEnabled(true);

                visitsTArea.setText("");
                friendsUsernameTField.setText("");
                visitidComboBox.setSelectedItem("Not Selected");
                visitsLabel.setText("Visits shared with "+username+" (Username Of Sharer| Visit ID | Country Name | City Name | Year Visited | Season Visited | " +
                        "Best Feature | Comment | Rating)");
                break;
        }
    }
}

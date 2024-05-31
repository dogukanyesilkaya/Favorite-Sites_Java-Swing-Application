import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class ShareFunctionsFrame extends JFrame {
    private JRadioButton shareVisitIDRButton;
    private JRadioButton showSharedVisitsRButton;
    private JTextField friendsUsernameTField;
    private JTextField visitidTField;
    private JTextArea visitsTArea;
    private JButton executeButton;
    private JLabel friendUsernameLabel;
    private JLabel visitidLabel;
    private JLabel visitsLabel;
    private JPanel mainPanel;
    private JButton backButton;

    Connection databaseConnection;
    String username;

    ButtonGroup buttonGroup = new ButtonGroup();
    public ShareFunctionsFrame(Connection connection, String username){
        databaseConnection = connection;
        this.username = username;

        add(mainPanel);
        setSize(900,600);
        setTitle("FavoriteSites Share Frame");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);

        friendsUsernameTField.setEditable(false);
        visitidTField.setEditable(false);
        executeButton.setEnabled(false);
        visitsTArea.setEnabled(false);

        SetupSelectionRadioButtons();

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
                ShowUserVisits();
                visitsLabel.setText(username+"'s visits (Country Name | City Name | Year Visited | Season Visited | " +
                        "Best Feature | Comment | Rating | Username | Visit ID)");
            }
        });

        showSharedVisitsRButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                HandleFieldSelectionLogic();
                visitsTArea.setText("");
                friendsUsernameTField.setText("");
                visitidTField.setText("");
                visitsTArea.setText("");
                visitsLabel.setText("Visits shared with "+username+" (Username Of Sharer| Visit ID | Country Name | City Name | Year Visited | Season Visited | " +
                        "Best Feature | Comment | Rating)");
            }
        });

        executeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selection = buttonGroup.getSelection().getActionCommand();
                switch (selection){
                    case "shareVisit":
                        if(CheckVisitID()){
                            ShareVisitFunctionality();
                        }else{
                            JOptionPane.showMessageDialog(null, "Please enter a valid Visit ID");
                        }
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
        try {
            PreparedStatement preparedStatement = databaseConnection.prepareStatement(query);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            DefaultTableModel tableModel=new DefaultTableModel();
            for (int i = 1; i <= columnCount; i++) {
                String columnLabel=metaData.getColumnLabel(i);
                tableModel.addColumn(columnLabel);
            }

            while (resultSet.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = resultSet.getObject(i);
                }
                tableModel.addRow(row);
            }
            int rowCount = tableModel.getRowCount();
            if(rowCount != 0){
                for (int r = 0; r < rowCount; r++) {
                    String visitInfo="";
                    for (int c = 0; c < columnCount; c++) {
                        visitInfo+=tableModel.getValueAt(r,c).toString()+" | ";
                    }
                    addTextToDisplayArea(visitInfo);
                }
            }else{
                JOptionPane.showMessageDialog(null, "There was an error!");
            }
        }catch (SQLException sqlException){
            sqlException.printStackTrace();
        }
    }
    private boolean CheckVisitID(){
        String visitID =visitidTField.getText();
        if(visitID == ""){
            return false;
        }

        String query = "SELECT * FROM visits WHERE visitid=?";
        try {
            PreparedStatement preparedStatement = databaseConnection.prepareStatement(query);
            preparedStatement.setString(1,visitID);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                return true;
            }else{
                return false;
            }

        }catch (SQLException sqlException){
            sqlException.printStackTrace();
        }
        return false;
    }

    private void ShowVisitsFunctionality(){


        String query = "SELECT sharingUsername,sv.visitid,countryName,cityName,yearVisited,seasonVisited,bestFeature,comment,rating " +
                        "FROM sharedvisits sv JOIN visits v ON v.username = sv.sharingUsername AND " +
                        "v.visitid = sv.visitid WHERE sv.username = ?";
        try {
            PreparedStatement preparedStatement = databaseConnection.prepareStatement(query);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            DefaultTableModel tableModel=new DefaultTableModel();
            for (int i = 1; i <= columnCount; i++) {
                tableModel.addColumn(metaData.getColumnLabel(i));
            }

            while (resultSet.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = resultSet.getObject(i);
                }
                tableModel.addRow(row);
            }
            int rowCount = tableModel.getRowCount();
            if(rowCount != 0){
                for (int r = 0; r < rowCount; r++) {
                    String visitInfo="";
                    for (int c = 0; c < columnCount; c++) {
                        visitInfo+=tableModel.getValueAt(r,c).toString()+" | ";
                    }
                    addTextToDisplayArea(visitInfo);
                }
            }else{
                JOptionPane.showMessageDialog(null, "There was an error!");
            }
        }catch (SQLException sqlException){
            sqlException.printStackTrace();
        }
    }

    private void ShareVisitFunctionality(){
        String query = "INSERT INTO sharedvisits VALUES(?,?,?)";
        try {
            PreparedStatement preparedStatement = databaseConnection.prepareStatement(query);
            preparedStatement.setString(1,visitidTField.getText());
            preparedStatement.setString(2,friendsUsernameTField.getText());
            preparedStatement.setString(3,username);

            int addedRow = preparedStatement.executeUpdate();
            if(addedRow>0){
                JOptionPane.showMessageDialog(null, "You have successfully shared visit");
            }else{
                JOptionPane.showMessageDialog(null, "There was an error!");
            }

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
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

    private void HandleFieldSelectionLogic(){
        String selection = buttonGroup.getSelection().getActionCommand();
        switch (selection){
            case "shareVisit":
                friendsUsernameTField.setEditable(true);
                visitidTField.setEditable(true);
                executeButton.setEnabled(true);
                break;
            case "showVisits":
                friendsUsernameTField.setEditable(false);
                visitidTField.setEditable(false);
                executeButton.setEnabled(true);
                break;
        }
    }
}

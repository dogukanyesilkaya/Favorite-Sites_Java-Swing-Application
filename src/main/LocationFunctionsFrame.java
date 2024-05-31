import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LocationFunctionsFrame extends FrameSuperClass{
    private JPanel mainPanel;
    private JRadioButton addVisitRButton;
    private JRadioButton displayVisitRadioButton;
    private JRadioButton updateVisitRButton;
    private JRadioButton deleteVisitRButton;
    private JComboBox visitidComboBox;
    private JTextField countryNameTField;
    private JTextField cityNameTField;
    private JTextField yearTField;
    private JTextField seasonTField;
    private JSlider ratingSlider;
    private JComboBox featureComboBox;
    private JTextField commentTField;
    private JButton executeButton;
    private JLabel infoLabel;
    private JLabel visitidLabel;
    private JLabel countryNameLabel;
    private JLabel cityNameLabel;
    private JLabel yearLabel;
    private JLabel seasonLabel;
    private JLabel ratingSliderLabel;
    private JLabel bestFeatureLabel;
    private JLabel commentLabel;
    private JButton backButton;

    ButtonGroup functionRadioButtons;
    ButtonGroup subFunctionRadioButtons;

    Connection databaseConnection;
    String username;


    public LocationFunctionsFrame(Connection connection,String username){
        databaseConnection = connection;
        this.username = username;

        add(mainPanel);
        setSize(900,600);
        setTitle("FavoriteSites Main Frame");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);

        deleteVisitRButton.setEnabled(false);
        updateVisitRButton.setEnabled(false);

        SetupFunctionRadioButtons();
        SetupFeatureComboBox();
        SetupVisitIdComboBox();

        HandleFieldSelectionLogic();

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        executeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selection = getSelectedFunction();
                switch (selection){
                    case "addVisit":
                        AddVisitFunctionality();
                        break;
                    case "displayVisit":
                        try {
                            DisplayVisitFunctionality();
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }
                        break;
                    case "deleteVisit":
                        DeleteVisitFunctionality();
                        break;

                    case "updateVisit":
                        try {
                            UpdateVisitFunctionality();
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }
                        break;
                }
            }
        });

    }
    private void AddVisitFunctionality(){
        String query = "INSERT INTO visits(countryName,cityName,yearVisited,seasonVisited,bestFeature,comment,rating,username)" +
                "VALUES (?,?,?,?,?,?,?,?)";
        try {
            PreparedStatement preparedStatement = databaseConnection.prepareStatement(query);
            preparedStatement.setString(1,countryNameTField.getText());
            preparedStatement.setString(2,cityNameTField.getText());
            preparedStatement.setString(3,yearTField.getText());
            preparedStatement.setString(4,seasonTField.getText());
            preparedStatement.setString(5,featureComboBox.getSelectedItem().toString());
            preparedStatement.setString(6,commentTField.getText());
            preparedStatement.setString(7,""+ratingSlider.getValue());
            preparedStatement.setString(8,username);

            int addedRow = preparedStatement.executeUpdate();
            if(addedRow>0){
                JOptionPane.showMessageDialog(null, "You have successfully added location");
            }else{
                JOptionPane.showMessageDialog(null, "There was a error!");
            }

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
//        List<String> inputs=new ArrayList<String>();
//        inputs.add(countryNameTField.getText());
//        inputs.add(cityNameTField.getText());
//        inputs.add(yearTField.getText());
//        inputs.add(seasonTField.getText());
//        inputs.add(featureComboBox.getSelectedItem().toString());
//        inputs.add(commentTField.getText());
//        inputs.add(""+ratingSlider.getValue());
//        inputs.add(username);
//        PreparedStatement preparedStatement = FillQueryWithInputs(query,inputs);
//        RunQueryOnce(preparedStatement,"You have successfully added location","There was a error!");
    }

    private void DisplayVisitFunctionality() throws SQLException {
        String query = "SELECT * FROM visits WHERE visitid=?";
        try {

            PreparedStatement preparedStatement = databaseConnection.prepareStatement(query);
            preparedStatement.setString(1, visitidComboBox.getSelectedItem().toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            DefaultTableModel tableModel=new DefaultTableModel();
            // Add columns to table model
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
            if(tableModel.getRowCount() != 0){
                countryNameTField.setText(tableModel.getValueAt(0,0).toString());
                cityNameTField.setText(tableModel.getValueAt(0,1).toString());
                yearTField.setText(tableModel.getValueAt(0,2).toString());
                seasonTField.setText(tableModel.getValueAt(0,3).toString());
                featureComboBox.setSelectedItem(tableModel.getValueAt(0,4).toString().toString());
                commentTField.setText(tableModel.getValueAt(0,5).toString());
                ratingSlider.setValue(Integer.parseInt(tableModel.getValueAt(0,6).toString().toString()));
            }else{
                JOptionPane.showMessageDialog(null, "Please select a valid visit ID");
            }
        }catch (SQLException sqlException){
            sqlException.printStackTrace();
        }

    }

    private void DeleteVisitFunctionality(){
        String query = "DELETE FROM visits WHERE username=? AND visitid=?";
        try {
            PreparedStatement preparedStatement = databaseConnection.prepareStatement(query);
            preparedStatement.setString(1,username);
            preparedStatement.setString(2, visitidComboBox.getSelectedItem().toString());

            int addedRow = preparedStatement.executeUpdate();
            if(addedRow>0){
                JOptionPane.showMessageDialog(null, "You have successfully removed visit");
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Please select a valid visit ID");
            throw new RuntimeException(ex);
        }
    }

    private void UpdateVisitFunctionality() throws SQLException {
        String query = "UPDATE visits SET countryName =?,cityName=?,yearVisited=?,seasonVisited=?," +
                "bestFeature=?,comment=?,rating=? WHERE visitid=?";
        try {
            PreparedStatement preparedStatement = databaseConnection.prepareStatement(query);
            preparedStatement.setString(1,countryNameTField.getText());
            preparedStatement.setString(2,cityNameTField.getText());
            preparedStatement.setString(3,yearTField.getText());
            preparedStatement.setString(4,seasonTField.getText());
            preparedStatement.setString(5,featureComboBox.getSelectedItem().toString());
            preparedStatement.setString(6,commentTField.getText());
            preparedStatement.setString(7,""+ratingSlider.getValue());
            preparedStatement.setString(8, visitidComboBox.getSelectedItem().toString());

            int addedRow = preparedStatement.executeUpdate();
            if(addedRow>0){
                JOptionPane.showMessageDialog(null, "You have successfully updated location");
            }else{
                JOptionPane.showMessageDialog(null, "There was an error!");
            }
        }catch (SQLException ex){
            JOptionPane.showMessageDialog(null, "Please select a valid visit ID");
            throw new RuntimeException(ex);
        }
    }

    private String getSelectedFunction(){
        String selection="";

        if(functionRadioButtons.getSelection() != null){
            selection = functionRadioButtons.getSelection().getActionCommand();
        }

        if(subFunctionRadioButtons.getSelection() != null){
            selection = subFunctionRadioButtons.getSelection().getActionCommand();
        }
        return selection;
    }

    private void SetupFunctionRadioButtons(){
        addVisitRButton.setActionCommand("addVisit");
        displayVisitRadioButton.setActionCommand("displayVisit");

        deleteVisitRButton.setActionCommand("deleteVisit");
        updateVisitRButton.setActionCommand("updateVisit");


        functionRadioButtons = new ButtonGroup();
        functionRadioButtons.add(addVisitRButton);
        functionRadioButtons.add(displayVisitRadioButton);


        subFunctionRadioButtons = new ButtonGroup();
        subFunctionRadioButtons.add(deleteVisitRButton);
        subFunctionRadioButtons.add(updateVisitRButton);

        ActionListener enableSubRadioButtons = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                subFunctionRadioButtons.clearSelection();
                deleteVisitRButton.setEnabled(true);
                updateVisitRButton.setEnabled(true);

                HandleFieldSelectionLogic();
            }
        };

        ActionListener disableSubRadioButtons = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                subFunctionRadioButtons.clearSelection();
                deleteVisitRButton.setEnabled(false);
                updateVisitRButton.setEnabled(false);

                HandleFieldSelectionLogic();
            }
        };

        addVisitRButton.addActionListener(disableSubRadioButtons);
        displayVisitRadioButton.addActionListener(enableSubRadioButtons);

        ActionListener handleLogicForSubRButtons = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                HandleFieldSelectionLogic();
            }
        };

        deleteVisitRButton.addActionListener(handleLogicForSubRButtons);
        updateVisitRButton.addActionListener(handleLogicForSubRButtons);

    }
    private void SetupFeatureComboBox(){
        featureComboBox.addItem("None");
        featureComboBox.addItem("Food");
        featureComboBox.addItem("Accomodation");
        featureComboBox.addItem("Sightseeing");
        featureComboBox.addItem("Museums");
    }
    private void SetupVisitIdComboBox(){
        visitidComboBox.addItem("Not Selected");

        String query = "SELECT visitid FROM visits WHERE username=?;";
        try {
            PreparedStatement preparedStatement = databaseConnection.prepareStatement(query);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                visitidComboBox.addItem(resultSet.getObject(1));
            }
        }catch (SQLException sqlException){
            JOptionPane.showMessageDialog(null, "User doesn't have a recorded location!");
            sqlException.printStackTrace();
        }
    }
    private void HandleFieldSelectionLogic(){
        String selection=getSelectedFunction();

        switch (selection){
            case "":
                visitidComboBox.setEnabled(false);
                countryNameTField.setEditable(false);
                cityNameTField.setEditable(false);
                yearTField.setEditable(false);
                seasonTField.setEditable(false);
                featureComboBox.setEnabled(false);
                commentTField.setEditable(false);
                ratingSlider.setEnabled(false);
                executeButton.setEnabled(false);
                break;
            case "addVisit":
                visitidComboBox.setEnabled(false);
                countryNameTField.setEditable(true);
                cityNameTField.setEditable(true);
                yearTField.setEditable(true);
                seasonTField.setEditable(true);
                featureComboBox.setEnabled(true);
                commentTField.setEditable(true);
                ratingSlider.setEnabled(true);
                executeButton.setEnabled(true);
                break;
            case "displayVisit":
                visitidComboBox.setEnabled(true);
                countryNameTField.setEditable(false);
                cityNameTField.setEditable(false);
                yearTField.setEditable(false);
                seasonTField.setEditable(false);
                featureComboBox.setEnabled(false);
                commentTField.setEditable(false);
                ratingSlider.setEnabled(false);
                executeButton.setEnabled(true);
                break;
            case "deleteVisit":
                visitidComboBox.setEnabled(true);
                countryNameTField.setEditable(false);
                cityNameTField.setEditable(false);
                yearTField.setEditable(false);
                seasonTField.setEditable(false);
                featureComboBox.setEnabled(false);
                commentTField.setEditable(false);
                ratingSlider.setEnabled(false);
                executeButton.setEnabled(true);
                break;

            case "updateVisit":
                visitidComboBox.setEnabled(false);
                countryNameTField.setEditable(true);
                cityNameTField.setEditable(true);
                yearTField.setEditable(true);
                seasonTField.setEditable(true);
                featureComboBox.setEnabled(true);
                commentTField.setEditable(true);
                ratingSlider.setEnabled(true);
                executeButton.setEnabled(true);
                break;
        }
    }

}

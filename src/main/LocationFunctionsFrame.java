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
    private JComboBox seasonComboBox;
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

    String username;


    public LocationFunctionsFrame(String username){
        SetupDatabaseConnection();
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
        SetupSeasonComboBox();
        UpdateVisitIdComboBox();

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
                        UpdateVisitIdComboBox();
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
                        UpdateVisitIdComboBox();
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

        List<String> inputs=new ArrayList<>();
        inputs.add(countryNameTField.getText());
        inputs.add(cityNameTField.getText());
        inputs.add(yearTField.getText());
        inputs.add(seasonComboBox.getSelectedItem().toString());
        inputs.add(featureComboBox.getSelectedItem().toString());
        inputs.add(commentTField.getText());
        inputs.add(""+ratingSlider.getValue());
        inputs.add(username);
        PreparedStatement preparedStatement = FillQueryWithInputs(query,inputs);
        RunQueryOnce(preparedStatement,"You have successfully added location","There was a error!");
    }

    private void DisplayVisitFunctionality() throws SQLException {
        String query = "SELECT * FROM visits WHERE visitid=?";

        List<String> inputs=new ArrayList<>();
        inputs.add(visitidComboBox.getSelectedItem().toString());

        PreparedStatement preparedStatement = FillQueryWithInputs(query,inputs);

        DefaultTableModel tableModel = FillSQLDataIntoTable(preparedStatement);

        if(tableModel.getRowCount() != 0){
            countryNameTField.setText(tableModel.getValueAt(0,0).toString());
            cityNameTField.setText(tableModel.getValueAt(0,1).toString());
            yearTField.setText(tableModel.getValueAt(0,2).toString());
            seasonComboBox.setSelectedItem(tableModel.getValueAt(0,3).toString());
            featureComboBox.setSelectedItem(tableModel.getValueAt(0,4).toString().toString());
            commentTField.setText(tableModel.getValueAt(0,5).toString());
            ratingSlider.setValue(Integer.parseInt(tableModel.getValueAt(0,6).toString().toString()));
        }else{
            JOptionPane.showMessageDialog(null, "Please select a valid visit ID");
        }
    }

    private void DeleteVisitFunctionality(){
        String query = "DELETE FROM visits WHERE username=? AND visitid=?";

        List<String> inputs=new ArrayList<String>();
        inputs.add(username);
        inputs.add(visitidComboBox.getSelectedItem().toString());

        PreparedStatement preparedStatement = FillQueryWithInputs(query,inputs);

        RunQueryOnce(preparedStatement,"You have successfully removed visit","Please select a valid visit ID");
    }

    private void UpdateVisitFunctionality() throws SQLException {
        String query = "UPDATE visits SET countryName =?,cityName=?,yearVisited=?,seasonVisited=?," +
                "bestFeature=?,comment=?,rating=? WHERE visitid=?";

        List<String> inputs=new ArrayList<String>();
        inputs.add(countryNameTField.getText());
        inputs.add(cityNameTField.getText());
        inputs.add(yearTField.getText());
        inputs.add(seasonComboBox.getSelectedItem().toString());
        inputs.add(featureComboBox.getSelectedItem().toString());
        inputs.add(commentTField.getText());
        inputs.add(""+ratingSlider.getValue());
        inputs.add(visitidComboBox.getSelectedItem().toString());

        PreparedStatement preparedStatement = FillQueryWithInputs(query,inputs);

        RunQueryOnce(preparedStatement,"You have successfully updated location","There was an error!");
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

    private void SetupSeasonComboBox(){
        seasonComboBox.addItem("");
        seasonComboBox.addItem("Spring");
        seasonComboBox.addItem("Summer");
        seasonComboBox.addItem("Fall");
        seasonComboBox.addItem("Winter");
    }
    private void UpdateVisitIdComboBox(){
        visitidComboBox.removeAllItems();
        visitidComboBox.addItem("Not Selected");


        String query = "SELECT visitid FROM visits WHERE username=?;";
        List<String> inputs=new ArrayList<String>();
        inputs.add(username);
        try {
            ResultSet resultSet=FillQueryWithInputs(query,inputs).executeQuery();
            while (resultSet.next()) {
                visitidComboBox.addItem(resultSet.getObject(1));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "User doesn't have a recorded location!");
            ex.printStackTrace();
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
                seasonComboBox.setEnabled(false);
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
                seasonComboBox.setEnabled(true);
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
                seasonComboBox.setEnabled(false);
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
                seasonComboBox.setEnabled(false);
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
                seasonComboBox.setEnabled(true);
                featureComboBox.setEnabled(true);
                commentTField.setEditable(true);
                ratingSlider.setEnabled(true);
                executeButton.setEnabled(true);
                break;
        }
    }

}

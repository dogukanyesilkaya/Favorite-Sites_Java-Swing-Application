import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DisplayFunctionsFrame extends FrameSuperClass{
    private JPanel mainPanel;
    private JTextArea displayTArea;
    private JLabel displayField;
    private JButton displayImageButton;
    private JButton displayVisitsOfYearButton;
    private JButton bestFeatureOfFoodButton;
    private JLabel visitidLabel;
    private JComboBox visitidComboBox;
    private JTextField yearTField;
    private JLabel yearLabel;
    private JButton mostVisitedButton;
    private JButton visitedInSpringButton;
    private JPanel rightPanel;
    private JButton backButton;
    private JCheckBox sharedVisitsCheckBox;

    String username;

    public DisplayFunctionsFrame(String username) {
        displayTArea.setFont(new Font("Arial", Font.PLAIN, 14));
        displayTArea.setEnabled(true);
        displayTArea.setEditable(false);

        SetupDatabaseConnection();
        this.username = username;

        DefaultJFrameSetup(this,mainPanel,900,600,"FavoriteSites Display Functions Frame",3);

        UpdateVisitIdComboBox(false);


        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        sharedVisitsCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(sharedVisitsCheckBox.isSelected()){
                    UpdateVisitIdComboBox(true);

                    yearTField.setEnabled(false);
                    displayVisitsOfYearButton.setEnabled(false);
                    bestFeatureOfFoodButton.setEnabled(false);
                    mostVisitedButton.setEnabled(false);
                    visitedInSpringButton.setEnabled(false);
                }else{
                    UpdateVisitIdComboBox(false);

                    yearTField.setEnabled(true);
                    displayVisitsOfYearButton.setEnabled(true);
                    bestFeatureOfFoodButton.setEnabled(true);
                    mostVisitedButton.setEnabled(true);
                    visitedInSpringButton.setEnabled(true);
                }
            }
        });

        displayVisitsOfYearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayField.setText("(Country Name | City Name | Season Visited | Best Feature | Comment | Rating | Visit ID)");
                displayTArea.setText("");
                DisplayVisitInfoOfYear();
            }
        });

        visitedInSpringButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayField.setText("Display Field");
                displayTArea.setText("");
                DisplayCountriesVisitedInSpring();
            }
        });

        mostVisitedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayField.setText("Display Field");
                displayTArea.setText("");
                DisplayMostVisitedCountry();
            }
        });

        bestFeatureOfFoodButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayField.setText("Display Field");
                displayTArea.setText("");
                DisplayCountriesWithBestFeatureOfFood();
            }
        });

        displayImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayField.setText("Display Field");
                displayTArea.setText("");
                DisplayImageOfGivenVisitID();

            }
        });

    }

    private void DisplayImageOfGivenVisitID() {

        String visitID = visitidComboBox.getSelectedItem().toString();

        if(visitID == "Not Selected"){
            return;
        }

        JFrame showImageFrame = new JFrame();
        JPanel showImagePanel=new JPanel();

        DefaultJFrameSetup(showImageFrame,showImagePanel,800,800,"Image Of Visit ID: "+visitID,2);


        ImageIcon location1ImageIcon = new ImageIcon("other/Location1.jpg");
        ImageIcon location2ImageIcon = new ImageIcon("other/Location2.jpg");
        ImageIcon location3ImageIcon = new ImageIcon("other/Location3.jpg");
        ImageIcon location4ImageIcon = new ImageIcon("other/Location4.jpg");
        ImageIcon location5ImageIcon = new ImageIcon("other/Location5.jpg");
        JLabel locationImage;
        int visitID_int = Integer.parseInt(visitID);
        switch (visitID_int % 5){
            case 0:
                locationImage = new JLabel(location1ImageIcon);
                showImagePanel.add(locationImage);
                break;
            case 1:
                locationImage = new JLabel(location2ImageIcon);
                showImagePanel.add(locationImage);
                break;
            case 2:
                locationImage = new JLabel(location3ImageIcon);
                showImagePanel.add(locationImage);
                break;
            case 3:
                locationImage = new JLabel(location4ImageIcon);
                showImagePanel.add(locationImage);
                break;
            case 4:
                locationImage = new JLabel(location5ImageIcon);
                showImagePanel.add(locationImage);
                break;
        }

    }


    private void DisplayCountriesWithBestFeatureOfFood(){

        String query = "SELECT * FROM visits WHERE username=? AND bestFeature=? ORDER BY rating DESC";

        List<String> inputs=new ArrayList<>();
        inputs.add(username);
        inputs.add("Food");

        PreparedStatement preparedStatement = FillQueryWithInputs(query,inputs);
        try {
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                do {
                    String countryName = resultSet.getString("countryName");
                    int rating = resultSet.getInt("rating");
                    addTextToDisplayArea(countryName+" | Rating:"+rating+"/5");

                }while (resultSet.next());
            }else{
                JOptionPane.showMessageDialog(null, "There was an error!");
            }

        }catch (SQLException sqlException){
            sqlException.printStackTrace();
        }

    }

    private void DisplayCountriesVisitedInSpring(){

        String query = "SELECT * FROM visits WHERE username=? AND seasonVisited=?";

        List<String> inputs=new ArrayList<>();
        inputs.add(username);
        inputs.add("Spring");

        PreparedStatement preparedStatement = FillQueryWithInputs(query,inputs);
        try {

            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                do {
                    String countryName = resultSet.getObject(1).toString();
                    addTextToDisplayArea(countryName);

                }while (resultSet.next());
            }else{
                JOptionPane.showMessageDialog(null, "There was an error!");
            }

        }catch (SQLException sqlException){
            sqlException.printStackTrace();
        }
    }

    private void DisplayMostVisitedCountry(){

        String DropViewIfExists="DROP VIEW IF EXISTS CountryVisitCount";

        String CountryVisitCountViewTable = "CREATE VIEW CountryVisitCount AS " +
                                            "SELECT COUNT(*) as VisitCount FROM visits GROUP BY countryName";


        String query = "SELECT countryName,COUNT(*) as VisitCount FROM visits WHERE username=? GROUP BY countryName HAVING VisitCount = " +
                                                            "(SELECT MAX(VisitCount) FROM CountryVisitCount)";

        RunQueryWithoutInput(DropViewIfExists);
        RunQueryWithoutInput(CountryVisitCountViewTable);
        PreparedStatement preparedStatement = FillQueryWithAnInput(query,username);
        try {
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                do {
                    String countryName = resultSet.getObject(1).toString();
                    String visitCount = resultSet.getObject(2).toString();
                    addTextToDisplayArea(countryName+" | Visited "+visitCount+" times.");
                }while (resultSet.next());
            }else{
                JOptionPane.showMessageDialog(null, "There was an error!");
            }

        }catch (SQLException sqlException){
            sqlException.printStackTrace();
        }



    }

    private void DisplayVisitInfoOfYear(){

        String query = "SELECT countryName,cityName,seasonVisited,bestFeature,comment,rating,visitid FROM visits WHERE username=? AND yearVisited=?";

        List<String> inputs=new ArrayList<>();
        inputs.add(username);
        inputs.add(yearTField.getText());

        PreparedStatement preparedStatement = FillQueryWithInputs(query,inputs);
        DefaultTableModel tableModel = FillSQLDataIntoTable(preparedStatement);

        int rowCount = tableModel.getRowCount();
        if(rowCount != 0){
            for (int r = 0; r < rowCount; r++) {
                String visitInfo="";
                for (int c = 0; c < tableModel.getColumnCount(); c++) {
                    if(c==5){ //for formatting rating column
                        visitInfo+=tableModel.getValueAt(r,c).toString()+"/5 | ";
                    }else{
                        visitInfo+=tableModel.getValueAt(r,c).toString()+" | ";
                    }

                }
                addTextToDisplayArea(visitInfo);
            }
        }else{
            JOptionPane.showMessageDialog(null, "There was an error!");
        }

    }

    private void UpdateVisitIdComboBox(boolean isSharedVisitsIncluded){
        visitidComboBox.removeAllItems();
        visitidComboBox.addItem("Not Selected");

        String query1 = "SELECT visitid FROM visits WHERE username=?;";
        try {
            ResultSet resultSet1=FillQueryWithAnInput(query1,username).executeQuery();
            while (resultSet1.next()) {
                visitidComboBox.addItem(resultSet1.getObject(1));

            }

            if(isSharedVisitsIncluded){
                String query2 = "SELECT visitid  FROM sharedvisits WHERE username=?";
                ResultSet resultSet2=FillQueryWithAnInput(query2,username).executeQuery();
                while (resultSet2.next()) {
                    visitidComboBox.addItem(resultSet2.getObject(1));
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "User doesn't have a recorded location!");
            ex.printStackTrace();
        }
    }



    private void addTextToDisplayArea(String text){
        displayTArea.append(text+"\n");
    }

}

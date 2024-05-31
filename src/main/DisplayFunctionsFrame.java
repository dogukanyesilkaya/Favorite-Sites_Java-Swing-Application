import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class DisplayFunctionsFrame extends JFrame{
    private JPanel mainPanel;
    private JTextArea displayTArea;
    private JLabel displayField;
    private JButton displayImageButton;
    private JButton displayVisitsOfYearButton;
    private JButton bestFeatureOfFoodButton;
    private JLabel visitidLabel;
    private JTextField visitidTField;
    private JTextField yearTField;
    private JLabel yearLabel;
    private JButton mostVisitedButton;
    private JButton visitedInSpringButton;
    private JPanel rightPanel;
    private JButton backButton;

    Connection databaseConnection;
    String username;

    public DisplayFunctionsFrame(Connection connection, String username) {  // PS: display visit displays any visit.Should only display visits made by user
        databaseConnection = connection;
        this.username = username;

        add(mainPanel);
        setSize(900, 600);
        setTitle("FavoriteSites Display Functions Frame");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);


        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        displayVisitsOfYearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayField.setText("(Country Name | City Name | Year Visited | Season Visited | Best Feature | Comment | Rating | Username | Visit ID)");
                DisplayVisitInfoOfYear();
            }
        });

        visitedInSpringButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayField.setText("Display Field");
                DisplayCountriesVisitedInSpring();
            }
        });

        mostVisitedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayField.setText("Display Field");
                DisplayMostVisitedCountry();
            }
        });

        bestFeatureOfFoodButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayField.setText("Display Field");
                DisplayCountriesWithBestFeatureOfFood();
            }
        });

        displayImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayField.setText("Display Field");
                if(CheckVisitID()){
                    DisplayImageOfGivenVisitID();
                }else{
                    JOptionPane.showMessageDialog(null, "Please enter a valid Visit ID");
                }

            }
        });

    }

    private void DisplayImageOfGivenVisitID() {
        displayTArea.setText("");

        String visitID =visitidTField.getText();
        JFrame showImageFrame = new JFrame("Image Of Visit ID: "+visitID);
        showImageFrame.setSize(800, 800);
        showImageFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        showImageFrame.setVisible(true);


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
                showImageFrame.add(locationImage);
                break;
            case 1:
                locationImage = new JLabel(location2ImageIcon);
                showImageFrame.add(locationImage);
                break;
            case 2:
                locationImage = new JLabel(location3ImageIcon);
                showImageFrame.add(locationImage);
                break;
            case 3:
                locationImage = new JLabel(location4ImageIcon);
                showImageFrame.add(locationImage);
                break;
            case 4:
                locationImage = new JLabel(location5ImageIcon);
                showImageFrame.add(locationImage);
                break;
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

    private void DisplayCountriesWithBestFeatureOfFood(){
        displayTArea.setText("");

        String query = "SELECT * FROM visits WHERE bestFeature=? ORDER BY rating DESC";
        try {
            PreparedStatement preparedStatement = databaseConnection.prepareStatement(query);
            preparedStatement.setString(1, "Food");
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
        displayTArea.setText("");

        String query = "SELECT * FROM visits WHERE seasonVisited=?";
        try {
            PreparedStatement preparedStatement = databaseConnection.prepareStatement(query);
            preparedStatement.setString(1, "Spring");
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
        displayTArea.setText("");

        String query = "SELECT countryName,COUNT(*) as VisitCount FROM visits GROUP BY countryName HAVING VisitCount = " +
                                                            "(SELECT COUNT(*) FROM visits GROUP BY countryName LIMIT 1)";
        try {
            PreparedStatement preparedStatement = databaseConnection.prepareStatement(query);
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
        displayTArea.setText("");

        String query = "SELECT * FROM visits WHERE yearVisited=?";
        try {
            PreparedStatement preparedStatement = databaseConnection.prepareStatement(query);
            preparedStatement.setString(1, yearTField.getText());
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

    private void addTextToDisplayArea(String text){
        displayTArea.append(text+"\n");
    }

}

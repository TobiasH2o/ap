import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;

public class DatePicker extends JPanel implements ActionListener {

    Container centerContainer = new Container();
    Container dayCenterContainer = new Container();
    Container dayHeaderContainer = new Container();
    JButton[] dayButtons = new JButton[42];
    JComboBox<? extends String> monthComboBox = new JComboBox<>(
            new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"});
    String[] years;
    DayOfWeek[] days;
    JComboBox<? extends String> yearComboBox;

    String date = "2000-01-01";

    public DatePicker() {

        dayHeaderContainer.setLayout(new GridLayout(1, 7));
        dayHeaderContainer.add(new JLabel("Mo"));
        dayHeaderContainer.add(new JLabel("Tu"));
        dayHeaderContainer.add(new JLabel("We"));
        dayHeaderContainer.add(new JLabel("Th"));
        dayHeaderContainer.add(new JLabel("Fr"));
        dayHeaderContainer.add(new JLabel("Sa"));
        dayHeaderContainer.add(new JLabel("Su"));

        dayCenterContainer.setLayout(new GridLayout(6, 7));

        for (int i = 0;i < dayButtons.length;i++) {
            dayButtons[i] = new JButton("--");
            dayButtons[i].addActionListener(this);
            dayButtons[i].setActionCommand("B" + i);
            dayButtons[i].setFocusPainted(false);
            dayButtons[i].setEnabled(false);
            dayCenterContainer.add(dayButtons[i]);
        }

        centerContainer.setLayout(new BorderLayout());
        centerContainer.add(dayHeaderContainer, BorderLayout.NORTH);
        centerContainer.add(dayCenterContainer, BorderLayout.CENTER);

        years = new String[10];

        years[0] = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"));
        for (int i = 1;i < years.length;i++) {
            years[i] = "" + (int) (Convert.getIfNumeric(years[i - 1]) + 1);
        }

        yearComboBox = new JComboBox<>(years);

        yearComboBox.addActionListener(this);
        monthComboBox.addActionListener(this);
        yearComboBox.setFocusable(false);
        monthComboBox.setFocusable(false);

        this.setLayout(new BorderLayout());
        this.add(centerContainer, BorderLayout.CENTER);
        this.add(monthComboBox, BorderLayout.NORTH);
        this.add(yearComboBox, BorderLayout.SOUTH);

        monthComboBox.setSelectedIndex(LocalDate.now().getMonthValue() - 1);

        calcDayCount();
        calcDayNames();
        buildGrid();

        dayButtons[days[0].getValue() - 2 + LocalDate.now().getDayOfMonth()].doClick();
        dayButtons[days[0].getValue() - 2 + LocalDate.now().getDayOfMonth()].grabFocus();


    }

    private void buildGrid() {
        for (JButton b : dayButtons) {
            b.setEnabled(false);
            b.setText("--");
        }
        int sDay = days[0].getValue() - 2;
        for (int i = 1;i <= days.length;i++) {
            if (("" + i).length() == 2) dayButtons[i + sDay].setText("" + i);
            else dayButtons[i + sDay].setText("0" + i);
            dayButtons[i + sDay].setEnabled(true);
            dayButtons[i + sDay].setActionCommand("B" + i);
        }
    }

    private void calcDayCount() {
        days = new DayOfWeek[LocalDate.of((int) Convert.getIfNumeric("" + yearComboBox.getSelectedItem()),
                                          monthComboBox.getSelectedIndex() + 1, 1).lengthOfMonth()];
        Log.logLine(
                days.length + " days in the selected month (" + Month.of(monthComboBox.getSelectedIndex() + 1) + ")");
    }

    private void calcDayNames() {
        DayOfWeek name;
        for (int i = 0;i < days.length;i++) {
            name = LocalDate.of((int) Convert.getIfNumeric("" + yearComboBox.getSelectedItem()),
                                monthComboBox.getSelectedIndex() + 1, i + 1).getDayOfWeek();
            days[i] = name;
            Log.logLine((i + 1) + ": " + name);
        }
    }

    public String getSelectedDate(){
        return date;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(yearComboBox)) {
            Log.logLine("Year changed to " + yearComboBox.getSelectedItem());
            calcDayNames();
            buildGrid();
        } else if (e.getSource().equals(monthComboBox)) {
            Log.logLine("Month changed to " + monthComboBox.getSelectedItem());
            calcDayCount();
            calcDayNames();
            buildGrid();
        } else if (e.getActionCommand().charAt(0) == 'B') {
            String month = "" + (monthComboBox.getSelectedIndex() + 1);
            if(month.length() == 1){
                month = "0" + month;
            }
            date =
                    yearComboBox.getSelectedItem() + "-" + month + "-" + e.getActionCommand().replace("B", "");
            Log.logLine("Date selected " + date);
        }

    }
}

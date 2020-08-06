import components.Contract;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SearchPanel extends JPanel implements ActionListener, DocumentListener {

    public final JFrame frame = new JFrame();
    private final JTextField searchTerm = new JTextField();
    private final JButton[] displayItems = new JButton[18];
    private String contractID = "";
    private boolean selectedFile = false;
    private int position = 0;

    private Contract[] dataSet;

    public SearchPanel() {

        frame.add(this);
        frame.setVisible(false);
        frame.setSize(400, 900);

        Container centerPanel = new Container();
        centerPanel.setLayout(new GridLayout(displayItems.length, 1));
        for (int i = 0;i < displayItems.length;i++) {
            displayItems[i] = new JButton("------");
            displayItems[i].addActionListener(this);
            displayItems[i].setActionCommand("B");
            centerPanel.add(displayItems[i]);
        }

        Container southPanel = new Container();
        southPanel.setLayout(new GridLayout(1, 2));
        JButton back = new JButton("<<<<<<");
        southPanel.add(back);
        JButton next = new JButton(">>>>>>");
        southPanel.add(next);

        this.setLayout(new BorderLayout());
        this.add(searchTerm, BorderLayout.NORTH);
        this.add(centerPanel, BorderLayout.CENTER);
        this.add(southPanel, BorderLayout.SOUTH);

        searchTerm.getDocument().addDocumentListener(this);

        back.addActionListener(this);
        next.addActionListener(this);
        back.setActionCommand("back");
        next.setActionCommand("next");

    }

    public boolean getSelectedFile() {
        return selectedFile;
    }

    public void setVisible(boolean x) {
        if (x) {
            selectedFile = false;
            searchTerm.setText("");
        }
        frame.setVisible(x);
    }


    public void redraw() {
        Contract[] dataSet;
        dataSet = getFilteredData();
        for (int i = 0;i < displayItems.length;i++) {
            if (dataSet[i] != null) {
                if (dataSet[i].contractID.toUpperCase().trim().startsWith("Q")) {
                    String string = dataSet[i].contractID;
                    displayItems[i].setText("Quote:  " + string.replace("Q", "") + " - " + dataSet[i].contractDate);
                } else {
                    displayItems[i].setText("Contract:  " + dataSet[i].contractID + " - " + dataSet[i].contractDate);
                }
                displayItems[i].setActionCommand("B:" + dataSet[i].contractID);
            } else {
                displayItems[i].setText("------");
                displayItems[i].setActionCommand("B:------");
            }
        }
    }

    public void changePage(int dir) {
        int count = 0;
        int cPos = 0;
        Log.logLine(position);
        while (position > -1 && position < dataSet.length && count < displayItems.length) {
            if ((dataSet[position].contractID.startsWith(searchTerm.getText()) || searchTerm.getText().equals(""))) {
                count++;
                cPos = position;
            }
            position += dir;
        }
        position = cPos;
        if (position < 0) position = 0;
        if (position > dataSet.length) position = dataSet.length;
        redraw();
    }

    public Contract[] getFilteredData() {
        int count = 0;
        int localPosition = position;
        Contract[] returnValue = new Contract[displayItems.length];
        while (count < displayItems.length && localPosition < dataSet.length) {
            if ((dataSet[localPosition].contractID.trim().startsWith(searchTerm.getText()) ||
                 searchTerm.getText().equals(""))) {
                returnValue[count] = dataSet[localPosition];
                count++;
            }
            localPosition++;
        }
        return returnValue;
    }

    public void setData(Contract[] contracts) {
        dataSet = contracts;
        redraw();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String event = e.getActionCommand().split(":")[0];

        switch (event) {

            case "next":
                if (searchTerm.getText().equals("")) {
                    position += displayItems.length;
                } else {
                    changePage(1);
                }
                redraw();
                break;

            case "back":
                if (searchTerm.getText().equals("")) {
                    position -= displayItems.length;
                    if (position < 0) {
                        position = 0;
                    }
                } else {
                    changePage(-1);
                }
                redraw();
                break;

            case "B":
                contractID = e.getActionCommand().split(":")[1];
                if (!contractID.equals("------")) {
                    selectedFile = true;
                    frame.dispose();
                }
                break;

            default:
                Log.logLine("unrecognized event '" + event + "'");
                break;

        }
    }

    public String getContractID() {
        return contractID;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        position = 0;
        redraw();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {

    }

    @Override
    public void changedUpdate(DocumentEvent e) {
    }
}

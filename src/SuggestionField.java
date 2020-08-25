import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class SuggestionField implements DocumentListener, KeyListener, FocusListener {

    String[] values;
    ArrayList<String> displayValues = new ArrayList<>(0);
    JDialog suggestionFrame;
    JPanel suggestionPanel = new JPanel();
    Color backGround = new Color(109, 104, 104, 133);
    Color selectBackGround = new Color(109, 104, 104, 133);
    Color textColor = new Color(5, 19, 88, 255);
    Color selectTextColor = new Color(115, 134, 238, 255);
    BoxLayout bl = new BoxLayout(suggestionPanel, BoxLayout.Y_AXIS);
    ArrayList<JTextField> boxes = new ArrayList<>(0);

    boolean grabbingFocus = false;
    boolean sticky = false;

    int selectedBox = -1;
    int selectedEntry = 0;
    int displaySize = 50;

    public SuggestionField(String[] values, JFrame parentDisplay) {
        suggestionFrame = new JDialog(parentDisplay);
        suggestionFrame.add(suggestionPanel);
        suggestionFrame.setUndecorated(true);
        suggestionFrame.setAlwaysOnTop(true);
        suggestionFrame.setFocusable(false);
        suggestionPanel.setFocusable(false);
        bl.maximumLayoutSize(suggestionPanel);
        this.values = values;
    }

    public void updateValues(String[] values) {
        this.values = values;
    }

    public void updateSuggestions() {
        boolean added = false;
        boolean add;
        int count = displaySize;
        displayValues.clear();
        if (selectedBox > -1 && selectedBox < boxes.size())
            if (values != null && boxes.get(selectedBox).getText().length() > 0) {
                for (int i = 0;i < values.length && count > 0;i++) {
                    add = true;
                    for (int k = 0;k < boxes.get(selectedBox).getText().length() && k < values[i].length();k++) {
                        if (values[i].toUpperCase().charAt(k) !=
                            boxes.get(selectedBox).getText().toUpperCase().charAt(k)) {
                            add = false;
                            break;
                        }
                    }
                    if (add && values[i].length() >= boxes.get(selectedBox).getText().length()) {
                        added = true;
                        count--;
                        displayValues.add(values[i]);
                    }
                }
            }
        if (!added) {
            displayValues.add("----");
        }
    }

    private void updateDisplay() {

        suggestionFrame.setSize(boxes.get(selectedBox).getWidth(), 16 * displayValues.size());
        suggestionPanel.removeAll();
        suggestionPanel.setLayout(new BoxLayout(suggestionPanel, BoxLayout.Y_AXIS));
        for (int i = 0;i < displayValues.size();i++) {
            JLabel a = new JLabel(displayValues.get(i));
            if (i == selectedEntry) {
                a.setBackground(selectBackGround);
                a.setForeground(selectTextColor);
            } else {
                a.setBackground(backGround);
                a.setForeground(textColor);
            }
            suggestionPanel.add(a);
        }
        suggestionPanel.revalidate();
        suggestionFrame.revalidate();
        Point possition = new Point(boxes.get(selectedBox).getLocationOnScreen().x,
                boxes.get(selectedBox).getLocationOnScreen().y + boxes.get(selectedBox).getHeight());
        suggestionFrame.setLocation(possition);
        suggestionFrame.repaint();
        suggestionFrame.setVisible(true);
    }

    public void addBox(JTextField newBox) {
        boxes.add(newBox);
        newBox.setFocusTraversalKeysEnabled(false);
        newBox.setFocusable(true);
        newBox.getDocument().addDocumentListener(this);
        newBox.addKeyListener(this);
        newBox.addFocusListener(this);
    }

    public void purge() {
        //
        for (JTextField e : boxes) {
            e.removeFocusListener(this);
            e.removeKeyListener(this);
            e.getDocument().removeDocumentListener(this);
        }
        suggestionFrame.setVisible(false);
        boxes.clear();
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        updateSuggestions();
        updateDisplay();
        selectedEntry = 0;
        if (selectedBox > -1) {
            boxes.get(selectedBox).grabFocus();
            sticky = true;
        }

    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        updateSuggestions();
        updateDisplay();
        selectedEntry = 0;
        if (selectedBox > -1) {
            boxes.get(selectedBox).grabFocus();
            sticky = true;
        }
    }

    @Override
    public void changedUpdate(DocumentEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int ID = e.getKeyCode();
        switch (ID) {
            case 40:
                if (selectedEntry < displayValues.size() - 1) selectedEntry++;
                updateDisplay();
                break;

            case 38:
                if (selectedEntry > 0) selectedEntry--;
                updateDisplay();
                break;

            case 10:
                if (!(displayValues.get(selectedEntry).equals("----") && boxes.get(selectedBox).getText().isEmpty()))
                    boxes.get(selectedBox).setText(displayValues.get(selectedEntry));
                suggestionFrame.setVisible(false);
                sticky = false;
                break;

            case 13:
            case 27:
                suggestionFrame.setVisible(false);
                if (selectedEntry >= 0 && selectedEntry < displayValues.size()) {
                    if (!displayValues.get(selectedEntry).equals("----")) {
                        boxes.get(selectedBox).setText(displayValues.get(selectedEntry));
                        for (String i : values) {
                            if (boxes.get(selectedBox).getText().equalsIgnoreCase(i)) {
                                boxes.get(selectedBox).setText(boxes.get(selectedBox).getText().toUpperCase());
                                break;
                            }
                        }
                    }
                }
                sticky = false;
                break;

            default:
                break;
        }
        if (selectedBox != -1) boxes.get(selectedBox).grabFocus();
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    private boolean isNumeric(String x) {
        if (x == null) {
            return false;
        }
        try {
            Double.parseDouble(x);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    @Override
    public void focusGained(FocusEvent e) {
        if (!grabbingFocus) {
            Log.logLine("Focus Gained");
            GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            for (int i = 0;i < boxes.size();i++) {
                if (e.getComponent() == boxes.get(i)) {
                    selectedBox = i;
                    break;
                }
            }
            displaySize = (gd.getDisplayMode().getWidth() - boxes.get(selectedBox).getLocationOnScreen().y -
                           boxes.get(selectedBox).getHeight()) /
                          boxes.get(selectedBox).getFontMetrics(boxes.get(selectedBox).getFont()).getHeight();
        } else {
            Log.logLine("Grabbed Focus");
            grabbingFocus = false;
        }

    }

    public void apply() {
        Log.logLine("Checking entry");
        suggestionFrame.setVisible(false);
        updateSuggestions();
        Log.logLine("Updated suggestions");
        if (selectedBox != -1) if (selectedEntry >= 0 && selectedEntry < displayValues.size()) {
            if (!displayValues.get(selectedEntry).equals("----")) {
                boxes.get(selectedBox).setText(displayValues.get(selectedEntry));
                for (String i : values) {
                    if (boxes.get(selectedBox).getText().equalsIgnoreCase(i)) {
                        Log.logLine("Set Values");
                        boxes.get(selectedBox).setText(boxes.get(selectedBox).getText().toUpperCase());
                        break;
                    }
                }
            }
        }
        selectedBox = -1;
        sticky = false;
    }

    @Override
    public void focusLost(FocusEvent e) {
        Log.logLine("Focus Lost");
        if (sticky) {
            grabbingFocus = true;
            boxes.get(selectedBox).grabFocus();
            Log.logLine("Grabbing Focus");
        } else {
            Log.logLine("Leaving");
            suggestionFrame.setVisible(false);
        }
    }

}

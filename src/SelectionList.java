import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class SelectionList extends Container {

    ArrayList<String> itemTitles = new ArrayList<>(0);
    ArrayList<String> optionTitles = new ArrayList<>(0);
    ArrayList<Container> line = new ArrayList<>(0);
    ArrayList<JCheckBox[]> checkBoxes = new ArrayList<>(0);

    public void addOption(String title) {
        optionTitles.add(title);
        rebuild();
    }

    public boolean checkItemOption(int optionIndex, int itemIndex) {
        return checkBoxes.get(itemIndex)[optionIndex].isSelected();
    }

    public void rebuild(){
        line.clear();
        checkBoxes.clear();
        Container c;
        JCheckBox j;
        JCheckBox[] jA;
        for(String itemTitle : itemTitles){
            c = new Container();
            c.setLayout(new GridLayout(1, optionTitles.size() + 1));
            c.add(new JLabel(itemTitle));
            jA = new JCheckBox[optionTitles.size()];
            for (int i = 0, optionTitlesSize = optionTitles.size();i < optionTitlesSize;i++) {
                j = new JCheckBox();
                jA[i] = j;
                c.add(j);
            }
            checkBoxes.add(jA);
            line.add(c);
        }
        redraw();
    }

    public void redraw() {
        this.removeAll();
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        Container c = new Container();
        c.setLayout(new GridLayout(1, optionTitles.size() + 1));
        c.add(new JLabel("Options"));
        for (String optionTitle : optionTitles)
            c.add(new JLabel(optionTitle));
        this.add(c);
        for (Container line : line)
            this.add(line);
        this.revalidate();
        this.repaint();
    }

    public void addItem(String name) {
        Container c = new Container();
        JCheckBox j;
        JCheckBox[] jA = new JCheckBox[optionTitles.size()];
        itemTitles.add(name);
        c.setLayout(new BoxLayout(c, BoxLayout.X_AXIS));
        c.add(new JLabel(name));
        for (int i = 0, optionTitlesSize = optionTitles.size();i < optionTitlesSize;i++) {
            j = new JCheckBox();
            jA[i] = j;
            c.add(j);
        }
        checkBoxes.add(jA);
        line.add(c);
        redraw();
    }

}

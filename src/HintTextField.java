import jdk.jfr.Description;

import javax.swing.*;
import java.awt.*;

public class HintTextField extends JTextField {


    public static final int RIGHT_LEADING = 20;
    public static final int CENTER_HIDDEN = 21;
    private final int stringPosition;
    protected String hint;

    public HintTextField(String hint, int stringPosition) {
        this.hint = hint;
        this.stringPosition = stringPosition;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Insets i = getInsets();
        // Calculates bottom of suggestion text
        int y = i.top + ((getHeight() - i.top - i.bottom) / 2) + (g.getFontMetrics().getAscent() / 2) - 2;
        // Minimum x position
        int x = getInsets().left;
        // Gets a medium colour between foreground and background
        int re = getMid(this.getBackground().getRed(), this.getForeground().getRed());
        int gr = getMid(this.getBackground().getGreen(), this.getForeground().getGreen());
        int bl = getMid(this.getBackground().getBlue(), this.getForeground().getBlue());
        g.setColor(new Color(re, gr, bl, 190));
        if (stringPosition == RIGHT_LEADING) g.drawString(hint, x + g.getFontMetrics().stringWidth(getText() + " "), y);
        else if (stringPosition == CENTER_HIDDEN && getText().length() == 0) g.drawString(hint, x, y);
    }

    private int getMid(int a, int b) {
        return ((Math.max(a, b) - Math.min(a, b)) / 2) + Math.min(a, b);
    }

    public void setHint(String s) {
        hint = s;
    }
}
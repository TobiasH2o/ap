import javax.swing.*;
import java.awt.*;

public class HintTextField extends JTextField {

    protected String hint;
    private final int stringPosition;
    public static final int RIGHT_LEADING = 20;
    public static final int CENTER_HIDDEN = 21;

    public HintTextField(String hint, int stringPosition) {
        this.hint = hint;
        this.stringPosition = stringPosition;
    }

    public HintTextField(String hint) {
        this.hint = hint;
        this.stringPosition = 21;
    }

    public HintTextField() {
        this.stringPosition = 21;
    }

    public void setHint(String hint) {
        this.hint = hint;
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (getText().length() == 0 || (getText().length() > 0 && stringPosition == RIGHT_LEADING)) {
            int h = getHeight();
            ((Graphics2D) g)
                    .setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            Insets ins = getInsets();
            FontMetrics fm = g.getFontMetrics();
            int c0 = getBackground().getRGB();
            int c1 = getForeground().getRGB();
            int m = 0xfefefefe;
            int c2 = ((c0 & m) >>> 1) + ((c1 & m) >>> 1);
            g.setColor(new Color(c2, true));
            if(stringPosition == RIGHT_LEADING)
                g.drawString(hint, ins.left + g.getFontMetrics().stringWidth(getText() + " "),
                             h / 2 + fm.getAscent() / 2 - 2);
            else
                g.drawString(hint, ins.left, h / 2 + fm.getAscent() / 2 - 2);
        }
    }
}
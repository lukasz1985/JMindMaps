package map;

import main.App;
import main.Globals;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class TitleEntry extends Widget {

    static String TITLE = "TITLE";


    public TitleEntry(Node node) {
        this.node = node;
        this.font = new Font("Arial", 0, 18);
        this.text = "";
    }

    Node node;
    Font font;
    String text;

    boolean has_focus = false;
    int caret_index;


    public void paint(Graphics2D graphics) {
        Rectangle bounds = this.calcBounds();

        // Draw the text:
        graphics.setFont(this.font);
        int y = bounds.y + this.getHeight() - this.getFontDescent();
        if (this.text.isEmpty()) {
            graphics.setColor(Color.LIGHT_GRAY);
            graphics.drawString(TITLE, bounds.x, y);
        }
        else {
            graphics.setColor(Color.BLACK);
            graphics.drawString(this.text, bounds.x, y);
        }

        // Draw the caret if this entry has focus:
        if (this.has_focus) {
            Point caret_position = this.getCaretPosition();
            graphics.drawRect(caret_position.x, caret_position.y, 1, this.getStringHeight(this.text));
        }
    }

    public void setText(String text) {
        this.text = text;
        this.recalculateSize();
        this.node.recalculateSize();
        this.view.repaint();
    }


    public void recalculateSize() {
        int width  = this.getStringWidth(this.text);
        int height = this.getStringHeight(this.text);
        width = Math.max(width, getStringWidth(TITLE));
        height = Math.max(height, getStringHeight(TITLE));
        this.size = new Dimension(width, height);
    }


    private int getStringWidth(String string) {
        return Globals.graphics.getFontMetrics(this.font).stringWidth(string);
    }


    private int getStringHeight(String string) {
        return (int) this.font.getLineMetrics(string, Globals.graphics.getFontRenderContext()).getHeight();
    }


    private int getFontDescent() {
        return (int) this.font.getLineMetrics(this.text, Globals.graphics.getFontRenderContext()).getDescent();
    }


    public void onClick(MouseEvent event) {
        Point mouse = new Point(event.getX(), event.getY());
        this.caret_index = this.getCaretIndex(mouse);
        this.view.repaint();
    }

    public void gainFocus() {
        this.has_focus = true;
    }

    public void loseFocus() {
        this.has_focus = false;
    }


    public boolean keyPressed(KeyEvent event) {
        if (event.getKeyCode() == 37) {
            // Left arrow was pressed:
            this.caret_index -= 1;
            this.view.repaint();

        }
        else if(event.getKeyCode() == 39) {
            // Right arrow was pressed:
            this.caret_index += 1;
            this.view.repaint();

        }
        else if (event.getKeyCode() == 8 && this.caret_index >= 1) {
            // Backspace was pressed:
            String text_before = this.text.substring(0, this.caret_index - 1);
            String text_after  = this.text.substring(this.caret_index);
            String new_text    = text_before + text_after;
            this.setText(new_text);
            this.caret_index -= 1;
        }
        else if (event.getKeyCode() == 127 && this.caret_index < this.text.length()) {
            String text_before = this.text.substring(0, this.caret_index);
            String text_after  = this.text.substring(this.caret_index + 1);
            String new_text    = text_before + text_after;
            this.setText(new_text);
        }
        else if (event.getKeyCode() == 32) {
            String text_before = this.text.substring(0, this.caret_index);
            String text_after  = this.text.substring(this.caret_index);
            String new_text    = text_before + " " +  text_after;
            this.caret_index += 1;
            this.setText(new_text);
        }

        if (this.caret_index < 0) this.caret_index = 0;
        if (this.caret_index > this.text.length()) this.caret_index = this.text.length();

        // At this point the node panel node is set to the current node, so we can call to update the current state:
        App.instance.side_panel.updateNodePanel();

        return true;
    }


    public boolean keyReleased(KeyEvent event) {
        return true;
    }


    public boolean keyTyped(KeyEvent event) {
        char character = event.getKeyChar();
        if (!this.characterCanBeEntered(character)) return false;
        String string_before = this.text.substring(0, this.caret_index);
        String string_after  = this.text.substring(this.caret_index);
        String new_text = string_before + character + string_after;
        this.setText(new_text);
        this.caret_index += 1;

        if (this.caret_index < 0) this.caret_index = 0;
        if (this.caret_index > this.text.length()) this.caret_index = this.text.length();

        // At this point the node panel node is set to the current node, so we can call to update the current state:
        App.instance.side_panel.updateNodePanel();

        return true;
    }


    private boolean characterCanBeEntered(char character) {
        return Character.isAlphabetic(character) || Character.isDigit(character) || character == '.' || character == ',' || character == '-';
    }


    private Point getCaretPosition() {
        Rectangle bounds = this.calcBounds();
        int x = bounds.x;
        int y = bounds.y;
        for(int i = 0; i < this.caret_index; i++) {
            String character = String.valueOf(this.text.charAt(i));
            x += this.getStringWidth(character);
        }
        return new Point(x, y);
    }


    // Calculate the index of caret given a position, this function will return an approximate caret index used
    // when clicking on this widget, to position the caret under the mouse cursor.
    private int getCaretIndex(Point position) {
        if (this.text.isEmpty()) return 0;
        Rectangle bounds = this.calcBounds();
        int caret_index = 0;
        int current_width = bounds.x;
        while(true) {
            String character = String.valueOf(this.text.charAt(caret_index));
            int character_width = this.getStringWidth(character);
            current_width += character_width ;
            if (current_width - character_width / 2 > position.x) return caret_index;;
            caret_index += 1;

            // If we are beyond the string return what's left:
            if (caret_index == this.text.length()) {
                return caret_index;
            }
        }
    }


}

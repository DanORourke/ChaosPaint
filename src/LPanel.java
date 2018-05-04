import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;

public class LPanel extends JPanel implements ShapeTab{
    private final Largest largest;

    LPanel(Largest largest){
        this.largest = largest;
        setLayout(new GridBagLayout());
        setBackground(Largest.BACKGROUND);
    }

    @Override
    public LinkedList<Vertex> getShape() {
        return null;
    }

    @Override
    public void click(int x, int y) {

    }
}

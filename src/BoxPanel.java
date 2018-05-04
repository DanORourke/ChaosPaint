import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;

public class BoxPanel extends JPanel implements ShapeTab{
    private final Largest largest;
    private LinkedList<Vertex> shape = new LinkedList<>();

    BoxPanel(Largest largest){
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

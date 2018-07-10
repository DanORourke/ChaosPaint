import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
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

    @Override
    public int[] getRes(){
        return new int[]{1, 1};
    }

    @Override
    public BufferedImage getActiveImage(){
        return new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    }
}

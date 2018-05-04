import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;

public class ChaosPanel extends JPanel implements ShapeTab{
    private final Largest largest;
    private LinkedList<Vertex> shape = new LinkedList<>();
    private Color color = Color.BLACK;
    private double gravity = 0.5;

    ChaosPanel(Largest largest){
        this.largest = largest;
        setLayout(new GridBagLayout());
        setBackground(Largest.BACKGROUND);
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public LinkedList<Vertex> getShape() {
        return null;
    }

    @Override
    public void click(int x, int y) {
        //shape.add(new Vertex(x, y));
        Stamp stamp = largest.getStamp();
        int size = 100;
        if (stamp == null || stamp.isEmpty()){
            stamp = new Stamp(0, 0, new byte[size][size][4]);
        }
        byte[][][] data = stamp.getData();
        for (int i = 0; i < size; i++){
            for (int j = 0; j < size; j++){
                if (i > -1 && i < 1024 && j > -1 && j < 1024){
                    data[i][j][0] = (byte)(200);
                    data[i][j][1] = (byte)(10 + i);
                    data[i][j][2] = (byte)(50 + j);
                    data[i][j][3] = (byte)255;
                }
            }
        }
        stamp.setxOffset(x);
        stamp.setyOffset(y);
        largest.setStamp(stamp);
    }
}


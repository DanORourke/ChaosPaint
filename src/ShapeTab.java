import java.util.LinkedList;

public interface ShapeTab {

    public LinkedList<Vertex> getShape();

    public default Stamp getStamp() {
        return new Stamp();
    }

    public default void redraw(){

    }

    public void click(int x, int y);
}

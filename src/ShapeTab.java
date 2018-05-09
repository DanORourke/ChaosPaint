import java.awt.*;
import java.util.LinkedList;

public interface ShapeTab {

    public LinkedList<Vertex> getShape();

    public default Stamp getStamp() {
        return new Stamp();
    }

    public default void redraw(){

    }

    public default void reset(){

    }

    public default void shiftDrag(int ox, int oy, int x, int y){

    }

    public default void shiftClick(int x, int y){

    }

    public default void click(int x, int y){

    }
}

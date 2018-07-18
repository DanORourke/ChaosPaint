
import java.awt.image.BufferedImage;
import java.util.LinkedList;

public interface ShapeTab {

    int[] getRes();

    BufferedImage getActiveImage();

    default void reset(){

    }

    default void shiftDrag(int ox, int oy, int x, int y){

    }

    default void shiftClick(int x, int y){

    }

    default void click(int x, int y){

    }
}

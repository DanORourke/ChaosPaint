import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Random;

public class CanvasPanel extends JPanel {
    private int pageSize = 600;
    private final ArrayList<double[]> points = new ArrayList<>();
    private final ArrayList<double[]> first = new ArrayList<>();
    private final ArrayList<double[]> second = new ArrayList<>();
    private Largest largest;

    CanvasPanel(){
        setPreferredSize(new Dimension(pageSize, pageSize));
        MyMouseListener ml = new MyMouseListener();
        addMouseListener(ml);
        addMouseMotionListener(ml);
    }

    public void paintComponent(Graphics g){
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setFont(new Font("TimesRoman", Font.PLAIN, 20));
        super.paintComponent(g2);
        for (double[] p : points){
            Random rand = new Random();

            int x = (int)(p[0] * pageSize);
            int y = (int)(p[1] * pageSize);
            int size = (int)(p[2] * pageSize);
            //size = (size == 0) ? 1 : size;
            int rColor = (int)(p[3] * 256);
            int gColor = (int)(p[4] * 256);
            int bColor = (int)(p[5] * 256);
            int alpha = (int)(p[6] * 256);

            g2.setStroke(new BasicStroke(size));
            Color color = new Color(rColor, gColor, bColor, alpha);
            g2.setColor(color);
            //g2.drawOval(x, y, size,size);
            if (size == 0){
                if (rand.nextDouble() < (p[2] * pageSize)){
                    g2.setStroke(new BasicStroke(1));
                    g2.drawLine(x, y, x, y);
                }
            }else{
                g2.drawLine(x, y, x, y);
            }
        }

        int[] firstX = new int[first.size()];
        int[] firstY = new int[first.size()];
        int i = 0;
        for (double[] p : first){
            int x = (int)(p[0] * pageSize);
            int y = (int)(p[1] * pageSize);
            int size = (int)(p[2] * pageSize);
            size = (size == 0) ? 1 : size;
            int rColor = (int)(p[3] * 256);
            int gColor = (int)(p[4] * 256);
            int bColor = (int)(p[5] * 256);
            int alpha = (int)(p[6] * 256);

            g2.setStroke(new BasicStroke(size));
            Color color = new Color(rColor, gColor, bColor, alpha);
            g2.setColor(color);
            firstX[i] = x;
            firstY[i] = y;
            //g2.drawOval(x, y, size,size);
            g2.drawLine(x, y, x, y);
            i++;
        }
        g2.setColor(Color.RED);
        g2.setStroke(new BasicStroke(1));
        g2.drawPolygon(firstX, firstY, first.size());

        int[] secondX = new int[second.size()];
        int[] secondY = new int[second.size()];
        int j = 0;
        for (double[] p : second){
            int x = (int)(p[0] * pageSize);
            int y = (int)(p[1] * pageSize);
            int size = (int)(p[2] * pageSize);
            size = (size == 0) ? 1 : size;
            int rColor = (int)(p[3] * 256);
            int gColor = (int)(p[4] * 256);
            int bColor = (int)(p[5] * 256);
            int alpha = (int)(p[6] * 256);

            g2.setStroke(new BasicStroke(size));
            Color color = new Color(rColor, gColor, bColor, alpha);
            g2.setColor(color);
            secondX[j] = x;
            secondY[j] = y;
            //g2.drawOval(x, y, size,size);
            g2.drawLine(x, y, x, y);
            j++;
        }
        g2.setColor(Color.BLUE);
        g2.setStroke(new BasicStroke(1));
        g2.drawPolygon(secondX, secondY, second.size());
    }

    void setPageSize(int pageSize){
        this.pageSize = pageSize;
        setPreferredSize(new Dimension(pageSize, pageSize));
    }

    int getPageSize(){
        return pageSize;
    }

    void erase(){
        points.clear();
        revalidate();
        repaint();
    }



    ArrayList<double[]> getPoints() {
        return points;
    }

    ArrayList<double[]> getFirst() {
        return first;
    }

    ArrayList<double[]> getSecond() {
        return second;
    }

    void setLargest(Largest largest) {
        this.largest = largest;
    }

    class MyMouseListener extends MouseAdapter {	//inner class inside GUI.DrawingPanel
        private Point origin;
        public void mouseClicked(MouseEvent e) {
            double[] point = {(double)e.getX()/pageSize, (double)e.getY()/pageSize};
            largest.addClick(point);
            System.out.println(point[0] + " " + point[1]);
        }
        public void mousePressed(MouseEvent e) {
            origin = new Point(e.getPoint());
        }
        public void mouseReleased(MouseEvent e) {
            origin = null;
        }
        public void mouseDragged(MouseEvent e) {
            //move the view around the engine
            if (SwingUtilities.isLeftMouseButton(e) && origin != null) {
                JViewport viewPort =
                        (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class, CanvasPanel.this);
                if (viewPort != null) {
                    //100 is there to correct for the filler JLabels in the drawing panel
                    int deltaX = origin.x - e.getX() - DrawingPanel.MARGIN;
                    int deltaY = origin.y - e.getY() - DrawingPanel.MARGIN;

                    Rectangle view = viewPort.getViewRect();
                    view.x += deltaX;
                    view.y += deltaY;

                    CanvasPanel.this.scrollRectToVisible(view);
                }
            }
        }
    } //end of MyMouseListener class
}

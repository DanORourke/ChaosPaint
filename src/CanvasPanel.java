import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class CanvasPanel extends JPanel {
    private int xPage = 512;
    private int yPage = 512;
    private int xRes = 1024;
    private int yRes = 1024;
    private BufferedImage image = new BufferedImage(xRes, yRes, BufferedImage.TYPE_INT_ARGB);
    private Largest largest;

    CanvasPanel(){
        super();
        setPreferredSize(new Dimension(xPage, yPage));
        MyMouseListener ml = new MyMouseListener();
        addMouseListener(ml);
        addMouseMotionListener(ml);
    }

    public void paintComponent(Graphics g){
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setFont(new Font("TimesRoman", Font.PLAIN, 20));
        super.paintComponent(g2);
        image = largest.getActiveImage();
        g.drawImage(image, 0, 0, getWidth(), getHeight(), this);

    }

    void setXPage(int xPage){
        this.xPage = xPage;
    }

    void setYPage(int yPage){
        this.yPage = yPage;
    }

    @Override
    public Dimension getPreferredSize(){
        return new Dimension(xPage, yPage);
    }

    int getXRes(){
        return xRes;
    }

    public void setxRes(int xRes) {
        this.xRes = xRes;
    }

    int getYRes(){
        return yRes;
    }

    public void setyRes(int yRes) {
        this.yRes = yRes;
    }

    int getxPage(){
        return xPage;
    }

    int getyPage(){
        return yPage;
    }

    void changeRes(int[] dim){
        xRes = dim[0];
        yRes = dim[1];
        xPage = xRes/2;
        yPage = yRes/2;
        image = new BufferedImage(xRes, yRes, BufferedImage.TYPE_INT_ARGB);
        revalidate();
        repaint();
    }

    void setLargest(Largest largest) {
        this.largest = largest;
    }

    class MyMouseListener extends MouseAdapter {	//inner class inside GUI.DrawingPanel
        private Point origin;
        public void mouseClicked(MouseEvent e) {
            int x = ((xRes*e.getX())/xPage);
            int y = ((yRes*e.getY())/yPage);
            if (e.isShiftDown() || SwingUtilities.isRightMouseButton(e)){
                largest.shiftClick(x, y);
            }else{
                largest.click(x, y);
            }
            revalidate();
            repaint();
        }
        public void mousePressed(MouseEvent e) {
            origin = new Point(e.getPoint());
        }
        public void mouseReleased(MouseEvent e) {
            origin = null;
        }
        public void mouseDragged(MouseEvent e) {
            //move the view around
            if (origin != null) {
                if (SwingUtilities.isRightMouseButton(e) || e.isShiftDown()){
                    int x = ((xRes*e.getX())/xPage);
                    int y = ((yRes*e.getY())/yPage);
                    int ox = (int)((xRes*origin.getX())/xPage);
                    int oy = (int)((yRes*origin.getY())/yPage);
                    largest.shiftDrag(ox, oy, x, y);
                }else{
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
        }
        public void mouseMoved(MouseEvent e){
            int x = ((xRes*e.getX())/xPage);
            int y = ((yRes*e.getY())/yPage);
            largest.setCordLabelText(x + ", " + y);
        }
    } //end of MyMouseListener class
}

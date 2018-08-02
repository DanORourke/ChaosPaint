import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public class DrawingPanel extends JPanel {
    private final CanvasPanel canvasPanel;
    static final int MARGIN = 100;

    DrawingPanel(CanvasPanel canvasPanel){
        super();
        this.canvasPanel = canvasPanel;
        this.setBackground(Largest.BACKGROUND);
        MyMouseListener ml = new MyMouseListener();
        addMouseListener(ml);
        addMouseMotionListener(ml);
        addMouseWheelListener(ml);
        setPanel();
    }

    private void setPanel(){
        setLayout(new GridBagLayout());

        JPanel northPanel = new JPanel();
        northPanel.setPreferredSize(new Dimension(0, DrawingPanel.MARGIN));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 3;
        c.weightx = 1.0;
        c.weighty = 1.0;
        add(northPanel, c);

        JPanel eastPanel = new JPanel();
        eastPanel.setPreferredSize(new Dimension(DrawingPanel.MARGIN, 0));
        c = new GridBagConstraints();
        c.gridx = 2;
        c.gridy = 1;
        c.weightx = 1.0;
        c.weighty = 1.0;
        add(eastPanel, c);

        JPanel westPanel = new JPanel();
        westPanel.setPreferredSize(new Dimension(DrawingPanel.MARGIN, 0));
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1.0;
        c.weighty = 1.0;
        add(westPanel, c);

        JPanel southPanel = new JPanel();
        southPanel.setPreferredSize(new Dimension(0, DrawingPanel.MARGIN));
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 3;
        c.weightx = 1.0;
        c.weighty = 1.0;
        add(southPanel, c);

        canvasPanel.setBackground(Color.WHITE);
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 1;
        c.weightx = 0.0;
        c.weighty = 0.0;
        add(canvasPanel, c);
    }

    class MyMouseListener extends MouseAdapter {	//inner class inside DrawingPanel
        private Point origin;
        public void mouseClicked(MouseEvent e) {

        }
        public void mousePressed(MouseEvent e) {
            origin = new Point(e.getPoint());
        }

        public void mouseReleased(MouseEvent e) {
            origin = null;
        }

        public void mouseMoved(MouseEvent e){

        }
        public void mouseDragged(MouseEvent e) {
            //move the view around the engine
            if (SwingUtilities.isLeftMouseButton(e) && origin != null) {
                JViewport viewPort =
                        (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class, DrawingPanel.this);
                if (viewPort != null) {
                    int deltaX = origin.x - e.getX();
                    int deltaY = origin.y - e.getY();

                    Rectangle view = viewPort.getViewRect();
                    view.x += deltaX;
                    view.y += deltaY;

                    DrawingPanel.this.scrollRectToVisible(view);
                }
            }
        }
        public void mouseWheelMoved(MouseWheelEvent e) {
            //zoom in and out, double or half
            int notches = e.getWheelRotation();
            int oldxP = canvasPanel.getxPage();
            int oldyP = canvasPanel.getyPage();
            if ((notches > 0 && ((oldxP <= 128) || (oldyP <= 128))) ||
                    (notches < 0 && ((oldxP >= 16384)|| (oldyP >= 16384)))){
                return;
            }
            int newxP = oldxP;
            int newyP = oldyP;
            if (notches > 0){
                newxP = newxP/2;
                newyP = newyP/2;
            }else{
                newxP = newxP*2;
                newyP = newyP*2;
            }
            int w = 0;
            int h = 0;
            int ocx = 0;
            int ocy = 0;

            JViewport viewPort =
                    (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class, DrawingPanel.this);
            if (viewPort != null) {
                Rectangle view = viewPort.getViewRect();
                w = view.width;
                h = view.height;
                ocx = view.x + w/2;
                ocy = view.y + h/2;
            }
            int oldwp = (DrawingPanel.this.getWidth() - oldxP)/2;
            int oldhp = (DrawingPanel.this.getHeight()  - oldyP)/2;
            ocx -= oldwp;
            ocy -= oldhp;

            int newwp = 100;
            int newhp = 100;
            if ((newxP + 200) < w){
                newwp = (w - newxP)/2;
            }
            if ((newyP + 200) < h){
                newhp = (h - newyP)/2;
            }

            int freshCX = newwp + (int)(ocx * ((double)newxP/oldxP)) - (w/2);
            int freshCY = newhp + (int)(ocy * ((double)newyP/oldyP)) - (h/2);

            canvasPanel.setXPage(newxP);
            canvasPanel.setYPage(newyP);

            Rectangle rec = new Rectangle(freshCX, freshCY, w, h);

            JScrollPane scroll =
                    (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, DrawingPanel.this);
            if (scroll != null){

                scroll.setViewportView(DrawingPanel.this);
            }
            DrawingPanel.this.scrollRectToVisible(rec);
        }
    } //end of MyMouseListener class
}

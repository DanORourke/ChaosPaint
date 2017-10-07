import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

class DrawingPanel extends JPanel {
    private final CanvasPanel canvasPanel;
    static final int MARGIN = 100;

    DrawingPanel(CanvasPanel canvasPanel){
        this.canvasPanel = canvasPanel;
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

    private void setXY(int pageSize){
        System.out.println(pageSize);
        canvasPanel.setPageSize(pageSize);
        revalidate();
    }

    class MyMouseListener extends MouseAdapter {	//inner class inside GUI.DrawingPanel
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
            //zoom in and out
            int notches = e.getWheelRotation();
            int oldP = canvasPanel.getPageSize();
            int newP = oldP;
            if (notches > 0){
                newP -= newP/10;
            }else{
                newP += newP/10;
            }
            if (newP < 251){
                newP = 250;
            }
            if (newP > 10000){
                newP = 10000;
            }
            int w = 0;
            int h = 0;
            int cx = 0;
            int cy = 0;
            JViewport viewPort =
                    (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class, DrawingPanel.this);
            if (viewPort != null) {
                Rectangle view = viewPort.getViewRect();
                w = view.width;
                h = view.height;
                cx = view.x + w/2;
                cy = view.y + h/2;
            }
            setXY(newP);
            repaint();
            double factor = ((double)newP)/oldP;
            int freshX = (int)(cx * factor) - w/2;
            int freshY = (int)(cy * factor) - h/2;

            JScrollPane scroll =
                    (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, DrawingPanel.this);
            if (scroll != null){
                //this gets scroll to update max on scrollbars for some reason
                //even though revalidate does not
                scroll.setViewportView(DrawingPanel.this);
            }
            Rectangle rec = new Rectangle(freshX, freshY, w, h);
            DrawingPanel.this.scrollRectToVisible(rec);

        }
    } //end of MyMouseListener class
}

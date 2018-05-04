import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class CanvasPanel extends JPanel {
    //private int pageSize = 512;
    private int xPage = 512;
    private int yPage = 512;
    private int xRes = 1024;
    private int yRes = 1024;
    private BufferedImage image = new BufferedImage(xRes, yRes, BufferedImage.TYPE_INT_ARGB);
    private byte[][][] points = new byte[xRes][yRes][4]; // 0 = r; 1 g; 2 = b; 3 = a
    private byte[][][] pointsTemp = new byte[xRes][yRes][4]; // 0 = r; 1 g; 2 = b; 3 = a
    private byte[][][] pointsStamp = new byte[xRes][yRes][4]; // 0 = r; 1 g; 2 = b; 3 = a
    private int whichPoints = 0; // 0 = points; 1 = pointsTemp; 2 = pointsStamp
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
        int listening = largest.getListening();//0 = main; 1 = chaos; 2 = L; 3 = box; 4 = julia
        byte[][][] p;
        if (listening != 0){
            p = largest.getListeningStamp().getData();
            if (p == null){
                return;
            }
        }else{
            if (whichPoints == 0){
                p = points;
            }else if (whichPoints == 1){
                p = pointsTemp;
            }else{
                p = pointsStamp;
            }
        }
        for (int x = 0 ; x < xRes; x++){
            for (int y = 0; y < yRes; y++){
                Color c = new Color(p[x][y][0] & 0xFF, p[x][y][1] & 0xFF,
                        p[x][y][2] & 0xFF, p[x][y][3] & 0xFF);
                image.setRGB(x, y, c.getRGB());
            }
        }
        g.drawImage(image, 0, 0, getWidth(), getHeight(), this);

//        LinkedList<Vertex> shape = largest.getShape(listening);
//        int[] x = new int[shape.size()];
//        int[] y = new int[shape.size()];
//        int i  = 0;
//        g2.setStroke(new BasicStroke(10));
//        for (Vertex v : shape){
//            x[i] = ((v.getX()*pageSize)/xRes);
//            y[i] = ((v.getY()*pageSize)/yRes);
//            g2.setColor(new Color(v.getR(), v.getG(), v.getB()));
//            g2.drawLine(x[i], y[i], x[i], y[i]);
//        }
//        if (shape.size() > 1){
//            g2.setStroke(new BasicStroke(5));
//            g2.setColor(Color.RED);
//            g2.drawPolygon(x, y, shape.size());
//        }
    }

    void setXPage(int xPage){
        this.xPage = xPage;
        //setPreferredSize(new Dimension(pageSize, pageSize));
        //revalidate();
    }

    void setYPage(int yPage){
        this.yPage = yPage;
        //setPreferredSize(new Dimension(pageSize, pageSize));
        //revalidate();
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

    void changeRes(int x, int y){
        xRes = x;
        yRes = y;
        xPage = x/2;
        yPage = y/2;
        image = new BufferedImage(xRes, yRes, BufferedImage.TYPE_INT_ARGB);
        eraseFinal();
        eraseTemp();
        eraseWorking();
    }

    byte[][][] getPoints(){
        return points;
    }

    int getWhichPoints(){
        return whichPoints;
    }

    void setWhichPoints(int thisOne){
        this.whichPoints = thisOne;
        revalidate();
        repaint();
    }

    byte[][][] getPointsTemp(){
        return pointsTemp;
    }

    byte[][][] getPointsStamp(){
        return pointsStamp;
    }

    void eraseFinal(){
//        for (int x = 0; x < xRes; x++){
//            for (int y = 0; y < yRes; y++){
//                points[x][y][0] = (byte)0;
//                points[x][y][1] = (byte)0;
//                points[x][y][2] = (byte)0;
//                points[x][y][3] = (byte)0;
//            }
//        }
//        revalidate();
//        repaint();
        points = new byte[xRes][yRes][4];
        revalidate();
        repaint();
    }

    void eraseTemp(){
//        for (int x = 0; x < xRes; x++){
//            for (int y = 0; y < yRes; y++){
//                pointsTemp[x][y][0] = (byte)0;
//                pointsTemp[x][y][1] = (byte)0;
//                pointsTemp[x][y][2] = (byte)0;
//                pointsTemp[x][y][3] = (byte)0;
//            }
//        }
//        revalidate();
//        repaint();
        pointsTemp = new byte[xRes][yRes][4];
        revalidate();
        repaint();
    }

    void eraseWorking(){
//        for (int x = 0; x < xRes; x++){
//            for (int y = 0; y < yRes; y++){
//                pointsStamp[x][y][0] = (byte)0;
//                pointsStamp[x][y][1] = (byte)0;
//                pointsStamp[x][y][2] = (byte)0;
//                pointsStamp[x][y][3] = (byte)0;
//            }
//        }
//        revalidate();
//        repaint();
        pointsStamp = new byte[xRes][yRes][4];
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
            if (SwingUtilities.isLeftMouseButton(e)){
                largest.addClick(x, y);
            }else if (whichPoints == 2){
                largest.getStamp().setxOffset(x);
                largest.getStamp().setyOffset(y);
                largest.stampWorking();
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
        public void mouseMoved(MouseEvent e){
            int x = ((xRes*e.getX())/xPage);
            int y = ((yRes*e.getY())/yPage);
            largest.setCordLabelText(x + ", " + y);
        }
    } //end of MyMouseListener class
}

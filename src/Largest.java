import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.util.LinkedList;

class Largest extends JFrame{
    static final Color BACKGROUND = new Color(100, 100, 100);
    private final CanvasPanel canvasPanel = new CanvasPanel();
//    private byte[][][] points = canvasPanel.getPoints();
//    private byte[][][] pointsTemp = canvasPanel.getPointsTemp();
//    private byte[][][] pointsStamp = canvasPanel.getPointsStamp();
//    private Stamp stamp = new Stamp();
    private JTabbedPane tabbed = new JTabbedPane();
    private JLabel cordLabel = new JLabel();
    private MainPanel main;
    private StampPanel spanel;

    Largest(){
        super("Chaos Paint");
        canvasPanel.setLargest(this);
        setFrame();
        prepareFrame();
    }

    private void setFrame(){
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1200, 600);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(true);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void prepareFrame(){
        JPanel scroll = addScroll();
        JTabbedPane tabbed = createTabbed();
        tabbed.setMinimumSize(new Dimension(0,0));
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scroll, tabbed);
        split.setBackground(Largest.BACKGROUND);
        split.setOneTouchExpandable(true);
        split.setResizeWeight(0.66);
        add(split);
        revalidate();
        split.setDividerLocation(7*split.getWidth()/9);
    }

    private JPanel addScroll(){
        JPanel left = new JPanel(new GridBagLayout());
        JScrollPane scroll = new JScrollPane(new DrawingPanel(canvasPanel));
        scroll.setWheelScrollingEnabled(false);
        scroll.setMinimumSize(new Dimension(0, 0));
        JPanel buttom = setBottom();
        buttom.setBackground(Largest.BACKGROUND);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        left.add(scroll, c);
        c.gridy = 1;
        c.weighty = 0.0;
        left.add(buttom, c);
        return left;
    }

    private JPanel setBottom(){
        JPanel bottom = new JPanel(new GridBagLayout());
        cordLabel = new JLabel("0, 0");
        cordLabel.setForeground(Color.white);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.9;
        c.fill = GridBagConstraints.BOTH;
        bottom.add(cordLabel, c);

//        JTextField xSize = new JTextField(String.valueOf(canvasPanel.getXRes()));
//        JTextField ySize = new JTextField(String.valueOf(canvasPanel.getYRes()));
//
//        JButton resize = new JButton("Resize Canvas");
//        resize.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                try {
//                    int x = Integer.parseInt(xSize.getText());
//                    int y = Integer.parseInt(ySize.getText());
//                    if (x > 127 && y > 127){
//                        canvasPanel.changeRes(x, y);
//                        points = canvasPanel.getPoints();
//                        pointsTemp = canvasPanel.getPointsTemp();
//                        pointsStamp = canvasPanel.getPointsStamp();
//                        if (tabbed.getSelectedIndex() != 0){
//                            ((ShapeTab)tabbed.getSelectedComponent()).redraw();
//                        }
//                    }
//                }catch (NumberFormatException e1){
//                    cordLabel.setText("Enter pixel sizes as integers, will delete old");
//                }
//                xSize.setText(String.valueOf(canvasPanel.getXRes()));
//                ySize.setText(String.valueOf(canvasPanel.getYRes()));
//            }
//        });
//
//        c.gridx = 1;
//        c.weightx = 0.1;
//        bottom.add(xSize, c);
//
//        c.gridx = 2;
//        bottom.add(ySize, c);
//
//        c.gridx = 3;
//        bottom.add(resize, c);

        return bottom;
    }

    BufferedImage getActiveImage(){
        return ((ShapeTab)tabbed.getSelectedComponent()).getActiveImage();
    }

    void setCordLabelText(String s){
        cordLabel.setText(s);
    }

    private JTabbedPane createTabbed(){
        tabbed.setMinimumSize(new Dimension(0,0));
        main = new MainPanel(this);
        spanel = new StampPanel(this);
        tabbed.addTab("Main", main);
        tabbed.addTab("Stamp", spanel);
        tabbed.addTab("Chaos", new ChaosPanel(this));
        tabbed.addTab("L-System", new LPanel(this));
        tabbed.addTab("Box", new BoxPanel(this));
        tabbed.addTab("Julia", new JuliaPanel(this));
        tabbed.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                ShapeTab tab = ((ShapeTab)tabbed.getSelectedComponent());
                tab.reset();
                changeRes(tab.getRes());
                Largest.this.reset();
            }
        });
        return tabbed;
    }

    void changeRes(int[] res){
        canvasPanel.changeRes(res);
    }

    void tellSPanelRes(int[] res){
        spanel.changeRes(res);
    }

    int[] getMainRes(){
        return main.getRes();
    }

    void reset(){
        revalidate();
        repaint();
    }

//    int getListening(){
//        return tabbed.getSelectedIndex();
//    }
//
//    Stamp getListeningStamp(){
//        return ((ShapeTab)tabbed.getSelectedComponent()).getStamp();
//    }
//
//    void showFinal(){
//        canvasPanel.setWhichPoints(0);
//    }
//
//    void showTemp(){
//        canvasPanel.setWhichPoints(1);
//    }
//
//    void showStamp(){
//        canvasPanel.setWhichPoints(2);
//    }
//
//    byte[][][] getPointsStamp() {
//        return pointsStamp;
//    }
//
//    void eraseStamp(){
//        stamp = new Stamp();
//        canvasPanel.setWhichPoints(2);
//        canvasPanel.eraseWorking();
//        pointsStamp = canvasPanel.getPointsStamp();
//    }
//
//    void eraseFinal(){
//        canvasPanel.eraseFinal();
//        points = canvasPanel.getPoints();
//    }
//
//    void eraseTemp(){
//        canvasPanel.eraseTemp();
//        pointsTemp = canvasPanel.getPointsTemp();
//    }
//
//    LinkedList<Vertex> getShape(int index){
//        ShapeTab tab = (ShapeTab) tabbed.getSelectedComponent();
//        return tab.getShape();
//    }

    void shiftDrag(int ox, int oy, int x, int y){
        ShapeTab tab = (ShapeTab) tabbed.getSelectedComponent();
        tab.shiftDrag(ox, oy, x, y);
        reset();
        //stampWorking();
    }

    void shiftClick(int x, int y){
        ShapeTab tab = (ShapeTab) tabbed.getSelectedComponent();
        tab.shiftClick(x, y);
        reset();
        //stampWorking();
    }

    void click(int x, int y){
        ShapeTab tab = (ShapeTab) tabbed.getSelectedComponent();
        tab.click(x, y);
        reset();
        //stampWorking();
    }
//
//    void stampWorking(){
//        canvasPanel.eraseWorking();
//        pointsStamp = canvasPanel.getPointsStamp();
//        stamp.stamp(pointsStamp);
//    }
//
//    void stampTemp(){
//        stamp.stamp(pointsTemp);
//        canvasPanel.revalidate();
//        canvasPanel.repaint();
//    }
//
//    void stampFinal(){
//        stamp.stamp(points);
//        canvasPanel.revalidate();
//        canvasPanel.repaint();
//    }
//
//    void tempToFinal(){
//        canvasPanel.eraseFinal();
//        points = canvasPanel.getPoints();
//        for (int x = 0; x < points.length; x++){
//            for (int y = 0; y < points[0].length; y++){
//                points[x][y][0] = pointsTemp[x][y][0];
//                points[x][y][1] = pointsTemp[x][y][1];
//                points[x][y][2] = pointsTemp[x][y][2];
//                points[x][y][3] = pointsTemp[x][y][3];
//            }
//        }
//        canvasPanel.revalidate();
//        canvasPanel.repaint();
//    }
//
//    void finalToTemp(){
//        canvasPanel.eraseTemp();
//        pointsTemp = canvasPanel.getPointsTemp();
//        for (int x = 0; x < points.length; x++){
//            for (int y = 0; y < points[0].length; y++){
//                pointsTemp[x][y][0] = points[x][y][0];
//                pointsTemp[x][y][1] = points[x][y][1];
//                pointsTemp[x][y][2] = points[x][y][2];
//                pointsTemp[x][y][3] = points[x][y][3];
//            }
//        }
//        canvasPanel.revalidate();
//        canvasPanel.repaint();
//    }

    void save(){
//        byte[][][] data = refineArray(points);
//        int width = data.length;
//        int height = data[0].length;
        BufferedImage image = main.getActiveImage();
//        for (int x = 0 ; x < width; x++){
//            for (int y = 0; y < height; y++){
//                if ((data[x][y][3] & 0xFF) != 0){
//                    Color c = new Color(data[x][y][0] & 0xFF, data[x][y][1] & 0xFF,
//                            data[x][y][2] & 0xFF, data[x][y][3] & 0xFF);
//                    image.setRGB(x, y, c.getRGB());
//                }
//            }
//        }
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("fileToSave.png"));
        int returnVal = fc.showSaveDialog(Largest.this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            try{
                ImageIO.write(image,"png", file);
            }
            catch(Exception ex){
                ex.printStackTrace();
            }
        }
    }

    public BufferedImage convertToARGB(BufferedImage image)
    {
        BufferedImage newImage = new BufferedImage(
                image.getWidth(), image.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = newImage.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return newImage;
    }

    void open(){
        JFileChooser fc = new JFileChooser();
        int returnVal = fc.showOpenDialog(Largest.this);
        BufferedImage img = null;
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            try {
                img = ImageIO.read(file);
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
        if (img == null){
            return;
        }
        stampify(img);
//        int[] cords = findCords(st);
//        Stamp stamp = new Stamp(cords[0], cords[1], st);
//        canvasPanel.setWhichPoints(2);
//        stampWorking();
//        resetMain();
    }

    private void stampify(int[][] st){
        main.stampify(st);
        spanel.stampify(st);
        reset();
    }

    void stampify(BufferedImage image){
        image = convertToARGB(image);
        int[][] data = Stamp.convertImage(image);
        stampify(data);
    }

//    private byte[][][] refineArray(byte[][][] original){
//        int width = original.length;
//        int height = original[0].length;
//        int minx = width;
//        int maxx = -1;
//        int miny = height;
//        int maxy = -1;
//        for (int x = 0; x < width; x++){
//            for(int y = 0; y < height; y++){
//                if ((original[x][y][3]&0xFF) != 0){
//                    minx = Math.min(minx, x);
//                    miny = Math.min(miny, y);
//                    maxx = Math.max(maxx, x);
//                    maxy = Math.max(maxy, y);
//                }
//            }
//        }
//
//        width = maxx - minx + 1;
//        height = maxy - miny + 1;
//        if (width < 1 || height < 1){
//            return new byte[1][1][4];
//        }
//        byte[][][] next = new byte[width][height][4];
//        for (int x = 0; x < width; x++){
//            for (int y = 0; y < height; y++){
//                next[x][y][0] = original[x + minx][y + miny][0];
//                next[x][y][1] = original[x + minx][y + miny][1];
//                next[x][y][2] = original[x + minx][y + miny][2];
//                next[x][y][3] = original[x + minx][y + miny][3];
//            }
//        }
//        return next;
//    }
//
//    public void workingToStamp(){
//        byte[][][] next = refineArray(pointsStamp);
//        int[] cords = findCords(next);
//        stamp = new Stamp(cords[0], cords[1], next);
//        canvasPanel.setWhichPoints(2);
//        stampWorking();
//        resetMain();
//    }
//
//    void tempToStamp(){
//        byte[][][] next = refineArray(pointsTemp);
//        int[] cords = findCords(next);
//        stamp = new Stamp(cords[0], cords[1], next);
//        canvasPanel.setWhichPoints(2);
//        stampWorking();
//        resetMain();
//    }
//
//    private int[] findCords(byte[][][] orig){
//        if (orig == null || orig[0] == null){
//            return new int[]{0, 0};
//        }
//        int x = Math.max(0, points.length/2);
//        int y = Math.max(0, points[0].length/2);
//        return new int[]{x, y};
//    }
//
//    public Stamp getStamp() {
//        return stamp;
//    }

//    int getWhichPoints(){
//        return canvasPanel.getWhichPoints();
//    }
//
//    public void setStamp(Stamp stamp) {
//        this.stamp = stamp;
//        canvasPanel.revalidate();
//        canvasPanel.repaint();
//    }
}

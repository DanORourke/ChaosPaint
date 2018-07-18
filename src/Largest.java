import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.image.*;
import java.io.File;

class Largest extends JFrame{
    static final Color BACKGROUND = new Color(100, 100, 100);
    private final CanvasPanel canvasPanel = new CanvasPanel();
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
        split.setResizeWeight(0.75);
        add(split);
        revalidate();
        //split.setDividerLocation(7*split.getWidth()/10);
        split.setDividerLocation(split.getWidth() - tabbed.getPreferredSize().width);
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

    void shiftDrag(int ox, int oy, int x, int y){
        ShapeTab tab = (ShapeTab) tabbed.getSelectedComponent();
        tab.shiftDrag(ox, oy, x, y);
        reset();
    }

    void shiftClick(int x, int y){
        ShapeTab tab = (ShapeTab) tabbed.getSelectedComponent();
        tab.shiftClick(x, y);
        reset();
    }

    void click(int x, int y){
        ShapeTab tab = (ShapeTab) tabbed.getSelectedComponent();
        tab.click(x, y);
        reset();
    }

    void save(){
        BufferedImage image = main.getActiveImage();
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

    private BufferedImage convertToARGB(BufferedImage image)
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
}


import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.FileChooserUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import static java.lang.Math.abs;

class Largest extends JFrame{
    private final CanvasPanel canvasPanel = new CanvasPanel();
    private final ArrayList<double[]> points = canvasPanel.getPoints();
    private final ArrayList<double[]> first = canvasPanel.getFirst();
    private final ArrayList<double[]> second = canvasPanel.getSecond();
    private int drawMethod = 0;
    private int reps = 10000;
    private double firstSize = 0.002;
    private double secondSize = 0.002;
    private Color firstColor = Color.black;
    private Color secondColor = Color.black;
    private double firstWeight = 1.0;
    private double secondWeight = 0.0;
    private double firstR = 0.5;
    private double secondR = 0.5;
    private ArrayList<Integer> firstLast = new ArrayList<>();
    private ArrayList<Integer> secondLast = new ArrayList<>();
    private ArrayList<Integer> firstAgain = new ArrayList<>();
    private ArrayList<Integer> secondAgain = new ArrayList<>();
    private boolean addingFirst = false;
    private boolean addingSecond = false;
    private JLabel adF = new JLabel();
    private JLabel adS = new JLabel();
    private JFileChooser fc = new JFileChooser();


    Largest(){
        super("Chaos Game");
        canvasPanel.setLargest(this);
        setFrame();
        prepareFrame();
    }

    private void setFrame(){
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1200, 600);
        setResizable(true);
        setLocationRelativeTo( null );
        setVisible(true);
    }

    private void prepareFrame(){
        JScrollPane scroll = new JScrollPane(new DrawingPanel(canvasPanel));
        scroll.setWheelScrollingEnabled(false);
        scroll.setMinimumSize(new Dimension(0, 0));
        JTabbedPane tabbed = createTabbed();
        tabbed.setMinimumSize(new Dimension(0,0));
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scroll, tabbed);
        split.setOneTouchExpandable(true);
        split.setResizeWeight(0.66);
        split.setDividerLocation(800);
        add(split);
        revalidate();
    }

    private JTabbedPane createTabbed(){
        JTabbedPane tabbed = new JTabbedPane();
        JPanel mainPanel = createMainPanel();
        tabbed.addTab("Main", mainPanel);
        JPanel firstPanel = createShapeTab(1);
        tabbed.addTab("First", firstPanel);
        JPanel secondPanel = createShapeTab(2);
        tabbed.addTab("Second", secondPanel);
        return tabbed;
    }

    private JPanel createMainPanel(){
        JPanel mainPanel = new JPanel(new GridBagLayout());

        JLabel errorLabel = new JLabel("");
        errorLabel.setForeground(Color.RED);

        JLabel weightF = new JLabel("Weight of First Shape: " + firstWeight);
        JLabel weightS = new JLabel("Weight of Second Shape: " + secondWeight);

        JTextField firstField = new JTextField();
        firstField.setPreferredSize(new Dimension(75, 25));

        JButton rF = new JButton("Reset First Weight");
        rF.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isProperDouble(firstField.getText())){
                    firstWeight = Double.valueOf(firstField.getText());
                    secondWeight = 1.0 - firstWeight;
                    updateWeightLabels(weightF, weightS, rF);
                }else{
                    error(errorLabel);
                }
                firstField.setText("");
            }
        });

        JLabel comType = new JLabel(createComTypeString());

        JButton cComType = new JButton("Cycle Draw Method");
        cComType.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawMethod++;
                if (drawMethod > 2){
                    drawMethod = 0;
                }
                comType.setText(createComTypeString());
                updateWeightLabels(weightF, weightS, rF);
            }
        });

        JLabel repLabel = new JLabel("Draw repetitions: " + reps);

        JTextField repField = new JTextField();
        repField.setPreferredSize(new Dimension(75, 25));

        JButton repB = new JButton("Change repetitions");
        repB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isInteger(repField.getText())){
                    reps = Integer.parseInt(repField.getText());
                }else{
                    error(errorLabel);
                }
                repField.setText("");
                repLabel.setText("Draw repetitions: " + reps);
            }
        });

        JButton d = new JButton("Draw");
        d.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addingFirst = false;
                addingSecond = false;
                adF.setText("");
                adS.setText("");
                if (drawMethod == 0){
                    if (!drawMix()){
                        error(errorLabel);
                    }
                }else if (drawMethod == 1){
                    if (!drawAvoid()){
                        error(errorLabel);
                    }
                }else if (drawMethod == 2){
                    if (!drawLinear()){
                        error(errorLabel);
                    }
                }else {
                    error(errorLabel);
                }
            }
        });

        JButton e = new JButton("Erase All");
        e.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                canvasPanel.erase();
            }
        });

        JButton s = new JButton("Save as PNG");
        s.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fc.setSelectedFile(new File("fileToSave.png"));
                int returnVal = fc.showSaveDialog(Largest.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    BufferedImage image = new BufferedImage(canvasPanel.getWidth(), canvasPanel.getHeight(),
                            BufferedImage.TYPE_INT_ARGB);
                    Graphics g = image.createGraphics();
                    canvasPanel.paintComponent(g);
                    try{
                        ImageIO.write(image,"png", file);
                    }
                    catch(Exception ex){
                        error(errorLabel);
                        ex.printStackTrace();
                    }
                }
            }
        });

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 2;
        c.weighty = 0.1;
        c.weightx = 1.0;
        mainPanel.add(errorLabel, c);

        c.gridy = 0;
        mainPanel.add(weightF, c);

        c.gridy = 1;
        mainPanel.add(weightS, c);

        c.gridy = 4;
        mainPanel.add(comType, c);

        c.gridy = 5;
        mainPanel.add(cComType, c);

        c.gridy = 6;
        mainPanel.add(repLabel, c);

        c.gridy = 3;
        c.gridwidth = 1;
        c.weighty = 0.1;
        c.weightx = 0.5;
        mainPanel.add(firstField, c);

        c.gridx = 1;
        mainPanel.add(rF, c);

        c.gridx = 0;
        c.gridy = 7;
        mainPanel.add(repField, c);

        c.gridx = 1;
        mainPanel.add(repB, c);

        c.gridx = 0;
        c.gridy = 8;
        mainPanel.add(d, c);

        c.gridx = 1;
        mainPanel.add(e, c);

        c.gridx = 0;
        c.gridy = 9;
        mainPanel.add(d, c);

        c.gridx = 1;
        mainPanel.add(e, c);

        c.gridx = 0;
        c.gridy = 10;
        c.gridwidth = 2;
        mainPanel.add(s, c);

        return mainPanel;
    }

    private String createComTypeString(){
        String s = "Draw Method: ";
        if (drawMethod == 0){
            return s + "Mix";
        }else if (drawMethod == 1){
            return s + "Avoid";
        }else if (drawMethod == 2){
            return s + "Linear";
        }else{
            return s + drawMethod;
        }
    }

    private void updateWeightLabels(JLabel fL, JLabel sL, JButton rF){
        if (drawMethod == 0){
            fL.setText("Weight of First Shape: " + firstWeight);
            sL.setText("Weight of Second Shape: " + secondWeight);
            rF.setText("Reset First Weight");
        }else{
            fL.setText("Weight of First Shape: NA");
            sL.setText("Weight of Second Shape: NA");
            rF.setText("NA");
        }
    }

    private void error(JLabel errorLabel){
        errorLabel.setText("Error");
        Timer timer = new Timer(5000, new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                javax.swing.SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        errorLabel.setText("");
                        revalidate();
                        repaint();
                    }
                });
            }
        });
        timer.setRepeats(false);
        timer.start();
        revalidate();
        repaint();
    }

    private boolean isProperDouble(String rField){
        double hello;
        try {
            hello = Double.parseDouble(rField);
            if (hello <= 1.0 && hello >= 0.0){
                return true;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return false;
    }

    private JPanel createShapeTab(int order){
        JPanel shapePanel = new JPanel(new GridBagLayout());

        JPanel cPanel = new JPanel();
        if (order == 1){
            cPanel.setBackground(firstColor);
        }else{
            cPanel.setBackground(secondColor);
        }

        cPanel.setPreferredSize(new Dimension(50, 50));

        JButton changeC = new JButton("Change Color");
        changeC.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color old;
                if (order == 1){
                    old = firstColor;
                }else{
                    old = secondColor;
                }
                Color newColor = JColorChooser.showDialog(
                        Largest.this,
                        "Choose Background Color",
                        old);
                if (newColor != null){
                    if (order == 1){
                        firstColor = newColor;
                    }else{
                        secondColor = newColor;
                    }
                    cPanel.setBackground(newColor);
                }
            }
        });

        double draw;
        if (order == 1){
            draw = firstR;
        }else {
            draw = secondR;
        }

        JLabel rlabel = new JLabel("Draw of next vertex: " + draw);

        JTextField rField = new JTextField();
        rField.setPreferredSize(new Dimension(75, 25));

        JButton changeDraw = new JButton("Change draw of next vertex");
        changeDraw.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isProperDouble(rField.getText())){
                    double draw = Double.parseDouble(rField.getText());
                    if (order == 1){
                        firstR = draw;
                    }else{
                        secondR = draw;
                    }
                    rField.setText("");
                    rlabel.setText("Draw of next vertex: " + draw);
                }
            }
        });

        double size;
        if (order == 1){
            size = firstSize;
        }else {
            size = secondSize;
        }

        JLabel slabel = new JLabel("Size of next vertex: " + size);

        JTextField sField = new JTextField();
        sField.setPreferredSize(new Dimension(75, 25));

        JButton changeSize = new JButton("Change size of next vertex");
        changeSize.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isProperDouble(sField.getText())){
                    double size = Double.parseDouble(sField.getText());
                    if (order == 1){
                        firstSize = size;
                    }else{
                        secondSize = size;
                    }
                    sField.setText("");
                    slabel.setText("Size of next vertex: " + size);
                }
            }
        });
        JLabel lastLabel = new JLabel("-1 vertex rules: " + createRules(order, true));

        JTextField lastField = new JTextField();
        lastField.setPreferredSize(new Dimension(75, 25));

        JButton lastB = new JButton("Add -1 vertex rule");
        lastB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isInteger(lastField.getText())){
                    int last = Integer.parseInt(lastField.getText());
                    if (order == 1){
                        firstLast.add(last);
                    }else{
                        secondLast.add(last);
                    }
                }
                lastField.setText("");
                lastLabel.setText("-1 vertex rules: " + createRules(order, true));
            }
        });

        JLabel againLabel = new JLabel("-2 vertex rules: " + createRules(order, false));

        JTextField againField = new JTextField();
        againField.setPreferredSize(new Dimension(75, 25));

        JButton againB = new JButton("Add -2 vertex rule");
        againB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isInteger(againField.getText())){
                    int last = Integer.parseInt(againField.getText());
                    if (order == 1){
                        firstAgain.add(last);
                    }else{
                        secondAgain.add(last);
                    }
                }
                againField.setText("");
                againLabel.setText("-2 vertex rules: " + createRules(order, false));
            }
        });

        JButton adding = new JButton("Add vertex");
        adding.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (order == 1){
                    addingFirst = true;
                    addingSecond = false;
                    adF.setText("Adding Vertex");
                    adS.setText("");
                }else{
                    addingFirst = false;
                    addingSecond = true;
                    adF.setText("");
                    adS.setText("Adding Vertex");                }
            }
        });

        JButton clear = new JButton("Clear Shape");
        clear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (order == 1){
                    first.clear();
                    firstLast.clear();
                    firstAgain.clear();
                }else{
                    second.clear();
                    secondLast.clear();
                    secondAgain.clear();
                }
                lastLabel.setText("-1 vertex rules: ");
                againLabel.setText("-2 vertex rules: ");
                revalidate();
                repaint();
            }
        });

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.weightx = 1.0;
        c.weighty = 0.0;
        if (order == 1){
            shapePanel.add(adF, c);
        }else{
            shapePanel.add(adS, c);
        }

        c.gridy = 2;
        shapePanel.add(rlabel, c);

        c.gridy = 4;
        shapePanel.add(slabel, c);

        c.gridy = 6;
        shapePanel.add(lastLabel, c);

        c.gridy = 8;
        shapePanel.add(againLabel, c);

        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.weightx = 0.5;
        c.weighty = 0.25;
        shapePanel.add(cPanel, c);

        c.gridx = 1;
        shapePanel.add(changeC, c);

        c.gridx = 0;
        c.gridy = 3;
        shapePanel.add(rField, c);

        c.gridx = 1;
        shapePanel.add(changeDraw, c);

        c.gridx = 0;
        c.gridy = 5;
        shapePanel.add(sField, c);

        c.gridx = 1;
        shapePanel.add(changeSize, c);

        c.gridx = 0;
        c.gridy = 7;
        shapePanel.add(lastField, c);

        c.gridx = 1;
        shapePanel.add(lastB, c);

        c.gridx = 0;
        c.gridy = 9;
        shapePanel.add(againField, c);

        c.gridx = 1;
        shapePanel.add(againB, c);

        c.gridx = 0;
        c.gridy = 10;
        shapePanel.add(adding, c);

        c.gridx = 1;
        shapePanel.add(clear, c);

        return shapePanel;
    }

    private String createRules(int order, boolean last){
        String s = "";
        ArrayList<Integer> rules;
        if (order == 1){
            if (last){
                rules = firstLast;
            }else{
                rules = firstAgain;
            }
        }else{
            if (last){
                rules = secondLast;
            }else{
                rules = secondAgain;
            }        }

        int i = 0;
        while (i < rules.size()){
            s += rules.get(i);
            if (i + 1 != rules.size()){
                s += ", ";
            }
            i++;
        }
        return s;
    }

    private boolean isInteger(String s) {
        if(s.isEmpty()) return false;
        for(int i = 0; i < s.length(); i++) {
            if(i == 0 && s.charAt(i) == '-') {
                if(s.length() == 1) return false;
                else continue;
            }
            if(Character.digit(s.charAt(i),10) < 0) return false;
        }
        return true;
    }

    private boolean drawMix(){
        // point = double[x, y, size, rcolor, gcolor, bcolor, alpha, r]
        Random rand = new Random();
        double[] a;
        if (!first.isEmpty()){
            a = first.get(0);
        }else if (!second.isEmpty()){
            a = second.get(0);
        }else{
            return false;
        }
        if (first.isEmpty() && secondWeight != 1.0){
            return false;
        }else if (second.isEmpty() && firstWeight != 1.0){
            return false;
        }

        double[] active = {a[0],a[1],a[2],a[3],a[4],a[5],a[6]};
        int fLast = -1;
        int fAgain = -1;
        int sLast = -1;
        int sAgain = -1;
        for (int i = 0; i < reps + 10; i++){
            double nextShape = rand.nextDouble();
            boolean useFirst = true;
            if (nextShape > firstWeight){
                useFirst = false;
            }
            double[] target;
            if (useFirst){
                int size = first.size();
                int get = rand.nextInt(size);
                boolean looking = true;
                while(looking){
                    boolean passLast = true;
                    boolean passAgain = true;
                    for (Integer rule : firstLast){
                        if (rule < 0){
                            rule = (rule%size) + size;
                        }
                        if (fLast != -1 && (fLast + rule)%size == get){
                            passLast = false;
                        }
                    }
                    for (Integer rule : firstAgain){
                        if (rule < 0){
                            rule = (rule%size) + size;
                        }
                        if (fAgain != -1 && (fAgain + rule)%size == get){
                            passAgain = false;
                        }
                    }
                    if (passLast && passAgain){
                        looking = false;
                    }else{
                        get = rand.nextInt(size);
                    }
                }
                //System.out.println( "get " + get + "   last " + fLast + "  again " + fAgain);
                target = first.get(get);
                fAgain = fLast;
                fLast = get;
            }else {
                int size = second.size();
                int get = rand.nextInt(size);
                boolean looking = true;
                while(looking){
                    boolean passLast = true;
                    boolean passAgain = true;
                    for (Integer rule : secondLast){
                        if (rule < 0){
                            rule = (rule%size) + size;
                        }
                        if (sLast != -1 && (sLast + rule)%size == get){
                            passLast = false;
                        }
                    }
                    for (Integer rule : secondAgain){
                        if (rule < 0){
                            rule = (rule%size) + size;
                        }
                        if (sAgain != -1 && (sAgain + rule)%size == get){
                            passAgain = false;
                        }
                    }
                    if (passLast && passAgain){
                        looking = false;
                    }else{
                        get = rand.nextInt(size);
                    }
                }
                sAgain = sLast;
                sLast = get;
                target = second.get(get);
            }
            double dx = (target[0] - active[0])*(1 - target[7]);
            double dy = (target[1] - active[1])*(1 - target[7]);
            active[0] = active[0] + dx;
            active[1] = active[1] + dy;
            active[2] = (active[2] + target[2])/2;
            active[3] = (active[3] + target[3])/2;
            active[4] = (active[4] + target[4])/2;
            active[5] = (active[5] + target[5])/2;
            active[6] = (active[6] + target[6])/2;
            double[] print = {active[0], active[1], active[2], active[3], active[4], active[5], active[6]};
            if (i > 9){
                points.add(print);
            }
        }
        revalidate();
        repaint();
        return true;
    }

    private boolean drawAvoid(){
        System.out.println("drawAvoid");
        if (first.size() < 3 || second.size() < 3){
            return false;
        }

        Random rand = new Random();
        double[] a = first.get(0);

        double[] active = {a[0],a[1],a[2],a[3],a[4],a[5],a[6]};
        int fLast = -1;
        int fAgain = -1;

        int[] polyX = new int[second.size()];
        int[] polyY = new int[second.size()];
        for (int p  = 0; p < second.size(); p++){
            double[] point = second.get(p);
            polyX[p] = (int)(point[0] * 10000);
            polyY[p] = (int)(point[1] * 10000);
        }

        Polygon poly = new Polygon(polyX, polyY, second.size());

        for (int i = 0; i < reps + 10; i++){
            double[] target;
            int size = first.size();
            int get = rand.nextInt(size);
            boolean looking = true;
            int attempts = 0;
            int resets = 0;
            while(looking){
                boolean passLast = true;
                boolean passAgain = true;
                boolean passPoly = true;
                for (Integer rule : firstLast){
                    if (rule < 0){
                        rule = (rule%size) + size;
                    }
                    if (fLast != -1 && (fLast + rule)%size == get){
                        passLast = false;
                    }
                }
                for (Integer rule : firstAgain){
                    if (rule < 0){
                        rule = (rule%size) + size;
                    }
                    if (fAgain != -1 && (fAgain + rule)%size == get){
                        passAgain = false;
                    }
                }
                target = first.get(get);
                int newX = (int)((active[0] + ((target[0] - active[0])*(1 - target[7]))) * 10000);
                int newY = (int)((active[1] + ((target[1] - active[1])*(1 - target[7]))) * 10000);
                if (poly.contains(new Point(newX, newY))){
                    passPoly = false;
                }
                if (passLast && passAgain && passPoly){
                    looking = false;
                }else if (resets > 100){
                    return false;
                }else if (attempts > size * 10){
                    double[] b = first.get(rand.nextInt(size));
                    active[0] = b[0];
                    active[1] = b[1];
                    active[2] = b[2];
                    active[3] = b[3];
                    active[4] = b[4];
                    active[5] = b[5];
                    active[6] = b[6];
                    resets ++;
                }else{
                    attempts++;
                    get = rand.nextInt(size);
                }
            }
            fAgain = fLast;
            fLast = get;
            target = first.get(get);

            double dx = (target[0] - active[0])*(1 - target[7]);
            double dy = (target[1] - active[1])*(1 - target[7]);
            active[0] = active[0] + dx;
            active[1] = active[1] + dy;
            active[2] = (active[2] + target[2])/2;
            active[3] = (active[3] + target[3])/2;
            active[4] = (active[4] + target[4])/2;
            active[5] = (active[5] + target[5])/2;
            active[6] = (active[6] + target[6])/2;
            double[] print = {active[0], active[1], active[2], active[3], active[4], active[5], active[6]};
            if (i > 9){
                points.add(print);
            }
        }
        revalidate();
        repaint();
        return true;
    }

    private boolean drawLinear(){
        System.out.println("drawLinear");
        // point = double[x, y, size, rcolor, gcolor, bcolor, alpha, r]
        if (first.size()%4 != 1 || first.size() < 5){
            return false;
        }

        double[] ac = first.get(0);
        double[] active = {0.0, 0.0, ac[2],ac[3],ac[4],ac[5],ac[6]};

        System.out.println(ac[0] + " " + ac[1]);

        ArrayList<Double> probs = new ArrayList<>();
        ArrayList<double[]> trans = new ArrayList<>();

        double pTotal = 0.0;

        for (int i = 1; i < first.size(); i += 4){
            double[] one = first.get(i);
            double[] two = first.get(i+1);
            double[] three = first.get(i+2);
            double[] four = first.get(i+3);

            double p = (one[0] + one[1])/2;
            pTotal += p;
            probs.add(p);

            double a = ac[0] - two[0];
            double b = ac[1] - two[1];

            double c = ac[0] - three[0];
            double d = ac[1] - three[1];

            double e = ac[0] - four[0];
            double f = ac[1] - four[1];

            double s = one[2];
            double r = one[3];
            double g = one[4];
            double bl = one[5];
            double al = one[6];

            double[] t = {a*4, b*4, c*4, d*4, e*8, f*8, s, r, g, bl, al};
            trans.add(t);
            System.out.println(t[0] + " " + t[1] + " " + t[2] + " " + t[3] + " " + t[4] + " " + t[5] + " " + t[6] +
                    " " + t[7] + " " + t[8] + " " + t[9] + " " + t[10]);
        }

        double ppTotal = 0.0;
        for (int i = 0; i < probs.size(); i++){
            double fp = probs.get(i)/pTotal;
            fp += ppTotal;
            ppTotal = fp;
            probs.remove(i);
            probs.add(i, fp);
        }

        System.out.println(probs);

        Random rand = new Random();
        int total = 0;
        for (int i = 0; i < reps; i++){
            double r = rand.nextDouble();
            int pick = 0;
            boolean picking = true;
            while(picking){
                double poss = probs.get(pick);
                if (r < poss){
                    picking = false;
                }else{
                    pick++;
                }
            }
            double[] transform = trans.get(pick);

            active[0] = ((active[0]*transform[0]) + (active[1]*transform[1]) + transform[4]);
            active[1] = ((active[0]*transform[2]) + (active[1]*transform[3]) + transform[5]);
            active[2] = (active[2] + transform[6])/2;
            active[3] = (active[3] + transform[7])/2;
            active[4] = (active[4] + transform[8])/2;
            active[5] = (active[5] + transform[9])/2;
            active[6] = (active[6] + transform[10])/2;

            double addX = active[0] + ac[0];
            double fX;
            if (addX > 0.0){
                fX = addX%1.0;
            }else{
                fX = 1 + (addX%1.0);
            }

            double addY = active[1] + ac[1];
            double fY;
            if (addY > 0.0){
                fY = addY%1.0;
            }else{
                fY = 1 + (addY%1.0);
            }


            double[] print = {fX, fY, active[2], active[3], active[4], active[5], active[6]};

            System.out.println(active[0] + " " + active[1] + " " + print[0] + " " + print[1]);


            if (active[0] > 1.0E12){
                active[0] = 0.0;
                active[1] = 0.0;
            }
            if (active[1] > 1.0E12) {
                active[0] = 0.0;
                active[1] = 0.0;
            }

            if (print[0] > 0.0 && print[0] < 1.0 && print[1] > 0.0 && print[1] < 1.0){
                points.add(print);
                total ++;
            }
        }
        System.out.println(total);
        revalidate();
        repaint();
        return true;
    }

    void addClick(double[] point){
        if (addingFirst){
            double rColor = ((double)firstColor.getRed())/256;
            double gColor = ((double)firstColor.getGreen())/256;
            double bColor = ((double)firstColor.getBlue())/256;
            double alpha = ((double)firstColor.getAlpha())/256;
//            System.out.println(point[0] + " " + point[1] + " " + firstSize + " " +
//                    rColor + " " + gColor + " " + bColor + " " + alpha + " " + firstR);
            first.add(new double[]{point[0], point[1], firstSize, rColor, gColor, bColor, alpha, firstR});
        }else if (addingSecond){
            double rColor = ((double)secondColor.getRed())/256;
            double gColor = ((double)secondColor.getGreen())/256;
            double bColor = ((double)secondColor.getBlue())/256;
            double alpha = ((double)secondColor.getAlpha())/256;
            second.add(new double[]{point[0], point[1], secondSize, rColor, gColor, bColor, alpha, secondR});
        }
        revalidate();
        repaint();
    }
}

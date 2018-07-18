import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.*;

public class LPanel extends JPanel implements ShapeTab{
    private final Largest largest;
    private GridBagConstraints c = new GridBagConstraints();
    private JLabel warning = new JLabel();
    private int xRes;
    private int yRes;
    private BufferedImage image;
    private String axiom = "FX";
    private String f = "F";
    private String g = "";
    private String h = "";
    private String x = "X+YF+";
    private String y = "-FX-Y";
    private int plus = 90;
    private int minus = -90;
    private Color one = Color.BLACK;
    private Color two = Color.GREEN;
    private Color three = Color.BLUE;
    private int depth = 14;
    private int stroke = 2;
    private HashMap<Character, String> map;
    private Stack<Integer> xStack = new Stack<>();
    private Stack<Integer> yStack = new Stack<>();
    private Stack<Integer> dStack = new Stack<>();
    private boolean cycling = false;
    private ArrayList<Action> actionList = new ArrayList<>();

    LPanel(Largest largest){
        super();
        this.largest = largest;
        setLayout(new GridBagLayout());
        setBackground(Largest.BACKGROUND);
        initImage();
        updateFields();
        redraw();
    }

    private void updateFields(){
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(1, 1, 1, 1);
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 0.4;
        c.weighty = 0.2;
        removeAll();
        actionList.clear();
        addWarning();
        addReps();
        addAxiom();
        addF();
        addG();
        addH();
        addX();
        addY();
        addPlus();
        addMinus();
        addOne();
        addTwo();
        addThree();
        addStroke();
        addStampify();
        addRandom();
        addResolution();
        addDraw();
        addSpacers();
    }

    private JLabel getLabel(String s, int y){
        JLabel label = new JLabel(s);
        label.setBackground(Largest.BACKGROUND);
        label.setForeground(Color.WHITE);
        label.setHorizontalAlignment(JLabel.RIGHT);

        c.gridx = 0;
        c.gridy = y;
        add(label, c);
        return label;
    }

    private JTextField getField(String s, int y){
        JTextField field = new JTextField(s);
        c.gridx = 2;
        c.gridy = y;
        add(field, c);
        return field;
    }

    private void addAxiom(){
        getLabel("Axiom:", 2);
        JTextField rF = getField(axiom, 2);
        Action a = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s = rF.getText().toUpperCase();
                if(validInstruct(s)){
                    axiom = s;
                    if(!cycling){
                        redraw();
                    }
                }else{
                    setWarningText("F, G, H, X, Y, +, -, 1, 2, 3, [, ]");
                }

                rF.setText(axiom);
            }
        };

        actionList.add(a);
        rF.addActionListener(a);
    }

    private void addY(){
        getLabel("Y:", 7);
        JTextField rF = getField(y, 7);
        Action a = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s = rF.getText().toUpperCase();
                if(validInstruct(s)){
                    y = s;
                    if(!cycling){
                        redraw();
                    }
                }else{
                    setWarningText("F, G, H, X, Y, +, -, 1, 2, 3, [, ]");
                }

                rF.setText(y);
            }
        };

        actionList.add(a);
        rF.addActionListener(a);
    }

    private void addX(){
        getLabel("X:", 6);
        JTextField rF = getField(x, 6);
        Action a = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s = rF.getText().toUpperCase();
                if(validInstruct(s)){
                    x = s;
                    if(!cycling){
                        redraw();
                    }
                }else{
                    setWarningText("F, G, H, X, Y, +, -, 1, 2, 3, [, or ]");
                }

                rF.setText(x);
            }
        };

        actionList.add(a);
        rF.addActionListener(a);
    }

    private void addH(){
        getLabel("H:", 5);
        JTextField rF = getField(h, 5);
        Action a = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s = rF.getText().toUpperCase();
                if(validInstruct(s)){
                    h = s;
                    if(!cycling){
                        redraw();
                    }
                }else{
                    setWarningText("F, G, H, X, Y, +, -, 1, 2, 3, [, ]");
                }

                rF.setText(h);
            }
        };

        actionList.add(a);
        rF.addActionListener(a);
    }

    private void addG(){
        getLabel("G:", 4);
        JTextField rF = getField(g, 4);
        Action a = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s = rF.getText().toUpperCase();
                if(validInstruct(s)){
                    g = s;
                    if(!cycling){
                        redraw();
                    }
                }else{
                    setWarningText("F, G, H, X, Y, +, -, 1, 2, 3, [, ]");
                }

                rF.setText(g);
            }
        };

        actionList.add(a);
        rF.addActionListener(a);
    }

    private void addF(){
        getLabel("F:", 3);
        JTextField rF = getField(f, 3);
        Action a = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s = rF.getText().toUpperCase();
                if(validInstruct(s)){
                    f = s;
                    if(!cycling){
                        redraw();
                    }
                }else{
                    setWarningText("F, G, H, X, Y, +, -, 1, 2, 3, [, ]");
                }

                rF.setText(f);
            }
        };

        actionList.add(a);
        rF.addActionListener(a);
    }

    private boolean validInstruct(String s){
        String[] list = new String[]{"F", "G", "H", "X", "Y", "+", "-", "[", "]", "1", "2", "3"};
        HashSet<String> set = new HashSet<>(Arrays.asList(list));
        int total = 0;
        for(int i  = 0; i < s.length(); i++){
            String t = s.substring(i, i+1);
            if(!set.contains(t)){
                return false;
            }
            if(t.equals("[")){
                total++;
            }else if(t.equals("]")){
                total--;
            }

        }
        return total == 0;
    }

    private void addThree(){
        JPanel cPanel = new JPanel();
        cPanel.setBackground(three);

        JButton cButton = new JButton("Color 3");
        cButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color newColor = JColorChooser.showDialog(
                        LPanel.this, "", null);
                if (newColor != null){
                    three = newColor;
                    cPanel.setBackground(newColor);
                    redraw();
                }
            }
        });

        c.gridx = 0;
        c.gridy = 12;
        this.add(cButton, c);
        c.gridx = 2;
        this.add(cPanel, c);
    }

    private void addTwo(){
        JPanel cPanel = new JPanel();
        cPanel.setBackground(two);

        JButton cButton = new JButton("Color 2");
        cButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color newColor = JColorChooser.showDialog(
                        LPanel.this, "", null);
                if (newColor != null){
                    two = newColor;
                    cPanel.setBackground(newColor);
                    redraw();
                }
            }
        });

        c.gridx = 0;
        c.gridy = 11;
        this.add(cButton, c);
        c.gridx = 2;
        this.add(cPanel, c);
    }

    private void addOne(){
        JPanel cPanel = new JPanel();
        cPanel.setBackground(one);

        JButton cButton = new JButton("Color 1");
        cButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color newColor = JColorChooser.showDialog(
                        LPanel.this, "", null);
                if (newColor != null){
                    one = newColor;
                    cPanel.setBackground(newColor);
                    redraw();
                }
            }
        });

        c.gridx = 0;
        c.gridy = 10;
        this.add(cButton, c);
        c.gridx = 2;
        this.add(cPanel, c);
    }

    private void addDraw(){

        JButton draw = new JButton("Draw");
        Action a = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cycling = true;
            }
        };
        Action b = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cycling = false;
                redraw();
            }
        };


        draw.addActionListener(b);
        for(Action t : actionList){
            draw.addActionListener(t);
        }
        draw.addActionListener(a);

        c.gridy = 15;
        c.gridx = 2;
        add(draw, c);

    }

    private void addStroke(){
        getLabel("Stroke:", 13);
        JTextField rF = getField(String.valueOf(stroke), 13);
        Action a = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int its = Integer.parseInt(rF.getText());
                    if (its < 1){
                        setWarningText("Enter an int greater than 0");
                    }else{
                        stroke = its;
                        if(!cycling){
                            redraw();
                        }
                    }
                }catch (NumberFormatException e1){
                    setWarningText("Enter an int greater than 0");
                }
                rF.setText(String.valueOf(stroke));
            }
        };

        actionList.add(a);
        rF.addActionListener(a);
    }

    private void addMinus(){
        getLabel("-:", 9);
        JTextField rF = getField(String.valueOf(minus), 9);
        Action a = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int its = Integer.parseInt(rF.getText());
                    minus = its%360;
                    if(!cycling){
                        redraw();
                    }
                }catch (NumberFormatException e1){
                    setWarningText("Enter an int");
                }
                rF.setText(String.valueOf(minus));
            }
        };

        actionList.add(a);
        rF.addActionListener(a);
    }

    private void addPlus(){
        getLabel("+:", 8);
        JTextField rF = getField(String.valueOf(plus), 8);
        Action a = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int its = Integer.parseInt(rF.getText());
                    plus = its%360;
                    if(!cycling){
                        redraw();
                    }
                }catch (NumberFormatException e1){
                    setWarningText("Enter an int");
                }
                rF.setText(String.valueOf(plus));
            }
        };

        actionList.add(a);
        rF.addActionListener(a);
    }

    private void addReps(){
        getLabel("Repetitions:", 1);
        JTextField rF = getField(String.valueOf(depth), 1);
        Action a = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int its = Integer.parseInt(rF.getText());
                    if (its < 1){
                        setWarningText("Enter an int greater than 0");
                    }else{
                        depth = its;
                        if(!cycling){
                            redraw();
                        }
                    }
                }catch (NumberFormatException e1){
                    setWarningText("Enter an int greater than 0");
                }
                rF.setText(String.valueOf(depth));
            }
        };

        actionList.add(a);
        rF.addActionListener(a);
    }

    private void addResolution(){
        getLabel("Resolution:", 14);
        JTextField rF = getField(String.valueOf(xRes), 14);
        Action a = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int its = Integer.parseInt(rF.getText());
                    if (its < 1) {
                        setWarningText("Enter an int greater than 0");
                    } else {
                        xRes = its;
                        yRes = its;
                        image = new BufferedImage(xRes, yRes, BufferedImage.TYPE_INT_ARGB);
                        largest.changeRes(getRes());
                        if(!cycling){
                            redraw();
                        }
                    }
                } catch (NumberFormatException e1) {
                    setWarningText("Enter an int greater than 0");
                }
                rF.setText(String.valueOf(xRes));
            }
        };

        actionList.add(a);
        rF.addActionListener(a);
    }

    private void addSpacers() {
        JLabel x1 = new JLabel();
        x1.setBackground(Largest.BACKGROUND);
        c.gridx = 1;
        c.gridy = 1;
        c.gridheight = 16;
        c.weightx = 0.2;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        this.add(x1, c);
    }

    private void addWarning(){
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 3;
        c.weighty = 1.0;
        warning.setBackground(Largest.BACKGROUND);
        warning.setForeground(Largest.BACKGROUND);
        warning.setText("filler");
        this.add(warning, c);
        c.gridwidth = 1;
        c.weighty = 0.2;
    }

    private void setWarningText(String text){
        warning.setForeground(Color.white);
        warning.setText(text);
        resetWarning();
    }

    private void resetWarning(){
        Timer timer = new Timer(3000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                warning.setForeground(Largest.BACKGROUND);
                warning.setText("filler");
            }
        });
        timer.start();
    }

    private void addStampify(){
        JButton stampify = new JButton("Stampify");
        stampify.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                largest.stampify(image);
            }
        });
        c.gridx = 0;
        c.gridy = 16;
        add(stampify, c);
    }

    private void addRandom(){
        JButton r = new JButton("Random");
        r.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                randomize();
            }
        });
        c.gridx = 2;
        c.gridy = 16;
        add(r, c);
    }

    private void randomize(){
        Random r = new Random();
        plus = r.nextInt(360);
        if(r.nextDouble() > 0.5){
            minus = plus * -1;
        }else{
            minus = r.nextInt(360);
        }

        String[] list = new String[]{"F", "G", "H", "X", "Y", "+", "-", "[", "]", "1", "2", "3"};

        int total = 0;//use to change depth??
        int al = (r.nextInt(5)) + 1;
        total+=al;

        StringBuilder ab = new StringBuilder();
        for(int i = 0; i < al; i++){
            ab.append(list[r.nextInt(5)]);
        }

        axiom = ab.toString();

        for(int i = 0; i < 5; i++){
            int il = r.nextInt(10);
            StringBuilder ib = new StringBuilder();
            int openCount = 0;
            for(int j = 0; j < il; j++){
                int next = r.nextInt(12);
                if(next != i && !(next == 8 && openCount < 1)){
                    ib.append(list[next]);
                    total++;
                    if(next == 7){
                        openCount++;
                    }
                    if(next == 8){
                        openCount--;
                    }
                }
            }
            while(openCount > 0){
                ib.append(list[8]);
                openCount--;
            }

            String s = ib.toString();
            if(s.length() == 0){
                s = list[i];
            }

            if(i == 0){
                f = s;
            }
            if(i == 1){
                g = s;
            }
            if(i == 2){
                h = s;
            }
            if(i == 3){
                x = s;
            }
            if(i == 4){
                y = s;
            }
        }

        depth = 200/total;

//        System.out.println(axiom);
//        System.out.println(f);
//        System.out.println(g);
//        System.out.println(h);
//        System.out.println(x);
//        System.out.println(y);
//        System.out.println(plus);
//        System.out.println(minus);
//        System.out.println();

        updateFields();
        redraw();
    }

    private void redraw(){
        image = new BufferedImage(xRes, yRes, BufferedImage.TYPE_INT_ARGB);

        createMap();
        int[] range = calcRange();//[turtle, turtle, turtle, xmin, xmax, ymin, ymax]
        //System.out.println(Arrays.toString(range));
        double xScale = (double)(xRes)/(range[4] - range[3]);
        double yScale = (double)(yRes)/(range[6] - range[5]);
        double scale = Math.min(xScale, yScale);
        int xCenter = (int)(((range[3] + range[4])* scale)/2);
        int yCenter = (int)(((range[5] + range[6])* scale)/2);

        int xStart = (xRes/2) - xCenter;
        int yStart = (yRes/2) - yCenter;
        int[] turtle = new int[]{xStart, yStart, -90};
        //System.out.println(Arrays.toString(turtle) + " " + scale);
        Graphics2D g = (Graphics2D) image.getGraphics();
        g.setStroke(new BasicStroke(stroke));
        g.setColor(one);
        for(char c : axiom.toCharArray()){
            draw(0, c, turtle, true, scale*0.95, g);
        }
        g.dispose();
        largest.reset();
    }

    private void draw(int rep, char c, int[] turtle, boolean print, double scale, Graphics2D g){
        if(rep < depth){
            String next = map.get(c);
            rep++;
            for(char n : next.toCharArray()){
                draw(rep, n, turtle, print, scale, g);
            }
            return;
        }
        //System.out.print(c);

        if(c == 'F' || c == 'G' || c == 'H'){

            if(print){
                int xn = (int)(Math.cos((Math.PI * turtle[2])/180)*100*scale) + turtle[0];
                int yn = (int)(Math.sin((Math.PI * turtle[2])/180)*100*scale) + turtle[1];
                //System.out.println(turtle[0] + " " + turtle[1] + " " + xn + " " + yn);
                g.drawLine(turtle[0], turtle[1], xn, yn);
                turtle[0] = xn;
                turtle[1] = yn;
            }else{
                int xn = (int)(Math.cos((Math.PI * turtle[2])/180)*100*scale) + turtle[0];
                int yn = (int)(Math.sin((Math.PI * turtle[2])/180)*100*scale) + turtle[1];
                turtle[0] = xn;
                turtle[1] = yn;
                turtle[3] = Math.min(turtle[3], xn);
                turtle[4] = Math.max(turtle[4], xn);
                turtle[5] = Math.min(turtle[5], yn);
                turtle[6] = Math.max(turtle[6], yn);
            }
        }else if(c == '+'){
            turtle[2] = turtle[2] + plus;
        }else if(c == '-'){
            turtle[2] = turtle[2] + minus;
        }else if(c == '1' && print){
            g.setColor(one);
        }else if(c == '2'&& print){
            g.setColor(two);
        }else if(c == '3'&& print){
            g.setColor(three);
        }else if(c == '['){
            //include color stack??
            xStack.push(turtle[0]);
            yStack.push(turtle[1]);
            dStack.push(turtle[2]);
        }else if(c == ']'){
            turtle[0] = xStack.pop();
            turtle[1] = yStack.pop();
            turtle[2] = dStack.pop();
        }
    }

    private int[] calcRange(){
        int[] turtle = new int[7];
//        turtle[0] = 0;
//        turtle[1] = 0;
        turtle[2] = -90;
//        turtle[3] = Integer.MAX_VALUE;
//        turtle[4] = Integer.MIN_VALUE;
//        turtle[5] = Integer.MAX_VALUE;
//        turtle[6] = Integer.MIN_VALUE;

        for(char c : axiom.toCharArray()){
            draw(0, c, turtle, false, 1.0, null);
        }

        return turtle;
    }

    private void createMap(){
        map = new HashMap<>();
        map.put('F', f);
        map.put('G', g);
        map.put('H', h);
        map.put('X', x);
        map.put('Y', y);
        map.put('+', "+");
        map.put('-', "-");
        map.put('[', "[");
        map.put(']', "]");
        map.put('1', "1");
        map.put('2', "2");
        map.put('3', "3");
    }

    private void initImage(){
        int[] mainRes = largest.getMainRes();
        xRes = mainRes[0];
        yRes = mainRes[1];
        image = new BufferedImage(xRes, yRes, BufferedImage.TYPE_INT_ARGB);
    }

    @Override
    public void click(int x, int y) {

    }

    @Override
    public int[] getRes(){
        return new int[]{xRes, yRes};
    }

    @Override
    public BufferedImage getActiveImage(){
        return image;
    }
}

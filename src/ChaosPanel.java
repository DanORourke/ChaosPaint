import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.*;

public class ChaosPanel extends JPanel implements ShapeTab{
    private final Largest largest;
    private GridBagConstraints c = new GridBagConstraints();
    private int size = 5;
    private int reps = 100000;
    private double gravity = 0.5;
    private int vertexSize = 10;
    private int pointSize = 2;
    private boolean showVertex = false;
    private boolean blend = false;
    private ArrayList<HashSet<Integer>> past = new ArrayList<>();
    private LinkedList<Integer> link = new LinkedList<>();
    private ArrayList<Integer> colorList = new ArrayList<>();
    private int[] colorMap = new int[size];
    private ImagePanel imagePanel = new ImagePanel();
    private JLabel warning = new JLabel();
    private int xRes;
    private int yRes;
    private BufferedImage image;
    private int left = 0;
    private int right = 0;
    private boolean cycling = false;
    private ArrayList<Action> actionList = new ArrayList<>();

    ChaosPanel(Largest largest){
        super();
        this.largest = largest;
        setLayout(new GridBagLayout());
        setBackground(Largest.BACKGROUND);
        initImage();
        initPast();
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(1, 1, 1, 1);
        colorList.add(Color.BLUE.getRGB());
        colorList.add(Color.YELLOW.getRGB());
        colorList.add(Color.RED.getRGB());
        colorList.add(Color.ORANGE.getRGB());
        colorList.add(Color.GREEN.getRGB());
        initColorMap();
        updateFields();
        redraw();
    }

    private void updateFields(){
        c.weightx = 0.4;
        c.weighty = 0.4;
        c.gridwidth = 1;
        c.gridheight = 1;
        removeAll();
        addWarning();
        addNumberSides();
        addReps();
        addGravity();
        addRules1();
        addRules2();
        addRules3();
        addVSize();
        addPSize();
        addShow();
        addBlend();
        setColor();
        addRedraw();
        addStampify();
        addRandom();
        addResolution();
        addSpacers();
        initColorMap();
    }

    private void addLeft(Component com){
        c.gridx = 0;
        c.gridy = left++;
        add(com, c);
    }

    private void addRight(Component com){
        c.gridx = 2;
        c.gridy = right++;
        add(com, c);
    }

    private void addRules3(){
        JTextField r3Field = new JTextField(getPastString(past.get(2)));
        addRight(r3Field);
        JButton r3Btn = new JButton("-3 Rules");
        addLeft(r3Btn);
        Action a = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pastInput(2, r3Field.getText());
                r3Field.setText(getPastString(past.get(2)));
            }
        };
        r3Field.addActionListener(a);
        r3Btn.addActionListener(a);
        actionList.add(a);
    }

    private void addRules2(){
        JTextField r2Field = new JTextField(getPastString(past.get(1)));
        addRight(r2Field);
        JButton r2Btn = new JButton("-2 Rules");
        addLeft(r2Btn);
        Action a = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pastInput(1, r2Field.getText());
                r2Field.setText(getPastString(past.get(1)));
            }
        };
        r2Field.addActionListener(a);
        r2Btn.addActionListener(a);
        actionList.add(a);
    }

    private void addRules1(){
        JTextField r1Field = new JTextField(getPastString(past.get(0)));
        addRight(r1Field);
        JButton r1Btn = new JButton("-1 Rules");
        addLeft(r1Btn);
        Action a = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pastInput(0, r1Field.getText());
                r1Field.setText(getPastString(past.get(0)));
            }
        };
        r1Field.addActionListener(a);
        r1Btn.addActionListener(a);
        actionList.add(a);
    }

    private void pastInput(int index, String input){
        HashSet<Integer> temp = new HashSet<>();
        if(input.equals("")){
            past.set(index, temp);
            redraw();
            largest.reset();
            return;
        }

        int min = 0;
        int max = size - 1;
        if(index > 0){
            min = size * -1;
        }
        if(index == 2){
            min *= 2;
        }
        String[] arr = input.trim().split("\\s*,\\s*");
        for(String s : arr){
            try {
                int n = Integer.parseInt(s);
                if (n < min || n > max){
                    setWarningText(min + " <= int < vertex, int, ...");
                    return;
                }else{
                    temp.add(n);
                }
            }catch (NumberFormatException e1){
                setWarningText(min + "<= int < vertex, int, ...");
                return;
            }
        }
        past.set(index, temp);
        //System.out.println(past.get(index));
        redraw();
        largest.reset();
    }

    private String getPastString(HashSet<Integer> p){
        if(p.isEmpty()){
            return "";
        }
        ArrayList<Integer> sorted = new ArrayList<>(p);
        Collections.sort(sorted);
        StringBuilder sb = new StringBuilder();
        sb.append(sorted.get(0));
        for(int i = 1; i < sorted.size(); i++){
            sb.append(", ");
            sb.append(sorted.get(i));
        }
        return sb.toString();
    }

    private void addGravity(){
        JTextField gField = new JTextField(String.valueOf(gravity));
        addRight(gField);
        JButton gBtn = new JButton("Gravity");
        addLeft(gBtn);
        Action a = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    double s = Double.parseDouble(gField.getText());
                    if (s < 0.0 || s > 1.0){
                        setWarningText("0 <= double <= 1");
                    }else{
                        gravity = s;
                        redraw();
                    }
                }catch (NumberFormatException e1){
                    setWarningText("0 <= double <= 1");
                }
                gField.setText(String.valueOf(gravity));
            }
        };
        gField.addActionListener(a);
        gBtn.addActionListener(a);
        actionList.add(a);
    }

    private void addBlend(){
        JCheckBox showCheck = new JCheckBox("Blend");
        showCheck.setSelected(blend);
        showCheck.setBackground(Largest.BACKGROUND);
        showCheck.setForeground(Color.WHITE);

        showCheck.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                blend = showCheck.isSelected();
                redraw();
            }
        });

        addRight(showCheck);
    }

    private void addShow(){
        JCheckBox showCheck = new JCheckBox("Show Vertex");
        showCheck.setSelected(showVertex);
        showCheck.setBackground(Largest.BACKGROUND);
        showCheck.setForeground(Color.WHITE);

        showCheck.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showVertex = showCheck.isSelected();
                redraw();
            }
        });

        addLeft(showCheck);
    }

    private void addPSize(){
        JTextField pField = new JTextField(String.valueOf(pointSize));
        addRight(pField);
        JButton pBtn = new JButton("Point Size");
        addLeft(pBtn);
        Action a = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int s = Integer.parseInt(pField.getText());
                    if (s < 1){
                        setWarningText("Enter an int greater than 0");
                    }else{
                        pointSize = s;
                        redraw();
                    }
                }catch (NumberFormatException e1){
                    setWarningText("Enter an int greater than 0");
                }
                pField.setText(String.valueOf(pointSize));
            }
        };
        pField.addActionListener(a);
        pBtn.addActionListener(a);
        actionList.add(a);
    }

    private void addVSize(){
        JTextField vField = new JTextField(String.valueOf(vertexSize));
        addRight(vField);
        JButton vBtn = new JButton("Vertex Size");
        addLeft(vBtn);
        Action a = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int s = Integer.parseInt(vField.getText());
                    if (s < 1){
                        setWarningText("Enter an int greater than 0");
                    }else{
                        vertexSize = s;
                        redraw();
                    }
                }catch (NumberFormatException e1){
                    setWarningText("Enter an int greater than 0");
                }
                vField.setText(String.valueOf(vertexSize));
            }
        };
        vField.addActionListener(a);
        vBtn.addActionListener(a);
        actionList.add(a);
    }

    private void addReps(){
        JTextField repField = new JTextField(String.valueOf(reps));
        addRight(repField);
        JButton repBtn = new JButton("Iterations");
        addLeft(repBtn);
        Action a = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int s = Integer.parseInt(repField.getText());
                    if (s < 1){
                        setWarningText("Enter an int greater than 0");
                    }else{
                        reps = s;
                        redraw();
                    }
                }catch (NumberFormatException e1){
                    setWarningText("Enter an int greater than 0");
                }
                repField.setText(String.valueOf(reps));
            }
        };
        repField.addActionListener(a);
        repBtn.addActionListener(a);
        actionList.add(a);
    }

    private void addNumberSides(){
        JTextField sizeField = new JTextField(String.valueOf(size));
        addRight(sizeField);
        JButton sizeBtn = new JButton("Vertices");
        addLeft(sizeBtn);
        Action a = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int s = Integer.parseInt(sizeField.getText());
                    if (s < 3){
                        setWarningText("Enter an int greater than 2");
                    }else{
                        size = s;
                        fixPast();
                        fixColors();
                        if(!cycling){
                            updateFields();
                        }
                        redraw();
                    }
                }catch (NumberFormatException e1){
                    setWarningText("Enter an int greater than 2");
                }
                sizeField.setText(String.valueOf(size));
            }
        };
        sizeField.addActionListener(a);
        sizeBtn.addActionListener(a);
        actionList.add(a);
    }

    private void fixColors(){
        while(colorList.size() > size){
            colorList.remove(colorList.size()-1);
        }
    }

    private void fixPast(){
        for(int i = 0; i < 3; i++){
            HashSet<Integer> temp = new HashSet<>();
            for(Integer n : past.get(i)){
                if(n < size){
                    temp.add(n);
                }
            }
            past.set(i, temp);
        }
    }

    private void addWarning(){
        c.gridx = 0;
        c.gridy = left++;
        right++;
        c.gridwidth = 3;
        warning.setBackground(Largest.BACKGROUND);
        warning.setForeground(Largest.BACKGROUND);
        warning.setText("filler");
        this.add(warning, c);
        c.gridwidth = 1;
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

    private void initImage(){
        int[] mainRes = largest.getMainRes();
        xRes = mainRes[0];
        yRes = mainRes[1];
        image = new BufferedImage(xRes, yRes, BufferedImage.TYPE_INT_ARGB);
    }

    private void redraw(){
        if(cycling){
            return;
        }
        initColorMap();
        image = new BufferedImage(xRes, yRes, BufferedImage.TYPE_INT_ARGB);
        ArrayList<Vertex> vertex = createVertex();
        Graphics2D g = (Graphics2D) image.getGraphics();
        if (showVertex){
            for(Vertex v : vertex){
                g.setColor(new Color(v.getC()));
                g.drawOval(v.getX() - vertexSize/2, v.getY() - vertexSize/2, vertexSize, vertexSize);
                g.fillOval(v.getX() - vertexSize/2, v.getY() - vertexSize/2, vertexSize, vertexSize);
                //System.out.println(v.getX() + " " + v.getY());
            }
        }
        int[] point = new int[]{xRes/2, yRes/2, colorMap[0]};

        for (int i = 0; i < 10; i++){
            if (point[0] == -1){
                largest.reset();
                return;
            }
            point = repeat(point, vertex, g, false);
        }

        for (int i = 0; i < reps ; i++){
            if (point[0] == -1){
                largest.reset();
                return;
            }
            point = repeat(point, vertex, g, true);
        }
        g.dispose();
        largest.reset();
    }

    private int[] repeat(int[] point, ArrayList<Vertex> vertex, Graphics2D g, boolean draw){
        Random r = new Random();

        Vertex next = vertex.get(r.nextInt(vertex.size()));
        int count = 0;
        int over = 100;
        while(!canChoose(next) && count < over){
            next = vertex.get(r.nextInt(vertex.size()));
            count++;
        }
        if (count == over){
            setWarningText("Too many misses");
            return new int[]{-1, -1, -1};
        }

        double other = 1.0 - gravity;
        int nx = (int)((point[0]*other) + (next.getX()*gravity));
        int ny = (int)((point[1]*other) + (next.getY()*gravity));
        int nc = Stamp.blend(point[2], next.getC(), gravity);

        if (draw){
            if(blend){
                for(int x = nx - pointSize + 1; x < nx + pointSize; x++){
                    for(int y = ny - pointSize + 1; y < ny + pointSize; y++){
                        if(x < 0 || y < 0 || x >= xRes || y >= yRes){
                            continue;
                        }
                        int oldc = image.getRGB(x, y);
                        if((oldc >> 24 & 0xff) == 0){
                            image.setRGB(x, y, nc);
                        }else{
                            image.setRGB(x, y, Stamp.blend(nc, oldc, 0.5));
                        }
                    }
                }

            }else{
                g.setColor(new Color(nc));
//            float alpha  = 0.5f;
//            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_IN, alpha));
                g.drawOval(nx - pointSize /2, ny - pointSize /2, pointSize, pointSize);
                g.fillOval(nx - pointSize /2, ny - pointSize /2, pointSize, pointSize);
//            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OUT));
//            g.drawOval(nx - pointSize/2, ny - pointSize/2, pointSize, pointSize);
//            g.fillOval(nx - pointSize/2, ny - pointSize/2, pointSize, pointSize);
            }
        }
        return new int[]{nx, ny, nc};
    }

//    private boolean canChoose(Vertex v){
//        int id = v.getId();
//
//        if (link.size() == 3){
//            int p1 = link.get(0);
//            int p2 = link.get(1);
//            int p3 = link.get(2);
//
//            for(Integer gap : past.get(0)){
//                if ((id + gap)%size == p1){
//                    if (past.get(1).contains(-1) && p1 == p2){
//                        return false;
//                    }else if (past.get(2).contains(-2) && p1 == p3){
//                        return false;
//                    }else if (!past.get(1).contains(-1) && !past.get(2).contains(-2)){
//                        return false;
//                    }
//
//                }
//            }
//
//            for(Integer gap : past.get(1)){
//                if (gap != -1 && (id + gap)%size == p2){
//                    if (past.get(2).contains(-1) && p2 == p3){
//                        return false;
//                    }else if (!past.get(2).contains(-1)){
//                        return false;
//                    }
//                }
//            }
//
//            for(Integer gap : past.get(2)){
//                if (gap >= 0 && (id + gap)%size == p3){
//                    return false;
//                }
//            }
//
//            link.removeLast();
//        }
//        link.addFirst(id);
//
//        return true;
//    }

    private boolean canChoose(Vertex v){
        int id = v.getId();

        if (link.size() == 3){
            int p1 = link.get(0);
            int p2 = link.get(1);
            int p3 = link.get(2);

            for(Integer gap : past.get(0)){
                if ((id + gap)%size == p1){
                    return false;
                }
            }

            for(Integer gap : past.get(1)){
                if (gap >= 0 && (id + gap)%size == p2){
                    return false;
                }else{
                    int ng = gap * -1;
                    if((id + ng)%size == p2 && p1 == p2){
                        return false;
                    }
                }
            }

            for(Integer gap : past.get(2)){
                if (gap >= 0 && (id + gap)%size == p3){
                    return false;
                }else if (gap < 0 && gap >= (size*-1)){
                    int ng = gap * -1;
                    if((id + ng)%size == p3 && p2 == p3){
                        return false;
                    }
                }else{
                    int ng = (gap  * -1) - size;
                    if((id + ng)%size == p3 && p1 == p3){
                        return false;
                    }
                }

            }
            link.removeLast();
        }
        link.addFirst(id);

        return true;
    }

    private void initPast(){
        HashSet<Integer> p1 = new HashSet<>();
        past.add(p1);
        HashSet<Integer> p2 = new HashSet<>();
        p2.add(-1);
        p2.add(-4);
        past.add(p2);
        HashSet<Integer> p3 = new HashSet<>();
        p3.add(-6);
        p3.add(-9);
        past.add(p3);
    }

    private ArrayList<Vertex> createVertex(){
        ArrayList<Vertex> vertex = new ArrayList<>();
        int centerX = xRes/2;
        int centerY = yRes/2;
        int distance = Math.min(centerX, centerY) - vertexSize;

        int start = -90;

        for(int i = 0; i < size; i++){
            int degree = start + ((i*360)/size);
            int yn = (int)(Math.sin((Math.PI * degree)/180)*distance) + centerY;
            int xn = (int)(Math.cos((Math.PI * degree)/180)*distance) + centerX;
            vertex.add(new Vertex(i, xn, yn, colorMap[i]));
        }
        return vertex;
    }

    private void initColorMap() {
        colorMap = new int[size];
        if (colorList.size() == 0)
        {
            for (int i = 0 ; i < size; i++){
                colorMap[i] = 0;
            }
            return;
        }

        if (colorList.size() == 1)
        {
            int c = colorList.get(0);
            for (int i = 0 ; i < size; i++){
                colorMap[i] = c;
            }
            return;
        }
        double mapDelta = 1.0/(size-1);
        double listDelta = 1.0/(colorList.size()-1);
        for (int i=0; i < size; i++) {
            double mapPercent = mapDelta * i;
            int index0 = (int)(mapPercent / listDelta);
            int index1 = Math.min(index0 + 1, colorList.size() - 1);
            double local = ((mapPercent/listDelta) - index0);
            colorMap[i] = Stamp.blend(colorList.get(index0), colorList.get(index1), 1.0 - local);
        }
        imagePanel.setImage(getPanelImage());
    }

    private void setColor(){
        c.gridx = 0;
        c.gridy = left++;
        c.gridwidth = 3;
        add(imagePanel, c);
        right++;
        c.gridwidth = 1;

        JButton adColor = new JButton("Add Color");
        adColor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color newColor = JColorChooser.showDialog(
                        ChaosPanel.this, "", null);
                if (newColor != null && colorList.size() < size){
                    addColor(newColor.getRGB());
                }else{
                    setWarningText("Too many Colors");
                }
            }
        });

        JButton remColor = new JButton("Remove Color");
        remColor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!colorList.isEmpty()){
                    removeColor();
                }
            }
        });

        addLeft(adColor);
        addRight(remColor);
    }

    private Image getPanelImage(){
        int height = 1;
        BufferedImage inImage = new BufferedImage(size, height, BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < size; x++){
            int c = colorMap[x];
            for(int y = 0; y < height; y++){
                inImage.setRGB(x, y, c);
            }
        }
        return inImage;
    }

    private void addColor(int c){
        colorList.add(c);
        initColorMap();
        imagePanel.setImage(getPanelImage());
        redraw();
    }

    private void removeColor(){
        colorList.remove(colorList.size() - 1);
        initColorMap();
        imagePanel.setImage(getPanelImage());
        redraw();
    }

    private void addRedraw(){
        JButton rd = new JButton("Redraw");
        rd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cycling = true;
                for(ActionListener a : actionList){
                    a.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null) {
                        //Nothing need go here, the actionPerformed method (with the
                        //above arguments) will trigger the respective listener
                    });
                }
                cycling = false;
                redraw();
            }
        });
        addRight(rd);
        left++;
    }

    private void addStampify(){
        JButton stampify = new JButton("Stampify");
        stampify.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                largest.stampify(image);
            }
        });
        addLeft(stampify);
    }

    private void addRandom(){
        JButton r = new JButton("Random");
        r.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                randomize();
            }
        });
        addRight(r);
    }

    private void randomize(){
        Random r = new Random();
        double chance = 0.2;
//        size = r.nextInt(10) + 3;
        //avoid too low cause they are mostly just blobs in the middle, but higher can be good
//        gravity = 0.5 + ((r.nextDouble() - 0.25)*0.4);
        while(colorList.size() > size){
            colorList.remove(colorList.size() - 1);
        }
//        colorList.clear();
//        colorList.add(Color.BLUE.getRGB());
//        colorList.add(Color.RED.getRGB());
        initColorMap();
        imagePanel.setImage(getPanelImage());

//        for(int i = 0; i < 3; i++){
//            HashSet<Integer>  p = past.get(i);
//            p.clear();
//            for(int j = 0 - i; j < size; j++){
//                if(r.nextDouble() < chance){
//                    p.add(j);
//                }
//            }
//        }
        for(int i = 0; i < 3; i++){
            HashSet<Integer>  p = past.get(i);
            p.clear();
            int min = 0;
            if(i > 0){
                min = size * -1;
                chance *= 0.5;
            }
            if(i == 2){
                min *= 2;
                chance *= 0.75;
            }
            for(int j = min; j < size; j++){
                if(r.nextDouble() < chance){
                    p.add(j);
                }
            }
        }
        updateFields();
        redraw();
    }

    private void addResolution(){
        JTextField resField = new JTextField(String.valueOf(xRes));
        addRight(resField);
        JButton resBtn = new JButton("Resolution");
        addLeft(resBtn);
        Action a = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int its = Integer.parseInt(resField.getText());
                    if (its < 1){
                        setWarningText("Enter an int greater than 0");
                    }else{
                        xRes = its;
                        yRes = its;
                        image = new BufferedImage(xRes, yRes, BufferedImage.TYPE_INT_ARGB);
                        largest.changeRes(getRes());
                        redraw();
                    }
                }catch (NumberFormatException e1){
                    setWarningText("Enter an int greater than 0");
                }
                resField.setText(String.valueOf(xRes));
            }
        };
        resField.addActionListener(a);
        resBtn.addActionListener(a);
        actionList.add(a);
    }

    private void addSpacers() {
        JLabel x1 = new JLabel();
        x1.setBackground(Largest.BACKGROUND);
        c.gridx = 1;
        c.gridy = 1;
        c.gridheight = 13;
        c.weightx = 0.2;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        this.add(x1, c);
    }

    @Override
    public void reset(){
        updateFields();
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

    class ImagePanel extends JPanel{
        private Image image;

        ImagePanel(){
            super();
        }

        void setImage(Image image) {
            this.image = image;
        }

        @Override
        public void paint(Graphics g){
            g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), null);
        }
    }
}


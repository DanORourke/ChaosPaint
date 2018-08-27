import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

public class JuliaPanel extends JPanel implements ShapeTab{
    private final Largest largest;
    private double c1Real = 0.0;
    private double c1Imaginary = 0.0;
    private int z1Power = 0;
    private double c2Real = 1.0;
    private double c2Imaginary = 0.0;
    private int z2Power = 2;
    private double c3Real = 0.272;
    private double c3Imaginary = 0.005;
    private boolean inverse = false;
    private boolean rational = false;
    private boolean biomorph = false;
    private int iterations = 64;
    private double xSpan = 4.0;
    private double ySpan = 4.0;
    private double xCenter = 0.0;
    private double yCenter = 0.0;
    private GridBagConstraints c = new GridBagConstraints();
    private ArrayList<Color> inColorList = new ArrayList<>();
    private int[] inColorMap = new int[iterations];
    private ArrayList<Color> outColorList = new ArrayList<>();
    private int[] outColorMap = new int[iterations];
    private JLabel warning = new JLabel();
    private ImagePanel inPanel = new ImagePanel();
    private ImagePanel outPanel = new ImagePanel();
    private int xRes;
    private int yRes;
    private BufferedImage image;
    private int left = 0;
    private int right = 0;
    private boolean cycling = false;
    private ArrayList<Action> actionList = new ArrayList<>();
    private JLabel instruct;

    JuliaPanel(Largest largest){
        super();
        this.largest = largest;
        setLayout(new GridBagLayout());
        setBackground(Largest.BACKGROUND);
        initImage();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.4;
        c.weighty = 0.4;
        c.insets = new Insets(1, 1, 1, 1);
        inColorList.add(Color.black);
        initInColorMap();
        inPanel.setImage(getInImage());
        outColorList.add(Color.yellow);
        outColorList.add(Color.BLUE);
        initOutColorMap();
        outPanel.setImage(getOutImage());
        updateFields();
        redraw();
    }

    private void initImage(){
        int[] mainRes = largest.getMainRes();
        xRes = mainRes[0];
        yRes = mainRes[1];
        image = new BufferedImage(xRes, yRes, BufferedImage.TYPE_INT_ARGB);
    }

    private void updateFields(){
        c.weightx = 0.4;
        c.weighty = 0.4;
        c.gridwidth = 1;
        c.gridheight = 1;
        removeAll();
        addWarning();
        addInstruct();
        addC1Z1();
        addC2Z2();
        addC3();
        addIterations();
        addChecks();
        setInColor();
        setOutColor();
        addRedraw();
        addStampify();
        addRandom();
        addResolution();
        addSpacers();
    }

    private void addLeft(Component com){
        c.gridx = 0;
        c.gridy = left++;
        add(com, c);
        int y;
    }

    private void addRight(Component com){
        c.gridx = 2;
        c.gridy = right++;
        add(com, c);
    }


    @Override
    public void reset(){
        updateFields();
    }

    private void addSpacers() {
        JLabel x1 = new JLabel();
        x1.setBackground(Largest.BACKGROUND);
        c.gridx = 1;
        c.gridy = 1;
        c.gridheight = 17;
        c.weightx = 0.2;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        this.add(x1, c);
    }

    private void addRedraw(){
        JButton r = new JButton("Redraw");
        addRight(r);
        left++;
        r.addActionListener(new ActionListener() {
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
    }

    private void addRandom(){
        JButton rBtn = new JButton("Random");
        rBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                randomize();
            }
        });
        addRight(rBtn);
    }

    private void randomize(){
        Random r = new Random();
        double d = 6.0;
        int i = 7;
        xSpan = 4.0;
        ySpan = 4.0;
        xCenter = 0.0;
        yCenter = 0.0;
        c1Real = r.nextDouble()* d - d/2;
        c1Imaginary = r.nextDouble()* d - d/2;
        z1Power = r.nextInt(i) - i/2;
        c2Real = r.nextDouble()* d - d/2;
        c2Imaginary = r.nextDouble()* d - d/2;
        z2Power = r.nextInt(i) - i/2;
        c3Real = r.nextDouble()* d - d/2;
        c3Imaginary = r.nextDouble()* d - d/2;
        inverse = r.nextBoolean();
        rational = r.nextBoolean();
        updateFields();
        redraw();
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

    private void addIterations(){
        JTextField itField = new JTextField(String.valueOf(iterations));
        addRight(itField);
        JButton itBtn = new JButton("Iterations");
        addLeft(itBtn);
        Action a = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int its = Integer.parseInt(itField.getText());
                    if (its < 1){
                        setWarningText("Enter an int greater than 0");
                    }else{
                        iterations = its;
                        redraw();
                    }
                }catch (NumberFormatException e1){
                    setWarningText("Enter an int greater than 0");
                }
                itField.setText(String.valueOf(iterations));
            }
        };
        itField.addActionListener(a);
        itBtn.addActionListener(a);
        actionList.add(a);
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

    private void addInstruct(){
        instruct = new JLabel(getInstructText());
        instruct.setBackground(Largest.BACKGROUND);
        instruct.setForeground(Color.white);
        c.gridx = 0;
        c.gridy = left++;
        c.gridwidth = 3;
        add(instruct, c);
        c.gridwidth = 1;
        right++;
    }

    private void addChecks(){
        JCheckBox inverseCheck = new JCheckBox("1/F(z)");
        addLeft(inverseCheck);
        inverseCheck.setSelected(inverse);
        inverseCheck.setBackground(Largest.BACKGROUND);
        inverseCheck.setForeground(Color.WHITE);
        inverseCheck.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inverse = inverseCheck.isSelected();
                instruct.setText(getInstructText());
                redraw();
            }
        });

        JCheckBox rationalCheck = new JCheckBox("Z1/Z2");
        addRight(rationalCheck);
        rationalCheck.setSelected(rational);
        rationalCheck.setBackground(Largest.BACKGROUND);
        rationalCheck.setForeground(Color.WHITE);
        rationalCheck.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rational = rationalCheck.isSelected();
                instruct.setText(getInstructText());
                redraw();
            }
        });
    }

    private String getInstructText(){
        String s = "C1*Z1 + C2*Z2 + C3";
        if (rational){
            s = "((C1 + Z1)/(C2 + Z2))+ C3";
        }
        if (inverse){
            s = "1/(" + s + ")";
        }
        return "F(z) = " + s;
    }

    private void addC1Z1(){
        JTextField c1RealField = new JTextField(String.valueOf(c1Real));
        addRight(c1RealField);
        JTextField c1ImaginaryField = new JTextField(String.valueOf(c1Imaginary));
        addRight(c1ImaginaryField);
        JTextField z1Field = new JTextField(String.valueOf(z1Power));
        addRight(z1Field);

        JButton c1RealBtn = new JButton("C1 Real");
        addLeft(c1RealBtn);
        Action acr = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    c1Real = Double.parseDouble(c1RealField.getText());
                    redraw();
                }catch (NumberFormatException e1){
                    setWarningText("Enter a double");
                }
                c1RealField.setText(String.valueOf(c1Real));
            }
        };
        c1RealField.addActionListener(acr);
        c1RealBtn.addActionListener(acr);
        actionList.add(acr);

        JButton c1ImaginaryBtn = new JButton("C1 Imaginary");
        addLeft(c1ImaginaryBtn);
        Action aci = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    c1Imaginary = Double.parseDouble(c1ImaginaryField.getText());
                    redraw();
                }catch (NumberFormatException e1){
                    setWarningText("Enter a double");
                }
                c1ImaginaryField.setText(String.valueOf(c1Imaginary));
            }
        };
        c1ImaginaryField.addActionListener(aci);
        c1ImaginaryBtn.addActionListener(aci);
        actionList.add(aci);

        JButton z1Btn = new JButton("Z1 Power");
        addLeft(z1Btn);
        Action az = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    z1Power = Integer.parseInt(z1Field.getText());
                    redraw();
                }catch (NumberFormatException e1){
                    setWarningText("Enter an int");
                }
                z1Field.setText(String.valueOf(z1Power));
            }
        };
        z1Field.addActionListener(az);
        z1Btn.addActionListener(az);
        actionList.add(az);

    }

    private void addC2Z2(){
        JTextField c2RealField = new JTextField(String.valueOf(c2Real));
        addRight(c2RealField);
        JTextField c2ImaginaryField = new JTextField(String.valueOf(c2Imaginary));
        addRight(c2ImaginaryField);
        JTextField z2Field = new JTextField(String.valueOf(z2Power));
        addRight(z2Field);

        JButton c2RealBtn = new JButton("C2 Real");
        addLeft(c2RealBtn);
        Action acr = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    c2Real = Double.parseDouble(c2RealField.getText());
                    redraw();
                }catch (NumberFormatException e1){
                    setWarningText("Enter a double");
                }
                c2RealField.setText(String.valueOf(c2Real));
            }
        };
        c2RealField.addActionListener(acr);
        c2RealBtn.addActionListener(acr);
        actionList.add(acr);

        JButton c2ImaginaryBtn = new JButton("C2 Imaginary");
        addLeft(c2ImaginaryBtn);
        Action aci = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    c2Imaginary = Double.parseDouble(c2ImaginaryField.getText());
                    redraw();
                }catch (NumberFormatException e1){
                    setWarningText("Enter a double");
                }
                c2ImaginaryField.setText(String.valueOf(c2Imaginary));
            }
        };

        c2ImaginaryField.addActionListener(aci);
        c2ImaginaryBtn.addActionListener(aci);
        actionList.add(aci);

        JButton z2Btn = new JButton("Z2 Power");
        addLeft(z2Btn);
        Action az = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    z2Power = Integer.parseInt(z2Field.getText());
                    redraw();
                }catch (NumberFormatException e1){
                    setWarningText("Enter an int");
                }
                z2Field.setText(String.valueOf(z2Power));
            }
        };

        z2Field.addActionListener(az);
        z2Btn.addActionListener(az);
        actionList.add(az);
    }

    private void addC3(){
        JTextField c3RealField = new JTextField(String.valueOf(c3Real));
        addRight(c3RealField);
        JTextField c3ImaginaryField = new JTextField(String.valueOf(c3Imaginary));
        addRight(c3ImaginaryField);

        JButton c3RealBtn = new JButton("C3 Real");
        addLeft(c3RealBtn);
        Action ar = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    c3Real = Double.parseDouble(c3RealField.getText());
                    redraw();
                }catch (NumberFormatException e1){
                    setWarningText("Enter a double");
                }
                c3RealField.setText(String.valueOf(c3Real));
            }
        };
        c3RealField.addActionListener(ar);
        c3RealBtn.addActionListener(ar);
        actionList.add(ar);

        JButton c3ImaginaryBtn = new JButton("C3 Imaginary");
        addLeft(c3ImaginaryBtn);
        Action ai = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    c3Imaginary = Double.parseDouble(c3ImaginaryField.getText());
                    redraw();
                }catch (NumberFormatException e1){
                    setWarningText("Enter a double");
                }
                c3ImaginaryField.setText(String.valueOf(c3Imaginary));
            }
        };
        c3ImaginaryField.addActionListener(ai);
        c3ImaginaryBtn.addActionListener(ai);
        actionList.add(ai);
    }

    private void addInColor(Color c){
        inColorList.add(c);
        initInColorMap();
        inPanel.setImage(getInImage());
        redraw();
    }

    private void removeInColor(){
        inColorList.remove(inColorList.size() - 1);
        initInColorMap();
        inPanel.setImage(getInImage());
        redraw();
    }

    private void addOutColor(Color c){
        outColorList.add(c);
        initOutColorMap();
        outPanel.setImage(getOutImage());
        redraw();
    }

    private void removeOutColor(){
        outColorList.remove(outColorList.size() - 1);
        initOutColorMap();
        outPanel.setImage(getOutImage());
        redraw();
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

    private void setOutColor(){
        c.gridx = 0;
        c.gridy = left++;
        c.gridwidth = 3;
        add(outPanel, c);
        right++;
        c.gridwidth = 1;

        JButton adColor = new JButton("Add outColor");
        addLeft(adColor);
        adColor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color newColor = JColorChooser.showDialog(
                        JuliaPanel.this, "", null);
                if (newColor != null && outColorList.size() < iterations){
                    addOutColor(newColor);
                }else{
                    setWarningText("Too many Colors");
                }
            }
        });

        JButton remColor = new JButton("Remove outColor");
        addRight(remColor);
        remColor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!outColorList.isEmpty()){
                    removeOutColor();
                }
            }
        });
    }

    private Image getOutImage(){
        int height = 25;
        BufferedImage outImage = new BufferedImage(iterations, height, BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < iterations; x++){
            int c = outColorMap[x];
            for(int y = 0; y < height; y++){
                outImage.setRGB(x, y, c);
            }
        }
        return outImage;
    }

    private Image getInImage(){
        int height = 25;
        BufferedImage inImage = new BufferedImage(iterations, height, BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < iterations; x++){
            int c = inColorMap[x];
            for(int y = 0; y < height; y++){
                inImage.setRGB(x, y, c);
            }
        }
        return inImage;
    }

    private void setInColor(){
        c.gridx = 0;
        c.gridy = left++;
        c.gridwidth = 3;
        add(inPanel, c);
        right++;
        c.gridwidth = 1;

        JButton adColor = new JButton("Add inColor");
        addLeft(adColor);
        adColor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color newColor = JColorChooser.showDialog(
                        JuliaPanel.this, "", null);
                if (newColor != null && inColorList.size() < iterations){
                    addInColor(newColor);
                }else{
                    setWarningText("Too many Colors");
                }
            }
        });

        JButton remColor = new JButton("Remove inColor");
        addRight(remColor);
        remColor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!outColorList.isEmpty()){
                    removeInColor();
                }
            }
        });
    }

    private void redraw(){
        if(cycling){
            return;
        }
        initOutColorMap();
        initInColorMap();

        double insideMax = 0.0;
        double insideMin = Double.MAX_VALUE;
        double[] outMax = new double[iterations+2];
        double[] outMin = new double[iterations+2];
        Arrays.fill(outMin, Double.MAX_VALUE);
        double[][][] hold = new double[xRes][yRes][2];
        image = new BufferedImage(xRes, yRes, BufferedImage.TYPE_INT_ARGB);
        double xFactor = xSpan/xRes;
        double yFactor = ySpan/yRes;
        int xResCenter = xRes/2;
        int yResCenter = yRes/2;
        for (int x = 0; x < xRes; x++){
            for(int y = 0 ; y < yRes; y++){
                double areal = ((x - xResCenter ) * xFactor) - xCenter;
                double aim = ((y - yResCenter) * yFactor) - yCenter;
                double[] it = iterate(areal, aim);
                hold[x][y][0] = it[0];
                hold[x][y][1] = it[1];
                if ((int)it[0] == iterations){
                    insideMax = Math.max(insideMax, it[1]);
                    insideMin = Math.min(insideMin, it[1]);
                }else{
                    outMax[(int)it[0]] = Math.max(outMax[(int)it[0]], it[1]);
                    outMin[(int)it[0]] = Math.min(outMin[(int)it[0]], it[1]);
                }
            }
        }
        //could init color maps now that i know the range, remove colors never accessed
        addHold(hold, insideMax, insideMin, outMax, outMin);
        largest.reset();
    }

    private void addHold(double[][][] hold, double insideMax, double insideMin, double[] outMax, double[] outMin){
        double gap = insideMax - insideMin;
        double step = (gap/iterations);
        for (int x = 0; x < hold.length; x++){
            for (int y = 0; y < hold[0].length; y++){
                double[] point = hold[x][y];
                if ((int)point[0] == iterations){
                    double p = point[1];
                    if (insideMax == 0.0 || p == insideMax) {
                        image.setRGB(x, y, inColorMap[iterations-1]);
                    }else{
                        p -= insideMin;
                        int indexLow = (int)((p*iterations)/gap);
                        double local = ((p - indexLow) / step);
                        int color = interpolate(inColorMap, indexLow, local);
                        image.setRGB(x, y, color);
                    }
                }else if (point[0] >= iterations - 1){
                    image.setRGB(x, y, inColorMap[iterations-1]);
                }else{
                    if (outMax[(int)point[0]] == 0){
                        System.out.println("problem " + x + " " + y);
                        continue;
                    }
                    int indexLow = iterations - (int)point[0] - 1;
                    double local = (point[1] - outMin[indexLow])/(outMax[indexLow] - outMin[indexLow]);
                    int color = interpolate(outColorMap, indexLow, local);
                    image.setRGB(x, y, color);
                }
            }
        }
    }

    private int interpolate(int[] colorMap, int indexLow, double local){
        if (indexLow == colorMap.length - 1){
            return colorMap[indexLow];
        }

        int indexHigh = indexLow + 1;
        int c0 = colorMap[indexLow];
        int c1 = colorMap[indexHigh];
        return Stamp.blend(c0, c1, 1.0 - local);
    }

    private double[] iterate(double areal, double aim){
        int it = 0;
        int limit = 4;
        ComplexNumber lex = new ComplexNumber(areal, aim);
        while(it < iterations && lex.distance() < limit){
            ComplexNumber next;
            if (biomorph){
                //not working right
                next = new ComplexNumber(lex);
                next.power(2);
                double sin = lex.sin();
                next.add(sin+c3Real, c3Imaginary);
            }else{
                if (rational){
                    next = new ComplexNumber(lex);
                    next.power(z1Power);
                    next.add(c1Real, c1Imaginary);
                    ComplexNumber denom = new ComplexNumber(lex);
                    denom.power(z2Power);
                    denom.add(c2Real, c2Imaginary);
                    next.divide(denom);
                    next.add(c3Real, c3Imaginary);
                }else{
                    ComplexNumber first = new ComplexNumber(lex);
                    first.power(z1Power);
                    first.multiply(c1Real, c1Imaginary);
                    ComplexNumber second = new ComplexNumber(lex);
                    second.power(z2Power);
                    second.multiply(c2Real, c2Imaginary);
                    next = new ComplexNumber(c3Real, c3Imaginary);
                    next.add(first);
                    next.add(second);
                }
            }
            if (inverse){
               next.inverse();
            }

            if (next.isUndefined()){
                return new double[]{(double)iterations+1, 0.0, 0.0};
            }
            lex = next;
            it++;
        }
        if (it == iterations){
            //add distance traveled
            double travelD = (((areal - lex.getReal())*(areal - lex.getReal())) +
                    ((aim - lex.getImaginary()) * (aim - lex.getImaginary())));
            return new double[]{(double)it, travelD};
        } else{
            return new double[]{(double)it, lex.distance()};
        }
    }

    private void initInColorMap(){
        inColorMap = new int[iterations];
        if (inColorList.size() == 0)
        {
            for (int i = 0 ; i < iterations; i++){
                inColorMap[i] = 0;
            }
            return;
        }
        if (inColorList.size() == 1)
        {
            Color c = inColorList.get(0);
            for (int i = 0 ; i < iterations; i++){
                inColorMap[i] = c.getRGB();
            }
            return;
        }
        double colorDelta = 1.0 / (inColorList.size() - 1);
        for (int i=0; i<iterations; i++) {
            double globalRel = (double) i / (iterations - 1);
            int index0 = (int) (globalRel / colorDelta);
            int index1 = Math.min(inColorList.size() - 1, index0 + 1);
            double localRel = (globalRel - (index0 * colorDelta)) / colorDelta;

            int c0 = inColorList.get(index0).getRGB();
            int c1 = inColorList.get(index1).getRGB();
            inColorMap[i] = Stamp.blend(c0, c1, 1.0 - localRel);

        }
    }

    private void initOutColorMap() {
        outColorMap = new int[iterations];
        if (outColorList.size() == 0)
        {
            for (int i = 0 ; i < iterations; i++){
                outColorMap[i] = 0;
            }
            return;
        }

        if (outColorList.size() == 1)
        {
            int c = outColorList.get(0).getRGB();
            for (int i = 0 ; i < iterations; i++){
                outColorMap[i] = c;
            }
            return;
        }
        double colorDelta = 1.0 / (outColorList.size()-1);
        for (int i=0; i < iterations; i++)
        {
            double globalRel = (double)i / (iterations-1);
            int index0 = (int)(globalRel / colorDelta);
            int index1 = Math.min(outColorList.size()-1, index0 + 1);
            double localRel = (globalRel - (index0*colorDelta)) / colorDelta;

            int c0 = outColorList.get(index0).getRGB();
            int c1 = outColorList.get(index1).getRGB();
            outColorMap[i] = Stamp.blend(c0, c1, 1.0 - localRel);
        }
    }

    @Override
    public void shiftClick(int x, int y) {
        xSpan = xSpan*2;
        ySpan = ySpan*2;
        redraw();
    }

    @Override
    public void click(int x, int y) {
        double xFactor = xSpan/xRes;
        double yFactor = ySpan/yRes;
        int xResCenter = xRes/2;
        int yResCenter = yRes/2;
        xCenter -= ((x - xResCenter) * xFactor);
        yCenter -= ((y - yResCenter) * yFactor);
        xSpan = xSpan/2;
        ySpan = ySpan/2;
        redraw();
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



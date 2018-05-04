import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
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
    GridBagConstraints c = new GridBagConstraints();
    private ArrayList<Color> inColorList = new ArrayList<>();
    private byte[][] inColorMap = new byte[iterations][4];
    private ArrayList<Color> outColorList = new ArrayList<>();
    private byte[][] outColorMap = new byte[iterations][4];
    private JLabel warning = new JLabel();
    private ImagePanel inPanel = new ImagePanel();
    private ImagePanel outPanel = new ImagePanel();
    private Stamp myStamp = new Stamp();
    private LinkedList<Vertex> shape = new LinkedList<>();

    JuliaPanel(Largest largest){
        super();
        this.largest = largest;
        setLayout(new GridBagLayout());
        setBackground(Largest.BACKGROUND);
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.4;
        c.weighty = 0.1;
        c.insets = new Insets(1, 1, 1, 1);
        addInColor(Color.black);
        addOutColor(Color.yellow);
        addOutColor(Color.BLUE);
        updateFields();
    }

    private void updateFields(){
        removeAll();
        addWarning();
        addInstruct();
        addC1Z1();
        addC2Z2();
        addC3();
        addChecks();
        addIterations();
        setInColor();
        setOutColor();
        addStampify();
        addRandom();
        redraw();
    }

    private void addRandom(){
        JButton rBtn = new JButton("Random");
        rBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                randomize();
                updateFields();
            }
        });
        c.gridx = 2;
        c.gridy = 16;
        add(rBtn, c);
    }

    private void randomize(){
        Random r = new Random();
        double d = 6.0;
        int i = 7;
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
                largest.setStamp(myStamp);
                largest.stampWorking();
                largest.workingToStamp();
            }
        });
        c.gridx = 0;
        c.gridy = 16;
        add(stampify, c);
    }

    private void addIterations(){
        JTextField itField = new JTextField(String.valueOf(iterations));
        JButton itBtn = new JButton("Iterations");
        itBtn.addActionListener(new ActionListener() {
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
        });

        c.gridx = 0;
        c.gridy = 10;
        add(itBtn, c);
        c.gridx = 2;
        add(itField, c);
    }

    private void addChecks(){
        JCheckBox inverseCheck = new JCheckBox("1/F(z)");
        inverseCheck.setSelected(inverse);
        inverseCheck.setBackground(Largest.BACKGROUND);
        inverseCheck.setForeground(Color.WHITE);

        inverseCheck.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inverse = inverseCheck.isSelected();
                redraw();
            }
        });

        JCheckBox rationalCheck = new JCheckBox("Rational");
        rationalCheck.setSelected(rational);
        rationalCheck.setBackground(Largest.BACKGROUND);
        rationalCheck.setForeground(Color.WHITE);

        rationalCheck.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rational = rationalCheck.isSelected();
                redraw();
            }
        });

        c.gridx = 0;
        c.gridy = 11;
        add(inverseCheck, c);
        c.gridx = 2;
        c.gridy = 11;
        add(rationalCheck, c);
    }

    private void addC1Z1(){
        JTextField c1RealField = new JTextField(String.valueOf(c1Real));
        JTextField c1ImaginaryField = new JTextField(String.valueOf(c1Imaginary));
        JTextField z1Field = new JTextField(String.valueOf(z1Power));

        JButton c1RealBtn = new JButton("C1 Real");
        c1RealBtn.addActionListener(new ActionListener() {
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
        });

        JButton c1ImaginaryBtn = new JButton("C1 Imaginary");
        c1ImaginaryBtn.addActionListener(new ActionListener() {
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
        });

        JButton z1Btn = new JButton("Z1 Power");
        z1Btn.addActionListener(new ActionListener() {
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
        });

        c.gridx = 0;
        c.gridy = 2;
        add(c1RealBtn, c);
        c.gridx = 2;
        add(c1RealField, c);
        c.gridx = 0;
        c.gridy = 3;
        add(c1ImaginaryBtn, c);
        c.gridx = 2;
        add(c1ImaginaryField, c);
        c.gridx = 0;
        c.gridy = 4;
        add(z1Btn, c);
        c.gridx = 2;
        add(z1Field, c);
    }

    private void addC2Z2(){
        JTextField c2RealField = new JTextField(String.valueOf(c2Real));
        JTextField c2ImaginaryField = new JTextField(String.valueOf(c2Imaginary));
        JTextField z2Field = new JTextField(String.valueOf(z2Power));

        JButton c2RealBtn = new JButton("C2 Real");
        c2RealBtn.addActionListener(new ActionListener() {
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
        });

        JButton c2ImaginaryBtn = new JButton("C2 Imaginary");
        c2ImaginaryBtn.addActionListener(new ActionListener() {
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
        });

        JButton z2Btn = new JButton("Z2 Power");
        z2Btn.addActionListener(new ActionListener() {
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
        });

        c.gridx = 0;
        c.gridy = 5;
        add(c2RealBtn, c);
        c.gridx = 2;
        add(c2RealField, c);
        c.gridx = 0;
        c.gridy = 6;
        add(c2ImaginaryBtn, c);
        c.gridx = 2;
        add(c2ImaginaryField, c);
        c.gridx = 0;
        c.gridy = 7;
        add(z2Btn, c);
        c.gridx = 2;
        add(z2Field, c);
    }

    private void addC3(){
        JTextField c3RealField = new JTextField(String.valueOf(c3Real));
        JTextField c3ImaginaryField = new JTextField(String.valueOf(c3Imaginary));

        JButton c3RealBtn = new JButton("C3 Real");
        c3RealBtn.addActionListener(new ActionListener() {
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
        });

        JButton c3ImaginaryBtn = new JButton("C3 Imaginary");
        c3ImaginaryBtn.addActionListener(new ActionListener() {
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
        });

        c.gridx = 0;
        c.gridy = 8;
        add(c3RealBtn, c);
        c.gridx = 2;
        add(c3RealField, c);
        c.gridx = 0;
        c.gridy = 9;
        add(c3ImaginaryBtn, c);
        c.gridx = 2;
        add(c3ImaginaryField, c);
    }

    private void addInstruct(){
        JLabel instruct = new JLabel("<html>F(z) = C1*F(z-1)^Z1Power + C2*F(z-1)^Z2Power + C3</html>");
        instruct.setBackground(Largest.BACKGROUND);
        instruct.setForeground(Color.white);
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 3;
        add(instruct, c);
        c.gridwidth = 1;
    }

    private void addInColor(Color c){
        inColorList.add(c);
        initInColorMap();
        inPanel.setImage(getInImage());
        redraw();
        revalidate();
        repaint();
    }

    private void removeInColor(){
        inColorList.remove(inColorList.size() - 1);
        initInColorMap();
        inPanel.setImage(getInImage());
        redraw();
        revalidate();
        repaint();
    }

    private void addOutColor(Color c){
        outColorList.add(c);
        initOutColorMap();
        outPanel.setImage(getOutImage());
        redraw();
        revalidate();
        repaint();
    }

    private void removeOutColor(){
        outColorList.remove(outColorList.size() - 1);
        initOutColorMap();
        outPanel.setImage(getOutImage());
        redraw();
        revalidate();
        repaint();
    }

    private void addWarning(){
        c.gridx = 0;
        c.gridy = 0;
        warning.setBackground(Largest.BACKGROUND);
        warning.setForeground(Largest.BACKGROUND);
        warning.setText("filler");
        this.add(warning, c);
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
        c.gridy = 14;
        c.gridwidth = 3;
        add(outPanel, c);

        JButton adColor = new JButton("Add outColor");
        adColor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color newColor = JColorChooser.showDialog(
                        JuliaPanel.this, "", null);
                if (newColor != null){
                    addOutColor(newColor);
                }
            }
        });

        JButton remColor = new JButton("Remove outColor");
        remColor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!outColorList.isEmpty()){
                    removeOutColor();
                }
            }
        });

        c.gridy = 15;
        c.gridwidth = 1;
        add(adColor, c);

        c.gridx = 2;
        add(remColor, c);
    }

    private Image getOutImage(){
        int height = 25;
        BufferedImage image = new BufferedImage(iterations, height, BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < iterations; x++){
            Color c = new Color(outColorMap[x][0] & 0xFF, outColorMap[x][1] & 0xFF,
                    outColorMap[x][2] & 0xFF, outColorMap[x][3] & 0xFF);
            for(int y = 0; y < height; y++){
                image.setRGB(x, y, c.getRGB());
            }
        }
        return image;
    }

    private Image getInImage(){
        int height = 25;
        BufferedImage image = new BufferedImage(iterations, height, BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < iterations; x++){
            Color c = new Color(inColorMap[x][0] & 0xFF, inColorMap[x][1] & 0xFF,
                    inColorMap[x][2] & 0xFF, inColorMap[x][3] & 0xFF);
            for(int y = 0; y < height; y++){
                image.setRGB(x, y, c.getRGB());
            }
        }
        return image;
    }

    private void setInColor(){
        c.gridx = 0;
        c.gridy = 12;
        c.gridwidth = 3;
        add(inPanel, c);

        JButton adColor = new JButton("Add inColor");
        adColor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color newColor = JColorChooser.showDialog(
                        JuliaPanel.this, "", null);
                if (newColor != null){
                    addInColor(newColor);
                }
            }
        });

        JButton remColor = new JButton("Remove inColor");
        remColor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!outColorList.isEmpty()){
                    removeInColor();
                }
            }
        });

        c.gridy = 13;
        c.gridwidth = 1;
        add(adColor, c);

        c.gridx = 2;
        add(remColor, c);
    }

    @Override
    public void redraw(){
        initOutColorMap();
        initInColorMap();
        LinkedList<double[]> insideNormal = new LinkedList<>();
        LinkedList<double[]> outsideNormal = new LinkedList<>();
        double insideMax = 0.0;
        double[] outMax = new double[iterations+2];
        byte[][][] points = largest.getPointsStamp();
        int xRes = points.length;
        int yRes = points[0].length;
        double xFactor = 4.0/xRes;
        double yFactor = 4.0/yRes;
        int xCenter = xRes/2;
        int yCenter = yRes/2;
        for (int x = 0; x < xRes; x++){
            for(int y = 0 ; y < yRes; y++){
                double areal = (x - xCenter) * xFactor;
                double aim = (y - yCenter) * yFactor;
                double[] it = iterate(areal, aim);
                if ((int)it[0] == iterations){
                    insideNormal.addLast(new double[]{it[1], x, y});
                    insideMax = Math.max(insideMax, it[1]);
                }else{
                    outsideNormal.addLast(new double[]{it[0], it[1], x, y});
                    outMax[(int)it[0]] = Math.max(outMax[(int)it[0]], it[1]);
                    //points[x][y] = getUseColor(it);
                }
            }
        }
        addInside(insideMax, insideNormal, points);
        addOutside(outMax, outsideNormal, points);
        myStamp = new Stamp(xCenter, yCenter, points);
        largest.revalidate();
        largest.repaint();
    }

    private void addOutside(double[] outMax, LinkedList<double[]> outsideNormal, byte[][][] points){
        for (double[] point : outsideNormal){
            int x = (int)point[2];
            int y = (int)point[3];
            if (point[0] >= iterations - 1){
                points[x][y][0] =  inColorMap[iterations-1][0];
                points[x][y][1] =  inColorMap[iterations-1][1];
                points[x][y][2] =  inColorMap[iterations-1][2];
                points[x][y][3] =  inColorMap[iterations-1][3];
            }else{
                if (outMax[(int)point[0]] == 0){
                    System.out.println("problem " + x + " " + y);
                    continue;
                }
                //add in reverse, easier to make cool pictures
                int indexLow = iterations - (int)point[0] - 1;
                //double local = (outMax[(int)point[0]] - point[1])/outMax[(int)point[0]];
                double local = point[1]/outMax[(int)point[0]];
                byte[] color = interpolate(outColorMap, indexLow, local);
                points[x][y] = color;
            }
        }
    }

    private void addInside(double insideMax, LinkedList<double[]> insideNormal, byte[][][] points){
        double step = (insideMax/iterations);
        for (double[] point : insideNormal){
            int x = (int)point[1];
            int y = (int)point[2];
            if (insideMax == 0.0 || point[0] == insideMax){
                points[x][y][0] =  inColorMap[iterations-1][0];
                points[x][y][1] =  inColorMap[iterations-1][1];
                points[x][y][2] =  inColorMap[iterations-1][2];
                points[x][y][3] =  inColorMap[iterations-1][3];
                continue;
            }
            int indexLow = (int)((point[0]/insideMax) * (iterations));
            double local = point[0] - indexLow * step;
            byte[] color = interpolate(inColorMap, indexLow, local/step);
            points[x][y] = color;
        }
    }

    private byte[] interpolate(byte[][] colorMap, int indexLow, double local){
        if (indexLow == colorMap.length - 1){
            byte[] ans = new byte[4];
            ans[0] = colorMap[indexLow][0];
            ans[1] = colorMap[indexLow][1];
            ans[2] = colorMap[indexLow][2];
            ans[3] = colorMap[indexLow][3];
            return ans;
        }

        int indexHigh = indexLow + 1;
        int r0 = colorMap[indexLow][0]&0xFF;
        int g0 = colorMap[indexLow][1]&0xFF;
        int b0 = colorMap[indexLow][2]&0xFF;
        int a0 = colorMap[indexLow][3]&0xFF;

        int r1 = colorMap[indexHigh][0]&0xFF;
        int g1 = colorMap[indexHigh][1]&0xFF;
        int b1 = colorMap[indexHigh][2]&0xFF;
        int a1 = colorMap[indexHigh][3]&0xFF;

        int dr = r1 - r0;
        int dg = g1 - g0;
        int db = b1 - b0;
        int da = a1 - a0;

        byte[] ans = new byte[4];
        ans[0] = (byte) (r0 + local * dr);
        ans[1] = (byte) (g0 + local * dg);
        ans[2] = (byte) (b0 + local * db);
        ans[3] = (byte) (a0 + local * da);

        return ans;
    }

    private byte[] getUseColor(double[] both){
        int it = (int)both[0];
        double distance = both[1];
        if (it > iterations){
            //undefined
            byte red = inColorMap[iterations-1][0];
            byte green = inColorMap[iterations-1][1];
            byte blue = inColorMap[iterations-1][2];
            byte alpha = inColorMap[iterations-1][3];
            return new byte[]{red, green, blue, alpha};
        }
        if (it == iterations){
            //inside, never used
            int index = (int)((distance/16.0)*iterations);
            byte red = inColorMap[index][0];
            byte green = inColorMap[index][1];
            byte blue = inColorMap[index][2];
            byte alpha = inColorMap[index][3];
            return new byte[]{red, green, blue, alpha};
        }
        if (it < 0){
            //inside it count, never used
            it = it * -1;
            byte red = inColorMap[iterations - it][0];
            byte green = inColorMap[iterations - it][1];
            byte blue = inColorMap[iterations - it][2];
            byte alpha = inColorMap[iterations - it][3];
            return new byte[]{red, green, blue, alpha};
        }
        //outside
        //use in reverse order so first one put in list is closest to julia
        byte red = outColorMap[iterations - it - 1][0];
        byte green = outColorMap[iterations - it - 1][1];
        byte blue = outColorMap[iterations - it - 1][2];
        byte alpha = outColorMap[iterations - it - 1][3];

        return new byte[]{red,green,blue,alpha};


//        int red1 = outColorMap[iterations - it][0] & 0xFF;
//        int green1 = outColorMap[iterations - it][1] & 0xFF;
//        int blue1 = outColorMap[iterations - it][2] & 0xFF;
//        int alpha1 = outColorMap[iterations - it][3] & 0xFF;
//
//        byte red = (byte)(((red1 * size) + (red0 * (12.0 - size)))/12.0);
//        byte green = (byte)(((green1 * size) + (green0 * (12.0 - size)))/12.0);
//        byte blue = (byte)(((blue1 * size) + (blue0 * (12.0 - size)))/12.0);
//        byte alpha = (byte)(((alpha1 * size) + (alpha0 * (12.0 - size)))/12.0);
//
//        return new byte[]{red, green, blue, alpha};
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
            //add distace traveled
            double travelD = (((areal - lex.getReal())*(areal - lex.getReal())) +
                    ((aim - lex.getImaginary()) * (aim - lex.getImaginary())));
            return new double[]{(double)it, travelD};
        } else{
            return new double[]{(double)it, lex.distance()};
        }
    }

    private double[] iteratePow(double areal, double aim){
        int it = 0;
        int limit = 4;
        double last = 1000.0;
        double dif = 0.00005;
        while(it < iterations && (areal * areal) + (aim * aim) < limit){
            it++;
//            double[] complex = complexPow(areal, aim, power);
//            areal = complex[0] + real;
//            aim = complex[1] + imaginary;
            //not working right, why not??
//            double distance = (areal * areal) + (aim * aim);
//            if(Math.abs(distance - last)< dif){
//                it = it * -1;
//                break;
//            }
//            last = distance;
        }
        return new double[]{(double)it, (areal * areal) + (aim * aim)};
    }

    private double[] complexPow(double areal, double aim, int pow){
        double[] complex = new double[2];
        double rworking = areal;
        double imworking = aim;
        int i = 1;
        while(i < pow){
            double rtemp = (rworking * areal) - (imworking * aim);
            imworking = (imworking*areal) + (rworking*aim);
            rworking = rtemp;
            i++;
        }
        complex[0] = rworking;
        complex[1] = imworking;
        return complex;
    }

    private void initInColorMap(){
        inColorMap = new byte[iterations][4];
        if (inColorList.size() == 0)
        {
            for (int i = 0 ; i < iterations; i++){
                inColorMap[i][0] = (byte)0;
                inColorMap[i][1] = (byte)0;
                inColorMap[i][2] = (byte)0;
                inColorMap[i][3] = (byte)0;
            }
            return;
        }
        if (inColorList.size() == 1)
        {
            Color c = inColorList.get(0);
            for (int i = 0 ; i < iterations; i++){
                inColorMap[i][0] = (byte)c.getRed();
                inColorMap[i][1] = (byte)c.getGreen();
                inColorMap[i][2] = (byte)c.getBlue();
                inColorMap[i][3] = (byte)c.getAlpha();
            }
            return;
        }
        double colorDelta = 1.0 / (inColorList.size() - 1);
        for (int i=0; i<iterations; i++) {
            double globalRel = (double) i / (iterations - 1);
            int index0 = (int) (globalRel / colorDelta);
            int index1 = Math.min(inColorList.size() - 1, index0 + 1);
            double localRel = (globalRel - index0 * colorDelta) / colorDelta;

            Color c0 = inColorList.get(index0);
            int r0 = c0.getRed();
            int g0 = c0.getGreen();
            int b0 = c0.getBlue();
            int a0 = c0.getAlpha();

            Color c1 = inColorList.get(index1);
            int r1 = c1.getRed();
            int g1 = c1.getGreen();
            int b1 = c1.getBlue();
            int a1 = c1.getAlpha();

            int dr = r1 - r0;
            int dg = g1 - g0;
            int db = b1 - b0;
            int da = a1 - a0;

            inColorMap[i][0] = (byte) (r0 + localRel * dr);
            inColorMap[i][1] = (byte) (g0 + localRel * dg);
            inColorMap[i][2] = (byte) (b0 + localRel * db);
            inColorMap[i][3] = (byte) (a0 + localRel * da);
        }
    }

    private void initOutColorMap() {
        outColorMap = new byte[iterations][4];
        if (outColorList.size() == 0)
        {
            for (int i = 0 ; i < iterations; i++){
                outColorMap[i][0] = (byte)0;
                outColorMap[i][1] = (byte)0;
                outColorMap[i][2] = (byte)0;
                outColorMap[i][3] = (byte)0;
            }
            return;
        }

        if (outColorList.size() == 1)
        {
            Color c = outColorList.get(0);
            for (int i = 0 ; i < iterations; i++){
                outColorMap[i][0] = (byte)c.getRed();
                outColorMap[i][1] = (byte)c.getGreen();
                outColorMap[i][2] = (byte)c.getBlue();
                outColorMap[i][3] = (byte)c.getAlpha();
            }
            return;
        }
        double colorDelta = 1.0 / (outColorList.size() - 1);
        for (int i=0; i<iterations; i++)
        {
            double globalRel = (double)i / (iterations - 1);
            int index0 = (int)(globalRel / colorDelta);
            int index1 = Math.min(outColorList.size()-1, index0 + 1);
//            int index1 = Math.min((int)(globalRel / colorDelta) + 1, colorList.size()-1);
//            int index0 = Math.max(0, index1 - 1);
            double localRel = (globalRel - index0 * colorDelta) / colorDelta;
//            double localRel = colorDelta / (globalRel - index0 * colorDelta);

            Color c0 = outColorList.get(index0);
            int r0 = c0.getRed();
            int g0 = c0.getGreen();
            int b0 = c0.getBlue();
            int a0 = c0.getAlpha();

            Color c1 = outColorList.get(index1);
            int r1 = c1.getRed();
            int g1 = c1.getGreen();
            int b1 = c1.getBlue();
            int a1 = c1.getAlpha();

            int dr = r1-r0;
            int dg = g1-g0;
            int db = b1-b0;
            int da = a1-a0;

            outColorMap[i][0] = (byte)(r0 + localRel * dr);
            outColorMap[i][1] = (byte)(g0 + localRel * dg);
            outColorMap[i][2] = (byte)(b0 + localRel * db);
            outColorMap[i][3] = (byte)(a0 + localRel * da);
        }
    }

    @Override
    public LinkedList<Vertex> getShape() {
        return null;
    }

    @Override
    public Stamp getStamp() {
        return myStamp;
    }

    @Override
    public void click(int x, int y) {

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



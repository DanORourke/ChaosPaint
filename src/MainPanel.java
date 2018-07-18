import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

public class MainPanel extends JPanel implements ShapeTab{
    private final Largest largest;
    private GridBagConstraints c = new GridBagConstraints();
    private JLabel warning = new JLabel();
    private JButton[] shows = new JButton[2];
    private int xRes = 1024;
    private int yRes = 1024;
    private BufferedImage tempImage = new BufferedImage(xRes, yRes, BufferedImage.TYPE_INT_ARGB);
    private BufferedImage tempStampImage = new BufferedImage(xRes, yRes, BufferedImage.TYPE_INT_ARGB);
    private BufferedImage finalImage = new BufferedImage(xRes, yRes, BufferedImage.TYPE_INT_ARGB);
    private BufferedImage finalStampImage = new BufferedImage(xRes, yRes, BufferedImage.TYPE_INT_ARGB);
    private int whichImage = 2; // 0 = temp; 1 = tempStamp; 2 = final; 3 = finalStamp
    private boolean showOverTemp = true;
    private boolean showOverFinal = true;
    private boolean showFinal = true;
    private Stamp stamp = new Stamp();
    private JTextField xOff;
    private JTextField yOff;
    private JTextField rotation;

    MainPanel(Largest largest){
        this.largest = largest;
        setLayout(new GridBagLayout());
        setBackground(Largest.BACKGROUND);
        c.insets = new Insets(1, 1, 1, 1);
        c.fill = GridBagConstraints.BOTH;
        addButtons();
    }

    private void addButtons(){
        c.weightx = 0.4;
        c.weighty = 0.4;
        c.gridwidth = 1;
        c.gridheight = 1;
        removeAll();
        addOffsets();
        addNudges();
        addDimensions();
        addFlip();
        addShear();
        addRotate();
        addHowAdd();
        addOver();
        addStampFinal();
        addStampifyTemp();
        addStampifyFinal();
        addAction();
        addTemp();
        addFinal();
        addFile();
        addResolution();
        addSpacers();
        addWarning();
        setFocus();
    }

    private void setFocus(){
        //System.out.println(whichPoints);
        if(whichImage > 1){
            shows[0].setForeground(new Color(10, 150, 10));
            shows[1].setForeground(null);
        }else{
            shows[1].setForeground(new Color(10, 150, 10));
            shows[0].setForeground(null);
        }

        revalidate();
        repaint();
    }

    private void addResolution(){
        JTextField resField = new JTextField(String.valueOf(xRes));
        JButton resBtn = new JButton("Resolution");
        resBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int its = Integer.parseInt(resField.getText());
                    if (its < 1){
                        setWarningText("Enter an int greater than 0");
                    }else{
                        xRes = its;
                        yRes = its;
                        tempImage = new BufferedImage(xRes, yRes, BufferedImage.TYPE_INT_ARGB);
                        finalImage = new BufferedImage(xRes, yRes, BufferedImage.TYPE_INT_ARGB);
                        stamp.setxOffset(xRes/2);
                        stamp.setyOffset(yRes/2);
                        resetSImage();
                        largest.changeRes(getRes());
                        largest.tellSPanelRes(getRes());
                    }
                }catch (NumberFormatException e1){
                    setWarningText("Enter an int greater than 0");
                }
                resField.setText(String.valueOf(xRes));
            }
        });

        c.gridx = 0;
        c.gridy = 20;
        add(resBtn, c);
        c.gridx = 2;
        add(resField, c);
    }

    private void addFile() {
        JButton save = createSave();
        JButton open = createOpen();
        c.gridx = 0;
        c.gridy = 19;
        this.add(save, c);
        c.gridx = 2;
        this.add(open, c);
    }

    private JButton createOpen() {
        JButton open = new JButton("Open");
        open.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                largest.open();
            }
        });
        return open;
    }

    private JButton createSave() {
        JButton save = new JButton("Save");
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                largest.save();
            }
        });
        return save;
    }

    private void addFinal() {
        JButton sFinal = createSFinal();
        JButton cFinal = createCFinal();
        c.gridx = 2;
        c.gridy = 14;
        this.add(sFinal, c);
        c.gridy = 18;
        this.add(cFinal, c);
    }

    private JButton createCFinal() {
        JButton cf = new JButton("Erase Final");
        cf.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                finalImage = new BufferedImage(xRes, yRes, BufferedImage.TYPE_INT_ARGB);
                resetSImage();
                largest.reset();
            }
        });
        return cf;
    }

    private JButton createSFinal() {
        JButton fbutton = new JButton("Show Final");
        shows[0] = fbutton;
        fbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showFinal = true;
                setWhichImage();
                setFocus();
                largest.reset();
            }
        });
        return fbutton;
    }

    private void addTemp() {
        JButton sTemp = createSTemp();
        JButton cTemp = createCTemp();
        c.gridx = 0;
        c.gridy = 14;
        this.add(sTemp, c);
        c.gridy = 18;
        this.add(cTemp, c);
    }

    private JButton createCTemp() {
        JButton ct = new JButton("Erase Temp");
        ct.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tempImage = new BufferedImage(xRes, yRes, BufferedImage.TYPE_INT_ARGB);
                resetSImage();
                largest.reset();
            }
        });
        return ct;
    }

    private JButton createSTemp() {
        JButton showTemp = new JButton("Show Temp");
        shows[1] = showTemp;
        showTemp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showFinal = false;
                setWhichImage();
                setFocus();
                largest.reset();
            }
        });
        return showTemp;
    }

    private void addAction() {
        JButton sToT = createSToT();
        JButton tToF = createTToF();
        JButton fToT = createFToT();
        c.gridx = 0;
        c.gridy = 15;
        this.add(sToT, c);
        c.gridy = 17;
        this.add(fToT, c);
        c.gridx = 2;
        this.add(tToF, c);
    }

    private BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    private JButton createFToT() {
        JButton st = new JButton("Temp=Final");
        st.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tempImage = deepCopy(finalImage);
                resetSImage();
                largest.reset();
            }
        });
        return st;
    }

    private JButton createTToF() {
        JButton st = new JButton("Final=Temp");
        st.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                finalImage = deepCopy(tempImage);
                resetSImage();
                largest.reset();
            }
        });
        return st;
    }

    private JButton createSToT() {
        JButton st = new JButton("Stamp Temp");
        st.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tempImage = tempStampImage;
                resetSImage();
                largest.reset();
            }
        });
        return st;
    }


    private void addStampifyTemp(){

        JButton ts = new JButton("Stampify Temp");
        ts.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                largest.stampify(tempImage);
            }
        });

        c.gridx = 0;
        c.gridy = 16;
        this.add(ts, c);
    }

    private void addStampifyFinal(){

        JButton ts = new JButton("Stampify Final");
        ts.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                largest.stampify(finalImage);
            }
        });

        c.gridx = 2;
        c.gridy = 16;
        this.add(ts, c);
    }

    private void addStampFinal(){
        JButton sf = new JButton("Stamp Final");
        sf.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                finalImage = finalStampImage;
                resetSImage();
                largest.reset();
            }
        });

        c.gridx = 2;
        c.gridy = 15;
        this.add(sf, c);
    }

    private void resetSImage(){
        tempStampImage = deepCopy(tempImage);
        stamp.mainStamp(tempStampImage);
        finalStampImage = deepCopy(finalImage);
        stamp.mainStamp(finalStampImage);
    }

    private void addOver() {
        JCheckBox overTemp = new JCheckBox("Show Over Temp");
        overTemp.setSelected(showOverTemp);
        overTemp.setBackground(Largest.BACKGROUND);
        overTemp.setForeground(Color.WHITE);
        JCheckBox overFinal = new JCheckBox("Show Over Final");
        overFinal.setSelected(showOverFinal);
        overFinal.setBackground(Largest.BACKGROUND);
        overFinal.setForeground(Color.WHITE);

        overTemp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showOverTemp = overTemp.isSelected();
                setWhichImage();
                largest.reset();
            }
        });
        overFinal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showOverFinal = overFinal.isSelected();
                setWhichImage();
                largest.reset();
            }
        });

        c.gridx = 0;
        c.gridy = 13;
        this.add(overTemp, c);
        c.gridx = 2;
        this.add(overFinal, c);
    }

    private void setWhichImage(){
        if(showFinal){
            if(showOverFinal){
                whichImage = 3;
            }else{
                whichImage = 2;
            }
        }else{
            if(showOverTemp){
                whichImage = 1;
            }else{
                whichImage = 0;
            }
        }
    }

    private void addFlip() {
        JCheckBox xcheck = new JCheckBox("Flip Horizontal");
        xcheck.setSelected(stamp.isXflip());
        xcheck.setBackground(Largest.BACKGROUND);
        xcheck.setForeground(Color.WHITE);
        JCheckBox ycheck = new JCheckBox("Flip Vertical");
        ycheck.setSelected(stamp.isYflip());
        ycheck.setBackground(Largest.BACKGROUND);
        ycheck.setForeground(Color.WHITE);

        xcheck.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stamp.setXflip(xcheck.isSelected());
                resetSImage();
                largest.reset();
            }
        });
        ycheck.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stamp.setYflip(ycheck.isSelected());
                resetSImage();
                largest.reset();
            }
        });

        c.gridx = 0;
        c.gridy = 11;
        this.add(xcheck, c);
        c.gridx = 2;
        this.add(ycheck, c);
    }

    private void addHowAdd() {
        JTextField howField = new JTextField(String.valueOf(stamp.getHowAdd()));
        JButton how = new JButton("How to Stamp");
        how.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    double h = Double.parseDouble(howField.getText());
                    stamp.setHowAdd(h);
                    resetSImage();
                    largest.reset();
                }catch (NumberFormatException e1){
                    setWarningText("0 <= double <= 1");
                }
                howField.setText(String.valueOf(stamp.getHowAdd()));
            }
        });

        c.gridx = 0;
        c.gridy = 10;
        this.add(how, c);
        c.gridx = 2;
        this.add(howField, c);
    }

    private void addRotate() {
        rotation = new JTextField(String.valueOf(stamp.getRotationDegree()));
        JButton theta = new JButton("Rotate");
        theta.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int t = Integer.parseInt(rotation.getText())%360;
                    stamp.setRotationDegree(t);
                    resetSImage();
                    largest.reset();
                }catch (NumberFormatException e1){
                    setWarningText("Input an integer");
                }
                rotation.setText(String.valueOf(stamp.getRotationDegree()));
            }
        });

        c.gridx = 0;
        c.gridy = 9;
        this.add(theta, c);
        c.gridx = 2;
        this.add(rotation, c);
    }

    private void addShear() {
        JTextField xsField = new JTextField(String.valueOf(stamp.getXshear()));
        JButton xs = createxShear(xsField);
        JTextField ysField = new JTextField(String.valueOf(stamp.getYshear()));
        JButton ys = createyShear(ysField);

        c.gridx = 0;
        c.gridy = 7;
        this.add(xs, c);
        c.gridy = 8;
        this.add(ys, c);
        c.gridx = 2;
        c.gridy = 7;
        this.add(xsField, c);
        c.gridy = 8;
        this.add(ysField, c);
    }

    private JButton createyShear(JTextField ysField) {
        JButton ys = new JButton("Shear Y");
        ys.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Double s = Double.parseDouble(ysField.getText());
                    stamp.setYshear(s);
                    resetSImage();
                    largest.reset();
                }catch (NumberFormatException e1){
                    setWarningText("Input a double");
                }
                ysField.setText(String.valueOf(stamp.getYshear()));
            }
        });
        return ys;
    }

    private JButton createxShear(JTextField xsField) {
        JButton xs = new JButton("Shear X");
        xs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Double s = Double.parseDouble(xsField.getText());
                    stamp.setXshear(s);
                    resetSImage();
                    largest.reset();
                }catch (NumberFormatException e1){
                    setWarningText("Input a double");
                }
                xsField.setText(String.valueOf(stamp.getXshear()));
            }
        });
        return xs;
    }

    private void addDimensions() {
        JTextField widthField = new JTextField(String.valueOf(stamp.getWidth()));
        JButton width = createWidthB(widthField);
        JTextField heightField = new JTextField(String.valueOf(stamp.getHeight()));
        JButton height = createHeightB(heightField);

        c.gridx = 0;
        c.gridy = 5;
        this.add(width, c);
        c.gridy = 6;
        this.add(height, c);
        c.gridx = 2;
        c.gridy = 5;
        this.add(widthField, c);
        c.gridy = 6;
        this.add(heightField, c);
    }

    private JButton createHeightB(JTextField heightField) {
        JButton height = new JButton("Set Height");
        height.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int h = Integer.parseInt(heightField.getText());
                    stamp.setHeight(h);
                    resetSImage();
                    largest.reset();
                }catch (NumberFormatException e1){
                    setWarningText("Input an integer");
                }
                heightField.setText(String.valueOf(stamp.getHeight()));
            }
        });
        return height;
    }

    private JButton createWidthB(JTextField widthField) {
        JButton width = new JButton("Set Width");
        width.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int w = Integer.parseInt(widthField.getText());
                    stamp.setWidth(w);
                    resetSImage();
                    largest.reset();
                }catch (NumberFormatException e1){
                    setWarningText("Input an integer");
                }
                widthField.setText(String.valueOf(stamp.getWidth()));
            }
        });
        return width;
    }

    private void addWarning(){
        c.gridx = 0;
        c.gridy = 0;
        warning.setBackground(Largest.BACKGROUND);
        warning.setForeground(Largest.BACKGROUND);
        warning.setText("filler");
        this.add(warning, c);
    }

    private void addNudges(){
        JButton nup = createnup();
        JButton ndown = createndown();
        JButton nleft = createnleft();
        JButton nright = createnright();

        c.gridx = 0;
        c.gridy = 3;
        this.add(nup, c);
        c.gridy = 4;
        this.add(ndown, c);
        c.gridx = 2;
        c.gridy = 3;
        this.add(nleft, c);
        c.gridy = 4;
        this.add(nright, c);
    }

    private JButton createnup(){
        JButton b = new JButton("Nudge Up");
        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stamp.setyOffset(stamp.getyOffset() - 5);
                resetSImage();
                yOff.setText(String.valueOf(stamp.getyOffset()));
                largest.reset();
            }
        });
        return b;
    }

    private JButton createndown(){
        JButton b = new JButton("Nudge Down");
        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stamp.setyOffset(stamp.getyOffset() + 5);
                resetSImage();
                yOff.setText(String.valueOf(stamp.getyOffset()));
                largest.reset();

            }
        });
        return b;
    }

    private JButton createnleft(){
        JButton b = new JButton("Nudge Left");
        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stamp.setxOffset(stamp.getxOffset() - 5);
                resetSImage();
                xOff.setText(String.valueOf(stamp.getxOffset()));
                largest.reset();

            }
        });
        return b;
    }

    private JButton createnright(){
        JButton b = new JButton("Nudge Right");
        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stamp.setxOffset(stamp.getxOffset() + 5);
                resetSImage();
                xOff.setText(String.valueOf(stamp.getxOffset()));
                largest.reset();

            }
        });
        return b;
    }


    private void addOffsets(){
        xOff = new JTextField(String.valueOf(stamp.getxOffset()));
        JButton xOffB = createxOffB();
        yOff = new JTextField(String.valueOf(stamp.getyOffset()));
        JButton yOffB = createyOffB();

        c.gridx = 0;
        c.gridy = 1;
        this.add(xOffB, c);
        c.gridy = 2;
        this.add(yOffB, c);
        c.gridx = 2;
        c.gridy = 1;
        this.add(xOff, c);
        c.gridy = 2;
        this.add(yOff, c);
    }

    private JButton createyOffB() {
        JButton yOff = new JButton("Set Y Offset");
        yOff.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int yo = Integer.parseInt(yOff.getText());
                    stamp.setyOffset(yo);
                    resetSImage();
                    largest.reset();
                }catch (NumberFormatException e1){
                    setWarningText("Input an integer");
                }
                yOff.setText(String.valueOf(stamp.getyOffset()));
            }
        });
        return yOff;
    }

    private JButton createxOffB() {
        JButton xOff = new JButton("Set X Offset");
        xOff.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int xo = Integer.parseInt(xOff.getText());
                    stamp.setxOffset(xo);
                    resetSImage();
                    largest.reset();
                }catch (NumberFormatException e1){
                    setWarningText("Input an integer");
                }
                xOff.setText(String.valueOf(stamp.getxOffset()));
            }
        });
        return xOff;
    }



    private void addSpacers() {
        JLabel x1 = new JLabel();
        x1.setBackground(Largest.BACKGROUND);
        c.gridx = 1;
        c.gridy = 1;
        c.gridheight = 26;
        c.weightx = 0.2;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        this.add(x1, c);

        JLabel y12 = new JLabel();
        y12.setBackground(Largest.BACKGROUND);
        c.gridx = 0;
        c.gridy = 12;
        c.gridwidth = 3;
        c.gridheight = 1;

        this.add(y12, c);

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

    void stampify(int[][] data){
        stamp = new Stamp(data);
        stamp.setxOffset(xRes/2);
        stamp.setyOffset(yRes/2);
        resetSImage();
    }

    @Override
    public void shiftDrag(int ox, int oy, int x, int y) {
        int delta = 5;
        if (oy > stamp.getyOffset()){
            if (x > ox){
                stamp.setRotationDegree(stamp.getRotationDegree() - delta);
            }else{
                stamp.setRotationDegree(stamp.getRotationDegree() + delta);
            }
        }else{
            if (x > ox){
                stamp.setRotationDegree(stamp.getRotationDegree() + delta);
            }else{
                stamp.setRotationDegree(stamp.getRotationDegree() - delta);
            }
        }
        rotation.setText(String.valueOf(stamp.getRotationDegree()));
        resetSImage();
    }

    @Override
    public void click(int x, int y) {
        stamp.setxOffset(x);
        stamp.setyOffset(y);
        xOff.setText(String.valueOf(stamp.getxOffset()));
        yOff.setText(String.valueOf(stamp.getyOffset()));
        resetSImage();
    }

    @Override
    public void reset(){
        addButtons();
        resetSImage();
        setWhichImage();
        largest.reset();
    }

    @Override
    public BufferedImage getActiveImage(){
        if (whichImage == 0){
            return tempImage;
        }else if (whichImage == 1){
            return tempStampImage;
        }else if (whichImage == 2){
            return finalImage;
        }else{
            return finalStampImage;
        }
    }

    @Override
    public int[] getRes(){
        return new int[]{xRes, yRes};
    }
}

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.ArrayList;

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
    private int left = 0;
    private int right = 0;
    private boolean cycling = false;
    private ArrayList<Action> actionList = new ArrayList<>();

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
        left = 0;
        right = 0;
        removeAll();
        addWarning();
        addOffsets();
        addNudges();
        addDimensions();
        addShear();
        addRotate();
        addRNudges();
        addHowAdd();
        addFlip();
        addFinalize();
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
        setFocus();
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
        Action ra  = new AbstractAction() {
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
        };
        resField.addActionListener(ra);
        resBtn.addActionListener(ra);
        //actionList.add(ra);

        c.gridx = 0;
        c.gridy = 22;
        add(resBtn, c);
        c.gridx = 2;
        add(resField, c);
    }

    private void addFile() {
        JButton save = createSave();
        JButton open = createOpen();
        c.gridx = 0;
        c.gridy = 21;
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
        c.gridy = 16;
        this.add(sFinal, c);
        c.gridy = 20;
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
        c.gridy = 16;
        this.add(sTemp, c);
        c.gridy = 20;
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
        c.gridy = 17;
        this.add(sToT, c);
        c.gridy = 19;
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
        c.gridy = 18;
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
        c.gridy = 18;
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
        c.gridy = 17;
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
        c.gridy = 15;
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

    private void addFinalize(){
        JButton stampify = new JButton("Stampify Stamp");
        addLeft(stampify);
        stampify.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BufferedImage bi =  new BufferedImage(xRes, yRes, BufferedImage.TYPE_INT_ARGB);
                stamp.mainStamp(bi);
                largest.stampify(bi);
                addButtons();
                revalidate();
                repaint();
            }
        });

        JButton redraw = new JButton("Redraw Stamp");
        addRight(redraw);
        redraw.addActionListener(new ActionListener() {
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
                ifNotCycling();
            }
        });
    }

    private void addFlip() {
        JCheckBox xcheck = new JCheckBox("Flip Horizontal");
        addLeft(xcheck);
        xcheck.setSelected(stamp.isXflip());
        xcheck.setBackground(Largest.BACKGROUND);
        xcheck.setForeground(Color.WHITE);
        JCheckBox ycheck = new JCheckBox("Flip Vertical");
        addRight(ycheck);
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
    }

    private void addHowAdd() {
        JTextField howField = new JTextField(String.valueOf(stamp.getHowAdd()));
        addRight(howField);
        JButton how = new JButton("How to Stamp");
        addLeft(how);
        Action ha = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    double h = Double.parseDouble(howField.getText());
                    stamp.setHowAdd(h);
                    ifNotCycling();
                }catch (NumberFormatException e1){
                    setWarningText("0 <= double <= 1");
                }
                howField.setText(String.valueOf(stamp.getHowAdd()));
            }
        };
        howField.addActionListener(ha);
        how.addActionListener(ha);
        actionList.add(ha);
    }

    private void addRNudges(){
        JButton cb = new JButton("Nudge Clock");
        addLeft(cb);
        cb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stamp.setRotationDegree(stamp.getRotationDegree() + 2);
                rotation.setText(String.valueOf(stamp.getRotationDegree()));
                ifNotCycling();
            }
        });

        JButton ccb = new JButton("Nudge Counter");
        addRight(ccb);
        ccb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stamp.setRotationDegree(stamp.getRotationDegree() - 2);
                rotation.setText(String.valueOf(stamp.getRotationDegree()));
                ifNotCycling();
            }
        });
    }

    private void addRotate() {
        rotation = new JTextField(String.valueOf(stamp.getRotationDegree()));
        addRight(rotation);
        JButton theta = new JButton("Rotate");
        addLeft(theta);
        Action ra = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int t = Integer.parseInt(rotation.getText())%360;
                    stamp.setRotationDegree(t);
                    ifNotCycling();
                }catch (NumberFormatException e1){
                    setWarningText("Input an integer");
                }
                rotation.setText(String.valueOf(stamp.getRotationDegree()));
            }
        };
        rotation.addActionListener(ra);
        theta.addActionListener(ra);
        actionList.add(ra);
    }

    private void addShear() {
        JTextField xsField = new JTextField(String.valueOf(stamp.getXshear()));
        addRight(xsField);
        JButton xs = new JButton("Shear X");
        addLeft(xs);
        Action xa = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Double s = Double.parseDouble(xsField.getText());
                    stamp.setXshear(s);
                    ifNotCycling();
                }catch (NumberFormatException e1){
                    setWarningText("Input a double");
                }
                xsField.setText(String.valueOf(stamp.getXshear()));
            }
        };
        xsField.addActionListener(xa);
        xs.addActionListener(xa);
        actionList.add(xa);

        JTextField ysField = new JTextField(String.valueOf(stamp.getYshear()));
        addRight(ysField);
        JButton ys = new JButton("Shear Y");
        addLeft(ys);
        Action ya = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Double s = Double.parseDouble(ysField.getText());
                    stamp.setYshear(s);
                    ifNotCycling();
                }catch (NumberFormatException e1){
                    setWarningText("Input a double");
                }
                ysField.setText(String.valueOf(stamp.getYshear()));
            }
        };
        ysField.addActionListener(ya);
        ys.addActionListener(ya);
        actionList.add(ya);
    }

    private JButton createyShear(JTextField ysField) {
        JButton ys = new JButton("Shear Y");
        ys.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Double s = Double.parseDouble(ysField.getText());
                    stamp.setYshear(s);
                    ifNotCycling();
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
                    ifNotCycling();
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
        addRight(widthField);
        JButton wb = new JButton("Set Width");
        addLeft(wb);
        Action wa = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int w = Integer.parseInt(widthField.getText());
                    stamp.setWidth(w);
                    ifNotCycling();
                }catch (NumberFormatException e1){
                    setWarningText("Input an integer");
                }
                widthField.setText(String.valueOf(stamp.getWidth()));
            }
        };
        widthField.addActionListener(wa);
        wb.addActionListener(wa);
        actionList.add(wa);

        JTextField heightField = new JTextField(String.valueOf(stamp.getHeight()));
        addRight(heightField);
        JButton hb = new JButton("Set Height");
        addLeft(hb);
        Action ha = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int h = Integer.parseInt(heightField.getText());
                    stamp.setHeight(h);
                    ifNotCycling();
                }catch (NumberFormatException e1){
                    setWarningText("Input an integer");
                }
                heightField.setText(String.valueOf(stamp.getHeight()));
            }
        };
        heightField.addActionListener(ha);
        hb.addActionListener(ha);
        actionList.add(ha);
    }

    private void addWarning(){
        c.gridx = 0;
        c.gridy = left++;
        right++;
        c.gridwidth = 3;
        warning.setBackground(Largest.BACKGROUND);
        warning.setForeground(Largest.BACKGROUND);
        warning.setText("filler");
        add(warning, c);
        c.gridwidth = 1;
    }

    private void addNudges(){
        JButton nup = createnup();
        addLeft(nup);
        JButton ndown = createndown();
        addLeft(ndown);
        JButton nleft = createnleft();
        addRight(nleft);
        JButton nright = createnright();
        addRight(nright);
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
        addRight(xOff);
        JButton xOffB = new JButton("Set X Offset");
        addLeft(xOffB);
        Action xa = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int xo = Integer.parseInt(xOff.getText());
                    stamp.setxOffset(xo);
                    ifNotCycling();
                }catch (NumberFormatException e1){
                    setWarningText("Input an integer");
                }
                xOff.setText(String.valueOf(stamp.getxOffset()));
            }
        };
        xOff.addActionListener(xa);
        xOffB.addActionListener(xa);
        actionList.add(xa);

        yOff = new JTextField(String.valueOf(stamp.getyOffset()));
        addRight(yOff);
        JButton yOffB = new JButton("Set Y Offset");
        addLeft(yOffB);
        Action ya = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int yo = Integer.parseInt(yOff.getText());
                    stamp.setyOffset(yo);
                    ifNotCycling();
                }catch (NumberFormatException e1){
                    setWarningText("Input an integer");
                }
                yOff.setText(String.valueOf(stamp.getyOffset()));
            }
        };
        yOff.addActionListener(ya);
        yOffB.addActionListener(ya);
        actionList.add(ya);
    }

    private void ifNotCycling(){
        if(!cycling){
            resetSImage();
            largest.reset();
        }
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


    private void addSpacers() {
        JLabel x1 = new JLabel();
        x1.setBackground(Largest.BACKGROUND);
        c.gridx = 1;
        c.gridy = 1;
        c.gridheight = 26;
        c.weightx = 0.2;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        add(x1, c);

        JLabel y12 = new JLabel();
        y12.setBackground(Largest.BACKGROUND);
        c.gridx = 0;
        c.gridy = 13;
        c.gridwidth = 3;
        c.gridheight = 1;

        add(y12, c);
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

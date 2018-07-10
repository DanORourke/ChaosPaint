import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.LinkedList;

public class MainPanel extends JPanel implements ShapeTab{
    private final Largest largest;
    private GridBagConstraints c = new GridBagConstraints();
    private JLabel warning = new JLabel();
    private JButton[] shows = new JButton[3];
    private int xRes = 1024;
    private int yRes = 1024;
    private BufferedImage stampImage = new BufferedImage(xRes, yRes, BufferedImage.TYPE_INT_ARGB);
    private BufferedImage tempImage = new BufferedImage(xRes, yRes, BufferedImage.TYPE_INT_ARGB);
    private BufferedImage finalImage = new BufferedImage(xRes, yRes, BufferedImage.TYPE_INT_ARGB);
    private int whichImage = 2; // 2 = stamp; 1 = temp; 0 = final
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
//        addReps();
//        addDistance();
//        addStartTheta();
//        addDeltaTheta();
//        addSpinRate();
//        addScale();
//        addFlipRep();
        addStampFinal();
        addNewStamp();
        addAction();
        addStamp();
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
        for (int i = 0; i < 3; i++){
            if (i == whichImage){
                shows[i].setForeground(new Color(10, 150, 10));
            }else{
                shows[i].setForeground(null);
            }
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
                        stampImage = new BufferedImage(xRes, yRes, BufferedImage.TYPE_INT_ARGB);
                        tempImage = new BufferedImage(xRes, yRes, BufferedImage.TYPE_INT_ARGB);
                        finalImage = new BufferedImage(xRes, yRes, BufferedImage.TYPE_INT_ARGB);
                        stamp.setxOffset(xRes/2);
                        stamp.setyOffset(yRes/2);
                        stamp.mainStamp(stampImage);
                        largest.changeRes(getRes());
                        largest.tellSPanelRes(getRes());
                        redraw();
                    }
                }catch (NumberFormatException e1){
                    setWarningText("Enter an int greater than 0");
                }
                resField.setText(String.valueOf(xRes));
            }
        });

        c.gridx = 0;
        c.gridy = 26;
        add(resBtn, c);
        c.gridx = 2;
        add(resField, c);
    }

    private void addFile() {
        JButton save = createSave();
        JButton open = createOpen();
        c.gridx = 0;
        c.gridy = 25;
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
        c.gridx = 0;
        c.gridy = 24;
        this.add(sFinal, c);
        c.gridx = 2;
        this.add(cFinal, c);
    }

    private JButton createCFinal() {
        JButton cf = new JButton("Erase Final");
        cf.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                finalImage = new BufferedImage(xRes, yRes, BufferedImage.TYPE_INT_ARGB);
                largest.reset();
            }
        });
        return cf;
    }

    private JButton createSFinal() {
        JButton showFinal = new JButton("Show Final");
        shows[0] = showFinal;
        showFinal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                whichImage = 0;
                setFocus();
                largest.reset();
            }
        });
        return showFinal;
    }

    private void addTemp() {
        JButton sTemp = createSTemp();
        JButton cTemp = createCTemp();
        c.gridx = 0;
        c.gridy = 23;
        this.add(sTemp, c);
        c.gridx = 2;
        this.add(cTemp, c);
    }

    private JButton createCTemp() {
        JButton ct = new JButton("Erase Temp");
        ct.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tempImage = new BufferedImage(xRes, yRes, BufferedImage.TYPE_INT_ARGB);
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
                whichImage = 1;
                setFocus();
                largest.reset();
            }
        });
        return showTemp;
    }

    private void addStamp() {
        JButton sStamp = createSStamp();
//        JButton cStamp = createCStamp();
        c.gridx = 0;
        c.gridy = 22;
        this.add(sStamp, c);
//        c.gridx = 2;
//        this.add(cStamp, c);
    }

//    private JButton createCStamp() {
//        JButton cs = new JButton("Erase Stamp");
//        cs.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                stamp = new Stamp();
//                stampImage = new BufferedImage(xRes, yRes, BufferedImage.TYPE_INT_ARGB);
//                largest.reset();
//            }
//        });
//        return cs;
//    }

    private JButton createSStamp() {
        JButton showStamp = new JButton("Show Stamp");
        shows[2] = showStamp;
        showStamp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                whichImage = 2;
                setFocus();
                largest.reset();
            }
        });
        return showStamp;
    }

    private void addAction() {
        JButton sToT = createSToT();
        JButton tToF = createTToF();
        JButton fToT = createFToT();
        c.gridx = 0;
        c.gridy = 20;
        this.add(sToT, c);
        c.gridx = 2;
        c.gridy = 21;
        this.add(fToT, c);
        c.gridy = 22;
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
                stamp.mainStamp(tempImage);
                largest.reset();
            }
        });
        return st;
    }

    private void addNewStamp(){
//        JButton ns = new JButton("???");
//        ns.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                /// what to do with this
//            }
//        });
//
//        c.gridx = 0;
//        c.gridy = 19;
//        this.add(ns, c);

        JButton ts = new JButton("Stampify Temp");
        ts.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                largest.stampify(tempImage);
            }
        });

        c.gridx = 2;
        c.gridy = 20;
        this.add(ts, c);
    }

    private void addStampFinal(){
        JButton sf = new JButton("Stamp Final");
        sf.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stamp.mainStamp(finalImage);
                largest.reset();
            }
        });

        c.gridx = 0;
        c.gridy = 21;
        this.add(sf, c);
    }

//    private void addFlipRep() {
//        JCheckBox xcheck = new JCheckBox("Flip Horizontal");
//        xcheck.setSelected(largest.getStamp().isXflipRepetition());
//        xcheck.setBackground(Largest.BACKGROUND);
//        xcheck.setForeground(Color.WHITE);
//        JCheckBox ycheck = new JCheckBox("Flip Vertical");
//        ycheck.setSelected(largest.getStamp().isYflipRepetition());
//        ycheck.setBackground(Largest.BACKGROUND);
//        ycheck.setForeground(Color.WHITE);
//
//        xcheck.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                largest.getStamp().setXflipRepetition(xcheck.isSelected());
//                largest.stampWorking();
//            }
//        });
//        ycheck.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                largest.getStamp().setYflipRepetition(ycheck.isSelected());
//                largest.stampWorking();
//            }
//        });
//
//        c.gridx = 0;
//        c.gridy = 17;
//        this.add(xcheck, c);
//        c.gridx = 2;
//        this.add(ycheck, c);
//    }
//
//    private void addScale(){
//        JTextField scaleField = new JTextField(String.valueOf(largest.getStamp().getScale()));
//        JButton scale = new JButton("Scale");
//        scale.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                try {
//                    Double s = Double.parseDouble(scaleField.getText());
//                    if (s > 1.0){
//                        setWarningText("Must be <= 1.0");
//                    }else{
//                        largest.getStamp().setScale(s);
//                        largest.stampWorking();
//                    }
//                }catch (NumberFormatException e1){
//                    setWarningText("Input a double");
//                }
//                scaleField.setText(String.valueOf(largest.getStamp().getScale()));
//            }
//        });
//
//        c.gridx = 0;
//        c.gridy = 16;
//        this.add(scale, c);
//        c.gridx = 2;
//        this.add(scaleField, c);
//    }
//
//    private void addSpinRate(){
//        JTextField spinField = new JTextField(String.valueOf(largest.getStamp().getSpinRate()));
//        JButton spin = new JButton("Change Spin");
//        spin.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                try {
//                    int d = Integer.parseInt(spinField.getText());
//                    largest.getStamp().setSpinRate(d);
//                    largest.stampWorking();
//                }catch (NumberFormatException e1){
//                    setWarningText("Spin each repetition");
//                }
//                spinField.setText(String.valueOf(largest.getStamp().getSpinRate()));
//            }
//        });
//
//        c.gridx = 0;
//        c.gridy = 15;
//        this.add(spin, c);
//        c.gridx = 2;
//        this.add(spinField, c);
//    }
//
//    private void addDeltaTheta(){
//        JTextField deltaField = new JTextField(String.valueOf(largest.getStamp().getDeltaDirection()));
//        JButton delta = new JButton("Change Angle");
//        delta.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                try {
//                    int d = Integer.parseInt(deltaField.getText());
//                    largest.getStamp().setDeltaDirection(d);
//                    largest.stampWorking();
//                }catch (NumberFormatException e1){
//                    setWarningText("Angle change each repetition");
//                }
//                deltaField.setText(String.valueOf(largest.getStamp().getDeltaDirection()));
//            }
//        });
//
//        c.gridx = 0;
//        c.gridy = 14;
//        this.add(delta, c);
//        c.gridx = 2;
//        this.add(deltaField, c);
//    }
//
//    private void addDistance(){
//        JTextField distanceField = new JTextField(String.valueOf(largest.getStamp().getDistance()));
//        JButton distance = new JButton("Distance");
//        distance.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                try {
//                    int d = Integer.parseInt(distanceField.getText());
//                    largest.getStamp().setDistance(d);
//                    largest.stampWorking();
//                }catch (NumberFormatException e1){
//                    setWarningText("How far away");
//                }
//                distanceField.setText(String.valueOf(largest.getStamp().getDistance()));
//            }
//        });
//
//        c.gridx = 0;
//        c.gridy = 12;
//        this.add(distance, c);
//        c.gridx = 2;
//        this.add(distanceField, c);
//    }
//
//    private void addStartTheta(){
//        JTextField startField = new JTextField(String.valueOf(largest.getStamp().getStartingDirection()));
//        JButton start = new JButton("Initial Angle");
//        start.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                try {
//                    int r = Integer.parseInt(startField.getText());
//                    largest.getStamp().setStartingDirection(r);
//                    largest.stampWorking();
//                }catch (NumberFormatException e1){
//                    setWarningText("Direction of change");
//                }
//                startField.setText(String.valueOf(largest.getStamp().getStartingDirection()));
//            }
//        });
//
//        c.gridx = 0;
//        c.gridy = 13;
//        this.add(start, c);
//        c.gridx = 2;
//        this.add(startField, c);
//    }
//
//    private void addReps(){
//        JTextField repField = new JTextField(String.valueOf(largest.getStamp().getReps()));
//        JButton how = new JButton("Repetitions");
//        how.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                try {
//                    int r = Integer.parseInt(repField.getText());
//                    largest.getStamp().setReps(r);
//                    largest.stampWorking();
//                }catch (NumberFormatException e1){
//                    setWarningText("Number of times drawn");
//                }
//                repField.setText(String.valueOf(largest.getStamp().getReps()));
//            }
//        });
//
//        c.gridx = 0;
//        c.gridy = 11;
//        this.add(how, c);
//        c.gridx = 2;
//        this.add(repField, c);
//    }

    private void resetSImage(){
        stampImage = new BufferedImage(xRes, yRes, BufferedImage.TYPE_INT_ARGB);
    }

    private void resetTImage(){
        tempImage = new BufferedImage(xRes, yRes, BufferedImage.TYPE_INT_ARGB);
    }

    private void resetFImage(){
        finalImage = new BufferedImage(xRes, yRes, BufferedImage.TYPE_INT_ARGB);
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
                stamp.mainStamp(stampImage);
                largest.reset();
            }
        });
        ycheck.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stamp.setYflip(ycheck.isSelected());
                resetSImage();
                stamp.mainStamp(stampImage);
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
                    stamp.mainStamp(stampImage);
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
                    int t = Integer.parseInt(rotation.getText());
                    stamp.setRotationDegree(t);
                    resetSImage();
                    stamp.mainStamp(stampImage);
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
                    stamp.mainStamp(stampImage);
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
                    stamp.mainStamp(stampImage);
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
                    stamp.mainStamp(stampImage);
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
                    stamp.mainStamp(stampImage);
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
        this.add(nleft, c);
        c.gridx = 2;
        c.gridy = 3;
        this.add(ndown, c);
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
                stamp.mainStamp(stampImage);
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
                stamp.mainStamp(stampImage);
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
                stamp.mainStamp(stampImage);
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
                stamp.mainStamp(stampImage);
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
                    stamp.mainStamp(stampImage);
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
                    stamp.mainStamp(stampImage);
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

//        JLabel y18 = new JLabel();
//        y18.setBackground(Largest.BACKGROUND);
//        c.gridy = 18;
//        this.add(y18, c);

//        JLabel y27 = new JLabel();
//        y27.setBackground(Largest.BACKGROUND);
//        c.gridy = 27;
//        this.add(y27, c);
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
        stampImage = new BufferedImage(xRes, yRes, BufferedImage.TYPE_INT_ARGB);
        stamp.mainStamp(stampImage);
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
        stamp.mainStamp(stampImage);
    }

    @Override
    public void click(int x, int y) {
        stamp.setxOffset(x);
        stamp.setyOffset(y);
        xOff.setText(String.valueOf(stamp.getxOffset()));
        yOff.setText(String.valueOf(stamp.getyOffset()));
        resetSImage();
        stamp.mainStamp(stampImage);
    }

    @Override
    public void reset(){
        addButtons();
        resetSImage();
        stamp.mainStamp(stampImage);
        largest.reset();
    }

    @Override
    public BufferedImage getActiveImage(){
        if (whichImage == 0){
            return finalImage;
        }else if (whichImage == 1){
            return tempImage;
        }else{
            return stampImage;
        }
    }

    @Override
    public int[] getRes(){
        return new int[]{xRes, yRes};
    }
}

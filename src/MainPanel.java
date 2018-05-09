import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

public class MainPanel extends JPanel implements ShapeTab{
    private final Largest largest;
    private GridBagConstraints c = new GridBagConstraints();
    private JLabel warning = new JLabel();
    private JButton[] shows = new JButton[3];

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
        addDimensions();
        addFlip();
        addShear();
        addRotate();
        addHowAdd();
        addReps();
        addDistance();
        addStartTheta();
        addDeltaTheta();
        addSpinRate();
        addScale();
        addFlipRep();
        addStampFinal();
        addNewStamp();
        addAction();
        addStamp();
        addTemp();
        addFinal();
        addFile();
        addSpacers();
        addWarning();
        setFocus();
    }

    private void setFocus(){
        int whichPoints = largest.getWhichPoints();
        //System.out.println(whichPoints);
        for (int i = 0; i < 3; i++){
            if (i == whichPoints){
                shows[i].setForeground(new Color(10, 150, 10));
            }else{
                shows[i].setForeground(null);
            }
        }
        revalidate();
        repaint();
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
                largest.eraseFinal();
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
                largest.showFinal();
                setFocus();
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
                largest.eraseTemp();
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
                largest.showTemp();
                setFocus();
            }
        });
        return showTemp;
    }

    private void addStamp() {
        JButton sStamp = createSStamp();
        JButton cStamp = createCStamp();
        c.gridx = 0;
        c.gridy = 22;
        this.add(sStamp, c);
        c.gridx = 2;
        this.add(cStamp, c);
    }

    private JButton createCStamp() {
        JButton cs = new JButton("Erase Stamp");
        cs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                largest.eraseStamp();
            }
        });
        return cs;
    }

    private JButton createSStamp() {
        JButton showStamp = new JButton("Show Stamp");
        shows[2] = showStamp;
        showStamp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                largest.showStamp();
                setFocus();
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
        this.add(fToT, c);
        c.gridy = 21;
        this.add(tToF, c);

    }

    private JButton createFToT() {
        JButton st = new JButton("Temp=Final");
        st.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                largest.finalToTemp();
            }
        });
        return st;
    }

    private JButton createTToF() {
        JButton st = new JButton("Final=Temp");
        st.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                largest.tempToFinal();
            }
        });
        return st;
    }

    private JButton createSToT() {
        JButton st = new JButton("Stamp Temp");
        st.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                largest.stampTemp();
            }
        });
        return st;
    }

    private void addNewStamp(){
        JButton ns = new JButton("Stamp=Altered");
        ns.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                largest.workingToStamp();
            }
        });

        c.gridx = 0;
        c.gridy = 19;
        this.add(ns, c);

        JButton ts = new JButton("Stamp=Temp");
        ts.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                largest.tempToStamp();
            }
        });

        c.gridx = 2;
        this.add(ts, c);
    }

    private void addStampFinal(){
        JButton sf = new JButton("Stamp Final");
        sf.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                largest.stampFinal();
            }
        });

        c.gridx = 0;
        c.gridy = 21;
        this.add(sf, c);
    }

    private void addFlipRep() {
        JCheckBox xcheck = new JCheckBox("Flip Horizontal");
        xcheck.setSelected(largest.getStamp().isXflipRepetition());
        xcheck.setBackground(Largest.BACKGROUND);
        xcheck.setForeground(Color.WHITE);
        JCheckBox ycheck = new JCheckBox("Flip Vertical");
        ycheck.setSelected(largest.getStamp().isYflipRepetition());
        ycheck.setBackground(Largest.BACKGROUND);
        ycheck.setForeground(Color.WHITE);

        xcheck.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                largest.getStamp().setXflipRepetition(xcheck.isSelected());
                largest.stampWorking();
            }
        });
        ycheck.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                largest.getStamp().setYflipRepetition(ycheck.isSelected());
                largest.stampWorking();
            }
        });

        c.gridx = 0;
        c.gridy = 17;
        this.add(xcheck, c);
        c.gridx = 2;
        this.add(ycheck, c);
    }

    private void addScale(){
        JTextField scaleField = new JTextField(String.valueOf(largest.getStamp().getScale()));
        JButton scale = new JButton("Scale");
        scale.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Double s = Double.parseDouble(scaleField.getText());
                    if (s > 1.0){
                        setWarningText("Must be <= 1.0");
                    }else{
                        largest.getStamp().setScale(s);
                        largest.stampWorking();
                    }
                }catch (NumberFormatException e1){
                    setWarningText("Input a double");
                }
                scaleField.setText(String.valueOf(largest.getStamp().getScale()));
            }
        });

        c.gridx = 0;
        c.gridy = 16;
        this.add(scale, c);
        c.gridx = 2;
        this.add(scaleField, c);
    }

    private void addSpinRate(){
        JTextField spinField = new JTextField(String.valueOf(largest.getStamp().getSpinRate()));
        JButton spin = new JButton("Change Spin");
        spin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int d = Integer.parseInt(spinField.getText());
                    largest.getStamp().setSpinRate(d);
                    largest.stampWorking();
                }catch (NumberFormatException e1){
                    setWarningText("Spin each repetition");
                }
                spinField.setText(String.valueOf(largest.getStamp().getSpinRate()));
            }
        });

        c.gridx = 0;
        c.gridy = 15;
        this.add(spin, c);
        c.gridx = 2;
        this.add(spinField, c);
    }

    private void addDeltaTheta(){
        JTextField deltaField = new JTextField(String.valueOf(largest.getStamp().getDeltaDirection()));
        JButton delta = new JButton("Change Angle");
        delta.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int d = Integer.parseInt(deltaField.getText());
                    largest.getStamp().setDeltaDirection(d);
                    largest.stampWorking();
                }catch (NumberFormatException e1){
                    setWarningText("Angle change each repetition");
                }
                deltaField.setText(String.valueOf(largest.getStamp().getDeltaDirection()));
            }
        });

        c.gridx = 0;
        c.gridy = 14;
        this.add(delta, c);
        c.gridx = 2;
        this.add(deltaField, c);
    }

    private void addDistance(){
        JTextField distanceField = new JTextField(String.valueOf(largest.getStamp().getDistance()));
        JButton distance = new JButton("Distance");
        distance.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int d = Integer.parseInt(distanceField.getText());
                    largest.getStamp().setDistance(d);
                    largest.stampWorking();
                }catch (NumberFormatException e1){
                    setWarningText("How far away");
                }
                distanceField.setText(String.valueOf(largest.getStamp().getDistance()));
            }
        });

        c.gridx = 0;
        c.gridy = 12;
        this.add(distance, c);
        c.gridx = 2;
        this.add(distanceField, c);
    }

    private void addStartTheta(){
        JTextField startField = new JTextField(String.valueOf(largest.getStamp().getStartingDirection()));
        JButton start = new JButton("Initial Angle");
        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int r = Integer.parseInt(startField.getText());
                    largest.getStamp().setStartingDirection(r);
                    largest.stampWorking();
                }catch (NumberFormatException e1){
                    setWarningText("Direction of change");
                }
                startField.setText(String.valueOf(largest.getStamp().getStartingDirection()));
            }
        });

        c.gridx = 0;
        c.gridy = 13;
        this.add(start, c);
        c.gridx = 2;
        this.add(startField, c);
    }

    private void addReps(){
        JTextField repField = new JTextField(String.valueOf(largest.getStamp().getReps()));
        JButton how = new JButton("Repetitions");
        how.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int r = Integer.parseInt(repField.getText());
                    largest.getStamp().setReps(r);
                    largest.stampWorking();
                }catch (NumberFormatException e1){
                    setWarningText("Number of times drawn");
                }
                repField.setText(String.valueOf(largest.getStamp().getReps()));
            }
        });

        c.gridx = 0;
        c.gridy = 11;
        this.add(how, c);
        c.gridx = 2;
        this.add(repField, c);
    }

    private void addFlip() {
        JCheckBox xcheck = new JCheckBox("Flip Horizontal");
        xcheck.setSelected(largest.getStamp().isXflip());
        xcheck.setBackground(Largest.BACKGROUND);
        xcheck.setForeground(Color.WHITE);
        JCheckBox ycheck = new JCheckBox("Flip Vertical");
        ycheck.setSelected(largest.getStamp().isYflip());
        ycheck.setBackground(Largest.BACKGROUND);
        ycheck.setForeground(Color.WHITE);

        xcheck.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                largest.getStamp().setXflip(xcheck.isSelected());
                largest.stampWorking();
            }
        });
        ycheck.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                largest.getStamp().setYflip(ycheck.isSelected());
                largest.stampWorking();
            }
        });

        c.gridx = 0;
        c.gridy = 9;
        this.add(xcheck, c);
        c.gridx = 2;
        this.add(ycheck, c);
    }

    private void addHowAdd() {
        JTextField howField = new JTextField(String.valueOf(largest.getStamp().getHowAdd()));
        JButton how = new JButton("How to Stamp");
        how.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int h = Integer.parseInt(howField.getText());
                    largest.getStamp().setHowAdd(h);
                    largest.stampWorking();
                }catch (NumberFormatException e1){
                    setWarningText("-1 = under; 0 = blend; 1 = over");
                }
                howField.setText(String.valueOf(largest.getStamp().getHowAdd()));
            }
        });

        c.gridx = 0;
        c.gridy = 8;
        this.add(how, c);
        c.gridx = 2;
        this.add(howField, c);
    }

    private void addRotate() {
        JTextField thetaField = new JTextField(String.valueOf(largest.getStamp().getRotationDegree()));
        JButton theta = new JButton("Rotate");
        theta.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int t = Integer.parseInt(thetaField.getText());
                    largest.getStamp().setRotationDegree(t);
                    largest.stampWorking();
                }catch (NumberFormatException e1){
                    setWarningText("Input an integer");
                }
                thetaField.setText(String.valueOf(largest.getStamp().getRotationDegree()));
            }
        });

        c.gridx = 0;
        c.gridy = 7;
        this.add(theta, c);
        c.gridx = 2;
        this.add(thetaField, c);
    }

    private void addShear() {
        JTextField xsField = new JTextField(String.valueOf(largest.getStamp().getXshear()));
        JButton xs = createxShear(xsField);
        JTextField ysField = new JTextField(String.valueOf(largest.getStamp().getYshear()));
        JButton ys = createyShear(ysField);

        c.gridx = 0;
        c.gridy = 5;
        this.add(xs, c);
        c.gridy = 6;
        this.add(ys, c);
        c.gridx = 2;
        c.gridy = 5;
        this.add(xsField, c);
        c.gridy = 6;
        this.add(ysField, c);
    }

    private JButton createyShear(JTextField ysField) {
        JButton ys = new JButton("Shear Y");
        ys.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Double s = Double.parseDouble(ysField.getText());
                    largest.getStamp().setYshear(s);
                    largest.stampWorking();
                }catch (NumberFormatException e1){
                    setWarningText("Input a double");
                }
                ysField.setText(String.valueOf(largest.getStamp().getYshear()));
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
                    largest.getStamp().setXshear(s);
                    largest.stampWorking();
                }catch (NumberFormatException e1){
                    setWarningText("Input a double");
                }
                xsField.setText(String.valueOf(largest.getStamp().getXshear()));
            }
        });
        return xs;
    }

    private void addDimensions() {
        JTextField widthField = new JTextField(String.valueOf(largest.getStamp().getWidth()));
        JButton width = createWidthB(widthField);
        JTextField heightField = new JTextField(String.valueOf(largest.getStamp().getHeight()));
        JButton height = createHeightB(heightField);

        c.gridx = 0;
        c.gridy = 3;
        this.add(width, c);
        c.gridy = 4;
        this.add(height, c);
        c.gridx = 2;
        c.gridy = 3;
        this.add(widthField, c);
        c.gridy = 4;
        this.add(heightField, c);
    }

    private JButton createHeightB(JTextField heightField) {
        JButton height = new JButton("Set Height");
        height.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int h = Integer.parseInt(heightField.getText());
                    largest.getStamp().setHeight(h);
                    largest.stampWorking();
                }catch (NumberFormatException e1){
                    setWarningText("Input an integer");
                }
                heightField.setText(String.valueOf(largest.getStamp().getHeight()));
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
                    largest.getStamp().setWidth(w);
                    largest.stampWorking();
                }catch (NumberFormatException e1){
                    setWarningText("Input an integer");
                }
                widthField.setText(String.valueOf(largest.getStamp().getWidth()));
            }
        });
        return width;    }

    private void addWarning(){
        c.gridx = 0;
        c.gridy = 0;
        warning.setBackground(Largest.BACKGROUND);
        warning.setForeground(Largest.BACKGROUND);
        warning.setText("filler");
        this.add(warning, c);
    }


    private void addOffsets(){
        JTextField xOffField = new JTextField(String.valueOf(largest.getStamp().getxOffset()));
        JButton xOffB = createxOffB(xOffField);
        JTextField yOffField = new JTextField(String.valueOf(largest.getStamp().getyOffset()));
        JButton yOffB = createyOffB(yOffField);

        c.gridx = 0;
        c.gridy = 1;
        this.add(xOffB, c);
        c.gridy = 2;
        this.add(yOffB, c);
        c.gridx = 2;
        c.gridy = 1;
        this.add(xOffField, c);
        c.gridy = 2;
        this.add(yOffField, c);
    }

    private JButton createyOffB(JTextField yOffField) {
        JButton yOff = new JButton("Set Y Offset");
        yOff.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int yo = Integer.parseInt(yOffField.getText());
                    largest.getStamp().setyOffset(yo);
                    largest.stampWorking();
                }catch (NumberFormatException e1){
                    setWarningText("Input an integer");
                }
                yOffField.setText(String.valueOf(largest.getStamp().getyOffset()));
            }
        });
        return yOff;
    }

    private JButton createxOffB(JTextField xOffField) {
        JButton xOff = new JButton("Set X Offset");
        xOff.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int xo = Integer.parseInt(xOffField.getText());
                    largest.getStamp().setxOffset(xo);
                    largest.stampWorking();
                }catch (NumberFormatException e1){
                    setWarningText("Input an integer");
                }
                xOffField.setText(String.valueOf(largest.getStamp().getxOffset()));
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

        JLabel y10 = new JLabel();
        y10.setBackground(Largest.BACKGROUND);
        c.gridx = 0;
        c.gridy = 10;
        c.gridwidth = 3;
        c.gridheight = 1;

        this.add(y10, c);

        JLabel y18 = new JLabel();
        y18.setBackground(Largest.BACKGROUND);
        c.gridy = 18;
        this.add(y18, c);

        JLabel y26 = new JLabel();
        y26.setBackground(Largest.BACKGROUND);
        c.gridy = 26;
        this.add(y26, c);
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

    @Override
    public LinkedList<Vertex> getShape() {
        return new LinkedList<>();
    }

    @Override
    public void shiftDrag(int ox, int oy, int x, int y) {
        Stamp stamp = largest.getStamp();
        int delta = 3;
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
        reset();
    }

    @Override
    public void click(int x, int y) {
        largest.getStamp().setxOffset(x);
        largest.getStamp().setyOffset(y);
        reset();
    }

    @Override
    public void reset(){
        addButtons();
    }
}

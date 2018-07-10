import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class StampPanel extends JPanel implements ShapeTab{
    private final Largest largest;
    private GridBagConstraints c = new GridBagConstraints();
    private JLabel warning = new JLabel();
    private Stamp stamp = new Stamp();
    int xRes;
    int yRes;
    private BufferedImage image;

    StampPanel(Largest largest){
        super();
        this.largest = largest;
        setLayout(new GridBagLayout());
        setBackground(Largest.BACKGROUND);
        initImage();
        c.insets = new Insets(1, 1, 1, 1);
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.4;
        c.weighty = 0.4;
        c.gridwidth = 1;
        c.gridheight = 1;
        updateFields();
    }

    private void initImage(){
        int[] mainRes = largest.getMainRes();
        xRes = mainRes[0];
        yRes = mainRes[1];
        image = new BufferedImage(xRes, yRes, BufferedImage.TYPE_INT_ARGB);
    }

    void changeRes(int[] res){
        xRes = res[0];
        yRes = res[1];
        image = new BufferedImage(xRes, yRes, BufferedImage.TYPE_INT_ARGB);
        stamp.spanelStamp(image);
    }

    private void updateFields(){
        c.weightx = 0.4;
        c.weighty = 0.4;
        c.gridwidth = 1;
        c.gridheight = 1;
        removeAll();
        addWarning();
        addDimensions();
        addReps();
        addDistance();
        addStartTheta();
        addDeltaTheta();
        addSpinRate();
        addScale();
        addHowAdd();
        addFlipRep();
        addRemoveAlpha();
        addFillAlpha();
        addChangeColor();
        addHowChangeColor();
        addStampify();
        addSpacers();
    }

    @Override
    public void reset(){
        //stamp.setData(largest.getStamp().getData());
        updateFields();
        revalidate();
        repaint();
    }

    private void addRemoveAlpha(){
        JTextField howField = new JTextField(String.valueOf(stamp.getMinAlpha()));
        JButton how = new JButton("Remove Alpha <");
        how.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int h = Integer.parseInt(howField.getText());
                    if (h < 0 || h > 255){
                        setWarningText("0 <= int <= 255");
                    }else{
                        stamp.setMinAlpha(h);
                        afterChange();
                    }
                }catch (NumberFormatException e1){
                    setWarningText("0 <= int <= 255");
                }
                howField.setText(String.valueOf(stamp.getMinAlpha()));
            }
        });

        c.gridx = 0;
        c.gridy = 12;
        this.add(how, c);
        c.gridx = 2;
        this.add(howField, c);
    }

    private void addFillAlpha(){
        JTextField howField = new JTextField(String.valueOf(stamp.getFillAlpha()));
        JButton how = new JButton("Fill Alpha >=");
        how.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int h = Integer.parseInt(howField.getText());
                    if (h < 0 || h > 255){
                        setWarningText("0 <= int <= 255");
                    }else{
                        stamp.setFillAlpha(h);
                        afterChange();
                    }
                }catch (NumberFormatException e1){
                    setWarningText("0 <= int <= 255");
                }
                howField.setText(String.valueOf(stamp.getFillAlpha()));
            }
        });

        c.gridx = 0;
        c.gridy = 13;
        this.add(how, c);
        c.gridx = 2;
        this.add(howField, c);
    }

    private void addChangeColor(){
        JPanel cPanel = new JPanel();
        Color col = new Color(stamp.getDeltaColor());
        cPanel.setBackground(col);
        JButton cButton = new JButton("Change Color");
        cButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color newColor = JColorChooser.showDialog(
                        StampPanel.this, "", null);
                if (newColor != null){
                    int nc = newColor.getRGB();
                    stamp.setDeltaColor(nc);
                    cPanel.setBackground(newColor);
                    afterChange();
                }
            }
        });

        c.gridx = 0;
        c.gridy = 14;
        this.add(cButton, c);
        c.gridx = 2;
        this.add(cPanel, c);
    }

    private void addHowChangeColor(){
        JTextField hccField = new JTextField(String.valueOf(stamp.getHowDeltaColor()));
        JButton hcc = new JButton("Old Color %");
        hcc.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Double s = Double.parseDouble(hccField.getText());
                    if (s > 1.0 || s < 0.0){
                        setWarningText("0.0 <= double <= 1.0");
                    }else{
                        stamp.setHowDeltaColor(s);
                        afterChange();
                    }
                }catch (NumberFormatException e1){
                    setWarningText("0.0 <= double <= 1.0");
                }
                hccField.setText(String.valueOf(stamp.getHowDeltaColor()));
            }
        });

        c.gridx = 0;
        c.gridy = 15;
        this.add(hcc, c);
        c.gridx = 2;
        this.add(hccField, c);
    }

    private void addStampify(){
        JButton ts = new JButton("Stampify");
        ts.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                largest.stampify(image);
                reset();
            }
        });
        c.gridx = 0;
        c.gridy = 16;
        this.add(ts, c);
    }

    private void addDimensions() {
        JTextField widthField = new JTextField(String.valueOf(stamp.getWidth()));
        JButton width = createWidthB(widthField);
        JTextField heightField = new JTextField(String.valueOf(stamp.getHeight()));
        JButton height = createHeightB(heightField);

        c.gridx = 0;
        c.gridy = 1;
        this.add(width, c);
        c.gridy = 2;
        this.add(height, c);
        c.gridx = 2;
        c.gridy = 1;
        this.add(widthField, c);
        c.gridy = 2;
        this.add(heightField, c);
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
                    afterChange();
                }catch (NumberFormatException e1){
                    setWarningText("0 <= double <= 1");
                }
                howField.setText(String.valueOf(stamp.getHowAdd()));
            }
        });

        c.gridx = 0;
        c.gridy = 9;
        this.add(how, c);
        c.gridx = 2;
        this.add(howField, c);
    }

    private JButton createHeightB(JTextField heightField) {
        JButton height = new JButton("Set Height");
        height.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int h = Integer.parseInt(heightField.getText());
                    stamp.setHeight(h);
                    afterChange();
                }catch (NumberFormatException e1){
                    setWarningText("Input an integer");
                }
                heightField.setText(String.valueOf(stamp.getHeight()));
            }
        });
        return height;
    }

    private void afterChange(){
        image = new BufferedImage(xRes, yRes, BufferedImage.TYPE_INT_ARGB);
        stamp.spanelStamp(image);
        largest.reset();
    }

    private JButton createWidthB(JTextField widthField) {
        JButton width = new JButton("Set Width");
        width.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int w = Integer.parseInt(widthField.getText());
                    stamp.setWidth(w);
                    afterChange();
                }catch (NumberFormatException e1){
                    setWarningText("Input an integer");
                }
                widthField.setText(String.valueOf(stamp.getWidth()));
            }
        });
        return width;
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

        JLabel y11 = new JLabel();
        y11.setBackground(Largest.BACKGROUND);
        c.gridx = 0;
        c.gridy = 11;
        c.gridwidth = 3;
        c.gridheight = 1;

        this.add(y11, c);

//        JLabel y18 = new JLabel();
//        y18.setBackground(Largest.BACKGROUND);
//        c.gridy = 10;
//        this.add(y18, c);

//        JLabel y27 = new JLabel();
//        y27.setBackground(Largest.BACKGROUND);
//        c.gridy = 27;
//        this.add(y27, c);
    }

    void stampify(int[][] st){
        stamp = new Stamp(st);
        initImage();
        stamp.spanelStamp(image);
    }

    private void addWarning(){
        c.gridx = 0;
        c.gridy = 0;
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

    private void addFlipRep() {
        JCheckBox xcheck = new JCheckBox("Flip Horizontal");
        xcheck.setSelected(stamp.isXflipRepetition());
        xcheck.setBackground(Largest.BACKGROUND);
        xcheck.setForeground(Color.WHITE);
        JCheckBox ycheck = new JCheckBox("Flip Vertical");
        ycheck.setSelected(stamp.isYflipRepetition());
        ycheck.setBackground(Largest.BACKGROUND);
        ycheck.setForeground(Color.WHITE);

        xcheck.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stamp.setXflipRepetition(xcheck.isSelected());
                afterChange();
            }
        });
        ycheck.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stamp.setYflipRepetition(ycheck.isSelected());
                afterChange();
            }
        });

        c.gridx = 0;
        c.gridy = 10;
        this.add(xcheck, c);
        c.gridx = 2;
        this.add(ycheck, c);
    }

    private void addScale(){
        JTextField scaleField = new JTextField(String.valueOf(stamp.getScale()));
        JButton scale = new JButton("Scale");
        scale.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Double s = Double.parseDouble(scaleField.getText());
                    if (s > 1.0){
                        setWarningText("Must be <= 1.0");
                    }else{
                        stamp.setScale(s);
                        afterChange();
                    }
                }catch (NumberFormatException e1){
                    setWarningText("Input a double");
                }
                scaleField.setText(String.valueOf(stamp.getScale()));
            }
        });

        c.gridx = 0;
        c.gridy = 8;
        this.add(scale, c);
        c.gridx = 2;
        this.add(scaleField, c);
    }

    private void addSpinRate(){
        JTextField spinField = new JTextField(String.valueOf(stamp.getSpinRate()));
        JButton spin = new JButton("Change Spin");
        spin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int d = Integer.parseInt(spinField.getText());
                    stamp.setSpinRate(d);
                    afterChange();
                }catch (NumberFormatException e1){
                    setWarningText("Spin each repetition");
                }
                spinField.setText(String.valueOf(stamp.getSpinRate()));
            }
        });

        c.gridx = 0;
        c.gridy = 7;
        this.add(spin, c);
        c.gridx = 2;
        this.add(spinField, c);
    }

    private void addDeltaTheta(){
        JTextField deltaField = new JTextField(String.valueOf(stamp.getDeltaDirection()));
        JButton delta = new JButton("Change Angle");
        delta.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int d = Integer.parseInt(deltaField.getText());
                    stamp.setDeltaDirection(d);
                    afterChange();
                }catch (NumberFormatException e1){
                    setWarningText("Angle change each repetition");
                }
                deltaField.setText(String.valueOf(stamp.getDeltaDirection()));
            }
        });

        c.gridx = 0;
        c.gridy = 6;
        this.add(delta, c);
        c.gridx = 2;
        this.add(deltaField, c);
    }

    private void addDistance(){
        JTextField distanceField = new JTextField(String.valueOf(stamp.getDistance()));
        JButton distance = new JButton("Distance");
        distance.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int d = Integer.parseInt(distanceField.getText());
                    stamp.setDistance(d);
                    afterChange();
                }catch (NumberFormatException e1){
                    setWarningText("How far away");
                }
                distanceField.setText(String.valueOf(stamp.getDistance()));
            }
        });

        c.gridx = 0;
        c.gridy = 4;
        this.add(distance, c);
        c.gridx = 2;
        this.add(distanceField, c);
    }

    private void addStartTheta(){
        JTextField startField = new JTextField(String.valueOf(stamp.getStartingDirection()));
        JButton start = new JButton("Initial Angle");
        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int r = Integer.parseInt(startField.getText());
                    stamp.setStartingDirection(r);
                    afterChange();
                }catch (NumberFormatException e1){
                    setWarningText("Direction of change");
                }
                startField.setText(String.valueOf(stamp.getStartingDirection()));
            }
        });

        c.gridx = 0;
        c.gridy = 5;
        this.add(start, c);
        c.gridx = 2;
        this.add(startField, c);
    }

    private void addReps(){
        JTextField repField = new JTextField(String.valueOf(stamp.getReps()));
        JButton how = new JButton("Repetitions");
        how.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int r = Integer.parseInt(repField.getText());
                    stamp.setReps(r);
                    afterChange();
                }catch (NumberFormatException e1){
                    setWarningText("Number of times drawn");
                }
                repField.setText(String.valueOf(stamp.getReps()));
            }
        });

        c.gridx = 0;
        c.gridy = 3;
        this.add(how, c);
        c.gridx = 2;
        this.add(repField, c);
    }

    @Override
    public BufferedImage getActiveImage(){
        return image;
    }

    @Override
    public int[] getRes(){
        return new int[]{xRes, yRes};
    }

}

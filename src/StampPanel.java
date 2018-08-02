import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class StampPanel extends JPanel implements ShapeTab{
    private final Largest largest;
    private GridBagConstraints c = new GridBagConstraints();
    private JLabel warning = new JLabel();
    private Stamp stamp = new Stamp();
    private int xRes;
    private int yRes;
    private BufferedImage image;
    private int left = 0;
    private int right = 0;
    private boolean cycling = false;
    private ArrayList<Action> actionList = new ArrayList<>();

    StampPanel(Largest largest){
        super();
        this.largest = largest;
        setLayout(new GridBagLayout());
        setBackground(Largest.BACKGROUND);
        initImage();
        c.insets = new Insets(1, 1, 1, 1);
        c.fill = GridBagConstraints.BOTH;
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
        addStartTheta();
        addDistance1();
        addDeltaTheta1();
        addSpinRate1();
        addScale1();
        addDistance2();
        addDeltaTheta2();
        addSpinRate2();
        addScale2();
        addUse();
        addHowAdd();
        addRemoveAlpha();
        addFillAlpha();
        addChangeColor();
        addHowChangeColor();
        addStampify();
        addSpacers();
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

    @Override
    public void reset(){
        updateFields();
        revalidate();
        repaint();
    }

    private void addRemoveAlpha(){
        JTextField howField = new JTextField(String.valueOf(stamp.getMinAlpha()));
        addRight(howField);
        JButton how = new JButton("Remove Alpha <=");
        addLeft(how);
        Action ha = new AbstractAction() {
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
        };
        howField.addActionListener(ha);
        how.addActionListener(ha);
        actionList.add(ha);
    }

    private void addFillAlpha(){
        JTextField howField = new JTextField(String.valueOf(stamp.getFillAlpha()));
        addRight(howField);
        JButton how = new JButton("Fill Alpha >=");
        addLeft(how);
        Action ha = new AbstractAction() {
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
        };
        howField.addActionListener(ha);
        how.addActionListener(ha);
        actionList.add(ha);
    }

    private void addChangeColor(){
        JButton colB = new JButton();
        addRight(colB);
        Color col = new Color(stamp.getDeltaColor());
        colB.setBackground(col);
        JButton cButton = new JButton("Change Color");
        addLeft(cButton);
        Action ca = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color newColor = JColorChooser.showDialog(
                        StampPanel.this, "", null);
                if (newColor != null){
                    int nc = newColor.getRGB();
                    stamp.setDeltaColor(nc);
                    colB.setBackground(newColor);
                    afterChange();
                }
            }
        };
        colB.addActionListener(ca);
        cButton.addActionListener(ca);
    }

    private void addHowChangeColor(){
        JTextField hccField = new JTextField(String.valueOf(stamp.getHowDeltaColor()));
        addRight(hccField);
        JButton hcc = new JButton("Old Color %");
        addLeft(hcc);
        Action ha = new AbstractAction() {
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
        };
        hccField.addActionListener(ha);
        hcc.addActionListener(ha);
        actionList.add(ha);
    }

    private void addStampify(){
        JButton ts = new JButton("Stampify");
        addLeft(ts);
        ts.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                largest.stampify(image);
                reset();
            }
        });

        JButton db = new JButton("Redraw");
        addRight(db);
        db.addActionListener(new ActionListener() {
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
                afterChange();
            }
        });
    }

    private void addDimensions() {
        JTextField widthField = new JTextField(String.valueOf(stamp.getWidth()));
        addRight(widthField);
        JButton width = new JButton("Set Width");
        addLeft(width);
        Action wa = new AbstractAction() {
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
        };
        widthField.addActionListener(wa);
        width.addActionListener(wa);
        actionList.add(wa);

        JTextField heightField = new JTextField(String.valueOf(stamp.getHeight()));
        addRight(heightField);
        JButton height = new JButton("Set Height");
        addLeft(height);
        Action ha = new AbstractAction() {
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
        };
        heightField.addActionListener(ha);
        height.addActionListener(ha);
        actionList.add(ha);
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
                    afterChange();
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

    private void afterChange(){
        if(!cycling){
            image = new BufferedImage(xRes, yRes, BufferedImage.TYPE_INT_ARGB);
            stamp.spanelStamp(image);
            largest.reset();
        }
    }

    private void addSpacers() {
        JLabel x1 = new JLabel();
        x1.setBackground(Largest.BACKGROUND);
        c.gridx = 1;
        c.gridy = 1;
        c.gridheight = 20;
        c.weightx = 0.2;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        this.add(x1, c);

//        JLabel y11 = new JLabel();
//        y11.setBackground(Largest.BACKGROUND);
//        c.gridx = 0;
//        c.gridy = 11;
//        c.gridwidth = 3;
//        c.gridheight = 1;
//
//        this.add(y11, c);
    }

    void stampify(int[][] st){
        stamp = new Stamp(st);
        initImage();
        stamp.spanelStamp(image);
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

    private void addUse() {
        JCheckBox xcheck = new JCheckBox("Use 1");
        xcheck.setSelected(stamp.isUse1());
        xcheck.setBackground(Largest.BACKGROUND);
        xcheck.setForeground(Color.WHITE);
        JCheckBox ycheck = new JCheckBox("Use 2");
        ycheck.setSelected(stamp.isUse2());
        ycheck.setBackground(Largest.BACKGROUND);
        ycheck.setForeground(Color.WHITE);
        JCheckBox leafCheck = new JCheckBox("Only Leaf");
        leafCheck.setSelected(stamp.isOnlyLeaf());
        leafCheck.setBackground(Largest.BACKGROUND);
        leafCheck.setForeground(Color.WHITE);

        xcheck.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stamp.setUse1(xcheck.isSelected());
                afterChange();
            }
        });
        ycheck.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stamp.setUse2(ycheck.isSelected());
                afterChange();
            }
        });
        leafCheck.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stamp.setOnlyLeaf(leafCheck.isSelected());
                afterChange();
            }
        });

        addLeft(xcheck);
        addLeft(leafCheck);
        addRight(ycheck);
        right++;
    }

    private void addScale1(){
        JTextField scaleField = new JTextField(String.valueOf(stamp.getScale1()));
        addRight(scaleField);
        JButton scale = new JButton("Scale 1");
        addLeft(scale);
        Action sa = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Double s = Double.parseDouble(scaleField.getText());
                    if (s > 1.0){
                        setWarningText("Must be <= 1.0");
                    }else{
                        stamp.setScale1(s);
                        afterChange();
                    }
                }catch (NumberFormatException e1){
                    setWarningText("Input a double");
                }
                scaleField.setText(String.valueOf(stamp.getScale1()));
            }
        };
        scaleField.addActionListener(sa);
        scale.addActionListener(sa);
        actionList.add(sa);
    }

    private void addScale2(){
        JTextField scaleField = new JTextField(String.valueOf(stamp.getScale2()));
        addRight(scaleField);
        JButton scale = new JButton("Scale 2");
        addLeft(scale);
        Action sa = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Double s = Double.parseDouble(scaleField.getText());
                    if (s > 1.0){
                        setWarningText("Must be <= 1.0");
                    }else{
                        stamp.setScale2(s);
                        afterChange();
                    }
                }catch (NumberFormatException e1){
                    setWarningText("Input a double");
                }
                scaleField.setText(String.valueOf(stamp.getScale2()));
            }
        };
        scaleField.addActionListener(sa);
        scale.addActionListener(sa);
        actionList.add(sa);
    }

    private void addSpinRate1(){
        JTextField spinField = new JTextField(String.valueOf(stamp.getSpinRate1()));
        addRight(spinField);
        JButton spin = new JButton("Spin 1");
        addLeft(spin);
        Action sa = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int d = Integer.parseInt(spinField.getText());
                    stamp.setSpinRate1(d);
                    afterChange();
                }catch (NumberFormatException e1){
                    setWarningText("Spin each repetition");
                }
                spinField.setText(String.valueOf(stamp.getSpinRate1()));
            }
        };
        spinField.addActionListener(sa);
        spin.addActionListener(sa);
        actionList.add(sa);
    }

    private void addSpinRate2(){
        JTextField spinField = new JTextField(String.valueOf(stamp.getSpinRate2()));
        addRight(spinField);
        JButton spin = new JButton("Spin 2");
        addLeft(spin);
        Action sa = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int d = Integer.parseInt(spinField.getText());
                    stamp.setSpinRate2(d);
                    afterChange();
                }catch (NumberFormatException e1){
                    setWarningText("Spin each repetition");
                }
                spinField.setText(String.valueOf(stamp.getSpinRate2()));
            }
        };
        spinField.addActionListener(sa);
        spin.addActionListener(sa);
        actionList.add(sa);
    }

    private void addDeltaTheta1(){
        JTextField deltaField = new JTextField(String.valueOf(stamp.getDeltaDirection1()));
        addRight(deltaField);
        JButton delta = new JButton("Angle 1");
        addLeft(delta);
        Action da = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int d = Integer.parseInt(deltaField.getText());
                    stamp.setDeltaDirection1(d);
                    afterChange();
                }catch (NumberFormatException e1){
                    setWarningText("Angle change each repetition");
                }
                deltaField.setText(String.valueOf(stamp.getDeltaDirection1()));
            }
        };
        deltaField.addActionListener(da);
        delta.addActionListener(da);
        actionList.add(da);
    }

    private void addDeltaTheta2(){
        JTextField deltaField = new JTextField(String.valueOf(stamp.getDeltaDirection2()));
        addRight(deltaField);
        JButton delta = new JButton("Angle 2");
        addLeft(delta);
        Action da = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int d = Integer.parseInt(deltaField.getText());
                    stamp.setDeltaDirection2(d);
                    afterChange();
                }catch (NumberFormatException e1){
                    setWarningText("Angle change each repetition");
                }
                deltaField.setText(String.valueOf(stamp.getDeltaDirection2()));
            }
        };
        deltaField.addActionListener(da);
        delta.addActionListener(da);
        actionList.add(da);
    }

    private void addDistance1(){
        JTextField distanceField = new JTextField(String.valueOf(stamp.getDistance1()));
        addRight(distanceField);
        JButton distance = new JButton("Distance 1");
        addLeft(distance);
        Action da = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int d = Integer.parseInt(distanceField.getText());
                    stamp.setDistance1(d);
                    afterChange();
                }catch (NumberFormatException e1){
                    setWarningText("How far away");
                }
                distanceField.setText(String.valueOf(stamp.getDistance1()));
            }
        };
        distanceField.addActionListener(da);
        distance.addActionListener(da);
        actionList.add(da);
    }

    private void addDistance2(){
        JTextField distanceField = new JTextField(String.valueOf(stamp.getDistance2()));
        addRight(distanceField);
        JButton distance = new JButton("Distance 2");
        addLeft(distance);
        Action da = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int d = Integer.parseInt(distanceField.getText());
                    stamp.setDistance2(d);
                    afterChange();
                }catch (NumberFormatException e1){
                    setWarningText("How far away");
                }
                distanceField.setText(String.valueOf(stamp.getDistance2()));
            }
        };
        distanceField.addActionListener(da);
        distance.addActionListener(da);
        actionList.add(da);
    }

    private void addStartTheta(){
        JTextField startField = new JTextField(String.valueOf(stamp.getStartingDirection()));
        addRight(startField);
        JButton start = new JButton("Initial Angle");
        addLeft(start);
        Action sa = new AbstractAction() {
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
        };
        startField.addActionListener(sa);
        start.addActionListener(sa);
        actionList.add(sa);
    }

    private void addReps(){
        JTextField repField = new JTextField(String.valueOf(stamp.getReps()));
        addRight(repField);
        JButton rb = new JButton("Repetitions");
        addLeft(rb);
        Action ra = new AbstractAction() {
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
        };
        repField.addActionListener(ra);
        rb.addActionListener(ra);
        actionList.add(ra);
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

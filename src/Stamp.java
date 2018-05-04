import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

public class Stamp {
    private boolean empty = false;
    private int xOffset = 0;
    private int yOffset = 0;
    private int width;
    private int height;
    private int rotationDegree = 0;
    private boolean xflip = false;
    private boolean yflip = false;
    private double xshear = 0.0;
    private double yshear = 0.0;
    private int howAdd = 0;
    private int reps = 0;
    private int startingDirection = 0;
    private int distance = 0;
    private int deltaDirection = 0;
    private int spinRate = 0;
    private double scale = 1.0;
    private boolean xflipRepetition = false;
    private boolean yflipRepetition = false;
    private byte[][][] data;

    Stamp(){
        data = new byte[0][0][0];
        width = 0;
        height = 0;
        empty = true;
    }

    Stamp(int xOffset, int yOffset, byte[][][] data){
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.data = data;
        this.width = data.length;
        this.height = data[0].length;
    }

    void stamp(byte[][][] working){
        if (empty){
            return;
        }
        byte[][][] adjusted = transformRaw(data);
        combine(adjusted, working, xOffset, yOffset);
        stamp(1, adjusted, working, xOffset, yOffset);
    }

    private void stamp(int depth, byte[][][] verb, byte[][][] noun, int xo, int yo){
        if (depth > reps || depth > 99){
            return;
        }
        byte[][][] next = copyArray(verb);
        if (depth%2 == 1 && (xflipRepetition || yflipRepetition)){
            next = flipRep(next);
        }
        int xn = xo;
        int yn = yo;
        int d = distance;
        int a = startingDirection;
        for (int i = 0; i < depth; i++){
            d = (int)(d * scale);
            a += deltaDirection %360;
            yn += Math.sin((Math.PI * a)/180)*d;
            xn += Math.cos((Math.PI * a)/180)*d;
        }
        next = transformRec(next, depth);
        combine(next, noun, xn, yn);
        stamp(++depth, verb, noun, xo, yo);
    }

    private byte[][][] flipRep(byte[][][] orig){
        double xf = 1.0;
        double yf = 1.0;
        if (xflipRepetition){
            xf = -1.0;
        }
        if (yflipRepetition){
            yf = -1.0;
        }
        AffineTransform at = new AffineTransform();
        at.scale(xf, yf);

        return transform(orig, at);
    }

    private byte[][][] transformRec(byte[][][] orig, int depth){
        int theta = (depth * spinRate)%360;
        if (scale == 1.0 && theta == 0){
            return copyArray(orig);
        }

        double useScale = Math.pow(scale, depth);

        double xSize = (double)(orig.length);
        double ySize = (double)(orig[0].length);

        AffineTransform at = new AffineTransform();
        at.translate(xSize/2, ySize/2);
        at.scale(useScale, useScale);
        at.rotate((Math.PI * theta)/180);
        at.translate(xSize/-2, ySize/-2);
        return transform(orig, at);
    }
//
//    private byte[][][] refineArray(byte[][][] original){
//        int width = original.length;
//        int height = original[0].length;
//        int minx = width;
//        int maxx = -1;
//        int miny = height;
//        int maxy = -1;
//        for (int x = 0; x < width; x++){
//            for(int y = 0; y < height; y++){
//                if ((original[x][y][3]&0xFF) != 0){
//                    minx = Math.min(minx, x);
//                    miny = Math.min(miny, y);
//                    maxx = Math.max(maxx, x);
//                    maxy = Math.max(maxy, y);
//                }
//            }
//        }
//
//        width = maxx - minx + 1;
//        height = maxy - miny + 1;
//        byte[][][] next = new byte[width][height][4];
//        for (int x = 0; x < width; x++){
//            for (int y = 0; y < height; y++){
//                next[x][y][0] = original[x + minx][y + miny][0];
//                next[x][y][1] = original[x + minx][y + miny][1];
//                next[x][y][2] = original[x + minx][y + miny][2];
//                next[x][y][3] = original[x + minx][y + miny][3];
//            }
//        }
//        return next;
//    }

    private byte[][][] transform(byte[][][] orig, AffineTransform at){
        int minx = Integer.MAX_VALUE;
        int maxx = Integer.MIN_VALUE;
        int miny = Integer.MAX_VALUE;
        int maxy = Integer.MIN_VALUE;

        int ow = orig.length;
        int oh = orig[0].length;

        for (int x = 0; x < ow; x++){
            for (int y = 0; y < oh; y++){
                Point2D dp = new Point(x, y);
                Point2D wp = new Point();
                wp = at.transform(dp, wp);
                int wx = (int)wp.getX();
                int wy = (int)wp.getY();
                minx = Math.min(minx, wx);
                miny = Math.min(miny, wy);
                maxx = Math.max(maxx, wx);
                maxy = Math.max(maxy, wy);
            }
        }
        try {
            at.invert();
        } catch (NoninvertibleTransformException e) {
            e.printStackTrace();
            return orig;
        }

        int xd = maxx - minx + 1;
        int yd = maxy - miny + 1;
        byte[][][] next = new byte[xd][yd][4];
        for(int x = 0; x < xd; x++){
            for(int y = 0; y < yd; y++){
                Point2D dp = new Point(x + minx, y + miny);
                Point2D wp = new Point();
                wp = at.transform(dp, wp);
                int wx = (int)wp.getX();
                int wy = (int)wp.getY();
                if (wx > -1 && wx < ow && wy > -1 && wy < oh){
                    next[x][y][0] = orig[wx][wy][0];
                    next[x][y][1] = orig[wx][wy][1];
                    next[x][y][2] = orig[wx][wy][2];
                    next[x][y][3] = orig[wx][wy][3];
                }
            }
        }
        return next;
    }

    private byte[][][] copyArray(byte[][][] orig ){
        int xRes = orig.length;
        int yRes = orig[0].length;
        byte[][][] next = new byte[xRes][yRes][4];
        for (int x= 0; x < xRes; x++){
            for(int y = 0; y < yRes; y++){
                next[x][y][0] = orig[x][y][0];
                next[x][y][1] = orig[x][y][1];
                next[x][y][2] = orig[x][y][2];
                next[x][y][3] = orig[x][y][3];
            }
        }
        return next;
    }


    private byte[][][] transformRaw(byte[][][] orig){
        int xRes = orig.length;
        int yRes = orig[0].length;
        //skip out if no transform
        if (xRes == width && yRes == height && rotationDegree == 0 && !xflip && !yflip && xshear == 0.0 && yshear == 0.0){
            return copyArray(orig);
        }

        double xSize = (double)(xRes);
        double ySize = (double)(yRes);
        AffineTransform at = new AffineTransform();
        // orders executed in stack order, lifo
        at.translate(xSize/2, ySize/2);
        at.shear(xshear, yshear);
        double xScale = width/xSize;
        double yScale = height/ySize;
        if (xflip){
            xScale = xScale * -1;
        }
        if (yflip){
            yScale = yScale * -1;
        }
        at.scale(xScale, yScale);
        at.rotate((Math.PI * rotationDegree)/180);
        at.translate(xSize/-2, ySize/-2);
        return transform(orig, at);
    }

    private void combine(byte[][][] verb, byte[][][] noun, int xo, int yo){
        int nw = noun.length;
        int nh = noun[0].length;
        int halfw = verb.length/2;
        int halfh = verb[0].length/2;
        for(int x = 0; x < verb.length; x++){
            for(int y = 0; y < verb[0].length; y++){
                int nx = x + xo - halfw;
                int ny = y + yo - halfh;
                if (nx > -1 && nx < nw &&
                        ny > -1 && ny < nh && (verb[x][y][3] & 0xFF) != 0){
                    if ((noun[nx][ny][3] & 0xFF) == 0){
                        noun[nx][ny][0] = verb[x][y][0];
                        noun[nx][ny][1] = verb[x][y][1];
                        noun[nx][ny][2] = verb[x][y][2];
                        noun[nx][ny][3] = verb[x][y][3];

                    }else{
                        if (howAdd > 0){
                            noun[nx][ny][0] = verb[x][y][0];
                            noun[nx][ny][1] = verb[x][y][1];
                            noun[nx][ny][2] = verb[x][y][2];
                            noun[nx][ny][3] = verb[x][y][3];

                        }else if (howAdd == 0){
                            int newR = ((noun[nx][ny][0] & 0xFF) + (verb[x][y][0] & 0xFF))/2;
                            int newG = ((noun[nx][ny][1] & 0xFF) + (verb[x][y][1] & 0xFF))/2;
                            int newB = ((noun[nx][ny][2] & 0xFF) + (verb[x][y][2] & 0xFF))/2;
                            int newA = ((noun[nx][ny][3] & 0xFF) + (verb[x][y][3] & 0xFF))/2;
                            noun[nx][ny][0] = (byte)newR;
                            noun[nx][ny][1] = (byte)newG;
                            noun[nx][ny][2] = (byte)newB;
                            noun[nx][ny][3] = (byte)newA;
                        }
                    }
                }
            }
        }
    }

    public int getxOffset() {
        return xOffset;
    }

    public void setxOffset(int xOffset) {
        this.xOffset = xOffset;
    }

    public int getyOffset() {
        return yOffset;
    }

    public void setyOffset(int yOffset) {
        this.yOffset = yOffset;
    }

    public int getRotationDegree() {
        return rotationDegree;
    }

    public void setRotationDegree(int rotationDegree) {
        this.rotationDegree = rotationDegree;
    }

    public byte[][][] getData() {
        if (empty){
            return null;
        }
        return data;
    }

    public void setData(byte[][][] data) {
        this.data = data;
    }

    public boolean isXflip() {
        return xflip;
    }

    public void setXflip(boolean xflip) {
        this.xflip = xflip;
    }

    public boolean isYflip() {
        return yflip;
    }

    public void setYflip(boolean yflip) {
        this.yflip = yflip;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public double getXshear() {
        return xshear;
    }

    public void setXshear(double xshear) {
        this.xshear = xshear;
    }

    public double getYshear() {
        return yshear;
    }

    public void setYshear(double yshear) {
        this.yshear = yshear;
    }

    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }

    public int getHowAdd() {
        return howAdd;
    }

    public void setHowAdd(int howAdd) {
        this.howAdd = howAdd;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public int getStartingDirection() {
        return startingDirection;
    }

    public void setStartingDirection(int startingDirection) {
        this.startingDirection = startingDirection;
    }

    public int getDeltaDirection() {
        return deltaDirection;
    }

    public void setDeltaDirection(int deltaDirection) {
        this.deltaDirection = deltaDirection;
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public boolean isXflipRepetition() {
        return xflipRepetition;
    }

    public void setXflipRepetition(boolean xflipRepetition) {
        this.xflipRepetition = xflipRepetition;
    }

    public boolean isYflipRepetition() {
        return yflipRepetition;
    }

    public void setYflipRepetition(boolean yflipRepetition) {
        this.yflipRepetition = yflipRepetition;
    }

    public int getSpinRate() {
        return spinRate;
    }

    public void setSpinRate(int spinRate) {
        this.spinRate = spinRate;
    }
}

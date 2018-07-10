import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

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
    private double howAdd = 0.5;
    private int reps = 0;
    private int startingDirection = 0;
    private int distance = 0;
    private int deltaDirection = 0;
    private int spinRate = 0;
    private double scale = 1.0;
    private boolean xflipRepetition = false;
    private boolean yflipRepetition = false;
    private int minAlpha = 0;
    private int fillAlpha = 255;
    private int deltaColor = 0;
    private double howDeltaColor = 0.5;
    private int[][] data;

    Stamp(){
        data = new int[0][0];
        width = 0;
        height = 0;
        empty = true;
    }

    Stamp(BufferedImage image){
        this.data = convertImage(image);
        this.width = data.length;
        this.height = data[0].length;
        this.xOffset = width/2;
        this.yOffset = height/2;
    }

    Stamp(int[][] data){
        this.data = data;
        this.width = data.length;
        this.height = data[0].length;
        this.xOffset = width/2;
        this.yOffset = height/2;
    }

    static int[][] convertImage(BufferedImage image){
        int width = image.getWidth();
        int height = image.getHeight();
        int[][] st = new int[width][height];
        for (int x = 0; x < width; x++){
            for (int y = 0; y < height; y++){
                st[x][y] = image.getRGB(x, y);
            }
        }
        return refineArray(st);
    }

    private static int[][] refineArray(int[][] original){
        int width = original.length;
        int height = original[0].length;
        int minx = width;
        int maxx = -1;
        int miny = height;
        int maxy = -1;
        for (int x = 0; x < width; x++){
            for(int y = 0; y < height; y++){
                if ((original[x][y] >> 24 & 0xFF) != 0){
                    minx = Math.min(minx, x);
                    miny = Math.min(miny, y);
                    maxx = Math.max(maxx, x);
                    maxy = Math.max(maxy, y);
                }
            }
        }

        width = maxx - minx + 1;
        height = maxy - miny + 1;
        if (width < 1 || height < 1){
            return new int[0][0];
        }
        int[][] next = new int[width][height];
        for (int x = 0; x < width; x++){
            for (int y = 0; y < height; y++){
                next[x][y] = original[x + minx][y + miny];
            }
        }
        return next;
    }

    static int blend(int c0, int c1, double weight0){
        if ( weight0 > 1.0 ) weight0 = 1.0;
        else if ( weight0 < 0.0 ) weight0 = 0.0;
        double weight1 = 1.0 - weight0;

        int a = (int)((weight0 * (c0 >> 24 & 0xff)) + (weight1 * (c1 >> 24 & 0xff)));
        int r = (int)((weight0 * (c0 >> 16 & 0xff)) + (weight1 * ((c1 >> 16 & 0xff))));
        int g = (int)((weight0 * (c0 >> 8 & 0xff)) + (weight1 *((c1 >> 8 & 0xff))));
        int b = (int)((weight0 * (c0 & 0xff)) + (weight1 * (c1 & 0xff)));

        return  (a << 24) | (r << 16) | (g << 8) | b ;
    }

//    void stamp(BufferedImage image){
//        if (empty){
//            return;
//        }
//        int[][] adjusted = transformRaw(data);
//        stamp(adjusted, image, xOffset, yOffset);
//        rep(1, adjusted, image, xOffset, yOffset);
//    }

    void mainStamp(BufferedImage image){
        if (empty){
            return;
        }
        int[][] adjusted = transformMain(data);
        stamp(adjusted, image, xOffset, yOffset);
    }

    void spanelStamp(BufferedImage image){
        if (empty){
            return;
        }
        int[][] adjusted = transformSPanel(data);
        BufferedImage temp = new BufferedImage(image.getWidth() * 2, image.getHeight() * 2,
                BufferedImage.TYPE_INT_ARGB);

        xOffset = image.getWidth();
        yOffset = image.getHeight();
        stamp(adjusted, temp, xOffset, yOffset);
        rep(1, adjusted, temp, xOffset, yOffset);
        makeFit(temp, image);
    }

    private void makeFit(BufferedImage temp, BufferedImage image){
        int tw = temp.getWidth();
        int th = temp.getHeight();
        int minx = tw;
        int maxx = -1;
        int miny = th;
        int maxy = -1;

        for (int x = 0; x < tw; x++){
            for(int y = 0; y < th; y++){
                if ((temp.getRGB(x, y)>>24 & 0xff) != 0){
                    minx = Math.min(minx, x);
                    miny = Math.min(miny, y);
                    maxx = Math.max(maxx, x);
                    maxy = Math.max(maxy, y);
                }
            }
        }

        int centerx = (minx + maxx)/2;
        int centery = (miny + maxy)/2;

        int w = image.getWidth();
        int h = image.getHeight();

        int w2 = w/2;
        int h2 = h/2;

        for (int x = 0; x < w;  x++){
            for(int y = 0; y < h; y++){
                int tx = centerx - w2 + x;
                int ty = centery - h2 + y;

                if(tx < 0 || ty < 0 || tx >= tw|| ty >= th){
                    continue;
                }
                image.setRGB(x, y, temp.getRGB(tx, ty));
            }
        }
    }

    private void rep(int depth, int[][] verb, BufferedImage image, int xo, int yo){
        if (depth > reps || depth > 99){
            return;
        }
        int[][] next = copyArray(verb);
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
        stamp(next, image, xn, yn);
        rep(++depth, verb, image, xo, yo);
    }

    private int[][] flipRep(int[][] orig){
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

    private int[][] transformRec(int[][] orig, int depth){
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

    private int[][] transform(int[][] orig, AffineTransform at){
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
        int[][] next = new int[xd][yd];
        for(int x = 0; x < xd; x++){
            for(int y = 0; y < yd; y++){
                Point2D dp = new Point(x + minx, y + miny);
                Point2D wp = new Point();
                wp = at.transform(dp, wp);
                int wx = (int)wp.getX();
                int wy = (int)wp.getY();
                if (wx > -1 && wx < ow && wy > -1 && wy < oh){
                    next[x][y] = orig[wx][wy];
                }
            }
        }
        return next;
    }

    private int[][] copyArray(int[][] orig ){
        int xRes = orig.length;
        int yRes = orig[0].length;
        int[][] next = new int[xRes][yRes];
        for (int x= 0; x < xRes; x++){
            for(int y = 0; y < yRes; y++){
                next[x][y] = orig[x][y];
            }
        }
        return next;
    }

    private int[][] transformSPanel(int[][] orig){
        int xRes = orig.length;
        int yRes = orig[0].length;
        //skip out if no transform
        if (xRes == width && yRes == height && minAlpha == 0 && fillAlpha == 255 && deltaColor == 0){
            return copyArray(orig);
        }

        int[][] next;
        if ( minAlpha == 0 && fillAlpha == 255 && deltaColor == 0){
            next = orig;

        }else{
            next = new int[xRes][yRes];
            for (int x = 0; x < xRes; x++){
                for(int y = 0; y < yRes; y++){
                    int c = orig[x][y];
                    int alph = c >> 24 & 0xFF;
                    if (alph < minAlpha){
                        next[x][y] = 0;
                    }else if (alph >= fillAlpha){
                        int a = 255;
                        int r = c >> 16 & 0xFF;
                        int g = c >> 8 & 0xFF;
                        int b = c & 0xFF;
                        int nc = (a << 24) | (r << 16) | (g << 8) | b ;
                        if (deltaColor != 0){
                            next[x][y] = blend(nc, deltaColor, howDeltaColor);
                        }else{
                            next[x][y] = nc;
                        }
                    }else{
                        if (deltaColor != 0){
                            next[x][y] = blend(c, deltaColor, howDeltaColor);
                        }else{
                            next[x][y] = c;
                        }
                    }
                }
            }
        }

        double xSize = (double)(xRes);
        double ySize = (double)(yRes);
        AffineTransform at = new AffineTransform();
        // orders executed in stack order, lifo
        at.translate(xSize/2, ySize/2);
        double xScale = width/xSize;
        double yScale = height/ySize;

        at.scale(xScale, yScale);
        at.translate(xSize/-2, ySize/-2);
        return transform(next, at);
    }


    private int[][] transformMain(int[][] orig){
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
            xScale *= -1;
        }
        if (yflip){
            yScale *= -1;
        }
        at.scale(xScale, yScale);
        at.rotate((Math.PI * rotationDegree)/180);
        at.translate(xSize/-2, ySize/-2);
        return transform(orig, at);
    }

    private void stamp(int[][] verb, BufferedImage image, int xo, int yo){
        int vw = verb.length;
        int vh = verb[0].length;
        int nw = image.getWidth();
        int nh = image.getHeight();
        int halfw = vw/2;
        int halfh = vh/2;
        for(int x = 0; x < vw; x++){
            for(int y = 0; y < vh; y++){
                int nx = x + xo - halfw;
                int ny = y + yo - halfh;
                if (nx > -1 && nx < nw &&
                        ny > -1 && ny < nh && (verb[x][y] >> 24 & 0xFF) != 0){
                    int c = image.getRGB(nx, ny);
                    if ((c >> 24 & 0xFF) != 0){
                        int blended = blend(verb[x][y], c, howAdd);
                        image.setRGB(nx, ny, blended);
                    }else{
                        image.setRGB(nx, ny, verb[x][y]);
                    }

//                    if ((c & 0xFF000000) >> 24 == 0){
//                        Color co = new Color(verb[x][y][0] & 0xFF, verb[x][y][1] & 0xFF,
//                                verb[x][y][2] & 0xFF, verb[x][y][3] & 0xFF);
//                        image.setRGB(x, y, co.getRGB());
//                    }else{
//                        if (howAdd > 0){
//                            Color co = new Color(verb[x][y][0] & 0xFF, verb[x][y][1] & 0xFF,
//                                    verb[x][y][2] & 0xFF, verb[x][y][3] & 0xFF);
//                            image.setRGB(x, y, co.getRGB());
////                            noun[nx][ny][0] = verb[x][y][0];
////                            noun[nx][ny][1] = verb[x][y][1];
////                            noun[nx][ny][2] = verb[x][y][2];
////                            noun[nx][ny][3] = verb[x][y][3];
//
//                        }else if (howAdd == 0){
//                            int newR = ((c & 0x00FF0000)>> 16 + (verb[x][y][0] & 0xFF))/2;
//                            int newG = ((c & 0x0000FF00)>> 8 + (verb[x][y][1] & 0xFF))/2;
//                            int newB = ((c & 0x000000FF) + (verb[x][y][2] & 0xFF))/2;
//                            int newA = ((c & 0xFF000000)>> 24 + (verb[x][y][3] & 0xFF))/2;
//                            Color co = new Color(newR, newG, newB, newA);
//                            image.setRGB(x, y, co.getRGB());
//                        }
//                    }
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

    public int[][] getData() {
        if (empty){
            return null;
        }
        return data;
    }

    public void setData(int[][] data) {
        empty = false;
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

    public double getHowAdd() {
        return howAdd;
    }

    public void setHowAdd(double howAdd) {
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

    public int getMinAlpha() {
        return minAlpha;
    }

    public void setMinAlpha(int minAlpha) {
        this.minAlpha = minAlpha;
    }

    public int getFillAlpha() {
        return fillAlpha;
    }

    public void setFillAlpha(int fillAlpha) {
        this.fillAlpha = fillAlpha;
    }

    public int getDeltaColor() {
        return deltaColor;
    }

    public void setDeltaColor(int deltaColor) {
        this.deltaColor = deltaColor;
    }

    public double getHowDeltaColor() {
        return howDeltaColor;
    }

    public void setHowDeltaColor(double howDeltaColor) {
        this.howDeltaColor = howDeltaColor;
    }
}

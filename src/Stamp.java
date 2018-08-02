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
    private int distance1 = 0;
    private int deltaDirection1 = 0;
    private int spinRate1 = 0;
    private double scale1 = 1.0;
    private int distance2 = 0;
    private int deltaDirection2 = 0;
    private int spinRate2 = 0;
    private double scale2 = 1.0;
    private boolean use1 = false;
    private boolean use2 = false;
    private boolean onlyLeaf = false;
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
        int factor = 2;
        BufferedImage temp = new BufferedImage(image.getWidth() * factor, image.getHeight() * factor,
                BufferedImage.TYPE_INT_ARGB);

        xOffset = image.getWidth();
        yOffset = image.getHeight();
        if(!onlyLeaf || reps == 0){
            stamp(adjusted, temp, xOffset, yOffset);
        }
        repTemp(1, adjusted, temp, xOffset, yOffset,startingDirection - 90);
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

    private void repTemp(int depth, int[][] verb, BufferedImage image, int xo, int yo, int a){
        if (depth > reps || depth > 99 || (!use1 && !use2)){
            return;
        }
        //int[][] next = copyArray(verb);

        if (use1){
            int xn1 = xo;
            int yn1 = yo;
            int d1 = (int)(distance1 * Math.pow(scale1, depth));
            int a1 = a + deltaDirection1 %360;
            yn1 += Math.sin((Math.PI * a1)/180)*d1;
            xn1 += Math.cos((Math.PI * a1)/180)*d1;

            int[][] next1 = transformRec(verb, depth, true);
            if(!onlyLeaf || depth == reps){
                stamp(next1, image, xn1, yn1);
            }

            repTemp(depth + 1, verb, image, xn1, yn1, a1);
        }

        if (use2){
            int xn2 = xo;
            int yn2 = yo;

            int d2 = (int)(distance2 * Math.pow(scale2, depth));
            int a2 = a + deltaDirection2 %360;
            yn2 += Math.sin((Math.PI * a2)/180)*d2;
            xn2 += Math.cos((Math.PI * a2)/180)*d2;

            int[][] next2 = transformRec(verb, depth,  false);
            if(!onlyLeaf || depth == reps){
                stamp(next2, image, xn2, yn2);
            }
            repTemp(depth + 1, verb, image, xn2, yn2, a2);
        }
    }

//    private void rep(int depth, int[][] verb, BufferedImage image, int xo, int yo){
//        if (depth > reps || depth > 99){
//            return;
//        }
//        int[][] next = copyArray(verb);
//        if (depth%2 == 1 && (use1 || use2)){
//            next = flipRep(next);
//        }
//        int xn = xo;
//        int yn = yo;
//        int d = distance;
//        int a = startingDirection;
//        for (int i = 0; i < depth; i++){
//            d = (int)(d * scale);
//            a += deltaDirection %360;
//            yn += Math.sin((Math.PI * a)/180)*d;
//            xn += Math.cos((Math.PI * a)/180)*d;
//        }
//        next = transformRec(next, true);
//        stamp(next, image, xn, yn);
//        rep(++depth, verb, image, xo, yo);
//    }

//    private int[][] flipRep(int[][] orig){
//        double xf = 1.0;
//        double yf = 1.0;
//        if (use1){
//            xf = -1.0;
//        }
//        if (use2){
//            yf = -1.0;
//        }
//        AffineTransform at = new AffineTransform();
//        at.scale(xf, yf);
//
//        return transform(orig, at);
//    }

    private int[][] transformRec(int[][] orig, int depth, boolean positive){
        int theta;
        if(positive){
            theta = spinRate1;
        }else{
            theta = spinRate2;
        }

        theta *= depth%360;

        double scale;
        if(positive){
            scale = scale1;
        }else{
            scale = scale2;
        }

        scale = Math.pow(scale, depth);

        if (scale == 1.0 && theta == 0){
            return copyArray(orig);
        }
        double xSize = (double)(orig.length);
        double ySize = (double)(orig[0].length);

        AffineTransform at = new AffineTransform();
        at.translate(xSize/2, ySize/2);
        at.scale(scale, scale);
        at.rotate((Math.PI * theta)/180);
        at.translate(xSize/-2, ySize/-2);
        return transform(orig, at);
    }

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
            System.out.println("tranform() in Stamp.java:  " + e.getMessage());
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
                    if (alph <= minAlpha){
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
        this.rotationDegree = rotationDegree%360;
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

    public void setXshear(double xs) {
        xshear = xs;
    }

    public double getYshear() {
        return yshear;
    }

    public void setYshear(double ys) {
        yshear = ys;
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

    public boolean isUse1() {
        return use1;
    }

    public void setUse1(boolean use1) {
        this.use1 = use1;
    }

    public boolean isUse2() {
        return use2;
    }

    public void setUse2(boolean use2) {
        this.use2 = use2;
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

    public int getDistance1() {
        return distance1;
    }

    public void setDistance1(int distance1) {
        this.distance1 = distance1;
    }

    public int getDeltaDirection1() {
        return deltaDirection1;
    }

    public void setDeltaDirection1(int deltaDirection1) {
        this.deltaDirection1 = deltaDirection1;
    }

    public int getSpinRate1() {
        return spinRate1;
    }

    public void setSpinRate1(int spinRate1) {
        this.spinRate1 = spinRate1;
    }

    public double getScale1() {
        return scale1;
    }

    public void setScale1(double scale1) {
        this.scale1 = scale1;
    }

    public int getDistance2() {
        return distance2;
    }

    public void setDistance2(int distance2) {
        this.distance2 = distance2;
    }

    public int getDeltaDirection2() {
        return deltaDirection2;
    }

    public void setDeltaDirection2(int deltaDirection2) {
        this.deltaDirection2 = deltaDirection2;
    }

    public int getSpinRate2() {
        return spinRate2;
    }

    public void setSpinRate2(int spinRate2) {
        this.spinRate2 = spinRate2;
    }

    public double getScale2() {
        return scale2;
    }

    public void setScale2(double scale2) {
        this.scale2 = scale2;
    }

    public boolean isOnlyLeaf() {
        return onlyLeaf;
    }

    public void setOnlyLeaf(boolean onlyLeaf) {
        this.onlyLeaf = onlyLeaf;
    }
}

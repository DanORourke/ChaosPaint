
public class Vertex {
    private int x = 0;
    private int y = 0;
    private int r = 0;
    private int g = 0;
    private int b = 0;
    private int a = 0;
    private double gravity = 0.5;
    private double probability = 0.5;

    Vertex(int x, int y){
        this.x = x;
        this.y = y;
    }

    Vertex(int x, int y, int r, int g, int b, int a, double gravity, double probability){
        this.x = x;
        this.y = y;
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
        this.gravity = gravity;
        this.probability = probability;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getR() {
        return r;
    }

    public void setR(int r) {
        this.r = r;
    }

    public int getG() {
        return g;
    }

    public void setG(int g) {
        this.g = g;
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }

    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }

    public double getGravity() {
        return gravity;
    }

    public void setGravity(double gravity) {
        this.gravity = gravity;
    }

    public double getProbability() {
        return probability;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }

}

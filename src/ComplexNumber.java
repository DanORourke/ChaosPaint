
class ComplexNumber {
    private double real = 0.0;
    private double imaginary = 0.0;
    private boolean undefined = false;

    ComplexNumber(double real, double imaginary){
        this.real = real;
        this.imaginary = imaginary;
    }

    ComplexNumber(ComplexNumber c){
        this.real = c.getReal();
        this.imaginary = c.getImaginary();
    }

    void add(ComplexNumber c){
        real += c.getReal();
        imaginary += c.getImaginary();
    }

    void add(double r, double i){
        real += r;
        imaginary += i;
    }

    void multiply(double r, double i){
        double rTemp = (real * r) - (imaginary * i);
        imaginary = (imaginary*r) + (real*i);
        real = rTemp;
    }

    private void divide(double r, double i){
        if ((r*r) + (i*i) == 0.0){
            undefined = true;
            return;
        }
        double rTemp = (((real * r) + (imaginary*i))/((r*r) + (i*i)));
        imaginary = (((imaginary * r) - (real*i))/((r*r) + (i*i)));
        real = rTemp;
    }

    void exp(){
        // e ^ c;
        double ex = Math.exp(real);
        double rtemp = cos();
        imaginary = sin();
        real = rtemp;
        multiply(ex, 0);
    }

    double sin(){
        //sinx(c)
        return (Math.sin(real)*Math.cosh(imaginary)) + (Math.cos(real) * Math.sinh(imaginary));
    }

    private double cos(){
        //cos(c)
        return (Math.cos(real) * Math.cosh(imaginary)) + (Math.sin(real)*Math.sinh(imaginary));
    }

    void divide(ComplexNumber c){
        divide(c.getReal(), c.getImaginary());
    }

    void power(int p){
        if(p == 0){
            real = 1.0;
            imaginary = 0.0;
        }
        if (p > 0){
            positivePow(p);
        }
        if (p < 0){
            positivePow(p * -1);
            inverse();
        }
    }

    double distance(){
        //not rooting it
        return (real * real) + (imaginary * imaginary);
    }

    private void positivePow(int p){
        int i = 1;
        double oldR = real;
        double oldI = imaginary;
        while(i < p){
            multiply(oldR, oldI);
            i++;
        }
    }

    void inverse(){
        ComplexNumber c = new ComplexNumber(1.0, 0.0);
        c.divide(real, imaginary);
        real = c.getReal();
        imaginary = c.getImaginary();
    }

    double getReal() {
        return real;
    }

    double getImaginary() {
        return imaginary;
    }

    boolean isUndefined() {
        return undefined;
    }
}

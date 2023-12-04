package benicio.soluces.dimensional.utils;

public class MetodosUtils {

    public static final float PI = 3.1416f;
    public static float calculoNewton(float maior_D, float central_D,float menor_D, float comprimento ){
        float result = 0.0f;

        float ATM = (float) (Math.pow(maior_D,2) * PI) / 40000;
        float ATC = (float) (Math.pow(central_D,2) * PI) / 40000;
        float ATME = (float) (Math.pow(menor_D,2) * PI) / 40000;

        result = ( ( ATM + (4 * ATC) + ATME ) / 6 ) * comprimento;
        return result;
    }
}

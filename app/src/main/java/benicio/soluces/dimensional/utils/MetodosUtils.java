package benicio.soluces.dimensional.utils;

import android.util.Log;

public class MetodosUtils {

    public static final float PI = 3.1416f;
    public static float calculoNewton(float maior_D, float central_D,float menor_D, float comprimento ){
        float result;

        float ATM = (float) (Math.pow(maior_D,2) * PI) / 40000;
        float ATC = (float) (Math.pow(central_D,2) * PI) / 40000;
        float ATME = (float) (Math.pow(menor_D,2) * PI) / 40000;

        result = ( ( ATM + (4 * ATC) + ATME ) / 6 ) * comprimento;
        return result;
    }

    public static float novoCalculoNewton(float raioBase, float raioMeio, float raioFim, float comprimento){
        float result;

        float areaBase = (float) (PI * Math.pow(raioBase, 2));
        float areaMeio = (float) (PI * Math.pow(raioMeio, 2));
        float areaFim = (float) (PI * Math.pow(raioFim, 2));

        Log.d("mayara", "base: " + areaBase);
        Log.d("mayara", "meio: " + areaMeio);
        Log.d("mayara", "fim: " + areaFim);

        float umCesto = (float) 1/6;

        Log.d("mayara", "umCesto: " + umCesto);


        result = (float) umCesto * ((areaBase + (4*areaMeio) + areaFim ) * comprimento);

        Log.d("mayara", "result: " + result);


        return result;
    }
}

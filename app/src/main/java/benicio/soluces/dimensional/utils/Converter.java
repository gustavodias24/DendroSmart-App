package benicio.soluces.dimensional.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class Converter {
    private static final float DIVIDAO_RAFAEL = 1.5312f;

    @SuppressLint("DefaultLocale")
    public static String converterDpParaCm(Context context, int valorDp) {
        float dp = valorDp; // Valor em dp
        float pixels = dpToPixels(dp, context); // Converte dp para pixels
        float centimetros = (pixelsToCm(pixels, context)) / DIVIDAO_RAFAEL; // Converte pixels para centímetros

        return String.format("%.2f cm", centimetros); // Exibe o resultado no TextView
    }

    public static float dpToPixels(float dp, Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return dp * (metrics.densityDpi / 160f);
    }

    public static float pixelsToCm(float pixels, Context context) {
        float centimetrosPorPolegada = 2.54f;
        return pixels / (context.getResources().getDisplayMetrics().xdpi / centimetrosPorPolegada);
    }

    public static int dpToPixels(Context context, float dp) {
        // Converte dp para pixels usando TypedValue e applyDimension
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }
}

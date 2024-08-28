package benicio.soluces.dimensional.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import benicio.soluces.dimensional.model.ProjetoModel;

public class ProjetosUtils {

    public static final String name = "Projetos";
    public static final String prefs = "Projetos_prefs";

    public static void saveList(List<ProjetoModel> lista, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(prefs, Context.MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(name, new Gson().toJson(lista)).apply();
    }

    public static List<ProjetoModel> returnList(Context context) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(prefs, Context.MODE_PRIVATE);

        List<ProjetoModel> lista = new Gson().fromJson(
                sharedPreferences.getString(name, ""),
                new TypeToken<List<ProjetoModel>>() {
                }.getType()
        );

        if (lista == null)
            return new ArrayList<>();

        return lista;

    }
}

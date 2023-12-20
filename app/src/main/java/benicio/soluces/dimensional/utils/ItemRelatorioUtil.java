package benicio.soluces.dimensional.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import benicio.soluces.dimensional.model.ItemRelatorio;

public class ItemRelatorioUtil {
    public static final String name_prefs = "item_prefs";
    public static final String name = "item_name";

    public static void saveList(List<ItemRelatorio> lista, Context c){
        SharedPreferences sharedPreferences = c.getSharedPreferences(name_prefs, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(
                name,
                new Gson().toJson(lista)
        ).apply();
    }

    public static List<ItemRelatorio> returnLista(Context c){
        SharedPreferences sharedPreferences = c.getSharedPreferences(name_prefs, Context.MODE_PRIVATE);

        List<ItemRelatorio> lista = new ArrayList<>();

        List<ItemRelatorio> listaExistente = new Gson().fromJson(
                sharedPreferences.getString(name, ""),
                new TypeToken<List<ItemRelatorio>>(){}.getType()
        );

        if ( listaExistente != null) { lista.addAll(listaExistente); }

        return lista;
    }

}

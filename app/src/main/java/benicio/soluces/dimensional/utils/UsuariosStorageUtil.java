package benicio.soluces.dimensional.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import benicio.soluces.dimensional.model.UsuarioModel;

public class UsuariosStorageUtil {

    private static final String PREF_NAME = "usuario_prefs";
    private static final String KEY_TRANSACOES = "usuarios";

    public static void saveUsuarios(Context context, List<UsuarioModel> usuarios) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(usuarios);
        editor.putString(KEY_TRANSACOES, json);
        editor.apply();
    }

    public static List<UsuarioModel> loadUsuarios(Context context) {
        try {
            SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            Gson gson = new Gson();
            String json = sharedPreferences.getString(KEY_TRANSACOES, "");
            Type type = new TypeToken<List<UsuarioModel>>() {
            }.getType();
            return gson.fromJson(json, type);
        }catch (Exception e){
            return new ArrayList<>();
        }
    }
}
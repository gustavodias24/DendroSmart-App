package benicio.soluces.dimensional.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import benicio.soluces.dimensional.R;
import benicio.soluces.dimensional.model.ItemRelatorio;
import benicio.soluces.dimensional.utils.ItemRelatorioUtil;

public class AdapterItens extends RecyclerView.Adapter<AdapterItens.MyViewHolder>{

    Context c;
    List<ItemRelatorio> lista;
    private List<Boolean> selectedStates;
    public AdapterItens(Context c, List<ItemRelatorio> lista) {
        this.c = c;
        this.lista = lista;

        this.selectedStates = new ArrayList<>();
        for (int i = 0; i < lista.size(); i++) {
            selectedStates.add(false); // começa tudo desmarcado
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_relatorio, parent, false));
    }

    @SuppressLint({"NotifyDataSetChanged", "ResourceType"})
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ItemRelatorio item = lista.get(position);
        holder.itemView.getRootView().setClickable(false);
        Picasso.get().load(R.raw.lixo).into(holder.btnExcluir);

        // evita loop de evento
        holder.checkSelecionado.setOnCheckedChangeListener(null);

        boolean isChecked = selectedStates.get(position);
        holder.checkSelecionado.setChecked(isChecked);

        holder.checkSelecionado.setOnCheckedChangeListener((buttonView, checked) -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                selectedStates.set(pos, checked);
            }
        });

        holder.infos1.setText(item.getDadosTora());
//        holder.infos2.setText(item.getDadosVolume());
        holder.infos3.setText(item.getDadosGps());

        holder.btn_volumes.setOnClickListener(v -> {
            // Texto de volume
            String volumeText = item.getDadosVolume();
            if (volumeText == null || volumeText.trim().isEmpty()) {
                volumeText = "Nenhuma informação de volume disponível.";
            }

            // Cria TextView programaticamente
            TextView tv = new TextView(c);
            tv.setText(volumeText);
            tv.setTextSize(14f);
            tv.setTextColor(Color.BLACK);
            tv.setPadding(32, 32, 32, 32);

            // Deixa rolável, caso tenha muitas linhas
            ScrollView scrollView = new ScrollView(c);
            scrollView.addView(tv);

            new AlertDialog.Builder(c)
                    .setTitle("Volumes")
                    .setView(scrollView)
                    .setPositiveButton("Fechar", (dialog, which) -> dialog.dismiss())
                    .show();
        });


        holder.btnExcluir.setOnClickListener(view -> {
            new AlertDialog.Builder(c)
                    .setTitle("Confirmar exclusão")
                    .setMessage("Você tem certeza que deseja excluir?")
                    .setPositiveButton("Sim", (dialog, which) -> {
                        int pos = holder.getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            lista.remove(pos);
                            notifyItemRemoved(pos); // ou notifyDataSetChanged()
                            ItemRelatorioUtil.saveList(lista, c);
                        }
                    })
                    .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        Picasso.get().load(item.getImagemArvore()).into(holder.imagem_da_arvore);
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class  MyViewHolder extends RecyclerView.ViewHolder {

        TextView infos1;
        Button btn_volumes;
        TextView infos3;
        ImageButton btnExcluir;
        CheckBox checkSelecionado;

        ImageView imagem_da_arvore;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            infos1 = itemView.findViewById(R.id.text_info_generic1);
            btn_volumes = itemView.findViewById(R.id.btn_volumes);
//            infos2 = itemView.findViewById(R.id.text_info_generic2);
            infos3 = itemView.findViewById(R.id.text_info_generic3);
            btnExcluir = itemView.findViewById(R.id.btn_excluir);
            imagem_da_arvore = itemView.findViewById(R.id.imagem_da_arvore);
            checkSelecionado = itemView.findViewById(R.id.check_selecionado);

        }
    }

    // Marca ou desmarca todos
    public void toggleSelectAll(boolean checked) {
        for (int i = 0; i < selectedStates.size(); i++) {
            selectedStates.set(i, checked);
        }
        notifyDataSetChanged();
    }

    // Retorna apenas os itens selecionados
    public List<ItemRelatorio> getSelectedItems() {
        List<ItemRelatorio> selecionados = new ArrayList<>();
        for (int i = 0; i < lista.size(); i++) {
            if (selectedStates.get(i)) {
                selecionados.add(lista.get(i));
            }
        }
        return selecionados;
    }
}

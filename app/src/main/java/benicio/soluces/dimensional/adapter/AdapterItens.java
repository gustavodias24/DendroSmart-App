package benicio.soluces.dimensional.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import benicio.soluces.dimensional.R;
import benicio.soluces.dimensional.model.ItemRelatorio;
import benicio.soluces.dimensional.utils.ItemRelatorioUtil;

public class AdapterItens extends RecyclerView.Adapter<AdapterItens.MyViewHolder>{

    Context c;
    List<ItemRelatorio> lista;

    public AdapterItens(Context c, List<ItemRelatorio> lista) {
        this.c = c;
        this.lista = lista;
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
        holder.infos1.setText(item.getDadosTora());
        holder.infos2.setText(item.getDadosGps());
        holder.infos3.setText(item.getDadosVolume());

        holder.btnExcluir.setOnClickListener( view -> {
            lista.remove(position);
            this.notifyDataSetChanged();
            ItemRelatorioUtil.saveList(lista, c);
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class  MyViewHolder extends RecyclerView.ViewHolder {

        TextView infos1;
        TextView infos2;
        TextView infos3;
        ImageButton btnExcluir;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            infos1 = itemView.findViewById(R.id.text_info_generic1);
            infos2 = itemView.findViewById(R.id.text_info_generic2);
            infos3 = itemView.findViewById(R.id.text_info_generic3);
            btnExcluir = itemView.findViewById(R.id.btn_excluir);

        }
    }
}

package benicio.soluces.dimensional.adapter;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import benicio.soluces.dimensional.R;
import benicio.soluces.dimensional.model.ProjetoModel;

public class AdapterProjetos extends RecyclerView.Adapter<AdapterProjetos.MyViewHolder> {

    List<ProjetoModel> listaProjeto;
    Activity c;

    public AdapterProjetos(List<ProjetoModel> listaProjeto, Activity c) {
        this.listaProjeto = listaProjeto;
        this.c = c;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_projetos, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ProjetoModel projetoModel = listaProjeto.get(position);

        holder.info.setText(Html.fromHtml(projetoModel.toString()));
        holder.abrirArquivo.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(projetoModel.getCaminhoProjeto()));
                c.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(c, "Nenhum aplicativo encontrado para abrir este arquivo.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaProjeto.size();
    }

    final class MyViewHolder extends RecyclerView.ViewHolder {

        TextView info;
        Button abrirArquivo;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            info = itemView.findViewById(R.id.text_info_projeto);
            abrirArquivo = itemView.findViewById(R.id.abrir_projeto);
        }
    }
}

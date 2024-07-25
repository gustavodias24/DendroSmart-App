package benicio.soluces.dimensional.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Html;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import benicio.soluces.dimensional.R;
import benicio.soluces.dimensional.model.PostagemModel;
import benicio.soluces.dimensional.model.ResponseMsg;
import benicio.soluces.dimensional.utils.RetrofitUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AdapterMsg extends RecyclerView.Adapter<AdapterMsg.MyViewHolder> {

    List<PostagemModel> lista;
    Activity a;


    public AdapterMsg(List<PostagemModel> lista, Activity a) {
        this.lista = lista;
        this.a = a;
    }

    public Bitmap decodeBase64(String base64Str) {
        byte[] decodedBytes = Base64.decode(base64Str, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_menssage, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        PostagemModel postagem = lista.get(position);
        if(postagem.isTem_imagem()){
            holder.imagemPost.setVisibility(View.VISIBLE);
            Picasso.get().load(R.drawable.carregando).into(holder.imagemPost);
            RetrofitUtil.createServiceMsg(
                    RetrofitUtil.createRetrofitMsg()
            ).getImagePostagem(postagem.get_id()).enqueue(new Callback<ResponseMsg>() {
                @Override
                public void onResponse(Call<ResponseMsg> call, Response<ResponseMsg> response) {
                    if ( response.isSuccessful()){
                        assert response.body() != null;
                        holder.imagemPost.setImageBitmap(decodeBase64(response.body().getMsg()));
                    }
                }

                @Override
                public void onFailure(Call<ResponseMsg> call, Throwable t) {

                }
            });
        }
        holder.textPost.setText(Html.fromHtml(postagem.toString()));
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imagemPost;
        TextView textPost;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imagemPost = itemView.findViewById(R.id.imagemPost);
            textPost = itemView.findViewById(R.id.textPost);
        }
    }
}

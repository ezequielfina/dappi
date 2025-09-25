package com.example.travelguide;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FlagsFavoritesAdapter extends RecyclerView.Adapter<FlagsFavoritesAdapter.FlagViewHolder> {

    private List<Integer> flagImageResIds;
    private Context context;

    // Constructor: recibe el contexto y la lista de imágenes
    public FlagsFavoritesAdapter(Context context, List<Integer> flagImageResIds) {
        this.context = context;
        this.flagImageResIds = flagImageResIds;
    }

    @NonNull
    @Override
    public FlagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla el layout de cada item de la lista (item_flag.xml)
        View view = LayoutInflater.from(context).inflate(R.layout.item_flag, parent, false);
        return new FlagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FlagViewHolder holder, int position) {
        // Toma el recurso de imagen correspondiente a la posición y lo asigna al ImageView
        Integer imageResId = flagImageResIds.get(position);
        holder.imgFlag.setImageResource(imageResId);
    }

    @Override
    public int getItemCount() {
        // Devuelve la cantidad de elementos en la lista (o 0 si está vacía)
        return flagImageResIds != null ? flagImageResIds.size() : 0;
    }

    // ViewHolder: representa cada item de la lista
    static class FlagViewHolder extends RecyclerView.ViewHolder {
        ImageView imgFlag; // ImageView donde se muestra la bandera

        public FlagViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFlag = itemView.findViewById(R.id.imgFlag);
        }
    }

    // Método helper para actualizar la lista de imágenes y refrescar la vista
    public void updateData(List<Integer> newImageResIds) {
        this.flagImageResIds = newImageResIds;
        notifyDataSetChanged();
    }
}

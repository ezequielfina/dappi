package ar.edu.uade.api.ui.user;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import ar.edu.uade.api.R;


public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavoriteViewHolder> {

    private List<Integer> favoritePlaceImageResIds;
    private Context context;

    // Constructor: recibe el contexto y la lista de imágenes
    public FavoritesAdapter(Context context, List<Integer> favoritePlaceImageResIds) {
        this.context = context;
        this.favoritePlaceImageResIds = favoritePlaceImageResIds;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla el layout de cada item (item_place_thumb.xml) para mostrar un lugar
        View view = LayoutInflater.from(context).inflate(R.layout.item_place_thumb, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        // Obtiene la imagen según la posición y la asigna al ImageView
        Integer imageResId = favoritePlaceImageResIds.get(position);
        holder.imgPlace.setImageResource(imageResId);
    }

    @Override
    public int getItemCount() {
        // Devuelve la cantidad de items a mostrar (o 0 si no hay datos)
        return favoritePlaceImageResIds != null ? favoritePlaceImageResIds.size() : 0;
    }

    // ViewHolder: representa cada item del RecyclerView
    static class FavoriteViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPlace; // Imagen del lugar favorito

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPlace = itemView.findViewById(R.id.imgPlace);
        }
    }

    // Método para actualizar la lista de imágenes
    public void updateData(List<Integer> newImageResIds) {
        this.favoritePlaceImageResIds = newImageResIds;
    }
}

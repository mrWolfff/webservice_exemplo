package com.example.animes;

import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class AnimeAdapter extends RecyclerView.Adapter {
    private List<Anime> listaAnimes;
    private Actions actions;
    private int posicaoRemovidoRecentemente;
    private Anime filmeRemovidoRecentemente;

    public AnimeAdapter(List<Anime> listaAnimes, Actions actions) {
        this.listaAnimes = listaAnimes;
        this.actions = actions;
    }

    public List<Anime> getListaAnimes() {
        return listaAnimes;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_row, viewGroup, false);
        AnimeViewHolder holder = new AnimeViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int i) {
        AnimeViewHolder holder = (AnimeViewHolder) viewHolder;
        holder.imageImageView.setImageBitmap(listaAnimes.get(i).getImagem());
        holder.tituloTextView.setText(listaAnimes.get(i).getTitulo());
        holder.generoTextView.setText(listaAnimes.get(i).getGenero());
        holder.episodiosTextView.setText(String.valueOf(listaAnimes.get(i).getEpisodios()));
        holder.anoTextView.setText(String.valueOf(listaAnimes.get(i).getAno()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actions.edit(viewHolder.getAdapterPosition());
            }
        });


    }

    public void remover(int position){
        posicaoRemovidoRecentemente = position;
        filmeRemovidoRecentemente = listaAnimes.get(position);


        listaAnimes.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,this.getItemCount());
        actions.undo();
    }

    public void restaurar(){
        listaAnimes.add(posicaoRemovidoRecentemente,filmeRemovidoRecentemente);
        notifyItemInserted(posicaoRemovidoRecentemente);
    }

    public void inserir(Anime anime){
        listaAnimes.add(anime);
        notifyItemInserted(getItemCount());
    }

    public void mover(int fromPosition, int toPosition){
        if (fromPosition < toPosition)
            for (int i = fromPosition; i < toPosition; i++)
                Collections.swap(listaAnimes, i, i+1);
        else
            for (int i = fromPosition; i > toPosition; i--)
                Collections.swap(listaAnimes, i, i-1);
        notifyItemMoved(fromPosition,toPosition);
    }

    public void updateTitle(String newTitle, int position){
        listaAnimes.get(position).setTitulo(newTitle);
        notifyItemChanged(position);
    }

    public void updateGenero(String newGenero, int position){
        listaAnimes.get(position).setGenero(newGenero);
        notifyItemChanged(position);
    }

    public void updateAno (int newAno, int position){
        listaAnimes.get(position).setAno(newAno);
        notifyItemChanged(position);
    }

    public void update(Anime anime, int position){
        listaAnimes.get(position).setImagem(anime.getImagem());
        listaAnimes.get(position).setTitulo(anime.getTitulo());
        listaAnimes.get(position).setGenero(anime.getGenero());
        listaAnimes.get(position).setEpisodios(anime.getEpisodios());
        listaAnimes.get(position).setAno(anime.getAno());
        notifyItemChanged(position);
    }

    @Override
    public int getItemCount() {
        return listaAnimes.size();
    }

    public static class AnimeViewHolder extends RecyclerView.ViewHolder {

        ImageView imageImageView;
        TextView tituloTextView;
        TextView generoTextView;
        TextView episodiosTextView;
        TextView anoTextView;

        public AnimeViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setTag(this);
            imageImageView = (ImageView) itemView.findViewById(R.id.imageImageView);
            tituloTextView = (TextView) itemView.findViewById(R.id.tituloTextView);
            generoTextView = (TextView) itemView.findViewById(R.id.generoTextView);
            episodiosTextView = (TextView) itemView.findViewById(R.id.episodiosTextView);
            anoTextView = (TextView) itemView.findViewById(R.id.anoTextView);
        }

    }

}

package dca.bstu.project;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dca.bstu.project.Activities.DetailsActivity;
import dca.bstu.project.Data.Recipie;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder>{
    private LayoutInflater inflater;
    private List<Recipie> recipeList;

    public ListAdapter(Context context, List<Recipie> recipes) {
        this.recipeList = recipes;
        this.inflater = LayoutInflater.from(context);
//        this.onRecipeClickListener = onRecipeClickListener;
    }

    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        final View view = inflater.inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ListAdapter.ViewHolder holder, int position) {
        Recipie recipe = recipeList.get(position);
        holder.viewImage.setImageBitmap(recipe.getImage());
        holder.viewTitle.setText(recipe.getName());
        String s = "";
        for(String item: recipe.getLeftComponents())
        {
            s+=item+"; ";
        }
        s+=".";
        holder.viewDescription.setText(s);
        holder.viewType.setText(String.valueOf(recipe.getUserCreator()));
        holder.viewID = recipe.getId();
        if(recipe.getReady()==1){
            holder.viewCheckBox.setChecked(true);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Recipie recipe = recipeList.get(position);
                Intent intent = new Intent(v.getContext(), DetailsActivity.class);
                intent.putExtra("RecipieObj", recipe.getId());
                v.getContext().startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView viewImage;
        final TextView viewTitle, viewType, viewDescription;
        int viewID;
        final CheckBox viewCheckBox;

        ViewHolder(View view) {
            super(view);
            this.viewImage = (ImageView)view.findViewById(R.id.listImage);
            this.viewTitle = view.findViewById(R.id.title);
            this.viewType = view.findViewById(R.id.type);
            this.viewDescription = view.findViewById(R.id.description);
            this.viewID = -1;
            this.viewCheckBox=view.findViewById(R.id.checkbox);

        }

    }
}

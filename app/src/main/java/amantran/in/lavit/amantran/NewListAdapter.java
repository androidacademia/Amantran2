package amantran.in.lavit.amantran;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by root on 2/28/18.
 */

public class NewListAdapter extends RecyclerView.Adapter<NewListAdapter.MyViewHolder> {
    private NewInvitationActivity context;
    private ArrayList<Contact> finalContactList;
    private LayoutInflater inflater;

    public NewListAdapter(NewInvitationActivity context, ArrayList<Contact> finalContactList) {
        this.context = context;
        this.finalContactList = finalContactList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.contact_image,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Contact contact = finalContactList.get(position);
        holder.textViewNickName.setText(contact.name);
        holder.itemView.setTag(contact.id);
        Uri imageUri = Uri.parse(contact.photoURI);
        if (!contact.photoURI.isEmpty()) {
            Log.i("Recycler", "" + imageUri);
            holder.circleImageContact.setImageURI(imageUri);
        }else {
            Character character = contact.name.toLowerCase().charAt(0);
            boolean isCharacter=ImageList.getImageMap().containsKey(character);
            if (isCharacter)
                holder.circleImageContact.setImageResource(ImageList.getImageMap().get(character));
            else
                holder.circleImageContact.setImageResource(R.mipmap.none);
        }


    }

    @Override
    public int getItemCount() {
        return finalContactList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        LinearLayout relativeLayoutImage;
        ImageView imageViewCancel;
        CircleImageView circleImageContact;
        TextView textViewNickName;
        public MyViewHolder(View itemView) {
            super(itemView);
            relativeLayoutImage= itemView.findViewById(R.id.relativeLayoutImage);
            imageViewCancel= itemView.findViewById(R.id.imageViewCancel);
            circleImageContact = itemView.findViewById(R.id.circleImageContact);
            textViewNickName = itemView.findViewById(R.id.textViewNickName);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            removeAt(getAdapterPosition());
        }

        private void removeAt(int position) {
            finalContactList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position,finalContactList.size());
            if (finalContactList.size()==0){
                context.finish();
            }
        }
    }


}

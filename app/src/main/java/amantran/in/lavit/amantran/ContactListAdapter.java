package amantran.in.lavit.amantran;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by root on 2/27/18.
 */

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<Contact> contacts;
    private LayoutInflater inflater;
    private LinearLayout linearLayoutHorizontal;
    private HashMap<String,Contact> finalContactList = new HashMap<>();
    private TextView buttonCancel,buttonNext;
    private TextView textViewTotalSelected;

    public ContactListAdapter(final Context context, final ArrayList<Contact> contacts,
                              LinearLayout linearLayoutHorizontal,
                              TextView buttonCancel,
                              TextView buttonNext,
                              TextView textViewTotalSelected) {
        this.buttonCancel = buttonCancel;
        this.buttonNext = buttonNext;
        this.textViewTotalSelected = textViewTotalSelected;
        this.contacts = contacts;
        this.linearLayoutHorizontal = linearLayoutHorizontal;
        this.context = context;
        inflater = LayoutInflater.from(context);
        notifyDataSetChanged();
        this.textViewTotalSelected.setText("0/"+contacts.size());
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Contact> intentContactList = new ArrayList<>(finalContactList.values());
                Intent intent = new Intent(context,NewInvitationActivity.class);
                intent.putExtra("finalList",intentContactList);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public ContactListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ContactListAdapter.MyViewHolder holder, int position) {
        final Contact contact = contacts.get(position);
        holder.bind(position);
        holder.setIsRecyclable(false);
        holder.itemView.setTag(contact.id);
        holder.textViewPhoneNumber.setText(contact.phoneNo);
        holder.textViewPersonName.setText(contact.name);

        Uri imageUri = Uri.parse(contact.photoURI);
        if (!contacts.get(position).photoURI.isEmpty()) {
            Log.i("Recycler", "" + imageUri);
            holder.profile_image.setImageURI(imageUri);
        }else {
            Character character = contact.name.toLowerCase().charAt(0);
            boolean isCharacter=ImageList.getImageMap().containsKey(character);
            if (isCharacter)
            holder.profile_image.setImageResource(ImageList.getImageMap().get(character));
            else
            holder.profile_image.setImageResource(R.mipmap.none);
        }
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }


    private SparseBooleanArray itemStateArray = new SparseBooleanArray();

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CircleImageView profile_image;
        TextView textViewPersonName;
        TextView textViewPhoneNumber;
        CheckBox checkBoxSelect;

        public MyViewHolder(View itemView) {
            super(itemView);
            profile_image = itemView.findViewById(R.id.profile_image);
            textViewPersonName = itemView.findViewById(R.id.textViewPersonName);
            textViewPhoneNumber = itemView.findViewById(R.id.textViewPhoneNumber);
            checkBoxSelect = itemView.findViewById(R.id.checkBoxSelect);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view=inflater.inflate(R.layout.contact_image,null);
            boolean isTrue = false;
            int adapterPosition = getAdapterPosition();
            Contact contact = contacts.get(adapterPosition);
            if (!itemStateArray.get(adapterPosition,false)){
                isTrue = true;
                checkBoxSelect.setChecked(isTrue);
                itemStateArray.put(adapterPosition,isTrue);
            }else {
                isTrue = false;
                checkBoxSelect.setChecked(isTrue);
                itemStateArray.put(adapterPosition,isTrue);
            }


            if (isTrue){
                addView(v,view,adapterPosition,contact);

            }else {
                removeView(v,view,adapterPosition,contact);
            }
        }

        void addView(final View listView, final View view, final int adapterPosition, final Contact contact){
            buttonNext.setVisibility(View.VISIBLE);
            Uri imageUri = Uri.parse(contact.photoURI);
            CircleImageView topImage = view.findViewById(R.id.circleImageContact);
            TextView textViewNickName = view.findViewById(R.id.textViewNickName);
            //Toast.makeText(context, "uri "+contact.photoURI, Toast.LENGTH_SHORT).show();
            textViewNickName.setText(contact.name);
            if (!contact.photoURI.isEmpty()) {
                Log.i("Recycler", "" + imageUri);
                topImage.setImageURI(imageUri);
            }else {
                Character character = contact.name.toLowerCase().charAt(0);
                boolean isCharacter=ImageList.getImageMap().containsKey(character);
                if (isCharacter)
                    topImage.setImageResource(ImageList.getImageMap().get(character));
                else
                    topImage.setImageResource(R.mipmap.none);
            }
            finalContactList.put(contact.id,contact);
            view.setTag(contact.id);
            //Toast.makeText(context, "Add Size "+finalContactList.size(), Toast.LENGTH_SHORT).show();
            linearLayoutHorizontal.addView(view);
            textViewTotalSelected.setText(finalContactList.size()+"/"+contacts.size());
            //Toast.makeText(context, "Added ID "+listView.getTag(), Toast.LENGTH_SHORT).show();
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isTrue = false;

                    if (checkBoxSelect.isChecked()) {
                        if (!itemStateArray.get(adapterPosition,false)){
                            isTrue = true;
                            checkBoxSelect.setChecked(isTrue);
                            itemStateArray.put(adapterPosition,isTrue);
                        }else {
                            isTrue = false;
                            checkBoxSelect.setChecked(isTrue);
                            itemStateArray.put(adapterPosition,isTrue);
                        }


                        if (isTrue){
                            addView(v,view,adapterPosition,contact);

                        }else {
                            removeView(v,view,adapterPosition,contact);
                        }

                    }
                    notifyDataSetChanged();
               }
            });

        }


        void removeView(View listView,View view,int adapterPosition,Contact contact){

            int childCount = linearLayoutHorizontal.getChildCount();

            for (int i=0;i<childCount;i++){
                View circleView = linearLayoutHorizontal.getChildAt(i);
                if (circleView.getTag().toString().equals(listView.getTag().toString())){
                    linearLayoutHorizontal.removeViewAt(i);
                    Log.i("ADAPTER","CircleView "+circleView.getTag().toString());
                    Log.i("ADAPTER","View  "+listView.getTag().toString()+" CircleView "+circleView.getTag().toString());
                    childCount = linearLayoutHorizontal.getChildCount();
                }

            }
            if (childCount == 0){
                buttonNext.setVisibility(View.INVISIBLE);
            }
            finalContactList.remove(contact.id);

            textViewTotalSelected.setText(finalContactList.size()+"/"+contacts.size());
        }

        void bind(int position) {
            // use the sparse boolean array to check
            if (!itemStateArray.get(position, false)) {
                checkBoxSelect.setChecked(false);
            }
            else {
                checkBoxSelect.setChecked(true);
            }

        }
    }
}

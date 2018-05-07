package amantran.in.lavit.amantran;

import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private ProgressBar progressBarLoad;
    private RecyclerView recyclerView;
    private String TAG = "MainActivity";
    private ContactListAdapter adapter;
    private RecyclerView.LayoutManager linearLayoutManager;
    private LinearLayout linearLayoutHorizontal,linearLayoutMain;
    private TextView buttonCancel,buttonNext;
    private TextView textViewTotalSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        linearLayoutMain = findViewById(R.id.linearLayoutMain);
        textViewTotalSelected = findViewById(R.id.textViewTotalSelected);
        buttonCancel = findViewById(R.id.buttonCancel);
        buttonNext = findViewById(R.id.buttonNext);
        linearLayoutHorizontal = findViewById(R.id.linearLayoutHorizontal);
        progressBarLoad = findViewById(R.id.progressBarListLoad);
        recyclerView = findViewById(R.id.recyclerView);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)){
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                mBuilder.setTitle("Read Contact Permission");
                mBuilder.setMessage("We need to read your contact to send Invitaion");
                mBuilder.setPositiveButton("Read it", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_CONTACTS},
                                MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                    }
                });
                mBuilder.setNegativeButton("Cancel",null);
                AlertDialog dialog = mBuilder.create();
                dialog.show();
            }else {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        }else {
            new ContactTask().execute();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS:
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    new ContactTask().execute();
                }else {
                    Snackbar snackbar = Snackbar.make(linearLayoutMain,"Permission Denied..",
                            Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
                break;
        }
    }

    public class ContactTask extends AsyncTask<Void,Void,ArrayList<Contact>>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBarLoad.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<Contact> doInBackground(Void... voids) {
            return getContactList();
        }

        @Override
        protected void onPostExecute(ArrayList<Contact> contacts) {
            super.onPostExecute(contacts);
            progressBarLoad.setVisibility(View.GONE);
            if (contacts!=null){
                Log.i(TAG,"SIZE "+contacts.size());
                if (contacts.size()>0) {
                    adapter = new ContactListAdapter
                            (MainActivity.this, contacts, linearLayoutHorizontal,
                                    buttonCancel, buttonNext, textViewTotalSelected);
                    recyclerView.setAdapter(adapter);
                }else {
                    Snackbar snackbar = Snackbar.make(linearLayoutMain,"No contacts available",
                            Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        }
    }

    private ArrayList<Contact> getContactList() {
        ArrayList<Contact> contactList = new ArrayList<>();
        Set<String> nameSet = new HashSet<>();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));
                String photoURI = cur.getString(cur.getColumnIndex(ContactsContract.Contacts
                                    .PHOTO_THUMBNAIL_URI));
                if (photoURI == null){
                    photoURI = "";
                }

                String phoneNo = "";
                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    if (pCur.moveToNext()) {
                        phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        Log.i(TAG, "Name: " + name);
                        Log.i(TAG, "Phone Number: " + phoneNo);
                        Contact contact = new Contact(id, name, photoURI, phoneNo);
                        contactList.add(contact);
                    }
                    pCur.close();
                }
                Log.i("MainActivity",id);


            }
        }
        if(cur!=null){
            cur.close();
        }

        Collections.sort(contactList, new Comparator<Contact>() {
            @Override
            public int compare(Contact o1, Contact o2) {
                return o1.name.compareTo(o2.name);
            }
        });
        return contactList;
    }
}

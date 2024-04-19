package com.example.contactsfromsqllite;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Objects;


public class ContactAdapter extends ArrayAdapter<Contact> {

    private Context mComtext;
    private int mResource;
    public ContactAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Contact> objects) {
        super(context, resource, objects);
        this.mComtext = context;
        this.mResource = resource;
    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(mComtext);
        convertView = layoutInflater.inflate(mResource, parent, false);
        TextView name = convertView.findViewById(R.id.ContactName);
        TextView phone = convertView.findViewById(R.id.ContactPhone);
        ImageButton delete = convertView.findViewById(R.id.imgBtn);
        delete.setOnClickListener(v -> DeleteContact(getItem(position)));
        name.setText(Objects.requireNonNull(getItem(position)).getName());
        phone.setText(Objects.requireNonNull(getItem(position)).getPhone());
        return convertView;
    }

    private void DeleteContact(Contact contact) {
        try {
            long id = Long.parseLong(contact.getId()); //id of row that you want to delete
            Uri deleteUri = ContentUris.withAppendedId(ContactsContract.Data.CONTENT_URI, id);
            mComtext.getContentResolver().delete(deleteUri, null, null);
            Toast.makeText(this.getContext(), "Contact " + contact.getName() + " deleted", Toast.LENGTH_SHORT).show();
            this.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this.getContext(), "Error deleting contact: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}

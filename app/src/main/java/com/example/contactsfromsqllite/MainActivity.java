package com.example.contactsfromsqllite;

import android.annotation.SuppressLint;
import android.content.ContentResolver;

import android.Manifest;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.cursoradapter.widget.SimpleCursorAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Cursor cursor;
    ListView listView;
    Button button;
    EditText name;
    EditText phone;
    ArrayList<Contact> list = new ArrayList<>();

    private static final int REQUEST_CODE_READ_CONTACTS=1;
    private static boolean READ_CONTACTS_GRANTED =false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.ListView);
        button = findViewById(R.id.Button);
        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);

        // получаем разрешения
        int hasReadContactPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        // если устройство до API 23, устанавливаем разрешение
        if(hasReadContactPermission == PackageManager.PERMISSION_GRANTED){
            READ_CONTACTS_GRANTED = true;
        }
        else{
            // вызываем диалоговое окно для установки разрешений
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS}, REQUEST_CODE_READ_CONTACTS);
        }
        // если разрешение установлено, загружаем контакты
        if (READ_CONTACTS_GRANTED) {
            getContacts();
            button.setEnabled(READ_CONTACTS_GRANTED);
        }
         //set OnClickListener() to the button
        button.setOnClickListener(v -> {
            if (!name.getText().toString().isEmpty() && !phone.getText().toString().isEmpty())
                AddContact(v);
        });

    }

    public void getContacts() {
            cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            list.clear();
            if(cursor!=null){
                while (cursor.moveToNext()) {
                    @SuppressLint("Range") String id = cursor.getString(
                            cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));
                    @SuppressLint("Range") String dbname = cursor.getString(
                            cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    @SuppressLint("Range") String dbphone = cursor.getString(
                            cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    list.add(new Contact(id, dbname, dbphone));
                }
                cursor.close();
            }
            ContactAdapter adapter = new ContactAdapter(this, R.layout.activity_contacts, list);
            listView.setAdapter(adapter);
            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        }

    public void AddContact(View v) {
        String newName = name.getText().toString();
        String newPhone = phone.getText().toString();

        Uri newUri = getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, new ContentValues());
        long rawContactsId = ContentUris.parseId(newUri);
        ContentValues values = new ContentValues();
        /* Связываем наш аккаунт с данными */
        values.put(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, rawContactsId);
        /* Устанавливаем MIMETYPE для поля данных */
        values.put(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        /* Имя для нашего аккаунта */
        values.put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, newName);
        getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
        values.clear();
        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactsId);
        /* Тип данных – номер телефона */
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        /* Номер телефона */
        values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, newPhone);
        /* Тип – мобильный */
        values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
        getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
        Toast.makeText(getApplicationContext(), newName + " added to the contacts list", Toast.LENGTH_LONG).show();
        name.setText("");
        phone.setText("");
        getContacts();
    }

}
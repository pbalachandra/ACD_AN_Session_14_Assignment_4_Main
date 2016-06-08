package com.acadgild.balu.acd_an_session_14_assignment_4_main;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    EditText editText_name;
    Button button_delete;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText_name = (EditText) findViewById(R.id.editText_name);

        button_delete = (Button) findViewById(R.id.button_delete);

        button_delete.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        if (editText_name.getText().toString().isEmpty())
        {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.empty),
                    Toast.LENGTH_LONG).show();
        }
        else {
            Delete_Contact(editText_name.getText().toString());
        }
    }

    private void Delete_Contact(String name)
    {
        ContentResolver contentResolver = getContentResolver();

        String where = ContactsContract.Data.DISPLAY_NAME + " = ? AND " +
                ContactsContract.Data.MIMETYPE + " = ? AND " +
                String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE) + " = ? ";
        String[] params = new String[]{name,
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
                String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_HOME)};

        Cursor cursor = managedQuery(ContactsContract.Data.CONTENT_URI, null, where, params, null);

        ArrayList<ContentProviderOperation> contentProvider = new ArrayList<>();
        if (cursor.getCount() <= 0)
        {
            Toast.makeText(getApplicationContext(),
                    String.format(getResources().getString(R.string.warning), name),
                    Toast.LENGTH_LONG).show();
            editText_name.setText("");
            return;
        }
        else
        {
            where = ContactsContract.Data.DISPLAY_NAME + " = ? ";
            params = new String[] {name};
            contentProvider.add(ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI)
                    .withSelection(where, params)
                    .build());
            try {
                contentResolver.applyBatch(ContactsContract.AUTHORITY, contentProvider);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (OperationApplicationException e) {
                e.printStackTrace();
            }
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.message),
                    Toast.LENGTH_LONG).show();
            editText_name.setText("");
        }
        cursor.close();

    }

}

package com.example.fivecontacts.main.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.example.fivecontacts.R;
import com.example.fivecontacts.main.model.Contato;
import com.example.fivecontacts.main.model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;

public class AlterarContatos_Activity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {


    EditText edtNome;
    ListView lv;
  //  Button btSalvar;
  BottomNavigationView bnv;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alterar_contatos);
       // tv =findViewById(R.id.MessageIntent);
        edtNome = findViewById(R.id.edtBusca);
        bnv = findViewById(R.id.bnv);
        bnv.setOnNavigationItemSelectedListener(this);
        bnv.setSelectedItemId(R.id.anvMudar);

        //Dados da Intent Anterior
        Intent quemChamou=this.getIntent();
        if (quemChamou!=null) {
            Bundle params = quemChamou.getExtras();
            if (params!=null) {
                //Recuperando o Usuario
                user = (User) params.getSerializable("usuario");
               // if (user != null) {
                 //   tv.setText(user.getNome());
                //}
            }
        }

        lv = findViewById(R.id.listContatosDoCell);


        /*
        btSalvar = findViewById(R.id.btSalvar);

        btSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {






                //Evento do Botão Salvar Vamos Salvar Contatos e Telefones em SharedPreferences"

              //  mostrarListaDeContatos();

              //salvarContatosSerializados();





            }
        });
*/
    }

   // private void preencherListaDeContatos() {



   // }

    public void salvarContato (Contato w){
          Contato c, k, z;
          c= new Contato();
          c.setNumero("tel:+141414141");
          c.setNome("Peppa");
        k= new Contato();
        k.setNumero("tel:+242414141");
        k.setNome("George");

        z= new Contato();
        z.setNumero("tel:+342414141");
        z.setNome("Richard");

        SharedPreferences salvaContatos =
                getSharedPreferences("contatos2",Activity.MODE_PRIVATE);

        SharedPreferences.Editor editor = salvaContatos.edit();

        editor.putInt("numContatos",4);


        try {
            ByteArrayOutputStream dt = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(dt);
            oos.writeObject(c);
            String contatoSerializado= dt.toString(StandardCharsets.ISO_8859_1.name());
            editor.putString("contato1", contatoSerializado);


            dt = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(dt);
            oos.writeObject(k);

            contatoSerializado= dt.toString(StandardCharsets.ISO_8859_1.name());
            editor.putString("contato2", contatoSerializado);

            dt = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(dt);
            oos.writeObject(z);

           contatoSerializado= dt.toString(StandardCharsets.ISO_8859_1.name());
            editor.putString("contato3", contatoSerializado);

            dt = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(dt);
            oos.writeObject(w);
            contatoSerializado= dt.toString(StandardCharsets.ISO_8859_1.name());
            editor.putString("contato4", contatoSerializado);



        }catch(Exception e){
            e.printStackTrace();
        }

        editor.commit();

     finish();




    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.v("PDM","Matando a Activity Lista de Contatos");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v("PDM","Matei a Activity Lista de Contatos");
    }


    public void onClickBuscar(View v){

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED) {

            Log.v("PDM", "Pedir permissão");
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 3333);
            return;
        }
        Log.v("PDM", "Tenho permissão");

        ContentResolver cr = getContentResolver();

        String consulta = ContactsContract.Contacts.DISPLAY_NAME + " LIKE ?";

        String [] argumentosConsulta= {"%"+edtNome.getText()+"%"};

        Cursor cursor= cr.query(ContactsContract.Contacts.CONTENT_URI, null,
                consulta,argumentosConsulta, null);


        String[] nomesSP = new String[cursor.getCount()];
        Log.v("PDM","Tamanho do cursor:"+cursor.getCount());
         final Contato c = new Contato();
        int i=0; int y=0;
        while (cursor.moveToNext()) {
            i++;

            int indiceNome = cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME);
            String contatoNome = cursor.getString(indiceNome);
            Log.v("PDM", "Contato " + i + ", Nome:" + contatoNome);

            c.setNome(contatoNome);
            nomesSP[y]=contatoNome;
            Log.v("PDM", "Contato " + y + ", Nome:+"+nomesSP[y]);
            y++;
            int indiceContatoID = cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID);
            String contactID = cursor.getString(indiceContatoID);

            String consultaPhone = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactID;

            Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null, consultaPhone, null, null);

            int j = 0;
            while (phones.moveToNext()) {
                j++;

                String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                c.setNumero("tel:+"+number);
                Log.v("PDM", " Telefone (" + j + " " + number);


            }



        }

        if (nomesSP !=null) {
            if(nomesSP.length>=1) {
                Log.v("pdm","Nome:"+nomesSP[0]);
                ArrayAdapter<String> adaptador;

                adaptador = new ArrayAdapter<String>(this, R.layout.list_view_layout, nomesSP);

                lv.setAdapter(adaptador);
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        salvarContato(c);
                        Intent intent = new Intent(getBaseContext(), ListaDeContatos_Activity.class);
                        intent.putExtra("usuario", user);
                        startActivity(intent);

                    }
                });
            }
        }

         /*   SharedPreferences salvaContatos =
                    getSharedPreferences("contatos2",Activity.MODE_PRIVATE);

            SharedPreferences.Editor editor = salvaContatos.edit();

            editor.putInt("numContatos",4);


            try {
                ByteArrayOutputStream dt = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(dt);
                oos.writeObject(c);
                String contatoSerializado = dt.toString(StandardCharsets.ISO_8859_1.name());
                editor.putString("contato4", contatoSerializado);

                editor.commit();

            }catch (Exception e){




            }

*/




    }





    protected int mostrarListaDeContatos() {


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED) {

            Log.v("PDM", "Pedir permissão");
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 3333);
            return 0;
        }
        ;

        Log.v("PDM", "Tenho permissão");

        //Projection, os campos que você quer
        String[] projection = new String[]
                {
                        ContactsContract.Profile._ID,
                        ContactsContract.Profile.DISPLAY_NAME_PRIMARY,
                        ContactsContract.Profile.LOOKUP_KEY,
                        ContactsContract.Profile.PHOTO_THUMBNAIL_URI
                };

        String selection = ContactsContract.Contacts.DISPLAY_NAME + " LIKE ?";
        String[] selectionArguments= {"K%"};

        ContentResolver cr = getContentResolver();
      //  Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null,null, null, null);
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI,projection,selection,selectionArguments, null);

        int indexDisplayName = cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME);

        while (cursor.moveToNext()) {
            //   String given = cursor.getString(indexGivenName);
            //  String family = cursor.getString(indexFamilyName);
            String display = cursor.getString(indexDisplayName);
            //Ler nome
            String contatoNome = cursor.getString(indexDisplayName);
            Log.v("PDM", "Nome do Contato:" + contatoNome);//+","+given+","+"family");
            String contactId =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            //
            //  Get all phone numbers.
            //


            Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
            while (phones.moveToNext()) {
                String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                int type = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                switch (type) {
                    case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                        // do something with the Home number here...
                        Log.v("PDM", "Tel do (" + contactId + "): " + contatoNome + ": " + number);
                        break;
                    case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                        // do something with the Mobile number here...
                        Log.v("PDM", "Tel do (" + contactId + "): " + contatoNome + ": " + number);
                        break;
                    case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                        // do something with the Work number here...
                        Log.v("PDM", "Tel do (" + contactId + "): " + contatoNome + ": " + number);

                        break;
                }
            }
            phones.close();
        }


        cursor.close();
        return 1;
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Checagem de o Item selecionado é o do perfil
        if (item.getItemId() == R.id.anvPerfil) {
            //Abertura da Tela MudarDadosUsario
            Intent intent = new Intent(this, PerfilUsuario_Activity.class);
            intent.putExtra("usuario", user);
            startActivity(intent);

        }
        // Checagem de o Item selecionado é o do perfil
        if (item.getItemId() == R.id.anvLigar) {
            //Abertura da Tela Mudar COntatos
            Intent intent = new Intent(this, ListaDeContatos_Activity.class);
            intent.putExtra("usuario", user);
            startActivity(intent);

        }
        return true;
    }
}
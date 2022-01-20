package com.example.fivecontacts.main.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.fivecontacts.R;
import com.example.fivecontacts.main.model.Contato;
import com.example.fivecontacts.main.model.User;
import com.example.fivecontacts.main.utils.Rotation;
import com.example.fivecontacts.main.utils.UIEducacionalPermissao;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AlterarContatos_Activity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    Boolean primeiraVezUser=true;
    EditText edtNome;
    ListView lv;
    BottomNavigationView bnv;
    User user;
    ImageButton btnFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alterar_contatos);
        edtNome = findViewById(R.id.edtBusca);
        bnv = findViewById(R.id.bnv);
        bnv.setOnNavigationItemSelectedListener(this);
        bnv.setSelectedItemId(R.id.anvMudar);

        btnFoto = (ImageButton) findViewById(R.id.barcodeButton);

        //Dados da Intent Anterior
        Intent quemChamou=this.getIntent();
        if (quemChamou!=null) {
            Bundle params = quemChamou.getExtras();
            if (params!=null) {
                //Recuperando o Usuario
                user = (User) params.getSerializable("usuario");
                setTitle("Alterar Contatos de Emergência");
            }
        }
        lv = findViewById(R.id.listView1);

        // Buscar assim que iniciar a página
        Buscar();
    }

    public boolean salvarContato (Contato w){
        ArrayList<Contato> conts = user.getContatos();
        for(Iterator<Contato> i = conts.iterator(); i.hasNext(); ) {
            Contato c = i.next();
            if (c.getNome().equals(w.getNome())) {
                Toast.makeText(this, "Nome de contato já existe", Toast.LENGTH_LONG).show();
                return false;
            }
        }
        SharedPreferences salvaContatos =
                getSharedPreferences("contatos",Activity.MODE_PRIVATE);

        int num = salvaContatos.getInt("numContatos", 0); //checando quantos contatos já tem
        SharedPreferences.Editor editor = salvaContatos.edit();
        // Salvando contato e adicionando ao objeto User
        try {
            ByteArrayOutputStream dt = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(dt);
            dt = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(dt);
            oos.writeObject(w);
            String contatoSerializado= dt.toString(StandardCharsets.ISO_8859_1.name());
            editor.putString("contato"+(num+1), contatoSerializado);
            editor.putInt("numContatos",num+1);
        }catch(Exception e){
            e.printStackTrace();
        }
        editor.commit();
        user.getContatos().add(w);

        return true;
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

    public void tirarFoto() {
        // Captura de foto
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            Log.v("PDM", "Pedir permissão");
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 4444);
            return;
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 1110);
        }


    }

    public void Buscar() {
        // Operação de busca de contatos do celular e exibição na tela
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
        final String[] nomesContatos = new String[cursor.getCount()];
        final String[] telefonesContatos = new String[cursor.getCount()];
        Log.v("PDM","Tamanho do cursor:"+cursor.getCount());

        int i=0;
        while (cursor.moveToNext()) {
            int indiceNome = cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME);
            String contatoNome = cursor.getString(indiceNome);
            Log.v("PDM", "Contato " + i + ", Nome:" + contatoNome);
            nomesContatos[i]= contatoNome;
            int indiceContatoID = cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID);
            String contactID = cursor.getString(indiceContatoID);
            String consultaPhone = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactID;
            Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null, consultaPhone, null, null);

            while (phones.moveToNext()) {
                String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                telefonesContatos[i]=number; //Salvando só último telefone
            }
            i++;
        }

        if (nomesContatos !=null) {
            for(int j=0; j<=nomesContatos.length; j++) {
                ArrayAdapter<String> adaptador;
                adaptador = new ArrayAdapter<String>(this, R.layout.list_view_layout, nomesContatos);
                lv.setAdapter(adaptador);
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Contato c= new Contato();
                        c.setNome(nomesContatos[i]);
                        c.setNumero("tel:+"+telefonesContatos[i]);
                        boolean res = salvarContato(c);
                        if(res) {
                            Intent intent = new Intent(getApplicationContext(), ListaDeContatos_Activity.class);
                            intent.putExtra("usuario", user);
                            startActivity(intent);
                            finish();
                        }

                    }
                });
            }
        }
    }


    public void onClickBuscar(View v){
        Buscar();
    }

    public void onClickFoto(View v){
        tirarFoto();
    }

    public void notifyBarcode(boolean b) {
        if(b) {
            Toast.makeText(this, "Contato adicionado", Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this, "QR Code inválido", Toast.LENGTH_LONG).show();
        }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case 3333:
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    // Pesquisar assim que permissão for concedida
                    Buscar();
                }
                break;
            case 4444:
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    // Tirar foto assim que permissão for concedida
                    tirarFoto();
                }
                break;
        }
    }

    public void analyze(Bitmap imageMedia) {
        try {
            InputImage image =
                    InputImage.fromBitmap(imageMedia, Rotation.getRotationCompensation("0", this, true));
            scanBarcodes(image);
        }catch(Exception e) {
            Log.v("PDM", "Erro no analyze");
        }
    }

    private void scanBarcodes(InputImage image) {

        BarcodeScannerOptions options =
                new BarcodeScannerOptions.Builder()
                        .setBarcodeFormats(
                                Barcode.FORMAT_QR_CODE,
                                Barcode.FORMAT_AZTEC)
                        .build();

        BarcodeScanner scanner = BarcodeScanning.getClient();

        Task<List<Barcode>> result = scanner.process(image)
                .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                    @Override
                    public void onSuccess(List<Barcode> barcodes) {
                        Log.v("PDM","Sucesso Barcode");

                        for (Barcode barcode: barcodes) {
                            Rect bounds = barcode.getBoundingBox();
                            Point[] corners = barcode.getCornerPoints();

                            String rawValue = barcode.getRawValue();

                            int valueType = barcode.getValueType();
                            // See API reference for complete list of supported types
                            switch (valueType) {
                                case Barcode.TYPE_CONTACT_INFO:
                                    Log.v("PDM","Tipo ContactInfo");
                                    Barcode.ContactInfo cont = barcode.getContactInfo();
                                    String cNome = cont.getName().getFirst();
                                    String cNumber = cont.getPhones().get(0).getNumber();
                                    Contato c= new Contato();
                                    c.setNome(cNome);
                                    c.setNumero("tel:+"+cNumber);
                                    boolean res = salvarContato(c);
                                    if(res) {
                                        notifyBarcode(true);
                                        Intent intent = new Intent(getApplicationContext(), ListaDeContatos_Activity.class);
                                        intent.putExtra("usuario", user);
                                        startActivity(intent);
                                        finish();
                                    }
                                    break;
                                default:
                                    notifyBarcode(false);
                                    break;
                            }
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.v("PDM","Falha Barcode");

                    }
                });

    }

    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1110) {
            Log.v("PDM", "resultado: "+resultCode);
            Bitmap captureImage = (Bitmap) data.getExtras().get("data");
            analyze(captureImage);
        }
    }
}
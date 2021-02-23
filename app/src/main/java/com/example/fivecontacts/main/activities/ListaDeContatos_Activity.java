package com.example.fivecontacts.main.activities;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.fivecontacts.R;
import com.example.fivecontacts.main.model.Contato;
import com.example.fivecontacts.main.model.User;
import com.example.fivecontacts.main.utils.UIEducacionalPermissao;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class ListaDeContatos_Activity extends AppCompatActivity implements UIEducacionalPermissao.NoticeDialogListener, BottomNavigationView.OnNavigationItemSelectedListener {

    ListView lv;
    BottomNavigationView bnv;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_de_contatos);

        bnv = findViewById(R.id.bnv);
        bnv.setOnNavigationItemSelectedListener(this);
        bnv.setSelectedItemId(R.id.anvLigar);


        lv = findViewById(R.id.listView1);
        preencherListaDeContatos(); //Montagem do ListView


        //Dados da Intent Anterior
        Intent quemChamou = this.getIntent();
        if (quemChamou != null) {
            Bundle params = quemChamou.getExtras();
            if (params != null) {
                //Recuperando o Usuario
                user = (User) params.getSerializable("usuario");
                if (user != null) {

                    setTitle("Contatos de Emergência de "+user.getNome());
                }
            }
        }

    }

    protected void preencherListaDeContatos() {

        SharedPreferences recuperarContatos = getSharedPreferences("contatos", Activity.MODE_PRIVATE);

        int num = recuperarContatos.getInt("numContatos", 0);
        final ArrayList<Contato> contatos = new ArrayList<Contato>();

        Contato contato;


        for (int i = 1; i <= num; i++) {
            String objSel = recuperarContatos.getString("contato" + i, "");
            if (objSel.compareTo("") != 0) {
                try {
                    ByteArrayInputStream bis =
                            new ByteArrayInputStream(objSel.getBytes(StandardCharsets.ISO_8859_1.name()));
                    ObjectInputStream oos = new ObjectInputStream(bis);
                    contato = (Contato) oos.readObject();

                    if (contato != null) {
                        contatos.add(contato);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }


        }


        if (contatos != null) {
            final String[] nomesSP, telsSP;
            nomesSP = new String[contatos.size()];
            Contato c;
            for (int i = 0; i < contatos.size(); i++) {
                nomesSP[i] = contatos.get(i).getNome();
            }

            ArrayAdapter<String> adaptador;

            adaptador = new ArrayAdapter<String>(this, R.layout.list_view_layout, nomesSP);

            lv.setAdapter(adaptador);


            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    if (checarPermissaoPhone_SMD()) {

                        Uri uri = Uri.parse(contatos.get(i).getNumero());
                      //   Intent itLigar = new Intent(Intent.ACTION_DIAL, uri);
                        Intent itLigar = new Intent(Intent.ACTION_CALL, uri);
                        startActivity(itLigar);
                    }


                }
            });
        }//fim do IF do tamanho de contatos
    }

    protected boolean checarPermissaoPhone_SMD(){

      if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
      == PackageManager.PERMISSION_GRANTED){

          Log.v ("SMD","Tenho permissão");

          return true;

      } else {

            if ( shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)){

                Log.v ("SMD","Primeira Vez");


                String mensagem = "Nossa aplicação precisa acessar o telefone para discagem automática. Uma janela de permissão será solicitada";
                String titulo = "Permissão de acesso a chamadas";
                int codigo =1;
                UIEducacionalPermissao mensagemPermissao = new UIEducacionalPermissao(mensagem,titulo, codigo);

                mensagemPermissao.onAttach ((Context)this);
                mensagemPermissao.show(getSupportFragmentManager(), "primeiravez2");




            }else{


                String mensagem = "Nossa aplicação precisa acessar o telefone para discagem automática. Uma janela de permissão será solicitada";
                String titulo = "Permissão de acesso a chamadas II";
                int codigo =1;

                UIEducacionalPermissao mensagemPermissao = new UIEducacionalPermissao(mensagem,titulo, codigo);

                mensagemPermissao.onAttach ((Context)this);
                mensagemPermissao.show(getSupportFragmentManager(), "segundavez2");


                Log.v ("SMD","Outra Vez");

            }


      }








        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                            int[] grantResults) {
        switch (requestCode) {
            case 2222:
               // Toast.makeText(this, "VOLTOU DA JANELA DE PERMISSÃO", Toast.LENGTH_LONG).show();
               if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                   Toast.makeText(this, "VALEU", Toast.LENGTH_LONG).show();







               }else{
                   Toast.makeText(this, "SEU FELA!", Toast.LENGTH_LONG).show();

                   String mensagem= "Seu aplicativo pode ligar diretamente, mas sem permissão não funciona. Se você marcou não perguntar mais, você deve ir na tela de configurações para mudar a instalação ou reinstalar o aplicativo  ";
                   String titulo= "Porque precisamos telefonar?";
                   UIEducacionalPermissao mensagemPermisso = new UIEducacionalPermissao(mensagem,titulo,2);
                   mensagemPermisso.onAttach((Context)this);
                   mensagemPermisso.show(getSupportFragmentManager(), "segundavez");
               }
                break;
        }
    }
            @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Checagem de o Item selecionado é o do perfil
        if (item.getItemId() == R.id.anvPerfil) {
            //Abertura da Tela MudarDadosUsario
            Intent intent = new Intent(this, PerfilUsuario_Activity.class);
            intent.putExtra("usuario", user);
            startActivityForResult(intent, 1111);

        }
        // Checagem de o Item selecionado é o do perfil
        if (item.getItemId() == R.id.anvMudar) {
            //Abertura da Tela Mudar COntatos
            Intent intent = new Intent(this, AlterarContatos_Activity.class);
            intent.putExtra("usuario", user);
            startActivityForResult(intent, 1112);

        }
        return true;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Caso seja um Voltar ou Sucesso selecionar o item Ligar

        if (requestCode == 1111) {//Retorno de Mudar Perfil
            bnv.setSelectedItemId(R.id.anvLigar);
        }

        if (requestCode == 1112) {//Retorno de Mudar Contatos
            bnv.setSelectedItemId(R.id.anvLigar);
            preencherListaDeContatos();
        }



    }

    protected ArrayList<Contato> montarListaDeContatosPorSerializacaoJava() {
        SharedPreferences salvaContatos = getSharedPreferences("contatos", Activity.MODE_PRIVATE);
        int num = salvaContatos.getInt("numContatos", 0);

        ArrayList<Contato> contatos = new ArrayList<Contato>();

        Contato c;
        for (int i = 1; i <= num; i++) {
            String objSel = salvaContatos.getString("contato" + i, "");
            if (objSel.compareTo("") != 0) {
                try {
                    ByteArrayInputStream dis = new ByteArrayInputStream(objSel.getBytes(StandardCharsets.ISO_8859_1.name()));
                    ObjectInputStream oos = new ObjectInputStream(dis);
                    c = (Contato) oos.readObject();
                    if (c != null) {
                        contatos.add(c);
                        //  Log.v("PDM",c.getNome());
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

            }


        }


        return contatos;
    }

    @Override
    public void onDialogPositiveClick(int codigo) {

        if (codigo==1){
          String[] permissions ={Manifest.permission.CALL_PHONE};
          requestPermissions(permissions, 2222);

        }
        //Log.v ("SMD","Clicou no OK");


    }


    // @Override
  //  public void onDialogPositiveClick( int codigo) {

  //      if (codigo==1) { // Primeira Vez
  //          String[] permissoes = {Manifest.permission.CALL_PHONE};
  //          requestPermissions(permissoes, 1212);
  //      }
   //     if (codigo==2) { // Não deu permissão :(
//
   //     }
  //  }
}

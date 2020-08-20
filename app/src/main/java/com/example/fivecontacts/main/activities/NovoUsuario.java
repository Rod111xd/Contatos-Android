package com.example.fivecontacts.main.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.fivecontacts.R;

public class NovoUsuario extends AppCompatActivity {

    boolean primeiraVezUser=true;
    boolean primeiraVezSenha=true;
    EditText edUser;
    EditText edPass;
    EditText edNome;
    Button btCriar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_usuario);

        btCriar=findViewById(R.id.btCriar);
        edUser=findViewById(R.id.edT_Login2);
        edPass=findViewById(R.id.edt_Pass2);
        edNome=findViewById(R.id.edtNome);

        //Evento de limpar Componente
        edUser.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (primeiraVezUser){
                    primeiraVezUser=false;
                    edUser.setText("");
                }

                return false;
            }
        });
        //Evento de limpar Componente

        edPass.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (primeiraVezSenha){
                    primeiraVezSenha=false;
                    edPass.setText("");
                    edPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD
                    );
                }
                return false;
            }
        });

        //Evento de limpar Componente - E-mail
        //TO-DO

        //Evento de limpar Componente - Nome
        //TO-DO

        btCriar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String nome, login, senha;
                nome = edNome.getText().toString();
                login = edUser.getText().toString();
                senha = edPass.getText().toString();

                SharedPreferences salvaUser= getSharedPreferences("usuarioPadrao", Activity.MODE_PRIVATE);
                SharedPreferences.Editor escritor= salvaUser.edit();

                escritor.putString("nome",nome);
                escritor.putString("senha",senha);
                escritor.putString("login",login);

                //Falta Salvar o E-mail

                escritor.commit(); //Salva em Disco


                Intent intent=new Intent(NovoUsuario.this,Pick_Contacts.class);
                startActivity(intent);

                //Mesmo após a chamar de um startActivity o método continuará execuntando
                //Por exemplo, aqui mataremos a Activity atual porém a Pick_Contacts é aqui será exibida
                Log.v("PDMv2","passei do StartActivity");
                finish();
            }
        });
    }
}
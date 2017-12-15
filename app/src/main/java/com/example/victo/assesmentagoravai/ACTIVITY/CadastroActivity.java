package com.example.victo.assesmentagoravai.ACTIVITY;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.victo.assesmentagoravai.DAO.ConfiguracaoFirebase;
import com.example.victo.assesmentagoravai.ENTITIES.Usuarios;
import com.example.victo.assesmentagoravai.HELPER.Base64Custom;
import com.example.victo.assesmentagoravai.HELPER.PreferenciasAndroid;
import com.example.victo.assesmentagoravai.R;
import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.Min;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.util.List;

public class CadastroActivity extends AppCompatActivity implements Validator.ValidationListener  {

    Validator validator;

    @NotEmpty(message = "Este campo não pode ficar vazio")
    private EditText etCadNome;

    @NotEmpty(message = "Este campo não pode ficar vazio")
    @Email(message = "Digite um email valido")
    private EditText etCadEmail;

    @NotEmpty(message = "Este campo não pode ficar vazio")
    @Password
    private EditText etCadSenha;

    @NotEmpty(message = "Este campo não pode ficar vazio")
    @ConfirmPassword (message = "Os campos Senha e Confirmar Senha devem ser iguais!")
    private EditText etCadConfirmarSenha;

    @NotEmpty(message = "Este campo não pode ficar vazio")
    @Min(11)
    private EditText etCadCpf;


    private Button btnCadastrar, btnLimpar;
    private Usuarios usuarios;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);


        validator = new Validator(this);
        validator.setValidationListener(this);


        etCadNome = (EditText) findViewById(R.id.etCadNome);
        etCadEmail = (EditText) findViewById(R.id.etCadEmail);
        etCadSenha = (EditText) findViewById(R.id.etCadSenha);
        etCadConfirmarSenha = (EditText) findViewById(R.id.etCadConfirmarSenha);
        etCadCpf = (EditText) findViewById(R.id.etCadCpf);
        btnCadastrar = (Button) findViewById(R.id.btnCadastrar);
        btnLimpar = (Button) findViewById(R.id.btnLimpar);


        //criando mascara para o cpf
        SimpleMaskFormatter smf = new SimpleMaskFormatter("NNN.NNN.NNN-NN");
        MaskTextWatcher mtw = new MaskTextWatcher(etCadCpf, smf);
        etCadCpf.addTextChangedListener(mtw);


        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validator.validate();
            }
        });


        btnLimpar.setOnClickListener(new View.OnClickListener() {
            @Override
                public void onClick(View v) {
                //limpar os campos edit text
                etCadNome.setText("");
                etCadEmail.setText("");
                etCadSenha.setText("");
                etCadConfirmarSenha.setText("");
                etCadCpf.setText("");
                }
        });

    }

    private void cadastrarUsuario(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
                usuarios.getEmail(),
                usuarios.getSenha()
        ).addOnCompleteListener(CadastroActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(CadastroActivity.this, "Usuario Cadastrado com sucesso!", Toast.LENGTH_LONG).show();

                    String identificadorUsuario = Base64Custom.codificarBase64(usuarios.getEmail());
                    FirebaseUser usuarioFirebase = task.getResult().getUser();
                    usuarios.setId(identificadorUsuario);
                    usuarios.salvar();

                    PreferenciasAndroid preferenciasAndroid = new PreferenciasAndroid(CadastroActivity.this);
                    preferenciasAndroid.salvarUsuarioPreferencias(identificadorUsuario, usuarios.getNome());
                    abrirLoginUsuario();
                } else {

                    String erroExcecao = "";

                    try {
                        throw task.getException();

                    } catch (FirebaseAuthWeakPasswordException e) {
                        erroExcecao = "Digite uma senha mais forte, contendo no mínimo 8 caracteres entre letras e números";

                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        erroExcecao = "O email digitado é inválido , digite um novo email.";

                    } catch (FirebaseAuthUserCollisionException e) {
                        erroExcecao = "Esse email já está cadastrado no sistema";

                    } catch (Exception e) {
                        erroExcecao = "Erro ao efetuar o cadastro";
                        e.printStackTrace();
                    }
                    Toast.makeText(CadastroActivity.this, "Erro: " + erroExcecao, Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    private void abrirLoginUsuario(){
        Intent intent = new Intent(CadastroActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }



    @Override
    public void onValidationSucceeded() {
        if (etCadSenha.getText().toString().equals(etCadConfirmarSenha.getText().toString())) {


            usuarios = new Usuarios();
            usuarios.setNome(etCadNome.getText().toString());
            usuarios.setEmail(etCadEmail.getText().toString());
            usuarios.setSenha(etCadSenha.getText().toString());
            usuarios.setCpf(etCadCpf.getText().toString());


            cadastrarUsuario();
        } else {
            Toast.makeText(CadastroActivity.this, "As Senhas não são correspondentes", Toast.LENGTH_LONG).show();

        }


    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            if (view instanceof EditText) {

                ((EditText) view).setError(message);

            } else {
                Toast.makeText(CadastroActivity.this, message, Toast.LENGTH_LONG).show();
            }

        }
    }
}

package me.caelumterrae.parsetagram;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class LoginActivity extends AppCompatActivity {
    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        btnLogin.setOnClickListener(new loginListener());
        btnRegister.setOnClickListener(new registerListener());

        ParseUser currentUser = ParseUser.getCurrentUser();

        if(currentUser != null){
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        }
    }

    private class loginListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();

            ParseUser.logInInBackground(username, password, new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if (user != null){
                        //Pass to the main view
                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(i);
                    }else{
                        //TODO: Render failure message or show an error to the user
                        Toast.makeText(LoginActivity.this, "Failed to Login", Toast.LENGTH_LONG);
                        Log.d("loginbutton", "it doesn't work");
                    }
                }
            });
        }
    }

    private class registerListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            final String username = etUsername.getText().toString();
            final String password = etPassword.getText().toString();

            ParseUser user = new ParseUser();
            user.setUsername(username);
            user.setPassword(password);

            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null){
                        // automatically bring the user to the main view when they register
                        ParseUser.logInInBackground(username, password, new LogInCallback() {
                            @Override
                            public void done(ParseUser user, ParseException e) {
                                if (user != null){
                                    //Pass to the main view
                                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(i);
                                }else{
                                    //TODO: Render failure message or show an error to the user
                                    Toast.makeText(LoginActivity.this, "Failed to Login", Toast.LENGTH_LONG);
                                    Log.d("loginbutton", "it doesn't work");
                                }
                            }
                        });
                    }else{
                        Toast.makeText(LoginActivity.this, "Failed to Register", Toast.LENGTH_LONG);
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}

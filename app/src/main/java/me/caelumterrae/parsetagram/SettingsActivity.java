package me.caelumterrae.parsetagram;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.parse.ParseUser;

public class SettingsActivity extends AppCompatActivity {
    Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new LogoutListener());
        ParseUser currentUser = ParseUser.getCurrentUser();

        if (currentUser == null){
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
        }
    }

    private class LogoutListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            ParseUser.logOut();
            Intent i = new Intent(SettingsActivity.this, LoginActivity.class);
            startActivity(i);
        }
    }
}

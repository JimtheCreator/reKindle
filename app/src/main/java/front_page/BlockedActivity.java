package front_page;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.window.OnBackInvokedDispatcher;

import devydev.mirror.net.R;

public class BlockedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocked);

        findViewById(R.id.fi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finishIt();
            }
        });
    }

    private void finishIt() {
        // Start an intent to go back to the home screen
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(homeIntent);
    }

    @Override
    public void onBackPressed() {
        finishIt();
        Toast.makeText(this, "FINISH AFFINITY", Toast.LENGTH_SHORT).show();
    }

    @NonNull
    @Override
    public OnBackInvokedDispatcher getOnBackInvokedDispatcher() {
        finishIt();
        return super.getOnBackInvokedDispatcher();
    }
}
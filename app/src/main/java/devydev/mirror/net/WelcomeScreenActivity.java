package devydev.mirror.net;

import static android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsetsController;
import android.view.WindowManager;



public class WelcomeScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        WindowInsetsController controller = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            controller = getWindow().getDecorView().getWindowInsetsController();

            if (controller != null) {
                controller.setSystemBarsAppearance(
                        APPEARANCE_LIGHT_STATUS_BARS,
                        APPEARANCE_LIGHT_STATUS_BARS
                );
            }
        }else{
            // Set the status bar color
            getWindow().setStatusBarColor(Color.WHITE);

            // Use the SYSTEM_UI_FLAG_LIGHT_STATUS_BAR flag to make the status bar icons black
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }


        findViewById(R.id.clog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Initialize SharedPreferences
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
                SharedPreferences.Editor pref = sharedPreferences.edit();
                pref.putBoolean("Seen", true);
                pref.commit();

                startActivity(new Intent(WelcomeScreenActivity.this, MainActivity.class));
                finish();


            }
        });
    }
}
package e.wolfsoft1.Xnotes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Thread background = new Thread() {
            public void run() {

                try {
                    sleep(2 * 1600);

                    Intent intent = new Intent(SplashScreen.this, GoogleSignin.class);
                    startActivity(intent);

                    //Remove activity
                    finish();

                } catch (Exception e) {

                }
            }
        };

        // start thread
        background.start();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

    }
}
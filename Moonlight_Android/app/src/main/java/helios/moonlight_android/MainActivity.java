package helios.moonlight_android;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.common.SignInButton;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    private SignInButton mSignInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSignInButton = (SignInButton) findViewById(R.id.sign_in_button);

        // Button listener
        mSignInButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, LoginTwoActivity.class);
        startActivity(intent);
    }

/*
    public void toSignIn(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}

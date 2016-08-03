package app.test.slm.com.testapp;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import app.test.slm.com.testapp.fragment.ContactListFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(findViewById(R.id.fragment_container) != null) {
            if(savedInstanceState != null) {
                return;
            }

            ContactListFragment contactListFragment = new ContactListFragment();
            contactListFragment.setArguments(getIntent().getExtras());

            FragmentTransaction t = getSupportFragmentManager().beginTransaction();
            t.add(R.id.fragment_container, contactListFragment);
            t.commit();
        }
    }
}

package mx.com.jaymi.resistance;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import mx.com.jaymi.resistance.adapter.PageAdapter;
import mx.com.jaymi.resistance.ayuda.CuatroFragment;
import mx.com.jaymi.resistance.ayuda.DosFragment;
import mx.com.jaymi.resistance.ayuda.TresFragment;
import mx.com.jaymi.resistance.ayuda.UnoFragment;

public class Main2ActAyuda extends AppCompatActivity implements UnoFragment.OnFragmentInteractionListener,
        DosFragment.OnFragmentInteractionListener,TresFragment.OnFragmentInteractionListener, CuatroFragment.OnFragmentInteractionListener {

    private Toolbar toolbar2;
    private TabLayout tabLayout2;
    private ViewPager viewPager2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2_act_ayuda);

        Toast.makeText(getApplicationContext(), "Desliza de derecha a izquierda",
                Toast.LENGTH_LONG).show();

        toolbar2 = (Toolbar) findViewById(R.id.toolbar2);
        tabLayout2 = (TabLayout)findViewById(R.id.tabLayout);
        viewPager2 = (ViewPager) findViewById(R.id.viewPager);
        setUpViewPager();
        ///setSupportActionBar(toolbar);

        if(toolbar2!= null){
            setSupportActionBar(toolbar2);

        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Main2ActAyuda.this, MainActivity.class);
                startActivity(intent);
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });

    }
    private ArrayList<Fragment> agregarFragments(){

        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(new UnoFragment());
        fragments.add(new DosFragment());
        fragments.add(new TresFragment());
        fragments.add(new CuatroFragment());

        return fragments;
    }
    private void setUpViewPager (){
        viewPager2.setAdapter(new PageAdapter(getSupportFragmentManager(),agregarFragments()));
        tabLayout2.setupWithViewPager(viewPager2);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}

package com.hfad.beeradvisor;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.List;

public class FindBeerActivity extends Activity {
    private BeerExpert expert = new BeerExpert();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_beer);
    }
    public void onClickFindBeer(View view){
        //get a ref to the TextView
        TextView brands = (TextView) findViewById(R.id.brands);
        //get a ref to the Spinner
        Spinner color = (Spinner) findViewById(R.id.color);
        //get the selected item in spinner
        String beerType = String.valueOf(color.getSelectedItem());
        //get rec from BeerExpert class
        List<String> brandList = expert.getBrands(beerType);
        StringBuilder brandsFormatted = new StringBuilder();
        for(String brand : brandList) {
            brandsFormatted.append(brand).append('\n');
        }
        //display the beers
        brands.setText(brandsFormatted);
    }
}

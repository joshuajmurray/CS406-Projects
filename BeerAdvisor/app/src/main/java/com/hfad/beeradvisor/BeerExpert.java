package com.hfad.beeradvisor;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joshua on 7/4/2017.
 */

public class BeerExpert {
    List<String> getBrands(String color) {
        List<String> brands = new ArrayList<String>();
        if(color.equals("amber")){
            brands.add("Jack Amber");
            brands.add("Red Moose");
        } else {
            brands.add("Jail Pale Ale");
            brands.add("Gout Stout");
        }
        return brands;
    }
}

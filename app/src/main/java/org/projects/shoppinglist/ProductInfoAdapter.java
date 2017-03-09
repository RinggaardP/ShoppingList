package org.projects.shoppinglist;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by Camilla on 16-02-2017.
 */

public class ProductInfoAdapter extends ArrayAdapter<Product> {

    public static Context context;

    public ProductInfoAdapter(Context context, ArrayList<Product> products) {
        super(context, 0, products);
        this.context = context;

    }
}

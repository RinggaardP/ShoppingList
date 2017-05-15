package org.projects.shoppinglist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static org.projects.shoppinglist.ProductInfoAdapter.context;

public class MainActivity extends AppCompatActivity implements MyDialogFragment.OnPositiveListener {


    private String[] items = { "0", "1", "2", "3", "4",
            "5", "6", "7" };

    private static final String TAG = "com.example.StateChange";
    private String name = "";

    static MyDialogFragment dialog;
    static Context context;


    ArrayAdapter<Product> adapter;
    ArrayAdapter<String> adapter2;
    ListView listView;
    FirebaseListAdapter<Product> mAdapter;
    DatabaseReference firebase;
    ArrayList<Product> bag = new ArrayList<Product>();

    public FirebaseListAdapter getMyAdapter()
    {
        return mAdapter;
    }

    Product lastDeletedProduct;
    int lastDeletedPosition;

    public void onPositiveClicked() {
        //Do your update stuff here to the listview
        //and the bag etc
        //just to show how to get arguments from the bag.
        Toast toast = Toast.makeText(context,
                "positive button clicked", Toast.LENGTH_LONG);
        toast.show();
        firebase.removeValue(); //here you can do stuff with the bag and
        //adapter etc.
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this;
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("bag"))
                bag = savedInstanceState.getParcelableArrayList("bag");
        }

        Spinner spinner = (Spinner) findViewById(R.id.addTextQ);

        //we use a predefined simple spinner drop down,
        //you could define your own layout, so that for instance
        //there was pictures in the drop down list.
        adapter2 = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, items);

        spinner.setAdapter(adapter2);

        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            //The AdapterView<?> type means that this can be any type,
            //so we can use both AdapterView<String> or any other
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                //So this code is called when ever the spinner is clicked
                Toast.makeText(MainActivity.this,
                        "Item selected: " + items[position], Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO you would normally do something here
                // for instace setting the selected item to "null"
                // or something.
            }
        });

        //This line will get the actual seleted item -
        //in our case the values in the spinner is simply
        //strings, so we need to make a cast to a String
        String item = (String) spinner.getSelectedItem();

        //getting our listiew - you can check the ID in the xml to see that it
        //is indeed specified as "list"
        listView = (ListView) findViewById(R.id.list);
        //here we create a new adapter linking the bag and the
        //listview
        adapter = new ArrayAdapter<Product>(this,
                android.R.layout.simple_list_item_checked, bag);

        //setting the adapter on the listview
        //listView.setAdapter(adapter);


        firebase = FirebaseDatabase.getInstance().getReference().child("items");

        mAdapter = new FirebaseListAdapter<Product>(this, Product.class, android.R.layout.simple_list_item_checked, firebase) {
            @Override
            protected void populateView(View view, Product product, int i) {
                TextView textView = (TextView) view.findViewById(android.R.id.text1); //standard android id.
                textView.setText(product.toString());
            }
        };
        listView.setAdapter(mAdapter);

        //here we set the choice mode - meaning in this case we can
        //only select one item at a time.
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        Button addButton = (Button) findViewById(R.id.addButton);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //læs fra edittext felterne
                Product p = new Product(getProductQuantityInt(), getProductName());
                //adapter.add(p);
                firebase.push().setValue(p);
                //The next line is needed in order to say to the ListView
                //that the data has changed - we have added stuff now!
                getMyAdapter().notifyDataSetChanged();
            }
        });

        Button deleteButton = (Button) findViewById(R.id.deleteButton);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int index = listView.getCheckedItemPosition();
                getMyAdapter().getRef(index).setValue(null);
                //The next line is needed in order to say to the ListView
                //that the data has changed - we have added stuff now!
                getMyAdapter().notifyDataSetChanged();
                listView.clearChoices();
            }
        });

        /*Button clearButton = (Button) findViewById(R.id.clearButton);

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bag.clear();
                //The next line is needed in order to say to the ListView
                //that the data has changed - we have added stuff now!
                getMyAdapter().notifyDataSetChanged();
            }
        })*/

        getSupportActionBar().setHomeButtonEnabled(true);

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, getText(R.id.list));
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));

    }

        //add some stuff to the list so we have something
        // to show on app startup
        //bag.add("Bananas");
        //bag.add("Apples");

        public void showDialog(View v) {
            //showing our dialog.

            dialog = new MyDialog();
            //Here we show the dialog
            //The tag "MyFragement" is not important for us.
            dialog.show(getFragmentManager(), "MyFragment");
        }

    public static class MyDialog extends MyDialogFragment {


        @Override
        protected void negativeClick() {
            //Here we override the method and can now do something
            Toast toast = Toast.makeText(context,
                    "negative button clicked", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public String getProductName(){
        EditText addText = (EditText) findViewById(R.id.addText);

        return addText.getText().toString();
    }
    public String getProductQuantity(){
        Spinner addTextQ = (Spinner) findViewById(R.id.addTextQ);

        return addTextQ.toString();
    }

    public int getProductQuantityInt(){
        Spinner addTextQ = (Spinner) findViewById(R.id.addTextQ);
        String s = addTextQ.getSelectedItem().toString();
        return Integer.parseInt(s);

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

        switch (item.getItemId()) {

            case R.id.item_clear:
                dialog = new MyDialog();
                //Here we show the dialog
                //The tag "MyFragement" is not important for us.
                dialog.show(getFragmentManager(), "MyFragment");


                return true;
        };

        return super.onOptionsItemSelected(item);
    }


    //This method is called before our activity is destoryed
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //ALWAYS CALL THE SUPER METHOD - To be nice!
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState");
		/* Here we put code now to save the state */
        //outState.putString("savedName", name);
        outState.putParcelableArrayList("bag", bag);

    }
    //this is called when our activity is recreated, but
    //AFTER our onCreate method has been called
    //EXTREMELY IMPORTANT DETAIL

    public void saveCopy()
    {
        lastDeletedPosition = listView.getCheckedItemPosition();
        lastDeletedProduct = bag.get(lastDeletedPosition);
    }




}

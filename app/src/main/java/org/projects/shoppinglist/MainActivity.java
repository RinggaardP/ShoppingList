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
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static org.projects.shoppinglist.ProductInfoAdapter.context;

public class MainActivity extends AppCompatActivity implements MyDialogFragment.OnPositiveListener {


    private String[] items = {"Item", "g", "kg", "l", "dl", "ml", "handful", "box", "bucket"};

    private static final String TAG = "com.example.StateChange";
    static MyDialogFragment dialog;

    static Context context;
    static TextView textView;

    NumberPicker numberPicker;

    //ArrayAdapter<Product> adapter;
    ArrayAdapter<String> adapter2;

    ListView listView;
    Toolbar toolbar;
    //User's name in settings
    String name;

    FirebaseListAdapter<Product> mAdapter;
    DatabaseReference firebase;

    public FirebaseListAdapter getMyAdapter() {
        return mAdapter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.context = this;
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        /*In the settings - sets the name*/
        name = MyPreferenceFragment.getName(this);
        updateUI(name);

        //Spinner spinner = (Spinner) findViewById(R.id.addTextQ);

        //we use a predefined simple spinner drop down,
        //you could define your own layout, so that for instance
        //there was pictures in the drop down list.
        //adapter2 = new ArrayAdapter<>(this,
        //      android.R.layout.simple_spinner_dropdown_item, items);

        //spinner.setAdapter(adapter2);

        numberPicker = (NumberPicker) findViewById(R.id.numberPicker);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(items.length-1);
        numberPicker.setDisplayedValues(items);
        numberPicker.setFormatter(new NumberPicker.Formatter(){
            @Override
            public String format (int val){
                return items[val];
            }
        });

        //getting our listiew - you can check the ID in the xml to see that it
        //is indeed specified as "list"
        listView = (ListView) findViewById(R.id.list);

        firebase = FirebaseDatabase.getInstance().getReference().child("items");

        mAdapter = new FirebaseListAdapter<Product>(this, Product.class, android.R.layout.simple_list_item_checked, firebase) {
            @Override
            protected void populateView(View view, Product product, int i) {
                textView = (TextView) view.findViewById(android.R.id.text1); //standard android id.
                textView.setText(product.getName() +" - "+ product.getQuantity()+" "+format(Integer.parseInt(product.getUnit())));////
            }
        };

        listView.setAdapter(mAdapter);
        //here we set the choice mode - meaning in this case we can
        //only select one item at a time.
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);


        /*-----BUTTONS-----*/
        /*-----ADD-----*/
        Button addButton = (Button) findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //læs fra edittext felterne
                Product p = new Product(getProductQuantityInt(), getProductName());
                p.setUnit(getProductUnit());



                //adapter.add(p);
                firebase.push().setValue(p);
                //The next line is needed in order to say to the ListView
                //that the data has changed - we have added stuff now!
                getMyAdapter().notifyDataSetChanged();
            }
        });
        /*-----DELETE-----*/
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
        /*-----END BUTTONS-----*/

        //Home button in the action bar
        getSupportActionBar().setHomeButtonEnabled(true);
    }

//DIALOG FOR CLEAR (YES)
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
//DIALOG FOR CLEAR (NO)
    public static class MyDialog extends MyDialogFragment {
        @Override
        protected void negativeClick() {
            //Here we override the method and can now do something
            Toast toast = Toast.makeText(context,
                    "negative button clicked", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
//CREATES MENU FROM MENU_MAIN.XML
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //GETTERS
    public String getProductName() {
        EditText addText = (EditText) findViewById(R.id.addText);

        return addText.getText().toString();
    }

    public int getProductQuantityInt() {
        EditText input = (EditText) findViewById(R.id.addTextQ);
        String inputValue = input.getText().toString();
        return Integer.parseInt(inputValue);
    }

    public String getProductUnit(){
         String unit = numberPicker.getValue() + "";
         return unit;
    }

    /*----ACTIONAR/MENU----*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        /*SHARE*/
        if (id == R.id.item_share) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            int nr = mAdapter.getCount();
            String result = "";
            for (int i = 0; i < nr; i++) {
                Product p = mAdapter.getItem(i);
                result = result + p.toString() + "\n";
            }
            sendIntent.putExtra(Intent.EXTRA_TEXT, result);
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
        }
        /*SETTINGS*/
        if (item.getItemId() == R.id.action_settings) {
            //Start our settingsactivity and listen to result - i.e.
            //when it is finished.
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivityForResult(intent, 1);
            Log.d("shopping", "settingsstarted");
            //notice the 1 here - this is the code we then listen for in the
            //onActivityResult

        }
        /*CLEAR*/
        if (item.getItemId() == R.id.item_clear) {
            dialog = new MyDialog();
            //Here we show the dialog
            //The tag "MyFragement" is not important for us.
            dialog.show(getFragmentManager(), "MyFragment");
        }

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
    }

    // POP-UP TOAST WITH NEWLY INSERTED NAME
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) //the code means we came back from settings
        {
            //I can can these methods like this, because they are static
            String name = MyPreferenceFragment.getName(this);
            String message = "Welcome, " + name;
            Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
            toast.show();
            updateUI(name);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    public   String format (int val){
    return items[val];
}
    //This method updates our text views.
    //UPDATES NAME USING INPUT FROM SETTINGS
    public void updateUI(String name) {
        TextView myName = (TextView) findViewById(R.id.myName);
        myName.setText(name);
    }
}

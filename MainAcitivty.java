
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.karanvishwakarma.newnote.howtouse.Main5Activity;
import com.example.karanvishwakarma.newnote.trash.Main3Activity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    TextView addanote,noteshead;
    final DBHandler  dbHandler  = new DBHandler (this);      //database for main list
    ArrayList<Contacts> arr = new ArrayList<>();
    EditText mainedit;
    ImageView mainlogo;
    CustomAdapter customAdapter;        //fav list custom adapter
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        listView = findViewById(R.id.listview);
        mainedit = findViewById(R.id.mainedit);
        mainedit.setVisibility(View.INVISIBLE);
        noteshead = findViewById(R.id.noteshead);
        addanote = findViewById(R.id.addanote);
        mainlogo = findViewById(R.id.mainlogo);
        List<Contacts> contacts = dbHandler.getAllContacts();

        for (Contacts cn : contacts) {
            arr.add(cn);
        }
        if(arr.isEmpty() == true){addanote.setVisibility(View.VISIBLE);mainlogo.setVisibility(View.VISIBLE);}else{mainlogo.setVisibility(View.INVISIBLE);addanote.setVisibility(View.INVISIBLE);}
        customAdapter = new CustomAdapter(MainActivity.this, arr);
        customAdapter.notifyDataSetChanged();
        customAdapter.getObject(customAdapter);
        listView.setAdapter(customAdapter);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(),Main2Activity.class);
                intent.putExtra("title","0");
                startActivity(intent);
            }
        });
        mainedit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                ArrayList<String> type_name_filter = new ArrayList<String>();

                String text = editable.toString();

                for (int i = 0; i < arr.size(); i++) {

                    if ((arr.get(i).getWordName().toString().toLowerCase()).contains(text.toLowerCase())) {
                        type_name_filter.add(arr.get(i).getWordName()+":"+arr.get(i).getMean());

                    }
                }
                listUpdate(type_name_filter);
            }
        });
    }
    public void listUpdate(ArrayList<String> data) {
        CustomAdapter2 customAdapter2 = new CustomAdapter2(MainActivity.this, data);
        listView.setAdapter(customAdapter2);
       // listView.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, data));

    }


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

        if(id == R.id.action_trash){
            Intent intent = new Intent(getApplicationContext(),Main3Activity.class);

            startActivity(intent);
            return true;
        }
        if(id == R.id.action_howtouse){
            Intent intent = new Intent(getApplicationContext(),Main5Activity.class);
            startActivity(intent);
        }
        if(id == R.id.action_search){
            View view = findViewById(R.id.action_search);
            view.setVisibility(View.INVISIBLE);
            noteshead.setVisibility(View.INVISIBLE);
            mainedit.setVisibility(View.VISIBLE);
            showSoftKeyboard(mainedit);
        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBackPressed() {
        if(mainedit.getVisibility() == View.VISIBLE)
        {
            View view = findViewById(R.id.action_search);
            view.setVisibility(View.VISIBLE);
            mainedit.setVisibility(View.INVISIBLE);
            hideSoftKeyboard(mainedit);
            noteshead.setVisibility(View.VISIBLE);
            listView.setAdapter(customAdapter);

        }else {
            finishAndRemoveTask();
        }
    }
    public void showSoftKeyboard(View view){
        if(view.requestFocus()){
            InputMethodManager imm =(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view,InputMethodManager.SHOW_IMPLICIT);
        }
    }
    public void hideSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
    }
}

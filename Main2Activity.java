# QuickNote


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.karanvishwakarma.newnote.howtouse.Main5Activity;
import com.example.karanvishwakarma.newnote.trash.Main3Activity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Main2Activity extends AppCompatActivity {
    EditText note,title;
    String tv1;
    LinearLayout innerlayout;
    SaveSharedPreference s;
    boolean bold = false;
    final DBHandler  dbHandler  = new DBHandler (this);
    public void showSoftKeyboard(View view){
        if(view.requestFocus()){
            InputMethodManager imm =(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view,InputMethodManager.SHOW_IMPLICIT);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        note        = findViewById(R.id.note);
        title       = findViewById(R.id.title);
        Intent in   = getIntent();
        innerlayout = findViewById(R.id.innerlayout);

        tv1 = in.getExtras().getString("title");
        innerlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                note.requestFocus();
                note.setCursorVisible(true);
                note.setSelection(note.getText().length());
                showSoftKeyboard(note);
            }
        });

        if(tv1.matches("0")){
            note.setHint("Enter note here..");
            title.setHint("Enter a unique title..");

        }
        else {
            title.setText(tv1);
            note.setText(dbHandler.getNoteFromTitle(tv1));
            title.setCursorVisible(false);
            note.setCursorVisible(false);

            note.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    note.setCursorVisible(true);
                }
            });
            title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    title.setCursorVisible(true);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.howtouse) {
            Intent intent = new Intent(getApplicationContext(), Main5Activity.class);
            startActivity(intent);
            return true;
        }
        if(id == R.id.trash){
            Intent intent = new Intent(getApplicationContext(), Main3Activity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.save) {

            if(note.getText().toString().matches("")|| title.getText().toString().matches("")){
                Toast.makeText(getApplicationContext(),"please enter a note", Toast.LENGTH_SHORT).show();
            }
            else {
                s = new SaveSharedPreference();
                Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                String formattedDate = df.format(c);
                    if (SaveSharedPreference.getIntegerCount(Main2Activity.this) == 0) {


                    s.setIntegerCount(getApplicationContext(),0);

                    dbHandler.addContact(new Contacts(s.getIntegerCount(Main2Activity.this), "" + title.getText().toString(), "" + note.getText().toString(),""+formattedDate));
                    s.setIntegerCount(getApplicationContext(),s.getIntegerCount(Main2Activity.this)+1);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    int titleExists = dbHandler.ifTitleExists(title.getText().toString());

                    if (titleExists == 1) {
                        if(tv1.matches("0")){
                            Toast.makeText(getApplicationContext(),"Please enter a unique title.. title already exists",Toast.LENGTH_SHORT).show();
                        }else {

                            dbHandler.updateExistingTitle(title.getText().toString(), note.getText().toString());
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        dbHandler.addContact(new Contacts(s.getIntegerCount(Main2Activity.this) + 1, "" + title.getText().toString(), "" + note.getText().toString(),""+formattedDate));
                        s.setIntegerCount(getApplicationContext(), s.getIntegerCount(Main2Activity.this) + 1);
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        String t = title.getText().toString();
        String n = note.getText().toString();

            if(t.matches(""))
            {
                Toast.makeText(getApplicationContext(), "Please enter a title", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }

            else {
                if (dbHandler.getNoteFromTitle(t).matches(n)) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                } else {
                    buildDialog(Main2Activity.this).show();
                    Toast.makeText(getApplicationContext(), "new note", Toast.LENGTH_SHORT).show();
                }
            }
    }
    public AlertDialog.Builder buildDialog(Context c) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("Notes");
        builder.setMessage("Save your changes or discard them..?");

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                String titlename = title.getText().toString();
                String notename = note.getText().toString();

                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                String formattedDate = df.format(c);
                if (titlename.matches("") || notename.matches("")) {
                    Toast.makeText(getApplicationContext(), "Please enter some title and note..", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    if (dbHandler.ifTitleExists(titlename) == 1) {

                        dbHandler.updateExistingTitle(titlename, notename);
                        Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        dbHandler.addContact(new Contacts(s.getIntegerCount(Main2Activity.this) + 1, "" + title.getText().toString(), "" + note.getText().toString(), "" + formattedDate));
                        s.setIntegerCount(getApplicationContext(), s.getIntegerCount(Main2Activity.this) + 1);
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        Toast.makeText(getApplicationContext(), "New note created", Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                        finish();
                    }
                }
                dialog.dismiss();
            }

        }).setNegativeButton("Discard", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
                dialog.dismiss();
            }
        }).setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        }).setIcon(R.drawable.logo);
        return builder;
    }

}
class SaveSharedPreference
{
    static final String PREF_USER_NAME= "initial";
    static final String INT_STRING = "initialInteger";
    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setUserName(Context ctx, String userName)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_NAME, userName);

        editor.commit();
    }
    public static void setIntegerCount(Context ctx,int c)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putInt(INT_STRING, c);
        editor.commit();
    }
    public static String getUserName(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_USER_NAME, "");
    }
    public static int getIntegerCount(Context ctx)
    {
        return getSharedPreferences(ctx).getInt(INT_STRING,0);
    }
}

package cube.sugar.thesimplestto_do;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class ListActivity extends AppCompatActivity {
    private String mTask = "";
    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    ArrayList<String> listTasks = new ArrayList<String>();
    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    ArrayAdapter<String> adapter;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    Set<String> set = new HashSet<String>();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        prefs = getSharedPreferences("cube.sugar.thesimplestto_do_prefs", MODE_PRIVATE);

        adapter=new ArrayAdapter<String>(ListActivity.this, android.R.layout.simple_list_item_1, listTasks);
        final ListView list = (ListView) findViewById(R.id.list);
        list.setAdapter(adapter);

        Set<String> tasks = prefs.getStringSet("cube.sugar.thesimplestto_do_prefs", null);
        if(tasks!=null) {
            listTasks.addAll(tasks);
            adapter.notifyDataSetChanged();
        }


        //get current date and edit it
        Date date = new Date();
        String stringDate = DateFormat.getDateInstance(DateFormat.LONG, Locale.ENGLISH).format(date);
        final TextView mDate = (TextView) findViewById(R.id.date);
        mDate.setText(stringDate);

        final LinearLayout carefreeContainer = (LinearLayout) findViewById(R.id.carefree_container);

        if (listTasks.size() == 0) {
            carefreeContainer.setVisibility(View.VISIBLE);
        }
        else
        {
            carefreeContainer.setVisibility(View.INVISIBLE);
        }

        FloatingActionButton mAdd = (FloatingActionButton) findViewById(R.id.add);
        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ListActivity.this);
                builder.setTitle("New task:");

                // Set up the input
                final EditText input = new EditText(ListActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                builder.setView(input);

                //toggle keyboard
                input.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                // Set up the buttons
                builder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mTask = input.getText().toString();
                        carefreeContainer.setVisibility(View.INVISIBLE);

                        hideKeyboard();

                        listTasks.add(mTask);
                        adapter.notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        hideKeyboard();
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listTasks.remove(position);
                adapter.notifyDataSetChanged();
                if (listTasks.size() == 0) {
                    carefreeContainer.setVisibility(View.VISIBLE);
                }

            }
        });
    }

    public void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideKeyboard();
    }

    @Override
    protected void onStop() {
        super.onStop();
        set.addAll(listTasks);
        editor=prefs.edit();
        editor.putStringSet("cube.sugar.thesimplestto_do_prefs",set);
        editor.apply();

    }
}

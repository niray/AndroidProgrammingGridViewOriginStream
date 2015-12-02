package actionbar.com.huway.actionbardemo;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    ListView lv_show;
    ArrayAdapter<String> adapter;
    private ProgressBar pgb_show;
    private Button btn_start;
    private OnAsyncTask onAsyncTask = new OnAsyncTask();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote);


//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        setTitle("Action");
//        toolbar.setBackgroundColor(Color.BLACK);
//        toolbar.setSubtitle("OAOA");
//        toolbar.setAlpha(0.1f);
//        Button btn = new Button(MainActivity.this);
//        btn.setText("haha");
//        btn.setBackgroundColor(Color.RED);
//
//        toolbar.addView(btn);
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
//            }
//        });
//
//        lv_show = (ListView) findViewById(R.id.lv_show);
//
//        adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1);
//
//        for (int i = 0; i < 55; i++) {
//            adapter.add("Item  : " + i);
//        }
//
//        lv_show.setAdapter(adapter);
//        registerForContextMenu(lv_show);
//        lv_show.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
//        lv_show.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
//            @Override
//            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
//
//            }
//
//            @Override
//            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//
//                mode.getMenuInflater().inflate(R.menu.menu_main, menu);
//
//                return true;
//            }
//
//            @Override
//            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
//                return false;
//            }
//
//            @Override
//            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
//                Toast.makeText(MainActivity.this, "CLICK", Toast.LENGTH_SHORT).show();
//                return true;
//            }
//
//            @Override
//            public void onDestroyActionMode(ActionMode mode) {
//
//            }
//        });

        pgb_show = (ProgressBar) findViewById(R.id.pgb_show);
        btn_start = (Button) findViewById(R.id.btn_start);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (onAsyncTask.getStatus().equals(AsyncTask.Status.RUNNING)) {
                        onAsyncTask = new OnAsyncTask();
                    }
                    onAsyncTask.execute(25, 55, 75, 100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        findViewById(R.id.btn_cancel_interrupt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAsyncTask.cancel(true);
            }
        });
        findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAsyncTask.cancel(false);
            }
        });

        startActivity(new Intent(MainActivity.this, PhotoGalleryActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.menu_delete) {
            startActivity(new Intent(MainActivity.this, CameraActivity.class));
            Toast.makeText(MainActivity.this, "ABC", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class OnAsyncTask extends AsyncTask<Integer, Integer, Void> {

        @Override
        protected Void doInBackground(Integer... params) {
            try {
                for (Integer ig : params) {
                    publishProgress(ig);
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            int p = values[0];
            pgb_show.setProgress(p);
            super.onProgressUpdate(values);
        }
    }

}

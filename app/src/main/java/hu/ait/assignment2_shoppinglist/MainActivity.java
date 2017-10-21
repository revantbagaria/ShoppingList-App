package hu.ait.assignment2_shoppinglist;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import hu.ait.assignment2_shoppinglist.adapter.ItemsAdapter;
import hu.ait.assignment2_shoppinglist.data.Item;
import hu.ait.assignment2_shoppinglist.touch.ItemsListTouchHelperCallback;
import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_NEW_ITEM = 101;
    public static final int REQUEST_EDIT_ITEM = 102;
    public static final String KEY_EDIT = "KEY_EDIT";
    private ItemsAdapter itemsAdapter;
    private CoordinatorLayout layoutContent;
    private DrawerLayout drawerLayout;
    private int itemToEditPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((MainApplication)getApplication()).openRealm();

        RealmResults<Item> allItems = getRealm().where(Item.class).findAll();
        Item itemsArray[] = new Item[allItems.size()];
        List<Item> itemsResult = new ArrayList<Item>(Arrays.asList(allItems.toArray(itemsArray)));

        itemsAdapter = new ItemsAdapter(itemsResult, this, ((MainApplication)getApplication()).getRealmItems());
        RecyclerView recyclerViewItems = (RecyclerView) findViewById(
                R.id.recyclerViewItems);
        recyclerViewItems.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewItems.setAdapter(itemsAdapter);

        ItemsListTouchHelperCallback touchHelperCallback = new ItemsListTouchHelperCallback(
                itemsAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(
                touchHelperCallback);
        touchHelper.attachToRecyclerView(recyclerViewItems);

        layoutContent = (CoordinatorLayout) findViewById(
                R.id.layoutContent);

        FloatingActionButton fabAdd = (FloatingActionButton) findViewById(R.id.btnAdd);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreateItemActivity();
            }
        });

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        setUpToolBar();

    }

    public Realm getRealm() {
        return ((MainApplication)getApplication()).getRealmItems();
    }

    private void setUpToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("My List");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void showCreateItemActivity() {
        Intent intentStart = new Intent(MainActivity.this,
                CreateNewItemActivity.class);
        startActivityForResult(intentStart, REQUEST_NEW_ITEM);
    }

    public void showEditItemActivity(String itemID, int position) {
        Intent intentStart = new Intent(MainActivity.this,
                CreateNewItemActivity.class);
        itemToEditPosition = position;

        intentStart.putExtra(KEY_EDIT, itemID);
        startActivityForResult(intentStart, REQUEST_EDIT_ITEM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                String itemID  = data.getStringExtra(
                        CreateNewItemActivity.KEY_ITEM);

                Item item = getRealm().where(Item.class)
                        .equalTo("itemID", itemID)
                        .findFirst();

                if (requestCode == REQUEST_NEW_ITEM) {
                    itemsAdapter.addItem(item);
                    showSnackBarMessage("Item added");
                } else if (requestCode == REQUEST_EDIT_ITEM) {
                    itemsAdapter.updateItem(itemToEditPosition, item);
                    showSnackBarMessage("Item edited");
                }
                break;
            case RESULT_CANCELED:
                showSnackBarMessage("Item adding cancelled");
                break;
        }
    }

    public void deleteItem(Item item) {
        getRealm().beginTransaction();
        item.deleteFromRealm();
        getRealm().commitTransaction();
    }


    private void showSnackBarMessage(String message) {
        Snackbar.make(layoutContent,
                message,
                Snackbar.LENGTH_LONG
        ).setAction("hide", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //...
            }
        }).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_from_toolbar:
                showCreateItemActivity();
                return true;
            case R.id.action_delete_all_from_toolbar:
                itemsAdapter.removeAll();
                return true;

            default:
                showCreateItemActivity();
                return true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((MainApplication)getApplication()).closeRealm();
    }
}

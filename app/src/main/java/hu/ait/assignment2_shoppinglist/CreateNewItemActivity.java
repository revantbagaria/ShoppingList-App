package hu.ait.assignment2_shoppinglist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.UUID;

import hu.ait.assignment2_shoppinglist.data.Item;
import io.realm.Realm;

public class CreateNewItemActivity extends AppCompatActivity {
    public static final String KEY_ITEM = "KEY_ITEM";
    private Spinner spinnerItemType;
    private EditText etItemName;
    private EditText etItemDesc;
    private EditText etEstim_Price;
    private CheckBox cbItemDone;
    private Item itemToEdit = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_item);

        setupUI();

        if (getIntent().getSerializableExtra(MainActivity.KEY_EDIT) != null) {
            initEdit();
        } else {
            initCreate();
        }
    }

    private void initCreate() {
        getRealm().beginTransaction();
        itemToEdit = getRealm().createObject(Item.class, UUID.randomUUID().toString());
        getRealm().commitTransaction();
    }

    private void initEdit() {
        String itemID = getIntent().getStringExtra(MainActivity.KEY_EDIT);
        itemToEdit = getRealm().where(Item.class)
                .equalTo("itemID", itemID)
                .findFirst();

        etItemName.setText(itemToEdit.getItemName());
        etItemDesc.setText(itemToEdit.getDescription());
        etEstim_Price.setText(itemToEdit.getEstim_price());
        cbItemDone.setChecked(itemToEdit.isDone());
        spinnerItemType.setSelection(itemToEdit.getItemType().getValue());
    }

    private void setupUI() {
        spinnerItemType = (Spinner) findViewById(R.id.spinnerItemType);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.itemtypes_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerItemType.setAdapter(adapter);

        etItemName = (EditText) findViewById(R.id.etItemName);
        etItemDesc = (EditText) findViewById(R.id.etItemDesc);
        etEstim_Price = (EditText) findViewById(R.id.etEstim_Price);
        cbItemDone = (CheckBox) findViewById(R.id.cbItemDone);

        Button btnAddItem = (Button) findViewById(R.id.btnAddItem);
        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveItem();
            }
        });
    }

    public Realm getRealm() {
        return ((MainApplication)getApplication()).getRealmItems();
    }

    private void saveItem() {

        if ("".equals(etItemName.getText().toString())){
            etItemName.setError("can not be empty");
        } else {
            Intent intentResult = new Intent();

            getRealm().beginTransaction();
            itemToEdit.setItemName(etItemName.getText().toString());
            itemToEdit.setDescription(etItemDesc.getText().toString());
            itemToEdit.setEstim_price(etEstim_Price.getText().toString());
            itemToEdit.setDone(cbItemDone.isChecked());
            itemToEdit.setItemType(spinnerItemType.getSelectedItemPosition());
            getRealm().commitTransaction();

            intentResult.putExtra(KEY_ITEM, itemToEdit.getItemID());
            setResult(RESULT_OK, intentResult);
            finish();
        }
    }
}

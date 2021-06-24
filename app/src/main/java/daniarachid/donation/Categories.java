package daniarachid.donation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

public class Categories extends AppCompatActivity {
    ListView subCategories;
    RecyclerView cats;
    Spinner categories;
    ArrayAdapter<Items> itemsCat;
    String[] categoriesArray = {"Home", "Clothing", "Electronics", "Family", "Hobbies", "Entertainment", "Food"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        //initialize views

        cats = findViewById(R.id.subCategoriesRec);
        categories = findViewById(R.id.categoriesSpin);
        //subCategories = findViewById(R.id.subCategoriesRec);
        //subCategories.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, categoriesArray));

        //cats.setAdapter(categoriesArray);
        //subCategories.setAdapter(new ArrayAdapter<>(thi));
        
    }

    class Items {

    }
}
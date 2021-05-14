package com.oricadu.financemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.oricadu.financemanager.model.Category;

public class ListCategoriesActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;
    FirebaseUser user;
    private FirebaseAuth auth;

    private RecyclerView recyclerView;
    private FloatingActionButton button;
    private EditText inputName, inputSum;

    protected static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;
        TextView categorySum;
        TextView categorySpentSum;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            categoryName = itemView.findViewById(R.id.category_name);
            categorySum = itemView.findViewById(R.id.expense_sum);
            categorySpentSum = itemView.findViewById(R.id.category_spent_sum);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_categories);

        auth = FirebaseAuth.getInstance();
        reference = database.getReference();
        user = auth.getCurrentUser();
        Log.i("User", "user.uid=" + user.getUid());

        inputName = findViewById(R.id.category_name);
        inputSum = findViewById(R.id.expense_sum);
        button = findViewById(R.id.action_button2);

        recyclerView = (RecyclerView) findViewById(R.id.category_recycler);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        FirebaseRecyclerAdapter<Category, CategoryViewHolder> adapter;

        adapter = new FirebaseRecyclerAdapter<Category, CategoryViewHolder>(
                Category.class,
                R.layout.category_item,
                CategoryViewHolder.class,
                reference.child(user.getUid())
                        .child("Categories")) {
            @Override
            protected void populateViewHolder(CategoryViewHolder categoryViewHolder, Category category, int i) {
                Log.i("User", "inside adapter user.uid=" + user.getUid());
                categoryViewHolder.categoryName.setText(category.getCategoryName());
                categoryViewHolder.categorySum.setText(String.valueOf(category.getCategorySum()));
                categoryViewHolder.categorySpentSum.setText(String.valueOf(category.getCategorySpentSum()));
                Log.i("User", String.valueOf(category.getCategorySpentSum()) + " spent " + category.getCategorySpentSum());
            }
        };

        recyclerView.setAdapter(adapter);





        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference.child(user.getUid())
                        .child("Categories")
                        .child(inputName.getText().toString().trim())
                        .setValue(new Category(
                                inputName.getText().toString().trim(),
                                Integer.parseInt(inputSum.getText().toString().trim()),
                                0, 0));

            }
        });

    }


}
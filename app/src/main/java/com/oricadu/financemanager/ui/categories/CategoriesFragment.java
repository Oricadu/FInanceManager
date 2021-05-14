package com.oricadu.financemanager.ui.categories;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.oricadu.financemanager.R;
import com.oricadu.financemanager.model.Category;

import java.util.List;

public class CategoriesFragment extends Fragment {

    private CategoriesViewModel categoriesViewModel;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference reference;
    private FirebaseUser user;
    private FirebaseAuth auth;

    private RecyclerView recyclerView;
    private FloatingActionButton button;
    private EditText inputName, inputSum;

    protected static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;
        TextView categorySum;
        TextView categorySpentSum;
        TextView categoryDifferenceSum;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            categoryName = itemView.findViewById(R.id.category_name);
            categorySum = itemView.findViewById(R.id.expense_sum);
            categorySpentSum = itemView.findViewById(R.id.category_spent_sum);
            categoryDifferenceSum = itemView.findViewById(R.id.category_difference_sum);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                }
            });
        }
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        categoriesViewModel =
                new ViewModelProvider(this).get(CategoriesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_categories, container, false);
        /*final TextView textView = root.findViewById(R.id.text_categories);
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/

        auth = FirebaseAuth.getInstance();
        reference = database.getReference();
        user = auth.getCurrentUser();

        if (user != null) {
            Log.i("User", "user.uid=" + user.getUid());

        }

        inputName = root.findViewById(R.id.category_name);
        inputSum = root.findViewById(R.id.expense_sum);
        button = root.findViewById(R.id.action_button2);


        recyclerView = (RecyclerView) root.findViewById(R.id.category_recycler);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        if (user != null) {
            final FirebaseRecyclerAdapter<Category, CategoryViewHolder> adapter;
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
                    categoryViewHolder.categoryDifferenceSum.setText(String.valueOf(category.getCategoryDifferenceSum()));
                    Log.i("User", String.valueOf(category.getCategorySpentSum()) + " spent " + category.getCategorySpentSum());
                }


            };

            recyclerView.setAdapter(adapter);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String categoryName = inputName.getText().toString().trim();
                    int categorySum = Integer.parseInt(inputSum.getText().toString().trim());
                    reference.child(user.getUid())
                            .child("Categories")
                            .child(categoryName)
                            .setValue(new Category(categoryName,
                                    categorySum,
                                    0, categorySum));

                }
            });

            ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                    int position = viewHolder.getAdapterPosition();
                    DatabaseReference ref = adapter.getRef(position);
                    ref.removeValue();
                }
            };

            new ItemTouchHelper(callback).attachToRecyclerView(recyclerView);



        }





        return root;
    }


}
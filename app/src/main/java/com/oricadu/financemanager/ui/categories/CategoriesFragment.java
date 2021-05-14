package com.oricadu.financemanager.ui.categories;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
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
import com.oricadu.financemanager.model.Aim;
import com.oricadu.financemanager.model.Category;

import java.util.List;

public class CategoriesFragment extends Fragment {

    private CategoriesViewModel categoriesViewModel;

    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static DatabaseReference reference = database.getReference();
    private static FirebaseAuth auth = FirebaseAuth.getInstance();
    private static FirebaseUser user = auth.getCurrentUser();

    private RecyclerView recyclerView;
    private FloatingActionButton button;
    private EditText inputName, inputSum;

    public static class CategoryAddDialog extends DialogFragment {

        EditText inputName, inputSum;

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.dialog_fragment, null);
            ViewGroup linearLayoutDialog = dialogView.findViewById(R.id.linear_layout_dialog);

            for (int i = 0; i < linearLayoutDialog.getChildCount(); i++) {
                View child = linearLayoutDialog.getChildAt(i);
                child.setVisibility(View.GONE);
            }

            inputName = (EditText) dialogView.findViewById(R.id.input_name);
            inputSum = (EditText) dialogView.findViewById(R.id.input_sum);

            inputName.setVisibility(View.VISIBLE);
            inputName.setHint(R.string.category_name);
            inputSum.setVisibility(View.VISIBLE);



            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity())
                    .setTitle("Add new category")
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            onDismiss(dialog);
                        }
                    })
                    .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            String name = inputName.getText().toString().trim();
                            String sum = inputSum.getText().toString().trim();

                            if (name.length() != 0 && sum.length() != 0) {
                                addCategory(name, Integer.parseInt(sum));

                            } else {
                                Toast.makeText(getActivity(), R.string.error_fill, Toast.LENGTH_LONG).show();
                            }

                        }
                    });
            dialogBuilder.setView(dialogView);

            return dialogBuilder.create();

        }
    }

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
                    CategoryAddDialog dialog = new CategoryAddDialog();
                    dialog.show(getChildFragmentManager(), "category");

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

    private static void addCategory(String name, int sum) {
        String categoryName = name;
        int categorySum = sum;
        reference.child(user.getUid())
                .child("Categories")
                .child(categoryName)
                .setValue(new Category(categoryName,
                        categorySum,
                        0, categorySum));
    }


}
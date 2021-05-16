package com.oricadu.financemanager.ui.categories;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
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

        private EditText inputName, inputSum, inputSpentSum;
        private String categoryName, categorySum, oldName, spentSum;

        private boolean isEdit = false;

        public  CategoryAddDialog(){
            super();
            isEdit = false;
        }

        public CategoryAddDialog(String name, String sum, String spent) {
            this.categoryName = name;
            this.categorySum = sum;
            this.oldName = name;
            this.spentSum = spent;
            this.isEdit = true;

        }

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
            inputSpentSum = (EditText) dialogView.findViewById(R.id.spent_sum);

            inputName.setVisibility(View.VISIBLE);
            inputName.setHint(R.string.category_name);
            inputSum.setVisibility(View.VISIBLE);
            inputSpentSum.setHint(R.string.spent);
            inputSpentSum.setVisibility(View.VISIBLE);


            if ((categorySum != null) && (categoryName != null)) {
                inputName.setText(categoryName);
                inputSum.setText(categorySum);
                inputSpentSum.setText(spentSum);
            }



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
                            String spent = inputSpentSum.getText().toString().trim();

                            if (name.length() != 0 && sum.length() != 0) {
                                if (isEdit) {
                                    editCategory(name, oldName, Integer.parseInt(sum), Integer.parseInt(spent));
                                } else {
                                    addCategory(name, Integer.parseInt(sum));
                                }

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
        CardView cardView;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.card_view);
            categoryName = itemView.findViewById(R.id.category_name);
            categorySum = itemView.findViewById(R.id.expense_sum);
            categorySpentSum = itemView.findViewById(R.id.category_spent_sum);
            categoryDifferenceSum = itemView.findViewById(R.id.category_difference_sum);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View item) {
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
                public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    return super.onCreateViewHolder(parent, viewType);

                }

                @Override
                protected void populateViewHolder(CategoryViewHolder categoryViewHolder, Category category, int i) {
                    Log.i("User", "inside adapter user.uid=" + user.getUid());
                    categoryViewHolder.categoryName.setText(category.getCategoryName());
                    categoryViewHolder.categorySum.setText(String.valueOf(category.getCategorySum()));
                    categoryViewHolder.categorySpentSum.setText(String.valueOf(category.getCategorySpentSum()));
                    categoryViewHolder.categoryDifferenceSum.setText(String.valueOf(category.getCategoryDifferenceSum()));
                    Log.i("User", String.valueOf(category.getCategorySpentSum()) + " spent " + category.getCategorySpentSum());

                    Log.i("User", "" + category.getCategoryDifferenceSum());
                    if (category.getCategoryDifferenceSum() < 0) {
                        categoryViewHolder.cardView.setCardBackgroundColor(Color.parseColor("#600D19"));
                    }

                    categoryViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View item) {
                            CardView cardView = (CardView) item;
                            TextView name = cardView.findViewById(R.id.category_name);
                            TextView sum = cardView.findViewById(R.id.expense_sum);
                            TextView spent = cardView.findViewById(R.id.category_spent_sum);
                            Log.i("category", "" + item);
//                            Log.i("category", "position " + position);
                            Log.i("category", "name " + name.getText());

                            CategoryAddDialog dialog = new CategoryAddDialog(name.getText().toString().trim(),
                                    sum.getText().toString().trim(),
                                    spent.getText().toString().trim());

                            dialog.show(getChildFragmentManager(), "category");

                            if ((dialog.inputSum != null) && (dialog.inputName != null)) {

                                dialog.inputName.setText(name.getText());
                                dialog.inputSum.setText(sum.getText());
                                dialog.inputSpentSum.setText(spent.getText());
                            }
                        }
                    });
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
                    final int position = viewHolder.getAdapterPosition();
                    Snackbar snackbar = Snackbar.make(viewHolder.itemView, getResources().getString(R.string.is_sure), Snackbar.LENGTH_LONG)
                    .setAction("yes", new View.OnClickListener (){
                        @Override
                        public void onClick(View v) {
                            DatabaseReference ref = adapter.getRef(position);
                            ref.removeValue();
                        }
                    });
                    View snack = snackbar.getView();
                    snack.setBackgroundColor(getResources().getColor(R.color.background, getActivity().getTheme()));
                    int snackbarTextId = com.google.android.material.R.id.snackbar_text;
                    int snackbarButtonId = com.google.android.material.R.id.snackbar_action;
                    TextView textView = (TextView) snack.findViewById(snackbarTextId);
                    Button button = (Button) snack.findViewById(snackbarButtonId);
                    textView.setTextColor(getResources().getColor(R.color.colorPrimaryDark, getActivity().getTheme()));
                    button.setBackgroundColor(getResources().getColor(R.color.colorAccent, getActivity().getTheme()));
                    snackbar.show();
                    recyclerView.getAdapter().notifyDataSetChanged();

                }
            };

            new ItemTouchHelper(callback).attachToRecyclerView(recyclerView);



        }





        return root;
    }

    private static void addCategory(String categoryName, int categorySum) {
        reference.child(user.getUid())
                .child("Categories")
                .child(categoryName)
                .setValue(new Category(categoryName,
                        categorySum,
                        0, categorySum));
    }

    private static void editCategory(String categoryName, String oldCategoryName, int categorySum, int categorySpentSum) {
        reference.child(user.getUid())
                .child("Categories")
                .child(oldCategoryName)
                .removeValue();

        reference.child(user.getUid())
                .child("Categories")
                .child(categoryName)
                .setValue(new Category(categoryName,
                        categorySum,
                        categorySpentSum,
                        categorySum - categorySpentSum));

    }


}
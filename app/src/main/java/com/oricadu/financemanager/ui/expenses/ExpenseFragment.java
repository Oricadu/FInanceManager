package com.oricadu.financemanager.ui.expenses;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.oricadu.financemanager.R;
import com.oricadu.financemanager.model.Category;
import com.oricadu.financemanager.model.Expense;
import com.oricadu.financemanager.ui.categories.CategoriesFragment;

import java.util.ArrayList;
import java.util.List;

public class ExpenseFragment extends Fragment {

    private ExpenseViewModel expenseViewModel;

    private FloatingActionButton fab;
    private EditText inputName, inputSum;
    private RecyclerView recyclerView;

    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static DatabaseReference reference = database.getReference();
    private static FirebaseAuth auth = FirebaseAuth.getInstance();
    private static FirebaseUser user = auth.getCurrentUser();

    private static FirebaseRecyclerAdapter<Expense, ExpenseViewHolder> adapter;

    public static class ExpenseAddDialog extends DialogFragment {

        EditText inputName, inputSum;
        Spinner spinner;

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
            spinner = (Spinner) dialogView.findViewById(R.id.spinner);


            inputName.setVisibility(View.VISIBLE);
            inputName.setHint(R.string.category_name);

            inputSum.setVisibility(View.VISIBLE);

            spinner.setVisibility(View.VISIBLE);

            DatabaseReference reference = database.getReference().child(user.getUid());
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<String> listCategories = new ArrayList<>();
                    for (DataSnapshot dataValues : snapshot.child("Categories").getChildren()) {
                        Category category = dataValues.getValue(Category.class);
                        String name = category.getCategoryName();
                        listCategories.add(name);

                    }
                    ListAdapter adapter = new ArrayAdapter<>(getContext(),
                            R.layout.spinner_item,
                            listCategories);
                    spinner.setAdapter((SpinnerAdapter) adapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity())
                    .setTitle("Add new expense")
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
                            String category = spinner.getSelectedItem().toString();

                            if (name.length() != 0 && sum.length() != 0) {
                                addExpense(name, Integer.parseInt(sum), category);

                            } else {
                                Toast.makeText(getActivity(), R.string.error_fill, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
            dialogBuilder.setView(dialogView);

            return dialogBuilder.create();

        }
    }

    protected static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;
        TextView expenseName;
        TextView expenseSum;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);

            categoryName = itemView.findViewById(R.id.category_name);
            expenseName = itemView.findViewById(R.id.expense_name);
            expenseSum = itemView.findViewById(R.id.expense_sum);
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        expenseViewModel =
                new ViewModelProvider(this).get(ExpenseViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_expense, container, false);
        /*final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/

//        spinner = root.findViewById(R.id.category_spinner);
        fab = root.findViewById(R.id.action_button);

        recyclerView = (RecyclerView) root.findViewById(R.id.expense_recycler);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);




        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExpenseAddDialog dialog = new ExpenseAddDialog();
                dialog.show(getChildFragmentManager(), "expense");

            }
        });

        reference.child(user.getUid()).child("Expenses").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.i("test", "test");
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        adapter = new FirebaseRecyclerAdapter<Expense, ExpenseViewHolder>(
                Expense.class,
                R.layout.expence_item,
                ExpenseViewHolder.class,
                reference.child(user.getUid())
                        .child("Expenses")) {
            @Override
            protected void populateViewHolder(ExpenseViewHolder expenseViewHolder, Expense expense, int i) {
                Log.i("User", "inside adapter user.uid=" + user.getUid());
                expenseViewHolder.categoryName.setText(expense.getCategoryName());
                expenseViewHolder.expenseSum.setText(String.valueOf(expense.getExpenseSum()));
                expenseViewHolder.expenseName.setText(String.valueOf(expense.getExpenseName()));
                Log.i("User", String.valueOf(expense.getExpenseSum()) + " spent " + expense.getCategoryName());
            }
        };

        recyclerView.setAdapter(adapter);

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


        return root;


    }

    public static void addExpense(String name, int sum, String spinner) {
        final String categoryName = spinner;
        String expenseName = name;
        final int expenseSum = sum;

        final Expense expense = new Expense(expenseName, expenseSum, categoryName);

        reference.child(user.getUid())
                .child("Expenses")
                .push()
                .setValue(expense);


        Log.i("add", categoryName);
        reference.child(user.getUid())
                .child("Categories")
                .child(categoryName)
                .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.i("add", task.getException() + "");
                } else {
                    Category category = task.getResult().getValue(Category.class);
                    int spentSum = category.getCategorySpentSum();

                    reference.child(user.getUid()).child("Categories")
                            .child(expense.getCategoryName())
                            .child("categorySpentSum")
                            .setValue(expenseSum + spentSum);



                }
            }
        });

        reference.child(user.getUid())
                .child("Categories")
                .child(categoryName)
                .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.i("add", task.getException() + "");
                } else {
                    Category category = task.getResult().getValue(Category.class);
                    int differenceSum = category.getCategoryDifferenceSum();
                    differenceSum -= expenseSum;


                    reference.child(user.getUid()).child("Categories")
                            .child(expense.getCategoryName())
                            .child("categoryDifferenceSum")
                            .setValue(differenceSum);

                }
            }
        });
    }


}
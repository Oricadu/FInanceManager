package com.oricadu.financemanager.ui.expenses;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

import java.util.ArrayList;
import java.util.List;

public class ExpenseFragment extends Fragment {

    private ExpenseViewModel expenseViewModel;

    private Spinner spinner;
    private FloatingActionButton fab;
    private EditText inputName, inputSum;
    private RecyclerView recyclerView;

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference reference;

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

        spinner = root.findViewById(R.id.category_spinner);
        fab = root.findViewById(R.id.action_button);
        inputName = root.findViewById(R.id.expense_name);
        inputSum = root.findViewById(R.id.expense_sum);

        recyclerView = (RecyclerView) root.findViewById(R.id.expense_recycler);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        FirebaseRecyclerAdapter<Expense, ExpenseViewHolder> adapter;

        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();


        if (user != null) {
            DatabaseReference reference = database.getReference().child(user.getUid());
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<String> listCategories = new ArrayList<>();
                    /*GenericTypeIndicator<List<Category>> t = new GenericTypeIndicator<List<Category>>() {};
                    listCategories = snapshot.child("Categories").getValue(t);*/
                    for (DataSnapshot dataValues : snapshot.child("Categories").getChildren()) {
                        Category category = dataValues.getValue(Category.class);
                        String name = category.getCategoryName();
                        listCategories.add(name);

                    }
                    ListAdapter adapter = new ArrayAdapter<>(root.getContext(),
                            android.R.layout.simple_spinner_item,
                            listCategories);
                    spinner.setAdapter((SpinnerAdapter) adapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String categoryName = spinner.getSelectedItem().toString();
                String expenseName = inputName.getText().toString().trim();
                final int expenseSum = Integer.parseInt(inputSum.getText().toString().trim());

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


        return root;


    }


}
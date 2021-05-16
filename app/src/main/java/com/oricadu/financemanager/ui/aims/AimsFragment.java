package com.oricadu.financemanager.ui.aims;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.oricadu.financemanager.R;
import com.oricadu.financemanager.model.Aim;

import java.text.DecimalFormat;

public class AimsFragment extends Fragment {

    private AimsViewModel aimsViewModel;

    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static DatabaseReference reference = database.getReference();
    private static FirebaseAuth auth = FirebaseAuth.getInstance();
    private static FirebaseUser user = auth.getCurrentUser();

    private RecyclerView recyclerView;
    private FloatingActionButton button;

    private Button allocate;
    private EditText allocateSum;

    public static class AimAddDialog extends DialogFragment {

        EditText inputName, inputSum, inputPercent;

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
            inputPercent = (EditText) dialogView.findViewById(R.id.aim_percent);

            inputName.setVisibility(View.VISIBLE);
            inputName.setHint(R.string.aim_name);
            inputSum.setVisibility(View.VISIBLE);
            inputPercent.setVisibility(View.VISIBLE);
            inputPercent.setHint(R.string.allocation_percent);


            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity())
                    .setTitle("Add new aim")
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
                            String percent = inputPercent.getText().toString().trim();

                            if (name.length() != 0 && sum.length() != 0) {
                                addAim(name, Integer.parseInt(sum), Integer.parseInt(percent
                                ));

                            } else {
                                Toast.makeText(getActivity(), R.string.error_fill, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
            dialogBuilder.setView(dialogView);

            return dialogBuilder.create();

        }
    }

    protected static class AimViewHolder extends RecyclerView.ViewHolder {
        TextView aimName;
        TextView aimSum;
        TextView aimAccumulatedSum;
        TextView aimPercent;
        TextView aimAllocatePercent;
        CardView cardView;

        public AimViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.card_view);

            aimName = itemView.findViewById(R.id.input_name);
            aimSum = itemView.findViewById(R.id.input_sum);
            aimAccumulatedSum = itemView.findViewById(R.id.aim_accumulated_sum);
            aimPercent = itemView.findViewById(R.id.aim_percent);
            aimAllocatePercent = itemView.findViewById(R.id.aim_allocate_percent);

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
        aimsViewModel =
                new ViewModelProvider(this).get(AimsViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_aims, container, false);
        /*final TextView textView = root.findViewById(R.id.text_categories);
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/

//        auth = FirebaseAuth.getInstance();
//        reference = database.getReference();
//        user = auth.getCurrentUser();

        if (user != null) {
            Log.i("User", "user.uid=" + user.getUid());

        }

        button = root.findViewById(R.id.action_button2);
        allocate = root.findViewById(R.id.allocate);
        allocateSum = root.findViewById(R.id.aim_allocate_sum);



        recyclerView = (RecyclerView) root.findViewById(R.id.aim_recycler);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        if (user != null) {


            final FirebaseRecyclerAdapter<Aim, AimsFragment.AimViewHolder> adapter;
            adapter = new FirebaseRecyclerAdapter<Aim, AimsFragment.AimViewHolder>(
                    Aim.class,
                    R.layout.aim_item,
                    AimsFragment.AimViewHolder.class,
                    reference.child(user.getUid())
                            .child("Aims")) {
                @Override
                protected void populateViewHolder(AimsFragment.AimViewHolder aimsViewHolder, Aim aim, int i) {

                    DecimalFormat format = new DecimalFormat("##%");
                    String percent = format.format((double) aim.getAimAccumulatedSum() / aim.getAimSum());
                    String allocationPercent = format.format((double) aim.getAimPercent() / 100);
                    Log.i("aim", "inside adapter user.uid=" + user.getUid());

                    aimsViewHolder.aimName.setText(aim.getAimName());
                    aimsViewHolder.aimSum.setText(String.valueOf(aim.getAimSum()));
                    aimsViewHolder.aimAccumulatedSum.setText(String.valueOf(aim.getAimAccumulatedSum()));
                    aimsViewHolder.aimPercent.setText(percent);
                    aimsViewHolder.aimAllocatePercent.setText(allocationPercent);
                    Log.i("aim", aim.getAimAccumulatedSum() + " sum " + aim.getAimSum());

                    Log.i("aim", "percent " + aim.getAimAccumulatedSum() + "  " +  aim.getAimSum());
                    if ((aim.getAimAccumulatedSum() != 0) && ((double) aim.getAimAccumulatedSum() / aim.getAimSum() > 1)) {
                        aimsViewHolder.cardView.setCardBackgroundColor(Color.parseColor("#3C6045"));
                    }

                }

            };

            recyclerView.setAdapter(adapter);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AimAddDialog dialog = new AimAddDialog();
                    dialog.show(getChildFragmentManager(), "aim");
                }
            });

            allocate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (allocateSum.getText().toString().trim().length() != 0) {
                        final int sum = Integer.parseInt(allocateSum.getText().toString().trim());
                        reference.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataValues : snapshot.child("Aims").getChildren()) {
                                    Aim aim = dataValues.getValue(Aim.class);
                                    double percent = (double) aim.getAimPercent() / 100;
                                    double accumulatedSum = aim.getAimAccumulatedSum();
                                    double newSum = (double) accumulatedSum + sum * percent;
                                    dataValues.getRef().child("aimAccumulatedSum").setValue(newSum);

                                    Log.i("aim", "aim " + aim);
                                    Log.i("aim", "percent " + percent);
                                    Log.i("aim", "oldSum " + accumulatedSum);
                                    Log.i("aim", "newSum " + newSum);
                                    Log.i("aim", "snapshot " + dataValues.getRef().child("aimAccumulatedSum"));
                                    Log.i("aim", "key " + dataValues.getKey());


                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        allocateSum.setText("");
                    } else {
                        Toast.makeText(getActivity(), "fill in the firld 'Sum'", Toast.LENGTH_LONG).show();
                    }

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

    private static void addAim(String name, int sum, int percent) {
        String aimName = name;
        int aimSum = sum;
        int aimPercent = percent;
        reference.child(user.getUid())
                .child("Aims")
                .child(aimName)
                .setValue(new Aim(aimName,
                        aimSum,
                        0, aimPercent));
    }

}
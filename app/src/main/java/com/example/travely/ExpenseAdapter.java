package com.example.travely;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.DateFormat;
import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    private List<Expense> expenses;
    private OnExpenseClickListener listener;

    public ExpenseAdapter(List<Expense> expenses, OnExpenseClickListener listener) {
        this.expenses = expenses;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_item, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense expense = expenses.get(position);
        holder.nameTextView.setText(expense.getName());
        holder.amountTextView.setText(String.valueOf(expense.getAmount()));
        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(holder.itemView.getContext());
        holder.dateTextView.setText(dateFormat.format(expense.getDate()));

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onExpenseClick(expense);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return expenses == null ? 0 : expenses.size();
    }

    public void setExpenses(List<Expense> expenses) {
        this.expenses = expenses;
        notifyDataSetChanged();
    }

    public interface OnExpenseClickListener {
        void onExpenseClick(Expense expense);
    }

    static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView amountTextView;
        TextView dateTextView;
        Button deleteButton;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.expenseNameTextView);
            amountTextView = itemView.findViewById(R.id.expenseAmountTextView);
            dateTextView = itemView.findViewById(R.id.expenseDateTextView);
            deleteButton = itemView.findViewById(R.id.deleteExpenseButton);
        }
    }
}

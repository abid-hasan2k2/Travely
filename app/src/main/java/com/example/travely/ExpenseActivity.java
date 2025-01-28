package com.example.travely;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExpenseActivity extends AppCompatActivity {

    private EditText expenseNameEditText;
    private EditText expenseAmountEditText;
    private EditText totalMembersEditText;
    private Button addExpenseButton;
    private TextView totalExpenseTextView;
    private TextView averageAmountTextView;
    private RecyclerView expenseRecyclerView;
    private ExpenseAdapter expenseAdapter;
    private ExpenseDatabase expenseDatabase;
    private List<Expense> expenseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);
        // Change the status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.black)); // replace 'your_status_bar_color' with your color resource
        }

        expenseNameEditText = findViewById(R.id.expenseNameEditText);
        expenseAmountEditText = findViewById(R.id.expenseAmountEditText);
        totalMembersEditText = findViewById(R.id.totalMembersEditText);
        addExpenseButton = findViewById(R.id.addExpenseButton);
        totalExpenseTextView = findViewById(R.id.totalExpenseTextView);
        averageAmountTextView = findViewById(R.id.averageAmountTextView);
        expenseRecyclerView = findViewById(R.id.expenseRecyclerView);

        expenseDatabase = ExpenseDatabase.getInstance(this);

        expenseRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        expenseList = new ArrayList<>();
        expenseAdapter = new ExpenseAdapter(expenseList, new ExpenseAdapter.OnExpenseClickListener() {
            @Override
            public void onExpenseClick(Expense expense) {
                new DeleteExpenseTask().execute(expense);
            }
        });
        expenseRecyclerView.setAdapter(expenseAdapter);

        loadExpenses();

        addExpenseButton.setOnClickListener(v -> {
            String expenseName = expenseNameEditText.getText().toString().trim();
            String expenseAmountStr = expenseAmountEditText.getText().toString().trim();
            String totalMembersStr = totalMembersEditText.getText().toString().trim();

            if (expenseName.isEmpty() || expenseAmountStr.isEmpty() || totalMembersStr.isEmpty()) {
                // Show error message if fields are empty
                return;
            }

            double expenseAmount = Double.parseDouble(expenseAmountStr);
            int totalMembers = Integer.parseInt(totalMembersStr);
            Expense expense = new Expense(expenseName, expenseAmount, new Date());
            new AddExpenseTask().execute(expense);

            // Clear input fields after adding expense
            expenseNameEditText.setText("");
            expenseAmountEditText.setText("");
        });
    }

    private void loadExpenses() {
        new LoadExpensesTask().execute();
    }

    private class LoadExpensesTask extends AsyncTask<Void, Void, List<Expense>> {
        @Override
        protected List<Expense> doInBackground(Void... voids) {
            return expenseDatabase.expenseDao().getAllExpenses();
        }

        @Override
        protected void onPostExecute(List<Expense> expenses) {
            expenseList.clear();
            expenseList.addAll(expenses);
            expenseAdapter.setExpenses(expenses);
            updateTotalExpense();
        }
    }

    private class AddExpenseTask extends AsyncTask<Expense, Void, Void> {
        @Override
        protected Void doInBackground(Expense... expenses) {
            expenseDatabase.expenseDao().insertExpense(expenses[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            loadExpenses();
        }
    }

    private class DeleteExpenseTask extends AsyncTask<Expense, Void, Void> {
        @Override
        protected Void doInBackground(Expense... expenses) {
            expenseDatabase.expenseDao().deleteExpense(expenses[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            loadExpenses();
        }
    }

    private void updateTotalExpense() {
        double total = 0;
        for (Expense expense : expenseList) {
            total += expense.getAmount();
        }
        totalExpenseTextView.setText("Total: Tk " + String.format("%.2f", total));

        String totalMembersStr = totalMembersEditText.getText().toString().trim();
        if (!totalMembersStr.isEmpty()) {
            int totalMembers = Integer.parseInt(totalMembersStr);
            if (totalMembers > 0) {
                double average = total / totalMembers;
                averageAmountTextView.setText("Average per Member: TK " + String.format(" %.2f", average));
            } else {
                averageAmountTextView.setText("Average per Member: TK 0.00");
            }
        }
    }
}

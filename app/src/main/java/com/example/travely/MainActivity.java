package com.example.travely;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ImageView profileImageView;
    private TextView welcomeTextView;
    private FirebaseAuth mAuth;
    private ImageView calculateImageView;
    private RecyclerView categoryRecyclerView;
    private TextInputEditText searchEditText;
    private DatabaseReference databaseReference;

    private List<CategoryItem> categories;
    private CategoryAdapter adapter;

    private WebView webView;
    private ProgressBar progressBar;
    private RecyclerView bannerRecyclerView;
    private BannerAdapter bannerAdapter;
    private List<BannerItem> banners;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Change the status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.black)); // replace 'your_status_bar_color' with your color resource
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Add click listener for the toolbar
        toolbar.setOnClickListener(v -> showLogoutDialog());

        profileImageView = findViewById(R.id.profileImageView);
        welcomeTextView = findViewById(R.id.welcomeTextView);
        // Initialize RecyclerView and set up LinearLayoutManager for horizontal scrolling


        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);

// Initialize RecyclerView and set up LinearLayoutManager for horizontal scrolling
        bannerRecyclerView = findViewById(R.id.bannerRecyclerView);
        // Initialize RecyclerView and set up LinearLayoutManager for horizontal scrolling
        bannerRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));


        // Initialize banner items and adapter
        banners = new ArrayList<>();
        banners.add(new BannerItem(R.drawable.event_banner_01, "13 Feb, 2025", "Sajek Valley", "Rangamati, Bangladesh",
                "https://docs.google.com/forms/d/e/1FAIpQLSdRM4L9IQtwuqJ7dK-GqZaQTzlXC-brBrmme2HZCT-mydHZ9w/viewform?usp=sharing"));
        banners.add(new BannerItem(R.drawable.event_banner_02, "21 Feb, 2025", "CoxsBazar Sea Beach", "CoxsBazar, Bangladesh",
                "https://docs.google.com/forms/d/e/1FAIpQLSfUn8lCNEZ6xad0jRTvyfJdv84Kg3GIgSum0d_s_egJtShNFA/viewform?usp=sharing"));
        banners.add(new BannerItem(R.drawable.event_banner_03, "28 Feb, 2025", "Bandarban Mountain", "Bandarban, Bangladesh",
                "https://docs.google.com/forms/d/e/1FAIpQLScyxJpAGILMYVtRcszul4DwIARyFC-GQAkIvqK9l9OtMHe4TQ/viewform?usp=sharing"));

        bannerAdapter = new BannerAdapter(MainActivity.this, banners);

        // Set RecyclerView adapter
        bannerRecyclerView.setAdapter(bannerAdapter);
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

        calculateImageView = findViewById(R.id.calculateImageView);
        categoryRecyclerView = findViewById(R.id.categoryRecyclerView);
        searchEditText = findViewById(R.id.searchEditText);


        setupCategoryRecyclerView();
        setClickListeners();
        loadUserData();

        // Add text change listener to searchEditText
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not used in this implementation
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCategories(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not used in this implementation
            }
        });
    }

    private void showLogoutDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    signOut();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    private String getUrlForPosition(int position) {
        switch (position) {
            case 0:
                return "https://docs.google.com/forms/d/e/1FAIpQLSdRM4L9IQtwuqJ7dK-GqZaQTzlXC-brBrmme2HZCT-mydHZ9w/viewform?usp=sharing";
            case 1:
                return "https://docs.google.com/forms/d/e/1FAIpQLSfUn8lCNEZ6xad0jRTvyfJdv84Kg3GIgSum0d_s_egJtShNFA/viewform?usp=sharing";
            case 2:
                return "https://docs.google.com/forms/d/e/1FAIpQLScyxJpAGILMYVtRcszul4DwIARyFC-GQAkIvqK9l9OtMHe4TQ/viewform?usp=sharing";
            default:
                return "";
        }
    }

    private void openUrlInBrowser(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);

        // Verify that the intent will resolve to at least one activity
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "No browser app found", Toast.LENGTH_SHORT).show();
        }
    }




    public void openEventFormActivity(String url) {
        Intent intent = new Intent(MainActivity.this, EventFormActivity.class);
        intent.putExtra("GOOGLE_FORM_URL", url);
        startActivity(intent);
    }

    private void setupCategoryRecyclerView() {
        categories = new ArrayList<>();
        categories.add(new CategoryItem("Mountain", R.drawable.mountain));
        categories.add(new CategoryItem("Sea", R.drawable.sea));
        categories.add(new CategoryItem("Waterfall", R.drawable.waterfall));
        categories.add(new CategoryItem("Tea Garden", R.drawable.tea_garden));
        categories.add(new CategoryItem("River", R.drawable.river));
        categories.add(new CategoryItem("Museum", R.drawable.museum));
        categories.add(new CategoryItem("Park", R.drawable.park));
        categories.add(new CategoryItem("Mosque", R.drawable.mosque));
        categories.add(new CategoryItem("Temple", R.drawable.temple));
        categories.add(new CategoryItem("Forest", R.drawable.forest));

        // Create adapter with initial categories list
        adapter = new CategoryAdapter(this, categories, this::openCategoryActivity);

        // Set RecyclerView layout manager and adapter
        categoryRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        categoryRecyclerView.setAdapter(adapter);
    }

    private void filterCategories(String query) {
        List<CategoryItem> filteredList = new ArrayList<>();

        for (CategoryItem item : categories) {
            // Check if category name contains the search query (case insensitive)
            if (item.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(item);
            }
        }

        // Update adapter with filtered list
        adapter.filterList(filteredList);
    }

    private void setClickListeners() {
        calculateImageView.setOnClickListener(v -> openActivity(ExpenseActivity.class));

    }

    private void openCategoryActivity(String category) {
        Class<?> activityClass;
        switch (category) {
            case "Mountain":
                activityClass = MountainActivity.class;
                break;
            case "Sea":
                activityClass = SeaActivity.class;
                break;
            case "Waterfall":
                activityClass = WaterFallActivity.class;
                break;
            case "Tea Garden":
                activityClass = TeaGardenActivity.class;
                break;
            case "River":
                activityClass = RiverActivity.class;
                break;
            case "Museum":
                activityClass = MuseumActivity.class;
                break;
            case "Park":
                activityClass = ParkActivity.class;
                break;
            case "Mosque":
                activityClass = MosqueActivity.class;
                break;
            case "Temple":
                activityClass = TempleActivity.class;
                break;
            case "Forest":
                activityClass = ForestActivity.class;
                break;
            default:
                return;
        }
        openActivity(activityClass);
    }

    private void openActivity(Class<?> activityClass) {
        Intent intent = new Intent(MainActivity.this, activityClass);
        startActivity(intent);
    }

    private void loadUserData() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            String userId = firebaseUser.getUid();
            DatabaseReference currentUserDb = databaseReference.child(userId);
            currentUserDb.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String userName = snapshot.child("name").getValue(String.class);
                        String userProfileImage = snapshot.child("profileImageUrl").getValue(String.class);

                        if (userName != null && userProfileImage != null) {
                            welcomeTextView.setText("Hey, " + userName + "\nLet's Have A Tour");

                            Glide.with(MainActivity.this)
                                    .load(userProfileImage)
                                    .into(profileImageView);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle error
                }
            });
        }
    }

    private void signOut() {
        mAuth.signOut();
        clearUserData();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void setupWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setSupportZoom(true);
        webSettings.setDefaultTextEncodingName("utf-8");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Error loading page: " + description, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadFormInWebView(String url) {
        webView.setVisibility(View.VISIBLE);
        bannerRecyclerView.setVisibility(View.GONE);
        webView.loadUrl(url);
    }

    @Override
    public void onBackPressed() {
        if (webView.getVisibility() == View.VISIBLE) {
            webView.setVisibility(View.GONE);
            bannerRecyclerView.setVisibility(View.VISIBLE);
        } else {
            super.onBackPressed();
        }
    }

    private void clearUserData() {
        // Clear SharedPreferences or any other data you want to clear on sign out
    }
}

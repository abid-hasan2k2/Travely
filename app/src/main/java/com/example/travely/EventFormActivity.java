package com.example.travely;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EventFormActivity extends AppCompatActivity {

    private WebView webView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_form);

        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);

        // Show ProgressBar when loading the page
        progressBar.setVisibility(View.VISIBLE);

        // Get the URL from the intent
        Intent intent = getIntent();
        String googleFormUrl = intent.getStringExtra("GOOGLE_FORM_URL");

        if (googleFormUrl != null && !googleFormUrl.isEmpty()) {
            // Configure WebView settings
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            // ... (other WebView settings)

            // Set WebViewClient to handle page loading
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    // Page has finished loading, hide ProgressBar
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                    // Handle errors
                    Toast.makeText(EventFormActivity.this, "Error loading page", Toast.LENGTH_SHORT).show();
                }
            });

            // Load the URL in the WebView
            webView.loadUrl(googleFormUrl);
        } else {
            Toast.makeText(this, "Invalid URL received", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}

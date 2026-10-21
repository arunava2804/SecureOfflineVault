package com.arunava.securevault.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.arunava.securevault.biometric.BiometricAuthHelper;
import com.arunava.securevault.databinding.ActivityVaultBinding;
import com.arunava.securevault.viewmodel.VaultViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class VaultActivity extends AppCompatActivity {

    private ActivityVaultBinding binding;
    private VaultViewModel viewModel;
    private VaultCredentialAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVaultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(VaultViewModel.class);
        setupRecyclerView();
        setupObservers();
        setupListeners();
    }

    private void setupRecyclerView() {
        adapter = new VaultCredentialAdapter(new VaultCredentialAdapter.CredentialActionListener() {
            @Override
            public String onRevealSecret(String encryptedSecret) {
                return viewModel.decryptSecret(encryptedSecret);
            }

            @Override
            public void onDeleteCredential(long id) {
                viewModel.deleteCredential(id);
                Toast.makeText(VaultActivity.this, "Credential Removed", Toast.LENGTH_SHORT).show();
            }
        });
        binding.rvVaultCredentials.setLayoutManager(new LinearLayoutManager(this));
        binding.rvVaultCredentials.setAdapter(adapter);
    }

    private void setupObservers() {
        viewModel.getIsUnlocked().observe(this, isUnlocked -> {
            if (isUnlocked) {
                binding.layoutLockedState.setVisibility(View.GONE);
                binding.layoutUnlockedState.setVisibility(View.VISIBLE);
            } else {
                binding.layoutLockedState.setVisibility(View.VISIBLE);
                binding.layoutUnlockedState.setVisibility(View.GONE);
            }
        });

        viewModel.getCredentials().observe(this, list -> {
            adapter.updateList(list);
        });
    }

    private void setupListeners() {
        binding.btnUnlockVault.setOnClickListener(v -> triggerBiometricUnlock());
        binding.btnLockVault.setOnClickListener(v -> viewModel.setUnlocked(false));
        binding.btnAddCredential.setOnClickListener(v -> showAddDialog());
    }

    private void triggerBiometricUnlock() {
        if (BiometricAuthHelper.canAuthenticate(this)) {
            BiometricAuthHelper.prompt(this, new BiometricAuthHelper.AuthCallback() {
                @Override
                public void onSuccess() {
                    viewModel.setUnlocked(true);
                }

                @Override
                public void onError(String message) {
                    Toast.makeText(VaultActivity.this, "Authentication Failed: " + message, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Fallback for emulators without fingerprint hardware
            viewModel.setUnlocked(true);
            Toast.makeText(this, "Biometrics bypassed (Hardware not enrolled)", Toast.LENGTH_SHORT).show();
        }
    }

    private void showAddDialog() {
        View dialogView = LayoutInflater.from(this).inflate(android.R.layout.simple_list_item_1, null, false);
        EditText etTitle = new EditText(this);
        etTitle.setHint("Title (e.g. GitHub Token, AWS Key)");
        EditText etAccount = new EditText(this);
        etAccount.setHint("Account / Username");
        EditText etSecret = new EditText(this);
        etSecret.setHint("Secret / Password / API Key");

        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);
        layout.addView(etTitle);
        layout.addView(etAccount);
        layout.addView(etSecret);

        new MaterialAlertDialogBuilder(this)
                .setTitle("Add Encrypted Credential")
                .setView(layout)
                .setPositiveButton("Save Encrypted", (dialog, which) -> {
                    String title = etTitle.getText().toString().trim();
                    String account = etAccount.getText().toString().trim();
                    String secret = etSecret.getText().toString().trim();
                    if (!title.isEmpty() && !secret.isEmpty()) {
                        viewModel.addCredential(title, account, secret, "CREDENTIAL");
                        Toast.makeText(this, "Credential Encrypted and Saved", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}

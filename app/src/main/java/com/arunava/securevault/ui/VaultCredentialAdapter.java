package com.arunava.securevault.ui;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.arunava.securevault.databinding.ItemVaultCredentialBinding;
import com.arunava.securevault.model.VaultCredential;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VaultCredentialAdapter extends RecyclerView.Adapter<VaultCredentialAdapter.VaultViewHolder> {

    public interface CredentialActionListener {
        String onRevealSecret(String encryptedSecret);
        void onDeleteCredential(long id);
    }

    private final List<VaultCredential> list = new ArrayList<>();
    private final Map<Long, Boolean> revealedMap = new HashMap<>();
    private final CredentialActionListener listener;

    public VaultCredentialAdapter(CredentialActionListener listener) {
        this.listener = listener;
    }

    public void updateList(List<VaultCredential> newList) {
        list.clear();
        if (newList != null) list.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VaultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemVaultCredentialBinding binding = ItemVaultCredentialBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new VaultViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull VaultViewHolder holder, int position) {
        VaultCredential item = list.get(position);
        holder.binding.tvTitle.setText(item.getTitle());
        holder.binding.tvAccount.setText(item.getAccountName());
        holder.binding.tvCategory.setText(item.getCategory().toUpperCase());

        boolean isRevealed = Boolean.TRUE.equals(revealedMap.get(item.getId()));
        if (isRevealed) {
            holder.binding.tvSecretValue.setText(listener.onRevealSecret(item.getEncryptedSecret()));
            holder.binding.btnToggleSecret.setText("Hide");
        } else {
            holder.binding.tvSecretValue.setText("••••••••••••••••••••");
            holder.binding.btnToggleSecret.setText("Reveal");
        }

        holder.binding.btnToggleSecret.setOnClickListener(v -> {
            revealedMap.put(item.getId(), !isRevealed);
            notifyItemChanged(position);
        });

        holder.binding.btnDelete.setOnClickListener(v -> listener.onDeleteCredential(item.getId()));
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class VaultViewHolder extends RecyclerView.ViewHolder {
        final ItemVaultCredentialBinding binding;
        VaultViewHolder(ItemVaultCredentialBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

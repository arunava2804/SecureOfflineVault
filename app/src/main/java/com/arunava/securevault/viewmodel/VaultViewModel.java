package com.arunava.securevault.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.arunava.securevault.crypto.KeystoreCryptoManager;
import com.arunava.securevault.db.VaultDatabaseHelper;
import com.arunava.securevault.model.VaultCredential;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VaultViewModel extends AndroidViewModel {

    private final MutableLiveData<Boolean> isUnlocked = new MutableLiveData<>(false);
    private final MutableLiveData<List<VaultCredential>> credentials = new MutableLiveData<>();
    private final VaultDatabaseHelper dbHelper;
    private final KeystoreCryptoManager cryptoManager;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public VaultViewModel(@NonNull Application application) {
        super(application);
        dbHelper = new VaultDatabaseHelper(application);
        cryptoManager = new KeystoreCryptoManager();
    }

    public LiveData<Boolean> getIsUnlocked() { return isUnlocked; }
    public LiveData<List<VaultCredential>> getCredentials() { return credentials; }

    public void setUnlocked(boolean unlocked) {
        isUnlocked.setValue(unlocked);
        if (unlocked) {
            loadCredentials();
        }
    }

    public void loadCredentials() {
        executor.execute(() -> {
            List<VaultCredential> list = dbHelper.getAllCredentials();
            credentials.postValue(list);
        });
    }

    public void addCredential(String title, String account, String plainSecret, String category) {
        executor.execute(() -> {
            String encrypted = cryptoManager.encrypt(plainSecret);
            dbHelper.insertCredential(title, account, encrypted, category);
            loadCredentials();
        });
    }

    public void deleteCredential(long id) {
        executor.execute(() -> {
            dbHelper.deleteCredential(id);
            loadCredentials();
        });
    }

    public String decryptSecret(String encryptedSecret) {
        return cryptoManager.decrypt(encryptedSecret);
    }
}

package com.arunava.securevault.model;

public class VaultCredential {
    private final long id;
    private final String title;
    private final String accountName;
    private final String encryptedSecret;
    private final String category;

    public VaultCredential(long id, String title, String accountName, String encryptedSecret, String category) {
        this.id = id;
        this.title = title;
        this.accountName = accountName;
        this.encryptedSecret = encryptedSecret;
        this.category = category;
    }

    public long getId() { return id; }
    public String getTitle() { return title; }
    public String getAccountName() { return accountName; }
    public String getEncryptedSecret() { return encryptedSecret; }
    public String getCategory() { return category; }
}

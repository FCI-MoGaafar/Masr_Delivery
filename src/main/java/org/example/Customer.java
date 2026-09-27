package org.example;

import java.util.*;

public class Customer {
    private final int id;
    private final String name;
    private final String mobNumber;
    private final HashSet<Address> addresses=new HashSet<>();
    private double walletBalance;
    private int completeOrderCount;
    private final LinkedList<String> recentSearches = new LinkedList<>();

    public Customer(int id, String name, String mobNumber, double walletBalance) {
        if(id<=0)
            throw new IllegalArgumentException("Invalid ID");
        if(name==null||name.isBlank())
            throw new IllegalArgumentException("Invalid name");
        if(mobNumber==null||mobNumber.isBlank()||!mobNumber.matches("^(010|011|012|015)\\d{8}$"))
            throw new IllegalArgumentException("Invalid Mobile Number");
        if(walletBalance<0)
            throw new IllegalArgumentException("Invalid wallet balance");
        this.id = id;
        this.name = name;
        this.mobNumber = mobNumber;
        this.walletBalance = walletBalance;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getMobNumber() {
        return mobNumber;
    }

    public HashSet<Address> getAddresses() {
        return new  HashSet<>(addresses);
    }

    public boolean hasAddress(Address address) {
        return addresses.contains(address);
    }

    public double getWalletBalance() {
        return walletBalance;
    }

    public LoyaltyTier getLoyaltyTier() {
        if(completeOrderCount>=0&&completeOrderCount<=9)
            return LoyaltyTier.Bronze;
        else if(completeOrderCount>=10&&completeOrderCount<=29)
            return LoyaltyTier.Silver;
        else
            return LoyaltyTier.Gold;

    }

    public int getCompleteOrderCount() {
        return completeOrderCount;
    }

    public void addAddress(Address address) {
        if(address==null)
            throw new IllegalArgumentException("Invalid address");
        if (addresses.contains(address))
            throw new IllegalArgumentException("this Address already exists");
        addresses.add(address);
    }
    public void removeAddress(Address address) {
        if(address==null)
            throw new IllegalArgumentException("Invalid address");
        if(!addresses.remove(address))
            throw new IllegalArgumentException("Address not found");
    }
    public void addWalletBalance(double amount) {
        if(amount<=0)
            throw new IllegalArgumentException("amount cannot be negative or zero");
        walletBalance += amount;
    }
    public void deductWalletBalance(double amount) {
        if(amount<=0)
            throw new IllegalArgumentException("amount cannot be negative or zero");
        if (amount>walletBalance)
            throw new IllegalArgumentException("amount cannot be greater than wallet balance");
        walletBalance -= amount;
    }
    public void increaseCompleteOrderCount() {
        completeOrderCount++;
    }
    public void addSearch(String query) {
        if (query != null && !query.isBlank()) {
            recentSearches.addFirst(query.trim());
            if (recentSearches.size() > 5) {
                recentSearches.removeLast(); // الاحتفاظ بآخر 5 فقط
            }
        }
    }

    public List<String> getRecentSearches() {
        return Collections.unmodifiableList(recentSearches);
    }

}

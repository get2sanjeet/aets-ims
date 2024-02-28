package main.java.pdf;

import java.util.Objects;

public final class Buyer implements CompanyInfo {
    private final String companyName;
    private final String address;
    private final String TaxIdentificationNumber;
    private final String phoneNumber;
    private final String email;

    public Buyer(String companyName, String address, String TaxIdentificationNumber, String phoneNumber, String email) {
        this.companyName = companyName;
        this.address = address;
        this.TaxIdentificationNumber = TaxIdentificationNumber;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public String companyName() {
        return companyName;
    }

    public String address() {
        return address;
    }

    public String TaxIdentificationNumber() {
        return TaxIdentificationNumber;
    }

    public String phoneNumber() {
        return phoneNumber;
    }

    public String email() {
        return email;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        Buyer that = (Buyer) obj;
        return Objects.equals(this.companyName, that.companyName) &&
                Objects.equals(this.address, that.address) &&
                Objects.equals(this.TaxIdentificationNumber, that.TaxIdentificationNumber) &&
                Objects.equals(this.phoneNumber, that.phoneNumber) &&
                Objects.equals(this.email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(companyName, address, TaxIdentificationNumber, phoneNumber, email);
    }

    @Override
    public String toString() {
        return "Buyer[" +
                "companyName=" + companyName + ", " +
                "address=" + address + ", " +
                "TaxIdentificationNumber=" + TaxIdentificationNumber + ", " +
                "phoneNumber=" + phoneNumber + ", " +
                "email=" + email + ']';
    }


}

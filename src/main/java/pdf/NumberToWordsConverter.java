package main.java.pdf;


import java.text.NumberFormat;

public class NumberToWordsConverter {
    private static final String[] units = {
            "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine"
    };

    private static final String[] teens = {
            "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"
    };

    private static final String[] tens = {
            "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"
    };

    private static final String[] thousands = {
            "", "Thousand", "Million", "Billion", "Trillion"
    };

    public static void main(String[] args) {
        float price = 174.9f;
        String priceInWords = convertPriceToWords(price);
        System.out.println("Price in words: " + priceInWords);
    }

    public static String convertPriceToWords(float price) {
        if (price < 0 || price > 999_999_999_999.99f) {
            throw new IllegalArgumentException("Price out of range");
        }


        String formattedPrice = String.format("%.2f", price).replace(",",".");

        String[] parts = formattedPrice.split("\\.");
        int dollars = Integer.parseInt(parts[0]);
        int cents = Integer.parseInt(parts[1]);

        return convert(dollars) + " Dollars and " + convert(cents) + " Cents";
    }

    private static String convert(long n) {
        if (n == 0) {
            return "Zero";
        }

        String result = "";

        int i = 0;
        while (n > 0) {
            if (n % 1000 != 0) {
                result = convertLessThanThousand(n % 1000) + " " + thousands[i] + " " + result;
            }
            n /= 1000;
            i++;
        }

        return result.trim();
    }

    private static String convertLessThanThousand(long n) {
        if (n >= 100) {
            return units[(int) (n / 100)] + " Hundred " + convertLessThanThousand(n % 100);
        } else if (n >= 11 && n <= 19) {
            return teens[(int) (n - 11)];
        } else {
            return tens[(int) (n / 10)] + ((n % 10 != 0) ? " " + units[(int) (n % 10)] : "");
        }
    }
}

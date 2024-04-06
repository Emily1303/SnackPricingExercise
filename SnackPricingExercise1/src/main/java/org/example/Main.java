package org.example;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

//        receiving the order and splitting it by a comma -> array
        String order = scanner.nextLine();
        String[] splitOrderArray = order.split(",");

//        taking the first element of the array order - this is the clientID and receiving the client name
        int clientID = Integer.parseInt(splitOrderArray[0]);
        String clientName = giveClientNameById(clientID);

        Map<String, Integer> productsQuantityMap = new HashMap<>();
        Map<String, Double> standardUnitPriceMap = new HashMap<>();
        Map<String, Double> promotionalUnitPriceMap = new HashMap<>();
        Map<String, Double> priceWithoutClientDiscountsMap = new HashMap<>();

        for (int i = 1; i < splitOrderArray.length; i++) {
            String orderedProductAndQuantity = splitOrderArray[i];
            String[] splitProductAndQuantity = orderedProductAndQuantity.split("=");

            int productID = Integer.parseInt(splitProductAndQuantity[0]);
            int productQuantity = Integer.parseInt(splitProductAndQuantity[1]);
            String productName = giveProductNameById(productID);
            double unitPrice = giveStandardUnitPriceByProductId(productID);
            double promotionalUnitPrice = givePromotionalUnitPrice(productID, unitPrice);

            productsQuantityMap.put(productName, productQuantity);
            standardUnitPriceMap.put(productName, unitPrice);

            promotionalUnitPriceMap.put(productName, unitPrice);

            if (promotionalUnitPrice != 0.00) {
                Double value = promotionalUnitPriceMap.get(productName);
                value = promotionalUnitPrice;
                promotionalUnitPriceMap.put(productName, value);
            }

            Integer productsNumber = productsQuantityMap.get(productName);
            BigDecimal resultFormat =
                    new BigDecimal(productsNumber * promotionalUnitPriceMap.get(productName)).
                            setScale(2, RoundingMode.HALF_UP);

            double resultFormatted = resultFormat.doubleValue();
            priceWithoutClientDiscountsMap.put(productName, resultFormatted);
        }

        double totalPriceWithoutClientDiscounts = 0.00;
        List<Double> prices = priceWithoutClientDiscountsMap.values().stream().collect(Collectors.toList());
        for (int i = 0; i < prices.size(); i++) {
            totalPriceWithoutClientDiscounts += prices.get(i);
        }

        double basicClientDiscount = giveBasicClientDiscount(clientID, totalPriceWithoutClientDiscounts);
        Map<String, Double> clientDiscountMap = new HashMap<>();
        clientDiscountMap.put("Basic Client Discount", basicClientDiscount);
        double totalPriceWithBasicClientDiscount = totalPriceWithoutClientDiscounts - basicClientDiscount;

        double additionalVolumeDiscount = giveAdditionalVolumeDiscount(clientID, totalPriceWithBasicClientDiscount);
        clientDiscountMap.put("Additional Volume Discount", additionalVolumeDiscount);

        double resultPriceAfterDiscounts = totalPriceWithBasicClientDiscount - additionalVolumeDiscount;

        System.out.printf("Client: %s%n", clientName);
        System.out.println("Product       Quantity     Standard Unit Price   " +
                "Promotional Unit " +
                "Price   " +
                "Line Total");


        Set<String> keys = productsQuantityMap.keySet();
        List<String> listKeys = keys.stream().collect(Collectors.toList());

        for (int j = 0; j < listKeys.size(); j++) {
            System.out.printf("%s   %d            ",
                    listKeys.get(j), productsQuantityMap.get(listKeys.get(j)));

            System.out.printf("EUR %.2f           ",
                    standardUnitPriceMap.get(listKeys.get(j)));

            if (standardUnitPriceMap.get(listKeys.get(j)) != promotionalUnitPriceMap.get(listKeys.get(j))) {
                System.out.printf("EUR %.5f           ",
                        promotionalUnitPriceMap.get(listKeys.get(j)));
            }

            System.out.printf("EUR %.2f%n",
                    priceWithoutClientDiscountsMap.get(listKeys.get(j)));
        }

        System.out.println();
        System.out.println("Total Before Client Discounts:    EUR " + totalPriceWithoutClientDiscounts);


        clientDiscountMap.entrySet().stream().forEach(e ->
                System.out.printf("%s:    EUR %.2f%n",
                        e.getKey(), e.getValue()));

        System.out.println("Order Total Amount:     EUR " + resultPriceAfterDiscounts);

    }

    //    this method gives the name of the client by given ID
    public static String giveClientNameById(int clientID) {
        String clientName = "";

        switch (clientID) {
            case 1: {
                clientName = "ABC Distribution";
            }
            case 2: {
                clientName = "DEF Foods";
            }
            case 3: {
                clientName = "GHI Trade";
            }
            case 4: {
                clientName = "JKL Kiosks";
            }
            case 5: {
                clientName = "MNO Vending";
            }
        }

        return clientName;
    }

    //    this method gives the name of the product by the ID
    public static String giveProductNameById(int productID) {
        String productName = "";

        if (productID == 1) {
            productName = "Danish Muffin";
        } else if (productID == 2) {
            productName = "Granny’s Cup Cake";
        } else if (productID == 3) {
            productName = "Frenchy’s Croissant";
        } else if (productID == 4) {
            productName = "Crispy chips";
        }

        return productName;
    }

    public static double giveStandardUnitPriceByProductId(int productId) {
        double targetMarkup = 0;
        double unitCost = 0;

        if (productId == 1) {
            unitCost = 0.52;
            targetMarkup = unitCost * 0.8;
        } else if (productId == 2) {
            unitCost = 0.38;
            targetMarkup = unitCost * 1.2;
        } else if (productId == 3) {
            unitCost = 0.41;
            targetMarkup = 0.90;
        } else if (productId == 4) {
            unitCost = 0.60;
            targetMarkup = 1.00;
        }

        double result = targetMarkup + unitCost;
        BigDecimal resultFormat = new BigDecimal(result).setScale(2, RoundingMode.HALF_UP);

        return resultFormat.doubleValue();
    }

    public static double givePromotionalUnitPrice(int productId, Double unitPrice) {
        double promotionalPricePerOne = 0.0;
        double percentage = 0.00;

        if (productId == 2) {
            promotionalPricePerOne = unitPrice - unitPrice * 0.30;
        } else if (productId == 4) {
            percentage = (unitPrice / (3 * unitPrice)) * 100;
            BigDecimal roundPercentage = new BigDecimal(percentage).setScale(2, RoundingMode.HALF_UP);
            double roundedPercentage = roundPercentage.doubleValue();
            promotionalPricePerOne = unitPrice - (roundedPercentage * unitPrice / 100);
        }

        BigDecimal resultFormat = new BigDecimal(promotionalPricePerOne).setScale(5, RoundingMode.HALF_UP);

        return resultFormat.doubleValue();
    }

    //    this method gives with how much to reduce the total price - Basic Client Discount
    public static double giveBasicClientDiscount(int clientId, double totalPrice) {
        double discount = 0.00;

        if (clientId == 1) {
            discount = 5 * totalPrice / 100;
        } else if (clientId == 2) {
            discount = 4 * totalPrice / 100;
        } else if (clientId == 3) {
            discount = 3 * totalPrice / 100;
        } else if (clientId == 4) {
            discount = 2 * totalPrice / 100;
        } else if (clientId == 5) {
            discount = 0 * totalPrice / 100;
        }

        BigDecimal resultFormat =
                new BigDecimal(discount).
                        setScale(2, RoundingMode.HALF_UP);

        double resultDiscountFormatted = resultFormat.doubleValue();

        return resultDiscountFormatted;
    }

    public static double giveAdditionalVolumeDiscount(int clientId, double totalPrice) {
        double discount = 0.00;

        if (clientId == 1 && totalPrice > 10000 && totalPrice < 30000) {
            discount = 0 * totalPrice / 100;
        } else if (clientId == 2 && totalPrice > 10000 && totalPrice < 30000) {
            discount = 1 * totalPrice / 100;
        } else if (clientId == 3 && totalPrice > 10000 && totalPrice < 30000) {
            discount = 1 * totalPrice / 100;
        } else if (clientId == 4 && totalPrice > 10000 && totalPrice < 30000) {
            discount = 3 * totalPrice / 100;
        } else if (clientId == 5 && totalPrice > 10000 && totalPrice < 30000) {
            discount = 5 * totalPrice / 100;
        } else if (clientId == 1 && totalPrice > 30000) {
            discount = 2 * totalPrice / 100;
        } else if (clientId == 2 && totalPrice > 30000) {
            discount = 2 * totalPrice / 100;
        } else if (clientId == 3 && totalPrice > 30000) {
            discount = 3 * totalPrice / 100;
        } else if (clientId == 4 && totalPrice > 30000) {
            discount = 5 * totalPrice / 100;
        } else if (clientId == 5 && totalPrice > 30000) {
            discount = 7 * totalPrice / 100;
        }

        BigDecimal resultFormat =
                new BigDecimal(discount).
                        setScale(2, RoundingMode.HALF_UP);

        double resultDiscountFormatted = resultFormat.doubleValue();

        return resultDiscountFormatted;
    }
}
package org.tanzu.carorders.model;

import java.util.List;

public record CarOrder(
    String orderId,
    String make,
    String model,
    int year,
    String trim,
    String exteriorColor,
    String interiorColor,
    String transmission,
    String engine,
    List<String> packages,
    List<String> accessories,
    double basePrice,
    double totalPrice,
    String estimatedDelivery,
    String status
) {
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Car Order #").append(orderId).append(" ===\n");
        sb.append("Vehicle    : ").append(year).append(" ").append(make).append(" ").append(model).append(" ").append(trim).append("\n");
        sb.append("Ext. Color : ").append(exteriorColor).append("\n");
        sb.append("Int. Color : ").append(interiorColor).append("\n");
        sb.append("Engine     : ").append(engine).append("\n");
        sb.append("Transmission: ").append(transmission).append("\n");
        sb.append("Packages   : ").append(String.join(", ", packages)).append("\n");
        sb.append("Accessories: ").append(String.join(", ", accessories)).append("\n");
        sb.append("Base Price : $").append(String.format("%,.2f", basePrice)).append("\n");
        sb.append("Total Price: $").append(String.format("%,.2f", totalPrice)).append("\n");
        sb.append("Est. Delivery: ").append(estimatedDelivery).append("\n");
        sb.append("Status     : ").append(status).append("\n");
        return sb.toString();
    }
}

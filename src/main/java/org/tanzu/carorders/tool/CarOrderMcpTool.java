package org.tanzu.carorders.tool;

import org.springaicommunity.mcp.annotation.McpTool;
import org.springframework.stereotype.Component;
import org.tanzu.carorders.model.CarOrder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class CarOrderMcpTool {

    private static final Random RANDOM = new Random();

    private static final Map<String, List<String>> MODELS_BY_MAKE = Map.of(
        "Toyota",   List.of("Camry", "Corolla", "RAV4", "Highlander", "Tacoma", "Supra"),
        "Honda",    List.of("Civic", "Accord", "CR-V", "Pilot", "Ridgeline", "HR-V"),
        "Ford",     List.of("Mustang", "F-150", "Explorer", "Bronco", "Maverick", "Edge"),
        "BMW",      List.of("3 Series", "5 Series", "X3", "X5", "M3", "M5"),
        "Tesla",    List.of("Model S", "Model 3", "Model X", "Model Y", "Cybertruck"),
        "Chevrolet",List.of("Silverado", "Camaro", "Tahoe", "Equinox", "Corvette", "Colorado"),
        "Audi",     List.of("A4", "A6", "Q5", "Q7", "RS6", "e-tron"),
        "Mercedes-Benz", List.of("C-Class", "E-Class", "GLC", "GLE", "AMG GT", "EQS")
    );

    private static final Map<String, List<String>> TRIMS_BY_MAKE = Map.of(
        "Toyota",   List.of("LE", "XLE", "Limited", "TRD Pro", "Platinum"),
        "Honda",    List.of("LX", "EX", "EX-L", "Sport", "Touring"),
        "Ford",     List.of("XL", "XLT", "Lariat", "King Ranch", "Platinum", "Tremor"),
        "BMW",      List.of("sDrive28i", "xDrive40i", "M Sport", "Competition", "Alpina"),
        "Tesla",    List.of("Standard Range", "Long Range", "Performance", "Plaid"),
        "Chevrolet",List.of("LS", "LT", "LTZ", "Z71", "High Country", "ZR2"),
        "Audi",     List.of("Premium", "Premium Plus", "Prestige", "S line", "Black Edition"),
        "Mercedes-Benz", List.of("Base", "AMG Line", "4MATIC", "AMG", "Maybach")
    );

    private static final List<String> EXTERIOR_COLORS = List.of(
        "Pearl White", "Midnight Black", "Deep Blue Metallic", "Racing Red",
        "Lunar Silver", "Glacier White", "Obsidian Black", "Ocean Blue",
        "Sunset Orange", "Forest Green", "Titanium Gray", "Champagne Gold"
    );

    private static final List<String> INTERIOR_COLORS = List.of(
        "Black Leather", "Tan Leather", "Gray Cloth", "Ivory Leather",
        "Red Leather", "Dark Brown Leather", "Blue Alcantara", "White Nappa Leather"
    );

    private static final List<String> ENGINES = List.of(
        "2.0L 4-Cylinder Turbo (255 hp)", "3.5L V6 (300 hp)", "5.0L V8 (450 hp)",
        "Electric Single Motor (283 hp)", "Electric Dual Motor (449 hp)",
        "2.5L Hybrid 4-Cylinder (208 hp)", "3.0L Inline-6 Turbo (382 hp)",
        "6.2L V8 Supercharged (650 hp)", "1.5L 3-Cylinder Turbo (181 hp)"
    );

    private static final List<String> TRANSMISSIONS = List.of(
        "8-Speed Automatic", "6-Speed Manual", "10-Speed Automatic",
        "CVT", "7-Speed Dual-Clutch", "Single-Speed Electric Drive"
    );

    private static final List<String> ALL_PACKAGES = List.of(
        "Premium Sound Package", "Advanced Safety Suite", "Cold Weather Package",
        "Technology Package", "Sport Appearance Package", "Towing Package",
        "Off-Road Package", "Luxury Interior Package", "Driver Assistance Package",
        "Night Vision Package", "Panoramic Sunroof Package", "Performance Upgrade Package"
    );

    private static final List<String> ALL_ACCESSORIES = List.of(
        "All-Weather Floor Mats", "Roof Rack", "Cargo Liner", "Splash Guards",
        "Remote Start", "Trailer Hitch", "Running Boards", "Tinted Windows",
        "Wireless Charging Pad", "Dashcam", "Ambient Lighting Kit", "Bike Rack"
    );

    private static final List<String> STATUSES = List.of(
        "Order Placed", "In Production", "Quality Inspection", "Awaiting Shipment",
        "In Transit", "Ready for Pickup"
    );

    private static final Map<String, Double> BASE_PRICES = Map.of(
        "Toyota", 28000.0, "Honda", 27000.0, "Ford", 35000.0,
        "BMW", 55000.0, "Tesla", 48000.0, "Chevrolet", 32000.0,
        "Audi", 52000.0, "Mercedes-Benz", 60000.0
    );

    @McpTool(description = "Generates a single random custom car order with make, model, trim, colors, engine, packages, accessories, and pricing")
    public String generateRandomCarOrder() {
        CarOrder order = buildRandomOrder();
        return order.toString();
    }

    @McpTool(description = "Generates multiple random custom car orders. Accepts a count parameter (1-20). Returns a formatted list of all orders with a summary.")
    public String generateRandomCarOrders(int count) {
        int safeCount = Math.max(1, Math.min(count, 20));
        List<CarOrder> orders = IntStream.range(0, safeCount)
            .mapToObj(i -> buildRandomOrder())
            .collect(Collectors.toList());

        StringBuilder sb = new StringBuilder();
        sb.append("Generated ").append(safeCount).append(" random car order(s):\n\n");
        orders.forEach(o -> {
            sb.append(o.toString());
            sb.append("\n");
        });

        double total = orders.stream().mapToDouble(CarOrder::totalPrice).sum();
        sb.append("─────────────────────────────────────\n");
        sb.append("Total Orders : ").append(safeCount).append("\n");
        sb.append("Combined Value: $").append(String.format("%,.2f", total)).append("\n");

        return sb.toString();
    }

    @McpTool(description = "Returns a random car order for a specific make (e.g., 'Toyota', 'Ford', 'BMW', 'Tesla', 'Honda', 'Chevrolet', 'Audi', 'Mercedes-Benz')")
    public String generateOrderForMake(String make) {
        if (!MODELS_BY_MAKE.containsKey(make)) {
            return "Unknown make: '" + make + "'. Available makes: " + String.join(", ", getAvailableMakes());
        }
        CarOrder order = buildOrderForMake(make);
        return order.toString();
    }

    @McpTool(description = "Returns the list of all available car makes supported by the order generator")
    public String getAvailableMakes() {
        List<String> makes = new ArrayList<>(MODELS_BY_MAKE.keySet());
        Collections.sort(makes);
        return "Available makes:\n" + makes.stream()
            .map(m -> "  • " + m + " (" + String.join(", ", MODELS_BY_MAKE.get(m)) + ")")
            .collect(Collectors.joining("\n"));
    }

    @McpTool(description = "Generates a random car order summary report with statistics across multiple orders. Accepts a count (1-50) for how many orders to analyze.")
    public String generateOrderReport(int count) {
        int safeCount = Math.max(1, Math.min(count, 50));
        List<CarOrder> orders = IntStream.range(0, safeCount)
            .mapToObj(i -> buildRandomOrder())
            .collect(Collectors.toList());

        Map<String, Long> makeCount = orders.stream()
            .collect(Collectors.groupingBy(CarOrder::make, Collectors.counting()));

        Map<String, Long> statusCount = orders.stream()
            .collect(Collectors.groupingBy(CarOrder::status, Collectors.counting()));

        double totalValue = orders.stream().mapToDouble(CarOrder::totalPrice).sum();
        double avgValue = totalValue / safeCount;
        double minPrice = orders.stream().mapToDouble(CarOrder::totalPrice).min().orElse(0);
        double maxPrice = orders.stream().mapToDouble(CarOrder::totalPrice).max().orElse(0);

        StringBuilder sb = new StringBuilder();
        sb.append("╔══════════════════════════════════════╗\n");
        sb.append("║       CAR ORDERS REPORT              ║\n");
        sb.append("╚══════════════════════════════════════╝\n\n");
        sb.append("Total Orders Analyzed: ").append(safeCount).append("\n\n");

        sb.append("── Orders by Make ──\n");
        makeCount.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .forEach(e -> sb.append("  ").append(e.getKey()).append(": ").append(e.getValue()).append("\n"));

        sb.append("\n── Orders by Status ──\n");
        statusCount.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .forEach(e -> sb.append("  ").append(e.getKey()).append(": ").append(e.getValue()).append("\n"));

        sb.append("\n── Pricing Summary ──\n");
        sb.append("  Total Value : $").append(String.format("%,.2f", totalValue)).append("\n");
        sb.append("  Average     : $").append(String.format("%,.2f", avgValue)).append("\n");
        sb.append("  Lowest      : $").append(String.format("%,.2f", minPrice)).append("\n");
        sb.append("  Highest     : $").append(String.format("%,.2f", maxPrice)).append("\n");

        return sb.toString();
    }

    private CarOrder buildRandomOrder() {
        List<String> makes = new ArrayList<>(MODELS_BY_MAKE.keySet());
        String make = makes.get(RANDOM.nextInt(makes.size()));
        return buildOrderForMake(make);
    }

    private CarOrder buildOrderForMake(String make) {
        List<String> models = MODELS_BY_MAKE.get(make);
        List<String> trims = TRIMS_BY_MAKE.get(make);

        String model = models.get(RANDOM.nextInt(models.size()));
        String trim = trims.get(RANDOM.nextInt(trims.size()));
        String extColor = EXTERIOR_COLORS.get(RANDOM.nextInt(EXTERIOR_COLORS.size()));
        String intColor = INTERIOR_COLORS.get(RANDOM.nextInt(INTERIOR_COLORS.size()));
        String engine = ENGINES.get(RANDOM.nextInt(ENGINES.size()));
        String transmission = TRANSMISSIONS.get(RANDOM.nextInt(TRANSMISSIONS.size()));
        String status = STATUSES.get(RANDOM.nextInt(STATUSES.size()));

        List<String> packages = pickRandom(ALL_PACKAGES, 1 + RANDOM.nextInt(4));
        List<String> accessories = pickRandom(ALL_ACCESSORIES, RANDOM.nextInt(5));

        int year = 2025 + RANDOM.nextInt(2);
        double basePrice = BASE_PRICES.get(make) + (RANDOM.nextDouble() * 15000);
        double packageCost = packages.size() * (800 + RANDOM.nextDouble() * 2200);
        double accessoryCost = accessories.size() * (150 + RANDOM.nextDouble() * 500);
        double totalPrice = basePrice + packageCost + accessoryCost;

        LocalDate delivery = LocalDate.now().plusWeeks(6 + RANDOM.nextInt(20));
        String estimatedDelivery = delivery.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"));

        String orderId = "ORD-" + String.format("%06d", RANDOM.nextInt(1_000_000));

        return new CarOrder(
            orderId, make, model, year, trim,
            extColor, intColor, transmission, engine,
            packages, accessories,
            Math.round(basePrice * 100.0) / 100.0,
            Math.round(totalPrice * 100.0) / 100.0,
            estimatedDelivery, status
        );
    }

    private <T> List<T> pickRandom(List<T> source, int count) {
        if (count <= 0) return Collections.emptyList();
        List<T> shuffled = new ArrayList<>(source);
        Collections.shuffle(shuffled, RANDOM);
        return shuffled.subList(0, Math.min(count, shuffled.size()));
    }
}

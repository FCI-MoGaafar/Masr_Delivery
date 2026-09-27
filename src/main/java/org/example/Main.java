package org.example;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;

public class Main {
    private static final Scanner sc = new Scanner(System.in);
    private static final Platform platform = new Platform();
    public static void main(String[] args) {
        seedData();
        while (true) {
            System.out.println("\n============================================");
            System.out.println("          MASR DELIVERY");
            System.out.println("============================================");
            System.out.println("1. Customer");
            System.out.println("2. Restaurant");
            System.out.println("3. Rider");
            System.out.println("4. Admin & Reports");
            System.out.println("0. Exit");
            int choice = readInt("Choose: ");
            try {
                switch (choice) {
                    case 1 -> customerMenu();
                    case 2 -> restaurantMenu();
                    case 3 -> riderMenu();
                    case 4 -> adminMenu();
                    case 0 -> { System.out.println("Goodbye."); return; }
                    default -> System.out.println("Invalid option.");
                }
            } catch (PlatformException | IllegalArgumentException e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        }
    }

    private static void customerMenu() {
        int id = readInt("Customer ID (1): ");
        Customer c = platform.getCustomer(id);
        if (c == null) { System.out.println("Customer not found."); return; }

        while (true) {
            System.out.println("\n--- Customer ---");
            System.out.println("1 Browse restaurants");
            System.out.println("2 Search restaurants");
            System.out.println("3 View menu");
            System.out.println("4 Add address");
            System.out.println("5 Pay wallet");
            System.out.println("6 Place order");
            System.out.println("7 Track order");
            System.out.println("8 Cancel order");
            System.out.println("9 Order history");
            System.out.println("10 Pay order");
            System.out.println("0 Back");
            int x = readInt("Choose: ");
            try {
                switch (x) {
                    case 1 -> browseRestaurants(c);
                    case 2 -> searchRestaurants(c);
                    case 3 -> viewMenu();
                    case 4 -> addAddress(c);
                    case 5 -> c.addWalletBalance(readDouble("Amount: "));
                    case 6 -> placeOrder(c);
                    case 10 -> platform.payOrder(readInt("Order ID: "));
                    case 7 -> trackOrder();
                    case 8 -> platform.cancelOrder(readInt("Order ID: "));
                    case 9 -> showHistory(c);
                    case 0 -> { return; }
                    default -> System.out.println("Invalid option.");
                }
            } catch (Exception e) { System.out.println("ERROR: " + e.getMessage()); }
        }
    }

    private static void browseRestaurants(Customer c) {
        String district = readLine("District filter (blank = any): ");
        double minRating = readDouble("Minimum rating (0 = any): ");
        Predicate<Restaurant> p = r -> r.isOpen()
                && (district.isBlank() || r.getLocation().equalsIgnoreCase(district))
                && r.getRating() >= minRating;
        c.addSearch("district=" + district + ",rating>=" + minRating);
        platform.searchRestaurants(p).forEach(System.out::println);
    }

    private static void searchRestaurants(Customer c) {
        String q = readLine("Search text: ");
        c.addSearch(q);
        platform.searchRestaurants(r -> r.isOpen() &&
                        (r.getName().toLowerCase().contains(q.toLowerCase()) ||
                                r.getCategories().stream().anyMatch(x -> x.toLowerCase().contains(q.toLowerCase()))))
                .forEach(System.out::println);
    }

    private static void viewMenu() {
        Restaurant r = platform.getRestaurant(readInt("Restaurant ID: "));
        if (r == null) { System.out.println("Not found."); return; }
        r.getMenu().getItems().forEach(System.out::println);
    }

    private static void addAddress(Customer c) {
        Address a = new Address(readLine("District: "), readLine("Details: "));
        c.addAddress(a);
        System.out.println("Address added.");
    }

    private static void placeOrder(Customer c) {
        Restaurant r = platform.getRestaurant(readInt("Restaurant ID: "));
        if (r == null) throw new IllegalArgumentException("Restaurant not found");
        viewMenu();
        List<OrderLine> lines = new ArrayList<>();
        while (true) {
            int itemId = readInt("Item ID (0 finish): ");
            if (itemId == 0) break;
            MenuItem item = r.getMenu().getItem(itemId);
            if (item == null) throw new IllegalArgumentException("Item not found");
            double qty = readDouble("Quantity: ");
            lines.add(new OrderLine(item, qty));
        }
        if (lines.isEmpty()) throw new IllegalArgumentException("At least one item required");
        System.out.println("Your addresses:");
        int n = 1;
        List<Address> addresses = new ArrayList<>(c.getAddresses());
        for (Address a : addresses) System.out.println(n++ + ". " + a);
        Address address = addresses.get(readInt("Address number: ") - 1);
        String promoCode = readLine("Promotion code (blank = none): ");
        Promotion promo = promoCode.isBlank() ? null : platformPromotion(promoCode);
        Order o = platform.placeOrder(c, r, address, lines, promo, readLine("Delivery notes (blank = none): "));
        System.out.println("\nOrder created: " + o);
        System.out.printf("Subtotal: %.2f%nDelivery: %.2f%nService: %.2f%nPromotion: %.2f%nTOTAL: %.2f%n",
                o.getSubtotal(), o.getDeliveryFee(), o.getServiceFee(), o.getPromotionDiscount(), o.getTotal());
    }

    private static Promotion platformPromotion(String code) {
        try {
            java.lang.reflect.Field f = Platform.class.getDeclaredField("promotions");
            f.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, Promotion> m = (Map<String, Promotion>) f.get(platform);
            Promotion p = m.get(code.toLowerCase());
            if (p == null) throw new PromotionException("Promotion not found");
            return p;
        } catch (ReflectiveOperationException e) {
            throw new PromotionException("Promotion lookup failed");
        }
    }


    private static void trackOrder() {
        Order o = platform.getOrder(readInt("Order ID: "));
        if (o == null) throw new IllegalArgumentException("Order not found");
        System.out.println(o);
        System.out.println("Elapsed: " + java.time.Duration.between(o.getPlacedAt(), LocalDateTime.now()).toMinutes() + " minutes");
    }

    private static void showHistory(Customer c) {
        platform.customerHistory(c).forEach(System.out::println);
        System.out.printf("Lifetime spent: %.2f EGP%n", platform.customerSpent(c));
    }

    private static void restaurantMenu() {
        Restaurant r = platform.getRestaurant(readInt("Restaurant ID: "));
        if (r == null) { System.out.println("Restaurant not found."); return; }
        while (true) {
            System.out.println("\n--- Restaurant ---");
            System.out.println("1 Accept order");
            System.out.println("2 Preparing");
            System.out.println("3 Ready");
            System.out.println("4 Toggle item availability");
            System.out.println("5 Add standard item");
            System.out.println("6 Remove item");
            System.out.println("7 Adjust stock");
            System.out.println("8 Open/Close");
            System.out.println("0 Back");
            int x = readInt("Choose: ");
            try {
                switch (x) {
                    case 1 -> platform.acceptOrder(readInt("Order ID: "));
                    case 2 -> platform.prepareOrder(readInt("Order ID: "));
                    case 3 -> platform.readyOrder(readInt("Order ID: "));
                    case 4 -> r.getMenu().changeAvailability(readInt("Item ID: "), readInt("1 available / 0 unavailable: ") == 1);
                    case 5 -> {
                        int id = readInt("Item ID: ");
                        r.getMenu().addItem(new StandardItem(id, readLine("Name: "), readLine("Category: "),
                                readInt("Prep minutes: "), true, readDouble("Price: ")));
                    }
                    case 6 -> r.getMenu().removeItem(readInt("Item ID: "));
                    case 7 -> r.getMenu().getItem(readInt("Item ID: ")).setDailyStock(readDouble("Stock: "));
                    case 8 -> { if (r.isOpen()) r.setClose(false); else r.setOpen(true); }
                    case 0 -> { return; }
                    default -> System.out.println("Invalid.");
                }
            } catch (Exception e) { System.out.println("ERROR: " + e.getMessage()); }
        }
    }

    private static void riderMenu() {
        Rider r = platform.getRider(readInt("Rider ID: "));
        if (r == null) { System.out.println("Rider not found."); return; }
        while (true) {
            System.out.println("\n--- Rider ---");
            System.out.println("1 On duty");
            System.out.println("2 Off duty");
            System.out.println("3 Current order");
            System.out.println("4 Assign order");
            System.out.println("5 Picked up");
            System.out.println("6 Delivered");
            System.out.println("7 Stats");
            System.out.println("0 Back");
            int x = readInt("Choose: ");
            try {
                switch (x) {
                    case 1 -> r.goOnDuty();
                    case 2 -> r.goOffDuty();
                    case 3 -> System.out.println(r.getActiveOrder());
                    case 4 -> platform.assignRider(readInt("Order ID: "), r.getId());
                    case 5 -> platform.pickedUp(readInt("Order ID: "));
                    case 6 -> platform.delivered(readInt("Order ID: "));
                    case 7 -> System.out.println(r);
                    case 0 -> { return; }
                    default -> System.out.println("Invalid.");
                }
            } catch (Exception e) { System.out.println("ERROR: " + e.getMessage()); }
        }
    }

    private static void adminMenu() {
        while (true) {
            System.out.println("\n--- Admin & Reports ---");
            System.out.println("1 All restaurants");
            System.out.println("2 All cuisines");
            System.out.println("3 Revenue");
            System.out.println("4 Orders by status");
            System.out.println("5 Average order value by district");
            System.out.println("6 Most ordered item");
            System.out.println("7 Peak ordering hour");
            System.out.println("8 Inactive customers");
            System.out.println("9 Add promotion");
            System.out.println("0 Back");
            int x = readInt("Choose: ");
            try {
                switch (x) {
                    case 1 -> platform.searchRestaurants(r -> true).forEach(System.out::println);
                    case 2 -> System.out.println(platform.allCuisines());
                    case 3 -> System.out.printf("Revenue: %.2f%n", platform.revenue(LocalDateTime.now().minusDays(30), LocalDateTime.now()));
                    case 4 -> System.out.println(platform.ordersByStatus());
                    case 5 -> System.out.println(platform.averageOrderValueByDistrict());
                    case 6 -> System.out.println(platform.mostOrderedItem().map(MenuItem::getName).orElse("No orders exist"));
                    case 7 -> System.out.println(platform.peakOrderingHour().isPresent() ? platform.peakOrderingHour().getAsInt() + ":00" : "No orders");
                    case 8 -> platform.inactiveCustomers().forEach(System.out::println);
                    case 9 -> addDemoPromotion();
                    case 0 -> { return; }
                    default -> System.out.println("Invalid.");
                }
            } catch (Exception e) { System.out.println("ERROR: " + e.getMessage()); }
        }
    }

    private static void addDemoPromotion() {
        String code = readLine("Code: ");
        double percent = readDouble("Percentage: ");
        double cap = readDouble("Max discount: ");
        Promotion promo = new Promotion(code, new PercentagePromotion(percent, cap), LocalDateTime.now().plusDays(30));
        platform.addPromotion(promo);
        System.out.println("Promotion added.");
    }

    private static void seedData() {
        Restaurant r = new Restaurant(1, "Masr Kitchen", "Maadi", 4.7, new Menu(), new HashSet<>(Set.of("Egyptian", "Grill")));
        r.setOpen(true);
        StandardItem burger = new StandardItem(101, "Burger", "Meals", 15, true, 100);
        burger.setDailyStock(100);
        StandardItem fries = new StandardItem(102, "Fries", "Sides", 10, true, 40);
        fries.setDailyStock(100);
        r.getMenu().addItem(burger);
        r.getMenu().addItem(fries);
        platform.addRestaurant(r);

        Customer c = new Customer(1, "Mohamed", "01012345678", 1000);
        c.addAddress(new Address("Maadi", "Street 10"));
        c.addAddress(new Address("Faisal", "Street 20"));
        platform.addCustomer(c);

        Rider rider = new Rider(1, "Ahmed Rider", VehicleType.MOTORCYCLE, "Maadi");
        rider.goOnDuty();
        platform.addRider(rider);

        platform.addPromotion(new Promotion("NILE20", new PercentagePromotion(20, 50), LocalDateTime.now().plusDays(30)));
    }

    private static String readLine(String msg) {
        System.out.print(msg);
        return sc.nextLine().trim();
    }

    private static int readInt(String msg) {
        while (true) {
            try { return Integer.parseInt(readLine(msg)); }
            catch (NumberFormatException e) { System.out.println("Enter a valid integer."); }
        }
    }

    private static double readDouble(String msg) {
        while (true) {
            try { return Double.parseDouble(readLine(msg)); }
            catch (NumberFormatException e) { System.out.println("Enter a valid number."); }
        }
    }
}
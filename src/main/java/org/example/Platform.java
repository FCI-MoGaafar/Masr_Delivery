package org.example;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Platform {
    private final Map<Integer, Restaurant> restaurants = new HashMap<>();
    private final Map<Integer, Customer> customers = new HashMap<>();
    private final Map<Integer, Rider> riders = new HashMap<>();
    private final Map<Integer, Order> orders = new HashMap<>();
    private final Map<String, Promotion> promotions = new HashMap<>();
    private int nextOrderId = 1000;

    public void addRestaurant(Restaurant r) { if (restaurants.putIfAbsent(r.getId(), r) != null) throw new IllegalArgumentException("Restaurant exists"); }
    public void removeRestaurant(int id) { restaurants.remove(id); }
    public void addCustomer(Customer c) { if (customers.putIfAbsent(c.getId(), c) != null) throw new IllegalArgumentException("Customer exists"); }
    public void addRider(Rider r) { if (riders.putIfAbsent(r.getId(), r) != null) throw new IllegalArgumentException("Rider exists"); }
    public void addPromotion(Promotion p) { promotions.put(p.getCode().toLowerCase(), p); }

    public Restaurant getRestaurant(int id) { return restaurants.get(id); }
    public Customer getCustomer(int id) { return customers.get(id); }
    public Rider getRider(int id) { return riders.get(id); }
    public Order getOrder(int id) { return orders.get(id); }

    public Collection<Restaurant> getRestaurants() { return Collections.unmodifiableCollection(restaurants.values()); }
    public Collection<Customer> getCustomers() { return Collections.unmodifiableCollection(customers.values()); }
    public Collection<Rider> getRiders() { return Collections.unmodifiableCollection(riders.values()); }
    public Collection<Order> getOrders() { return Collections.unmodifiableCollection(orders.values()); }

    public List<Restaurant> searchRestaurants(Predicate<Restaurant> condition) {
        return restaurants.values().stream().filter(condition)
                .sorted(Comparator.comparingDouble(Restaurant::getRating).reversed()
                        .thenComparing(Restaurant::getName))
                .toList();
    }

    public Set<String> allCuisines() {
        return restaurants.values().stream()
                .flatMap(r -> r.getCategories().stream())
                .collect(Collectors.toCollection(TreeSet::new));
    }

    public double distanceKm(String from, String to) {
        if (from.equalsIgnoreCase(to)) return 0;
        return 5;
    }

    public Order placeOrder(Customer customer, Restaurant restaurant, Address address, List<OrderLine> lines, Promotion promotion, String notes) {
        if (!restaurant.isOpen()) throw new RestaurantClosedException("Restaurant is closed");
        if (!customer.hasAddress(address)) throw new IllegalArgumentException("Address does not belong to customer");

        for (OrderLine line : lines) {
            if (!line.getItem().isAvailable()) throw new ItemUnavailableException(line.getItem().getName() + " is unavailable");
            if (!Double.isInfinite(line.getItem().getDailyStock()) &&
                    line.getQuantity() > line.getItem().getDailyStock())
                throw new StockShortageException("Insufficient stock for " + line.getItem().getName());
        }

        Promotion p = promotion;
        if (p != null && !p.isApplicable(customer, restaurant, address, lines.stream().mapToDouble(OrderLine::calculatePrice).sum()))
            throw new PromotionException("Promotion is expired or not applicable");

        // استخدام OderBuilder بالطريقة الصحيحة المتوافقة مع الكلاس الخاص بك
        OderBuilder builder = new OderBuilder()
                .setId(nextOrderId++)
                .setCustomer(customer)
                .setRestaurant(restaurant)
                .setDeliveryAddress(address.getDistrict() + " - " + address.getDetails());

        lines.forEach(builder::addOrderLine);

        Order order = builder.build();

        // تعيين الـ Promotion مباشرة بعد البناء إذا كان موجوداً
        if (p != null) {
            order.setPromotion(p);
        }

        double subtotal = lines.stream().mapToDouble(OrderLine::calculatePrice).sum();
        double distance = distanceKm(restaurant.getLocation(), address.getDistrict());
        PlatformConfig cfg = PlatformConfig.getInstance();
        double delivery = cfg.getBaseDeliveryFee() + Math.max(0, distance - cfg.getFreeKm()) * cfg.getExtraKmFee();
        if (customer.getLoyaltyTier() == LoyaltyTier.Silver) delivery *= 0.90;
        if (customer.getLoyaltyTier() == LoyaltyTier.Gold) delivery = 0;
        double service = Math.round(subtotal * cfg.getServiceRate() * 100.0) / 100.0;
        double promoDiscount = p == null ? 0 : p.discount(subtotal, delivery);

        order.setPricing(subtotal, delivery, service, promoDiscount);
        lines.forEach(line -> line.getItem().consumeStock(line.getQuantity()));
        orders.put(order.getId(), order);
        return order;
    }

    public void payOrder(int orderId) {
        Order o = requireOrder(orderId);
        if (o.isPaid()) throw new IllegalArgumentException("Order already paid");
        o.getCustomer().deductWalletBalance(o.getTotal());
        o.markPaid();
    }

    public void cancelOrder(int orderId) {
        Order o = requireOrder(orderId);
        if (o.getStatus() == OrderStatus.OUT_FOR_DELIVERY || o.getStatus() == OrderStatus.DELIVERED
                || o.getStatus() == OrderStatus.CANCELLED)
            throw new IllegalOrderTransitionException("Order cannot be cancelled now");
        o.changeStatus(OrderStatus.CANCELLED);
        if (o.isPaid()) o.getCustomer().addWalletBalance(o.getTotal());
    }

    public void acceptOrder(int id) {
        requireOrder(id).changeStatus(OrderStatus.ACCEPTED);
    }
    public void prepareOrder(int id) {
        requireOrder(id).changeStatus(OrderStatus.PREPARING);
    }
    public void readyOrder(int id) {
        requireOrder(id).changeStatus(OrderStatus.READY);
    }

    public void assignRider(int orderId, int riderId) {
        Order o = requireOrder(orderId);
        Rider r = riders.get(riderId);
        if (r == null) throw new IllegalArgumentException("Rider not found");
        o.assignRider(r);
    }

    public void pickedUp(int orderId) { requireOrder(orderId).changeStatus(OrderStatus.OUT_FOR_DELIVERY); }

    public void delivered(int orderId) {
        Order o = requireOrder(orderId);
        o.changeStatus(OrderStatus.DELIVERED);
        if (o.getRider() != null) o.getRider().completeDelivery();
    }

    private Order requireOrder(int id) {
        Order o = orders.get(id);
        if (o == null) throw new IllegalArgumentException("Order not found");
        return o;
    }

    public List<Order> customerHistory(Customer c) {
        return orders.values().stream().filter(o -> o.getCustomer().equals(c))
                .sorted(Comparator.comparing(Order::getPlacedAt).reversed()).toList();
    }

    public double customerSpent(Customer c) {
        return customerHistory(c).stream().filter(Order::isPaid).mapToDouble(Order::getTotal).sum();
    }

    public Map<OrderStatus, Long> ordersByStatus() {
        return orders.values().stream().collect(Collectors.groupingBy(Order::getStatus, Collectors.counting()));
    }

    public Map<String, Double> averageOrderValueByDistrict() {
        return orders.values().stream().filter(Order::isPaid)
                .collect(Collectors.groupingBy(Order::getDeliveryAddress,
                        Collectors.averagingDouble(Order::getTotal)));
    }

    public Optional<MenuItem> mostOrderedItem() {
        return orders.values().stream().flatMap(o -> o.getOrderLines().stream())
                .collect(Collectors.groupingBy(OrderLine::getItem, Collectors.summingDouble(OrderLine::getQuantity)))
                .entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey);
    }

    public OptionalInt peakOrderingHour() {
        return orders.values().stream()
                .collect(Collectors.groupingBy(o -> o.getPlacedAt().getHour(), Collectors.counting()))
                .entrySet().stream().max(Map.Entry.comparingByValue())
                .map(e -> OptionalInt.of(e.getKey())).orElseGet(OptionalInt::empty);
    }

    public List<Customer> inactiveCustomers() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(30);
        return customers.values().stream().filter(c ->
                        customerHistory(c).stream().noneMatch(o -> o.getPlacedAt().isAfter(cutoff)))
                .toList();
    }

    public double revenue(LocalDateTime from, LocalDateTime to) {
        return orders.values().stream()
                .filter(o -> o.isPaid() && !o.getPlacedAt().isBefore(from) && !o.getPlacedAt().isAfter(to))
                .mapToDouble(Order::getTotal).sum();
    }

    public List<Restaurant> topFiveRestaurants(YearMonth month) {
        return restaurants.values().stream()
                .sorted(Comparator.comparingDouble(r -> -orders.values().stream()
                        .filter(o -> o.getRestaurant().equals(r) && o.isPaid()
                                && YearMonth.from(o.getPlacedAt()).equals(month))
                        .mapToDouble(Order::getTotal).sum()))
                .limit(5).toList();
    }

    public Map<Rider, Double> riderAverageDeliveryMinutes() {
        return riders.values().stream().collect(Collectors.toMap(
                r -> r,
                r -> orders.values().stream().filter(o -> o.getRider() != null && o.getRider().equals(r)
                                && o.getStatus() == OrderStatus.DELIVERED)
                        .mapToLong(Order::deliveryDurationMinutes).average().orElse(0)));
    }

    public List<Customer> searchCustomersNotOrdered30Days() {
        return inactiveCustomers();
    }

    public List<Order> getReadyOrdersForDispatch() {
        return orders.values().stream()
                .filter(o -> o.getStatus() == OrderStatus.READY && o.getRider() == null)
                .sorted((o1, o2) -> {
                    boolean isGold1 = o1.getCustomer().getLoyaltyTier() == LoyaltyTier.Gold;
                    boolean isGold2 = o2.getCustomer().getLoyaltyTier() == LoyaltyTier.Gold;

                    if (isGold1 && !isGold2) return -1;
                    if (!isGold1 && isGold2) return 1;

                    return o1.getPlacedAt().compareTo(o2.getPlacedAt());
                })
                .toList();
    }

    public List<Restaurant> getTopTierRestaurantsReport() {
        return restaurants.values().stream()
                .filter(r -> r.getRating() > 4.5)
                .filter(r -> {
                    long completedCount = orders.values().stream()
                            .filter(o -> o.getRestaurant().equals(r) && o.getStatus() == OrderStatus.DELIVERED)
                            .count();
                    return completedCount >= 20;
                })
                .toList();
    }
}

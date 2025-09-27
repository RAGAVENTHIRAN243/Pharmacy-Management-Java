package pharmacy;

import java.time.LocalDate;
import java.util.*;

public class PharmacyApp {
    private static List<Medicine> medicines = new ArrayList<>();
    private static List<Customer> customers = new ArrayList<>();
    private static List<Prescription> prescriptions = new ArrayList<>();
    private static Pharmacist pharmacist = new Pharmacist("P1", "Dr. Smith", "LIC123");

    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n--- Pharmacy Management ---");
            System.out.println("1. Add Medicine");
            System.out.println("2. Add Customer");
            System.out.println("3. Add Prescription");
            System.out.println("4. Make Sale");
            System.out.println("5. Display Medicines");
            System.out.println("6. Exit");
            System.out.print("Choose: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
            case 1:
                addMedicine();
                break;
            case 2:
                addCustomer();
                break;
            case 3:
                addPrescription();
                break;
            case 4:
                makeSale();
                break;
            case 5:
                displayMedicines();
                break;
            case 6:
                System.exit(0);
                break;
            default:
                System.out.println("Invalid choice!");
        }

        }
    }

    private static void addMedicine() {
        System.out.print("ID: ");
        String id = sc.nextLine();
        System.out.print("Name: ");
        String name = sc.nextLine();
        System.out.print("Requires prescription (true/false): ");
        boolean rx = sc.nextBoolean();
        System.out.print("Price: ");
        double price = sc.nextDouble();
        System.out.print("Stock: ");
        int stock = sc.nextInt();
        System.out.print("Expiry (YYYY-MM-DD): ");
        String date = sc.next();
        medicines.add(new Medicine(id, name, rx, price, stock, LocalDate.parse(date)));
    }

    private static void addCustomer() {
        System.out.print("ID: ");
        String id = sc.nextLine();
        System.out.print("Name: ");
        String name = sc.nextLine();
        System.out.print("Phone: ");
        String phone = sc.nextLine();
        customers.add(new Customer(id, name, phone));
    }

    private static void addPrescription() {
        System.out.print("Prescription ID: ");
        String id = sc.nextLine();
        System.out.print("Customer ID: ");
        String custId = sc.nextLine();
        Customer c = customers.stream().filter(x -> x.getId().equals(custId)).findFirst().orElse(null);
        if (c == null) {
            System.out.println("Customer not found!");
            return;
        }
        System.out.print("Valid until (YYYY-MM-DD): ");
        LocalDate valid = LocalDate.parse(sc.nextLine());
        Prescription p = new Prescription(id, c, valid);

        System.out.println("Add medicine IDs (type 'done' to finish):");
        while (true) {
            String medId = sc.nextLine();
            if (medId.equals("done")) break;
            Medicine m = medicines.stream().filter(x -> x.getId().equals(medId)).findFirst().orElse(null);
            if (m == null) {
                System.out.println("Medicine not found!");
                continue;
            }
            System.out.print("Quantity: ");
            int qty = sc.nextInt(); sc.nextLine();
            p.addItem(new PrescriptionItem(m, qty));
        }
        prescriptions.add(p);
    }

    private static void makeSale() {
        System.out.print("Sale ID: ");
        String id = sc.nextLine();
        Sale sale = new Sale(id, pharmacist);

        while (true) {
            System.out.print("Enter medicine ID (or 'done'): ");
            String medId = sc.nextLine();
            if (medId.equals("done")) break;

            Medicine m = medicines.stream().filter(x -> x.getId().equals(medId)).findFirst().orElse(null);
            if (m == null) {
                System.out.println("Medicine not found!");
                continue;
            }
            if (m.isExpired()) {
                System.out.println("⚠ Medicine expired! Cannot sell.");
                continue;
            }
            System.out.print("Quantity: ");
            int qty = sc.nextInt(); sc.nextLine();

            if (qty > m.getStock()) {
                System.out.println("Not enough stock!");
                continue;
            }
            if (m.isRequiresPrescription()) {
                System.out.print("Enter prescription ID: ");
                String pid = sc.nextLine();
                Prescription p = prescriptions.stream().filter(x -> x.toString().contains(pid)).findFirst().orElse(null);
                if (p == null || !p.isValid()) {
                    System.out.println("Invalid prescription!");
                    continue;
                }
            }
            m.reduceStock(qty);
            sale.addDetail(new SaleDetail(m, qty));
        }
        System.out.println("\n--- Sale Bill ---");
        System.out.println(sale);
    }

    private static void displayMedicines() {
        System.out.println("\n--- Medicines ---");
        for (Medicine m : medicines) {
            System.out.println(m);
        }
    }
}

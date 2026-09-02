package hospitalsystem;

import java.sql.*;
import java.util.Scanner;

public class HospitalSystem {
    private static final String URL = getEnvironmentVariable(
            "HOSPITAL_DB_URL", "jdbc:mysql://localhost:3306/hospital");
    private static final String USER = getEnvironmentVariable("HOSPITAL_DB_USER", "root");
    private static final String PASSWORD = System.getenv("HOSPITAL_DB_PASSWORD");

    public static void main(String[] args) {
        if (PASSWORD == null || PASSWORD.isBlank()) {
            System.err.println("HOSPITAL_DB_PASSWORD is not set. See README.md for setup instructions.");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        int choice;

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            System.out.println("Connected to Hospital Database successfully!");

            do {
                System.out.println("Patient Database Menu");
                System.out.println("1. Add Patient Record");
                System.out.println("2. View All Patients");
                System.out.println("3. Delete Patient");
                System.out.println("4. Exit");
                System.out.print("Enter your choice: ");
                choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1 -> addPatient(conn, scanner);
                    case 2 -> viewPatients(conn);
                    case 3 -> deletePatient(conn, scanner);
                    case 4 -> System.out.println("Exiting program...");
                    default -> System.out.println("Invalid choice! Please try again.");
                }
            } while (choice != 4);

        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
        }
    }

    private static void addPatient(Connection conn, Scanner scanner) {
        try {
            System.out.print("Enter full name: ");
            String name = scanner.nextLine();
            System.out.print("Enter next of kin: ");
            String kin = scanner.nextLine();
            System.out.print("Enter address: ");
            String address = scanner.nextLine();

            String sql = "INSERT INTO patients (full_name, next_of_kin, address) VALUES (?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, name);
                ps.setString(2, kin);
                ps.setString(3, address);
                ps.executeUpdate();
            }

            System.out.println("Patient record added successfully!");
        } catch (SQLException e) {
            System.out.println("Error adding patient: " + e.getMessage());
        }
    }

    private static void viewPatients(Connection conn) {
        try {
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM patients")) {
                System.out.println("Patient Records");
                while (rs.next()) {
                    System.out.printf("ID: %d | Name: %s | Next of Kin: %s | Address: %s%n",
                            rs.getInt("patient_id"),
                            rs.getString("full_name"),
                            rs.getString("next_of_kin"),
                            rs.getString("address"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error viewing patients: " + e.getMessage());
        }
    }

    private static void deletePatient(Connection conn, Scanner scanner) {
        try {
            System.out.print("Enter patient ID to delete: ");
            int id = scanner.nextInt();

            String sql = "DELETE FROM patients WHERE patient_id = ?";
            int rows;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                rows = ps.executeUpdate();
            }

            if (rows > 0) {
                System.out.println("Patient deleted successfully!");
            } else {
                System.out.println("Patient ID not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error deleting patient: " + e.getMessage());
        }
    }

    private static String getEnvironmentVariable(String name, String defaultValue) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? defaultValue : value;
    }
}



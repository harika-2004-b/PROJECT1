package system;
import java.util.*;

// --- Encapsulation ---
class Book {
    private String title;
    private boolean isIssued;

    public Book(String title) {
        this.title = title;
        this.isIssued = false;
    }

    public String getTitle() {
        return title;
    }

    public boolean isIssued() {
        return isIssued;
    }

    public void setIssued(boolean issued) {
        this.isIssued = issued;
    }
}

// --- Abstraction ---
abstract class User {
    protected String username;

    public User(String username) {
        this.username = username;
    }

    public abstract void accessLibrary(List<Book> books);
}

// --- Inheritance + Polymorphism: Student ---
class Student extends User {
    public Student(String username) {
        super(username);
    }

    @Override
    public void accessLibrary(List<Book> books) {
        System.out.println("\n[Student Panel]");
        viewBooks(books);
        issueBook(books);
    }

    private void viewBooks(List<Book> books) {
        System.out.println("Available books:");
        for (Book book : books) {
            System.out.println("- " + book.getTitle() + (book.isIssued() ? " (Issued)" : ""));
        }
    }

    private void issueBook(List<Book> books) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter book name to issue: ");
        String title = scanner.nextLine();

        for (Book book : books) {
            if (book.getTitle().equalsIgnoreCase(title) && !book.isIssued()) {
                book.setIssued(true);
                System.out.println("Book issued: " + book.getTitle());
                return;
            }
        }
        System.out.println("Book not available or already issued.");
    }
}

// --- User Info Class ---
class UserInfo {
    private String username;
    private String contactInfo;

    public UserInfo(String username, String contactInfo) {
        this.username = username;
        this.contactInfo = contactInfo;
    }

    public String getUsername() {
        return username;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    @Override
    public String toString() {
        return "Username: " + username + ", Contact Info: " + contactInfo;
    }
}

// --- Transaction Class ---
class Transaction {
    private String username;
    private String bookTitle;
    private Date issueDate;
    private Date returnDate;

    public Transaction(String username, String bookTitle, Date issueDate) {
        this.username = username;
        this.bookTitle = bookTitle;
        this.issueDate = issueDate;
        this.returnDate = null;
    }

    public String getUsername() {
        return username;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public long calculateFine() {
        if (returnDate == null) {
            return 0;
        }
        long diff = returnDate.getTime() - issueDate.getTime();
        long days = diff / (1000 * 60 * 60 * 24);
        long allowedDays = 14;
        long finePerDay = 5;
        if (days > allowedDays) {
            return (days - allowedDays) * finePerDay;
        }
        return 0;
    }

    @Override
    public String toString() {
        return "User: " + username +
               ", Book: " + bookTitle +
               ", Issued: " + issueDate +
               ", Returned: " + (returnDate == null ? "Not returned" : returnDate) +
               ", Fine: " + calculateFine();
    }
}

// --- Inheritance + Polymorphism: Admin ---
class Admin extends User {
    private List<UserInfo> users = new ArrayList<>();
    private List<Transaction> transactions = new ArrayList<>();

    public Admin(String username) {
        super(username);
    }

    @Override
    public void accessLibrary(List<Book> books) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n[Admin Panel]");
            System.out.println("1. Add Book  2. Remove Book  3. View Books");
            System.out.println("4. Return Book  5. View/Edit User Info  6. View Issue/Return History  7. Exit");
            System.out.print("Enter choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter new book title: ");
                    String titleToAdd = scanner.nextLine();
                    books.add(new Book(titleToAdd));
                    System.out.println("Book added.");
                    break;

                case 2:
                    System.out.print("Enter book title to remove: ");
                    String titleToRemove = scanner.nextLine();
                    books.removeIf(book -> book.getTitle().equalsIgnoreCase(titleToRemove));
                    System.out.println("Book removed.");
                    break;

                case 3:
                    for (Book book : books) {
                        System.out.println("- " + book.getTitle() + (book.isIssued() ? " (Issued)" : ""));
                    }
                    break;

                case 4:
                    System.out.print("Enter book to return: ");
                    String returnTitle = scanner.nextLine();
                    boolean found = false;
                    for (Book book : books) {
                        if (book.getTitle().equalsIgnoreCase(returnTitle) && book.isIssued()) {
                            book.setIssued(false);
                            System.out.println("Book returned.");

                            for (Transaction t : transactions) {
                                if (t.getBookTitle().equalsIgnoreCase(returnTitle) && t.getReturnDate() == null) {
                                    t.setReturnDate(new Date());
                                    long fine = t.calculateFine();
                                    if (fine > 0) {
                                        System.out.println("Late return fine: " + fine);
                                    }
                                    break;
                                }
                            }
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        System.out.println("Book not found or not issued.");
                    }
                    break;

                case 5:
                    viewEditUserInfo(scanner);
                    break;

                case 6:
                    viewIssueReturnHistory();
                    break;

                case 7:
                    System.out.println("Exiting Admin Panel.");
                    return;

                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void viewEditUserInfo(Scanner scanner) {
        System.out.println("\nUser List:");
        for (int i = 0; i < users.size(); i++) {
            System.out.println((i + 1) + ". " + users.get(i));
        }

        System.out.print("Enter user number to edit or 0 to add new user: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        if (choice == 0) {
            System.out.print("Enter new username: ");
            String username = scanner.nextLine();
            System.out.print("Enter contact info: ");
            String contact = scanner.nextLine();
            users.add(new UserInfo(username, contact));
            System.out.println("User added.");
        } else if (choice > 0 && choice <= users.size()) {
            UserInfo user = users.get(choice - 1);
            System.out.println("Editing user: " + user.getUsername());
            System.out.print("Enter new contact info: ");
            String contact = scanner.nextLine();
            user.setContactInfo(contact);
            System.out.println("User info updated.");
        } else {
            System.out.println("Invalid choice.");
        }
    }

    private void viewIssueReturnHistory() {
        System.out.println("\nIssue/Return History:");
        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }
        for (Transaction t : transactions) {
            System.out.println(t);
        }
    }

    public void addTransaction(String username, String bookTitle) {
        transactions.add(new Transaction(username, bookTitle, new Date()));
    }
}

// --- Main System (Entry Point) ---
 public class LibraryManagementSystem {
    public static void main(String[]args) {
        Scanner scanner = new Scanner(System.in);
        List<Book> books = new ArrayList<>();
        books.add(new Book("Java Programming"));
        books.add(new Book("OOP Concepts"));
        books.add(new Book("Data Structures"));
        books.add(new Book("Core Java"));

        System.out.println("Welcome to the Library Management System");

        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        System.out.print("Enter role (student/admin): ");
        String role = scanner.nextLine().toLowerCase();

        User user;
        if (role.equals("student")) {
            user = new Student(username);
        } else if (role.equals("admin")) {
            user = new Admin(username);
        } else {
            System.out.println("Invalid role.");
            return;
        }

        user.accessLibrary(books);

        System.out.println("Thank you! Logging out...");
    }
}

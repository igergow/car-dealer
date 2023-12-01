import java.io.*;
import java.net.*;
import java.util.Scanner;

public class CarDealerClient {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private Scanner scanner;

    public CarDealerClient(String ip, int port) {
        try {
            clientSocket = new Socket(ip, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            scanner = new Scanner(System.in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connect() {
        try {
            if (clientSocket.isConnected()) {
                System.out.println("Connected to the car dealer server.");
                interactWithServer();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void interactWithServer() {
        String userInput;
        try {
            while (true) {
                System.out.println("\nOptions:");
                System.out.println("1. Request car price by model");
                System.out.println("2. Send car for evaluation");
                System.out.println("3. Sell your car");
                System.out.println("4. Exit");
                System.out.print("Choose an option: ");

                userInput = scanner.nextLine();
                switch (userInput) {
                    case "1":
                        requestCarPrice();
                        break;
                    case "2":
                        sendCarForEvaluation();
                        break;
                    case "3":
                        sellCar();
                        break;
                    case "4":
                        closeConnection();
                        return;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }

                System.out.println("Server says: " + receiveResponse());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void requestCarPrice() {
        System.out.print("Enter the car model (e.g., Toyota Corolla): ");
        String model = scanner.nextLine();
        sendRequest("PRICE " + model);
    }

    private void sendCarForEvaluation() {
        System.out.print("Enter your car details (brand model price): ");
        String carDetails = scanner.nextLine();
        sendRequest("EVALUATE " + carDetails);
    }

    private void sellCar() {
        System.out.print("Enter your car details to sell (brand model price): ");
        String carDetails = scanner.nextLine();
        sendRequest("SELL " + carDetails);
    }

    public void sendRequest(String request) {
        out.println(request);
    }

    public String receiveResponse() throws IOException {
        return in.readLine();
    }

    public void closeConnection() {
        try {
            out.close();
            in.close();
            scanner.close();
            clientSocket.close();
            System.out.println("Disconnected from the server.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        CarDealerClient client = new CarDealerClient("localhost", 1234);
        client.connect();
    }
}

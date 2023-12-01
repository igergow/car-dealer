import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CarDealerServer {
    private ServerSocket serverSocket;
    private List<Car> carsForSale;
    private ExecutorService pool;

    public CarDealerServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
            carsForSale = Collections.synchronizedList(new ArrayList<>() {
                {
                    add(new Car("BMW", "X5", 50000));
                    add(new Car("BMW", "X6", 60000));
                    add(new Car("BMW", "X7", 70000));
                    add(new Car("Audi", "A6", 40000));
                    add(new Car("Audi", "A7", 50000));
                    add(new Car("Audi", "A8", 60000));
                    add(new Car("Mercedes", "S500", 80000));
                    add(new Car("Mercedes", "S600", 90000));
                    add(new Car("Mercedes", "S700", 100000));
                    add(new Car("Toyota", "Auris", 20000));
                }
            });
            pool = Executors.newFixedThreadPool(3);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                pool.execute(clientHandler);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ClientHandler implements Runnable {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                String request;
                while ((request = in.readLine()) != null) {
                    String[] tokens = request.split(" ", 2);
                    String command = tokens[0];

                    switch (command) {
                        case "PRICE":
                            sendCarPrice(tokens[1]);
                            break;
                        case "EVALUATE":
                            evaluateCar(tokens[1]);
                            break;
                        case "SELL":
                            sellCar(tokens[1]);
                            break;
                        default:
                            out.println("Invalid command");
                            break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                closeConnection();
            }
        }

        private void sendCarPrice(String model) {
            Car car = carsForSale.stream()
                    .filter(c -> c.getModel().equalsIgnoreCase(model))
                    .findFirst()
                    .orElse(null);
            if (car != null) {
                System.out.println("Sending details to the client");
                out.println("Price of " + model + ": " + car.getPrice());
            } else {
                out.println("Car not found");
            }
        }

        private void evaluateCar(String carDetails) {
            System.out.println("Sending evaluation to the client");
            out.println("Car evaluated: " + carDetails);
        }

        private void sellCar(String carDetails) {
            String[] details = carDetails.split(" ");
            System.out.println("Sending sell confirmation to the client");
            Car car = new Car(details[1], details[2], Double.parseDouble(details[3]));
            carsForSale.add(car);
            out.println("Car sold: " + carDetails);
        }

        private void closeConnection() {
            try {
                out.close();
                in.close();
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        CarDealerServer server = new CarDealerServer(1234);
        server.start();
    }
}
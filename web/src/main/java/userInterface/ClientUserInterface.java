package userInterface;

import java.io.File;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Scanner;

import javax.inject.Inject;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;

import session.client.UserInterfaceRemote;
import utils.Reader;
import biz.keyinsights.sda.web.config.AppConfig;

@Service
public class ClientUserInterface {

    @Inject
    private UserInterfaceRemote userInterface;
    
    public static final String CHOICE_ONE = "1";
    public static final String CHOICE_TWO = "2";
    public static final String CHOICE_THREE = "3";
    public static final String CHOICE_FOUR = "4";
    public static final String CHOICE_FIVE = "5";
    public static final String CHOICE_ZERO = "0";
    public static final String CHOICE_Y = "y";
    public static final String CHOICE_N = "n";
    public static final String PORT_PATTERN = "\\d+";
    public static final String IPADDRESS_PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

    public static void main(String[] args) {
    	AnnotationConfigApplicationContext context =
    	        new AnnotationConfigApplicationContext(AppConfig.class);
        ClientUserInterface ui = context.getBean(ClientUserInterface.class);
        Scanner sc = new Scanner(System.in);
        boolean exit = false;
        while (!exit) {
            ui.printInstruction();
            String cmd = sc.nextLine();
            if (cmd.equals(CHOICE_ONE)) {
                ui.doCreateRegression();
                exit = true;
            } else if (cmd.equals(CHOICE_TWO)) {
                ui.doJoinRegression();
                exit = true;
            } else if (cmd.equals(CHOICE_ZERO)) {
                exit = true;
            }
        }
        sc.close();
        context.close();
    }

    public void printInstruction() {
        System.out.println(UserInterfaceOutput.HEADER);
        System.out.print(UserInterfaceOutput.INSTRUCTION);
    }

    public void doCreateRegression() {
        boolean exit = false;
        Scanner sc = new Scanner(System.in);
        doAddCreator(sc);
        while (!exit) {
            System.out.print(UserInterfaceOutput.CREATE_REGRESSION_INSTRUCTION);
            String cmd = sc.nextLine();
            if (cmd.equals(CHOICE_ONE)) {
                doAddUser(sc);
            } else if (cmd.equals(CHOICE_TWO)) {
                doRemoveUser(sc);
            } else if (cmd.equals(CHOICE_THREE)) {
                doListUsers();
            } else if (cmd.equals(CHOICE_FOUR)) {
                doCreatorStartRegression(sc);
            } else if (cmd.equals(CHOICE_ZERO)) {
                exit = true;
            }
        }
        sc.close();
    }

    public void doAddCreator(Scanner sc) {
        while (true) {
            System.out.print(UserInterfaceOutput.ENTER_YOUR_PORT);
            String cmd = sc.nextLine();
            if (cmd.matches(PORT_PATTERN)) {
                int port = Integer.valueOf(cmd);
                String ip = "";
                try {
                    ip = Inet4Address.getLocalHost().getHostAddress();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                userInterface.addCreator(ip, port, true);
                userInterface.setCurrentUser(ip, port, true);
                while (true) {
                    System.out.print(UserInterfaceOutput.ARE_YOU_DATA_SOURCE);
                    cmd = sc.nextLine();
                    if (cmd.equals(CHOICE_Y)) {
                        userInterface.addDataSourceUser(ip, port, true);
                        break;
                    } else if (cmd.equals(CHOICE_N)) {
                        break;
                    }
                }
                System.out.println("Regression creator successfully added.");
                System.out.println("IPv4 address: " + ip);
                System.out.println("Port number: " + port);
                break;
            } else {
                System.out.println(UserInterfaceOutput.INVALID_PORT);
            }
        }
    }

    public void doAddUser(Scanner scanner) {
        String ip;
        int port;
        System.out.println(UserInterfaceOutput.ADD_USER);
        while (true) {
            System.out.print(UserInterfaceOutput.USER_IP);
            String cmd = scanner.nextLine();
            if (cmd.matches(IPADDRESS_PATTERN)) {
                ip = cmd;
                break;
            } else {
                System.out.println(UserInterfaceOutput.INVALID_IP);
            }
        }
        while (true) {
            System.out.print(UserInterfaceOutput.USER_PORT_NUMBER);
            String cmd = scanner.nextLine();
            if (cmd.matches(PORT_PATTERN)) {
                port = Integer.valueOf(cmd);
                break;
            } else {
                System.out.println(UserInterfaceOutput.INVALID_PORT);
            }
        }
        userInterface.addDataSourceUser(ip, port, false);
        System.out.println(UserInterfaceOutput.USER_ADDED);
    }

    public void doListUsers() {
        List<String> users = userInterface.getDataSourceUserDetail();
        if (users.isEmpty()) {
            System.out.println(UserInterfaceOutput.NO_USER);
            return;
        }
        System.out.println(UserInterfaceOutput.USER_LIST);
        int i = 1;
        for (String u : users) {
            System.out.println(i + ". " + u);
            i++;
        }
    }

    public void doRemoveUser(Scanner sc) {
        doListUsers();
        List<String> list = userInterface.getDataSourceUserDetail();
        if (list.isEmpty()) {
            return;
        }
        System.out.print(UserInterfaceOutput.REMOVE_USER);
        String cmd = sc.nextLine();
        if (cmd.matches(PORT_PATTERN)) {
            int choice = Integer.valueOf(cmd);
            if (1 <= choice && choice <= list.size()) {
                choice--;
                String[] userInfo = list.get(choice).split(":");
                userInterface.removeDataSourceUser(userInfo[0], Integer.valueOf(userInfo[1]));
                System.out.println(UserInterfaceOutput.USER_REMOVED);
                return;
            }
        }
        System.out.println(UserInterfaceOutput.INVALID_CHOICE);
    }

    public void doCreatorStartRegression(Scanner sc) {
        System.out.println(UserInterfaceOutput.WAITE);
        while (!userInterface.checkAllDataSourceUserConnected()) {
            try {
                printClientMessage();
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        printClientMessage();
        System.out.println("All data providers are connected.");
        System.out.println("Sending data provider information ...");
        userInterface.sendDataSourceUserInformation();
        doSelectRegressionType(sc);
    }

    public void printClientMessage() {
        String msg = userInterface.getClinetMessage();
        if (!msg.equals("")) {
            System.out.print(msg);
        }
    }

    public void doJoinRegression() {
        String ip;
        int port;
        Scanner sc = new Scanner(System.in);
        System.out.println(UserInterfaceOutput.ENTER_CREATOR_INFORMATION);
        while (true) {
            System.out.print(UserInterfaceOutput.CREATOR_IP);
            String cmd = sc.nextLine();
            if (cmd.matches(IPADDRESS_PATTERN)) {
                ip = cmd;
                break;
            } else {
                System.out.println(UserInterfaceOutput.INVALID_IP);
            }
        }
        while (true) {
            System.out.print(UserInterfaceOutput.CREATOR_PORT_NUMBER);
            String cmd = sc.nextLine();
            if (cmd.matches(PORT_PATTERN)) {
                port = Integer.valueOf(cmd);
                break;
            } else {
                System.out.println(UserInterfaceOutput.INVALID_PORT);
            }
        }
        userInterface.addCreator(ip, port, true);
        ip = "";
        try {
            ip = Inet4Address.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        while (true) {
            System.out.print(UserInterfaceOutput.ENTER_YOUR_PORT);
            String cmd = sc.nextLine();
            if (cmd.matches(PORT_PATTERN)) {
                port = Integer.valueOf(cmd);
                break;
            } else {
                System.out.println(UserInterfaceOutput.INVALID_PORT);
            }
        }
        userInterface.setCurrentUser(ip, port, true);
        doJoinerStartRegression(sc);
        sc.close();
    }

    public void doJoinerStartRegression(Scanner sc) {
        userInterface.pingCreator();
        System.out.println(UserInterfaceOutput.WAITE);
        while (!userInterface.checkAllDataSourceUserConnected()) {
            try {
                printClientMessage();
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        printClientMessage();
        System.out.println("All data providers are connected.");
        doUploadMatrix(sc);
    }

    public void doSelectRegressionType(Scanner sc) {
        while (true) {
            System.out.println("Please select regression type:");
            System.out.println("1. Linear regression");
            System.out.println("2. Binomial Logistic regression");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");
            String cmd = sc.nextLine();
            if (cmd.equals("0")) {
                break;
            } else if (cmd.equals("1")) {
                userInterface.setRegressionType("linearRegression");
            } else if (cmd.equals("2")) {
                userInterface.setRegressionType("binomialLogisitcRegression");
            } else {
                continue;
            }
            doUploadMatrix(sc);
            break;
        }
    }

    public void doUploadMatrix(Scanner sc) {
        String responseMatrix;
        String designMatrix;
        boolean confirm = false;
        while (!confirm) {
            System.out.println("Please enter path of response matrix: ");
            String path = sc.nextLine();
            Reader reader = new Reader();
            responseMatrix = reader.readMatrixString(path);
            System.out.println("Please enter path of design matrix: ");
            path = sc.nextLine();
            File f = new File(path);
            if (!f.exists()) {
                System.out.println("File does not exit: " + path);
                continue;
            }
            designMatrix = reader.readMatrixString(path);
            if (responseMatrix != null && designMatrix != null) {
                System.out.println("1. Continue");
                System.out.println("2. Change matrix path");
                System.out.println("0. Exit");
                System.out.print("Enter choice: ");
                String cmd = sc.nextLine();
                if (cmd.equals(CHOICE_ONE)) {
                    userInterface.setResponseMatrix(responseMatrix);
                    userInterface.setDesignMatrix(designMatrix);
                    confirm = true;
                } else if (cmd.equals(CHOICE_ZERO)) {
                    break;
                }
            }
            if (confirm) {
                this.startRegression();
            }
        }
    }

    public void startRegression() {
        System.out.println("Performing regression ...");
        userInterface.startRegression();
        while (!userInterface.checkRegressionEnd()) {
            try {
                printClientMessage();
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        printClientMessage();
        System.out.println("Regression end.");
    }
}
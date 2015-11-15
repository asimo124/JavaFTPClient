import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.commons.net.ftp.FTPFile;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * mftp client - list, get, and put files (recursively if needed)
 */
public class Command {

    public static String port = "21";
    public static String username;
    public static String password;
    public static String host;
    public static boolean doneConnecting = false;
    public static StringBuilder directory = new StringBuilder("");
    public static FTPClient ftpClient = new FTPClient();

    public static void Command() { }

    protected void runCommand(String[] args) {
        String command = "";
        if (args.length > 0) {
            command  = args[0];
        }
        int i = 0;
        ArrayList<String> args_next = new ArrayList<>();
        for (String arg : args) {
            if (i > 0) {
                args_next.add(arg);
            }
            i++;
        }
        Command comm = new Command();
        switch (command) {
            case "open":
                comm.runOpen(args_next);
                break;
            case "close":
                comm.disconnect();
                System.out.println("Closed connection");
                System.exit(1);
                break;
            case "get":
                break;
            case "put":
                break;
            case "cd":
                comm.changeRemoteDir(args_next);
                break;
            case "ls":
                comm.listRemoteDir();
                break;
            default:
                Command.doneConnecting = true;
                System.out.println("Unrecognized command");
        }
    }
    private void runOpen(ArrayList<String> args_next) {
        if (args_next.size() < 1) {
            System.out.println("Not enough arguments passed.");
            return;
        }
        int i = 0;
        boolean set_main_arg = false;
        ListIterator args_itr = args_next.listIterator();
        while(args_itr.hasNext()) {
            Object get_arg1 = args_itr.next();
            Object get_arg2 = null;
            char[] charArgs1= get_arg1.toString().toCharArray();
            if (charArgs1[0] == '-') { // is option argument
                if (args_itr.hasNext()) {
                    get_arg2 = args_itr.next();
                }
                char[] charArgs2 = get_arg2.toString().toCharArray();
                if (charArgs2[0] == '-') {
                    System.out.println("Specificed invalid argument after option");
                    System.exit(0);
                }
                switch (charArgs1[1]) {
                    case 'P':
                        Command.port = (get_arg2.toString());
                        break;
                    case 'u':
                        Command.username = get_arg2.toString();
                        break;
                    case 'p':
                        Pattern p = Pattern.compile("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}");
                        Matcher m = p.matcher(get_arg2.toString());
                        if (m.matches()) {
                            System.out.println("Invalid order of arguments.");
                            System.exit(0);
                        }
                        Command.password = get_arg2.toString();
                        break;
                }
            }
            else {
                if (set_main_arg == false) {
                    Command.host = get_arg1.toString();
                }
                set_main_arg = true;
            }
        }
        this.connect();
        if (Command.doneConnecting == true) {
            System.out.println("Connection Open");
            ftpclient.requestInput();
        }
    }
    private void listRemoteDir() {
        try {
            FTPFile[] files = this.ftpClient.listFiles(this.directory.toString());
            for (int i = 0; i < files.length; i++) {
                if (i == 0) {
                    System.out.println("");
                }
                if (files[i].isDirectory()) {
                    System.out.println("-- [" + files[i].getName() + "]");
                }
                else {
                    System.out.println(files[i].getName());
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        ftpclient.requestInput();
    }
    private void changeRemoteDir(ArrayList<String> args_next) {
        if (args_next.size() < 1) {
            return;
        }
        String change_dir = args_next.get(0);
        if (!change_dir.substring(change_dir.length() - 2, change_dir.length() - 1).equals("/")) {
            change_dir = change_dir + "/";
        }
        Command.directory.append(change_dir);
        ftpclient.requestInput();
    }
    private void connect() {
        if (Command.host.equals("247")) {
            Command.host = "71.40.14.247";
            Command.username = "alextest2";
            Command.password = "gortex22";
            Command.port = "21";
        }
        String server = Command.host;
        String port2 = Command.port;
        int port = Integer.parseInt(port2);
        String user = Command.username;
        String pass = Command.password;
        Command.doneConnecting = false;
        try {
            this.ftpClient.connect(server, port);
        }
        catch (IOException ex) {
            Command.doneConnecting = true;
            System.out.println("Error: " + ex.getMessage());
            System.exit(0);
        }
        catch (NullPointerException ex) {
            Command.doneConnecting = true;
            System.out.println("Error: " + ex.getMessage());
            System.exit(0);
        }
        try {
            this.ftpClient.login(user, pass);
            String get_reply = this.ftpClient.getReplyString();
            if (!get_reply.equals("230 Logged on\r\n")) {
                System.out.println("Invalid credentials");
                System.exit(0);
            }
        }
        catch (IOException ex) {
            Command.doneConnecting = true;
            System.out.println("Error: " + ex.getMessage());
            System.exit(0);
        }
        catch (NullPointerException ex) {
            Command.doneConnecting = true;
            System.out.println("Error: " + ex.getMessage());
            System.exit(0);
        }
        Command.doneConnecting = true;
        if (!this.ftpClient.isConnected()) {
           System.out.println("Invalid credentials");
           return;
        }
        this.ftpClient.enterLocalPassiveMode();
        ftpclient.requestInput();
    }
    private void disconnect() {
        try {
            if (this.ftpClient.isConnected()) {
                this.ftpClient.logout();
                this.ftpClient.disconnect();
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    /*/
    private void uploadFile() {
            this.ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            // APPROACH #2: uploads second file using an OutputStream
            File secondLocalFile = new File("C:/Users/alex/Pictures/Java/Projects/files/ftpclient/test2.txt");
            InputStream inputStream = new FileInputStream(secondLocalFile);
            String secondRemoteFile = "test2.txt";
            inputStream = new FileInputStream(secondLocalFile);
            System.out.println("Start uploading second file");
            OutputStream outputStream = ftpClient.storeFileStream(secondRemoteFile);
            byte[] bytesIn = new byte[4096];
            int read = 0;
            while ((read = inputStream.read(bytesIn)) != -1) {
                outputStream.write(bytesIn, 0, read);
            }
            inputStream.close();
            outputStream.close();
            boolean completed = ftpClient.completePendingCommand();
            if (completed) {
                System.out.println("File uploaded successfully.");
            }
        }
        catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
            ex.printStackTrace();
        }
        finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    //*/
}
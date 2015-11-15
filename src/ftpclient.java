import java.util.Scanner;

public class ftpclient {
	public void ftpclient() { }
    public static void main(String[] args) {
        ftpclient.requestInput();
    }
    public static void requestInput() {
        Scanner sc = new Scanner(System.in);
        String use_dir = "";
        if (Command.directory.length() > 1) {
            use_dir = Command.directory.substring(0, Command.directory.length() - 1);
        }
        System.out.print("mftp /" + use_dir + "> ");
        String input = sc.nextLine();
        String[] command_args = input.split("\\s+");
        Command comm2 = new Command();
        comm2.runCommand(command_args);
    }
}

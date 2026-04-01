import java.util.Scanner;

public class MiniGit {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Repository repo = new Repository();
        repo.load();

        System.out.println("Welcome to MiniGit! 🚀");
        System.out.println("Available commands: init, add <filename>, commit <message>, log, checkout <commitId|branch>, branch <name>, revert <commitId>, mkdir <foldername>, exit");

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine();
            String[] parts = input.split(" ", 2);
            String command = parts[0];

            switch (command) {
                case "init":
                    repo.init();
                    break;
                case "add":
                    if (parts.length > 1) repo.addFile(parts[1]);
                    else System.out.println("Please specify a filename.");
                    break;
                case "commit":
                    if (parts.length > 1) repo.commit(parts[1]);
                    else System.out.println("Please specify a commit message.");
                    break;
                case "log":
                    repo.log();
                    break;
                case "checkout":
                    if (parts.length > 1) repo.checkout(parts[1]);
                    else System.out.println("Please specify a commit ID or branch name.");
                    break;
                case "branch":
                    if (parts.length > 1) repo.createBranch(parts[1]);
                    else System.out.println("Please specify a branch name.");
                    break;
                case "revert":
                    if (parts.length > 1) repo.revert(parts[1]);
                    else System.out.println("Please specify a commit ID.");
                    break;
                case "mkdir":
                    if (parts.length > 1) repo.makeDirectory(parts[1]);
                    else System.out.println("Please specify a folder name.");
                    break;
                case "exit":
                    repo.save();
                    System.out.println("Exiting MiniGit. Goodbye!");
                    return;
                default:
                    System.out.println("Unknown command. Try again!");
            }
        }
    }
}

package gitlet;

import java.io.File;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @Rossi TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String... args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }

        Repository repository = new Repository();
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                validateNumArgs(args, 1);
                repository.init();
                break;
            case "add":
                validateNumArgs(args, 2);
                repository.add(args[1]);
                break;
            case "commit":
                validateNumArgs(args, 2);
                repository.commit(args[1]);
                break;
            case "rm":
                validateNumArgs(args, 2);
                repository.remove(args[1]);
                break;
            case "log":
                validateNumArgs(args, 1);
                repository.log();
                break;
            case "global-log":
                validateNumArgs(args, 1);
                repository.globalLog();
                break;
            case "find":
                validateNumArgs(args, 2);
                repository.find(args[1]);
                break;
            case "status":
                validateNumArgs(args, 1);
                repository.status();
                break;
            case "checkout":
                if (args.length > 4 || args.length < 2) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                } else if (args.length == 4) {
                    repository.checkout(args[1], args[3]);
                } else if (args.length == 3) {
                    repository.checkout(args[2]);
                } else {
                    repository.checkoutBranch(args[1]);
                }
                break;
            case "branch":
                validateNumArgs(args, 2);
                repository.branch(args[1]);
                break;
            case "rm-branch":
                validateNumArgs(args, 2);
                repository.removeBranch(args[1]);
                break;
            case "reset":
                validateNumArgs(args, 2);
                repository.reset(args[1]);
                break;
            /** Check the spec for detailed description for merge */
            case "merge":
                validateNumArgs(args, 2);
                repository.merge(args[1]);
                break;
            default:
                System.out.println("No command with that name exists.");
                System.exit(0);
        }
    }

    public static void validateNumArgs(String[] args, int n) {
        if (args.length != n) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }
}

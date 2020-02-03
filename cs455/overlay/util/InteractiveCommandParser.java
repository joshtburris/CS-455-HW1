package cs455.overlay.util;

import java.util.Scanner;

public class InteractiveCommandParser {

    Scanner in;
    String line;
    
    public InteractiveCommandParser() {
        in = new Scanner(System.in);
    }
    
    // Returns "exit" string as an indicator to stop accepting new console commands.
    public String getConsoleCommand() {
        
        // Read in a console commands
        System.out.print(">>> ");
        line = in.nextLine().trim().toLowerCase();
        
        // Check if an exit command was given, and return empty if it was
        switch (line) {
            case "exit": case "quit": case "stop":
                return "exit";
            default:
                return line;
        }
    }

}

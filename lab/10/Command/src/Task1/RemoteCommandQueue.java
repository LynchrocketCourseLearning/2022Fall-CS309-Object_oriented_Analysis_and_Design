package Task1;

import java.util.ArrayDeque;
import java.util.Queue;

public class RemoteCommandQueue {
    Queue<Command> commands;

    public RemoteCommandQueue() {
        commands = new ArrayDeque<>();
    }

    /**
     * only add command but not execute
     *
     * @param command
     */
    public void buttonPressed(Command command) {
        //todo: complete
        commands.add(command);
    }

    /**
     * execute the command in the queue by first-in-first-out principle
     * if there is no command in the queue display "no command"
     */
    public void commandExecute() {
        // todo: compelte
        if(commands.isEmpty()){
            System.out.println("No command");
        }else {
            commands.poll().execute();
        }
    }

}

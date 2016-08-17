package pro.zackpollard.telegrambot.api.event.chat.message;

import lombok.ToString;
import pro.zackpollard.telegrambot.api.chat.message.Message;

/**
 * @author Zack Pollard
 */
@ToString
public class CommandMessageReceivedEvent extends TextMessageReceivedEvent {

    private final String[] args;
    private final String argsString;
    private final String command;

    public CommandMessageReceivedEvent(Message message) {

        super(message);

        this.command = getContent().getContent().substring(1).split(" ")[0].split("@")[0];

        int argsStart = getContent().getContent().indexOf(" ");

        if (argsStart != -1) {

            this.argsString = getContent().getContent().substring(argsStart).trim();
            this.args = argsString.split(" ");
        } else {

            this.args = new String[]{};
            this.argsString = "";
        }
    }

    /**
     * Gets the arguments for the received command
     *
     * @return An array of the arguments for the received command
     */
    public String[] getArgs() {
        return this.args;
    }

    /**
     * Gets a string of all of the arguments as they were entered into the telegram client
     *
     * @return A String of all the arguments for the received command
     */
    public String getArgsString() {
        return this.argsString;
    }

    /**
     * Gets the command that was received that fired this Event
     *
     * @return The command that was receied that fired this Event
     */
    public String getCommand() {
        return this.command;
    }
}
package pro.zackpollard.telegrambot.api.chat;

/**
 * @author Zack Pollard
 */
public interface ChannelChat extends Chat {

    /**
     * Gets the Username of the Channel that this ChannelChat object refers to
     *
     * @return The Username of this Channel
     */
    String getUsername();

    /**
     * Gets the ChatType of this Chat
     *
     * @return The ChatType of this Chat
     */
    default ChatType getType() {

        return ChatType.CHANNEL;
    }
}

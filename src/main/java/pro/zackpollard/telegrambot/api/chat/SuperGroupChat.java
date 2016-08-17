package pro.zackpollard.telegrambot.api.chat;

/**
 * @author Zack Pollard
 */
public interface SuperGroupChat extends GroupChat {

    default ChatType getType() {

        return ChatType.SUPERGROUP;
    }

    /**
     * Gets the Username of the SuperGroup that this SuperGroupChat object refers to
     *
     * @return The Username of this SuperGroup
     */
    String getUsername();

    /**
     * This method will kick and ban the User with the specified User ID from the chat if they are currently in it
     *
     * @param userId The User ID of the User you want to kick from the chat
     *
     * @return True if the user was kicked, otherwise False
     */
    boolean kickChatMember(int userId);

    /**
     * This method will unban a user that was previously banned from the chat
     *
     * @param userId The ID of the User you want to unban
     *
     * @return True if the user was unbanned, otherwise False
     */
    boolean unbanChatMember(int userId);
}

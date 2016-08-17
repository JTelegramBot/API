package pro.zackpollard.telegrambot.api.event.chat;

import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.chat.message.Message;
import pro.zackpollard.telegrambot.api.chat.message.content.NewChatParticipantContent;
import pro.zackpollard.telegrambot.api.event.chat.message.MessageEvent;
import pro.zackpollard.telegrambot.api.user.User;

/**
 * @author Zack Pollard
 */
public class ParticipantJoinGroupChatEvent extends MessageEvent {

    public ParticipantJoinGroupChatEvent(Message message) {
        super(message);
    }

    /**
     * Gets the User that joined the GroupChat that triggered this Event
     *
     * @return The User that joined the GroupChat that triggered this Event
     */
    public User getParticipant() {

        return ((NewChatParticipantContent) getMessage().getContent()).getContent();
    }

    /**
     * Gets the Chat that the User joined that triggered this Event
     *
     * @return The Chat that the User joined that triggered this Event
     */
    public Chat getChat() {

        return getMessage().getChat();
    }
}

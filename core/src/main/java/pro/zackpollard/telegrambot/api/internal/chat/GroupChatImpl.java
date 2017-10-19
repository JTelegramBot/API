package pro.zackpollard.telegrambot.api.internal.chat;

import org.json.JSONObject;
import pro.zackpollard.telegrambot.api.TelegramBot;
import pro.zackpollard.telegrambot.api.chat.ChatPhoto;
import pro.zackpollard.telegrambot.api.chat.GroupChat;
import pro.zackpollard.telegrambot.api.chat.message.Message;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableMessage;

/**
 * @author Zack Pollard
 */
public class GroupChatImpl implements GroupChat {

    private final int id;
    private final String title;
    private final boolean allMembersAreAdministrators;
    private final ChatPhoto photo;

    private final TelegramBot telegramBot;

    private GroupChatImpl(JSONObject jsonObject, TelegramBot telegramBot) {

        this.id = jsonObject.getInt("id");
        this.title = jsonObject.getString("title");
        this.allMembersAreAdministrators = jsonObject.optBoolean("all_members_are_administrators");
        this.photo = ChatPhotoImpl.createChatPhoto(jsonObject.optJSONObject("photo"));
        this.telegramBot = telegramBot;
    }

    public static GroupChat createGroupChat(JSONObject jsonObject, TelegramBot telegramBot) {

        return new GroupChatImpl(jsonObject, telegramBot);
    }

    /**
     * Gets the name of the group chat.
     *
     * @return The group chat name, currently can be null due to chat creation by ID with no way of getting the group chats name from telegram servers.
     */
    @Override
    public String getName() {
        return title;
    }

    @Override
    public ChatPhoto getPhoto() {
        return photo;
    }

    @Override
    public TelegramBot getBotInstance() {
        return telegramBot;
    }

    @Override
    public String getId() {
        return String.valueOf(id);
    }

    @Override
    public Message sendMessage(SendableMessage message) {

        return telegramBot.sendMessage(this, message);
    }

    @Override
    public boolean isAllMembersAreAdministrators() {
        return allMembersAreAdministrators;
    }

    @Override
    public boolean kickChatMember(int userId, long until_date) {

        return telegramBot.kickChatMember(this.getId(), userId, until_date);
    }
}
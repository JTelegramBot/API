package pro.zackpollard.telegrambot.api.internal.chat.message.content;

import org.json.JSONObject;
import pro.zackpollard.telegrambot.api.TelegramBot;
import pro.zackpollard.telegrambot.api.chat.message.content.Content;

/**
 * @author Zack Pollard
 */
public class ContentImpl {

    private final static String[] CONTENT_TYPE_NAMES = new String[]{
            "text", "audio", "document", "game", "photo", "sticker",
            "video", "voice", "contact", "location", "venue",
            "new_chat_member", "left_chat_member", "new_chat_title",
            "new_chat_photo", "delete_chat_photo", "group_chat_created",
            "supergroup_chat_created", "channel_chat_created",
            "migrate_to_chat_id", "migrate_from_chat_id", "pinned_message"
    };

    public static Content createContent(JSONObject jsonObject, TelegramBot telegramBot) {

        String messageType = null;

        for (String contentType : CONTENT_TYPE_NAMES) {

            if (!jsonObject.isNull(contentType)) {

                messageType = contentType;
                break;
            }
        }

        if (messageType != null) {
            switch (messageType) {

                case "text":

                    return TextContentImpl.createTextContent(jsonObject.getString("text"), jsonObject.optJSONArray("entities"));
                case "audio":

                    return AudioContentImpl.createAudioContent(jsonObject.getJSONObject("audio"));
                case "document":

                    return DocumentContentImpl.createDocumentContent(jsonObject.getJSONObject("document"), jsonObject.optString("caption"));
                case "game":

                    return GameContentImpl.createGameContent(jsonObject.getJSONObject("game"));
                case "photo":

                    return PhotoContentImpl.createPhotoContent(jsonObject.getJSONArray("photo"), jsonObject.optString("caption"));
                case "sticker":

                    return StickerContentImpl.createStickerContent(jsonObject.getJSONObject("sticker"));
                case "video":

                    return VideoContentImpl.createVideoContent(jsonObject.getJSONObject("video"), jsonObject.optString("caption"));
                case "video_note":
                    
                    return VideoNoteContentImpl.createVideoNoteContent(jsonObject.getJSONObject("video_note"));
                case "voice":

                    return VoiceContentImpl.createVoiceContent(jsonObject.getJSONObject("voice"));
                case "contact":

                    return ContactContentImpl.createContactContent(jsonObject.getJSONObject("contact"));
                case "location":

                    return LocationContentImpl.createLocationContent(jsonObject.getJSONObject("location"));
                case "venue":

                    return VenueContentImpl.createVenueContent(jsonObject.getJSONObject("venue"));
                case "new_chat_member":

                    return NewChatParticipantContentImpl.createNewParticipantContent(jsonObject.getJSONObject("new_chat_member"));
                case "new_chat_members":
                    
                    return NewChatParticipantsContentImpl.createNewParticipantsContent(jsonObject.getJSONArray("new_chat_members"));
                case "left_chat_member":

                    return LeftChatParticipantContentImpl.createLeftChatParticipantContent(jsonObject.getJSONObject("left_chat_member"));
                case "new_chat_title":

                    return NewChatTitleContentImpl.createNewChatTitleContent(jsonObject.getString("new_chat_title"));
                case "new_chat_photo":

                    return NewChatPhotoContentImpl.createNewChatPhotoContent(jsonObject.getJSONArray("new_chat_photo"));
                case "delete_chat_photo":

                    return DeleteChatPhotoContentImpl.createDeleteChatPhotoContent();
                case "group_chat_created":

                    return GroupChatCreatedContentImpl.createGroupChatCreatedContent();
                case "supergroup_chat_created":

                    return SuperGroupChatCreatedContentImpl.createSuperGroupChatCreatedContent();
                case "channel_chat_created":

                    return ChannelChatCreatedContentImpl.createChannelChatCreatedContent();
                case "migrate_to_chat_id":

                    return MigrateToChatIDContentImpl.createMigrateToChatIDContent(jsonObject.getLong("migrate_to_chat_id"));
                case "migrate_from_chat_id":

                    return MigrateFromChatIDContentImpl.createMigrateFromChatIDContent(jsonObject.getLong("migrate_from_chat_id"));
                case "pinned_message":

                    return PinnedMessageContentImpl.createPinnedMessageContent(jsonObject.getJSONObject("pinned_message"), telegramBot);
            }
        }

        System.err.println("Unsupported message content received, report to developer. Supposed message type was " + (messageType != null ? messageType : "null"));
        return null;
    }
}

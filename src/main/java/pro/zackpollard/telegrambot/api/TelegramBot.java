package pro.zackpollard.telegrambot.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequestWithBody;
import com.mashape.unirest.request.body.MultipartBody;
import lombok.Getter;
import org.json.JSONException;
import org.json.JSONObject;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.chat.inline.InlineReplyMarkup;
import pro.zackpollard.telegrambot.api.chat.inline.send.InlineQueryResponse;
import pro.zackpollard.telegrambot.api.chat.message.ForceReply;
import pro.zackpollard.telegrambot.api.chat.message.Message;
import pro.zackpollard.telegrambot.api.chat.message.content.*;
import pro.zackpollard.telegrambot.api.chat.message.content.type.PhotoSize;
import pro.zackpollard.telegrambot.api.chat.message.content.type.Sticker;
import pro.zackpollard.telegrambot.api.chat.message.content.type.Video;
import pro.zackpollard.telegrambot.api.chat.message.content.type.Voice;
import pro.zackpollard.telegrambot.api.chat.message.send.*;
import pro.zackpollard.telegrambot.api.event.ListenerRegistry;
import pro.zackpollard.telegrambot.api.internal.chat.ChannelChatImpl;
import pro.zackpollard.telegrambot.api.internal.chat.GroupChatImpl;
import pro.zackpollard.telegrambot.api.internal.chat.IndividualChatImpl;
import pro.zackpollard.telegrambot.api.internal.chat.SuperGroupChatImpl;
import pro.zackpollard.telegrambot.api.internal.chat.message.MessageImpl;
import pro.zackpollard.telegrambot.api.internal.chat.message.send.FileContainer;
import pro.zackpollard.telegrambot.api.internal.event.ListenerRegistryImpl;
import pro.zackpollard.telegrambot.api.internal.managers.FileManager;
import pro.zackpollard.telegrambot.api.internal.updates.RequestUpdatesManager;
import pro.zackpollard.telegrambot.api.keyboards.InlineKeyboardMarkup;
import pro.zackpollard.telegrambot.api.keyboards.ReplyKeyboardHide;
import pro.zackpollard.telegrambot.api.keyboards.ReplyKeyboardMarkup;
import pro.zackpollard.telegrambot.api.updates.UpdateManager;

/**
 * @author Zack Pollard
 */
public final class TelegramBot {

    public static final String API_URL = "https://api.telegram.org/";
    private static final Gson GSON = new GsonBuilder().create();
    @Getter
    private final static FileManager fileManager = new FileManager();

    @Getter
    private final String authToken;
    private final ListenerRegistry listenerRegistry;
    private UpdateManager updateManager = null;

    @Getter
    private final int botID;
    @Getter
    private final String botName;
    @Getter
    private final String botUsername;

    private TelegramBot(String authToken, int botID, String botName, String botUsername) {

        this.authToken = authToken;
        this.botID = botID;
        this.botName = botName;
        this.botUsername = botUsername;

        listenerRegistry = ListenerRegistryImpl.getNewInstance();
    }

    /**
     * Use this method to get a new TelegramBot instance with the selected auth token.
     *
     * @param authToken The bots auth token.
     * @return A new TelegramBot instance to base your bot around.
     */

    public static TelegramBot login(String authToken) {

        try {

            HttpRequestWithBody request = Unirest.post(API_URL + "bot" + authToken + "/getMe");
            HttpResponse<String> response = request.asString();
            JSONObject jsonResponse = processResponse(response);

            if (jsonResponse != null && checkResponseStatus(jsonResponse)) {

                JSONObject result = jsonResponse.getJSONObject("result");

                return new TelegramBot(authToken, result.getInt("id"), result.getString("first_name"), result.getString("username"));
            }
        } catch (UnirestException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Chat getChat(long chatID) {

        if (chatID < 0) {

            /** This is a guess of the starting value of supergroups **/
            if (chatID > -999999999) {

                return GroupChatImpl.createGroupChat((int) chatID, this);
            } else {

                return SuperGroupChatImpl.createSuperGroupChat(chatID, this);
            }
        } else {

            return IndividualChatImpl.createIndividualChat((int) chatID, this);
        }
    }

    public Chat getChat(String chatID) {

        if (chatID != null && chatID.length() > 0) {

            if (chatID.charAt(0) == '@') {

                return ChannelChatImpl.createChannelChat(chatID, this);
            } else {

                long longChatID;

                try {

                    longChatID = Long.parseLong(chatID);
                } catch (NumberFormatException e) {

                    System.err.println("TelegramBot#getChat(String chatID) was called with invalid ChatID.");
                    return null;
                }

                return getChat(longChatID);
            }
        }

        return null;
    }

    public String getBotAPIUrl() {

        return API_URL + "bot" + authToken + "/";
    }

    public Message sendMessage(Chat chat, SendableMessage message) {

        HttpResponse<String> response;
        JSONObject jsonResponse = null;
        Message messageResponse = null;

        switch (message.getType()) {

            case TEXT: {

                SendableTextMessage textMessage = (SendableTextMessage) message;

                try {
                    MultipartBody request = Unirest.post(getBotAPIUrl() + "sendMessage")
                            .field("chat_id", chat.getId(), "application/json")
                            .field("text", textMessage.getMessage(), "application/json")
                            .field("disable_web_page_preview", textMessage.isDisableWebPagePreview())
                            .field("parse_mode", textMessage.getParseMode() != null ? textMessage.getParseMode().getModeName() : ParseMode.NONE);

                    processReplyContent(request, textMessage);
                    processNotificationContent(request, textMessage);

                    response = request.asString();
                    jsonResponse = processResponse(response);
                } catch (UnirestException e) {
                    e.printStackTrace();
                }

                break;
            }
            case FORWARDED: {

                SendableForwardMessage forwardMessage = (SendableForwardMessage) message;

                try {
                    MultipartBody request = Unirest.post(getBotAPIUrl() + "forwardMessage")
                            .field("chat_id", chat.getId(), "application/json")
                            .field("from_chat_id", forwardMessage.getChatID())
                            .field("message_id", forwardMessage.getMessageID());

                    processNotificationContent(request, forwardMessage);

                    response = request.asString();
                    jsonResponse = processResponse(response);
                } catch (UnirestException e) {
                    e.printStackTrace();
                }

                break;
            }
            case PHOTO: {

                SendablePhotoMessage photoMessage = (SendablePhotoMessage) message;

                try {
                    MultipartBody request = Unirest.post(getBotAPIUrl() + "sendPhoto")
                            .field("chat_id", chat.getId(), "application/json")
                            .field("photo", photoMessage.getPhoto().getFileID() != null ? photoMessage.getPhoto().getFileID() : new FileContainer(photoMessage.getPhoto()), photoMessage.getPhoto().getFileID() == null);

                    if (photoMessage.getCaption() != null)
                        request.field("caption", photoMessage.getCaption(), "application/json");

                    processReplyContent(request, photoMessage);
                    processNotificationContent(request, photoMessage);

                    response = request.asString();
                    jsonResponse = processResponse(response);
                } catch (UnirestException e) {
                    e.printStackTrace();
                }

                messageResponse = checkResponseStatus(jsonResponse) ? (MessageImpl.createMessage(jsonResponse != null ? jsonResponse : null, this)) : null;

                //Photo cacheing to FileManager
                if (photoMessage.getPhoto().getFile() != null && messageResponse != null) {

                    if (!messageResponse.getContent().getType().equals(ContentType.PHOTO)) {

                        System.err.println("The API returned content type " + messageResponse.getContent().getType().name() + " when a " + message.getType() + " type was sent, this is not supported by this API and will break cacheing, please create an issue on github or message @zackpollard on telegram.");
                        break;
                    }

                    PhotoSize[] photoSizes = ((PhotoContent) messageResponse.getContent()).getContent();

                    int largestPhotoSize = 0;
                    int largestPhotoID = 0;

                    for (int i = 0; i < photoSizes.length; ++i) {

                        int size = photoSizes[i].getHeight() * photoSizes[i].getWidth();

                        if (largestPhotoSize < size) {

                            largestPhotoSize = size;
                            largestPhotoID = i;
                        }
                    }

                    fileManager.cacheFileID(photoMessage.getPhoto().getFile(), photoSizes[largestPhotoID].getFileId());
                }

                break;
            }
            case AUDIO: {

                SendableAudioMessage audioMessage = (SendableAudioMessage) message;

                try {
                    MultipartBody request = Unirest.post(getBotAPIUrl() + "sendAudio")
                            .field("chat_id", chat.getId(), "application/json")
                            .field("audio", audioMessage.getAudio().getFileID() != null ? audioMessage.getAudio().getFileID() : new FileContainer(audioMessage.getAudio()), audioMessage.getAudio().getFileID() == null);

                    processReplyContent(request, audioMessage);
                    processNotificationContent(request, audioMessage);

                    if (audioMessage.getDuration() != 0) request.field("duration", audioMessage.getDuration());
                    if (audioMessage.getPerformer() != null)
                        request.field("performer", audioMessage.getPerformer(), "application/json");
                    if (audioMessage.getTitle() != null)
                        request.field("title", audioMessage.getTitle(), "application/json");

                    response = request.asString();
                    jsonResponse = processResponse(response);
                } catch (UnirestException e) {
                    e.printStackTrace();
                }

                messageResponse = checkResponseStatus(jsonResponse) ? (MessageImpl.createMessage(jsonResponse != null ? jsonResponse : null, this)) : null;

                //Audio cacheing to FileManager
                if (audioMessage.getAudio().getFile() != null && messageResponse != null) {

                    String fileID;

                    switch (messageResponse.getContent().getType()) {

                        case AUDIO:
                            fileID = ((AudioContent) messageResponse.getContent()).getContent().getFileId();
                            break;
                        case VOICE:

                            fileID = ((VoiceContent) messageResponse.getContent()).getContent().getFileId();
                            break;
                        default:
                            System.err.println("The API returned content type " + messageResponse.getContent().getType().name() + " when an audio type was sent, this is not supported by this API, please create an issue on github or message @zackpollard on telegram.");
                            return null;
                    }

                    fileManager.cacheFileID(audioMessage.getAudio().getFile(), fileID);
                }

                break;
            }
            case DOCUMENT: {

                SendableDocumentMessage documentMessage = (SendableDocumentMessage) message;

                try {
                    MultipartBody request = Unirest.post(getBotAPIUrl() + "sendDocument")
                            .field("chat_id", chat.getId(), "application/json")
                            .field("document", documentMessage.getDocument().getFileID() != null ? documentMessage.getDocument().getFileID() : new FileContainer(documentMessage.getDocument()), documentMessage.getDocument().getFileID() == null);

                    if (documentMessage.getCaption() != null)
                        request.field("caption", documentMessage.getCaption(), "application/json");

                    processReplyContent(request, documentMessage);
                    processNotificationContent(request, documentMessage);

                    response = request.asString();
                    jsonResponse = processResponse(response);
                } catch (UnirestException e) {
                    e.printStackTrace();
                }

                messageResponse = checkResponseStatus(jsonResponse) ? (MessageImpl.createMessage(jsonResponse != null ? jsonResponse : null, this)) : null;

                //Document cacheing to FileManager
                if (documentMessage.getDocument().getFile() != null && messageResponse != null) {

                    String fileID;

                    switch (messageResponse.getContent().getType()) {

                        case AUDIO:
                            fileID = ((AudioContent) messageResponse.getContent()).getContent().getFileId();
                            break;
                        case DOCUMENT:
                            fileID = ((DocumentContent) messageResponse.getContent()).getContent().getFileId();
                            break;
                        default:
                            System.err.println("The API returned content type " + messageResponse.getContent().getType().name() + " when a document type was sent, this is not supported by this API, please create an issue on github or message @zackpollard on telegram.");
                            return null;
                    }

                    fileManager.cacheFileID(documentMessage.getDocument().getFile(), fileID);
                }

                break;
            }
            case STICKER:

                SendableStickerMessage stickerMessage = (SendableStickerMessage) message;

                try {
                    MultipartBody request = Unirest.post(getBotAPIUrl() + "sendSticker")
                            .field("chat_id", chat.getId(), "application/json")
                            .field("sticker", stickerMessage.getSticker().getFileID() != null ? stickerMessage.getSticker().getFileID() : new FileContainer(stickerMessage.getSticker()), stickerMessage.getSticker().getFileID() == null);

                    processReplyContent(request, stickerMessage);
                    processNotificationContent(request, stickerMessage);

                    response = request.asString();
                    jsonResponse = processResponse(response);
                } catch (UnirestException e) {
                    e.printStackTrace();
                }

                messageResponse = checkResponseStatus(jsonResponse) ? (MessageImpl.createMessage(jsonResponse != null ? jsonResponse : null, this)) : null;

                //Sticker cacheing to FileManager
                if (stickerMessage.getSticker().getFile() != null && messageResponse != null) {

                    if (!messageResponse.getContent().getType().equals(ContentType.STICKER)) {

                        System.err.println("The API returned content type " + messageResponse.getContent().getType().name() + " when a " + message.getType() + " type was sent, this is not supported by this API and will break cacheing, please create an issue on github or message @zackpollard on telegram.");
                        break;
                    }

                    Sticker sticker = ((StickerContent) messageResponse.getContent()).getContent();

                    fileManager.cacheFileID(stickerMessage.getSticker().getFile(), sticker.getFileId());
                }

                break;

            case VIDEO:

                SendableVideoMessage videoMessage = (SendableVideoMessage) message;

                try {
                    MultipartBody request = Unirest.post(getBotAPIUrl() + "sendVideo")
                            .field("chat_id", chat.getId(), "application/json")
                            .field("video", videoMessage.getVideo().getFileID() != null ? videoMessage.getVideo().getFileID() : new FileContainer(videoMessage.getVideo()), videoMessage.getVideo().getFileID() == null);

                    if (videoMessage.getDuration() > 0) request.field("duration", videoMessage.getDuration());
                    if (videoMessage.getWidth() > 0) request.field("width", videoMessage.getWidth());
                    if (videoMessage.getHeight() > 0) request.field("height", videoMessage.getHeight());
                    if (videoMessage.getCaption() != null)
                        request.field("caption", videoMessage.getCaption(), "application/json");

                    processReplyContent(request, videoMessage);
                    processNotificationContent(request, videoMessage);

                    response = request.asString();
                    jsonResponse = processResponse(response);
                } catch (UnirestException e) {
                    e.printStackTrace();
                }

                messageResponse = checkResponseStatus(jsonResponse) ? (MessageImpl.createMessage(jsonResponse != null ? jsonResponse : null, this)) : null;

                //Video cacheing to FileManager
                if (videoMessage.getVideo().getFile() != null && messageResponse != null) {

                    if (!messageResponse.getContent().getType().equals(ContentType.VIDEO)) {

                        System.err.println("The API returned content type " + messageResponse.getContent().getType().name() + " when a " + message.getType() + " type was sent, this is not supported by this API and will break cacheing, please create an issue on github or message @zackpollard on telegram.");
                        break;
                    }

                    Video video = ((VideoContent) messageResponse.getContent()).getContent();

                    fileManager.cacheFileID(videoMessage.getVideo().getFile(), video.getFileId());
                }

                break;
            case VOICE:

                SendableVoiceMessage voiceMessage = (SendableVoiceMessage) message;

                try {
                    MultipartBody request = Unirest.post(getBotAPIUrl() + "sendVoice")
                            .field("chat_id", chat.getId(), "application/json")
                            .field("voice", voiceMessage.getVoice().getFileID() != null ? voiceMessage.getVoice().getFileID() : new FileContainer(voiceMessage.getVoice()), voiceMessage.getVoice().getFileID() == null);

                    if (voiceMessage.getDuration() > 0) request.field("duration", voiceMessage.getDuration());

                    processReplyContent(request, voiceMessage);
                    processNotificationContent(request, voiceMessage);

                    response = request.asString();
                    jsonResponse = processResponse(response);
                } catch (UnirestException e) {
                    e.printStackTrace();
                }

                messageResponse = checkResponseStatus(jsonResponse) ? (MessageImpl.createMessage(jsonResponse != null ? jsonResponse : null, this)) : null;

                //Voice cacheing to FileManager
                if (voiceMessage.getVoice().getFile() != null && messageResponse != null) {

                    if (!messageResponse.getContent().getType().equals(ContentType.VOICE)) {

                        System.err.println("The API returned content type " + messageResponse.getContent().getType().name() + " when a " + message.getType() + " type was sent, this is not supported by this API and will break cacheing, please create an issue on github or message @zackpollard on telegram.");
                        break;
                    }

                    Voice voice = ((VoiceContent) messageResponse.getContent()).getContent();

                    fileManager.cacheFileID(voiceMessage.getVoice().getFile(), voice.getFileId());
                }

                break;

            case LOCATION:

                SendableLocationMessage locationMessage = (SendableLocationMessage) message;

                try {
                    MultipartBody request = Unirest.post(getBotAPIUrl() + "sendLocation")
                            .field("chat_id", chat.getId(), "application/json")
                            .field("latitude", locationMessage.getLatitude())
                            .field("longitude", locationMessage.getLongitude());

                    processReplyContent(request, locationMessage);
                    processNotificationContent(request, locationMessage);

                    response = request.asString();
                    jsonResponse = processResponse(response);
                } catch (UnirestException e) {
                    e.printStackTrace();
                }

                break;
            case VENUE:

                SendableVenueMessage venueMessage = (SendableVenueMessage) message;

                try {

                    MultipartBody request = Unirest.post(getBotAPIUrl() + "sendLocation")
                            .field("chat_id", chat.getId(), "application/json")
                            .field("latitude", venueMessage.getLatitude())
                            .field("longitude", venueMessage.getLongitude())
                            .field("title", venueMessage.getTitle())
                            .field("address", venueMessage.getAddress());

                    if (venueMessage.getFoursquareId() != null) request.field("foursquare_id", venueMessage.getFoursquareId());

                    processReplyContent(request, venueMessage);
                    processNotificationContent(request, venueMessage);

                    response = request.asString();
                    jsonResponse = processResponse(response);
                } catch (UnirestException e) {
                    e.printStackTrace();
                }

                break;
            case CHAT_ACTION:

                SendableChatAction sendableChatAction = (SendableChatAction) message;

                try {
                    MultipartBody request = Unirest.post(getBotAPIUrl() + "sendChatAction")
                            .field("chat_id", chat.getId(), "application/json")
                            .field("action", sendableChatAction.getChatAction().getName());

                    response = request.asString();
                } catch (UnirestException e) {
                    e.printStackTrace();
                }

                return null;
        }

        return checkResponseStatus(jsonResponse) ? (messageResponse != null ? messageResponse : MessageImpl.createMessage(jsonResponse, this)) : null;
    }

    private JSONObject editMessageText(String chatId, Long messageId, String inlineMessageId, String text, String parseMode, boolean disableWebPagePreview, InlineReplyMarkup inlineReplyMarkup) {

        HttpResponse<String> response;
        JSONObject jsonResponse = null;

        try {
            MultipartBody requests = Unirest.post(getBotAPIUrl() + "editMessageText")
                    .field("text", text, "application/json")
                    .field("disable_web_page_preview", disableWebPagePreview);

            if(chatId != null) requests.field("chat_id", chatId, "application/json");
            if(messageId != null) requests.field("message_id", messageId);
            if(inlineMessageId != null) requests.field("inline_message_id", inlineMessageId, "application/json");
            if(parseMode != null) requests.field("parse_mode", parseMode, "application/json");
            if(inlineReplyMarkup != null) requests.field("reply_markup", GSON.toJson(inlineReplyMarkup, InlineKeyboardMarkup.class), "application/json");

            response = requests.asString();
            jsonResponse = processResponse(response);
        } catch (UnirestException e) {
            e.printStackTrace();
        }

        return jsonResponse;
    }

    public Message editMessageText(Message oldMessage, String text, String parseMode, boolean disableWebPagePreview, InlineReplyMarkup inlineReplyMarkup) {

        return this.editMessageText(oldMessage.getChat().getId(), oldMessage.getMessageId(), text, parseMode, disableWebPagePreview, inlineReplyMarkup);
    }

    public Message editMessageText(String chatId, Long messageId, String text, String parseMode, boolean disableWebPagePreview, InlineReplyMarkup inlineReplyMarkup) {

        if(chatId != null && messageId != null && text != null) {

            JSONObject jsonResponse = this.editMessageText(chatId, messageId, null, text, parseMode, disableWebPagePreview, inlineReplyMarkup);

            if (jsonResponse != null) {

                return MessageImpl.createMessage(jsonResponse.getJSONObject("result"), this);
            }
        }

        return null;
    }

    public boolean editInlineMessageText(String inlineMessageId, String text, String parseMode, boolean disableWebPagePreview, InlineReplyMarkup inlineReplyMarkup) {

        if(inlineMessageId != null && text != null) {

            JSONObject jsonResponse = this.editMessageText(null, null, inlineMessageId, text, parseMode, disableWebPagePreview, inlineReplyMarkup);

            if (jsonResponse != null) {

                if (jsonResponse.getBoolean("result")) return true;
            }
        }

        return false;
    }

    public boolean answerInlineQuery(String inlineQueryId, InlineQueryResponse inlineQueryResponse) {

        if (inlineQueryId != null && inlineQueryResponse != null) {

            HttpResponse<String> response;
            JSONObject jsonResponse;

            try {
                MultipartBody requests = Unirest.post(getBotAPIUrl() + "answerInlineQuery")
                        .field("inline_query_id", inlineQueryId)
                        .field("results", GSON.toJson(inlineQueryResponse.getResults()))
                        .field("cache_time", inlineQueryResponse.getCacheTime())
                        .field("is_personal", inlineQueryResponse.isPersonal())
                        .field("next_offset", inlineQueryResponse.getNextOffset());

                response = requests.asString();
                jsonResponse = processResponse(response);

                if (jsonResponse != null) {

                    if (jsonResponse.getBoolean("result")) return true;
                }
            } catch (UnirestException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    public boolean answerCallbackQuery(String callbackQueryId, String text, boolean showAlert) {

        if(callbackQueryId != null && text != null) {

            HttpResponse<String> response;
            JSONObject jsonResponse;

            try {
                MultipartBody requests = Unirest.post(getBotAPIUrl() + "answerCallbackQuery")
                        .field("callback_query_id", callbackQueryId, "application/json")
                        .field("text", text, "application/json")
                        .field("show_alert", showAlert);

                response = requests.asString();
                jsonResponse = processResponse(response);

                if (jsonResponse != null) {

                    if (jsonResponse.getBoolean("result")) return true;
                }
            } catch (UnirestException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    public boolean kickChatMember(String chatId, int userId) {

        HttpResponse<String> response;
        JSONObject jsonResponse;

        try {
            MultipartBody request = Unirest.post(getBotAPIUrl() + "kickChatMember")
                    .field("chat_id", chatId, "application/json")
                    .field("user_id", userId);

            response = request.asString();
            jsonResponse = TelegramBot.processResponse(response);

            if(jsonResponse != null) {

                if(jsonResponse.getBoolean("result")) return true;
            }
        } catch (UnirestException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean unbanChatMember(String chatId, int userId) {

        HttpResponse<String> response;
        JSONObject jsonResponse;

        try {
            MultipartBody request = Unirest.post(getBotAPIUrl() + "unbanChatMember")
                    .field("chat_id", chatId, "application/json")
                    .field("user_id", userId);

            response = request.asString();
            jsonResponse = TelegramBot.processResponse(response);

            if(jsonResponse != null) {

                if(jsonResponse.getBoolean("result")) return true;
            }
        } catch (UnirestException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void startUpdates(boolean getPreviousUpdates) {

        updateManager = new RequestUpdatesManager(this, getPreviousUpdates);
    }

    public ListenerRegistry getEventsManager() {

        return listenerRegistry;
    }

    private static JSONObject processResponse(HttpResponse<String> response) {

        if (response != null) {

            if (response.getStatus() == 200) {

                try {

                    return new JSONObject(response.getBody());
                } catch (JSONException e) {

                    System.err.println("The API didn't return a JSON response. The actual response was " + response.getBody());
                }
            } else {

                JSONObject jsonResponse = null;

                try {

                    jsonResponse = new JSONObject(response.getBody());
                } catch (JSONException e) {
                }

                if (jsonResponse != null) {

                    System.err.println("The API returned the following error: " + jsonResponse.getString("description"));
                } else {

                    System.err.println("The API returned error code " + response.getStatus());
                }
            }
        }

        return null;
    }

    private static void processReplyContent(MultipartBody multipartBody, ReplyingOptions replyingOptions) {

        if (replyingOptions.getReplyTo() != 0)
            multipartBody.field("reply_to_message_id", String.valueOf(replyingOptions.getReplyTo()), "application/json");
        if (replyingOptions.getReplyMarkup() != null) {

            switch (replyingOptions.getReplyMarkup().getType()) {

                case FORCE_REPLY:
                    multipartBody.field("reply_markup", GSON.toJson(replyingOptions.getReplyMarkup(), ForceReply.class), "application/json");
                    break;
                case KEYBOARD_HIDE:
                    multipartBody.field("reply_markup", GSON.toJson(replyingOptions.getReplyMarkup(), ReplyKeyboardHide.class), "application/json");
                    break;
                case KEYBOARD_MARKUP:
                    multipartBody.field("reply_markup", GSON.toJson(replyingOptions.getReplyMarkup(), ReplyKeyboardMarkup.class), "application/json");
                    break;
                case INLINE_KEYBOARD_MARKUP:
                    multipartBody.field("reply_markup", GSON.toJson(replyingOptions.getReplyMarkup(), InlineKeyboardMarkup.class), "application/json");
                    break;
            }
        }
    }

    private static void processNotificationContent(MultipartBody multipartBody, NotificationOptions notificationOptions) {

        multipartBody.field("disable_notification", notificationOptions.isDisableNotification());
    }

    private static boolean checkResponseStatus(JSONObject jsonResponse) {

        if (jsonResponse != null) {

            if (jsonResponse.getBoolean("ok")) {

                return true;
            } else {

                System.err.println("The API returned the following error: " + jsonResponse.getString("description"));
            }
        } else {

            System.err.println("JSON Response was null, something went wrong...");
        }

        return false;
    }
}

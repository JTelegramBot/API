package pro.zackpollard.telegrambot.api.event;

import pro.zackpollard.telegrambot.api.event.chat.*;
import pro.zackpollard.telegrambot.api.event.chat.game.GameInlineCallbackQueryReceivedEvent;
import pro.zackpollard.telegrambot.api.event.chat.game.GameMessageCallbackQueryReceivedEvent;
import pro.zackpollard.telegrambot.api.event.chat.inline.InlineCallbackQueryReceivedEvent;
import pro.zackpollard.telegrambot.api.event.chat.inline.InlineQueryReceivedEvent;
import pro.zackpollard.telegrambot.api.event.chat.inline.InlineResultChosenEvent;
import pro.zackpollard.telegrambot.api.event.chat.message.*;

/**
 * @author DarkSeraphim.
 */
public interface Listener {

    default void onAudioMessageReceived(AudioMessageReceivedEvent event) {
    }

    default void onContactMessageReceived(ContactMessageReceivedEvent event) {
    }

    default void onCommandMessageReceived(CommandMessageReceivedEvent event) {
    }

    default void onDocumentMessageReceived(DocumentMessageReceivedEvent event) {
    }

    default void onLocationMessageReceived(LocationMessageReceivedEvent event) {
    }

    default void onMessageReceived(MessageReceivedEvent event) {
    }

    default void onMessageEditReceived(MessageEditReceivedEvent event) {
    }

    default void onPhotoMessageReceived(PhotoMessageReceivedEvent event) {
    }

    default void onStickerMessageReceived(StickerMessageReceivedEvent event) {
    }

    default void onTextMessageReceived(TextMessageReceivedEvent event) {
    }

    default void onVideoMessageReceived(VideoMessageReceivedEvent event) {
    }

    default void onVideoNoteMessageReceived(VideoNoteMessageReceivedEvent event) {
    }

    default void onVoiceMessageReceived(VoiceMessageReceivedEvent event) {
    }

    default void onDeleteGroupChatPhoto(DeleteGroupChatPhotoEvent event) {
    }

    default void onGroupChatCreated(GroupChatCreatedEvent event) {
    }

    default void onChannelChatCreated(ChannelChatCreatedEvent event) {
    }

    default void onNewGroupChatPhoto(NewGroupChatPhotoEvent event) {
    }

    default void onNewGroupChatTitle(NewGroupChatTitleEvent event) {
    }

    default void onParticipantJoinGroupChat(ParticipantJoinGroupChatEvent event) {
    }

    default void onParticipantLeaveGroupChat(ParticipantLeaveGroupChatEvent event) {
    }

    default void onInlineQueryReceived(InlineQueryReceivedEvent event) {
    }

    default void onInlineResultChosen(InlineResultChosenEvent event) {
    }

    default void onCallbackQueryReceivedEvent(CallbackQueryReceivedEvent event) {
    }

    default void onMessageCallbackQueryReceivedEvent(MessageCallbackQueryReceivedEvent event) {
    }

    default void onInlineCallbackQueryReceivedEvent(InlineCallbackQueryReceivedEvent event) {
    }

    default void onGameMessageCallbackQueryReceivedEvent(GameMessageCallbackQueryReceivedEvent event) {
    }

    default void onGameInlineCallbackQueryReceivedEvent(GameInlineCallbackQueryReceivedEvent event) {
    }
}
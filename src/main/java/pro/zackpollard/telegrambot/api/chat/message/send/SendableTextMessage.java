package pro.zackpollard.telegrambot.api.chat.message.send;

import lombok.*;
import pro.zackpollard.telegrambot.api.chat.message.Message;
import pro.zackpollard.telegrambot.api.chat.message.ReplyMarkup;

/**
 * @author Zack Pollard
 */
@ToString
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SendableTextMessage implements SendableMessage, ReplyingOptions, NotificationOptions {

    @NonNull
    @Getter
    private final String message;
    @Getter
    private final long replyTo;
    @Getter
    private final boolean disableWebPagePreview;
    @Getter
    private final ReplyMarkup replyMarkup;
    @Getter
    private final ParseMode parseMode;
    @Getter
    private final boolean disableNotification;

    public static SendableTextMessageBuilder builder() {
        return new SendableTextMessageBuilder();
    }

    public static SendableTextMessageBuilder markdown(String text) {
        return builder().message(text).parseMode(ParseMode.MARKDOWN);
    }

    public static SendableTextMessageBuilder html(String text) {
        return builder().message(text).parseMode(ParseMode.HTML);
    }

    public static SendableTextMessageBuilder plain(String text) {
        return builder().message(text).parseMode(ParseMode.NONE);
    }

    @Override
    public MessageType getType() {
        return MessageType.TEXT;
    }

    @ToString
    public static class SendableTextMessageBuilder {

        private String message;
        private long replyTo;
        private boolean disableWebPagePreview;
        private ReplyMarkup replyMarkup;
        private ParseMode parseMode;
        private boolean disableNotification;

        SendableTextMessageBuilder() {
        }

        public SendableTextMessage.SendableTextMessageBuilder message(String message) {
            this.message = message;
            return this;
        }

        public SendableTextMessage.SendableTextMessageBuilder replyTo(Message replyTo) {
            this.replyTo = replyTo != null ? replyTo.getMessageId() : 0;
            return this;
        }

        public SendableTextMessage.SendableTextMessageBuilder replyTo(long replyTo) {
            this.replyTo = replyTo;
            return this;
        }

        public SendableTextMessage.SendableTextMessageBuilder disableWebPagePreview(boolean disableWebPagePreview) {
            this.disableWebPagePreview = disableWebPagePreview;
            return this;
        }

        public SendableTextMessage.SendableTextMessageBuilder replyMarkup(ReplyMarkup replyMarkup) {
            this.replyMarkup = replyMarkup;
            return this;
        }

        public SendableTextMessage.SendableTextMessageBuilder parseMode(ParseMode parseMode) {
            this.parseMode = parseMode;
            return this;
        }

        public SendableTextMessage.SendableTextMessageBuilder disableNotification(boolean disableNotification) {

            this.disableNotification = disableNotification;
            return this;
        }

        public SendableTextMessage build() {
            return new SendableTextMessage(message, replyTo, disableWebPagePreview, replyMarkup, parseMode, disableNotification);
        }
    }
}
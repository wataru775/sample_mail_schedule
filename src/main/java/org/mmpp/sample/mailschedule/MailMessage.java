package org.mmpp.sample.mailschedule;

import lombok.Data;

import java.util.Date;

/**
 * メールメッセージ
 */
@Data
public class MailMessage {
    /**
     * メール送信元
     */
    private String from;
    /**
     * メール送信先
     */
    private String to;
    /**
     * 件名
     */
    private String subject;
    /**
     * 送信日時
     */
    private Date sendDate;
    /**
     * メッセージID
     */
    private String messageID;
    /**
     * 返信メッセージID
     */
    private String inReplyTo;
}

package org.mmpp.sample.mailschedule;

import javax.mail.MessagingException;
import java.util.List;

public interface POP3MailService {
    /**
     * 接続ユーザID & パスワードを設定します
     * @param username ユーザーID
     * @param password パスワード
     */
    public void setUserPass(String username, String password);
    /**
     * メールサーバー設定を設定します
     * @param host サーバー名
     * @param port 接続ポート
     * @param ssl SSL認証の有無
     */
    public void setMailServerConfig(String host, int port, boolean ssl);

    /**
     * メールサーバへ接続します
     * @throws MessagingException
     */
    public void connect() throws MessagingException;

    /**
     * フォルダへ接続します
     * @param folderName 対象フォルダ名
     * @throws MessagingException
     */
    public void openFolder(String folderName) throws MessagingException;

    /**
     * フォルダを閉じます
     * @throws MessagingException
     */
    public void closeFolder() throws MessagingException;

    /**
     * フォルダ内のメッセージ件数を取得します
     * @return メッセージ件数
     * @throws MessagingException
     */
    public int getMessageCount() throws MessagingException;

    /**
     * 新着メッセージ件数を取得します
     * @return メッセージ件数
     * @throws MessagingException
     */
    public int getNewMessageCount() throws MessagingException;

    /**
     * メールサーバーから切断します
     * @throws MessagingException
     */
    public void disconnect() throws MessagingException;

    /**
     * メールメッセージ一覧を取得します
     * @return
     * @throws MessagingException
     */
    public List<MailMessage> findAll() throws MessagingException;

}

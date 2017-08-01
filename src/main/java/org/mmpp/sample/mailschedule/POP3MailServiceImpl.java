package org.mmpp.sample.mailschedule;

import com.sun.mail.pop3.POP3SSLStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class POP3MailServiceImpl implements POP3MailService {
    // ロガー
    private Logger logger = LoggerFactory.getLogger(POP3MailServiceImpl.class);

    private Session session = null;
    private Store store = null;
    private String username, password;
    private Folder folder;
    private String host;
    private int port;
    private boolean ssl;

    /**
     * 接続ユーザID & パスワードを設定します
     * @param username ユーザーID
     * @param password パスワード
     */
    @Override
    public void setUserPass(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * メールサーバー設定を設定します
     * @param host サーバー名
     * @param port 接続ポート
     * @param ssl SSL認証の有無
     */
    @Override
    public void setMailServerConfig(String host, int port, boolean ssl){
        this.host = host;
        this.port = port;
        this.ssl = ssl;
    }

    /**
     * メールサーバへ接続します
     * @throws MessagingException
     */
    @Override
    public void connect() throws MessagingException {

        String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

        Properties pop3Props = new Properties();

        if(this.ssl) {
            pop3Props.setProperty("mail.pop3.socketFactory.class", SSL_FACTORY);
            pop3Props.setProperty("mail.pop3.socketFactory.fallback", "false");
            pop3Props.setProperty("mail.pop3.port", "995");
            pop3Props.setProperty("mail.pop3.socketFactory.port", "995");
        }

        URLName url = new URLName("pop3", host, port, "", username, password);

        session = Session.getInstance(pop3Props, null);
        store = new POP3SSLStore(session, url);
        store.connect();
    }

    /**
     * フォルダへ接続します
     * @param folderName 対象フォルダ名
     * @throws MessagingException
     */
    @Override
    public void openFolder(String folderName) throws MessagingException {

        // Open the Folder
        folder = store.getDefaultFolder();

        folder = folder.getFolder(folderName);

        if (folder == null) {
            throw new MessagingException("Invalid folder");
        }

        folder.open(Folder.READ_ONLY);

        // try to open read/write and if that fails try read-only
//
//        try {
//
//            folder.open(Folder.READ_WRITE);
//
//        } catch (MessagingException ex) {
//
//            folder.open(Folder.READ_ONLY);
//
//        }
    }

    /**
     * フォルダを閉じます
     * @throws MessagingException
     */
    @Override
    public void closeFolder() throws MessagingException {
        folder.close(false);
    }
    /**
     * フォルダ内のメッセージ件数を取得します
     * @return メッセージ件数
     * @throws MessagingException
     */
    @Override
    public int getMessageCount() throws MessagingException {
        return folder.getMessageCount();
    }

    /**
     * 新着メッセージ件数を取得します
     * @return メッセージ件数
     * @throws MessagingException
     */
    @Override
    public int getNewMessageCount() throws MessagingException {
        return folder.getNewMessageCount();
    }

    /**
     * メールサーバーから切断します
     * @throws MessagingException
     */
    @Override
    public void disconnect() throws MessagingException {
        store.close();
    }

    /**
     * メールメッセージ一覧を取得します
     * @return
     * @throws MessagingException
     */
    @Override
    public List<MailMessage> findAll() throws MessagingException {

        List<MailMessage> mailMessages = new LinkedList<MailMessage>();
        // Attributes & Flags for all messages ..
        Message[] messages = folder.getMessages();

        // Use a suitable FetchProfile
        FetchProfile fetchProfile = new FetchProfile();
        fetchProfile.add(FetchProfile.Item.ENVELOPE);
        fetchProfile.add("X-Mailer");

        // TODO 調査 何故か重い
//        folder.fetch(messages, fetchProfile);

        for (Message message : messages) {
            try {
                mailMessages.add(castMessageToMailMessage(message));
            }catch (MessagingException e){
                // メッセージの読み取りエラー 次のメッセージへ読み取り継続
                logger.info("castMessageToMailMessage Exception : " + e.getMessage());
            }
        }

        return mailMessages;

    }

    /**
     * Mailサーバのメッセージ形式を内部のMailメッセージ形式に変換します
     * @param message メッセージ形式
     * @return MAilメッセージ形式
     * @throws MessagingException Mailサーバのメッセージの内容を読み取れない場合の例外
     */
    private MailMessage castMessageToMailMessage(Message message) throws MessagingException {
        MailMessage mailMessage = new MailMessage();
        // FROM
        mailMessage.setFrom(addressesToString(message.getFrom()));
        // TO
        mailMessage.setTo(addressesToString(message.getRecipients(Message.RecipientType.TO)));
        // SUBJECT
        mailMessage.setSubject(message.getSubject());
        // DATE
        mailMessage.setSendDate( message.getSentDate());
        // Message-ID
        mailMessage.setMessageID(message.getHeader("Message-ID")[0]);
        // In-Reply-To
        mailMessage.setMessageID(message.getHeader("In-Reply-To") != null ? message.getHeader("In-Reply-To")[0] : null);

        return mailMessage;
    }

    /**
     * FROMやTOのアドレス一覧をカンマくくりの文字列にします
     * @param addresses アドレス一覧
     * @return アドレス文字列
     */
    private String addressesToString(Address[] addresses) {
        StringBuffer bufToString = new StringBuffer();
        for(Address address : addresses){
            if(bufToString.length()!=0){
                bufToString.append(",");
            }
            bufToString.append(address.toString());
        }

        return bufToString.toString();
    }

}

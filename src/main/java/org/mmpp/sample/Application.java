package org.mmpp.sample;

import org.mmpp.sample.mailschedule.MailMessage;
import org.mmpp.sample.mailschedule.POP3MailService;
import org.mmpp.sample.mailschedule.POP3MailServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@PropertySource(value = "classpath:account.properties", encoding="UTF-8")
public class Application implements CommandLineRunner {
    // ロガー
    private Logger logger = LoggerFactory.getLogger(Application.class);
    @Autowired
    private Environment environment;
    /**
     * 実行
     * @param args 引数
     */
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Application.class);
        // 実行時のバナーをなくします
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }

    @Override
    public void run(String... strings) {
        POP3MailService mailService = new POP3MailServiceImpl();
        try {
            mailService.setUserPass(environment.getProperty("mail.user"),environment.getProperty("mail.password"));
            mailService.setMailServerConfig(environment.getProperty("mail.host"),Integer.parseInt(environment.getProperty("mail.port")),Boolean.parseBoolean(environment.getProperty("mail.ssl")));
            mailService.connect();
            mailService.openFolder("INBOX");

            int totalMessages = mailService.getMessageCount();
            int newMessages = mailService.getNewMessageCount();

            logger.info("Total messages = " + totalMessages);
            logger.info("New messages = " + newMessages);
            logger.info("-------------------------------");

            for(MailMessage mailMessage : mailService.findAll()){
                logger.info(mailMessage.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            try {
                mailService.closeFolder();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                mailService.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
package cn.silver.framework.message.service.impl;

import cn.silver.framework.message.constant.MessageStatus;
import cn.silver.framework.message.service.IMailService;
import cn.silver.framework.system.domain.SysMessage;
import cn.silver.framework.system.domain.SysMessageReceiver;
import cn.silver.framework.system.service.ISysMessageReceiverService;
import cn.silver.framework.system.service.ISysMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class MailServiceImpl implements IMailService {
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private ISysMessageService messageService;
    @Autowired
    private ISysMessageReceiverService receiverService;
    @Value("${spring.mail.username}")
    private String from;

    @Override
    public void send(SysMessage message) {
        log.info("发送邮件：{}", message.getSendContent());
        List<SysMessageReceiver> receivers = this.receiverService.selectByMain(message.getId());
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(message.getSenderAccount());
        mail.setTo(message.getReceiveAccount());
        mail.setSubject(message.getTitle());
        mail.setText(message.getSendContent());
        try {
            mailSender.send(mail);
            message.setSendStatus(MessageStatus.FINISHED.getCode());
        } catch (Exception e) {
            message.setRemark("消息发送失败," + e.getMessage());
            log.error(e.getMessage(), e);
            message.setSendStatus(MessageStatus.FAILD.getCode());
        } finally {
            message.setSendNum(message.getSendNum() + 1);
            this.messageService.update(message);
        }
    }

    public void sendSimpleMail() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setText("内容");
        message.setSubject("主题");
        message.setTo("收件人");
        message.setCc("抄送人");
        message.setBcc("密送人");
        mailSender.send(message);
    }

    public void sendHtmlMail() {
        MimeMessage mailMessage = mailSender.createMimeMessage();
        //需要借助Helper类
        MimeMessageHelper helper = new MimeMessageHelper(mailMessage);
        String context = "<b>尊敬的用户：</b><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;您好，管理员已为你申请了新的账号，" +
                "请您尽快通过<a href=\"http://www.liwz.top/\">链接</a>登录系统。"
                + "<br>修改密码并完善你的个人信息。<br><br><br><b>员工管理系统<br>Li，Wan Zhi</b>";
        try {
            helper.setFrom("发送人");
            helper.setTo("收件人");
            helper.setBcc("密送人");
            helper.setSubject("主题");
            helper.setSentDate(new Date());//发送时间
            helper.setText(context, true);
            mailSender.send(mailMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void sendMailWithFile() {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        // true表示构建一个可以带附件的邮件对象
        MimeMessageHelper helper = null;
        try {
            helper = new MimeMessageHelper(mimeMessage, true);
            helper.setSubject("这是一封测试邮件");
            helper.setFrom("97******9@qq.com");
            helper.setTo("10*****16@qq.com");
            helper.setSentDate(new Date());
            helper.setText("这是测试邮件的正文");
            // 第一个参数是自定义的名称，后缀需要加上，第二个参数是文件的位置
            helper.addAttachment("资料.xlsx", new File("/Users/gamedev/Desktop/测试数据 2.xlsx"));
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error(e.getMessage(), e);
        }

    }
}

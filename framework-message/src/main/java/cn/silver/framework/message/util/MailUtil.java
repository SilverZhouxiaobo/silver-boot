package cn.silver.framework.message.util;

import com.google.common.collect.Lists;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import javax.mail.search.SearchTerm;
import java.util.List;
import java.util.Properties;

/**
 * ClassName: MainUtil
 *
 * @author chenqc
 * @Description: 邮件工具类
 * @date 2015-9-24
 */
public class MailUtil {

	//SSL FACTORY全类名
	public final static String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

	/**
	 * @param @param  host pop服务地址
	 * @param @param  userName
	 * @param @param  password
	 * @param @return
	 *
	 * @return Mail
	 *
	 * @throws Exception
	 * @throws
	 * @Description: 使用pop3协议读邮件
	 * @author chenqc
	 * @date 2015-9-24
	 */
	public static List<Mail> readMailByPop3(String host, String userName, String password) throws Exception {
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);

		Store store = session.getStore("pop3");
		store.connect(host, -1, userName, password);
		Folder folder = store.getFolder("INBOX");
		folder.open(Folder.READ_ONLY);
		Message messages[] = folder.getMessages();

		ShowMail re = null;
		List<Mail> mails = Lists.newArrayList();

		for (int i = 0; i < messages.length; i++) {
			Mail m = new Mail();
			re = new ShowMail((MimeMessage) messages[i]);
			m.setMessageId(re.getMessageId());
			m.setSubject(re.getSubject());
			m.setIsNew(re.isNew());
			m.setSentDate(re.getSentDate());
			m.setReplySign(re.getReplySign());
			m.setIsContainAttach(re.isContainAttach((Part) messages[i]));
			m.setCc(re.getMailAddress("cc"));
			m.setBcc(re.getMailAddress("bcc"));
			m.setTo(re.getMailAddress("to"));
			m.setFrom(re.getFrom());
			re.getMailContent((Part) messages[i]);
			m.setBodyText(re.getBodyText());
			mails.add(m);
		}
		return mails;
	}

	public static List<Mail> readMailByPop3(String host, String userName, String password, SearchTerm searchTerm) throws Exception {
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);

		Store store = session.getStore("pop3");
		store.connect(host, -1, userName, password);
		Folder folder = store.getFolder("INBOX");
		folder.open(Folder.READ_ONLY);
		Message[] messages = folder.search(searchTerm);

		ShowMail re = null;
		List<Mail> mails = Lists.newArrayList();

		for (int i = 0; i < messages.length; i++) {
			Mail m = new Mail();
			re = new ShowMail((MimeMessage) messages[i]);
			m.setMessageId(re.getMessageId());
			m.setSubject(re.getSubject());
			m.setIsNew(re.isNew());
			m.setSentDate(re.getSentDate());
			m.setReplySign(re.getReplySign());
			m.setIsContainAttach(re.isContainAttach((Part) messages[i]));
			m.setCc(re.getMailAddress("cc"));
			m.setBcc(re.getMailAddress("bcc"));
			m.setTo(re.getMailAddress("to"));
			m.setFrom(re.getFrom());
			re.getMailContent((Part) messages[i]);
			m.setBodyText(re.getBodyText());
			mails.add(m);


		}
		return mails;
	}

	/**
	 * @param @param  smtp
	 * @param @param  from
	 * @param @param  to
	 * @param @param  subject
	 * @param @param  content
	 * @param @param  username
	 * @param @param  password
	 * @param @return
	 *
	 * @return boolean
	 *
	 * @throws
	 * @Description: 发送邮件
	 * @author chenqc
	 * @date 2015-10-13
	 */
	public static boolean sendMail(String smtp, String from, String to, String subject, String content, String username, String password) {
		return SentMail.send(smtp, from, to, subject, content, username, password);
	}


	/**
	 * @param @param  smtp
	 * @param @param  from
	 * @param @param  to
	 * @param @param  copyto
	 * @param @param  subject
	 * @param @param  content
	 * @param @param  username
	 * @param @param  password
	 * @param @return
	 *
	 * @return boolean
	 *
	 * @throws
	 * @Description: 发送邮件, 有抄送参数
	 * @author chenqc
	 * @date 2015-10-13
	 */
	public static boolean sendAndCc(String smtp, String from, String to, String copyto, String subject, String content, String username, String password) {
		return SentMail.sendAndCc(smtp, from, to, copyto, subject, content, username, password);
	}

	/**
	 * @param @param  smtp
	 * @param @param  from
	 * @param @param  to
	 * @param @param  copyto
	 * @param @param  subject
	 * @param @param  content
	 * @param @param  username
	 * @param @param  password
	 * @param @param  filename
	 * @param @return
	 *
	 * @return boolean
	 *
	 * @throws
	 * @Description: 发送邮件, 有抄送, 附件路径参数
	 * @author chenqc
	 * @date 2015-10-13
	 */
	public static boolean sendAndCcAndFile(String smtp, String from, String to, String copyto, String subject, String content, String username, String password, String filename) {
		return SentMail.sendAndCc(smtp, from, to, copyto, subject, content, username, password, filename);
	}


	public static void main(String[] args) {
		/** 测试邮件发送 */
		String smtp = "smtp.163.com";
		String from = "fkjava8888@163.com";
		String to = "279256487@qq.com";
		String subject = "邮件主题";
		String content = "<a href='http://baidu.com'>baidu</a>";
		String username = "fkjava8888@163.com";
		String password = "fkjava888";
		boolean b = MailUtil.sendMail(smtp, from, to, subject, content, username, password);
		;
		System.out.println(b);


	}
}

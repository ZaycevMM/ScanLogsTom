package ru.krista;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.simplejavamail.email.Email;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.Mailer;
import org.simplejavamail.mailer.config.ServerConfig;
import org.simplejavamail.mailer.config.TransportStrategy;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import ru.krista.config.MailConfig;
import ru.krista.utility.CheckingFile;
import ru.krista.utility.PropertiesUtil;


public class Application {
	
	
	private static final Logger LOGGING=Logger.getLogger("ERRORS");
	public static void main(String[] args) {
		
		//System.getProperties().put("http.proxyHost", "10.0.0.1");
		//System.getProperties().put("http.proxyPort", "8080");
		//System.getProperties().put("http.proxyUser", "antivir");
		//System.getProperties().put("http.proxyPassword", "antivir");
		
		//for mail
		Map<String, Integer> mailInfo = new HashMap<String, Integer>();
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		//get logs files
		File[] scanningFiles = new File(System.getenv("CATALINA_HOME")+"/logs").listFiles();
		//sorted files log
		List<File> sortedFiles = new LinkedList<File>();
		for (File file : scanningFiles) {
			if (file.getName().contains("budget-service.log") && 
					Integer.parseInt(dateFormat.format(new Date(file.lastModified()))) > Integer.parseInt(PropertiesUtil.getProperties().getProperty("date")) &&
					Integer.parseInt(dateFormat.format(new Date(file.lastModified()))) != Integer.parseInt(dateFormat.format(new Date()))) {
				sortedFiles.add(file);
			}
		}
		//check
		for (File file : sortedFiles) {
			if (CheckingFile.getCheck(file)) {
				try {
			         FileReader reader = new FileReader(file);
			         BufferedReader in = new BufferedReader(reader);
			         String checkedString;
			         int value = 0;
			         while ((checkedString = in.readLine()) != null) {
			        	 if (checkedString.toLowerCase().contains(PropertiesUtil.getProperties().getProperty("text").toLowerCase())) {
			        		 mailInfo.put(file.getName(), value+=1);
			        		 LOGGING.info(file.getName() + " contains: " + checkedString);
			           }
			         }
			         in.close();
			       } catch (IOException e) {
			         e.printStackTrace();
			       } finally {
			    	   CheckingFile.addCheck(file.getName());
				}
			}
		}
		
		//send mail
		if (mailInfo.size() == 0 ) {
			//System.exit(0);
		}
		
		String text="";
		for (Map.Entry entry : mailInfo.entrySet()) {
		    text =text + "Файл:" + entry.getKey() + " Ошибок: "
		        + entry.getValue()+ "\n";
		}
		//Email settings
		//SimpleMailMessage message = new SimpleMailMessage();
 		//message.setFrom("zaycev@krista-omsk.ru");
		//message.setTo(PropertiesUtil.getProperties().getProperty("toSend"));
		//message.setSubject("ORA-00001: нарушено ограничение уникальности");
        //message.setText(text);
        //javaMailSender.send(message);
        //Email email = new EmailBuilder()
        	    //.from("LOGS", PropertiesUtil.getProperties().getProperty("toSend"))
        	    //.to("LOGS", PropertiesUtil.getProperties().getProperty("toSend"))
        	    //.subject("ORA-00001: нарушено ограничение уникальности")
        	    //.text(text)
        	    //.build();

        	//new Mailer(new ServerConfig("smtp.yandex.ru", 465, "zaycev@krista-omsk.ru", "password"),
        	//		TransportStrategy.SMTP_SSL).sendMail(email);
		
		 try{
				String idTelegram = PropertiesUtil.getProperties().getProperty("id-telegram");
			 	String strUrl = "https://api.telegram.org/bot233399109:AAFlzy8FxHuFkxrW99ujsOtxOpDC9SZwN90/sendMessage?chat_id="+ idTelegram +"&text="
						+ URLEncoder.encode(text, "UTF-8");
				System.setProperty("https.proxyHost", "10.0.0.1");
		        System.setProperty("https.proxyPort", "8080");
		        System.setProperty("http.proxyHost", "10.0.0.1");
		        System.setProperty("http.proxyPort", "8080");
		        
		        Authenticator.setDefault(new ProxyAuthenticator("antivir", "antivir"));
				URL url = new URL(strUrl);
		        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.0.0.1", 8080)); 
		        HttpURLConnection urlConn = (HttpURLConnection)url.openConnection(proxy);
		        String encoded = new String(Base64.getEncoder().encodeToString(("antivir" + ":" + "antivir").getBytes()));

		        urlConn.setRequestProperty("Proxy-Authorization", "Basic " + encoded.getBytes());
		        urlConn.setDoOutput(true);
		        urlConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
				urlConn.connect();
				    
		        boolean redirect = false;

				// normally, 3xx is redirect
				int status = urlConn.getResponseCode();
				if (status != HttpURLConnection.HTTP_OK) {
					if (status == HttpURLConnection.HTTP_MOVED_TEMP
						|| status == HttpURLConnection.HTTP_MOVED_PERM
							|| status == HttpURLConnection.HTTP_SEE_OTHER)
					redirect = true;
				}
				if (redirect) {
					// get redirect url from "location" header field
					String newUrl = urlConn.getHeaderField("Location");

					// open the new connnection again
					urlConn = (HttpURLConnection) new URL(newUrl).openConnection();
					urlConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
					urlConn.setRequestProperty("Proxy-Authorization", "Basic " + encoded.getBytes());
					//System.out.println("Redirect to URL : " + newUrl);
				}
			    } catch (IOException e) {
			        //System.err.println("Error creating HTTP connection");
			        e.printStackTrace();
			    }
	}
}

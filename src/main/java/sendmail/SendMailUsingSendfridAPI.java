package sendmail;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Attachments;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import org.bouncycastle.util.encoders.Base64;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;

public class SendMailUsingSendfridAPI implements Runnable{

    private String fileName;
    private String toEmail;  //ionel.condor@gmail.com, ionel_condor@yahoo.com,

    public SendMailUsingSendfridAPI(String fileName, String toEmail) {
        this.fileName = fileName;
        this.toEmail = toEmail;
    }

    public void sendEmail() {
         Thread t  = new Thread(this);
        t.start();
    }

    @Override
    public void run() {


        //auth
        SendGrid sg = new SendGrid(System.getenv("SENDGRID_API_KEY"));

        //set subject and content
        String subject = " News report attached here ";
        String body = " Dear user, pls find attached the report for the news you requested";

        Email from = new Email ();
        from.setEmail("ionel.condor@fasttrackit.org");
        from.setName("App 14 Febr");

        //preparing the list of emails , from a string put them in an array and put the array in a Personalization object
        String[] arrayListOfEmails = toEmail.split(",");

        Personalization personalization = new Personalization();
        for(String token: arrayListOfEmails) {
            personalization.addTo(new Email(token));
        }


        // build the mail object
        Mail mail = new Mail();
        mail.addPersonalization(personalization);
        mail.setFrom(from);
        mail.setSubject(subject);

        Content content = new Content();
        content.setType("text/html");
        content.setValue("<p>"+body+"</p");

        mail.addContent(content);

        Attachments attachment = new Attachments();
        attachment.setFilename(fileName);
        attachment.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        attachment.setDisposition("attachment");


        byte[] allBytes = new byte[0];
        try {
            allBytes = Files.readAllBytes(Paths.get(fileName));
        } catch (IOException e) {
            e.printStackTrace();


        }
        byte [] allBytesEncoded = Base64.encode(allBytes);
        attachment.setContent(new String (allBytesEncoded));



        mail.addAttachments(attachment);

        mail.addCategory("news analysis");


        Response response = null;
        try {
            Request request = new Request();

            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            response = sg.api(request);
        } catch (IOException e) {
            e.printStackTrace();

        }

        System.out.println(response.getStatusCode());
        System.out.println(response.getHeaders());
        System.out.println(response.getBody());






    }
//    public static void main(String[] args) {
//        SendMailUsingSendfridAPI sm  = new SendMailUsingSendfridAPI("analizaStire1676397803734.txt", "ionel.condor@gmail.com");
//        sm.sendEmail();
//
//    }
}

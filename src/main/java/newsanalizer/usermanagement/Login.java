package newsanalizer.usermanagement;

import sendmail.SendMailUsingSendfridAPI;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Login {

    private final int MAXTRIES=3;
    private final int WAITTIME=3;

    private static List<User> listUsers;
    public void doLogin() {

        // load from db the list of users
        listUsers = loadDB();
//        for(User u: listUsers)
//            System.out.println(u);

        // read from kb a user
        //while user from kb ! = a user in db stay here


        int counterTries = 0;
        boolean succes = false;
        boolean isAdmin = false;
        String email=null;
        do {

            if(counterTries==MAXTRIES ) { // hardcoded
                System.out.println("..... sorry.... waiting 3 sec ...");
                try {
                    TimeUnit.SECONDS.sleep(WAITTIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                counterTries=0;
            }

            System.out.println("username:");
            String kbUsername = new Scanner(System.in).nextLine();
            System.out.println("pwd:");
            String kbPwd = new Scanner(System.in).nextLine();

            User userKb = new User();
            userKb.setUsername(kbUsername);
            userKb.setPassword(kbPwd);
            counterTries++;
            for(User u: listUsers) { // de cate randuri am in fisier

                if(u.equalsUsers(userKb))
                {
                    System.out.println(u);
                    System.out.println("ok, you are logged in now");
                    succes=true;
                    email=u.getUsername();
                    if(u.isAdmin()) {
                        isAdmin=true;
                    }
                }

            }



        }
        while(!succes);

        System.out.println("gone");

        if(isAdmin)
            printAdminMenu();
        else
            printUserMenu(email);



    }

    private List<User> loadDB() {

        Path path= Paths.get("users.txt");
        List<User> listOfUsers = new ArrayList<>();

        List<String> listOfUsersAsStrings= null;
        try {
            listOfUsersAsStrings = Files.readAllLines(path);
        } catch (IOException e) {
          e.printStackTrace();
        }
        System.out.println(listOfUsersAsStrings);

            for(int i = 0; i<listOfUsersAsStrings.size();i++) {
                User uObj = new User();
                String currentLineOfText = listOfUsersAsStrings.get(i);
                StringTokenizer st = new StringTokenizer(currentLineOfText, ",");
                while(st.hasMoreTokens()) {
                    String u = st.nextToken();
                    String p = st.nextToken();
                    String admin = st.nextToken().trim();


                    uObj.setUsername(u.trim());
                    uObj.setPassword(p.trim());

                    if(admin.equalsIgnoreCase("true"))
                        uObj.setAdmin(true);


                }
                System.out.println(uObj);
                listOfUsers.add(uObj);
            }




        return listOfUsers;
    }

    private static void   printAdminMenu() {
        System.out.println("0. Add user ");

        String option = new Scanner(System.in).nextLine();
 if(option.equalsIgnoreCase("0")) {
     System.out.println("new username:");
     String kbUsername = new Scanner(System.in).nextLine();
     System.out.println("new pwd:");
     String kbPwd = new Scanner(System.in).nextLine();

     User userKb = new User();
     userKb.setUsername(kbUsername);
     userKb.setPassword(kbPwd);
     listUsers.add(userKb);
     String newRow = "\n"+kbUsername +","+kbPwd+", false";
     Path pOut = Paths.get("users.txt");
     try {
         Files.write(pOut, newRow.getBytes(), StandardOpenOption.APPEND);
     } catch (IOException e) {
         throw new RuntimeException(e);
     }
 }

    }

    private static void   printUserMenu(String email ) {
        System.out.println("1. Analiza stiri , incarcari fisier ");
        String option = new Scanner(System.in).nextLine();
        if(option.equalsIgnoreCase("1")) {
            System.out.println("nume fisier de stiri:");
            String filename = new Scanner(System.in).nextLine();

            // algoritm de parsare stiri si restul
            analyzeNews(filename, email);

        }

    }

    public static void   analyzeNews(String filename, String email ) {


        String news=null;
        Path p = Paths.get(filename);
        try {
            news = new String(Files.readAllBytes(p));
        } catch (IOException e) {
            e.printStackTrace();
        }

        StringTokenizer st = new StringTokenizer(news,"~");
        String currentNews;
        while(st.hasMoreTokens()) {


            currentNews = st.nextToken().toLowerCase().trim();

          //  System.out.println(currentNews);

            // now we do what adi did
            Map<String, Integer> report = parseNews(currentNews);

            // afisam si trimitem pe mail fisierul , dar intai construim fisierul pe disc

            String fileName ="analizaStire"+ System.currentTimeMillis()+".txt";
            Path pout = Paths.get(fileName);


             StringBuffer row = new StringBuffer();
            for(Map.Entry<String, Integer> value: report.entrySet()) {
                //System.out.println(value.getKey()+" : " +value.getValue());
                row.append(value.getKey());
                row.append(":");
                row.append(value.getValue());
                row.append("\n");


            }

            try {
                Files.write(pout, row.toString().getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // sendemail
            SendMailUsingSendfridAPI sm  = new SendMailUsingSendfridAPI(fileName, email);

            sm.sendEmail(); // blocheaza pana se executa , nu trece mai departe


            System.out.println("...am trimis mail, trec la urm  news .... ");


        }


    }

    private static  Map parseNews(String currentNews) {
        Map<String, Integer> report = new HashMap<>();
        //

        StringTokenizer stCurrentNews= new StringTokenizer(currentNews);
        int nrWordsPerCurrentnews=0;
        while (stCurrentNews.hasMoreTokens()) {
            String tokenNews=stCurrentNews.nextToken();

            int index = tokenNews.lastIndexOf(".");
            if(index !=-1)
                tokenNews = tokenNews.substring(0,index);

             index = tokenNews.lastIndexOf(",");
            if(index !=-1)
                tokenNews = tokenNews.substring(0,index);

            if(tokenNews.length()>4) {
                if (!report.containsKey(tokenNews))
                    report.put(tokenNews, 1);
                else
                    report.put(tokenNews, report.get(tokenNews) + 1);
            }
        }

        return report;
    }


}

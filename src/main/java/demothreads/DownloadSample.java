package demothreads;

public class DownloadSample {

    public static void main(String[] args) {

        FileDownload file1 = new FileDownload("xdfhjghjhdfghghkgh ");
        FileDownload file2 = new FileDownload("googleeerereeeeeee ");
        FileDownload file3 = new FileDownload("xdfhjghjhdfghghkgh ");
        FileDownload file4 = new FileDownload("googleeerereeeeeee ");

        int nr = Runtime.getRuntime().availableProcessors();
        System.out.println(nr);

        file1.start();
        file2.start();
        file3.start();
        file4.start();

        System.out.println("acum pot face alte chestii ");

    }
}

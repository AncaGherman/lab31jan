package demothreads;

import java.util.Random;

public class FileDownload extends Thread
{
    private String fileName;

    public FileDownload(String fileName) {
        this.fileName=fileName;
    }

    @Override
    public void run(){
        System.out.println("download "+fileName);

        int downloadTime = new Random().nextInt(3000)+1000;
        try {
            Thread.sleep(downloadTime);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("a durat:" +downloadTime);
    }
}

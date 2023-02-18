package demothreads;



     class Message {
        private String content;
        private boolean empty = true;

        public synchronized String read() {
            while (empty) {
                try {
                    wait();
                } catch (InterruptedException e) {}
            }
            empty = true;
            notifyAll();
            System.out.println("i am called to read:"+content);
            return content;
        }

        public synchronized void write(String message) {

            while (!empty) {
                try {
                    wait();
                } catch (InterruptedException e) {}
            }
            empty = false;
            this.content = message;
            System.out.println("i am called to write:"+message);
            notifyAll();
        }
    }

     class ReaderThread extends Thread {
        private Message message;

        public ReaderThread(Message message) {
            this.message = message;
        }

        public void run() {
            String msg = null;
            while ((msg = message.read()) != null) {
            }
        }
    }

     class WriterThread extends Thread {
        private Message message;
        private String[] messages = {
                "first one ",
                "second one",
                "third one"
        };

        public WriterThread(Message message) {
            this.message = message;
        }

        public void run() {
            for (String currentMsg : messages) {
                message.write(currentMsg);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {}
            }
            message.write(null);
        }
    }

    public class DemoNotifyWait {
        public static void main(String[] args) {
            Message message = new Message();
            ReaderThread reader = new ReaderThread(message);
            WriterThread writer = new WriterThread(message);

            reader.start();
            writer.start();

        }
    }


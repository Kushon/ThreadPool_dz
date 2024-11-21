package org.threadpool;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import com.beust.jcommander.JCommander;

public class Main {
    public static void main(String[] argv)  {
        Args args = new Args();
        JCommander.newBuilder().addObject(args).build().parse(argv);

        List<String> urls = getUrls(args.files);

        if(args.mode.equals("multi-thread")){

            try {
                var startTime = System.currentTimeMillis();

                MultiThreading(urls, args.folder, args.count);

                var endTime = System.currentTimeMillis();

                System.out.println(endTime - startTime + " ms");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        } else if (args.mode.equals("one-thread")) {
            try {
                var startTime = System.currentTimeMillis();

                MultiThreading(urls, args.folder, 1);

                var endTime = System.currentTimeMillis();

                System.out.println(endTime - startTime + " ms");

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
    }


    public static void MultiThreading(List<String> urls, String FILE_NAME, int nThread) throws InterruptedException {


        ExecutorService executorService = Executors.newFixedThreadPool(nThread);
        List<Future<String>> futures = new ArrayList<>();
        int i = 1;
        for (String url : urls) {
            DownloadPicTask downloadTask = new DownloadPicTask(url, FILE_NAME, i);
            futures.add(executorService.submit(downloadTask));
            i++;
        }

        for (var f : futures) {
            while (!f.isDone()) {
                Thread.sleep(100);
            }
        }

        executorService.shutdown();

    }

    public static class DownloadPicTask implements Callable<String> {
        private String url_file;
        private String file_name;
        private int index;

        public DownloadPicTask(String url_file, String file_name, int index ) {
            this.url_file = url_file;
            this.index = index;
            this.file_name = file_name;
        }

        @Override
        public String call() throws Exception {
            pictureDownloading(url_file, file_name, index);
            System.out.println("Файл загрузился в " + file_name +"\\" + index);
            return "Файл загрузился в " + file_name +"\\" + index;

        }
    }
    public static void pictureDownloading(String FILE_URL, String FILE_NAME, int index) throws IOException {
        InputStream in = new URL(FILE_URL).openStream();
        Files.copy(in, Paths.get(FILE_NAME  +"\\"+ index + ".png"), StandardCopyOption.REPLACE_EXISTING);
    }
    public static List<String> getUrls(String EnteredUrls){
        List<String> urlsList = new ArrayList<>();
        String[] urlsArray = EnteredUrls.split(";");
        for (String url : urlsArray) {
            urlsList.add(url);
        }
        return urlsList;

    }
}

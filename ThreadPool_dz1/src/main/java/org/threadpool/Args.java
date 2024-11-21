package org.threadpool;

import com.beust.jcommander.Parameter;

public class Args {
    @Parameter (names = "--mode")
    public String mode;
    @Parameter (names = "--count")
    public int count ;
    @Parameter (names = "--files")
    public String files;
    @Parameter (names = "--folder")
    public String folder;

}
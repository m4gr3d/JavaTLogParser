package com.o3dr.tlog;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;

import com.MAVLink.Messages.MAVLinkMessage;

public class Main {
    private static String[] inputArgs;
    private static final Date date = new Date();

    public static void main(String[] args) {
        inputArgs = args;
        parseTLog();
    }

    private static void parseTLog() {
        if (inputArgs.length < 1) {
            System.out.println("Usage: java -jar TLogParser <input_file> [<input_file_2> ... <input_file_n>]");
            return;
        }

        for(String inputFile : inputArgs) {

            System.out.println("Parsing input file: " + inputFile);
            try {
                URI uri = new URI(inputFile);
                TLogParser.getAllEventsAsync(uri, new TLogParser.TLogParserFilter() {

                    @Override
                    public boolean addEventToList(TLogParser.Event event) {
                        // add your own filter
                        // return 180 == event.getMavLinkMessage().msgid;

                        return true;
                    }

                    @Override
                    public boolean continueIterating() {
                        return true;
                    }

                }, new TLogParser.TLogParserCallback() {
                    @Override
                    public void onResult(List<TLogParser.Event> eventList) {
                        System.out.println("Success " + (eventList == null ? 0 : eventList.size()));
                        for (TLogParser.Event event : eventList) {
                            // msg_heartbeat heartbeat = (msg_heartbeat)event.getMavLinkMessage();
                            long timestamp = event.getTimestamp();
                            MAVLinkMessage mavlinkMsg = event.getMavLinkMessage();
                            date.setTime(timestamp);
                            System.out.println("event time " + date.toGMTString() + " id:" + mavlinkMsg.msgid + " " + timestamp + " " + mavlinkMsg);
                        }

                        System.out.println();
                    }

                    @Override
                    public void onFailed(Exception e) {
                        System.out.println("Failed to get message " + e);
                        System.out.println();
                    }
                });
            } catch (URISyntaxException e) {
                System.out.println("Failed to get uri from tlog path");
                System.out.println();
            }
        }
    }
}

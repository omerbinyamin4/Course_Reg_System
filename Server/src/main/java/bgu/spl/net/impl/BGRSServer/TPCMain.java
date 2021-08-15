package bgu.spl.net.impl.BGRSServer;

import bgu.spl.net.srv.Server;

public class TPCMain {
    public static void main (String [] args) {
        if (args.length != 0) {
            if (!Database.getInstance().initialize("./Courses.txt")) System.out.println("No Course file found");
            else {
                Server.threadPerClient(
                        Integer.parseInt(args[0]), //port
                        () -> new BGRSProtocol(), //protocol factory
                        () -> new BGRSEncoderDecoder() //message encoder decoder factory
                ).serve();
            }
        }
        else{
            System.out.print("no arguments");
        }
    }
}

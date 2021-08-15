package bgu.spl.net.impl.BGRSServer;

import bgu.spl.net.srv.Reactor;

public class ReactorMain {
    public static void main (String [] args){
        if (args.length == 2) {
            Reactor<Message> server = new Reactor<>(
                    Integer.parseInt(args[1]),
                    Integer.parseInt(args[0]),
                    () -> new BGRSProtocol(),
                    () -> new BGRSEncoderDecoder());
            if (Database.getInstance().initialize("./Courses.txt")) server.serve();
            else System.out.println("No Course file found");
        }
        else{
            System.out.print("no arguments");
        }
    }
}

package com.test;

import akka.actor.*;
import scala.concurrent.duration.Duration;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public class HelloAkka {
    public static class Message implements Serializable{
        String message;

        Message(String message){
            this.message=message;
        }
    }

    public static class MessageRequest implements Serializable {

    }

    public static class MessageResponse implements Serializable{
        String message;

        MessageResponse(String message){
            this.message=message;
        }
    }

    public static class MessageActor extends UntypedActor {
        String state;

        @Override
        public void onReceive(Object message) {
            if (message instanceof MessageRequest)
                getSender().tell(new MessageResponse(state),getSelf());
            else if (message instanceof Message)
                state=((Message) message).message;
            else
                unhandled(message);
        }
    }

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("akkatest");
        ActorRef actor = system.actorOf(Props.create(MessageActor.class), "actor");
        Inbox inbox = Inbox.create(system);

        actor.tell(new Message("first"), ActorRef.noSender());
        inbox.send(actor,new MessageRequest());
        System.out.println(((MessageResponse)inbox.receive(Duration.create(0, TimeUnit.SECONDS))).message);

        actor.tell(new Message("second"), ActorRef.noSender());
        inbox.send(actor,new MessageRequest());
        System.out.println(((MessageResponse)inbox.receive(Duration.create(0, TimeUnit.SECONDS))).message);

        system.shutdown();
    }
}

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.*;

/**
 * Created by Stepan on 12.01.16.
 */
public class MyAgentTask2 extends Agent {
    private double oldX;
    private double newX;
    static Random random = new Random();

    public static final Map<String, List<String>> graph = Collections.unmodifiableMap(new HashMap<String, List<String>>() {
        {
            put("a1", Collections.unmodifiableList(Arrays.asList("a3", "a6")));
            put("a2", Collections.unmodifiableList(Arrays.asList("a1", "a4")));
            put("a3", Collections.unmodifiableList(Arrays.asList("a2", "a4")));
            put("a4", Collections.unmodifiableList(Arrays.asList("a2", "a5")));
            put("a5", Collections.unmodifiableList(Arrays.asList("a3", "a6")));
            put("a6", Collections.unmodifiableList(Arrays.asList("a1", "a5")));
        }
    });
    public static final Map<String, Double> values = Collections.unmodifiableMap(new HashMap<String, Double>() {
        {
            put("a1", 4.0);
            put("a2", 2.0);
            put("a3", 6.0);
            put("a4", 5.0);
            put("a5", 7.0);
            put("a6", 9.0);
        }
    });

    private List<String> edges;

    @Override
    protected void setup() {
        newX = values.get(getLocalName());
        edges = graph.get(getLocalName());
        oldX = newX;

        addBehaviour(new MyBehavior());
        System.out.println(getLocalName() + " initialized value =  " + newX);
    }

    public class MyBehavior extends CyclicBehaviour {

        private int phase = 0;
        private int time = 0;
        private double y = 0;
        private double sum = 0;

        public MyBehavior() {
            super(MyAgentTask2.this);
        }

        @Override
        public void action() {

            ACLMessage msg = receive();
            if (msg == null) {
                return;
            }

            switch (phase) {
                case 0:
                    for (String to : edges) {
                        if (!b(time)) {
                            continue;
                        }
                        double curX = isDelay(time) ? oldX : newX;
                        double y = curX + w(time);
                        sendMyMessage(y, to);
                    }
                    y = newX + w(time);
                    phase = 1;
                    break;

                case 1:
                    if (msg.getPerformative() == ACLMessage.PROPOSE) {
                        oldX = newX;
                        newX += a(time) * sum;
                        System.out.println(getLocalName() + " : " + newX);
                        time++;
                        sum = 0;
                        phase = 2;
                        return;
                    } else {
                        update(msg);
                    }

                case 2:
                    if (msg.getPerformative() == ACLMessage.PROPOSE) {
                        phase = 0;
                    } else {
                        update(msg);
                    }
            }

        }

        private void update(ACLMessage msg) {
            double yij = Double.parseDouble(msg.getContent());
            sum += yij - y;

            if (done()) {
                System.out.println(getLocalName() + " answer is " + newX);
                doDelete();
            }
        }

        private void sendMyMessage(double value, String... to) {
            ACLMessage newMessage = new ACLMessage(ACLMessage.INFORM);
            newMessage.setContent("" + value);
            for (String t : to) {
                newMessage.addReceiver(new AID(t, AID.ISLOCALNAME));
            }
            send(newMessage);
        }
    }


    private static double a(int t) {
        if (t % 2 == 0) {
            return 0.02;
        } else {
            return 0.01;
        }
    }

    private static double w(int t) {
        if (t % 2 == 0) {
            return (Math.random() * 2 - 1) * 0.75;
        } else {
            return (Math.random() * 2 - 1) * 0.5;
        }
    }

    private static boolean b(int t) {

        return t == 0 || random.nextInt(t) > t / 5;

    }

    private static boolean isDelay(int t) {
        return t == 0 || random.nextInt(t) < t / 6;
    }

}

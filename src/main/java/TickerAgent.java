import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.*;

/**
 * Created by Stepan on 12.01.16.
 */
public class TickerAgent extends Agent {
    //start with: -agents a0:TickerAgent;a1:MyAgentTask2;a2:MyAgentTask2;a3:MyAgentTask2;a4:MyAgentTask2;a5:MyAgentTask2;a6:MyAgentTask2

    private List<String> links;
    public static final int tick = 200;
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

    @Override
    protected void setup() {
        links = new ArrayList<>(graph.keySet());

        addBehaviour(new MyBehavior());
        System.out.println(getLocalName() + " initialized");
    }

    public class MyBehavior extends TickerBehaviour {

        public MyBehavior() {
            super(TickerAgent.this, tick);
        }

        @Override
        protected void onTick() {
            ACLMessage newMessage = new ACLMessage(ACLMessage.PROPOSE);
            newMessage.setContent("");
            for (String t : links) {
                newMessage.addReceiver(new AID(t, AID.ISLOCALNAME));
            }
            send(newMessage);
        }
    }

}

package task1;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.*;

/**
 * Created by Stepan on 22.09.15.
 */
@SuppressWarnings("all")
public class MyAgentTask1 extends Agent {
    //-agents 0:MyAgentTask1;1:MyAgentTask1;2:MyAgentTask1;3:MyAgentTask1;4:MyAgentTask1;5:MyAgentTask1

    private static class Graph {
        private final int V;
        private int E;
        private List<Integer>[] adj;

        public Graph(int V) {
            this.V = V;
            this.E = 0;
            adj = new ArrayList[V];
            for (int i = 0; i < V; i++) {
                adj[i] = new ArrayList<>();
            }
        }

        public void addEdge(int v, int w) {
            adj[v].add(w);
            adj[w].add(v);
            E++;
        }

        public List<Integer> adj(int v) {
            return adj[v];
        }

        @Override
        public String toString() {
            return "graph{" +
                    "V=" + V +
                    ", E=" + E +
                    ", adj=" + Arrays.toString(adj) +
                    '}';
        }
    }


    private Map<String, Integer> information = new HashMap<>();

    static final Graph myGraph = new Graph(6);

    static {
        myGraph.addEdge(0, 1);
        myGraph.addEdge(1, 2);
        myGraph.addEdge(2, 3);
        myGraph.addEdge(3, 0);
        myGraph.addEdge(2, 4);
        myGraph.addEdge(4, 5);
        myGraph.addEdge(5, 1);

        System.out.println("Сommunication of agents: " + myGraph);
    }

    protected void setup() {
        Random random = new Random();
        Integer next = Math.abs(random.nextInt() % 10);
        System.out.println("Agent number " + this.getLocalName() + " generated " + next);
        information.put(this.getLocalName(), next);
        addBehaviour(new MyBehaviour(this));
    }

    class MyBehaviour extends SimpleBehaviour {

        public MyBehaviour(Agent a) {
            super(a);
        }

        public void action() {
            ACLMessage message = receive();
            if (message != null) {
                Map<String, Integer> newData = getMapFromString(message.getContent());
                for (Map.Entry<String, Integer> entry : newData.entrySet()) {
                    information.put(entry.getKey(), entry.getValue());
                }
            }

            if (6 == information.size()) {
                double result = information.values().stream().mapToDouble(i -> i / 6.0).sum();
                System.out.println("Agent number " + myAgent.getLocalName() + " return the result:" + result);
                myAgent.doDelete();
            }

            List<Integer> to = myGraph.adj(Integer.valueOf(myAgent.getLocalName()));

            message = new ACLMessage(ACLMessage.INFORM);
            message.setContent(getStringFromMap(information));

            for (Integer agentId : to) {
                AID id = new AID(String.valueOf(agentId), AID.ISLOCALNAME);
                message.addReceiver(id);
            }

            send(message);
        }

        public boolean done() {
            return false;
        }

    }

    public static String getStringFromMap(Map<String, Integer> map) {
        OutputStream data = new ByteArrayOutputStream();
        XMLEncoder xmlEncoder = new XMLEncoder(data);
        xmlEncoder.writeObject(map);
        xmlEncoder.flush();
        return data.toString();
    }

    public static Map<String, Integer> getMapFromString(String map) {
        XMLDecoder xmlDecoder = new XMLDecoder(new ByteArrayInputStream(map.getBytes()));
        return (Map<String, Integer>) xmlDecoder.readObject();
    }


}

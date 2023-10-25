import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;


public class BuyerAgent extends Agent {
    DFAgentDescription[] dfAgentDescriptions;
    private AID bestSeller;
    private Double bestPrice = Double.MAX_VALUE;
    private int count = 0;
    @Override
    protected void setup() {
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                DFAgentDescription dfAgentDescription = new DFAgentDescription();
                ServiceDescription serviceDescription = new ServiceDescription();
                serviceDescription.setType("album");
                dfAgentDescription.addServices(serviceDescription);
                try {
                    dfAgentDescriptions = DFService.search(myAgent, dfAgentDescription);
                    for (DFAgentDescription DFAD : dfAgentDescriptions) {
                        AID sellerAID = DFAD.getName();
                        // CFP = Call For Proposal
                        ACLMessage aclMessage = new ACLMessage(ACLMessage.CFP);
                        aclMessage.setContent("What is the price for the FACE album? :)");
                        aclMessage.addReceiver(sellerAID);
                        send(aclMessage);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage receivedMsg = receive();
                if (receivedMsg != null) {
                    switch (receivedMsg.getPerformative()) {
                        case ACLMessage.PROPOSE :
                            count ++;
                            double price = Double.parseDouble(receivedMsg.getContent());
                            if (price < bestPrice) {
                                bestPrice = price;
                                bestSeller = receivedMsg.getSender();
                            }
                            if (dfAgentDescriptions.length == count) {
                                ACLMessage aclMessage = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                                aclMessage.addReceiver(bestSeller);
                                aclMessage.setContent("I accept your proposal price :)");
                                send(aclMessage);
                            }
                            break;
                        case ACLMessage.AGREE : {
                            ACLMessage aclMessage = new ACLMessage(ACLMessage.REQUEST);
                            aclMessage.addReceiver(receivedMsg.getSender());
                            aclMessage.setContent("I would like to buy the album :)");
                            send(aclMessage);
                        }
                        break;
                        case ACLMessage.CONFIRM : {
                            System.out.println("Agent : " + receivedMsg.getSender() + " -----> " + receivedMsg.getContent());
                        }
                        break;
                    }
                }
            }
        });
    }
}

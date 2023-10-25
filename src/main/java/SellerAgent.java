import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

public class SellerAgent extends Agent {
    private String price;
    @Override
    protected void setup() {
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                price = (String) getArguments()[0];
                // Annuaire pour publier les services - JADE
                DFAgentDescription dfAgentDescription = new DFAgentDescription();
                // La description de service
                ServiceDescription service1 = new ServiceDescription();
                service1.setType("album");
                service1.setName("BTS");
                dfAgentDescription.addServices(service1);
                try {
                    DFService.register(myAgent, dfAgentDescription);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage receiveMsg = receive();
                if (receiveMsg != null) {
                    switch (receiveMsg.getPerformative()) {
                        case ACLMessage.CFP :
                            ACLMessage aclMessage = new ACLMessage(ACLMessage.PROPOSE);
                            aclMessage.setContent(price);
                            aclMessage.addReceiver(receiveMsg.getSender());
                            send(aclMessage);
                            break;
                        case ACLMessage.ACCEPT_PROPOSAL:
                            ACLMessage aclMessage_ = new ACLMessage(ACLMessage.AGREE);
                            aclMessage_.setContent("I can sell you the album :)");
                            aclMessage_.addReceiver(receiveMsg.getSender());
                            send(aclMessage_);
                            break;
                        case ACLMessage.REQUEST :
                            ACLMessage aclMessage__ = new ACLMessage(ACLMessage.CONFIRM);
                            aclMessage__.setContent("I'll send it your way, enjoy :)");
                            aclMessage__.addReceiver(receiveMsg.getSender());
                            send(aclMessage__);
                            break;
                    }
                }
                else
                    block();
            }
        });
    }

    @Override
    protected void takeDown() {
        try {
            // Important
            DFService.deregister(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

import jade.wrapper.AgentContainer;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

public class SimpleContainer {
    public static void main(String[] args) throws StaleProxyException {
        Runtime runtime = Runtime.instance();
        ProfileImpl profile = new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST, "localhost");
        AgentContainer agentContainer = runtime.createAgentContainer(profile);
        AgentController seller1 = agentContainer.createNewAgent("seller1", SellerAgent.class.getName(), new Object[]{"7"});
        AgentController seller2 = agentContainer.createNewAgent("seller2", SellerAgent.class.getName(), new Object[]{"1306"});
        AgentController seller3 = agentContainer.createNewAgent("seller3", SellerAgent.class.getName(), new Object[]{"0"});
        AgentController buyer = agentContainer.createNewAgent("buyer", BuyerAgent.class.getName(), new Object[]{});
        seller1.start();
        seller2.start();
        seller3.start();
        buyer.start();
    }
}

package asia.zm.rule;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import asia.zm.dev.coolstore.Order;

public class FireKJar {
	public static void main(String[] args) {
		KieServices ks = KieServices.Factory.get();
		KieContainer kContainer = ks.getKieClasspathContainer();
		KieSession kSession = kContainer.newKieSession("");

		Order order = new Order();
		order.setOrderValue(110D);
		kSession.insert(order);

		int rules = kSession.fireAllRules();
	}
}

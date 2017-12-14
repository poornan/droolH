package asia.zm.rule;

import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import asia.zm.dev.coolstore.Order;
import org.kie.api.runtime.StatelessKieSession;

public class FireKJar {
    public static void main(String[] args) {
        runStatefull();
        runStateless();
    }

    private static void runStateless() {
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer();
        StatelessKieSession kSession = kContainer.newStatelessKieSession("ksession1");

        Order order = new Order();
        order.setOrderValue(110D);

        kSession.execute(order);
        System.out.println(order.getApprovingOffocer());
//		int rules = kSession.fireAllRules();ß
    }

    private static void runStatefull() {
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer();
        KieBase base = kContainer.getKieBase("kbase1");
        KieSession kSession = base.newKieSession();

        Order order = new Order();
        order.setOrderValue(110D);
        kSession.insert(order);

        int rules = kSession.fireAllRules();
        System.out.println(rules);
        System.out.println(order.getApprovingOffocer());
    }
}

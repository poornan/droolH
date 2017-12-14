package asia.zm.rule;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.drools.compiler.kproject.ReleaseIdImpl;
import org.drools.core.io.impl.UrlResource;
import org.kie.api.KieServices;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.KieScanner;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;

public class DecisionTableTest {

	public static void main(String[] args) {

		// works even without -SNAPSHOT versions
		String url = "http://localhost:8080/kie-drools/maven2/de/test/Test/1.2.3/Test-1.2.3.jar";

		// make sure you use "LATEST" here!
		ReleaseIdImpl releaseId = new ReleaseIdImpl("de.test", "Test", "LATEST");

		KieServices ks = KieServices.Factory.get();

		ks.getResources().newUrlResource(url);

		KieContainer kieContainer = ks.newKieContainer(releaseId);

		// check every 5 seconds if there is a new version at the URL
		KieScanner kieScanner = ks.newKieScanner(kieContainer);
		kieScanner.start(5000L);
		// alternatively:
		// kieScanner.scanNow();

		Scanner scanner = new Scanner(System.in);
		while (true) {
			runRule(kieContainer);
			System.out.println("Press enter in order to run the test again....");
			scanner.nextLine();
		}
	}

	private static void runRule(KieContainer kieKontainer) {
		StatelessKieSession kSession = kieKontainer.newStatelessKieSession("testSession");
		kSession.setGlobal("out", System.out);
		kSession.execute("testRuleAgain");
	}
}

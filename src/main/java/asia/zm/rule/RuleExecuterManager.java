package asia.zm.rule;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import asia.zm.dev.coolstore.Order;

public class RuleExecuterManager {
	static Map<String, KieBase> kieBaseCache = null;
	static {
		kieBaseCache = new HashMap<>();
	}

	public static void main(String args[]) throws IOException {
		URL jarUrl = RuleExecuterManager.class.getClassLoader().getResource("coolStore-2.0.0.jar");
		/*
		 * try (ZipFile zipFile = new ZipFile(jarUrl.getPath())) {
		 * 
		 * if (zipFile != null) { Enumeration<? extends ZipEntry> entries =
		 * zipFile.entries(); // get entries from the zip file...
		 * 
		 * if (entries != null) { while (entries.hasMoreElements()) { ZipEntry entry =
		 * entries.nextElement(); System.out.println(entry.getName()); if
		 * ("asia/zm/dev/coolstore/oaTable.gdst".equals(entry.getName())) { InputStream
		 * inputStream = zipFile.getInputStream(entry);
		 * System.out.println(inputStream.toString()); } else if
		 * ("asia/zm/dev/coolstore/OrderaApproval.rdrl".equals(entry.getName())) {
		 * InputStream inputStream = zipFile.getInputStream(entry);
		 * System.out.println(inputStream.toString());
		 * 
		 * } } } } }
		 */

		unZipIt("C:\\Dev\\Code\\zm-ruleengine\\RuleJars\\coolStore-2.0.0.jar",
				"C:\\Dev\\Code\\zm-ruleengine\\RuleJars\\Extracted");
		Order order = new Order();
		order.setOrderValue(110.0D);
		executeStatefull(order);
	}

	public static void executeStatefull(Order order) {

		try {
			KieSession kieSession = null;
			if (kieBaseCache.get("validateApplicant") == null) {
				String content = new String(Files.readAllBytes(Paths.get(
						"C:/Dev/Code/zm-ruleengine/RuleJars/Extracted/asia/zm/dev/coolstore/OrderaApproval.rdrl")),
						Charset.forName("UTF-8"));
				System.out.println("Read New Rules set from File");
				// load up the knowledge base
				KieServices ks = KieServices.Factory.get();
				String inMemoryDrlFileName = "src/main/resources/stateFulSessionRule.drl";
				KieFileSystem kfs = ks.newKieFileSystem();
				kfs.write(inMemoryDrlFileName, ks.getResources().newReaderResource(new StringReader(content))
						.setResourceType(ResourceType.DRL));
				KieBuilder kieBuilder = ks.newKieBuilder(kfs).buildAll();
				if (kieBuilder.getResults().hasMessages(Message.Level.ERROR)) {
					System.out.println(kieBuilder.getResults().toString());
				}
				KieContainer kContainer = ks.newKieContainer(kieBuilder.getKieModule().getReleaseId());
				KieBaseConfiguration kbconf = ks.newKieBaseConfiguration();
				KieBase kbase = kContainer.newKieBase(kbconf);
				kieSession = kbase.newKieSession();
				System.out.println("Put rules KieBase into Custom Cache");
				kieBaseCache.put("validateApplicant", kbase);
			} else {
				System.out.println("Get existing rules KieBase from Custom Cache");
				kieSession = kieBaseCache.get("validateApplicant").newKieSession();
			}

			/*
			 * kSession.addEventListener(new DebugAgendaEventListener());
			 * kSession.addEventListener(new DebugRuleRuntimeEventListener());
			 */

			kieSession.insert(order);
			kieSession.fireAllRules();
			System.out.println(order.getApprovingOffocer());
			kieSession.dispose();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public static void unZipIt(String zipFile, String outputFolder) {
		byte[] buffer = new byte[1024];
		try {
			// create output directory is not exists
			File folder = new File(outputFolder);
			if (!folder.exists()) {
				folder.mkdir();
			}
			// get the zip file content
			ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
			// get the zipped file list entry
			ZipEntry ze = zis.getNextEntry();
			while (ze != null) {
				String fileName = ze.getName();

				File newFile = new File(outputFolder + File.separator + fileName);
				if (ze.isDirectory()) {
					new File(newFile.getAbsolutePath()).mkdirs();
				} else {
					System.out.println("file unzip : " + newFile.getAbsoluteFile());
					// create all non exists folders
					// else you will hit FileNotFoundException for compressed folder
					new File(newFile.getParent()).mkdirs();

					FileOutputStream fos = new FileOutputStream(newFile);
					int len;
					while ((len = zis.read(buffer)) > 0) {
						fos.write(buffer, 0, len);
					}
					fos.close();
				}
				ze = zis.getNextEntry();
			}
			zis.closeEntry();
			zis.close();
			System.out.println("UnZip Done");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

}

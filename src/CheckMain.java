import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
 
public class CheckMain {

	public static void main(String[] args) throws IOException {
		//Process[] processList;
		ArrayList processList = new ArrayList();
		String processDetail [];
		int nomP = 0;
		java.lang.Process p;
		
		try {
			String process;

			// getRuntime: Returns the runtime object associated with the current Java application.
			// Pour windows
			p = Runtime.getRuntime().exec(System.getenv("windir") +"\\system32\\"+"tasklist.exe /fo csv /nh");
			// Pour linux
			// Process p = Runtime.getRuntime().exec("ps -few");
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((process = input.readLine()) != null) {
				System.out.println("Processus : " + process); // <-- Print all Process here line
												// by line
				processDetail = process.split(",");
				System.out.println("Nom du processus : " + processDetail[nomP]);
				//processList[i] = processDetail[nomP];
				processList.add(processDetail[nomP]);
 			}
			input.close();
		} catch (Exception err) {
			err.printStackTrace();
		}
		System.out.println("contenu de processList : " + processList);
		boolean vpnIsRunning = processList.contains("VPN");
		if (vpnIsRunning) {
			System.out.println("le tunnel VPN est actif");
		}
		else {
			// exec: Executes the specified string command in a separate process.
			// N tentative de relance du tunnel VPN : runtime ?
			   System.out.println("Creating Process...");
			   p = Runtime.getRuntime().exec("notepad.exe");
		}
		
	}
}
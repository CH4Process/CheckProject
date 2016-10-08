import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
 
public class CheckMain {
	static java.lang.Process p;
	static ArrayList processList = new ArrayList();
	static String processCible = "notepad.exe";
	static boolean processIsRunning;

	public static void main(String[] args) throws IOException {
	//	ArrayList processList = new ArrayList();
	//	java.lang.Process p;
	
		getOnProcessList();

		// Affichage de l'ensemble des processu récupérés
		System.out.println("contenu de processList : " + processList);
		
		isProcessRunning();
		
		if (processIsRunning) {
			System.out.println("le processus est actif");
			// Si processus VPN actif, vérifier la connexion : envoyer signal test et attendre la réponse
			// 		Si ok, fin du test
			pingInternalServer();
		}
		else {
			// exec: Executes the specified string command in a separate process.
			// N tentative de relance du tunnel VPN : runtime ?
			   System.out.println("Le tunnel VPN ne fait pas partie de la liste des processus");
			   restartProcess();
		}
		
	}
	private static void getOnProcessList(){
	//	java.lang.Process p;
		String carASup = "\"";
		String processDetail [];
		int nomP = 0;
		try {
			// process : recois la chaine de caractère retournée par getRuntime
			String process;

			// getRuntime: Returns the runtime object associated with the current Java application.
			// Pour windows
			p = Runtime.getRuntime().exec(System.getenv("windir") +"\\system32\\"+"tasklist.exe /fo csv /nh");
			// Pour linux
			// Process p = Runtime.getRuntime().exec("ps -few");
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((process = input.readLine()) != null) {
				System.out.println("Processus : " + process); 	// <-- Print all Process here line
																// by line
				process = process.replaceAll(carASup,"");
				// processDetail : tableau à une dimension qui recoit la chaine de caractère process séparée selon le caractère ','
				processDetail = process.split(",");
				// nomP : nom du processus en position 0 du tableau process detail
				System.out.println("Nom du processus : " + processDetail[nomP]);
				//processList : Array list qui regroupe l'ensemble des nomP
				processList.add(processDetail[nomP]);
			}
			input.close();
		} catch (Exception err) {
			err.printStackTrace();
		}
	}
	private static boolean isProcessRunning (){
	//	String processCible = "notepad.exe";
	//	String processCible = "VPN.exe";
	// controle que le process processCible fait bien partie de la liste
		return processIsRunning = processList.contains(processCible);		
	}
	private static void pingInternalServer(){
		Socket socketInt = new Socket();
		InetSocketAddress addressInt = new InetSocketAddress("ch4pcsup.ddns.net",1197);  
		try {
			socketInt.connect(addressInt,2000);
			System.out.println("connexion à CH4pcsup via VPN réussie");
		//		Si KO :
		} catch (Exception eInt) {
			System.out.println("échec de connexion à CH4pcsup via VPN");
		//		SI serveur à retourner un anomalie spécifique ?
		//		SINON : test connexion :
			pingExternalServer();
		} finally {
			try {socketInt.close();}
			catch (Exception eInt) {}
		}
	}
	private static void pingExternalServer() {
		Socket socketExt = new Socket();
		InetSocketAddress addressExt = new InetSocketAddress("www.google.com",80);  
		try {
			socketExt.connect(addressExt,2000);
			System.out.println("connexion à www.google.fr réussie");
			//test de connexion OK --> Le problème vient du serveur ou du logiciel VPN
			// arret relance du processus
		} catch (Exception eExt) {
			System.out.println("échec de connexion à www.google.fr");
			// test de connexion KO --> connexion internet perdue
			// redemarre dongle USB ?
		} finally {
			try {socketExt.close();}
			catch (Exception eExt) {}
		}
	}
	private static void restartProcess(){
		int nbreRestart = 0;
		int nbreMaxRestart = 5;
		while (!processIsRunning && nbreRestart < nbreMaxRestart) {
			nbreRestart ++;
			try {
				p = Runtime.getRuntime().exec("notepad.exe");
				getOnProcessList();
				isProcessRunning();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
		if (nbreRestart >= nbreMaxRestart) {
			System.out.println("Nombre maximum de tentatvive de redémarrage du processus atteint !!!");
		}
		else {
			System.out.println("Le nombre de tentative de redémarrage du processus est de : "+nbreRestart);
		}
	}
}
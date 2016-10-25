import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
 
public class CheckMainNew {
	static java.lang.Process p;
	static String processCible = "notepad.exe";
//	static String processCible = "VPN.exe";
	static boolean processIsRunning;
	static int nbreRestart = 0;
	static int nbreMaxRestart = 3;

	public static void main(String[] args) throws IOException {

		int i = 0;
		while (true) {
			i++;
			System.out.println("-----------------------------");
			System.out.println("Boucle de traitement N° : "+i);
		
			checkMain();
			
			try {
				System.out.println("-----------------------------");
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	private static void checkMain () {
		try {
			pingInternalServer();
		//		Si KO :
		} catch (Exception eInt) {
			System.out.println("main : échec de connexion à CH4pcsup via VPN");
        //		SI serveur à retourner un anomalie spécifique ?
	   //		SINON : test connexion :
			try {
				pingExternalServer();
				processIsRunning = isProcessRunning(processCible);
				if (processIsRunning) {
					System.out.println("Processus actif. Arret / relance");
					p.destroy();
					p = Runtime.getRuntime().exec("notepad.exe");
					pingExternalServer();					
				} else {
					System.out.println("Processus inactif. relance");
					restartProcess();
					pingExternalServer();
				}
			} catch (Exception e) {
				System.out.println("main : échec de connexion à www.google.fr");
				pingExternalServer();
				// TODO: handle exception
			}
		} 
	}
	private static ArrayList getOnProcessList(){
	//	java.lang.Process p;
		ArrayList processListTemp = new ArrayList();
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
				process = process.replaceAll(carASup,"");
				// processDetail : tableau à une dimension qui recoit la chaine de caractère process séparée selon le caractère ','
				processDetail = process.split(",");
				// nomP : nom du processus en position 0 du tableau process detail
				//processList : Array list qui regroupe l'ensemble des nomP
				processListTemp.add(processDetail[nomP]);
			}
			input.close();
		} catch (Exception err) {
			err.printStackTrace();
		}
		return processListTemp;
	}
	private static boolean isProcessRunning (String processCibleTemp){
		ArrayList processList = new ArrayList();
		processList = getOnProcessList();
		// Affichage de l'ensemble des processu récupérés
		System.out.println("isProcessRunning : contenu de processList : " + processList);
        // conntrole que le process processCible fait bien partie de la liste
		System.out.println("isProcessRunning : processCibleTemp = "+processCibleTemp);
		return processList.contains(processCibleTemp);		
	}
	private static void pingInternalServer() throws Exception {
		Socket socketInt = new Socket();
		InetSocketAddress addressInt = new InetSocketAddress("ch4pcsup.ddns.net",1197);  
		socketInt.connect(addressInt,2000);
		System.out.println("connexion à CH4pcsup via VPN réussie");
		try {socketInt.close();}
		catch (Exception eInt) {}
	}
/*	private static void pingInternalServer() {
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
	}*/
	/*private static void pingExternalServer() throws Exception {
		Socket socketExt = new Socket();
		InetSocketAddress addressExt = new InetSocketAddress("www.google.com",80);  
		socketExt.connect(addressExt,2000);
		System.out.println("connexion à www.google.fr réussie");
		try {socketExt.close();}
		catch (Exception eExt) {}
	}*/
	private static void pingExternalServer() {
		Socket socketExt = new Socket();
		InetSocketAddress addressExt = new InetSocketAddress("www.google.com",80);  
		try {
			socketExt.connect(addressExt,2000);
			System.out.println("connexion à www.google.fr réussie");
			//test de connexion OK --> Le problème vient du serveur ou du logiciel VPN
			// arret relance du processus ?
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
		while (!processIsRunning && nbreRestart < nbreMaxRestart) {
			nbreRestart ++;
			System.out.println("restartProcess / tentative n° : "+nbreRestart);
			try {
				p = Runtime.getRuntime().exec("notepad.exe");
				processIsRunning = isProcessRunning(processCible);
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
			nbreRestart = 0;
		}
	}
}
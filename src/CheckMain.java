import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
 
public class CheckMain {

	public static void main(String[] args) throws IOException {
		ArrayList processList = new ArrayList();
		String processDetail [];
		int nomP = 0;
		java.lang.Process p;
		String processCible = "notepad.exe";
		String carASup = "\"";
		
		try {
			// process : recois la chaine de caract�re retourn�e par getRuntime
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
				process = process.replaceAll(carASup,"");
				// processDetail : tableau � une dimension qui recoit la chaine de caract�re process s�par�e selon le caract�re ','
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
		// Affichage de l'ensemble des processu r�cup�r�s
		System.out.println("contenu de processList : " + processList);
		// controle que le process processCible fait bien partie de la liste
		boolean vpnIsRunning = processList.contains(processCible);
		if (vpnIsRunning) {
			System.out.println("le tunnel VPN est actif");
			// Si processus VPN actif, v�rifier la connexion : envoyer signal test et attendre la r�ponse
			// 		Si ok, fin du test
			Socket socketInt = new Socket();
			InetSocketAddress addressInt = new InetSocketAddress("ch4pcsup.ddns.net",1197);  
			try {
				socketInt.connect(addressInt,2000);
				System.out.println("connexion � CH4pcsup via VPN r�ussie");
			//		Si KO :
			} catch (Exception eInt) {
				System.out.println("�chec de connexion � CH4pcsup via VPN");
			//		SI serveur � retourner un anomalie sp�cifique ?
			//		SINON : test connexion :
				Socket socketExt = new Socket();
				InetSocketAddress addressExt = new InetSocketAddress("www.google.com",80);  
				try {
					socketExt.connect(addressExt,2000);
					System.out.println("connexion � www.google.fr r�ussi");
					//test de connexion OK --> Le probl�me vient du serveur ou du logiciel VPN
					// arret relance du processus
				} catch (Exception eExt) {
					System.out.println("�chec de connexion � www.google.fr");
					// test de connexion KO --> connexion internet perdue
					// redemarre dongle USB ?
				} finally {
					try {socketExt.close();}
					catch (Exception eExt) {}
				}
			} finally {
				try {socketInt.close();}
				catch (Exception eInt) {}
			}
		}
		else {
			// exec: Executes the specified string command in a separate process.
			// N tentative de relance du tunnel VPN : runtime ?
			   System.out.println("Le tunnel VPN ne fait pas partie de la liste des processus");
			   p = Runtime.getRuntime().exec("notepad.exe");
		}
		
	}
}
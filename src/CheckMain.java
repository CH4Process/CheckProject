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
			// process : recois la chaine de caractère retournée par getRuntime
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
		// Affichage de l'ensemble des processu récupérés
		System.out.println("contenu de processList : " + processList);
		// controle que le process processCible fait bien partie de la liste
		boolean vpnIsRunning = processList.contains(processCible);
		if (vpnIsRunning) {
			System.out.println("le tunnel VPN est actif");
			// Si processus VPN actif, vérifier la connexion : envoyer signal test et attendre la réponse
			// Utilisation de l'objet socket comme pour le test de connection à google ? 
			// avec en parmaètre l'adresse du pc serveur
			// ...
			// 		Si ok, fin du test
			//		Si KO :
			//			SI serveur à retourner un anomalie ?
			//			SINON : test connexion :
						Socket socket = new Socket();
						InetSocketAddress address = new InetSocketAddress("www.google.com",80);  
						try {
							socket.connect(address,2000);
							System.out.println("connexion à www.google.fr réussi");
							//test de connexion OK --> Le problème vient du serveur ou du logiciel VPN
							// arret relance du processus
						} catch (Exception e) {
							System.out.println("échec de connexion à www.google.fr");
							// test de connexion KO --> connexion internet perdue
							// redemarre dongle USB ?
						} finally {
							try {socket.close();}
							catch (Exception e) {}
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
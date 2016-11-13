import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
 
public class CheckMainNew {
	static java.lang.Process p;
	static String processCible = "notepad.exe";
	static boolean processIsRunning;
	static boolean internalServer;
	static boolean externalServer;
	static int nbreRestart = 0;
	static int nbreMaxRestart = 3;
	static final String KILL = "taskkill /IM ";

	public static void main(String[] args) {

		int compteurTraitemet = 0;
		while (true) {
			compteurTraitemet++;
			System.out.println("-----------------------------");
			long debut = System.currentTimeMillis();
			System.out.println("Boucle de traitement N° : "+compteurTraitemet);
			System.out.println("Debut du traitement : "+ getDateHeure());
		
			checkMain();
			
			try {
				System.out.println("Fin du traitement : "+ getDateHeure());
				long fin = System.currentTimeMillis();
				long delta = fin - debut;
				System.out.println("Durée du traitement : "+ delta + "ms");
				System.out.println("-----------------------------");
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	private static String getDateHeure(){
		Date date = new Date();
		SimpleDateFormat formatDate = new SimpleDateFormat("E yyyy.MM.dd 'at' hh:mm:ss:SSS");
		return formatDate.format(date);
	}
	private static void checkMain () {
		pingInternalServer();
		//		Si KO :
		if (!internalServer) {
			System.out.println("main : échec de connexion à CH4pcsup via VPN");
			pingExternalServer();
			if (externalServer) {
				processIsRunning = isProcessRunning(processCible);
				if (processIsRunning) {
					killRestartProcess();
				} else {
					restartProcess();
				}
			}else {
				System.out.println("main : échec de connexion à www.google.fr2");
				nbreRestart = 0;
				while (!externalServer && nbreRestart < nbreMaxRestart) {
					nbreRestart ++;
					pingExternalServer();
					if (!externalServer) {
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}	
					}
				}
				if (externalServer) {
					nbreRestart = 0;
					while (!internalServer && nbreRestart < nbreMaxRestart) {
						nbreRestart ++;
						pingInternalServer();
						if (!internalServer) {
							try {
								Thread.sleep(5000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}	
						}
					}
				}
			}
		} else {
			System.out.println("main : connexion à CH4pcsup via VPN réussie");

		}
	}
	private static ArrayList getOnProcessList(){
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
	/*private static void pingInternalServer() throws Exception {
		Socket socketInt = new Socket();
		InetSocketAddress addressInt = new InetSocketAddress("ch4pcsup.ddns.net",1197);  
		socketInt.connect(addressInt,2000);
		System.out.println("connexion à CH4pcsup via VPN réussie");
		try {socketInt.close();}
		catch (Exception eInt) {}
	}*/
	private static void pingInternalServer() {
		Socket socketInt = new Socket();
		InetSocketAddress addressInt = new InetSocketAddress("ch4pcsup.ddns.net",1197);  
		try {
			socketInt.connect(addressInt,2000);
			internalServer = true;
			System.out.println("connexion à CH4pcsup via VPN réussie");
		//		Si KO :
		} catch (Exception eInt) {
			System.out.println("échec de connexion à CH4pcsup via VPN");
			internalServer = false;
		//		SI serveur à retourner un anomalie spécifique ?
		//		SINON : test connexion :
		} finally {
			try {socketInt.close();}
			catch (Exception eInt) {}
		}
	}
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
			externalServer = true;
			System.out.println("connexion à www.google.fr réussie");
		} catch (Exception eExt) {
			System.out.println("échec de connexion à www.google.fr1");
			externalServer = false;
		} finally {
			try {socketExt.close();}
			catch (Exception eExt) {}
		}
	}
	private static void restartProcess(){
		nbreRestart = 0;
		while (!processIsRunning && nbreRestart < nbreMaxRestart) {
			nbreRestart ++;
			System.out.println("restartProcess / tentative n° : "+nbreRestart);
			try {
				p = Runtime.getRuntime().exec(processCible);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			processIsRunning = isProcessRunning(processCible);
			pingInternalServer();
		} 
		controleNbreRestart(nbreRestart);
	}
	private static void killRestartProcess(){
		nbreRestart = 0;
		while (!internalServer && nbreRestart < nbreMaxRestart) {
			nbreRestart ++;
			System.out.println("Processus actif. Arret / relance");
			processKill(processCible);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("On relance le processus");
			try {
				p = Runtime.getRuntime().exec(processCible);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			pingInternalServer();
		}
		controleNbreRestart(nbreRestart);
	}
	private static void processKill (String processCible){
		try {
			System.out.println("processKill : on tue le processus : "+processCible);
			System.out.println("commande : " +KILL+processCible);
			Runtime.getRuntime().exec(KILL + processCible);
		} catch (Exception e) {
			System.out.println("processKill : problème lors du kill : "+e.getMessage());
		}
	}
	private static void controleNbreRestart (int nbreRestart){
		if (nbreRestart >= nbreMaxRestart) {
			System.out.println("Nombre maximum de tentatvive de redémarrage du processus atteint !!!");
		}
		else {
			System.out.println("Le nombre de tentative de redémarrage du processus est de : "+nbreRestart);
			nbreRestart = 0;
		}
	}
}
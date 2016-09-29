
public class CheckProc {
	protected String processDetail [];

	// constructeur process avec 1 paramètre : associe les variables recu aux variables d'instances
	public CheckProc (String processDetail[]) {
	System.out.println("Alimentation d'un objet process");
	this.constructeurProcess(processDetail);
	}

	private void constructeurProcess(String[] processDetail) {
		// TODO Auto-generated method stub
		this.processDetail = processDetail;
	}

	public String[] getProcessDetail() {
		return processDetail;
	}

	public void setProcessDetail(String[] processDetail) {
		this.processDetail = processDetail;
	}
} 
import java.util.ArrayList;

public class Page {
	private String name;
	private float nbLinkIn = 0;
	private float nbLinkOut;
	private float pageRank;
	
	private ArrayList<float[]> linkName = new ArrayList<float[]>();
	
	public Page(String name, float nbLinkOut, float pageRank) {
		this.name = name;
		this.nbLinkOut = nbLinkOut;
		this.pageRank = pageRank;
	}
	
	public void addLink(float pageRank, float linkOut){
		float[] link = {pageRank, linkOut};
		linkName.add(link);
		this.nbLinkIn++;
	}
	
	public float[] getLink(int index){
		return linkName.get(index);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getNbLinkIn() {
		return nbLinkIn;
	}

	public void setNbLinkIn(float nbLinkIn) {
		this.nbLinkIn = nbLinkIn;
	}
	
	public float getNbLinkOut() {
		return nbLinkOut;
	}

	public void setNbLinkOut(float nbLinkOut) {
		this.nbLinkOut = nbLinkOut;
	}

	public float getPageRank() {
		return pageRank;
	}

	public void setPageRank(float pageRank) {
		this.pageRank = pageRank;
	}

}
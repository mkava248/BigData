public class Page {
	private String name;
	private int nbLink;
	private float pageRank;
	
	public Page(String name, int nbLink, float pageRank) {
		this.name = name;
		this.nbLink = nbLink;
		this.pageRank = pageRank;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getNbLink() {
		return nbLink;
	}

	public void setNbLink(int nbLink) {
		this.nbLink = nbLink;
	}

	public float getPageRank() {
		return pageRank;
	}

	public void setPageRank(float pageRank) {
		this.pageRank = pageRank;
	}

}
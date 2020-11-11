package ke.co.mobiticket.mobiticket.pojos;

import java.util.List;
public class Section {
    private String sectionTitle;
    private List<Ticket> allItemsInSection;
    public Section(String sectionTitle, List<Ticket> allItemsInSection) {
        this.sectionTitle = sectionTitle;
        this.allItemsInSection = allItemsInSection;
    }
    public String getSectionTitle() {
        return sectionTitle;
    }
    public void setSectionTitle(String sectionTitle) {
        this.sectionTitle = sectionTitle;
    }
    public List<Ticket> getAllItemsInSection() {
        return allItemsInSection;
    }
    public void setAllItemsInSection(List<Ticket> allItemsInSection) {
        this.allItemsInSection = allItemsInSection;
    }
}

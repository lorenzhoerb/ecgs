package at.ac.tuwien.sepm.groupphase.backend.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.CascadeType;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
public class Competition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 4095)
    private String name;

    // @TODO: change name in the ER-Diagram to match end
    @Column(nullable = false)
    private LocalDateTime beginOfRegistration;

    @Column(nullable = false)
    private LocalDateTime endOfRegistration;

    // @TODO: change name in the ER-Diagram as end is a keyword in h2
    @Column(nullable = false)
    private LocalDateTime endOfCompetition;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 4095)
    private String picturePath;

    @Column(nullable = false)
    private Boolean isPublic;

    @Column(nullable = false)
    private Boolean draft;

    @Column(nullable = true, length = 255)
    private String email;

    @Column(nullable = true, length = 255)
    private String phone;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
        name = "competition_gradingGroup",
        joinColumns = {@JoinColumn(referencedColumnName = "id")},
        inverseJoinColumns = {@JoinColumn(referencedColumnName = "id")}
    )
    private Set<GradingGroup> competition;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(referencedColumnName = "id")
    private ApplicationUser creator;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "competition")
    private Set<Judge> judges;

    public Competition() {}

    public Competition(String name, LocalDateTime beginOfRegistration, LocalDateTime endOfRegistration,
                       LocalDateTime endOfCompetition, String description, String picturePath, Boolean isPublic,
                       Boolean draft, String email, String phone) {
        this.name = name;
        this.beginOfRegistration = beginOfRegistration;
        this.endOfRegistration = endOfRegistration;
        this.endOfCompetition = endOfCompetition;
        this.description = description;
        this.picturePath = picturePath;
        this.isPublic = isPublic;
        this.draft = draft;
        this.email = email;
        this.phone = phone;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getBeginOfRegistration() {
        return beginOfRegistration;
    }

    public void setBeginOfRegistration(LocalDateTime beginOfRegistration) {
        this.beginOfRegistration = beginOfRegistration;
    }

    public LocalDateTime getEndOfRegistration() {
        return endOfRegistration;
    }

    public void setEndOfRegistration(LocalDateTime endOfRegistration) {
        this.endOfRegistration = endOfRegistration;
    }

    public LocalDateTime getEndOfCompetition() {
        return endOfCompetition;
    }

    public void setEndOfCompetition(LocalDateTime endOfCompetition) {
        this.endOfCompetition = endOfCompetition;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public Boolean getPublic() {
        return isPublic;
    }

    public void setPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public Boolean getDraft() {
        return draft;
    }

    public void setDraft(Boolean draft) {
        this.draft = draft;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Set<GradingGroup> getCompetition() {
        return competition;
    }

    public void setCompetition(Set<GradingGroup> competition) {
        this.competition = competition;
    }

    public Set<Judge> getJudges() {
        return judges;
    }

    public void setJudges(Set<Judge> judges) {
        this.judges = judges;
    }

    @Override
    public String toString() {
        return "Competition{"
            + "id=" + id
            + ", name='" + name + '\''
            + ", beginOfRegistration=" + beginOfRegistration
            + ", endOfRegistration=" + endOfRegistration
            + ", endOfCompetition=" + endOfCompetition
            + ", description='" + description + '\''
            + ", picturePath='" + picturePath + '\''
            + ", isPublic=" + isPublic
            + ", draft=" + draft
            + ", email='" + email + '\''
            + ", phone='" + phone + '\''
            + ", competition=" + competition
            + ", judges=" + judges
            + '}';
    }
}

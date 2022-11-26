package at.ac.tuwien.sepm.groupphase.backend.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import java.util.Set;

@Entity
public class ManagedBy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    private ApplicationUser manager;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    private ApplicationUser member;

    @ManyToMany(mappedBy = "clubs")
    private Set<Flags> flags;

    @Column(nullable = false, length = 4095)
    private String teamName;

    public ManagedBy() {}

    public ManagedBy(ApplicationUser manager, ApplicationUser member, String teamName) {
        this.manager = manager;
        this.member = member;
        this.teamName = teamName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ApplicationUser getManager() {
        return manager;
    }

    public void setManager(ApplicationUser manager) {
        this.manager = manager;
    }

    public ApplicationUser getMember() {
        return member;
    }

    public void setMember(ApplicationUser member) {
        this.member = member;
    }

    public Set<Flags> getFlags() {
        return flags;
    }

    public void setFlags(Set<Flags> flags) {
        this.flags = flags;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    @Override
    public String toString() {
        return "ManagedBy{"
            + "id=" + id
            + ", manager=" + manager
            + ", member=" + member
            + ", flags=" + flags
            + ", teamName='" + teamName + '\''
            + '}';
    }
}

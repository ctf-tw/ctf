package kl.tw.ctf.domain;


import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A SuspectedReason.
 */
@Entity
@Table(name = "suspected_reason")
public class SuspectedReason implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Column(name = "reason")
    private String reason;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReason() {
        return reason;
    }

    public SuspectedReason reason(String reason) {
        this.reason = reason;
        return this;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SuspectedReason suspectedReason = (SuspectedReason) o;
        if (suspectedReason.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), suspectedReason.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "SuspectedReason{" +
            "id=" + getId() +
            ", reason='" + getReason() + "'" +
            "}";
    }
}

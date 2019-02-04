package ru.madelinn.lunchvoting.model;

import org.hibernate.validator.constraints.SafeHtml;
import ru.madelinn.lunchvoting.HasId;
import ru.madelinn.lunchvoting.View;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@MappedSuperclass
@Access(AccessType.FIELD)
public abstract class AbstractBaseEntity implements HasId {

    public static final int START_SEQ = 100000;

    @Id
    @SequenceGenerator(name = "global_seq", sequenceName = "global_seq",allocationSize = 1, initialValue = START_SEQ)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "global_seq")
    protected Integer id;

    @NotBlank
    @Size(min = 3, max = 50)
    @Column(name = "name", nullable = false)
    @SafeHtml(groups = {View.Web.class})
    protected String name;

    protected AbstractBaseEntity(){

    }

    protected AbstractBaseEntity(Integer id, String name){
        this.id = id;
        this.name = name;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || !getClass().equals(o.getClass()))
            return false;

        AbstractBaseEntity that = (AbstractBaseEntity) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id == null ? 0 : id;
    }

    @Override
    public String toString() {
        return String.format("Entity %s (%s, %s)",getClass().getName(), id, name);
    }
}

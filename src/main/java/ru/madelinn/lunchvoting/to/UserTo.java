package ru.madelinn.lunchvoting.to;

import org.hibernate.validator.constraints.SafeHtml;
import ru.madelinn.lunchvoting.HasId;
import ru.madelinn.lunchvoting.model.Restaurant;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

public class UserTo implements HasId, Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    @NotBlank
    @Size(min = 3, max = 50)
    @SafeHtml
    private String name;

    @Email
    @NotBlank
    @Size(max = 50)
    @SafeHtml
    private String email;

    @Size(min = 6, max = 100)
    private String password;

    private Restaurant vote;

    public UserTo() {

    }

    public UserTo(Integer id, String name, String email, String password, Restaurant vote){
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.vote = vote;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Restaurant getVote() {
        return vote;
    }

    public void setVote(Restaurant vote) {
        this.vote = vote;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }
}

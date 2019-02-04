package ru.madelinn.lunchvoting;

import ru.madelinn.lunchvoting.model.User;
import ru.madelinn.lunchvoting.to.UserTo;
import ru.madelinn.lunchvoting.util.Util;

public class AuthorizedUser extends org.springframework.security.core.userdetails.User {

    private static final long serialVersionUID = 1L;

    private UserTo userTo;

    public AuthorizedUser(User user){
        super(user.getEmail(), user.getPassword(), true, true, true, true, user.getRoles());
        this.userTo = Util.asTo(user);
    }

    public int getId(){
        return userTo.getId();
    }

    public void update(UserTo newTo){
        userTo = newTo;
    }

    public UserTo getUserTo(){
        return userTo;
    }

    @Override
    public String toString(){
        return userTo.toString();
    }

}

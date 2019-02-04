package ru.madelinn.lunchvoting.util;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import ru.madelinn.lunchvoting.HasId;
import ru.madelinn.lunchvoting.model.Role;
import ru.madelinn.lunchvoting.model.User;
import ru.madelinn.lunchvoting.to.UserTo;
import ru.madelinn.lunchvoting.util.exception.IllegalRequestDataException;
import ru.madelinn.lunchvoting.util.exception.NotFoundException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class Util {

    private Util() {

    }

    public static User createNewFromTo(UserTo newUser) {
        return new User(null, newUser.getName(), newUser.getEmail(), newUser.getPassword(), newUser.getVote(), Role.ROLE_USER);
    }

    public static UserTo asTo(User user) {
        return new UserTo(user.getId(), user.getName(), user.getEmail(), user.getPassword(), user.getVote());
    }

    public static User updateFromTo(User user, UserTo userTo){
        user.setName(userTo.getName());
        user.setEmail(userTo.getEmail());
        user.setPassword(userTo.getPassword());
        user.setVote(userTo.getVote());
        return user;
    }

    public static User prepareToSave(User user, PasswordEncoder passwordEncoder) {
        String password = user.getPassword();
        user.setPassword(StringUtils.isEmpty(password) ? password : passwordEncoder.encode(password));
        user.setEmail(user.getEmail().trim().toLowerCase());
        return user;
    }

    public static <T> T checkNotFoundWithId(T object, int id) {
        return checkNotFound(object, "id=" + id);
    }

    public static void checkNotFoundWithId(boolean found, int id) {
        checkNotFound(found, "id=" + id);
    }

    public static <T> T checkNotFound(T object, String msg) {
        checkNotFound(object != null, msg);
        return object;
    }

    public static void checkNotFound(boolean found, String arg) {
        if (!found) {
            throw new NotFoundException(arg);
        }
    }

    public static void checkNew(HasId bean) {
        if(!bean.isNew())
            throw new IllegalRequestDataException(bean + "must be new (id=null");
    }

    public static void assureIdConsistent(HasId bean, int id){
        if(bean.isNew())
            bean.setId(id);
        else if (bean.getId() != id)
            throw new IllegalRequestDataException(bean + "must be with id=" + id);
    }

    public static Throwable getRootCause(Throwable t) {
        Throwable result = t;
        Throwable cause;

        while (null != (cause = result.getCause()) && (result != cause)) {
            result = cause;
        }
        return result;
    }

    public static <T extends Throwable> void validateRootCause(Runnable runnable, Class<T> exceptionClass) {
        assertThrows(exceptionClass, () -> {
            try {
                runnable.run();
            } catch (Exception e) {
                throw getRootCause(e);
            }
        });
    }

    public static String getMessage(Throwable e) {
        return e.getLocalizedMessage() != null ? e.getLocalizedMessage() : e.getClass().getName();
    }
}

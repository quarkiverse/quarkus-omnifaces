package io.quarkiverse.omnifaces.it;

import static org.omnifaces.util.Faces.refresh;
import static org.omnifaces.util.Faces.setSessionAttribute;
import static org.omnifaces.util.Messages.addGlobalError;
import static org.omnifaces.util.Messages.addGlobalInfo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Future;

import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.omnifaces.cdi.Push;
import org.omnifaces.cdi.PushContext;
import org.omnifaces.cdi.ViewScoped;

@Named
@ViewScoped
public class PushTestBean implements Serializable {

    @Inject
    @Push
    PushContext app;

    @Inject
    @Push
    PushContext sess;

    @Inject
    @Push
    PushContext view;

    public void open() {
        setSessionAttribute("pushTestUser", UUID.randomUUID().toString());
        refresh();
    }

    public void pushApp() {
        app.send(LocalDateTime.now().toString());
    }

    public void pushSess() {
        sess.send(LocalDateTime.now().toString());
    }

    public void pushView() {
        view.send(LocalDateTime.now().toString());
    }

    public void pushUser(String recipientUser) {
        Set<Future<Void>> sent = sess.send(LocalDateTime.now().toString(), recipientUser);

        if (sent.isEmpty()) {
            addGlobalError("This user does not exist!");
        } else {
            addGlobalInfo("Sent to {0} sockets", sent.size());
        }
    }

}
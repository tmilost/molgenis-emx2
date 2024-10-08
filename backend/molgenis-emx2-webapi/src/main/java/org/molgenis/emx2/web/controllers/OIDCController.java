package org.molgenis.emx2.web.controllers;

import static java.util.Objects.requireNonNull;
import static org.molgenis.emx2.web.SecurityConfigFactory.OIDC_CLIENT_NAME;

import io.javalin.http.Context;
import java.util.Optional;
import org.molgenis.emx2.Database;
import org.molgenis.emx2.MolgenisException;
import org.molgenis.emx2.web.MolgenisSessionManager;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.engine.CallbackLogic;
import org.pac4j.core.engine.DefaultCallbackLogic;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.exception.http.RedirectionAction;
import org.pac4j.core.http.adapter.HttpActionAdapter;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.FindBest;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.javalin.JavalinHttpActionAdapter;
import org.pac4j.javalin.JavalinWebContext;
import org.pac4j.jee.context.session.JEESessionStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OIDCController {

  private static final Logger logger = LoggerFactory.getLogger(OIDCController.class);

  private final MolgenisSessionManager sessionManager;
  private final Config securityConfig;
  private final SessionStore sessionStore;

  public OIDCController(MolgenisSessionManager sessionManager, Config securityConfig) {
    this.sessionManager = requireNonNull(sessionManager);
    this.securityConfig = requireNonNull(securityConfig);
    this.sessionStore = FindBest.sessionStore(null, securityConfig, JEESessionStore.INSTANCE);
  }

  public Object handleLoginRequest(Context ctx) {
    final JavalinWebContext context = new JavalinWebContext(ctx);
    sessionStore.set(context, Pac4jConstants.REQUESTED_URL, ctx.queryParams("redirect"));
    final var client =
        securityConfig
            .getClients()
            .findClient(OIDC_CLIENT_NAME)
            .orElseThrow(
                () ->
                    new MolgenisException(
                        "Expected OIDC client not found in security configuration"));
    HttpAction action;
    try {
      Optional<RedirectionAction> redirectionAction =
          client.getRedirectionAction(context, JEESessionStore.INSTANCE);
      if (redirectionAction.isEmpty()) {
        throw new MolgenisException("Expected OIDC redirection action not found");
      }
      action = redirectionAction.get();

    } catch (final HttpAction e) {
      action = e;
    }
    return JavalinHttpActionAdapter.INSTANCE.adapt(action, context);
  }

  public Object handleLoginCallback(Context ctx) {
    final JavalinWebContext context = new JavalinWebContext(ctx);

    final HttpActionAdapter adapter =
        FindBest.httpActionAdapter(null, securityConfig, JavalinHttpActionAdapter.INSTANCE);
    final CallbackLogic callbackLogic =
        FindBest.callbackLogic(null, securityConfig, DefaultCallbackLogic.INSTANCE);

    callbackLogic.perform(
        context, sessionStore, securityConfig, adapter, null, false, OIDC_CLIENT_NAME);

    final ProfileManager manager = new ProfileManager(context, sessionStore);
    Optional<UserProfile> oidcProfile = manager.getProfile();

    if (oidcProfile.isEmpty()) {
      logger.error("OIDC sign in failed, no profile found");
      ctx.status(500);
      ctx.redirect("/");
      return ctx.res();
    }

    String user = oidcProfile.get().getAttribute("email").toString();
    if (user == null || user.isEmpty()) {
      logger.error("OIDC sign in failed, email claim is empty");
      ctx.status(500);
      ctx.redirect("/");
      return ctx.res();
    }

    Database database = sessionManager.getSession(ctx.req()).getDatabase();
    if (!database.hasUser(user)) {
      logger.info("Add new OIDC user({}) to database", user);
      database.addUser(user);
    }
    database.setActiveUser(user);
    logger.info("OIDC sign in for user: {}", user);

    ctx.status(302);
    return ctx.res();
  }
}

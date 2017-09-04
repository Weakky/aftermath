package Builder;

import parser.Shared.ScriptException;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by Utilisateur on 25/03/2015.
 */
public interface ScriptDelegate {
    public Object run(Object... args) throws URISyntaxException, IOException, ScriptException;
}

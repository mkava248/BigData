import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class GetSource {

    /**
     * Permet d'obtenir le code source d'une page web
     *
     * @param sUrl (String)
     * @return code surce (String)
     * @throws Exception
     */
    static public String getCode(String sUrl) throws Exception {

        URL url = new URL(sUrl);
        URLConnection connection = url.openConnection();
        String codeSource = "", line = "";

        // Creation du buffer de connection
        BufferedReader in = new BufferedReader(new InputStreamReader(
                connection.getInputStream()));

        // Boucle pour lire les lignes du html
        while ((line = in.readLine()) != null) {
            codeSource += line + '\n';

        }
        return codeSource;
    }

}
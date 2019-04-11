import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.ClientCookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class Main {

    private static final Logger logger = Logger.getLogger("CA");

    public static String getValueFromObject(Object object) throws UnsupportedEncodingException {
        if (object == null) {
            return "";
        } else if (object.toString() == "null") {
            return "";
        } else {
            return URLEncoder.encode(object.toString(), "UTF-8");
        }
    }

    public static void main(String[] args) throws Exception {
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;
        HttpClientContext context = HttpClientContext.create();
        HttpGet request;
        String url;
        String idTokenClavier = null;
        String idUsagerCrypte = null;
        String nuusager = "XXXXXXX"; // ID USAGER (EX: 00000X)
        String idUsager = "XXXXXXX"; // LOGIN DE CONNEXION
        String codeconf = "XXXXXXX"; // VOTRE CODE CONFIDENTIEL

        String nomUsager = null;
        String numCons = null;
        String codTmp = null;
        String idAgence = null;
        String raisonSoc = null;
        String error = null;
        String isMBL = null;
        String mailCons = null;
        String nomConseiller = null;
        String action = null;
        String nbMessagesNonLus = null;
        String region = null;
        String codeErreur = null;
        String isPortefeuille = null;
        String mar = null;

        CookieStore cookieStore = new BasicCookieStore();
        client = HttpClients.custom().setDefaultCookieStore(cookieStore).build();

        try {
            url = "https://www.caisse-epargne.fr/cepac/authentification/manage?step=identification&identifiant=" + idUsager + "&isAccessible=1";
            request = new HttpGet(url);
            //request.setConfig(config);

            response = client.execute(request);
            if (response.getStatusLine().getStatusCode() == 200) {
                String body = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
                JSONObject jsonObject = new JSONObject(body);
                JSONObject keyboard = (JSONObject) jsonObject.get("keyboard");
                idTokenClavier = keyboard.getString("Id");
                logger.debug(idTokenClavier);
            } else {
                logger.error(response);
            }
        } catch (Exception e) {
            logger.error(e);
        } finally {
            response.close();
        }


        try {
            url = "https://www.netpro131.caisse-epargne.fr/login.aspx?auth_mode=ajax&nuusager=" + nuusager + "&codconfstar=&nuabbd=" + idUsager + "&idTokenClavier=" + idTokenClavier + "&codconf=" + codeconf + "&typeAccount=WP&authMode=ajax&ctx=typsrv%3DWP&clavierSecurise=1&newCodeConf=";
            request = new HttpGet(url);
            //request.setConfig(config);
            response = client.execute(request);
            if (response.getStatusLine().getStatusCode() == 200) {
                String body = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
                JSONObject jsonObject = new JSONObject(body);
                idUsagerCrypte = jsonObject.getString("IdUsagerCrypte");

                nomUsager = getValueFromObject(jsonObject.get("nomUsager"));
                numCons = getValueFromObject(jsonObject.get("numCons"));
                codTmp = getValueFromObject(jsonObject.get("codTmp"));
                idAgence = getValueFromObject(jsonObject.get("IdAgence"));
                raisonSoc = getValueFromObject(jsonObject.get("raisonSoc"));
                error = getValueFromObject(jsonObject.get("error"));
                isMBL = getValueFromObject(jsonObject.get("isMBL"));
                mailCons = getValueFromObject(jsonObject.get("mailCons"));
                nomConseiller = getValueFromObject(jsonObject.get("nomConseiller"));
                action = getValueFromObject(jsonObject.get("action"));
                nbMessagesNonLus = getValueFromObject(jsonObject.get("nbMessagesNonLus"));
                region = getValueFromObject(jsonObject.get("region"));
                codeErreur = getValueFromObject(jsonObject.get("codeErreur"));
                isPortefeuille = getValueFromObject(jsonObject.get("numCons"));
                mar = getValueFromObject(jsonObject.get("mar"));

                BasicClientCookie cookie = new BasicClientCookie("xtat", "-" + idUsagerCrypte);
                cookie.setDomain(".caisse-epargne.fr");
                cookie.setPath("/");
                cookie.setAttribute(ClientCookie.PATH_ATTR, "/");
                cookie.setAttribute(ClientCookie.DOMAIN_ATTR, ".caisse-epargne.fr");
                cookieStore.addCookie(cookie);

                logger.debug(idUsagerCrypte);
                logger.debug(jsonObject);
            } else {
                logger.error(response);
            }
        } catch (Exception e) {
            logger.error(e);
        } finally {
            response.close();
        }

        try {
            url = "https://www.caisse-epargne.fr/cepac/authentification/manage?step=authentification&" +
                    "redirectUrl=&" +
                    "deiUrl=https%3A%2F%2Fwww.netpro131.caisse-epargne.fr&" +
                    "typeAccount=WP&" +
                    "action=" + action + "&" +
                    "nomUsager=" + nomUsager + "&" +
                    "IdUsager=" + idUsager + "&" +
                    "IdUsagerCrypte=" + idUsagerCrypte + "&" +
                    "nomConseiller=" + nomConseiller + "&" +
                    "region=" + region + "&" +
                    "nbMessagesNonLus=" + nbMessagesNonLus + "&" +
                    "error=" + error + "&" +
                    "codeErreur=" + codeErreur + "&" +
                    "isPortefeuille=" + isPortefeuille + "&" +
                    "isMBL=" + isMBL + "&" +
                    "mailCons=" + mailCons + "&" +
                    "numCons=" + numCons + "&" +
                    "IdAgence=" + idAgence + "&" +
                    "raisonSoc=" + raisonSoc + "&" +
                    "mar=" + mar + "&" +
                    "codTmp=" + codTmp;

            request = new HttpGet(url);

            response = client.execute(request);
            if (response.getStatusLine().getStatusCode() == 200) {
                String body = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
                Document doc = Jsoup.parse(body);
                Elements elem = doc.getElementsByClass("NumeroDeCompte");
                Element solde = elem.get(3);
                logger.debug(solde.text());
            } else {
                logger.error(response);
            }
        } catch (Exception e) {
            logger.error(e);
        } finally {
            response.close();
            client.close();
        }


    }
}

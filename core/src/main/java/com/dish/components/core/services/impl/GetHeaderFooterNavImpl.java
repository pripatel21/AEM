
package com.dish.components.core.services.impl;

import com.dish.components.core.services.GetHeaderFooterNav;
import org.apache.felix.scr.annotations.*;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Dictionary;

/**
 * Created by narreddy on 9/6/17.
 */
@Component(immediate = true,
        metatype = true,
        label = "Dish Main Navigation Fetch Service",
        description = "Fetches Navigation from Sling"
)
@Properties({
        @Property(
                name = "headerLink",
                value = "https://www.dish.com/verify-aem/pageheadhandler.ashx",
                label = "Link to get Header Response"
        ),
        @Property(
                name = "headerBodyLink",
                value = "https://www.dish.com/verify-aem/headerbodyhandler.ashx",
                label = "Link to get Header Html Elements"
        ),
        @Property(
                name = "footerBodyLink",
                value = "https://www.dish.com/verify-aem/footerbodyhandler.ashx",
                label = "Link to get Footer Html Elements"
        )
})
@Service({GetHeaderFooterNav.class})

/**
 * This class will get the Header and Footer Menu from the Dish urls. We can configure
 * the urls from where to get the Header and Footer Navigation responses.
 */
public class GetHeaderFooterNavImpl implements GetHeaderFooterNav {

    private static final Logger log = LoggerFactory.getLogger(GetHeaderFooterNavImpl.class);
    private String headerLink = "";
    private String headerBodyLink = "";
    private String footerBodyLink = "";
    private String headerNav = "";
    private String headerBodyElements = "";
    private String footerBodyElements = "";

    /**
     * This method will get the Header Menu Response to build the
     * Header for the Dish.com website from the given url
     * @return - Header response
     */
    public String getHeaderNavigationMenu() {

        try {
            CloseableHttpClient httpclient = createAcceptSelfSignedCertificateClient();
            HttpGet httpget = new HttpGet(headerLink);
            log.info("Executing request " + httpget.getRequestLine());

            CloseableHttpResponse  response = httpclient.execute(httpget);

            headerNav = EntityUtils.toString(response.getEntity(), "UTF-8");
        } catch (NoSuchAlgorithmException ae) {
            log.error("No Such Algorithm Exception", ae);
        } catch(KeyStoreException se) {
            log.error("Key Store Exception", se);
        } catch(KeyManagementException me) {
            log.error("Key Management Exception", me);
        } catch(IOException ie) {
            log.error("IO Exception", ie);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return headerNav;
    }


    /**
     * This method will get the Header Menu Response to build the
     * Header for the Dish.com website from the given url
     * @return - Header response
     */
    public String getHeaderBodyElements() {

        try {
            CloseableHttpClient httpclient = createAcceptSelfSignedCertificateClient();
            HttpGet httpget = new HttpGet(headerBodyLink);
            log.info("Executing request " + httpget.getRequestLine());

            CloseableHttpResponse  response = httpclient.execute(httpget);

            headerBodyElements = EntityUtils.toString(response.getEntity(), "UTF-8");
        } catch (NoSuchAlgorithmException ae) {
            log.error("No Such Algorithm Exception", ae);
        } catch(KeyStoreException se) {
            log.error("Key Store Exception", se);
        } catch(KeyManagementException me) {
            log.error("Key Management Exception", me);
        } catch(IOException ie) {
            log.error("IO Exception", ie);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return headerBodyElements;
    }

    /**
     * This method will get the Footer Menu Response to build the
     * Footer for the Dish.com website from the given url
     * @return - Footer response
     */
    public String getFooterBodyElements() {

        try {
            CloseableHttpClient httpclient = createAcceptSelfSignedCertificateClient();
            HttpGet httpget = new HttpGet(footerBodyLink);
            log.info("Executing request " + httpget.getRequestLine());

            CloseableHttpResponse  response = httpclient.execute(httpget);

            footerBodyElements = EntityUtils.toString(response.getEntity(), "UTF-8");
        } catch (NoSuchAlgorithmException ae) {
            log.error("No Such Algorithm Exception", ae);
        } catch(KeyStoreException se) {
            log.error("Key Store Exception", se);
        } catch(KeyManagementException me) {
            log.error("Key Management Exception", me);
        } catch(IOException ie) {
            log.error("IO Exception", ie);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return footerBodyElements;
    }


    /**
     * This method will help build the connection to over https. This might skip the
     * ssl certificate and try to connect to end url.
     * @return - Httpclient
     * @throws KeyManagementException
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     */
    private static CloseableHttpClient createAcceptSelfSignedCertificateClient()
            throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {

        // use the TrustSelfSignedStrategy to allow Self Signed Certificates
        SSLContext sslContext = SSLContextBuilder
                .create()
                .loadTrustMaterial(new TrustSelfSignedStrategy())
                .build();

        // we can optionally disable hostname verification.
        // if you don't want to further weaken the security, you don't have to include this.
        HostnameVerifier allowAllHosts = new NoopHostnameVerifier();

        // create an SSL Socket Factory to use the SSLContext with the trust self signed certificate strategy
        // and allow all hosts verifier.
        SSLConnectionSocketFactory connectionFactory = new SSLConnectionSocketFactory(sslContext, allowAllHosts);

        // finally create the HttpClient using HttpClient factory methods and assign the ssl socket factory
        return HttpClients
                .custom()
                .setSSLSocketFactory(connectionFactory)
                .build();
    }


    @Activate
    protected void activate(final ComponentContext ctx) {
        log.info("Activating Instance of {}", this.getClass().getName());
        Dictionary<?, ?> props = ctx.getProperties();
        headerLink  = PropertiesUtil.toString(props.get("headerLink"), headerLink);
        headerBodyLink = PropertiesUtil.toString(props.get("headerBodyLink"), headerBodyLink);
        footerBodyLink = PropertiesUtil.toString(props.get("footerBodyLink"), footerBodyLink);
    }
}

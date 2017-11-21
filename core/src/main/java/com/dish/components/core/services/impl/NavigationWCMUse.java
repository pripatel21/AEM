package com.dish.components.core.services.impl;

import com.adobe.cq.sightly.WCMUse;
import com.day.cq.commons.jcr.JcrConstants;
import com.dish.components.core.services.GetHeaderFooterNav;
import com.dish.components.core.services.constants.DISHConstants;
import org.apache.commons.io.IOUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by narreddy on 9/7/17.
 */
@Component(immediate=true)
@Service

/**
 * This class will extend WCMUse pojo to get the required implicit objects to access the JCR. Also
 * this class will call the Header/Footer classes from the JCR to display it onto template.
 */
public class NavigationWCMUse extends WCMUse {

    private static final Logger log = LoggerFactory.getLogger(NavigationWCMUse.class);
    protected String headerNav;
    protected String headerBodyElements;
    protected String footerBodyElements;

    Session session = null;

    @Reference
    GetHeaderFooterNav getHeaderFooterNav;

    /**
     * This method will override the active method and will get the values from JCR node to show on templates
     */
    @Override
    public void activate() {
        try {
            session = getResourceResolver().adaptTo(Session.class);
            Node rootNode = session.getRootNode();
            Node varNode = rootNode.getNode(DISHConstants.VAR_NODE_PATH);

            /*GetHeaderFooterNavImpl getMenu = new GetHeaderFooterNavImpl();

            headerNav = getMenu.getHeaderNavigationMenu();
            headerBodyElements = getMenu.getHeaderBodyElements();
            footerBodyElements = getMenu.getFooterBodyElements();


            Resource headerDataResource = getResourceResolver().getResource(DISHConstants.HEADER_NAV_JCR_DATA_NODE);
            if (headerDataResource != null) {
                InputStream headerNavIS = headerDataResource.adaptTo(InputStream.class);
                headerNav = headerNavIS.toString();
            }

            Resource headerBodyDataResource = getResourceResolver().getResource(DISHConstants.HEADER_BODY_JCR_DATA_NODE);
            if (headerBodyDataResource != null) {
                InputStream headerBodyIS = headerBodyDataResource.adaptTo(InputStream.class);
                headerBodyElements = headerBodyIS.toString();
            }

            Resource footerBodyDataResource = getResourceResolver().getResource(DISHConstants.FOOTER_BODY_JCR_DATA_NODE);
            if (footerBodyDataResource != null) {
                InputStream footerBodyIS = footerBodyDataResource.adaptTo(InputStream.class);
                footerBodyElements = footerBodyIS.toString();
            }
            */

            if (varNode.hasNode(DISHConstants.DISH_FOLDER_NODE)) {
                Node navNode = varNode.getNode(DISHConstants.DISH_FOLDER_NODE);
                InputStream headNavIS = navNode.getNode(DISHConstants.HEADER_NAV_PROP).getNode(JcrConstants.JCR_CONTENT).
                                                                            getProperty(JcrConstants.JCR_DATA).getStream();
                headerNav = IOUtils.toString(headNavIS, DISHConstants.UTF_8_ENCODING);
                log.info("Header Nav ==> ");


                InputStream headerBodyIS = navNode.getNode(DISHConstants.HEADER_BODY_ELE_PROP).getNode(JcrConstants.JCR_CONTENT).
                                                                            getProperty(JcrConstants.JCR_DATA).getStream();
                headerBodyElements = IOUtils.toString(headerBodyIS, DISHConstants.UTF_8_ENCODING);

                InputStream footerBodyIS = navNode.getNode(DISHConstants.FOOTER_BODY_ELE_PROP).getNode(JcrConstants.JCR_CONTENT).
                                                                            getProperty(JcrConstants.JCR_DATA).getStream();
                footerBodyElements = IOUtils.toString(footerBodyIS, DISHConstants.UTF_8_ENCODING);
            }


        } catch (IOException io) {
            log.error("IO Exception", io);
        } catch (RepositoryException re) {
            log.error("Repository Exception", re);
        } catch (Exception e) {
            log.error("Exception " , e);
        }
    }

    /**
     * Returns the Header Navigation
     * @return
     */
    public String getHeaderNav() {
        return this.headerNav;
    }

    /**
     * Returns the Header Body Elements
     * @return
     */
    public String getHeaderBodyElements() { return this.headerBodyElements; }

    /**
     * Returns the Footer Body elements
     * @return
     */
    public String getFooterBodyElements() { return this.footerBodyElements; }
}
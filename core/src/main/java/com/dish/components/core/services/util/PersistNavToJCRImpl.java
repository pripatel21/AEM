package com.dish.components.core.services.util;

/**
 * Created by narreddy on 11/3/17.
 */

import com.day.cq.commons.jcr.JcrConstants;
import com.dish.components.core.services.PersistNavToJCR;
import com.dish.components.core.services.constants.DISHConstants;
import org.apache.commons.io.IOUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.jcr.api.SlingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * This class will persist the Header/Footer from dish.com to JCR.
 */
@Component(immediate = true, metatype = false)
@Service({PersistNavToJCR.class})
public class PersistNavToJCRImpl implements PersistNavToJCR {

    private static final Logger log = LoggerFactory.getLogger(PersistNavToJCRImpl.class);

    @Reference
    private SlingRepository repository;

    Session session = null;
    Node dishNode = null;

    /**
     * This Method will check for the Nodes. If the node exist then will update the Menu elements.
     * If Nodes doesn't exist, will create all the Nodes and set the Header Elements properties.
     * @param headerNav - Header Nav response from Job
     * @param headerBodyNav -- Header Body response from Job
     * @param footerBodyNav -- Footer Body response from Job
     */
    public void persistNav(String headerNav, String headerBodyNav, String footerBodyNav) {
        try {
            session = repository.loginService(null, null);
            Node rootNode = session.getRootNode();
            Node varNode = rootNode.getNode(DISHConstants.VAR_NODE_PATH);

            Map<String, String> navKeyValues = new HashMap<String, String>();
            navKeyValues.put(DISHConstants.HEADER_NAV_PROP, headerNav);
            navKeyValues.put(DISHConstants.HEADER_BODY_ELE_PROP, headerBodyNav);
            navKeyValues.put(DISHConstants.FOOTER_BODY_ELE_PROP, footerBodyNav);

            if (!varNode.hasNode(DISHConstants.DISH_FOLDER_NODE)) {
                dishNode = JcrUtils.getOrAddFolder(varNode, DISHConstants.DISH_FOLDER_NODE);
                log.info("== Creating Node Path == ");
                session.save();
                log.info("== Successfully Created Nodes == ");
                writeToFile(navKeyValues, session);
            } else if(varNode.hasNode(DISHConstants.DISH_FOLDER_NODE)){
                log.info(" == Node Already Exists == ");
                dishNode = varNode.getNode(DISHConstants.DISH_FOLDER_NODE);
                log.debug(" == Going to Write File Method == ");
                writeToFile(navKeyValues, session);
            }
        }catch (RepositoryException re) {
            log.error("Repository Exception" , re);
        } finally{
            if(session != null && session.isLive()) {
                try {
                    session.save();
                    session.logout();
                } catch (RepositoryException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * This method will write the response of Header/Footer to the File in JCR under /var/dish path.
     * @param elements Header/Footer elements
     * @param session JCR session to persist values
     */
    private void writeToFile(Map<String, String> elements,  Session session) {
        InputStream is;
        ValueFactory valueFactory;
        Binary contentValue;
        Node fileNode;
        Node resNode;
        Calendar lastModified;

        try {
            log.debug(" == Inside Write File == ");
            lastModified = Calendar.getInstance();
            lastModified.setTimeInMillis(lastModified.getTimeInMillis());

            for(Map.Entry<String, String> entry :  elements.entrySet()) {
                is = IOUtils.toInputStream(entry.getValue(), DISHConstants.UTF_8_ENCODING);
                valueFactory = session.getValueFactory();
                contentValue = valueFactory.createBinary(is);
                if(dishNode.hasNode(entry.getKey())) {
                    log.debug("Dish folder has '{}' Node ", dishNode.getNode(entry.getKey()));
                    fileNode = dishNode.getNode(entry.getKey());
                    if(fileNode.hasNode(JcrConstants.JCR_CONTENT)) {
                        log.debug("Header Node has '{}' Node ", fileNode.getNode(JcrConstants.JCR_CONTENT));
                        resNode = fileNode.getNode(JcrConstants.JCR_CONTENT);
                        resNode.setProperty(JcrConstants.JCR_DATA, contentValue);
                        log.debug("Setting the New Value to {}", resNode.getProperty(JcrConstants.JCR_DATA));
                        resNode.setProperty(JcrConstants.JCR_LASTMODIFIED, lastModified);
                    } else {
                        log.info("== jcr:content Node Exist ==");
                        fileNode = dishNode.addNode(entry.getKey(), JcrConstants.NT_FILE);
                        fileNode.addMixin(JcrConstants.MIX_REFERENCEABLE);
                        resNode = fileNode.addNode(JcrConstants.JCR_CONTENT, JcrConstants.NT_RESOURCE);
                        resNode.setProperty(JcrConstants.JCR_MIMETYPE, DISHConstants.MIME_TYPE);
                        resNode.setProperty(JcrConstants.JCR_DATA, contentValue);
                        resNode.setProperty(JcrConstants.JCR_LASTMODIFIED, lastModified);
                    }
                } else {
                    log.info("== No Node Exists and Writing Nodes to JCR==");
                    fileNode = dishNode.addNode(entry.getKey(), JcrConstants.NT_FILE);
                    fileNode.addMixin(JcrConstants.MIX_REFERENCEABLE);
                    resNode = fileNode.addNode(JcrConstants.JCR_CONTENT, JcrConstants.NT_RESOURCE);
                    resNode.setProperty(JcrConstants.JCR_MIMETYPE, DISHConstants.MIME_TYPE);
                    resNode.setProperty(JcrConstants.JCR_DATA, contentValue);
                    resNode.setProperty(JcrConstants.JCR_LASTMODIFIED, lastModified);
                }
                session.save();
                log.info(" == Session Saved in WriteToFile Method == ");
            }
        } catch(IOException ioe) {
            log.error("IO Exception", ioe);
        } catch (UnsupportedRepositoryOperationException ue) {
            log.error("Unsupported Repository Operation Exception", ue);
        } catch (NoSuchNodeTypeException ne) {
            log.error("No Such Node Type Exception", ne);
        } catch (LockException le) {
            log.error("Lock Exception", le);
        } catch (VersionException ve) {
            log.error("Version Exception", ve);
        } catch (ConstraintViolationException cve) {
            log.error("Constraint Violation Exception", cve);
        } catch (ItemExistsException iee) {
            log.error("Item Exists Exception", iee);
        } catch (PathNotFoundException pne) {
            log.error("Path Not Found Exception", pne);
        } catch (ValueFormatException vfe) {
            log.error("Value Format Exception", vfe);
        } catch (RepositoryException re) {
            log.error("Repository Exception", re);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try {
                session.save();
            } catch (RepositoryException re) {
                re.printStackTrace();
            }
        }
    }
}

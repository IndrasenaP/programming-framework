/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.smartsocietyproject.pf;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import eu.smartsocietyproject.pf.helper.JSONCollectiveIntermediary;
import java.io.IOException;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import static org.junit.Assert.*;
import org.springframework.http.MediaType;

/**
 * Work in progress. This class will hold the tests for the real PM when ready.
 *
 * @author Svetoslav Videnov <s.videnov@dsg.tuwien.ac.at>
 */
/*
public class PeerManagerProxyTest {

    private static WireMockServer pm;
    private PeerManagerProxy proxy;

    private static String readJsonFileHelper(String fileName) throws IOException {
        return IOUtils.toString(Thread
                .currentThread()
                .getContextClassLoader()
                .getResourceAsStream(fileName), "UTF-8");
    }

    @BeforeClass
    public static void setUpClass() throws IOException {
        pm = new WireMockServer(8080);
        pm.start();
        pm.stubFor(WireMock.get(WireMock
                .urlEqualTo(PeerManagerPaths.collectiveGet.replace("{collective_id}", "5")))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type",
                                MediaType.APPLICATION_JSON.toString())
                        .withBody(readJsonFileHelper("Collective.json"))));
    }

    @Before
    public void setUp() {
        proxy = new PeerManagerProxy("http", "127.0.0.1", 8080);
    }

    @After
    public void tearDown() {
    }

    //@Test
    public void testPersistCollective() {
    }

    //@Test
    public void testReadCollectiveById() {
        CollectiveIntermediary inter = this.proxy.readCollectiveById("5");
        assertEquals("5", inter.getId());
        for (Peer p : inter.getMembers()) {
            assertTrue(p.toString().contains("JhonnyD")
                    || p.toString().contains("Niceguy2")
                    || p.toString().contains("AlbertW"));
        }
    }

    //@Test
    public void testReadCollectiveByQuery() {
    }

}
*/
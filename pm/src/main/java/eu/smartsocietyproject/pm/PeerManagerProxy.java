package eu.smartsocietyproject.pm;

import eu.smartsocietyproject.peermanager.Peer;
import eu.smartsocietyproject.peermanager.PeerManager;
import eu.smartsocietyproject.peermanager.query.PeerQuery;
import eu.smartsocietyproject.peermanager.helper.PeerIntermediary;
import eu.smartsocietyproject.peermanager.helper.CollectiveIntermediary;
import eu.smartsocietyproject.peermanager.query.CollectiveQuery;
import eu.smartsocietyproject.pf.CollectiveBase;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

public class PeerManagerProxy implements PeerManager {

    private UriComponents collectiveById;

    public PeerManagerProxy() {
        this("http", "elog.disi.unitn.it", 8081);
    }

    public PeerManagerProxy(String protocol,
            String host,
            int port) {
        collectiveById = UriComponentsBuilder.newInstance()
                .scheme(protocol)
                .host(host)
                .port(port)
                .path(PeerManagerPaths.collectiveGet)
                .build();
    }

    @Override
    public void persistCollective(CollectiveBase collective) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public CollectiveIntermediary readCollectiveById(String id) {
        RequestEntity<Void> request = RequestEntity
                .get(this.collectiveById.expand(id).encode().toUri())
                .build();

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate
                .exchange(request, String.class);
        CollectiveIntermediary collective
                = new CollectiveIntermediary();
        collective.setId(id);
        try {
            JSONArray users = new JSONObject(response.getBody())
                    .getJSONArray("collectedUsers");
            for (int i = 0; i < users.length(); i++) {
                collective.addMember(new PeerIntermediary(users.getString(i)));
            }
        } catch (JSONException ex) {
            //todo-sv: handle
        }

        return collective;
    }

    @Override
    public CollectiveIntermediary readCollectiveByQuery(PeerQuery query) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<CollectiveIntermediary> readCollectiveByQuery(CollectiveQuery query) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

package eu.smartsocietyproject.pf;

import eu.smartsocietyproject.peermanager.PeerManager;
import eu.smartsocietyproject.peermanager.query.PeerQuery;
import eu.smartsocietyproject.pf.helper.JSONCollectiveIntermediary;
import eu.smartsocietyproject.peermanager.query.CollectiveQuery;

import java.util.List;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
/* TODO ADAPT TO NEW INTERFACE + FACTORY
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
    public void persistCollective(CollectiveIntermediary collective) {
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
        //todo: fix this with new API
        //CollectiveIntermediary collective = CollectiveIntermediary.createEmpty();
//                = new CollectiveIntermediary();
//        collective.setId(id);
//        try {
//            JSONArray users = new JSONObject(response.getBody())
//                    .getJSONArray("collectedUsers");
//            for (int i = 0; i < users.length(); i++) {
//                collective.addMember(new PeerIntermediary(users.getString(i)));
//            }
//        } catch (JSONException ex) {
//            //todo-sv: handle
//        }

        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public CollectiveIntermediary createCollectiveFromQuery(PeerQuery query) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<CollectiveIntermediary> findCollectives(CollectiveQuery query) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
*/
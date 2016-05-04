package eu.smartsocietyproject.pm;

import eu.smartsocietyproject.peermanager.PeerManager;
import eu.smartsocietyproject.peermanager.PeerQuery;
import eu.smartsocietyproject.peermanager.ResidentCollectiveIntermediary;
import eu.smartsocietyproject.pf.Collective;
import org.springframework.http.RequestEntity;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

public class PeerManagerProxy implements PeerManager {

	private UriComponents collectiveById;

	public PeerManagerProxy() {
		collectiveById = UriComponentsBuilder.newInstance()
				.scheme("http")
				.host("elog.disi.unitn.it")
				.port(8081)
				.path("/kos-smartsociety/smartsociety-peermanager/collectives/{collective_id}")
				.build();
	}

	@Override
	public void persistCollective(Collective collective) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public ResidentCollectiveIntermediary readCollectiveById(String id) {
		RequestEntity<Void> request = RequestEntity
				.get(this.collectiveById.expand(id).encode().toUri())
				.build();
		
		ResidentCollectiveIntermediary collective 
				= new ResidentCollectiveIntermediary();
		//todo-sv: fill here
		
		return collective;
	}

	@Override
	public ResidentCollectiveIntermediary readCollectiveByQuery(PeerQuery query) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
}
